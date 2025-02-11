<template>
  <div class="chat-window">
    <!-- 添加标题 -->
    <div class="title">
      <el-icon><HomeFilled /></el-icon>SpringAI 机器人对话
    </div>

    <!-- 消息显示区域 -->
    <div class="messages" ref="messageContainer" @scroll="handleScroll">
      <div v-for="(message, index) in messages" :key="index" :class="['message', message.isBot ? 'bot' : 'user']">
         <!-- 用户头像和消息 -->
         <div v-if="!message.isBot" class="message-container user-message">
          <el-avatar :icon="UserFilled"></el-avatar>
          <div class="message-info">
            <div class="sender">我</div>
            <div class="timestamp">{{ formatTimestamp(message.timestamp) }}</div>
          </div>
          <div class="message-content" v-html="highlightContent(message.content)"></div>
        </div>

         <!-- AI头像和消息 -->
         <div v-else class="message-container bot-message">
          <el-avatar :icon="Menu"></el-avatar>
          <div class="message-info">
            <div class="sender">Spring AI</div>
            <div class="timestamp">{{ formatTimestamp(message.timestamp) }}</div>
          </div>
          <div class="message-content" v-html="highlightContent(message.content)"></div>
        </div>

        <!-- <div class="message-content" v-html="highlightContent(message.content)"></div> -->
        <div v-if="message.status === 'streaming'" class="loading-indicator">正在火速思考中...</div>
        <div v-else-if="message.status === 'error'" class="error-message">
          {{ message.content }}
          <button @click="message.retry?.()">Retry</button>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <input
        v-model="inputMessage"
        @keyup.enter="sendMessage"
        placeholder="Type your message here..."
        ref="inputRef"
      />
      <button @click="sendMessage">Send</button>
      <button v-if="isLoading" @click="stopGeneration">Stop</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, reactive, onMounted, onBeforeUnmount, nextTick} from 'vue'
import {fetchEventSource} from '@microsoft/fetch-event-source'
import { HomeFilled, UserFilled, Menu } from '@element-plus/icons-vue'; // 导入所需图标

// 生成随机用户ID（示例：8位字母数字组合）
const generateUserId = () => {
  return Math.random().toString(36).substr(2, 8);
};

// 持久化存储用户ID
const userId = ref('');

enum MessageStatus {
  Streaming = 'streaming',
  Complete = 'complete',
  Error = 'error',
}

interface Message {
  id: string
  content: string
  isBot: boolean
  status?: MessageStatus
  timestamp: number
  retry?: () => Promise<void>
}

const messages = ref<Message[]>([])
const inputMessage = ref('')
const isLoading = ref(false)
const controller = ref<AbortController>()
const messageContainer = ref<HTMLElement>()
const inputRef = ref<HTMLInputElement>()

// 格式化时间戳
const formatTimestamp = (timestamp: number) => {
  const date = new Date(timestamp);
  // return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
};

// 自动滚动控制
let autoScroll = true
let lastCharType: 'chinese' | 'english' | 'other' = 'other'

const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value && autoScroll) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
  })
}

const handleScroll = () => {
  if (!messageContainer.value) return
  const {scrollTop, scrollHeight, clientHeight} = messageContainer.value
  autoScroll = scrollHeight - (scrollTop + clientHeight) < 50
}

// 字符类型检测
const getCharType = (char: string): 'chinese' | 'english' | 'other' => {
  if (/[\u4e00-\u9fa5\u3000-\u303F\uFF00-\uFFEF]/.test(char)) {
    return 'chinese'
  }
  if (/[a-zA-Z]/.test(char)) {
    return 'english'
  }
  return 'other'
}

// 智能空格处理核心逻辑
const processContent = (prev: string, newData: string): string => {
  if (prev.length === 0) return newData

  const lastChar = prev.slice(-1)
  const newFirstChar = newData[0] || ''

  const prevType = getCharType(lastChar)
  const newType = getCharType(newFirstChar)

  let processed = newData

  // 需要添加空格的情况
  const shouldAddSpace =
      (prevType === 'english' && newType === 'english') || // 英文接英文
      (prevType === 'chinese' && newType === 'english') || // 中文接英文
      (prevType === 'english' && newType === 'chinese' && !/[!?,.]$/.test(lastChar)) // 英文接中文（非标点结尾）

  // 需要删除空格的情况
  const shouldRemoveSpace =
      (prevType === 'chinese' && newType === 'chinese') || // 中文接中文
      (prevType === 'other' && /^[\u4e00-\u9fa5]/.test(newData)) // 特殊符号接中文

  if (shouldAddSpace && !lastChar.match(/\s/) && !newFirstChar.match(/\s/)) {
    processed = ' ' + processed
  } else if (shouldRemoveSpace) {
    processed = processed.replace(/^\s+/, '')
  }

  return processed
}

const sendChatRequest = async (content: string, botMessage: Message) => {
  controller.value = new AbortController()

  await fetchEventSource('http://localhost:8080/chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'text/event-stream',
      'X-Content-Lang': 'zh-CN'
    },
    body: JSON.stringify({message: content,userId:userId.value}),
    signal: controller.value?.signal,
    openWhenHidden: true,

    onopen: async response => {
      if (!response.ok) throw new Error(`HTTP error ${response.status}`)
    },

    onmessage: event => {
      if (event.data === '[DONE]') {
        botMessage.status = MessageStatus.Complete
        return
      }

      const processedData = processContent(botMessage.content, event.data)
      botMessage.content += processedData
      botMessage.timestamp = Date.now()

      // 更新最后字符类型
      const lastChar = processedData.slice(-1)
      lastCharType = getCharType(lastChar)

      scrollToBottom()
    },

    onerror: err => {
      throw err
    }
  })
}

// 错误处理
const handleRequestError = (botMessage: Message, error: unknown) => {
  const errorMessage = error instanceof Error
      ? navigator.onLine
          ? error.message
          : '网络连接不可用'
      : '请求发生未知错误'

  botMessage.status = MessageStatus.Error
  botMessage.content = errorMessage
  botMessage.retry = createRetryHandler(botMessage.content)
}

// 主发送逻辑
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  const userContent = inputMessage.value.trim()
  inputMessage.value = ''

  // 创建用户消息
  const userMessage = reactive<Message>({
    id: `user-${Date.now()}`,
    content: userContent,
    isBot: false,
    timestamp: Date.now()
  })
  messages.value.push(userMessage)

  // 创建机器人消息
  const botMessage = reactive<Message>({
    id: `bot-${Date.now()}`,
    content: '',
    isBot: true,
    status: MessageStatus.Streaming,
    timestamp: Date.now()
  })
  messages.value.push(botMessage)

  isLoading.value = true

  try {
    await sendChatRequest(userContent, botMessage)
  } catch (err) {
    handleRequestError(botMessage, err)
  } finally {
    isLoading.value = false
    nextTick(() => inputRef.value?.focus())
  }
}

// 停止生成
const stopGeneration = () => {
  controller.value?.abort()
  isLoading.value = false
}

// 创建重试处理器
const createRetryHandler = (retryContent: string) => async () => {
  const retryMessage = reactive<Message>({
    id: `bot-${Date.now()}`,
    content: retryContent,
    isBot: true,
    status: MessageStatus.Streaming,
    timestamp: Date.now()
  })
  messages.value.push(retryMessage)
  try {
    await sendChatRequest(retryContent, retryMessage)
  } catch (err) {
    handleRequestError(retryMessage, err)
  }
}

// 生命周期
onMounted(() => {
  userId.value = localStorage.getItem('chatUserId') || generateUserId();
  localStorage.setItem('chatUserId', userId.value);
  messageContainer.value?.addEventListener('scroll', handleScroll)
  inputRef.value?.focus()
})

onBeforeUnmount(() => {
  messageContainer.value?.removeEventListener('scroll', handleScroll)
  controller.value?.abort()
})

// 高亮内容中的特殊标记
const highlightContent = (content: string) => {
  return content.replace(/($DONE$)/g, '<span class="done">$1</span>')
}
</script>

<style scoped>
.chat-window {
  display: flex;
  flex-direction: column;
  height: 80vh; /* 使用视口高度 */
  width: 60vw; /* 使用视口宽度 */
  max-width: none; /* 移除最大宽度限制 */
  margin: 0; /* 移除外边距 */
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 10px;
  background-color: #f9f9f9;
  box-sizing: border-box; /* 确保内边距和边框不会增加总尺寸 */
}

.title {
  background-color: #409EFF; /* 蓝色背景 */
  color: white; /* 白色文字 */
  padding: 10px;
  text-align: center;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px; /* 给标题和内容之间留一些空间 */
}

.messages {
  flex-grow: 1;
  overflow-y: auto;
  padding: 10px;
  border-bottom: 1px solid #ccc;
}

.message-container {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
}

.user-message {
  flex-direction: row-reverse;
  justify-content: flex-start;  
}

.bot-message {
  flex-direction: row;
  justify-content: flex-start;  
}

.el-avatar {
  margin-right: 10px;
}

.user-message .el-avatar {
  margin-left: 10px; /* 头像在最右侧 */
  margin-right: 0;
}

.message-info {
  display: flex;
  flex-direction: column;
  margin-right: 10px;
}

.sender {
  font-weight: bold;
}

.timestamp {
  font-size: 12px;
  color: #888;
}

.message-content {
  word-break: break-word;
  max-width: 70%;
  padding: 10px;
  border-radius: 8px;
}

.user-message .message-content {
  background-color: #409EFF; /* 用户消息背景颜色 */
  color: #ffffff;
  text-align: right;
}

.user-message .el-avatar {
  margin-left: 10px;
  margin-right: 0;
}

.bot-message .message-content {
  background-color: #ffffff; /* AI消息背景颜色 */
  color: #888;
  text-align: left;
}

.loading-indicator {
  font-style: italic;
  color: gray;
}

.error-message {
  color: red;
}

.input-area {
  display: flex;
  padding: 10px;
}

.input-area input {
  flex-grow: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.input-area button {
  margin-left: 10px;
  padding: 10px 10px;
  border: none;
  background-color: #409EFF;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

.input-area button:hover {
  background-color: #007bff;
}

/* 字体优化 */
body {
  font-family: 'Arial', sans-serif;
  font-size: 16px;
  line-height: 1.6;
}

.message-content {
  font-size: 16px;
  color: #333;
}
</style>