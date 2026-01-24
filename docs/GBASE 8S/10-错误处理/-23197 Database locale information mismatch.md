错误信息
```
-23197  Database locale information mismatch.
```

 错误原因
```
 连接的数据库字符集与DB_LOCALE环境变量不一致
```

解决方案
```
设置正确的环境变量
```

参考步骤
```
#查询数据库字符集
echo "select * from sysdbslocale;"|dbaccess sysmaster

#5488为GB18030字符集，设置环境变量为GB18030后连接数据库
export DB_LOCALE=zh_CN.GB18030-2000
export CLIENT_LOCALE=zh_CN.GB18030-2000
dbaccess xxxdb -

#57372为UTF8字符集，设置环境变量为UTF8后连接数据库
export DB_LOCALE=zh_CN.UTF8
export CLIENT_LOCALE=zh_CN.UTF8
dbaccess xxxdb -
```