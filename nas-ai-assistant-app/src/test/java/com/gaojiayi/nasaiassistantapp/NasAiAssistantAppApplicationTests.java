package com.gaojiayi.nasaiassistantapp;

import com.gaojiayi.nasaiassistantapp.config.TestDataSourceConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * NAS AI Assistant App 测试启动类
 * 用于启动 Spring Boot 测试环境
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestDataSourceConfig.class)
class NasAiAssistantAppApplicationTests {

    @Test
    void contextLoads() {
        // 测试 Spring 上下文是否能够正常加载
        // 如果这个测试通过，说明所有的 Bean 都能够正确注入和初始化
    }
}
