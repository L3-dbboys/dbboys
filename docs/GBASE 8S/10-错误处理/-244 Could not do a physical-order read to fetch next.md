错误信息
```
244    Could not do a physical-order read to fetch next row.
-244    无法执行物理顺序读来访存下一行。
```

错误原因
```
顺序扫描表时，所扫的行已被锁定。
```


解决方案：（选择一种）
```
1、创建合理索引，避免顺序扫描
2、设置锁等待时间，等待被锁定的数据行锁释放（sql前执行：set lock mode to wait 10;）
如是java程序，在JDBC URL总增加IFX_LOCK_MODE_WAIT=10;
如使用的是数据库管理工具datastudio，连接上右键-编辑连接-驱动属性-右键-添加新属性IFX_LOCK_MODE_WAIT 10
3、修改隔离级别为LC，（sql前执行：set isolation to committed read last committed;）
如是java程序，在JDBC URL总增加IFX_ISOLATION_LEVEL=5;
如使用的是数据库管理工具datastudio，连接上右键-编辑连接-驱动属性-右键-添加新属性IFX_ISOLATION_LEVEL 5
```

重现示例
|sesssion1|session2|
|---|---|
|dbaccess testdb - ||
|drop table if exists t;||
|create table t(col1 int,col2 char(100));||
|insert into t select tabid,tabname from systables; ||
|begin;||
|delete from t where col1=1;||
||dbaccess gbasedb -  |
||select * from t where col1=1;|
||-244    Could not do a physical-order read to fetch next row.|
||-107    ISAM error: record is locked.|
