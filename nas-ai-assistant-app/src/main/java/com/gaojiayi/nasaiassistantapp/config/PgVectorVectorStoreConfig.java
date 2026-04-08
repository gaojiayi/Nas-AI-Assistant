package com.gaojiayi.nasaiassistantapp.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gaojiayi.nasaiassistantapp.rag.NasDocumentLoader;

import jakarta.annotation.Resource;

@Configuration
public class PgVectorVectorStoreConfig {

    private static final Logger log = LoggerFactory.getLogger(PgVectorVectorStoreConfig.class);
    @Resource
    private NasDocumentLoader nasDocumentLoader;

    @Bean
    public ApplicationRunner vectorStoreInitializer(
            VectorStore vectorStore,
            @Qualifier("vectorJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return args -> {
            log.info("PgVector 向量存储初始化开始");

            // 检查表是否存在且有数据
            if (hasDocuments(jdbcTemplate)) {
                log.info("向量数据库已有数据，跳过加载");
                return;
            }

            log.info("向量数据库为空，开始加载文档...");
            List<Document> documents = nasDocumentLoader.loadMarkdowns();

            // 分批加载（DashScope API 限制单次 batch size 不超过 10）
            int batchSize = 10;
            for (int i = 0; i < documents.size(); i += batchSize) {
                int end = Math.min(i + batchSize, documents.size());
                vectorStore.add(documents.subList(i, end));
            }

            log.info("文档加载完成，共 {} 条", documents.size());
        };
    }

    /**
     * 检查向量库表是否存在且有数据
     */
    private boolean hasDocuments(JdbcTemplate jdbcTemplate) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM public.vector_store",
                    Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            // 表不存在或查询失败，返回 false
            log.info("向量表不存在或为空: {}", e.getMessage());
            return false;
        }
    }

}
