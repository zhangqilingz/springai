package com.zy.springai.qquickstart;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DashscopeTest {

    @Test
    public void dashscopeChat(@Autowired DashScopeChatModel dashScopeChatModel) {
        String call = dashScopeChatModel.call("你是谁？");
        System.out.println(call);
    }

    @Test
    public void text2Video() throws ApiException, NoApiKeyException, InputRequiredException {
        VideoSynthesis vs = new VideoSynthesis();
        VideoSynthesisParam param =
                VideoSynthesisParam.builder()
                        .model("wan2.2-t2v-plus")
                        .prompt("何永亮是一个程序员，不帅，正在写代码")
                        .size("1920*1080")
                        .apiKey("sk-1308c476504a4d7c9c08c4b51bd1937a")
                        .build();
        System.out.println("please wait...");
        VideoSynthesisResult result = vs.call(param);
        System.out.println(JsonUtils.toJson(result));
    }
}
