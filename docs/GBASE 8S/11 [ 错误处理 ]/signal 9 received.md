错误信息
```
[gbasedbt@dbhost1 ~]$ onstat -m
Fast Recovery (Prim) -- Up 00:00:19 -- 2267664 Kbytes
Generic System Block: -1 seconds

Message Log File: /opt/gbase/tmp/online.log
02/08/23 21:49:45  Physical Recovery Started at Page (1:184044).
02/08/23 21:49:46  Physical Recovery Complete: 3 Pages Examined, 3 Pages Restored.
02/08/23 21:49:47  Assert Failed: Unexpected virtual processor termination: pid = 47790, signal 9 received.
02/08/23 21:49:47  GBase Database Server Version 12.10.FC4G1AEE
02/08/23 21:49:47   Who: Session(1, gbasedbt@dbhost1, 0, 0x4609c028)
                Thread(7, main_loop(), 46055028, 1)
                File: mt.c Line: 15364
02/08/23 21:49:47  stack trace for pid 47789 written to /opt/gbase/tmp/af.3efa87b
02/08/23 21:49:47   See Also: /opt/gbase/tmp/af.3efa87b
02/08/23 21:49:51  Thread ID 7 will NOT be suspended because
          it is a daemon.
02/08/23 21:49:51   See Also: /opt/gbase/tmp/af.3efa87b
02/08/23 21:49:51  Starting crash time check of:
02/08/23 21:49:51  1. memory block headers
02/08/23 21:49:51  2. stacks
02/08/23 21:49:51  Crash time checking found no problems
02/08/23 21:49:51  Unexpected virtual processor termination: pid = 47790, signal 9 received.
02/08/23 21:49:51  Dataskip is now OFF for all dbspaces
02/08/23 21:49:51  Restartable Restore has been ENABLED
02/08/23 21:49:51  Recovery Mode
```
查看系统日志：/var/log/message
```
Feb  8 21:49:47 dbhost1 kernel: [47795]   500 47795   612244      566      34      478             0 oninit
Feb  8 21:49:47 dbhost1 kernel: [47796]   500 47796   613783     2491      61      473             0 oninit
Feb  8 21:49:47 dbhost1 kernel: [47797]   500 47797   613783     5616      72      472             0 oninit
Feb  8 21:49:47 dbhost1 kernel: [47798]   500 47798   613783     4496      69      472             0 oninit
Feb  8 21:49:47 dbhost1 kernel: [47799]   500 47799   612244      755      42      477             0 oninit
Feb  8 21:49:47 dbhost1 kernel: Out of memory: Kill process 47789 (oninit) score 378 or sacrifice child
Feb  8 21:49:47 dbhost1 kernel: Killed process 47790 (oninit) total-vm:2448976kB, anon-rss:1880kB, file-rss:312kB, shmem-rss:292kB
Feb  8 21:50:01 dbhost1 systemd: Started Session 427 of user root.
```
错误原因
```
系统内存管理OOM在系统内存不足时，kill了数据库进程。
```
解决方案
```
调整数据库内存参数减少数据库内存占用，或增加系统内存。
```