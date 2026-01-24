文档适用场景：HAC集群两节点均宕机，DRAUTO参数为0，1，2，3任意值。

【HAC集群】有且只有两个节点，巡检【[实例状态检查](/docs/08%20%5B%20监控巡检%20%5D/日常巡检/实例状态检查)】过程中，如集群中两个节点均因掉电等原因出现宕机，参考以下流程处理：

### 1、检查数据库日志，根据日志的最后时间确认最后宕机节点
分别在两个节点使用【gbasedt】用户执行：
```
onstat -m
```
如输出日志中看不出最后日期，检查输出第三行的日志文件，如以下文件,确认数据库宕机日期及时间：
```
Message Log File: /opt/gbase/tmp/online.log
```

### 2、启动最后一个宕机的数据库节点
根据第一步检查结果，启动最后宕机的数据库节点。
【gbasedbt】用户执行：
```
oninit -v
```
示例输出：
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
Verbose output complete: mode = 5
```
如最后显示【mode=5】执行第3步，如最后显示【mode=2】执行第4步。

### 3、第二步显示【mode = 5】
如第二步最后输出为【mode = 5】，则表示集群主节点已启动，按以下步骤启动最先宕机的节点为备节点。
最先宕机节点【gbasedbt】用户执行：
```
oninit -PHY -v
```
执行完成最后显示【mode = 2】，执行以下命令设置该节点为集群备机：（实例名可通过【env |grep GBASEDBTSERVER】查看）
```
onmode -d secondary <最后宕机节点实例名>
```
最后宕机节点使用【gbasedbt】用户操作：（实例名可通过【env |grep GBASEDBTSERVER】查看）
```
onmode -d primary <最先宕机节点实例名>
```
### 4、第二步显示【mode = 2】
如第二步最后输出为【mode = 2】，则表示集群备节点已启动，按以下步骤启动最先宕机的节点为主节点。
最先宕机节点【gbasedbt】用户执行：
```
oninit -v
```
执行完成最后显示【mode = 5】则正常。

### 5、检查集群状态
【gbasedbt】用户在任一节点执行：
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