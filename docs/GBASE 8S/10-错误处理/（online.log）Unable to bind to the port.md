错误信息
```
13:42:47  Unable to bind to the port (9088) on the host (192.168.17.105) for the server (gbase01).
```
错误原因
```
数据库使用端口9088被占用，或监听IP不是本机IP
```


解决方案
```
1、检查端口是否被占用【netstat -an |grep 9088】
 
2、检查监听IP是否为本机IP【ip a】
```
