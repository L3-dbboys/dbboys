错误信息
```
-252    Cannot get system information for table.
-113    ISAM error: the file is locked.
-252    不能获取表的系统信息。
-113    ISAM 错误：该文件已被锁定。
```

错误原因
```
表上有锁，select count(*)就会报此错误
```

解决方案：(三选一)
```
1、设置锁等待时间。或设置隔离级别为脏读。（CR或LC都会报错）
2、使用LC隔离级别并增加where 1=1条件
3、使用LC隔离级别并将count(*)修改为count(1)
```

重现示例
|序号|session1|session2|
|---|---|---|
|1|create table t(col1 int);| |
|2|begin;| |
|3|insert into t values(100);| |
|4| |select count(*) from t;|
|5| |报错：-252，-113|
|6| |set isolation committed read last committed;|
|7| |select count(*) from t where 1=1;|
|8| |正常返回count|