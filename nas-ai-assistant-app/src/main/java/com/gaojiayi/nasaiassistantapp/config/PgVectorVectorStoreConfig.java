package com.gaojiayi.nasaiassistantapp.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.gaojiayi.nasaiassistantapp.rag.NasDocumentLoader;

import jakarta.annotation.Resource;

@Configuration
public class PgVectorVectorStoreConfig {

    private static final Logger log = LoggerFactory.getLogger(PgVectorVectorStoreConfig.class);
    @Resource
    private NasDocumentLoader nasDocumentLoader;

    @Value("${pgvector.ai.vectorstore.pgvector.initialize-schema:true}")
    private boolean initializeSchema;

    @Value("${pgvector.ai.vectorstore.pgvector.index-type:HNSW}")
    private String indexType;

    @Value("${pgvector.ai.vectorstore.pgvector.dimensions:1024}")
    private int dimensions;

    @Value("${pgvector.ai.vectorstore.pgvector.distance-type:COSINE_DISTANCE}")
    private String distanceType;

    @Value("${pgvector.ai.vectorstore.pgvector.max-document-batch-size:10000}")
    private int maxDocumentBatchSize;

    @Value("${pgvector.ai.vectorstore.pgvector.vector-table-name:vector_store}")
    private String vectorTableName;

    @Value("${pgvector.ai.vectorstore.pgvector.schema-name:public}")
    private String schemaName;

    @Bean("pgVectorVectorStore")
    public VectorStore pgVectorVectorStore(
            @Qualifier("vectorJdbcTemplate") JdbcTemplate jdbcTemplate,
            DashScopeEmbeddingModel dashscopeEmbeddingModel) {

        log.info("PgVector 向量存储开始配置");

        VectorStore pgVectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(dimensions)
                .distanceType(getDistanceType())
                .indexType(getIndexType())
                .initializeSchema(initializeSchema)
                .schemaName(schemaName)
                .vectorTableName(vectorTableName)
                .maxDocumentBatchSize(maxDocumentBatchSize)
                .build();

        // 等待 PgVectorStore 初始化完成后再检查数据
        try {
            Thread.sleep(1000); // 给 PgVectorStore 一些时间来完成初始化
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 检查表是否存在且有数据
        if (hasDocuments(jdbcTemplate)) {
            log.info("向量数据库已有数据，跳过加载");
            return pgVectorStore;
        }

        log.info("向量数据库为空，开始加载文档...");
        List<Document> documents = nasDocumentLoader.loadMarkdowns();

        // 分批加载（DashScope API 限制单次 batch size 不超过 10）
        int batchSize = 10;
        for (int i = 0; i < documents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, documents.size());
            pgVectorStore.add(documents.subList(i, end));
        }

        log.info("文档加载完成，共 {} 条", documents.size());
        return pgVectorStore;
    }

    /**
     * 将字符串配置转换为距离类型枚举
     */
    private PgVectorStore.PgDistanceType getDistanceType() {
        switch (distanceType.toUpperCase()) {
            case "COSINE_DISTANCE":
                return PgVectorStore.PgDistanceType.COSINE_DISTANCE;
            case "EUCLIDEAN_DISTANCE":
                return PgVectorStore.PgDistanceType.EUCLIDEAN_DISTANCE;
            case "INNER_PRODUCT":
                return PgVectorStore.PgDistanceType.NEGATIVE_INNER_PRODUCT;
            default:
                log.warn("未知的距离类型: {}, 使用默认值 COSINE_DISTANCE", distanceType);
                return PgVectorStore.PgDistanceType.COSINE_DISTANCE;
        }
    }

    /**
     * 将字符串配置转换为索引类型枚举
     */
    private PgVectorStore.PgIndexType getIndexType() {
        switch (indexType.toUpperCase()) {
            case "HNSW":
                return PgVectorStore.PgIndexType.HNSW;
            case "IVFFLAT":
                return PgVectorStore.PgIndexType.IVFFLAT;
            case "NONE":
                return PgVectorStore.PgIndexType.NONE;
            default:
                log.warn("未知的索引类型: {}, 使用默认值 HNSW", indexType);
                return PgVectorStore.PgIndexType.HNSW;
        }
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
