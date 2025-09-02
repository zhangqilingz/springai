package com.zy.springai.rag;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class EmbeddingTest {

    /**
     * DashScopeEmbeddingModel, 向量模型的实现
     */
    @Test
    void testEmbedding(@Autowired DashScopeEmbeddingModel embeddingModel) {
        float[] embedded = embeddingModel.embed("我是zy");
        System.out.println(embedded.length);
        System.out.println(Arrays.toString(embedded));
    }

    @BeforeEach
    public void init(@Autowired VectorStore vectorStore) {
        Document document1 = Document.builder()
                .text("""
                        预订航班:
                        - 通过我们的网站或移动应用程序预订。
                        - 预订时需要全额付款。
                        - 确保个人信息（姓名、ID 等）的准确性，因为更正可能会产生 25 的费用。
                        """)
                .build();

        Document document2 = Document.builder()
                .text("""
                        取消预订:
                        - 最晚在航班起飞前 48 小时取消。
                        - 取消费用：经济舱 75 美元，豪华经济舱 50 美元，商务舱 25 美元。
                        - 退款将在 7 个工作日内处理。
                        """)
                .build();

        // 将文本向量化，并且存入向量数据库
        vectorStore.add(Arrays.asList(document1, document2));
    }

    @Test
    void similaritySearchTest(@Autowired VectorStore vectorStore) {
        // 相似性查询
        SearchRequest searchRequest = SearchRequest.builder()
                .query("航班预定")
                // 最多返回的相似结果数量
                .topK(2)
                // 设置相似分数下限
//                .similarityThreshold(0.51)
                .build();
        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        documents.forEach(document -> {
            System.out.println(document.getText());
            System.out.println(document.getScore());
        });
    }

}
