package com.zy.springai.rag.config;

import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatMemoryConfig {

    @Value("${spring.ai.memory.redis.host}")
    private String redisHost;
    @Value("${spring.ai.memory.redis.port}")
    private int redisPort;
    @Value("${spring.ai.memory.redis.password}")
    private String redisPassword;
    @Value("${spring.ai.memory.redis.timeout}")
    private int redisTimeout;

    /**
     * 配置 chatMemory为JdbcChatMemoryRepository，以数据库的形式存储对话记忆（默认是InMemoryChatMemoryRepository）
     * @param chatMemoryRepository
     * @return
     */
    @Primary
    @Bean("jdbcChatMemory")
    ChatMemory jdbcChatMemory(JdbcChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory
                .builder()
                .maxMessages(10)
                .chatMemoryRepository(chatMemoryRepository).build();
    }

    /**
     * 配置chatMemory为RedisChatMemoryRepository，以redis的形式存储对话记忆
     * @return
     */
    @Bean("redisChatMemory")
    ChatMemory redisChatMemory() {
        RedisChatMemoryRepository redisChatMemoryRepository = RedisChatMemoryRepository.builder()
                .host(redisHost)
                .port(redisPort)
                // 若没有设置密码则注释该项
//           .password(redisPassword)
                .timeout(redisTimeout)
                .build();

        return MessageWindowChatMemory
                .builder()
                .maxMessages(10)
                .chatMemoryRepository(redisChatMemoryRepository).build();
    }
}
