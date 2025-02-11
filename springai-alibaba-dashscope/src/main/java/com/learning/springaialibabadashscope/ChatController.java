/**
 * Author:   claire Date:    2025/2/11 - 11:44 Description: History:
 * <author>          <time>                   <version>          <desc>
 * claire          2025/2/11 - 11:44          V1.0.0
 */

package com.learning.springaialibabadashscope;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.*;

/**
 *
 * @author claire
 * @date 2025/2/11 - 11:44
 * @since 1.0.0
 */
@RestController
public class ChatController {
    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

    private final ChatClient dashScopeChatClient;

    private final ChatModel chatModel;

    public ChatController(ChatModel chatModel) {

        this.chatModel = chatModel;

        // 构造时，可以设置 ChatClient 的参数
        // {@link org.springframework.ai.chat.client.ChatClient};
        this.dashScopeChatClient = ChatClient.builder(chatModel)
                                             // 实现 Chat Memory 的 Advisor
                                             // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                                             .defaultAdvisors(
                                                 new MessageChatMemoryAdvisor(new InMemoryChatMemory())
                                             )
                                             // 实现 Logger 的 Advisor
                                             .defaultAdvisors(
                                                 new SimpleLoggerAdvisor()
                                             )
                                             // 设置 ChatClient 中 ChatModel 的 Options 参数
                                             .defaultOptions(
                                                 DashScopeChatOptions.builder()
                                                                     .withTopP(0.7)
                                                                     .build()
                                             )
                                             .build();
    }

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/chat/simple")
    public String simpleChat() {

        return dashScopeChatClient.prompt(DEFAULT_PROMPT).call().content();
    }

    /**
     * ChatClient 流式调用
     * curl -X GET -H "Content-Type: application/json" -d '{"userId":"testuserid", "message":"你好"}' http://localhost:8080/chat/stream
     * 你好！有什么我可以帮你的吗？
     */
    @GetMapping("/chat/stream")
    public Flux<String> streamChat(@RequestBody ChatRequest request, HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        return dashScopeChatClient.prompt(request.message()).stream().content();
    }

    /**
     * ChatClient 使用自定义的 Advisor 实现功能增强.
     *
     */
    @GetMapping("/advisor/chat/{id}/{prompt}")
    public Flux<String> advisorChat(
        HttpServletResponse response,
        @PathVariable String id,
        @PathVariable String prompt) {

        response.setCharacterEncoding("UTF-8");

        return this.dashScopeChatClient.prompt(prompt)
                                       .advisors(
                                           a -> a
                                               .param(CHAT_MEMORY_CONVERSATION_ID_KEY, id)
                                               .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                                       ).stream().content();
    }

    record ChatRequest(String userId, String message) {

    }
}
