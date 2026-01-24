hac备机切换为主宕机，报以下错误：
```
11/20/24 09:34:52  Checkpoint Completed:  duration was 0 seconds.
11/20/24 09:34:52  Wed Nov 20 - loguniq 68, logpos 0x2657a018, timestamp: 0x3d674f59 Interval: 135891

11/20/24 09:34:52  Maximum server connections 0 
11/20/24 09:34:52  Checkpoint Statistics - Avg. Txn Block Time 0.000, # Txns blocked 0, Plog used 137, Llog used 0

11/20/24 09:36:52  Checkpoint Completed:  duration was 0 seconds.
11/20/24 09:36:52  Wed Nov 20 - loguniq 68, logpos 0x265e4018, timestamp: 0x3d67a3d7 Interval: 135892

11/20/24 09:36:52  Maximum server connections 0 
11/20/24 09:36:52  Checkpoint Statistics - Avg. Txn Block Time 0.000, # Txns blocked 0, Plog used 152, Llog used 0

11/20/24 09:38:52  Checkpoint Completed:  duration was 0 seconds.
11/20/24 09:38:52  Wed Nov 20 - loguniq 68, logpos 0x26625018, timestamp: 0x3d67a6bb Interval: 135893

11/20/24 09:38:52  Maximum server connections 0 
11/20/24 09:38:52  Checkpoint Statistics - Avg. Txn Block Time 0.000, # Txns blocked 0, Plog used 68, Llog used 0

11/20/24 09:40:52  Checkpoint Completed:  duration was 0 seconds.
11/20/24 09:40:52  Wed Nov 20 - loguniq 68, logpos 0x2665a018, timestamp: 0x3d67a9ac Interval: 135894

11/20/24 09:40:52  Maximum server connections 0 
11/20/24 09:40:52  Checkpoint Statistics - Avg. Txn Block Time 0.000, # Txns blocked 0, Plog used 105, Llog used 0

11/20/24 09:41:13  Checkpoint Completed:  duration was 0 seconds.
11/20/24 09:41:13  Wed Nov 20 - loguniq 68, logpos 0x26661018, timestamp: 0x3d67aa0c Interval: 135895

11/20/24 09:41:13  Maximum server connections 0 
11/20/24 09:41:13  Checkpoint Statistics - Avg. Txn Block Time 0.000, # Txns blocked 0, Plog used 60, Llog used 0

11/20/24 09:41:15  DR: Receive error
11/20/24 09:41:15  SMX thread is exiting
11/20/24 09:41:15  dr_secrcv thread : asfcode = -25582: oserr = 0: errstr = : Network connection is broken.

11/20/24 09:41:15  DR_ERR set to -1
11/20/24 09:41:15  DR: Turned off on secondary server
11/20/24 09:41:15  Skipping failover callback.
11/20/24 09:41:26  Defragmenter cleaner thread now running
11/20/24 09:41:26  Defragmenter cleaner thread cleaned:0 partitions
11/20/24 09:41:26  Logical Recovery has reached the transaction cleanup phase.
11/20/24 09:41:26  Checkpoint Completed:  duration was 0 seconds.
11/20/24 09:41:26  Wed Nov 20 - loguniq 68, logpos 0x26663018, timestamp: 0x3d67aa1d Interval: 135896

11/20/24 09:41:26  Maximum server connections 0 
11/20/24 09:41:26  Checkpoint Statistics - Avg. Txn Block Time 0.000, # Txns blocked 0, Plog used 22, Llog used 1

11/20/24 09:41:26  Logical Recovery Complete.
	  1537683 Committed, 1 Rolled Back, 0 Open, 0 Bad Locks

11/20/24 09:41:26  Logical Recovery Complete.
11/20/24 09:41:27  Quiescent Mode
11/20/24 09:41:27  Checkpoint Completed:  duration was 0 seconds.
11/20/24 09:41:27  Wed Nov 20 - loguniq 68, logpos 0x26665018, timestamp: 0x3d67aa51 Interval: 135897

11/20/24 09:41:27  Maximum server connections 0 
11/20/24 09:41:27  Checkpoint Statistics - Avg. Txn Block Time 0.000, # Txns blocked 0, Plog used 10, Llog used 2

11/20/24 09:41:27  B-tree scanners enabled.
11/20/24 09:41:27  DR: Reservation of the last logical log for log backup turned on
11/20/24 09:41:27  SCHAPI: dbScheduler/dbWorker are getting created in another thread
11/20/24 09:41:27  DR: new type = standard
11/20/24 09:41:27  DR: new type = primary, secondary server name = gbaseserver1 
11/20/24 09:41:27  DR: Trying to connect to secondary server = gbaseserver1
11/20/24 09:41:27  Starting BldNotification
11/20/24 09:41:27  DR: Cannot connect to secondary server
11/20/24 09:41:27  DR: Turned off on primary server
11/20/24 09:41:27  Starting BldNotification
11/20/24 09:41:27  On-Line Mode
11/20/24 09:41:28  Assert Failed: No Exception Handler
11/20/24 09:41:28  GBase Database Server Version 12.10.FC4G1AEE
11/20/24 09:41:28   Who: Session(64, gbasedbt@PSASBAF02, 0, 0xf3c111f8)
		Thread(168, build_smi, f3be2ee8, 1)
		File: mtex.c Line: 512
11/20/24 09:41:28   Results: Exception Caught. Type: MT_EX_OS, Context: mem
11/20/24 09:41:28   Action: Please notify GBASE Techical Support.
11/20/24 09:41:28   See Also: /opt/GBASE/gbase/tmp/af.4903e47
11/20/24 09:41:36  Thread ID 168 will NOT be suspended because
          it is deemed too critical to the server.
11/20/24 09:41:36   See Also: /opt/GBASE/gbase/tmp/af.4903e47
11/20/24 09:41:36  Starting crash time check of:
11/20/24 09:41:36  1. memory block headers
11/20/24 09:41:36  2. stacks
11/20/24 09:41:36  Found bad memory block; address:fb5934e8
11/20/24 09:41:36  No Exception Handler
11/20/24 09:41:36  Exception Caught. Type: MT_EX_OS, Context: mem
11/20/24 09:41:36  The Master Daemon Died
11/20/24 09:41:36  The Master Daemon Died
11/20/24 09:41:36  The Master Daemon Died
11/20/24 09:41:36  Fatal error in ADM VP at mt.c:15085
11/20/24 09:41:36  Unexpected virtual processor termination: pid = 3910745, exit status = 0x1.
11/20/24 09:41:36  PANIC: Attempting to bring system down
```
错误原因
```
版本缺陷
```

解决方案
```
#创建upgraded文件或升级新版本(3.6.x以上)
touch $GBASEDBTDIR/etc/upgraded  #主备均执行
```
