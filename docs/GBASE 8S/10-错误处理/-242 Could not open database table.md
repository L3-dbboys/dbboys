错误信息
```
-242 Could not open database table.
-113 ISAM error: the file is locked.
-106 ISAM error: non-exclusive access.
-104    ISAM error: too many files open.
-242 不能打开数据库表 (表名)。
-113 ISAM 错误：该文件已被锁定。
-106: ISAM 错误：非独占访问。
```

错误原因
```
DDL操作无法给表加独占锁，或打开文件太多超过内核参数限制
-113 表上或行上有锁
-106 表无锁但有查询打开了此表，无法锁定
```

解决方案
```
1、ISAM=-113 设置锁等待时间，或加大锁等时间
2、ISAM=-106 如果是创建索引，可在sql语句最后加上关键字online，其他操作稍等后重试。
3、ISAM=-104 调整内核参数open files
```


重现示例：
|session1|session2|
|---|---|
|dbaccess gbasedb -||
|CREATE TABLE exp_tab (a int) FRAGMENT BY EXPRESSION partition p1 (a `<=` 10) IN datadbs1, partition p2 (a `<=` 20) IN datadbs1, partition p3 (a `<=` 30) IN datadbs1; ||
|begin;||
|insert into exp_tab values(10);||
||dbaccess gbasedb - |
||ALTER FRAGMENT ON TABLE exp_tab ADD PARTITION p4 (a`<=`40) IN datadbs1;|
||报错：242: 不能打开数据库表 (gbasedbt.exp_tab)。|
||报错：113: ISAM 错误：文件已锁定。|
|String SelectTab = "select * from exp_tab";||
|pstmt = conn.prepareStatement(SelectTab);||
|ResultSet rs=pstmt.executeQuery();||
|while (rs.next())`{`||
|            Thread.sleep(1000000); //模拟查询未完成，游标未释放||
|            System.out.println(rs.getString(1));||
|`}`||
|dbaccess gbasedb -||
||ALTER TABLE exp_tab ADD b int;|
||报错：242: 不能打开数据库表 (gbasedbt.exp_tab)。|
||报错：-106: ISAM 错误：非独占访问。|