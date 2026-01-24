错误信息
```
20:55:32  GBase 8t Database server started.
20:55:32  Requested shared memory segment size rounded from 4308KB to 4792KB
20:55:32  shmat: [EINVAL][22]:shared memory base address illegal
20:55:32  Contiguous shared memory segment allocation failed at 0xc00000005e800000.
Allocation successful at oxffffffffffffffff.
Check SHMBASE is consistent with the value in $GBASEDBTDIR/etc/onconfig.std.
If you are using the correct SHMBAsE value in your $ONCONFIG file, then
consider this message informational only.
20:55:32  mt_shm_init: can't create virtual seqment

20:55:32  shmctl: errno = 22
```
错误原因
```
HP内存参数不足
```

解决方案:调整内存参数  
【root】用户执行：
```
kctune -h shmmax="4398046511104"
#reboot生效
```
