数据库处于【Blocked:LAST_LOG_RESERVED4BACKUP】状态，表示数据库日志已无可用日志，需要及时归档日志。可能是业务日志写入速度快于日志备份速度，也可能是数据库未配置日志自动备份。

【onstat -】命令查看数据库状态类似以下输出：
```
On-Line -- Up 00:08:21 -- 1686980 Kbytes
Blocked:LAST_LOG_RESERVED4BACKUP
```

如需快速恢复业务，可将【LTAPEDEV】参数置空，所有日志标记为已备份，可重复使用。
使用操作系统【gbasedbt】用户执行以下命令：
```
onmode -wf LTAPEDEV=/dev/null
```
注意：LTAPEDEV参数设置为/dev/null会导致已有日志备份不连续，请确认后操作


执行完成后，所有日志标记为已备份，可使用【onstat -l】查看日志状态。

对于生产系统，建议配置日志自动备份，可参考【ontape自动备份日志】和【onbar自动备份日志】