package com.zy.springai.rag.config;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatProperties;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(DashScopeChatModel chatModel,
                                    DashScopeChatProperties options,
                                    @Qualifier("jdbcChatMemory") ChatMemory chatMemory) {

        DashScopeChatOptions dashScopeChatOptions = DashScopeChatOptions.fromOptions(options.getOptions());
        return  ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultOptions(dashScopeChatOptions)
                .build();
    }

}