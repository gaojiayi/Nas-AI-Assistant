package com.gaojiayi.nasaiassistantapp.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NAS图片搜索工具
 *
 * 提供NAS相关的专业图片搜索接口：
 * - NAS品牌型号设备图片搜索
 * - NAS软件界面截图搜索
 * - NAS配置教程图片搜索
 * - NAS网络拓扑图搜索
 * - NAS硬件组件图片搜索
 *
 * 使用 Pexels API 进行图片搜索
 */
@Component
@Slf4j
public class NasImageSearchTool {

    // Pexels API 密钥 - 从环境变量读取
    private static final String API_KEY = System.getenv("PEXELS_API_KEY");

    // Pexels 常规搜索接口
    private static final String API_URL = "https://api.pexels.com/v1/search";

    /**
     * 搜索NAS品牌型号设备图片
     *
     * @param brand NAS品牌（如：群晖、威联通、铁威马、华芸、西部数据等）
     * @param model 型号（可选，如：DS220+、TS-464、F5-221等）
     * @return 图片 URL 列表（逗号分隔）
     */
    @Tool(description = "搜索NAS品牌型号设备图片，品牌可选：群晖、威联通、铁威马、华芸、西部数据等")
    public String searchNasDevice(
            @ToolParam(description = "NAS品牌，如：群晖、威联通、铁威马、华芸、西部数据") String brand,
            @ToolParam(description = "NAS型号，可选，如：DS220+、TS-464、F5-221等") String model
    ) {
        String query = "NAS " + brand;
        if (StrUtil.isNotBlank(model)) {
            query += " " + model;
        }
        log.info("搜索NAS设备图片：品牌={}，型号={}", brand, model);
        return searchImages(query);
    }

    /**
     * 搜索NAS软件界面截图
     *
     * @param software 软件名称（如：DSM、QTS、TOS、Asustor ADM等）
     * @param feature 功能界面（如：控制面板、File Station、Storage Manager、虚拟机等）
     * @return 图片 URL 列表（逗号分隔）
     */
    @Tool(description = "搜索NAS软件界面截图，软件可选：DSM、QTS、TOS、Asustor ADM等")
    public String searchNasSoftwareInterface(
            @ToolParam(description = "NAS软件名称，如：DSM、QTS、TOS、Asustor ADM") String software,
            @ToolParam(description = "功能界面，如：控制面板、File Station、Storage Manager、虚拟机") String feature
    ) {
        String query = software + " interface " + feature;
        log.info("搜索NAS软件界面：软件={}，功能={}", software, feature);
        return searchImages(query);
    }

    /**
     * 搜索NAS配置教程图片
     *
     * @param configType 配置类型（如：RAID、网络、用户权限、备份、虚拟机等）
     * @param brand 品牌（可选，如：群晖、威联通等）
     * @return 图片 URL 列表（逗号分隔）
     */
    @Tool(description = "搜索NAS配置教程图片，配置类型可选：RAID、网络、用户权限、备份、虚拟机等")
    public String searchNasConfigTutorial(
            @ToolParam(description = "配置类型，如：RAID、网络、用户权限、备份、虚拟机") String configType,
            @ToolParam(description = "NAS品牌，可选，如：群晖、威联通等") String brand
    ) {
        String query = "NAS " + configType + " configuration tutorial";
        if (StrUtil.isNotBlank(brand)) {
            query += " " + brand;
        }
        log.info("搜索NAS配置教程：类型={}，品牌={}", configType, brand);
        return searchImages(query);
    }

    /**
     * 搜索NAS网络拓扑图
     *
     * @param topologyType 拓扑类型（如：家庭网络、企业网络、远程访问、备份网络等）
     * @return 图片 URL 列表（逗号分隔）
     */
    @Tool(description = "搜索NAS网络拓扑图，类型可选：家庭网络、企业网络、远程访问、备份网络等")
    public String searchNasNetworkTopology(
            @ToolParam(description = "网络拓扑类型，如：家庭网络、企业网络、远程访问、备份网络") String topologyType
    ) {
        String query = "NAS network topology " + topologyType;
        log.info("搜索NAS网络拓扑图：类型={}", topologyType);
        return searchImages(query);
    }

    /**
     * 搜索NAS硬件组件图片
     *
     * @param component 组件类型（如：硬盘、内存、RAID卡、网卡、电源等）
     * @param brand 品牌（可选）
     * @return 图片 URL 列表（逗号分隔）
     */
    @Tool(description = "搜索NAS硬件组件图片，组件类型可选：硬盘、内存、RAID卡、网卡、电源等")
    public String searchNasHardwareComponent(
            @ToolParam(description = "硬件组件类型，如：硬盘、内存、RAID卡、网卡、电源") String component,
            @ToolParam(description = "品牌，可选") String brand
    ) {
        String query = "NAS " + component;
        if (StrUtil.isNotBlank(brand)) {
            query += " " + brand;
        }
        log.info("搜索NAS硬件组件：组件={}，品牌={}", component, brand);
        return searchImages(query);
    }

    /**
     * 搜索NAS应用中心软件图片
     *
     * @param appType 应用类型（如：媒体服务器、下载工具、备份软件、监控软件、办公软件等）
     * @param appName 具体应用名称（可选，如：Plex、Jellyfin、Transmission、Docker等）
     * @return 图片 URL 列表（逗号分隔）
     */
    @Tool(description = "搜索NAS应用中心软件图片，应用类型可选：媒体服务器、下载工具、备份软件、监控软件等")
    public String searchNasApplication(
            @ToolParam(description = "应用类型，如：媒体服务器、下载工具、备份软件、监控软件") String appType,
            @ToolParam(description = "具体应用名称，可选，如：Plex、Jellyfin、Transmission、Docker") String appName
    ) {
        String query = "NAS " + appType;
        if (StrUtil.isNotBlank(appName)) {
            query += " " + appName;
        }
        log.info("搜索NAS应用：类型={}，应用={}", appType, appName);
        return searchImages(query);
    }

    /**
     * 通用图片搜索方法
     *
     * @param query 搜索关键词
     * @return 图片 URL 列表（逗号分隔）
     */
    private String searchImages(String query) {
        try {
            List<String> imageUrls = searchMediumImages(query);
            if (imageUrls.isEmpty()) {
                return "未找到相关图片，请尝试其他关键词。";
            }
            return String.join(",", imageUrls);
        } catch (Exception e) {
            log.error("搜索图片失败：query={}", query, e);
            return "搜索图片失败：" + e.getMessage();
        }
    }

    /**
     * 搜索中等尺寸的图片列表
     *
     * @param query 搜索关键词
     * @return 图片 URL 列表
     */
    private List<String> searchMediumImages(String query) {
        // 设置请求头（包含 API 密钥）
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", API_KEY);

        // 设置请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("size", "medium");

        // 发送 GET 请求
        String response = HttpUtil.createGet(API_URL)
                .addHeaders(headers)
                .form(params)
                .execute()
                .body();

        // 解析响应 JSON
        return JSONUtil.parseObj(response)
                .getJSONArray("photos")
                .stream()
                .map(photoObj -> (JSONObject) photoObj)
                .map(photoObj -> photoObj.getJSONObject("src"))
                .map(photo -> photo.getStr("medium"))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }
}
