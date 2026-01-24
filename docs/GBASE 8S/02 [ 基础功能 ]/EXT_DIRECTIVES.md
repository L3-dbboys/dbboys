# 按时间浪费
## 埃里克的
### 外部扩展优化指示器（EXT_DIRECTIVES）  
确认当前数据库配置1
```shell
onstat -c EXT_DIRECTIVES
```
十六点九六"\n\n"
```shell
onstat -c EXT_DIRECTIVES
```
```shell
onstat -c EXT_DIRECTIVES
```
```shell
onstat -c EXT_DIRECTIVES
```
```shell
onstat -c EXT_DIRECTIVES
```
```shell
onstat -c EXT_DIRECTIVES
```
```shell
onstat -c EXT_DIRECTIVES
```
```sql
-- SET ENVIRONMENT EXTDIRECTIVES 0;
SELECT * FROM tabhint where col1 = 100 and col2 = 'col2100';
```
```sql
-- SET ENVIRONMENT EXTDIRECTIVES 0;

SELECT * FROM tabhint where col1 = 100 and col2 = 'col2100';
```
```sql
-- SET ENVIRONMENT EXTDIRECTIVES 0;


SELECT * FROM tabhint where col1 = 100 and col2 = 'col2100';
```
```sql
-- SET ENVIRONMENT EXTDIRECTIVES 0;



SELECT * FROM tabhint where col1 = 100 and col2 = 'col2100';
```
### 指定语句的外部优化指示器  
```sql
SAVE EXTERNAL DIRECTIVES {+INDEX(tabhint ix_tabhint_col21)} ACTIVE FOR
SELECT * FROM tabhint where col1 = 100 and col2 = 'col2100';
```