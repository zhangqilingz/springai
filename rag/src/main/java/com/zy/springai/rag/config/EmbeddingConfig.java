package com.zy.springai.rag.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfig {

    @Bean("simpleVectorStore")
    public VectorStore vectorStore(DashScopeEmbeddingModel dashScopeEmbeddingModel) {
        // 配置简单向量数据库
        return SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
    }
    /**
     * 使用简单向量数据库的chatClient
     */
    @Bean
    public ChatClient chatClient(@Qualifier("simpleVectorStore") VectorStore vectorStore, DashScopeChatModel dashScopeChatModel) {
        return ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(
                        // 添加日志拦截
                        SimpleLoggerAdvisor.builder().build(),
                        // 添加向量数据库拦截
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(
                                        SearchRequest.builder()
                                                // 检索最相似的记录数最大为5
                                                .topK(5)
                                                // 检索相似分数阈值
//                                                .similarityThreshold(0.5)
                                                .build()
                                )
                                .build()
                )
                .defaultSystem("""
                        你是一个航空客服助手。基于提供的 Context information 回答。
                        """)
                .build();
    }

    @Bean("myMilvusClient")
    public VectorStore milvusVectorStore(MilvusServiceClient milvusServiceClient, DashScopeEmbeddingModel dashScopeEmbeddingModel) {
        return MilvusVectorStore.builder(milvusServiceClient, dashScopeEmbeddingModel)
                .collectionName("test")
                .databaseName("default")
                .indexType(IndexType.IVF_FLAT)
                .metricType(MetricType.COSINE)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .initializeSchema(true)
                .build();
    }


}
