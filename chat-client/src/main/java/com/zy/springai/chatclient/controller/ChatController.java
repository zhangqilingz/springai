package com.zy.springai.chatclient.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.zy.springai.chatclient.config.ReReadingAdvisor;
import com.zy.springai.chatclient.pojo.ModelOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "chat", produces = "text/stream;charset=UTF-8")
public class ChatController {

    @Resource(name = "redisChatMemory")
    private ChatMemory chatMemory;

    Map<String, ChatModel> platforms = new HashMap<>();

    public ChatController(DashScopeChatModel dashScopeChatModel, DeepSeekChatModel deepSeekChatModel) {
        platforms.put("dashscope", dashScopeChatModel);
        platforms.put("deepseek", deepSeekChatModel);
    }

    @RequestMapping(value = "/chat1")
    public Flux<String> chat(String message, ModelOptions options) {
        ChatModel chatModel = platforms.get(options.getPlatform());

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultOptions(ChatOptions.builder()
                        .model(options.getModel())
                        .temperature(options.getTemperature())
                        .build())
                // 添加不同的顾问用于拦截
                .defaultAdvisors(
                        // 这是一个简单的日志拦截顾问
                        new SimpleLoggerAdvisor(),
                        // 这是一个敏感词顾问
                        new SafeGuardAdvisor(List.of("张三"), "这是敏感词，不能说", 1),
                        // 这是一个自定义的 “问题重读顾问”
                        new ReReadingAdvisor(),
                        // 这是一个记忆拦截顾问,实现大模型的”记忆“
                        PromptChatMemoryAdvisor.builder(chatMemory).build()
                        )
                // 设置对话id的key，用于隔离记忆
                .defaultAdvisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, options.getUserId()))
                .build();

        return chatClient.prompt().user(message).stream().content();
    }

    /**
     * 基于jdbc的对话存储
     * @param message
     * @param options
     * @return
     */
    @RequestMapping(value = "/chat2")
    public Flux<String> chat1(String message, ModelOptions options) {
        ChatModel chatModel = platforms.get(options.getPlatform());

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultOptions(ChatOptions.builder()
                        .model(options.getModel())
                        .temperature(options.getTemperature())
                        .build())
                // 添加不同的顾问用于拦截
                .defaultAdvisors(
                        // 这是一个简单的日志拦截顾问
                        new SimpleLoggerAdvisor(),
                        // 这是一个敏感词顾问
                        new SafeGuardAdvisor(List.of("张三"), "这是敏感词，不能说", 1),
                        // 这是一个自定义的 “问题重读顾问”
                        new ReReadingAdvisor(),
                        // 这是一个记忆拦截顾问,实现大模型的”记忆“
                        PromptChatMemoryAdvisor.builder(chatMemory).build()
                )
                // 设置对话id的key，用于隔离记忆
                .defaultAdvisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, options.getUserId()))
                .build();

        return chatClient.prompt().user(message).stream().content();
    }

}
