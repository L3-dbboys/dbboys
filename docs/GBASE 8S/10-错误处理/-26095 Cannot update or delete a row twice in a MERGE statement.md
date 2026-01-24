错误信息
```
-26095  Cannot update or delete a row twice in a MERGE statement.
-26095  在 MERGE 语句中无法对一行进行两次更新或删除。
```

错误原因：
```
merge中的update有多行匹配，无法更新
```

解决方案
```
检查数据是否唯一匹配
```

以下操作可重现：
```
drop table if exists t1;
drop table if exists t2;
create table t1(col1 int,col2 int);
insert into t1 values(1,1);
insert into t1 values(1,2);
create table t2(col1 int,col2 int);
insert into t2 values(1,1);
merge into t2 using t1 on t2.col1=t1.col1 when matched then update set t2.col2=t1.col1;
```
