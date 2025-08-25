package com.zy.springai.chatclient.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.zy.springai.chatclient.config.ReReadingAdvisor;
import com.zy.springai.chatclient.pojo.ActorsFilms;
import com.zy.springai.chatclient.pojo.Address;
import com.zy.springai.chatclient.pojo.ModelOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "chat", produces = "text/stream;charset=UTF-8")
@Slf4j
public class ChatController {

    /**
     * 注入不同的chatMemory以实现不同的记忆存储
     */
    @Resource(name = "redisChatMemory")
    private ChatMemory chatMemory;

    /**
     * 读取提示词模板文件，以设置系统提示词
     */
//    @Value("classpath:/prompt/system1.st")
    private org.springframework.core.io.Resource systemResource;

    Map<String, ChatModel> platforms = new HashMap<>();

    public ChatController(DashScopeChatModel dashScopeChatModel, DeepSeekChatModel deepSeekChatModel) {
        platforms.put("dashscope", dashScopeChatModel);
        platforms.put("deepseek", deepSeekChatModel);
    }

    /**
     * 基本的对话实现
     */
    @RequestMapping(value = "/chat1")
    public Flux<String> chat(String message, ModelOptions options,
                             @Value("classpath:/prompt/system1.st") org.springframework.core.io.Resource systemResource) {
        ChatModel chatModel = platforms.get(options.getPlatform());
        ChatClient.Builder chatClientBuilder = buildChatClient(options, chatModel);

        ChatClient chatClient = chatClientBuilder
                // 设置系统提示词
                .defaultSystem(systemResource)
                .build();

        return chatClient.prompt().user(message).stream().content();
    }

    /**
     * 结构化输出，响应为boolean案例
     */
    @RequestMapping(value = "/chat2")
    public void chatStructureOut1(String message, ModelOptions options,
                             @Value("classpath:/prompt/is-complain.st") org.springframework.core.io.Resource systemResource) {
        ChatModel chatModel = platforms.get(options.getPlatform());

        ChatClient chatClient = buildChatClient(options, chatModel).build();

        Boolean isComplain = chatClient.prompt()
                // 设置系统提示词，判断当前用户是否表现了投诉意图，结构化输出boolean值
                .system(systemResource)
                .user(message)
                .call()
                .entity(Boolean.class);

        // 根据投诉意图进行不同的业务逻辑
        if (Boolean.TRUE.equals(isComplain)) {
            log.info("用户是投诉，转接人工客服！");
        } else {
            log.info("用户不是投诉，自动流转客服机器人。");
            // todo 继续调用 客服ChatClient进行对话
        }
    }

    /**
     * 结构化输出，响应为实体案例
     */
    @RequestMapping(value = "/chat3")
    public void chatStructureOut2(String message, ModelOptions options,
                      @Value("classpath:/prompt/address-prompt.st") org.springframework.core.io.Resource systemResource) {
        ChatModel chatModel = platforms.get(options.getPlatform());

        ChatClient chatClient = buildChatClient(options, chatModel).build();

        Address address = chatClient.prompt()
                // 设置系统提示词，将用户提示词结构化输出为Address实体
                .system(systemResource)
                .user(message)
                .call()
                .entity(Address.class);
        System.out.println(address);
    }

    /**
     * 结构化输出原理
     */
    @RequestMapping(value = "/chat4")
    public String chatStructureOut3(String message, ModelOptions options,
                      @Value("classpath:/prompt/system1.st") org.springframework.core.io.Resource systemResource)  throws IOException {
        ChatModel chatModel = platforms.get(options.getPlatform());
        ChatClient chatClient = buildChatClient(options, chatModel).build();

        BeanOutputConverter<ActorsFilms> beanOutputConverter = new BeanOutputConverter<>(ActorsFilms.class);
        // 根据ActorsFilms类生成格式模板提示词
        String format = beanOutputConverter.getFormat();
        // 读取提示词模板
        String templateText  = StreamUtils.copyToString(systemResource.getInputStream(), StandardCharsets.UTF_8);
        // 构建完整的提示词
        Prompt prompt = PromptTemplate.builder().template(templateText)
                .variables(Map.of("actor", message, "format", format)).build().create();

        ChatResponse chatResponse = chatClient.prompt()
                .user(prompt.getContents())
                .call().chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

    private @NotNull ChatClient.Builder buildChatClient(ModelOptions options, ChatModel chatModel) {
        return ChatClient.builder(chatModel)
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
                .defaultAdvisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, options.getUserId()));
    }

}
