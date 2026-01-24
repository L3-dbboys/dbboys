ifxclone用于在备机执行克隆一个实例，操作步骤如下：
```
#主节点：
echo "dbhost02" >/etc/hosts.equiv
onmode -wf ENABLE_SNAPSHOT_COPY=1
#配置好sqlhosts文件
ifx01   onsoctcp        192.168.17.120  9088
ifx02   onsoctcp        192.168.17.121  9088

#备节点：
echo "dbhost01" >/etc/hosts.equiv
export ONCONFIG=onconfig
sed -i 's/FULL_DISK_INIT.*/FULL_DISK_INIT 1/g' $GBASEDBTDIR/etc/$ONCONFIG
#配置sqlhosts文件
ifx01   onsoctcp        192.168.17.120  9088
ifx02   onsoctcp        192.168.17.121  9088
```
恢复备机为RSS
```
ifxclone -S ifx01 -I 192.168.17.120 -P 9088 -t ifx02 -i 192.168.17.121 -p 9088 -d RSS -L -T
```
执行输出
```
[gbasedbt@dbhost02 etc]$ ifxclone -S ifx01 -I 192.168.17.120 -P 9088 -t ifx02 -i 192.168.17.121 -p 9088 -d RSS -L -T
A server 'ifx02' already exists in the cluster but is disconnected from the 
primary server.
Do you want to delete the pre-existing server record and then 
proceed with cloning? (y or n) ->y
The record of RSS server 'ifx02' is being deleted.
Restoring clone server ifx02 from source server ifx01.
Look at online log for status of clone server...
```
恢复为独立单机实例
```
ifxclone -S ifx01 -I 192.168.17.120 -P 9088 -t ifx02 -i 192.168.17.121 -p 9088  -L -T
```
输出
```
Restoring clone server ifx02 from source server ifx01.
Look at online log for status of clone server...
```

源库必须参数，可动态调整
```
ENABLE_SNAPSHOT_COPY 1
```
否则报错:
```
Onconfig variable 'ENABLE_SNAPSHOT_COPY' is not set at the source server 'ifx01'.
```
ifxclone执行的服务器必须配置ONCONFIG环境变量，否则报错
```
ERROR - ONCONFIG not set
```
源端sqlhosts文件已包含目标端实例信息，可忽略
```
Server name (ifx02) already exists in the SQLHOSTS file. 
```
目标库FULL_DISK_INIT必须设置为1，否则报错
```
10:36:19  DISK INITIALIZATION ABORTED: potential instance overwrite detected.
To disable this check, set FULL_DISK_INIT to 1 in your config file and retry.
```
不指定-L参数，默认从源复制配置文件，会出现以下错误
```
11:01:31  DISK INITIALIZATION ABORTED: potential instance overwrite detected.
To disable this check, set FULL_DISK_INIT to 1 in your config file and retry.
```
添加信任，否则报错
```
11:04:00  SMX failed to create pipes for clone restore.
11:04:00  Snapshot instantiation failed, killing myself.
```
克隆命令：(备机执行，执行完成后备机恢复为RSS)
```
ifxclone -S ifx01 -I 192.168.17.120 -P 9088 -t ifx02 -i 192.168.17.121 -p 9088 -d RSS -L -T
```