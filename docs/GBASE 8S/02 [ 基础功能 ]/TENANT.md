### 创建tenant  
创建tenant，指定空间，locale，vp和限制锁（仅限普通用户）
```sql
EXECUTE FUNCTION task('tenant create', 'mytenant', '{dbspace:"tenantdbs01", locale:"zh_CN.utf8", vpclass:"myvp,num=1", session_limit_locks:"500", logmode:"BUFFERED"}' );
```
删除tenant
```sql
EXECUTE FUNCTION task('tenant drop', 'mytenant');
```
普通用户限制会话锁的数据量：
```sql
SET ENVIRONMENT IFX_SESSION_LIMIT_LOCKS '500';
```