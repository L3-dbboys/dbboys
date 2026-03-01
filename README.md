# DBboys

面向 GBase 8s 的开源数据库开发与运维工具，覆盖“装、用、管、卸”全生命周期。客户端基于 JavaFX，内置知识库与 SQL 工作台，适合日常开发、自助巡检与批量管理。

## 功能特性
- **SQL 工作台**：一键执行混合 `PL/SQL` / `DDL` / `DML` 脚本，支持绑定变量与结果集编辑。
- **多版本适配**：同一客户端无缝切换 GBase 8s 不同版本、字符集与 `sqlmode`。
- **对象管理**：库表拖拽展示，批量多选，DDL 导出，全局临时表与大对象查看编辑。
- **运维辅助**：安装/巡检/空间与参数管理、实例启停、后台任务、连接级变更历史、只读连接。
- **知识库**：Markdown 网状笔记，粘贴图片即用，表格/自动标题/编号/缩进、下载与滚动截图。
- **升级与发布**：支持在线/离线平滑升级，内置下载管理。

## 环境要求
- Windows 10/11（当前打包脚本仅适配 Windows）。
- JDK 25.0.1（需包含 `jpackage`、`jlink`），已在 `build.bat` 中默认使用。
- JavaFX 25 对应的 `javafx-jmods` 路径（在 `build.bat` 变量 `JAVAFX_JMODS` 中配置）。
- 依赖库已随项目提供于 `lib/`，无需额外下载。

## 从源码构建（Windows）
1. 安装 JDK 25.0.1，并将其 `bin` 目录加入系统 `PATH`。
2. 将本地 JavaFX jmods 路径填写到 `build.bat` 中的 `JAVAFX_JMODS` 变量。
3. 在项目根目录双击或执行 `build.bat`，脚本会：
   - 编译 Java 源码到 `bin/`，生成 `lib/lib_nonmodular/dbboys.jar`。
   - 使用 `jlink` 生成瘦身运行时，再用 `jpackage` 打包 app-image。
   - 复制资源后输出 `dbboys.zip`。
4. 解压生成的 `dbboys.zip`，运行 `dbboys/bin/dbboys.exe` 即可启动客户端。

## 目录速览
- `src/`：JavaFX 应用源码。
- `lib/lib_modular/`：JavaFX 模块。
- `lib/lib_nonmodular/`：第三方依赖、7zip 与升级脚本。
- `etc/`：默认配置与日志配置（`config.properties`、`log4j2.xml`）。
- `images/`：应用图标与界面资源。
- `docs/`：项目与数据库相关文档。
- `build.bat`：Windows 一键编译打包脚本。

## 配置与数据
- 初次启动会在 `data/` 生成 `dbboys.dat` 等运行数据；配置默认值见 `etc/config.properties`。
- UI 语言可通过 `UI_LANG`（如 `zh-CN`/`zh-TW`）调整；日志由 `etc/log4j2.xml` 管理。

## 版本与更新
- 当前内置版本：`DBboys V1.0.0beta.20260221`（见 `src/com/dbboys/app/Main.java`）。
- 更新记录请查看 `CHANGELOG.md`。

## 许可
本项目采用仓库根目录的 `LICENSE`。
