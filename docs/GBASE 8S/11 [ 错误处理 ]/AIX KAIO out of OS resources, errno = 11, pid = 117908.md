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
