package com.zy.springai.rag.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class VectorStoreInitializer {

    @Resource
    private VectorStore vectorStore;

    /**
     * 初始化简单向量数据库的数据
     */
    @PostConstruct
    public void init() {
        // 1. 声明内容文档
        Document doc = Document.builder()
                .text("""
                预订航班:
                - 通过我们的网站或移动应用程序预订。
                - 预订时需要全额付款。
                - 确保个人信息（姓名、ID 等）的准确性，因为更正可能会产生 25 的费用。
                """)
                .build();
        Document doc2 = Document.builder()
                .text("""
                取消预订:
                - 最晚在航班起飞前 48 小时取消。
                - 取消费用：经济舱 75 美元，豪华经济舱 50 美元，商务舱 25 美元。
                - 退款将在 7 个工作日内处理。
                """)
                .build();

        // 2. 将文本进行向量化，并且存入向量数据库（无需再手动向量化）
        vectorStore.add(Arrays.asList(doc,doc2));
    }
}
