package com.gaojiayi.nasaiassistantapp.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;

/**
 * 测试数据源配置
 * 覆盖生产环境的数据源配置，使用H2内存数据库
 */
@TestConfiguration
public class TestDataSourceConfig {

    /**
     * 测试用的主数据源
     * 使用H2内存数据库
     */
    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    /**
     * 测试用的PGVector数据源
     * 也使用H2内存数据库进行测试
     */
    @Bean(name = "pgVectorDataSource")
    public DataSource pgVectorDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:mem:testpgvector;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    /**
     * 初始化测试数据库表结构
     */
    @Bean
    public Boolean initializeTestDatabase() {
        try {
            // 等待数据源创建完成，然后初始化表结构
            Thread.sleep(100); // 简单等待，确保数据源已创建
            
            JdbcTemplate jdbcTemplate = new JdbcTemplate(@NonNull DataSource primaryDataSource());
            
            // 创建chat_message表
            jdbcTemplate.execute("DROP TABLE IF EXISTS chat_message");
            jdbcTemplate.execute("""
                CREATE TABLE chat_message (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    chat_id VARCHAR(255) NOT NULL,
                    message_type VARCHAR(50) NOT NULL,
                    message_text CLOB,
                    message_metadata CLOB,
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    is_delete INT DEFAULT 0
                )
                """);
            
            // 创建索引
            jdbcTemplate.execute("CREATE INDEX idx_chat_message_chat_id ON chat_message(chat_id)");
            jdbcTemplate.execute("CREATE INDEX idx_chat_message_create_time ON chat_message(create_time)");
            jdbcTemplate.execute("CREATE INDEX idx_chat_message_is_delete ON chat_message(is_delete)");
            
            System.out.println("H2测试数据库表结构初始化完成");
            return true;
            
        } catch (Exception e) {
            System.err.println("初始化H2测试数据库失败: " + e.getMessage());
            // 不抛出异常，允许测试继续进行
            return false;
        }
    }
}
