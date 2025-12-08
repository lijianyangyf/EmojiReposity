# 整体设计思路

**功能点：**

1. 启动后快速打开「表情图库」页。
2. 使用 **RecyclerView + GridLayoutManager** 展示表情（网格方式）。
3. 表情图片实际保存在 **文件存储** 中（应用专属目录）。
4. 图片信息（文件路径、描述等）放在 **Room 数据库** 中。
5. 支持 > 1000 张图片，滑动流畅不卡顿，不内存泄漏。

**结构分层：**

* **数据层（Data Layer）**

  * `AppDatabase`（Room）
  * `EmojiDao`
  * `EmojiEntity`
  * `EmojiRepository`
* **逻辑层（ViewModel）**

  * `EmojiViewModel`
* **UI 层**

  * `MainActivity`
  * `EmojiAdapter`（RecyclerView Adapter）
  * `activity_main.xml` / `item_emoji.xml`

**文件存储方案：**

* 使用应用专属的外部目录（不用额外申请存储权限）：

  ```kotlin
  val emojiDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/emoji")
  ```
* 你可以用 **Android Studio -> Device File Explorer** 或者数据线，提前把表情 PNG/JPG 复制进这个目录（上千张没问题）。

> **说明**：下面全部以 **Kotlin + AndroidX + Room + Glide** 为例，结构简单、性能好，也方便以后扩展。