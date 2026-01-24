创建表：
```sql
create table tblob(col1 blob,col2 clob);
```
查询库中包含clob/blob字段的表：
```sql
select tabname,colname as clobcol from systables t,syscolumns c
where t.tabid=c.tabid
and c.coltype=41
and t.tabid>99
and c.extended_id=11  --clob
--and c.extended_id=10  --blob
;
```
查询包含text字段的表：
```
select tabname,colname  from systables t,syscolumns c
where t.tabid=c.tabid
and c.coltype=12
and t.tabid>99;
```
输出：
```

tabname  tblob
clobcol  col2

查询到 1 行。
```