错误信息
```
Message Log File: /opt/gbase/tmp/online.log

21:24:05 Affinitied Vp 10 to phys proc 3
21:24:05 Affinitied Vp 11 to phys proc 4
21:24:05 Affinitied VP 12 to phys proc 5
21:24:05 Affinitied VP 13 to phys proc 6
21:24:05 Affinitied Vp 14 to phys proc 7
21:24:05 Affinitied Vp 1 to phys proc 0
21:24:05 DR: DRAUTO is 3 (CMSM)
21:24:05 DR: ENCRYPT_HDR is 0 (HDR encryption Disabled)
21:24:05 CCFLAGS2 value set to 0x200
21:24:05 SOL_FEAT_CTRL value set to 0x8008
21:24:05 SOL_DEF_CTRL value set to 0x4b0
21:24:05 Event notification facility epoll enabled.
21:24:05 GBase Database Server Version 12.10.FC4G1AEE Software Serial Number AAA#B000000
21:24:05 The chunk '/data/gbasedbt/dbs/rootdbs' must have READ/WRITE permissions for owner (600).
21:24:05 oninit: Fatal error in shared memory initialization
```
错误原因
```
数据库安装用户错误，必须以root用户安装
```


解决方案：（选择一种）
```
1、以root用户重新安装
2、以root用户运行安装目录下的RUNasroot.installserver脚本更改权限
```
