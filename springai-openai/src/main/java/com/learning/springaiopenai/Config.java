/**
 * Author:   claire Date:    2025/2/10 - 15:15 Description: History:
 * <author>          <time>                   <version>          <desc>
 * claire          2025/2/10 - 15:15          V1.0.0
 */

package com.learning.springaiopenai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author claire
 * @date 2025/2/10 - 15:15
 * @since 1.0.0
 */
@Configuration
public class Config {
    
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("你是一个智能机器人,你的名字叫 Spring AI智能机器人").build();

    }

    @Bean
    InMemoryChatMemory inMemoryChatMemory() {
        return new InMemoryChatMemory();
    }
}
