package com.gaojiayi.nasaiassistantapp.config;

import jakarta.annotation.Resource;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider;

import com.gaojiayi.nasaiassistantapp.tools.DocumentReaderTool;
import com.gaojiayi.nasaiassistantapp.tools.FileOperationTool;
import com.gaojiayi.nasaiassistantapp.tools.MailSendTool;
import com.gaojiayi.nasaiassistantapp.tools.NasImageSearchTool;
import com.gaojiayi.nasaiassistantapp.tools.PDFGenerationTool;
import com.gaojiayi.nasaiassistantapp.tools.TavilyWebSearchTool;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Nas助手 工具注册配置类
 * 
 * 该类负责注册和配置Nas助手所需的所有工具，包括本地工具和MCP工具。
 * 通过Spring的@Configuration注解标识为配置类，在应用启动时自动加载。
 *
 * 包含工具：
 * - 网络搜索工具（TavilyWebSearchTool）：用于获取网络信息
 * - 邮件发送工具（MailSendTool）：用于发送邮件通知
 * - 文档读取工具（DocumentReaderTool）：用于读取各种文档格式
 * - PDF生成工具（PDFGenerationTool）：用于生成PDF文档
 * - 文件操作工具（FileOperationTool）：用于文件读写操作
 * - 图片搜索工具（NasImageSearchTool）：用于图片搜索功能
 * - MCP工具：通过AsyncMcpToolCallbackProvider集成的外部工具
 */
@Configuration
public class NasAIToolRegistration {


    /**
     * MCP工具回调提供者
     * 用于获取通过MCP协议集成的外部工具，如高德地图、图片搜索等
     */
    @Resource
    private AsyncMcpToolCallbackProvider toolCallbackProvider;
    
    /**
     * 网络搜索工具
     * 基于Tavily API实现，用于搜索网络信息获取最新知识
     */
    @Autowired
    private TavilyWebSearchTool tavilyWebSearchTool;
    
    /**
     * 邮件发送工具
     * 用于发送邮件通知和报告
     */
    @Autowired
    private MailSendTool mailSendTool;
    
    /**
     * 文档读取工具
     * 支持读取多种格式的文档内容
     */
    @Autowired
    private DocumentReaderTool documentReaderTool;
    
    /**
     * 文件操作工具
     * 提供文件读写、删除等基本操作功能
     */
    @Autowired
    private FileOperationTool fileOperationTool;
    
    /**
     * PDF生成工具
     * 用于将内容转换为PDF格式文档
     */
    @Autowired
    private PDFGenerationTool pdfGenerationTool;
    
    /**
     * 图片搜索工具
     * 用于搜索和管理图片资源
     */
    @Autowired
    private NasImageSearchTool nasImageSearchTool;

    /**
     * 注册Nas助手的所有工具
     * 
     * 该方法将本地工具和MCP工具合并为一个工具数组，
     * 供Spring AI框架使用。本地工具包括文件操作、网络搜索等，
     * MCP工具通过AsyncMcpToolCallbackProvider动态获取。
     * 
     * @return 包含所有可用工具的ToolCallback数组
     */
    @Bean
    public ToolCallback[] NasAssistantTools() {
        // 本地工具：应用内部实现的核心功能工具
        ToolCallback[] localTools = ToolCallbacks.from(
                tavilyWebSearchTool,
                mailSendTool,
                documentReaderTool,
                pdfGenerationTool,
                fileOperationTool,
                nasImageSearchTool);

        // MCP工具：通过MCP协议集成的外部工具
        ToolCallback[] mcpTools = toolCallbackProvider.getToolCallbacks();

        // 合并本地工具和MCP工具，返回完整的工具集
        return Stream.concat(
                Arrays.stream(localTools),
                Arrays.stream(mcpTools)).toArray(ToolCallback[]::new);
    }
}
