package com.zy.springai.modelstructuredagent.controller;

import com.zy.springai.modelstructuredagent.pojo.AiJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping(produces = "text/stream;charset=UTF-8")
@Slf4j
public class MultiModelsController {

    @Autowired
    private ChatClient planningChatClient;
    @Autowired
    private ChatClient botChatClient;

    @GetMapping("/sink/chat")
    Flux<String> stream(@RequestParam String message) {
        // 创建一个用于接收多条消息的 Sink
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        // 推送消息
        sink.tryEmitNext("正在执行任务...");
        new Thread(() -> {
            var job = planningChatClient.prompt().user(message)
                    .call().entity(AiJob.Job.class);

            switch (job.jobType()) {
                case CANCEL -> {
                    if(job.keyInfos().size()==0){
                        sink.tryEmitNext("请输入姓名和订单号.");
                    }
                    else {
                        sink.tryEmitNext("退票成功!");
                    }
                }
                case QUERY -> {
                    System.out.println(job);
                    // todo.. 执行业务
                    sink.tryEmitNext("查询预定信息：xxxx");
                }
                case OTHER -> {
                    // 不是业务代码，就调用客服机器人去响应
                    Flux<String> content = botChatClient.prompt().user(message).stream().content();
                    content.doOnNext(sink::tryEmitNext)
                            .doOnComplete(() -> sink.tryEmitComplete())
                            .subscribe();
                }
                default -> log.info("解析失败：{}", job);
            }
        }).start();
        return sink.asFlux();
    }
}
