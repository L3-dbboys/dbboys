数据库处于【Blocked:OVERRIDE_DOWN_SPACE】状态，表示当前数据库已有部分空间不可用，一般为存储设备故障导致。

【onstat -】查看数据库状态类似如下输出：
```
On-Line (CKPT INP) -- Up 00:08:21 -- 1686980 Kbytes
Blocked:OVERRIDE_DOWN_SPACE
```
参考以下步骤处理：

1、【gbasedbt】用户执行以下命令检查状态异常的chunk：
```
onstat -d |grep PD
```
参考输出
```
4863e028         2      2      0          6250       0                     PD-B-- /data/gbase/datadbs01chk001
```
dd读取该文件，确认设备可读
```
dd if=/data/gbase/datadbs01chk001 of=/dev/null count=1
```
如设备无法读取，检查存储设备是否异常，在存储设备恢复正常后，执行【onspaces -s】将chunk状态更改为On
```
onspaces -s datadbs01 -p /data/gbase/datadbs01chk001 -o 0 -O -y
```
参考输出（失败，无法改变chunk状态）
```
Verifying physical disk space, please wait ...
Cannot change chunk status.
ISAM error:  illegal argument to ISAM function.
```

如onspaces无法改变chunk状态，存储恢复后，从备份恢复数据，或联系厂商支持更改chunk状态。