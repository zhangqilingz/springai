package com.zy.springai.rag.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequestMapping(produces = "text/stream;charset=UTF-8")
@RestController
public class EmbeddingController {

    @Resource
    private ChatClient chatClient;

    @GetMapping("/chat1")
    Flux<String> stream1(@RequestParam String message) {
        Flux<String> content = chatClient.prompt().user(message).stream().content();
        return content;
    }
}
