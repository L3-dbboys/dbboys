执行以下命令检查实例状态：
```
onstat -
```
数据库应处于【On-Line】或【Read-Only】状态，且第二行无【Blocked: reason】输出，则正常。
如以下输出表示数据库正常运行：
```
# 低版本输出（单机或集群主节点）
GBase Database Server Version 12.10.FC4G1AEE -- On-Line -- Up 00:11:54 -- 833360 Kbytes
# 高版本输出（单机或集群主节点）
On-Line -- Up 7 days 00:41:09 -- 1686980 Kbytes
# SSC共享磁盘集群备机
Read-Only (SDS) -- Up 7 days 00:41:09 -- 1686980 Kbytes
# HAC同城主备集群备机
Read-Only (Sec) -- Up 7 days 00:41:09 -- 1686980 Kbytes
# RHAC异地主备集群备机
Read-Only (RSS) -- Up 7 days 00:41:09 -- 1686980 Kbytes
```
第二行有【Blocked: reason】输出，表示数据库被Blocked，需根据Blocked原因采取措施。
如以下输出，表示数据库日志没有及时归档，已无可用日志，需及时归档日志：
```
On-Line -- Up 7 days 00:41:09 -- 1686980 Kbytes
Blocked:LAST_LOG_RESERVED4BACKUP
```
如出现以下输出，则该数据库实例未启动或已关闭，参考【[数据库异常宕机](../09-应急处理/数据库异常宕机.md)】启动，其中【gbase01】是实例名：
```
shared memory not initialized for GBASEDBTSERVER 'gbase01'
```
【onstat -】命令输出格式解释如下：（[]中内容根据当时实例状态可能显示或不显示）
```
[Version--]Mode [(Type)]--[(Checkpnt)]--Up Uptime--Sh_mem Kbytes
[Blocked: reason][other]
```
|输出|说明|
|----|----|
|Version|产品版本，高版本不再显示|
|Mode|当前数据库运行模式，可能包括以下几种模式|
|Mode|【On-Line】数据库单机或集群主节点正常运行模式，数据库可用|
|Mode|【Read-Only】数据库集群备机正常运行模式，支持只读|
|Mode|【Initialization】实例正在启动，数据库不可用|
|Mode|【Shutting Down】实例正在关闭，数据库不可用|
|Mode|【Quiescent】静默模式，数据库不可连接，常见于恢复完成后状态，数据库不可用|
|Mode|【Fast Recovery】物理恢复模式，数据库不可用，常见于集群搭建或物理恢复完成|
|Mode|【Single-User】管理员单用户模式，仅超级管理员可用，常见于某些管理操作|
|(Type)|单机，【不显示】|
|(Type)|SSC共享磁盘主节点，【不显示】|
|(Type)|HAC集群主节点，显示【(Prim)】|
|(Type)|RHAC集群主节点【不显示】|
|(Type)|SSC共享磁盘备节点，显示【(SDS)】|
|(Type)|HAC集群备节点，显示【(Sec)】|
|(Type)|RHAC集群备节点，显示【(RSS)】|
|(Checkpnt)|【不显示】表示当前未处于检查点执行期间|
|(Checkpnt)|【(CKPT REQ)】数据库服务器需要执行检查点但未开始执行，可能会影响业务|
|(Checkpnt)|【(CKPT INP)】数据库服务器正在执行检查点，不影响数据库正常运行|
|(Checkpnt)|【(LONGTX)】数据库发生长事务，正在回滚，不影响数据库正常运行，等待回滚完成即可|
|Uptime|数据库运行时间|
|Sh_mem|数据库分配内存总大小，单位为KB|
|reason|【CKPT】被检查点阻塞，参考【[Blocked:CKPT](../09-应急处理/Blocked%20CKPT.md)】处理|
|reason|【LONGTX】长事务阻塞数据库，参考【[Blocked:LONGTX](../09-应急处理/Blocked%20LONGTX.md)】处理|
|reason|【ARCHIVE】备份阻塞数据库，参考【[Blocked:ARCHIVE](../09-应急处理/Blocked%20ARCHIVE.md)】处理|
|reason|【LAST_LOG_RESERVED4BACKUP】日志没及时归档，参考【[Blocked:LAST_LOG_RESERVED4BACKUP](../09-应急处理/Blocked%20LAST_LOG_RESERVED4BACKUP.md)】处理|
|reason|【OVERRIDE_DOWN_SPACE】数据库空间状态OFFLINE，参考【[Blocked:OVERRIDE_DOWN_SPACE](../09-应急处理/Blocked%20OVERRIDE_DOWN_SPACE.md)】处理|
|reason|【HA_CONV_STD】数据库集群切换执行中，等待完成即可，如长时间未完成，及时联系厂商支持|
|other|【Generic system Block: -1 seconds】数据库异常被阻塞，【[重启数据库](../09-应急处理/重启数据库.md)】或联系厂商支持|

更多【onstat】命令参考及输出说明，参考【[GBase 8s 管理员参考.pdf](https://www.dbboys.com/dl/gbase8s/docs/Administrator_Reference.pdf)】。


