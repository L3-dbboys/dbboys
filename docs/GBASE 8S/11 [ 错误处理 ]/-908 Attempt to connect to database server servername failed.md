---
hide_title: false
sidebar_position: 908
---
错误信息
```
-908    Attempt to connect to database server (servername) failed.
-908    试图与数据库服务器（服务器名）连接失败。
```

错误原因
```
程序无法连接到数据库
```

解决方案
```
1、检查IP，端口信息是否填写错误
2、检查客户端与数据库之间网络是否畅通【从客户端ping 数据库服务器IP】
3、检查数据库服务器防火墙是否关闭【servie firewalld status】如是active，关闭
4、检查数据库监听是否正常 【onstat -g ntt |grep soctcplst】  #有输出正常
```

