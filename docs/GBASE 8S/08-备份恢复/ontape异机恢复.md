### 恢复前准备
1、安装与备份机器版本一致的数据库软件，无需创建或初始化实例。  
2、恢复环境变量，复制gbasedbt用户环境变量到目标机器gbasedbt。默认环境变量文件为~gbasedbt/.bash_profile  
3、恢复配置文件, 将备份环境的$GBASEDBTDIR/etc/$ONCONFIG配置文件复制到恢复环境, 将备份环境的$GBASEDBTSQLHOSTS配置文件复制到恢复环境。  
4、目标机器修改配置文件$GBASEDBTSQLHOSTS，将IP修改为当前机器IP。  
5、将备份服务器上的备份数据复制到目标服务器同样的路径，如/backup/dbhost01_0_L0,并重命名备份文件，使文件名命名符合“主机名_实例编号_L0”  
6、根据备份服务器onstat -d输出，在恢复环境创建对应的数据文件，如gbasedbt用户执行：
```
touch /data/gbase/rootdbschk001
chmod 660 /data/gbase/rootdbschk001
```
### 开始恢复
```
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
Do you want to back up the logs? (y/n)n
Restore a level 1 archive (y/n) n
Do you want to restore log tapes? (y/n)n
/opt/gbase/bin/onmode -sy

Program over.
```
### 切换状态
```
onmode -m
```
参考文档【[GBase 8s 备份与恢复指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Backup_and_Restore_Guide.pdf)】
