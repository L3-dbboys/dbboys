文档适用场景：HAC集群中某一节点宕机，DRAUTO参数为0，1，2，3任意值。

【HAC集群】有且只有两个节点，巡检【[实例状态检查](../07-监控巡检/实例状态检查.md)】过程中，如集群中某节点因掉电等原因出现宕机，参考以下两个场景流程处理：

### 场景1：未宕机节点节点状态为【On-Line】
如【onstat -】显示未宕机的节点状态为【On-Line】，表明数据库宕机的节点为备节点，或主节点宕机且备节点已切换为主节点，参考以下流程处理：

1、启动宕机节点到最后一个检查点，在宕机节点使用【gbasedbt】用户操作：
```
oninit -PHY -v
```
示例输出：（最后显示【mode = 2】）
```
Reading configuration file '/opt/gbase8s33.0_CH4/etc/onconfig.gbase03'...succeeded
Creating /GBASEDBTTMP/.infxdirs...succeeded
Allocating and attaching to shared memory...succeeded
Creating resident pool 15446 kbytes...succeeded
Creating infos file "/opt/gbase8s33.0_CH4/etc/.infos.gbase03"...succeeded
Linking conf file "/opt/gbase8s33.0_CH4/etc/.conf.gbase03"...succeeded
Initializing rhead structure...rhlock_t 32768 (1024K)... rlock_t (13281K)... Writing to infos file...succeeded
Initialization of Encryption...succeeded
Initializing ASF...succeeded
Initializing Dictionary Cache and SPL Routine Cache...succeeded
Bringing up ADM VP...succeeded
Creating VP classes...succeeded
Forking main_loop thread...succeeded
Initializing DR structures...succeeded
Forking 1 'soctcp' listener threads...succeeded
Starting tracing...succeeded
Initializing 32 flushers...succeeded
Initializing log/checkpoint information...succeeded
Initializing dbspaces...succeeded
Opening primary chunks...succeeded
Validating chunks...succeeded
Initialize Async Log Flusher...succeeded
Starting B-tree Scanner...succeeded
Init ReadAhead Daemon...succeeded
Init DB Util Daemon...succeeded
Initializing DBSPACETEMP list...succeeded
Init Auto Tuning Daemon...succeeded
Initializing dataskip structure...succeeded
Updating Global Row Counter...succeeded
Forking onmode_mon thread...succeeded
Creating periodic thread...succeeded
Creating periodic thread...succeeded
Verbose output complete: mode = 2
```
2、设置宕机节点为备节点，在宕机节点使用【gbasedbt】用户操作：（实例名可通过【env |grep GBASEDBTSERVER】查看）
```
onmode -d secondary <未宕机节点实例名>
```
3、设置未机节点为主节点，在未宕机节点使用【gbasedbt】用户操作：（实例名可通过【env |grep GBASEDBTSERVER】查看）
```
onmode -d primary <宕机节点实例名>
```
【onstat -m】数据库日志示例输出：
```
10:44:23  DR: Primary server connected
10:44:23  DR: Secondary server needs failure recovery

10:44:25  DR: Sending log 33 (current), size 50000 pages, 0.27 percent used
10:44:26  DR: Sending Logical Logs Completed
10:44:27  DR: Primary server operational
```
4、未宕机节点检查集群状态，【gbasedbt】用户执行：
```
onstat -g cluster
```
示例输出：(【Status】显示【Connected,On】则正常）
```
On-Line (Prim) -- Up 00:23:08 -- 1686980 Kbytes

Primary Server:gbase01
Current Log Page:33,138
Index page logging status: Disabled


Server  ACKed Log    Applied Log  Supports     Status
        (log, page)  (log, page)  Updates
gbase03 33,138       33,138       No           ASYNC(HDR),Connected,On
```
### 场景2：未宕机节点节点状态为【Read-Only】
如【onstat -】显示未宕机的节点状态为【Read-Only】，表明数据库宕机的为主节点，且未发生主备节点切换，参考以下流程处理：

1、启动宕机节点，在宕机节点使用【gbasedbt】用户操作：
```
oninit -v
```
2、宕机节点检查集群状态，【gbasedbt】用户执行：
```
onstat -g cluster
```
示例输出：(【Status】显示【Connected,On】则正常）
```
On-Line (Prim) -- Up 00:23:08 -- 1686980 Kbytes

Primary Server:gbase01
Current Log Page:33,138
Index page logging status: Disabled


Server  ACKed Log    Applied Log  Supports     Status
        (log, page)  (log, page)  Updates
gbase03 33,138       33,138       No           ASYNC(HDR),Connected,On
```