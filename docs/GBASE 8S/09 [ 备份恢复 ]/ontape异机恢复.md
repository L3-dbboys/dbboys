---
hide_title: false
sidebar_position: 3
---

安装数据库软件，无需创建或初始化实例
```
安装与备份机器版本一致的数据库软件
```
恢复环境变量
```
配置目标机器gbasedbt用户环境变量，与备份环境保持一致
```
恢复配置文件
```
将备份环境的$GBASEDBTDIR/etc/$ONCONFIG配置文件复制到恢复环境
将备份环境的$GBASEDBTSQLHOSTS配置文件复制到恢复环境
```
目标机器修改配置文件，将IP修改为当前机器IP
```
$GBASEDBTSQLHOSTS
```
复制备份数据
```
将备份服务器上的备份数据复制到目标服务器，如/backup/dbhost01_0_L0,重命名备份文件，使文件名命名符合“主机名_实例编号_L0”
```
创建数据文件
```
根据备份服务器onstat -d输出，在恢复环境创建对应的数据文件，如gbasedbt用户执行：
touch /data/gbase/rootdbschk001
chmod 660 /data/gbase/rootdbschk001
```
开始恢复
```
ontape -r
```
恢复参考输出
```
[gbasedbt@gbasehost01 ~]$ ontape -r
Restore will use level 0 archive file /opt/gbase/backup/gbasehost01_0_L0. Press Return to continue ...


Archive Tape Information

Tape type:      Archive Backup Tape 
Online version: GBase Database Server Version 12.10.FC4G1AEE 
Archive date:   Tue Jul 28 10:04:40 2020 
User id:        gbasedbt 
Terminal id:    /dev/pts/1 
Archive level:  0 
Tape device:    /opt/gbase/backup/ 
Tape blocksize (in k): 32 
Tape size (in k): system defined for directory 
Tape number in series: 1 

Spaces to restore:1 [rootdbs                              ] 
2 [plogdbs                  ] 
3 [llogdbs                   ] 
4 [datadbs01               ] 
5 [sbspace01               ] 

Archive Information

GBase Database Server Copyright 2001, 2018  General Data Corporation
Initialization Time       07/27/2020 14:57:35
System Page Size          2048
Version                   30
Index Page Logging        OFF
Archive CheckPoint Time   07/28/2020 10:04:40

Dbspaces
number   flags    fchunk   nchunks  flags    owner                            name
1        60001    1        1        N  BA    gbasedbt                         rootdbs                                                                                                                         
2        40001    2        1        N  BA    gbasedbt                         plogdbs                                                                                                                         
3        40001    3        1        N  BA    gbasedbt                         llogdbs                                                                                                                         
4        42001    4        1        N TBA    gbasedbt                         tempdbs01                                                                                                                       
5        68001    5        1        N SBA    gbasedbt                         sbspace01                                                                                                                       
6        60001    6        1        N  BA    gbasedbt                         datadbs01                                                                                                                       


Chunks
chk/dbs offset   size     free     bpages   flags pathname
1   1   0        512000   498315            PO-B- /data/gbase/rootchk
2   2   0        512000   11947             PO-B- /data/gbase/plogchk
3   3   0        512000   11947             PO-B- /data/gbase/llogchk
4   4   0        512000   511512            PO-B- /data/gbase/tempchk01
5   5   0        512000   25659             POSB- /data/gbase/sbspace01
6   6   0        512000   511576            PO-B- /data/gbase/datachk01

Continue restore? (y/n)y
Do you want to back up the logs? (y/n)y
Would you like to back up log 7? (y/n) y
Read/Write End Of Medium enabled: blocks = 2 

Please label this tape as number 1 in the log tape sequence. 

This tape contains the following logical logs: 
    7
Log salvage is complete, continuing restore of archive.
Restore a level 1 archive (y/n) n
Do you want to restore log tapes? (y/n)n #如要恢复日志输入y，如不要之恢复到备份时间点选n
/opt/gbase/bin/onmode -sy

Program over.
```
切换数据库状态
```
onmode -m
```