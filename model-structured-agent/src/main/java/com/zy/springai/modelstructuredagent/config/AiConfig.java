package com.zy.springai.modelstructuredagent.config;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeChatProperties;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.zy.springai.modelstructuredagent.tools.TicksTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AiConfig {

    @Value("classpath:/prompt/ticket-assistant.st")
    private Resource planningPrompt;

    @Bean
    public ChatClient planningChatClient(DashScopeChatModel chatModel,
                                         DashScopeChatProperties options,
                                         @Qualifier("jdbcChatMemory") ChatMemory chatMemory) {
        DashScopeChatOptions dashScopeChatOptions = DashScopeChatOptions.fromOptions(options.getOptions());
        dashScopeChatOptions.setTemperature(0.7);

        return ChatClient.builder(chatModel)
                .defaultSystem(planningPrompt)
                // 添加不同的顾问用于拦截
                .defaultAdvisors(
                        // 这是一个简单的日志拦截顾问
                        new SimpleLoggerAdvisor(),
                        // 这是一个记忆拦截顾问,实现大模型的”记忆“
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultOptions(dashScopeChatOptions)
                .build();
    }

    @Bean
    public ChatClient botChatClient(DashScopeChatModel chatModel,
                                    DashScopeChatProperties options,
                                    TicksTools ticksTools,
                                    @Qualifier("jdbcChatMemory") ChatMemory chatMemory) {

        DashScopeChatOptions dashScopeChatOptions = DashScopeChatOptions.fromOptions(options.getOptions());
        dashScopeChatOptions.setTemperature(1.2);
        return  ChatClient.builder(chatModel)
                // 这里底层会告诉大模型提供了哪些工具可以调用
                .defaultTools(ticksTools)
                .defaultSystem("""
                           你是XS航空智能客服代理， 请以友好的语气服务用户。
                            """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultOptions(dashScopeChatOptions)
                .build();
    }

}