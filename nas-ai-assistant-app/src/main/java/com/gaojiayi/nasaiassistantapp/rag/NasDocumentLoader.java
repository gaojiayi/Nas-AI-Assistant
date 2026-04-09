package com.gaojiayi.nasaiassistantapp.rag;

import java.util.ArrayList;

import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.ai.document.Document;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Component
@Slf4j
public class NasDocumentLoader {

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    /**
     * 加载所有markdown文件
     * 
     * @return
     */
    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:documents/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                // 从文件名中提取状态：格式为 "nas-技术篇.md"
                String status = "";
                int dashIndex = filename.lastIndexOf(" - ");
                int suffixIndex = filename.lastIndexOf("篇.md");
                if (dashIndex != -1 && suffixIndex != -1) {
                    status = filename.substring(dashIndex + 3, suffixIndex);
                }
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("status", status)
                        .build();
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(markdownDocumentReader.get());
            }
        } catch (Exception e) {
            log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;
    }

}
