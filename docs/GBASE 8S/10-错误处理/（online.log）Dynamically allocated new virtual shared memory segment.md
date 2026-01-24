错误信息
```
12:05:32 Dynamically allocated new virtual shared memory segment (size 2560000KB)
12:05:32 Memory sizes:resident:13706024 KB, virtual:111007308 KB，no SHMTOTAL limit
```

错误原因
```
动态分配锁、连接数量激增、内存泄漏等会导致数据库虚拟内存段动态增加
```

解决方案
```
【onstat -g seg】观察数据库内存段是否持续增长，如持续增长可能出现内存泄漏，【onstat -g ses】查看是否存在占用内存极大的session（如100MB以上），或【onstat -g mem】查看内存占用情况，根据实际情况处理。
```
