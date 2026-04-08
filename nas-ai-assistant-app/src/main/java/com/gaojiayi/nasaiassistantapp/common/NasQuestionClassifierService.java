package com.gaojiayi.nasaiassistantapp.common;

import java.util.Set;

/**
 * 问题分类服务
 * 用于判断用户问题是NAS相关、通用问题还是敏感问题
 */
public class NasQuestionClassifierService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NasQuestionClassifierService.class);

    /**
     * 问题类型枚举
     */
    public  enum QuestionType {
        /**
         * NAS相关问题
         */
        NAS_RELATED,
        /**
         * 通用问题（天气、新闻、历史、科技等）
         */
        GENERAL,
        /**
         * 敏感问题（暴力、色情、赌博、毒品、政治等）
         */
        SENSITIVE,
        /**
         * 未知类型
         */
        UNKNOWN
    }

    /**
     * 敏感词集合
     */
    private static final Set<String> SENSITIVE_KEYWORDS = Set.of(
            "暴力", "杀人", "打架", "赌博", "毒品", "吸毒", "色情", "黄色", "裸体", "暗网",
            "政治", "反政府", "颠覆", "分裂", "恐怖", "炸弹", "武器", "枪支",
            "诈骗", "传销", "邪教", "迷信", "自杀", "自残", "未成年人性行为");
    /**
     * NAS关键词集合
     */
    private static final Set<String> NAS_KEYWORDS = Set.of("网络附加存储", "私有云", "个人云", "家庭服务器", "企业级存储", "集中式存储", "数据中心",
            "NAS专用硬盘", "机械硬盘", "HDD", "固态硬盘", "SSD", "盘位", "双盘位", "四盘位", "八盘位", "磁盘阵列", "RAID 0", "RAID 1", "RAID 5",
            "RAID 6", "RAID 10", "千兆网口", "2.5G网口", "万兆网卡", "10GbE", "处理器架构", "X86架构", "ARM架构", "UPS", "不间断电源", "NVMe缓存",
            "SSD缓存加速", "群晖", "DSM", "威联通", "QTS", "TrueNAS", "Unraid", "OpenMediaVault", "OMV", "Docker", "容器技术", "虚拟机",
            "软路由", "Plex", "Emby", "Jellyfin", "影视墙", "家庭影院", "Nextcloud", "ownCloud", "网盘挂载", "SMB", "CIFS", "NFS",
            "FTP", "SFTP", "WebDAV", "iSCSI", "内网穿透", "DDNS", "动态域名解析", "端口转发", "公网IP", "IPv6", "FRP", "Tailscale",
            "ZeroTier", "链路聚合", "数据备份", "自动同步", "增量备份", "裸机备份", "文件共享", "协同办公", "远程访问", "多端同步", "照片管理", "AI相册分类",
            "离线下载", "PT下载", "时间机器", "快照保护", "数据冗余", "容灾防勒索", "Synology", "QNAP", "极空间", "绿联", "铁威马", "华硕", "戴尔",
            "PowerScale", "易安信", "EMC", "NetApp", "HPE", "西部数据", "红盘", "希捷", "酷狼", "东芝", "飞牛");

    /**
     * 问候语集合
     */
    private static final Set<String> GREETING_KEYWORDS = Set.of(
            "你好", "您好", "hi", "hello", "嗨", "哈喽", "早上好", "晚上好",
            "在吗", "在不在", "有人吗", "能帮我吗", "可以帮我吗");

    /**
     * 通用问题关键词集合
     */
    private static final Set<String> GENERAL_KEYWORDS = Set.of(
            "天气", "温度", "下雨", "下雪", "晴朗", "多云", "雾",
            "新闻", "今日新闻", "最近新闻", "发生了什么",
            "历史", "古代", "近代", "朝代", "历史事件",
            "科技", "技术", "人工智能", "AI", "手机", "电脑", "互联网",
            "生活", "日常", "做饭", "做菜", "旅游", "出行", "交通",
            "教育", "学习", "考试", "学校", "大学", "工作", "职场",
            "健康", "医疗", "锻炼", "运动", "饮食", "减肥",
            "娱乐", "电影", "音乐", "游戏", "综艺", "电视剧",
            "经济", "股票", "投资", "理财", "金融",
            "地理", "国家", "城市", "省份", "地图", "位置",
            "动物", "植物", "科学", "天文", "物理", "化学");

    /**
     * 分类问题类型
     *
     * @param question 用户问题
     * @return 问题类型
     */
    public static QuestionType classify(String question) {
        if (question == null || question.trim().isEmpty()) {
            return QuestionType.UNKNOWN;
        }

        String lowerQuestion = question.toLowerCase();

        // 1. 首先检测敏感词
        if (containsSensitiveKeyword(lowerQuestion)) {
            return QuestionType.SENSITIVE;
        }

        // 2. 检测问候语（归类为NAS相关，因为是NAS助手的对话开场）
        if (containsGreetingKeyword(lowerQuestion)) {
            return QuestionType.NAS_RELATED;
        }

        // 3. 检测NAS相关关键词
        if (containsLoveKeyword(lowerQuestion)) {
            return QuestionType.NAS_RELATED;
        }

        // 4. 检测通用问题关键词
        if (containsGeneralKeyword(lowerQuestion)) {
            return QuestionType.GENERAL;
        }

        // 5. 默认返回未知，让RAG尝试处理
        return QuestionType.UNKNOWN;
    }

    /**
     * 检测是否包含敏感词
     */
    private static boolean containsSensitiveKeyword(String question) {
        for (String keyword : SENSITIVE_KEYWORDS) {
            if (question.contains(keyword)) {
                log.debug("检测到敏感词: {}", keyword);
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否包含问候语
     */
    private static boolean containsGreetingKeyword(String question) {
        for (String keyword : GREETING_KEYWORDS) {
            if (question.contains(keyword)) {
                log.debug("检测到问候语: {}", keyword);
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否包含NAS相关关键词
     */
    private static boolean containsLoveKeyword(String question) {
        for (String keyword : NAS_KEYWORDS) {
            if (question.contains(keyword)) {
                log.debug("检测到NAS相关关键词: {}", keyword);
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否包含通用问题关键词
     */
    private static boolean containsGeneralKeyword(String question) {
        for (String keyword : GENERAL_KEYWORDS) {
            if (question.contains(keyword)) {
                log.debug("检测到通用问题关键词: {}", keyword);
                return true;
            }
        }
        return false;
    }
}
