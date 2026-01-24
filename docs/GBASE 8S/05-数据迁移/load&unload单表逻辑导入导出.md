### 说明
load/unload用于单表文本数据的装载和卸载，unload导出表数据到指定文件，load则将指定的文本文件数据插入到表。  
load是dbaccess的功能，只能在dbaccess中使用。
### 示例1-导出表test的所有数据到test.unl文件
```
dbaccess gbasedb -<<!
unload to test.unl select * from test;
!
```
### 示例2-将文本文件test.unl的数据导入到表test
```
dbaccess gbasedb -<<!
load from test.unl insert into test;
!
```
参考文档【[GBase 8s 导入导出工具指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Migration_Guide.pdf)】