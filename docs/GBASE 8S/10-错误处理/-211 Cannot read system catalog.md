应用错误日志：（URL中IFX_LOCK_MODE_WAIT=10）
```
Caused by: java.sql.SQLException: Cannot read system catalog (systables).
	at com.gbasedbt.jdbc.IfxSqli.a(IfxSqli.java:3570)
	at com.gbasedbt.jdbc.IfxSqli.D(IfxSqli.java:3852)
	at com.gbasedbt.jdbc.IfxSqli.dispatchMsg(IfxSqli.java:2738)
	at com.gbasedbt.jdbc.IfxSqli.receiveMessage(IfxSqli.java:2663)
	at com.gbasedbt.jdbc.IfxSqli.executePrepare(IfxSqli.java:1290)
	at com.gbasedbt.jdbc.IfxPreparedStatement.f(IfxPreparedStatement.java:485)
	at com.gbasedbt.jdbc.IfxPreparedStatement.a(IfxPreparedStatement.java:466)
	at com.gbasedbt.jdbc.IfxPreparedStatement.<init>(IfxPreparedStatement.java:268)
	at com.gbasedbt.jdbc.IfxSqliConnect.h(IfxSqliConnect.java:6339)
	at com.gbasedbt.jdbc.IfxSqliConnect.prepareStatement(IfxSqliConnect.java:2542)
	at com.alibaba.druid.filter.FilterChainImpl.connection_prepareStatement(FilterChainImpl.java:535)
	at com.alibaba.druid.filter.FilterAdapter.connection_prepareStatement(FilterAdapter.java:908)
	at com.alibaba.druid.filter.FilterEventAdapter.connection_prepareStatement(FilterEventAdapter.java:116)
	at com.alibaba.druid.filter.FilterChainImpl.connection_prepareStatement(FilterChainImpl.java:531)
	at com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl.prepareStatement(ConnectionProxyImpl.java:326)
	at com.alibaba.druid.pool.DruidPooledConnection.prepareStatement(DruidPooledConnection.java:362)
	at org.apache.ibatis.executor.statement.PreparedStatementHandler.instantiateStatement(PreparedStatementHandler.java:86)
	at org.apache.ibatis.executor.statement.BaseStatementHandler.prepare(BaseStatementHandler.java:88)
	at org.apache.ibatis.executor.statement.RoutingStatementHandler.prepare(RoutingStatementHandler.java:59)
	at org.apache.ibatis.executor.ReuseExecutor.prepareStatement(ReuseExecutor.java:89)
	at org.apache.ibatis.executor.ReuseExecutor.doQuery(ReuseExecutor.java:59)
	at org.apache.ibatis.executor.BaseExecutor.queryFromDatabase(BaseExecutor.java:325)
	at org.apache.ibatis.executor.BaseExecutor.query(BaseExecutor.java:156)
	at org.apache.ibatis.executor.CachingExecutor.query(CachingExecutor.java:109)
	at com.github.pagehelper.PageInterceptor.intercept(PageInterceptor.java:151)
	at org.apache.ibatis.plugin.Plugin.invoke(Plugin.java:62)
	at com.sun.proxy.$Proxy583.query(Unknown Source)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:151)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:145)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:140)
	at sun.reflect.GeneratedMethodAccessor262.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:427)
	... 134 common frames omitted
Caused by: java.sql.SQLException: ISAM error: Lock Timeout Expired
	at com.gbasedbt.util.IfxErrMsg.getSQLException(IfxErrMsg.java:408)
	at com.gbasedbt.jdbc.IfxSqli.D(IfxSqli.java:3857)
	... 166 common frames omitted
```
相关错误号：
```
-211    Cannot read system catalog <error-text\> # 无法读取系统目录 <error-text>。
-154    ISAM error: Lock Timeout Expired. # ISAM错误: 锁定因超时而失效。
-144    ISAM error: key value locked.  # ISAM 错误: 键值被锁定。
-107    ISAM error: record is locked.  # ISAM错误: 记录被锁定。
```
错误原因
```
查询的表正在执行ddl操作，表已被锁。如：alter table,create index等。
```

解决方案
```
设置锁等待，或稍后重试。或kill锁表的会话。
```


SQL查找锁表会话：(库名表名替换为目标库名表名)
```
select first 1 owner as sid from sysmaster:syslocks where dbsname='testdb' and tabname='t';
```
onstat查找锁表会话：（t为表名，替换为目标表名）
```
echo "set isolation to dirty read;select lower(hex(partnum)) from systables where tabname='t'" |dbaccess testdb
#输出：
0x00200047
#查找锁表的线程：
onstat -k |grep 200047
#输出：（需要第三列地址46163728）
448b7300         0                46163728           448b57e8         HDR+X    200047   0           0       
#查找sessionid：
onstat -u |grep 46163728
#输出：(需要第三列sessionid）
46163728         Y-BP--- 165      gbasedbt 0        47747468         0    18    0        66
```
查看锁表sql：
```
onstat -g ses 165
```
kill会话：
```
onmode -z 165
```



以下操作可重现：
|session1|session2|
|----|----|
|create table t(col1 int);||
|begin;||
|create index idx_t on t(col1);| |
| |select * from t;|
| |返回错误211/107|
|rollback;||
|begin;||
|alter table t add col2 int;| |
| |select * from t;|
| |返回错误211/144|
| |set lock mode to wait 2;|
| |select * from t;|
| |返回错误211/154|
|rollback;||
|begin;||
|drop table t;| |
| |select * from t;|
| |返回错误211/154|



