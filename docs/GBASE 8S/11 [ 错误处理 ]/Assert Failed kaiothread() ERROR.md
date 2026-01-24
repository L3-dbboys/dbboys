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

