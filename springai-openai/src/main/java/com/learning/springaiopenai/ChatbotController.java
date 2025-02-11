/**
 * Author:   claire Date:    2025/2/10 - 15:15 Description: History:
 * <author>          <time>                   <version>          <desc>
 * claire          2025/2/10 - 15:15          V1.0.0
 */

package com.learning.springaiopenai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 *
 * @author claire
 * @date 2025/2/10 - 15:15
 * @since 1.0.0
 */
@RestController
@CrossOrigin("*")
@Slf4j
public class ChatbotController {

    private final ChatClient chatClient;
    private InMemoryChatMemory inMemoryChatMemory;

    public ChatbotController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest request) {
        //用户id
        String userId = request.userId();

        return chatClient.prompt(request.message())
                         .stream().content().map(content -> ServerSentEvent.builder(content).event("message").build())
                         //问题回答结速标识,以便前端消息展示处理
                         .concatWithValues(ServerSentEvent.builder("[DONE]").build())
                         .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).event("error").build()));
    }

    /**
     * curl -X POST -H "Content-Type: application/json" -d '{"userId":"testuserid", "message":"你好"}' http://localhost:8080/chat/stream
     * event:message
     * data:你好
     *
     * event:message
     * data:！
     *
     * event:message
     * data:我是
     *
     * event:message
     * data:Spring
     *
     * event:message
     * data: AI
     *
     * event:message
     * data:智能
     *
     * event:message
     * data:机器人
     *
     * event:message
     * data:，
     *
     * event:message
     * data:很高兴
     *
     * event:message
     * data:见到
     *
     * event:message
     * data:你
     *
     * event:message
     * data:。
     *
     * event:message
     * data:有什么
     *
     * event:message
     * data:我可以
     *
     * event:message
     * data:帮助
     *
     * event:message
     * data:你的
     *
     * event:message
     * data:吗
     *
     * event:message
     * data:？
     *
     * data:[DONE]
     *
     */
    @PostMapping(value = "/chat/stream/mem", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChatInMem(@RequestBody ChatRequest request) {
        //用户id
        String userId = request.userId();

        return chatClient.prompt(request.message())
            .advisors(new MessageChatMemoryAdvisor(inMemoryChatMemory,userId,20), new SimpleLoggerAdvisor())
                         .stream().content().map(content -> ServerSentEvent.builder(content).event("message").build())
                         //问题回答结速标识,以便前端消息展示处理
                         .concatWithValues(ServerSentEvent.builder("[DONE]").build())
                         .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).event("error").build()));
    }

    record ChatRequest(String userId, String message) {

    }
}
