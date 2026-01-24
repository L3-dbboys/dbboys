### 关闭数据库
```
onmode -ky
```
### 执行恢复
```
ontape -r
```
参考输出
```shell
[gbasedbt@gbasehost01 ~]$ ontape -r
Restore will use level 0 archive file /opt/gbase/backup/gbasehost01_0_L0. Press Return to continue ...


Archive Tape Information

Tape type:      Archive Backup Tape 
Online version: GBase Database Server Version 12.10.FC4G1TL 
Archive date:   Thu Nov 27 21:18:32 2025 
User id:        gbasedbt 
Terminal id:    /dev/pts/1 
Archive level:  0 
Tape device:    /opt/gbase/backup/ 
Tape blocksize (in k): 32 
Tape size (in k): system defined for directory 
Tape number in series: 1 

Spaces to restore:1 [rootdbs                                                                                                                         ] 
2 [plogdbs                                                                                                                         ] 
3 [llogdbs                                                                                                                         ] 
4 [datadbs01                                                                                                                       ] 
5 [sbspace01                                                                                                                       ] 

Archive Information

GBase Database Server Copyright 2001, 2025  General Data Corporation
Initialization Time       11/27/2025 02:49:25
System Page Size          2048
Version                   37
Index Page Logging        OFF
Archive CheckPoint Time   11/27/2025 21:18:32

Dbspaces
number   flags    fchunk   nchunks  flags    owner                            name
1        70001    1        1        N  BA    gbasedbt                         rootdbs                                                                                                                         
2        70001    2        1        N  BA    gbasedbt                         plogdbs                                                                                                                         
3        60001    3        1        N  BA    gbasedbt                         llogdbs                                                                                                                         
4        42001    4        1        N TBA    gbasedbt                         tempdbs01                                                                                                                       
5        68001    5        1        N SBA    gbasedbt                         sbspace01                                                                                                                       
6        60001    6        1        N  BA    gbasedbt                         datadbs01                                                                                                                       


Chunks
chk/dbs offset   size     free     bpages   flags pathname
1   1   0        512000   493115            PO-B- /opt/gbase/dbs/rootdbschk001
2   2   0        517000   4947              PO-B- /opt/gbase/dbs/plogdbschk001
3   3   0        517120   5067              PO-B- /opt/gbase/dbs/llogdbschk001
4   4   0        512000   511512            PO-B- /opt/gbase/dbs/tempdbs01chk001
5   5   0        512000   25659             POSB- /opt/gbase/dbs/sbspace01chk001
6   6   0        512000   504968            PO-B- /opt/gbase/dbs/datadbs01chk001

Continue restore? (y/n)y
Do you want to back up the logs? (y/n)y
File created: /opt/gbase/backup/gbasehost01_0_Log0000000012
Log salvage is complete, continuing restore of archive.
Restore a level 1 archive (y/n) n
Do you want to restore log tapes? (y/n)y

Roll forward should start with log number 12
Restore will use log backup file /opt/gbase/backup/gbasehost01_0_Log0000000012. Press Return to continue ...

Rollforward log file /opt/gbase/backup/gbasehost01_0_Log0000000012 ...

Program over.
```
可使用以下命令检查恢复是否在正常执行
```
onstat -D -r 1
```
对比两次输出“page Wr”列是否变化，可确认数据库是否正在恢复写入数据。  
恢复完成后，数据库处于Quiescent模式，使用onmode -m切换到online。

### 切换数据库状态
```
onmode -m
```
参考文档【[GBase 8s 备份与恢复指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Backup_and_Restore_Guide.pdf)】