### LINUX运行过程中资源不足
错误信息
```
21:31:10  Assert Failed: out of KAIO resources
```

错误原因
```
KAIO资源不足，此错误不影响业务正常运行，会重试
```

解决方案
```
echo "fs.aio-max-nr = 10485760">>/etc/sysctl.conf
sysctl -p
```



### LINUX aio-max-nr内核参数过大启动失败
```
15:10:39  Event alarms enabled.  ALARMPROG = '/opt/gbase/etc/log_full.sh'
15:10:39  Booting Language <c> from module <>
15:10:39  Loading Module <CNULL>
15:10:39  Booting Language <builtin> from module <>
15:10:39  Loading Module <BUILTINNULL>
15:10:44  DR: DRAUTO is 3 (CMSM)
15:10:44  DR: ENCRYPT_HDR is 0 (HDR encryption Disabled)
15:10:44  Event notification facility epoll enabled.
15:10:44  Trusted host cache successfully built:/etc/hosts.equiv.
15:10:44  CCFLAGS2 value set to 0x200
15:10:44  SQL_FEAT_CTRL value set to 0x8008
15:10:44  SQL_DEF_CTRL value set to 0x4b0
15:10:44  GBase Database Server Version 12.10.FC4G1AEE Software Serial Number AAA#B000000
15:10:44  Assert Failed: initializing KAIO failed
15:10:44  GBase Database Server Version 12.10.FC4G1AEE
15:10:44   Who: Session(6, gbasedbt@, 0, (nil))
                Thread(43, kaio, 0, 8)
                File: kaioapi.c Line: 279
15:10:44   Results: io_queue_init(10000000) failed returning -22, errno = 0
15:10:44  stack trace for pid 28036 written to /opt/gbase/tmp/af.413ca74
15:10:44   See Also: /opt/gbase/tmp/af.413ca74
15:10:48  initializing KAIO failed
15:10:48  io_queue_init(10000000) failed returning -22, errno = 0
15:10:48  Assert Failed: initializing KAIO failed
15:10:48  GBase Database Server Version 12.10.FC4G1AEE
15:10:48   Who: Session(1, gbasedbt@dbhost1, 0, (nil))
                Thread(44, kaio, 0, 10)
                File: kaioapi.c Line: 279
15:10:48   Results: io_queue_init(10000000) failed returning -22, errno = 0
15:10:48  stack trace for pid 28038 written to /opt/gbase/tmp/af.414ca78
15:10:49  Assert Failed: kaiothread() ERROR
15:10:49  GBase Database Server Version 12.10.FC4G1AEE
15:10:49   Who: Thread(43, kaio, 0, 8)
                File: kaio.c Line: 2259
```
错误原因
```
fs.aio-max-nr内核参数配置过大
```
如：
```
[gbasedbt@dbhost1 ~]$ cat /etc/sysctl.conf 
# sysctl settings are defined through files in
# /usr/lib/sysctl.d/, /run/sysctl.d/, and /etc/sysctl.d/.
#
# Vendors settings live in /usr/lib/sysctl.d/.
# To override a whole file, create a new file with the same in
# /etc/sysctl.d/ and put new settings there. To override
# only specific settings, add a file with a lexically later
# name in /etc/sysctl.d/ and put new settings there.
#
# For more information, see sysctl.conf(5) and sysctl.d(5).
kernel.shmmax=4398046511104  
kernel.shmall=4294967296  
kernel.shmmin=4096  
kernel.sem=250 32000  32 128 
#初始化KAIO失败，如80000000，减小该值或保持默认1048576
fs.aio-max-nr=80000000  
```
KAIO当前有效值
```
cat /proc/sys/fs/aio-max-nr
```
### LINUX未开启KAIOON=1启动失败
错误信息
```
06:14:19  Requested shared memory segment size rounded from 110629KB to 110632KB
06:14:19  Successfully added a bufferpool of page size 2K.

06:14:19  Event alarms enabled.  ALARMPROG = '/opt/g5/etc/alarmprogram.sh'
06:14:19  Booting Language <c> from module <>
06:14:19  Loading Module <CNULL>
06:14:19  Booting Language <builtin> from module <>
06:14:19  Loading Module <BUILTINNULL>
06:14:24  DR: DRAUTO is 0 (Off)
06:14:24  DR: ENCRYPT_HDR is 0 (HDR encryption Disabled)
"/opt/g5/tmp/online.log" 89L, 4707C                                                                                45,1          22%
06:14:27  invoke_alarm(): mt_exec failed, status -1, errno 0
06:14:27  io_queue_init(52428800) failed returning -22, errno = 0
06:14:27  invoke_alarm(): /bin/sh -c '/opt/g5/etc/alarmprogram.sh 5 6 "Internal Subsystem failure: 'AIO'" "kaiothread() ERROR" "" 6075'
06:14:27  invoke_alarm(): mt_exec failed, status 32256, errno 0
06:14:27  Assert Failed: kaiothread() ERROR
06:14:27  GBase 8s Database Server Version 12.10.FC4G1AEE
06:14:27   Who: Thread(18, kaio, 0, 1)
                File: kaio.c Line: 2192
06:14:27  stack trace for pid 24493 written to /opt/g5/tmp/af.3fad980
06:14:27   See Also: /opt/g5/tmp/af.3fad980
06:14:28  Thread ID 18 will NOT be suspended because
          it is a daemon.
06:14:28   See Also: /opt/g5/tmp/af.3fad980
06:14:28  Starting crash time check of:
06:14:28  1. memory block headers
06:14:28  2. stacks
06:14:28  Crash time checking found no problems
06:14:28  kaiothread() ERROR
06:14:28  invoke_alarm(): /bin/sh -c '/opt/g5/etc/alarmprogram.sh 5 6 "Internal Subsystem failure: 'MT'" "kaiothread() ERROR" "" 6500'
06:14:28  invoke_alarm(): mt_exec failed, status 32256, errno 0
06:18:19  GBase 8s Database Server Started.
06:18:19  Requested shared memory segment size rounded from 4308KB to 4788KB
06:18:19  shmget: [EEXIST][17]: key 52564801: shared memory already exists
06:18:19  mt_shm_init: can't create resident segment

06:18:25  The Master Daemon Died
06:18:25  invoke_alarm(): /bin/sh -c '/opt/g5/etc/alarmprogram.sh 5 6 "Internal Subsystem failure: 'MT'" "The Master Daemon Died" "" 6069'
06:18:25  invoke_alarm(): mt_exec failed, status 32256, errno 0
06:18:25  PANIC: Attempting to bring system down
```
错误原因
```
未开启KAIO
```

解决方案
```
export KAIOON=1
```

### AIX运行过程中资源不足
错误信息
```
KAIO: out of OS resources, errno = 11, pid = 117908
```
错误原因
```
AIX系统KAIO资源不足
```

解决方案:按以下步骤操作消除报错，或不处理忽略该消息，不影响使用。  

【gbasedbt】用户调整环境变量
```
export IFMX_AIXKAIO_NUM_REQ=8192
```
【root】用户调整系统内核不小于8192(默认4096)
```
1. From the command line type:
$ smitty chgaio

2. From the menu select:
MAXIMUM number of REQUESTS

3. Change the value for this option to 12288 then press Enter

4. Press the F10 key to exit SMIT

5. Reboot the UNIX system
```

扩展说明
```
Indeed, when you start your engine, GBase engine allocates a number of AIOCB structure (AIOCB = asynchronous I/O control block) specified by IFMX_AIXKAIO_NUM_REQ.
The AIOCB is an Operating System structure used by Informix to manage several parameters of your asynchronous I/O requests (file descriptor, buffer location, file offset, length of your request ...) and to receive completion status information for asynchronous I/O requests.??
GBase does not increase dynamically the number of AIOCB when all AIOCB blocks are used at the same time, informix must then wait until an AIOCB is free to be reused for a new asynchronous I/O request.
The default value is not enough for your current asynchronous I/O requests activity
```
