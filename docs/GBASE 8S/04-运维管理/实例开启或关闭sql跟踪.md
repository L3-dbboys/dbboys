### 开启sqltrace
打开sqltrace跟踪最多15000条sql，所需内存：15000*4k=60MB
```
dbaccess sysadmin - <<!
EXECUTE FUNCTION task("set sql tracing on","15000","4","high","global");
!
```
查看执行耗时较长sql：
```
dbaccess sysmaster -<<!
unload to sql.unl select sql_maxtime,sql_avgtime,sql_statement from syssqltrace order by sql_maxtime desc;
!
```

命令查看trace信息：
```
onstat -g his
```
查询顺序扫描的sql：
```
select first 10 sql_runtime,sql_executions,sql_statement from 
syssqltrace t,
syssqltrace_iter i
where t.sql_id = i.sql_id 
and i.sql_itr_info='Seq Scan'
order by 1 desc;
```

### 关闭sqltrace
```
dbaccess sysadmin - <<!
EXECUTE FUNCTION task("set sql tracing off");
!
```

### 暂停sqltrace
```
dbaccess sysadmin - <<!
EXECUTE FUNCTION task("set sql tracing suspend");
!
```

