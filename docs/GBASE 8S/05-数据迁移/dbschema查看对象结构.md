dbschema工具输出创建表、视图或数据库所需的SQL语句。
### 示例1-导出库testdb的建库SQL语句到文本gbasedb.sql
```
dbschema -d gbasedb -ss gbasedb.sql
```
### 示例2-导出gbasedb库中表test的建表语句
```
dbschema –d gbasedb -t test -ss
```
### 示例3-全库ddl语句导出到gbasedb.sql
```
dbschema -d gbasedb gbasedb.sql
```
参考文档【[GBase 8s 导入导出工具指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Migration_Guide.pdf)】
