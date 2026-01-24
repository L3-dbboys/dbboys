错误日志：
```
23:35:22 dynamically alocated 1000000 locks
23:35:42 dynamically alocated 1000000 locks
23:36:08 dynamically alocated 1000000 locks
......
03:23:34 Logical Log 21695 omplete, timestamp: 0xe8e6c5c1
03:23:54 Lock table overflow - user id 1005.session id 90
03:23:54 Lock table overflow - user id 1005.session id 103
03:23:54 Lock table overflow - user id 1005.session id 68
03:23:54 Lock table overflow - user id 1005.session id 40
03:23:54 Lock table overflow - user id 1005.session id 103
03:23:54 SCHAPl: [post_alarm_message 19-1837178] Error -243 Could not position within a table (informix.ph_task)
03:23:54 SCHAPI: [post_alarm_message 19-1837178] Error -134 ISAM error: no more locks
```
错误原因
```
锁已动态分配99次，每次1000000，不能再分配锁，锁资源溢出，数据库DML不可用
```

解决方案
```
kill占用大量锁的会话，通过【onstat -u】输出的【locks】列查看，或【onstat -x】的【locks】列查看，大表需要分批操作
```
