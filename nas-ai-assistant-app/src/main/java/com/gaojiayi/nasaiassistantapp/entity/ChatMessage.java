package com.gaojiayi.nasaiassistantapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "`chat_message`", autoResultMap = true)
public class ChatMessage implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("conversation_id")
    private String conversationId;

    @TableField("message_type")
    private MessageType messageType;

    @TableField("content")
    private String content;

    @TableField(value = "metadata", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    @TableField(value = "`create_time`", fill = FieldFill.INSERT)
    private Date createTime;

    @Version
    @TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("`is_delete`")
    @TableLogic
    private boolean isDelete;

    // Manual getter and setter for conversationId
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    // Getters and setters for other fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public enum MessageType {
        USER,
        ASSISTANT,
        SYSTEM,
        TOOL
    }
}
