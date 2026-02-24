@echo off
:: ========== 核心修复：彻底解决中文乱码 ==========
:: 设置控制台代码页为UTF-8（必须放在最开头，且无多余字符）
chcp 65001 >nul 2>&1
:: 设置批处理文件自身的编码相关环境变量
set "PYTHONIOENCODING=UTF-8"
set "LC_ALL=zh_CN.UTF-8"
:: 开启延迟扩展（避免变量解析问题）
setlocal enabledelayedexpansion

:: 定义变量，JAVAFX_JMODS为javafx-jmods所在路径
set JAVAFX_JMODS=D:\Programs\javafx-jmods-25.0.1

:: 编译所有子目录的源码（用空格分隔不同目录的文件）
dir /b /s src\*.java > sources.txt
javac -encoding UTF-8 -d bin -sourcepath src -cp lib\lib_modular\*;lib\lib_nonmodular\* @sources.txt
echo 编译源码完成。

:: 复制fxml、css文件夹到bin
xcopy /e /h /y /q "src\com\dbboys\fxml\*" "bin\com\dbboys\fxml\"
xcopy /e /h /y /q "src\com\dbboys\css\*" "bin\com\dbboys\css\"

:: 生成jar文件
jar --create --file lib/lib_nonmodular/dbboys.jar --main-class com.dbboys.app.Main -C bin .
echo 生成dbboys.jar完成。


:: 生成最小化jre
jlink  --module-path "%JAVAFX_JMODS%;lib\lib_modular"  --add-modules javafx.fxml,org.json,net.sf.jsqlparser,javafx.swing,org.controlsfx.controls,org.commonmark,java.sql,org.apache.lucene.queryparser,org.apache.lucene.sandbox,org.apache.lucene.analysis.smartcn,org.apache.lucene.core,org.apache.logging.log4j,org.apache.logging.log4j.core  --output jre-min --strip-debug --no-man-pages  --no-header-files
echo 生成最小化jre完成。


:: 打包exe
jpackage --type app-image --name dbboys --input lib\lib_nonmodular --main-jar dbboys.jar --main-class com.dbboys.app.Main --runtime-image jre-min --icon images\dbboys.ico --java-options "-Xmx512m" --java-options "-Dlog4j2.configurationFile=etc/log4j2.xml"
echo 打包完成。

:: 删除编译时生成的临时文件 sources.txt
if exist "sources.txt" (
    del /f /q "sources.txt"
    echo 已删除临时文件：sources.txt
)

:: 删除编译输出目录 bin（包含旧的class文件）
if exist "bin" (
    rd /s /q "bin"
    echo 已删除旧编译目录：bin
)

:: 删除旧的JAR包（避免打包旧jar）
if exist "lib\lib_nonmodular\dbboys.jar" (
    del /f /q "lib\lib_nonmodular\dbboys.jar"
    echo 已删除旧JAR包：lib\lib_nonmodular\dbboys.jar
)

:: 删除最小化JRE目录（避免旧jre干扰）
if exist "jre-min" (
    rd /s /q "jre-min"
    echo 已删除旧最小化JRE目录：jre-min
)

:: 复制其他目录到dbboys
xcopy /e /h /y /q "docs\*" "dbboys\docs\"
xcopy /e /h /y /q "extlib\*" "dbboys\extlib\"
xcopy /e /h /y /q "images\*" "dbboys\images\"
xcopy /e /h /y /q "etc\*" "dbboys\etc\"
echo 文件夹已复制。


:: 压缩dbboys目录
set "EXE=%~dp0lib\lib_nonmodular\7za.exe"
set "ZIP_FILE=%~dp0dbboys.zip"
if exist "%ZIP_FILE%" (
    del /f /q "%ZIP_FILE%"
    echo 已删除旧的dbboys.zip压缩包。
)

"%EXE%" a -tzip -mx=5 -r -y "%ZIP_FILE%" "dbboys\*"

echo 已打包dbboys.zip。

:: 删除dbboys目录
if exist "dbboys" (
    rd /s /q "dbboys"
    echo 已删除dbboys目录。
)
pause