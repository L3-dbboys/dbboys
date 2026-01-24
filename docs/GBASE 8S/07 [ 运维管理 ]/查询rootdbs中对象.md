### 存储于rootdbs中的用户表、索引
dbaccess sysmaster -
```
SELECT UNIQUE e.dbsname dbname, e.tabname
FROM sysdbspaces d, syschunks c, sysextents e
WHERE d.name = 'rootdbs'
AND c.dbsnum = d.dbsnum
AND e.chunk = c.chknum
AND e.dbsname NOT IN ('rootdbs', 'sysmaster', 'sysadmin', 'sysuser', 'sysutils','sysha','system')
ORDER BY 1, 2;
```
输出
```
dbname   testdb
tabname  tabinroot
```