package com.zy.springai.rag.config;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfig {

    @Bean
    public VectorStore vectorStore(DashScopeEmbeddingModel dashScopeEmbeddingModel) {
        // 配置简单向量数据库
        return SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
    }
}
