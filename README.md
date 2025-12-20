# EmojiMaster (本地表情包管理器)

这是一个基于 Android Jetpack 架构构建的轻量级本地表情包管理应用。项目旨在演示现代 Android 开发的最佳实践，特别是针对 Android 10+ 分区存储（Scoped Storage）的严格适配与高效的本地文件管理。

## 🌟 核心功能

*   **🔒 隐私安全**: 严格遵守分区存储规范，**无需**申请 `READ_EXTERNAL_STORAGE` 或 `WRITE_EXTERNAL_STORAGE` 敏感权限即可运行。
*   **📂 智能导入**: 
    *   调用系统文件选择器（SAF）批量导入图片。
    *   内置 **MD5 哈希校验**，自动识别重复文件，智能处理文件名冲突。
*   **⚡ 流畅体验**: 
    *   基于 `RecyclerView` + `GridLayoutManager` 的网格展示。
    *   集成 `Glide` 图片加载库，通过 `DiskCacheStrategy.ALL` 策略实现高效缓存。
*   **👁️ 大图预览**: 支持点击列表项查看高清全屏大图。
*   **✅ 批量管理**: 
    *   长按任意图片进入**编辑模式**。
    *   支持多选、反选，一键批量删除（同步清理物理文件与数据库记录）。

## 🛠 技术栈

*   **语言**: Kotlin
*   **架构模式**: MVVM (Model-View-ViewModel)
*   **UI 框架**: Android View System (XML) + ViewBinding
*   **异步与并发**: Kotlin Coroutines + Flow + LiveData
*   **本地存储**: 
    *   **数据库**: Jetpack Room (ORM)
    *   **文件系统**: App-Specific Directory (应用私有目录)
*   **依赖库**: Glide (图片加载), AndroidX Lifecycle

## 📱 环境要求

*   **Min SDK**: API 24 (Android 7.0)
*   **开发工具**: Android Studio Ladybug 或更高版本

## 📂 项目结构概览

```text
com.example.localshare
├── data                 # 数据层 (Room Database, DAO, Repository)
├── ui                   # 表现层 (Adapter, ViewModel, PreviewActivity)
└── MainActivity.kt      # 应用主入口 (列表展示、导入逻辑、交互控制)
```

## 🚀 快速开始

1.  克隆本项目到本地。
2.  使用 Android Studio 打开项目根目录。
3.  等待 Gradle Sync 完成。
4.  连接真机或模拟器（确保系统版本 >= Android 7.0）。
5.  点击 **Run** 运行应用。

## ⚠️ 注意事项

*   **数据持久性**: 图片存储在应用的**私有目录**中。这意味着卸载应用后，导入的所有图片数据将被系统自动清除。
*   **导入性能**: 当前版本的导入与哈希计算逻辑在 IO 协程中执行，若一次性导入数百张高清大图，可能会产生一定的处理耗时，建议分批导入。

---
**License**: MIT
**Maintainer**: Android 架构师
