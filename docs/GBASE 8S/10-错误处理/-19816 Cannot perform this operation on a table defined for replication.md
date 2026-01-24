错误信息
```
-19816  Cannot perform this operation on a table defined for replication.
-19816  无法针对为复制而定义的表执行该操作。
```


错误原因
```
定义了CDC复制的表不支持DDL操作。
```

解决方案
```
关闭该表的复制。
```

示例代码
```
dbaccess syscdcv1 -<<EOF
EXECUTE FUNCTION cdc_opensess("gbase01", 0, 300, 1, 1, 0);
EXECUTE FUNCTION cdc_set_fullrowlogging('testdb:gbasedbt.testtab1', 0);
EOF
```