ODBC连接时使用USER和PASSWORD关键字报No connect permission. sqlerrm(sysusers)  
GO使用ODBC访问数据库时，字符串
```
DRIVER=/opt/gbase8s-odbc-driver/lib/cli/iclis09b.so;SERVER=gbase01;DATABASE=testdb;DB_LOCALE=zh_CN.utf8;CLIENT_LOCALE=zh_CN.utf8;USER=gbasedbt;PASSWORD=GBase123
```

时，报无连接权限错误  
```
SQLDriverConnect: {HY000} [GBasedbt][GBasedbt ODBC Driver][GBasedbt]No connect permission. sqlerrm(sysusers)
{01S00} [GBasedbt][GBasedbt ODBC Driver]Invalid connection string attribute.
{01S00} [GBasedbt][GBasedbt ODBC Driver]Invalid connection string attribute.
```

在《GBase 8s V8.8 ODBC Driver 程序员指南》里，数据库并没有使用USER/PASSWORD这两个关键字（虽然这两个关键字在DSN中可用）  
因此，需要修改为(USER->UID,PASSWORD->PWD)：
```
DRIVER=/opt/gbase8s-odbc-driver/lib/cli/iclis09b.so;SERVER=gbase01;DATABASE=testdb;DB_LOCALE=zh_CN.utf8;CLIENT_LOCALE=zh_CN.utf8;UID=gbasedbt;PWD=GBase123
```

或者 DSN（两种都支持）的写法  
```
DSN=testdb;USER=gbasedbt;PASSWORD=GBase123
```
或者
```
DSN=testdb;UID=gbasedbt;PWD=GBase123
```