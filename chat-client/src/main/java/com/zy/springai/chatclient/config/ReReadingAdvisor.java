package com.zy.springai.chatclient.config;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 重读advisor
 * 实现BaseAdvisor的before和after方法，可以在请求之前或响应之前拦截，做一些增强功能
 */
public class ReReadingAdvisor implements BaseAdvisor {

    private static final String DEFAULT_USER_TEXT_ADVISOR = """
            {re2_input_query}
            Read the question again: {re2_input_query}
            """;

    /**
     * 这里实现请求前的重读
     */
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 拿到用户的提示词
        String contents = chatClientRequest.prompt().getContents();

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template(DEFAULT_USER_TEXT_ADVISOR)
                .build();
        // 设置重读提示词
        String re2InputQuery = promptTemplate.render(Map.of("re2_input_query", contents));

        ChatClientRequest clientRequest = chatClientRequest.mutate()
                .prompt(Prompt.builder().content(re2InputQuery).build())
                .build();

        return clientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
