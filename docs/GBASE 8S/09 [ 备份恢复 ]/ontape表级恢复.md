---
hide_title: false
sidebar_position: 4
---

GBase 8s支持使用【archecker】命令从备份中恢复某个表的数据到指定时间点，对于生产环境中误删表数据的场景非常有用，以下是某生产环境执行表级恢复过程。

【ontape】或【onbar】的备份文件，均可用于表级别恢复。

表名和库名已脱敏处理。

设置配置文件【restore.cmd】，源表需指定【in datadbsxxx】,目标表无需指定：
```
SET COMMIT TO 100;
SET WORKSPACE to datadbs1;
DATABASE ******db;
CREATE TABLE ******tab
(
    id varchar(64) not null ,
    ifm_machine_supplies_task_id varchar(64),
    cabin_class_code varchar(8),
    class_sort integer,
    product_sort integer,
    wh_product_id varchar(64),
    fore_cabin varchar(64),
    rear_cabin varchar(64),
    remark lvarchar(1024),
    sales_type varchar(4),
    wh_warehouse_id varchar(64),
    recycle_number decimal(16,0),
    spec varchar(64),
    product_class_name varchar(64),
    actual_delivery_quantity decimal(16,0),
    recycle_status integer,
    fore_cabin_recycle_number decimal(16,6),
    rear_cabin_recycle_number decimal(16,6),
    fore_cabin_consume_number decimal(16,6),
    rear_cabin_consume_number decimal(16,6),
    total_recycle_number decimal(16,6),
    total_delivery_quantity decimal(16,6),
    total_consume_quantity decimal(16,6),
    primary key (id)
) in datadbs1;
CREATE TABLE ******tab_restore
(
    id varchar(64) not null ,
    ifm_machine_supplies_task_id varchar(64),
    cabin_class_code varchar(8),
    class_sort integer,
    product_sort integer,
    wh_product_id varchar(64),
    fore_cabin varchar(64),
    rear_cabin varchar(64),
    remark lvarchar(1024),
    sales_type varchar(4),
    wh_warehouse_id varchar(64),
    recycle_number decimal(16,0),
    spec varchar(64),
    product_class_name varchar(64),
    actual_delivery_quantity decimal(16,0),
    recycle_status integer,
    fore_cabin_recycle_number decimal(16,6),
    rear_cabin_recycle_number decimal(16,6),
    fore_cabin_consume_number decimal(16,6),
    rear_cabin_consume_number decimal(16,6),
    total_recycle_number decimal(16,6),
    total_delivery_quantity decimal(16,6),
    total_consume_quantity decimal(16,6),
    primary key (id)
);
INSERT INTO ******tab_restore SELECT * FROM ******tab;
RESTORE TO "2023-06-27 16:00:00";
```
设置时间格式：
```
export GL_DATETIME="%Y-%m-%d %H:%M:%S"
```
执行恢复（-t表示使用ontape备份文件，如果是onbar，则为【archecker -bvs -f restore.cmd】）：
```
archecker -tvs -f restore.cmd
```
日志输出：
```
[gbasedbt@node1 restore]$ archecker -tvs -f restore.cmd
GBase Database Server Version 12.10.FC4G1AEE
Program Name:   archecker
Version:        8.0
Released:       2018-08-30 14:17:46
CSDK:           GBase CSDK Version 4.10
ESQL:           GBASE-ESQL Version 4.10.FC4G1_2.0.1A2_2
Compiled:       08/30/18 14:17  on Linux 2.6.32-220.el6.x86_64 #1 SMP Wed Nov 9 08:03:13 EST 2011 

AC_STORAGE               /tmp
AC_MSGPATH               /tmp/ac_msg.log
AC_VERBOSE               on
AC_TAPEDEV               /data/backup/
AC_TAPEBLOCK             32 KB
AC_LTAPEDEV              /data/backup/
AC_LTAPEBLOCK            32 KB
Dropping old log control tables
Extracting table ******db:******tab into ******db:******tab_restore
Archive file /data/backup/node1_0_L0
Tape type:      Archive Backup Tape
OnLine version: GBase Database Server Version 12.10.FC4G1AEE
Archive date:   Mon Jun 26 04:00:01 2023
Archive level:  0
Tape blocksize:  32768
Tape size:  2147483647
Tape number in series:  1
........................................
........................................
........................................
........................................
.......................................
Scan PASSED
Control page checks PASSED
Table checks PASSED
Table extraction commands 1
Tables found on archive 1
LOADED: ******db:******tab_restore produced 631569 rows.
Creating log control tables
Log file /data/backup/node1_0_Log0000003983
Log tape backup date:   Mon Jun 26 06:06:35 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Log file /data/backup/node1_0_Log0000003984
Log tape backup date:   Mon Jun 26 09:30:00 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3984
Log file /data/backup/node1_0_Log0000003985
Log tape backup date:   Mon Jun 26 12:53:41 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3985
Log file /data/backup/node1_0_Log0000003986
Log tape backup date:   Mon Jun 26 16:09:52 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3986
Log file /data/backup/node1_0_Log0000003987
Log tape backup date:   Mon Jun 26 19:42:24 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3987
Log file /data/backup/node1_0_Log0000003988
Log tape backup date:   Mon Jun 26 23:40:32 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3988
Log file /data/backup/node1_0_Log0000003989
Log tape backup date:   Tue Jun 27 02:30:17 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3989
Log file /data/backup/node1_0_Log0000003990
Log tape backup date:   Tue Jun 27 06:24:39 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3990
Log file /data/backup/node1_0_Log0000003991
Log tape backup date:   Tue Jun 27 08:09:47 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3991
Log file /data/backup/node1_0_Log0000003992
Log tape backup date:   Tue Jun 27 08:24:54 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3992
Log file /data/backup/node1_0_Log0000003993
Log tape backup date:   Tue Jun 27 11:45:56 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3993
Log file /data/backup/node1_0_Log0000003994
Log tape backup date:   Tue Jun 27 15:04:46 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3994
Log file /data/backup/node1_0_Log0000003995
Log tape backup date:   Tue Jun 27 17:05:33 2023
Tape blocksize: 32768
Tape size: 2147483647
Tape number in series: 1
Switching to log 3995
Recovery PIT reached.

Logically recovered ******db:******tab_restore Inserted 2574 Deleted 26 Updated 13783
```