/**
 * Author:   claire Date:    2025/2/11 - 16:39 Description: History:
 * <author>          <time>                   <version>          <desc>
 * claire          2025/2/11 - 16:39          V1.0.0
 */

package com.learning.springaiollama;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author claire
 * @date 2025/2/11 - 16:39
 * @since 1.0.0
 */
@RestController
public class ChatController {
    private static final String DEFAULT_PROMPT = "你好，介绍下你自己！请用中文回答。";

    private ChatClient chatClient;
    private final ChatModel chatModel;

    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
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
                                        OllamaOptions.builder()
                                                     .withTopP(0.7)
                                                     .build()
                                    )
                                    .build();
    }

    @PostMapping("/chat/simple")
    public String simpleChat(HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        return this.chatClient.prompt(DEFAULT_PROMPT)
                              .call().content();
    }

    /**
     * curl -X POST -H "Content-Type: application/json" -d '{"userId":"testuserid", "message":"你好"}'
     * http://localhost:8081/chat/stream
     * <think>
     *
     * </think>
     *
     * 你好！很高兴见到你，有什么我可以帮忙的吗？无论是学习、工作还是生活中的问题，都可以告诉我哦！😊
     */
    @PostMapping("/chat/model/stream")
    public Flux<String> streamModelChat(@RequestBody ChatRequest request, HttpServletResponse response) {
        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");

        Flux<ChatResponse> stream = chatModel.stream(new Prompt(request.message()));
        return stream.map(resp -> resp.getResult().getOutput().getContent());
    }

    /**
     * curl -X POST -H "Content-Type: application/json" -d '{"userId":"testuserid", "message":"你好"}' http://localhost:8081/chat/client/stream
<think>

</think>

你好！很高兴见到你，有什么我可以帮忙的吗
     */
    @PostMapping("/chat/client/stream")
    public Flux<String> streamClientChat(@RequestBody ChatRequest request, HttpServletResponse response) {
        // 避免返回乱码
        response.setCharacterEncoding("UTF-8");

        return chatClient.prompt(request.message()).stream().content();
    }

    record ChatRequest(String userId, String message) {

    }
}
