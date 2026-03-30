# 图片添加说明

## 📋 需要完成的步骤

### 1. 抠图处理
使用以下工具之一对两张图片进行抠图：
- remove.bg (推荐，免费)
- Photopea.com (在线Photoshop)
- Adobe Photoshop

### 2. 保存文件
将抠图后的PNG文件保存到：
```
/Users/gaojiayi/nas-ai-assistant/public/images/
```

**文件名：**
- 电路板图片 → `nas_board.png`
- NAS设备图片 → `nas-host.png`

### 3. 激活显示
保存图片后，在HTML中删除 `style="display: none;"`：
```html
<div class="banner-images" style="display: none;">  <!-- 删除这个style -->
```

## 🎨 设计效果
- 电路板图片：180px宽度，-5度旋转
- NAS设备图片：220px宽度，3度旋转
- 悬停效果：放大1.05倍并归正角度
- 阴影效果：增强立体感

## 🔄 完成后效果
两张抠图后的图片会显示在"骨灰级NAS玩家助手"横幅右侧，带有倾斜角度和悬停动画效果。
