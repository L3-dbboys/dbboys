查询表注释：
```sql
select a.tabname,a.comments from syscomments a,systables b where a.tabname = b.tabname and b.tabid >= 99;
```
输出示例：
```
tabname   t
comments  表t comment
```
查询字段注释：
```sql
select a.* from SYSCOLCOMMENTS a,systables b where a.tabname = b.tabname and b.tabid >= 99;
```
输出示例：
```
tabname   t
colname   col1
comments  t.col1
```
导出comment为sql脚本：
```sql
unload to comment.unl delimiter ";"
select 'comment on table ' || tabname || ' is ''' || replace(comments, chr(39), '''''') || '''' as comment from syscomments 
union all 
select 'comment on column ' || tabname || '.' || colname || ' is ''' || replace(comments, chr(39), '''''') || '''' as comment  from syscolcomments;
```
示例输出：
```sql
comment on table t is '表t';
comment on column t.col1 is '列t.col1';
```