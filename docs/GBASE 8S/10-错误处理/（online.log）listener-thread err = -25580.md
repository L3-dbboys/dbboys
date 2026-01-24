错误信息：
```
15:20:06  listener-thread: err = -25580: oserr = 107: errstr = : System error occurred in network function. 
 System error = 107.
16:20:06  listener-thread: err = -25580: oserr = 107: errstr = : System error occurred in network function. 
 System error = 107.
17:20:06  listener-thread: err = -25580: oserr = 107: errstr = : System error occurred in network function. 
 System error = 107.
```

错误原因
```
Python或其他程序定时通过数据库端口建立tcp连接，监控端口可用性。
```

解决方案
```
停止程序定时建立tcp连接，可使用tcpdump排查报错时刻哪台服务器在创建tcp连接。
```

