package com.zy.springai.chatclient;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatClientTest {

    @Test
    public void chatClientTest(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient client = chatClientBuilder.build();
        String content = client
                .prompt("你是谁，当前北京时间是多少")
                .call()
                .content();
        System.out.println(content);
    }

    @Test
    public void chatClientBuilderTest(@Autowired DashScopeChatModel dashScopeChatModel) {
        ChatClient client = ChatClient.builder(dashScopeChatModel).build();
        String content = client
                .prompt("你是谁，当前北京时间是多少")
                .call()
                .content();
        System.out.println(content);
    }
}
