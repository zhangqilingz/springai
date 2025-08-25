package com.zy.springai.qquickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeepseekTest {

    @Test
    public void deepseekChat(@Autowired DeepSeekChatModel deepSeekChatModel) {
        String call = deepSeekChatModel.call("你是谁");
        System.out.println(call);
    }

}
