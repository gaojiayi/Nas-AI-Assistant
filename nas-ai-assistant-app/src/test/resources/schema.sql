-- H2数据库测试用的表结构
-- 注意：这是为H2内存数据库优化的schema，不包含MySQL特定的语法

-- 创建chat_message表
DROP TABLE IF EXISTS chat_message;
CREATE TABLE chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_id VARCHAR(255) NOT NULL,
    message_type VARCHAR(50) NOT NULL,
    message_text CLOB,
    message_metadata CLOB,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_delete INT DEFAULT 0
);

-- 创建索引
CREATE INDEX idx_chat_message_chat_id ON chat_message(chat_id);
CREATE INDEX idx_chat_message_create_time ON chat_message(create_time);
CREATE INDEX idx_chat_message_is_delete ON chat_message(is_delete);
