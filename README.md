# Yreader

一个用于在 IDEA 编辑器中阅读小说的插件，支持分页阅读、内容隐藏等功能。

## 功能特点

- 支持导入 TXT 格式小说文件
- 自动分页显示，每页显示 3 行，每行 20 字
- 使用 Java 文档注释格式显示内容，融入代码中
- 支持快捷键翻页和鼠标滚轮翻页
- 支持快速隐藏/显示内容功能
- 防抖动设计，避免快速翻页时的性能问题

## 快捷键

- `Ctrl + Alt + N`: 选择并插入小说文件
- `Alt + →`: 下一页
- `Alt + ←`: 上一页
- `Ctrl + Alt + H`: 显示/隐藏小说内容
- 鼠标滚轮：上下翻页

## 使用方法

1. 安装插件后，使用 `Ctrl + Alt + N` 打开文件选择器
2. 选择 TXT 格式的小说文件
3. 小说内容会以 JavaDoc 格式插入到当前光标位置
4. 使用快捷键或鼠标滚轮进行翻页
5. 需要隐藏内容时，按 `Ctrl + Alt + H`

## 开发说明

### 主要类说明

- `NovelService`: 核心服务类，负责小说内容的管理和格式化
- `InsertNovelAction`: 处理小说文件的选择和插入
- `NextPageAction`: 处理下一页功能
- `PreviousPageAction`: 处理上一页功能
- `ToggleNovelAction`: 处理内容显示/隐藏功能

### 构建和运行

1. 克隆项目到本地
2. 使用 IntelliJ IDEA 打开项目
3. 运行 `gradle buildPlugin` 构建插件
4. 在 IDEA 中安装生成的插件文件

## 注意事项

- 插件会在文档中插入注释格式的内容
- 隐藏功能会将内容替换为普通的 TODO 注释
- 建议在代码注释区域使用，避免影响代码逻辑 

## 配置选项

可以在 Settings/Preferences -> Tools -> Novel Reader Settings 中配置：

- 每行字数：控制每行显示的字符数（默认20）
- 每页行数：控制每页显示的行数（默认3）

配置修改后会立即生效，并重新格式化当前加载的内容。 