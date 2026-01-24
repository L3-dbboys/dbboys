### 编译
```
javac -encoding UTF-8 -d bin -sourcepath src -cp "lib\lib_modular\*;lib\lib_nonmodular\*" src\com\dbboys\app\Main.java 
```
### 生成jar
```
jar --create --file lib/lib_nonmodular/dbboys.jar --main-class com.dbboys.app.Main -C bin .
```
### 生成最小化jre
```
jlink  --module-path "D:\Programs\javafx-jmods-25.0.1;lib\lib_modular"  --add-modules javafx.fxml,org.json,net.sf.jsqlparser,javafx.swing,org.controlsfx.controls,org.commonmark,java.sql,org.apache.lucene.queryparser,org.apache.lucene.sandbox,org.apache.lucene.analysis.smartcn,org.apache.lucene.core,org.apache.logging.log4j,org.apache.logging.log4j.core  --output jre-min --strip-debug --no-man-pages  --no-header-files
```
### 打包
```
jpackage --type app-image --name dbboys --input lib\lib_nonmodular --main-jar dbboys.jar --main-class com.dbboys.app.Main --runtime-image jre-min --icon images\dbboys.ico --java-options "-Xmx512m" --java-options "-Dlog4j2.configurationFile=etc/log4j2.xml"
```