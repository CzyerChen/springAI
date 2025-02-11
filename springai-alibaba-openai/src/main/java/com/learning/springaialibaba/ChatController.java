/**
 * Author:   claire Date:    2025/2/10 - 19:04 Description: History:
 * <author>          <time>                   <version>          <desc>
 * claire          2025/2/10 - 19:04          V1.0.0
 */

package com.learning.springaialibaba;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 *
 * @author claire
 * @date 2025/2/10 - 19:04
 * @since 1.0.0
 */
@RestController
public class ChatController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！";

    @Value("${spring.ai.openai.api-key}")
    private String key;
    private final ChatClient chatClient;

//    private final ChatModel chatModel;

    public ChatController(ChatModel chatModel) {

//        this.chatModel = chatModel;

        // 构造时，可以设置 ChatClient 的参数
        // {@link org.springframework.ai.chat.client.ChatClient};
        this.chatClient = ChatClient.builder(chatModel)
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
                                              OpenAiChatOptions.builder()
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

        return chatClient.prompt(DEFAULT_PROMPT).call().content();
    }

    /**
     * ChatClient 流式调用
     * curl -X GET -H "Content-Type: application/json" -d '{"userId":"testuserid", "message":"你好"}' http://localhost:8080/chat/stream
     * 你好！有什么我可以帮忙的吗
     */
    @GetMapping("/chat/stream")
    public Flux<String> streamChat(@RequestBody ChatRequest request, HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt(request.message()).stream().content();
    }

    record ChatRequest(String userId, String message) {

    }
}