---
hide_title: false
sidebar_position: 13
---
![tblspace.png](/img/tblspace.png)
Tblspace Tblspace是一个特别的表空间，每个空间均有这样一个表空间，该表空间中每一页记录一个表的描述信息及extent信息等(Partition Page)，此空间在oncheck -pe显示的extent信息类似如下：
```
 rootdbs:'gbasedbt'.TBLSpace                                         13      250
 rootdbs:'gbasedbt'.TBLSpace                                      63554       50
 rootdbs:'gbasedbt'.TBLSpace                                      64065      100
 rootdbs:'gbasedbt'.TBLSpace                                      71257      200
 ```
与常规表空间一样，该表空间首页为bitmap页，第二页为Partition Page，该页描述Tblspace Tblspace本身，是Dbspace中首个Partition Page，偏移量为14，与Dbspace描述页记录的位置一致，如以下为保留页中记录的rootdbs中首个空间描述页位置：
```
[ offset   44 len    4 ]Partition Table Page Number     14
```
rootdbs中，Tblspace Tblspace初始extent大小250页：
```
DBspace Usage Report: rootdbs             Owner: gbasedbt  Created: 12/08/2023


 Chunk Pathname                             Pagesize(k)  Size(p)  Used(p)  Free(p)
     1 /data/gbase/rootdbschk001                      2   100000    75122    24878

 Description                                                   Offset(p)  Size(p)
 ------------------------------------------------------------- -------- --------
 RESERVED PAGES                                                       0       12
 CHUNK FREELIST PAGE                                                 12        1
 rootdbs:'gbasedbt'.TBLSpace                                         13      250
 PHYSICAL LOG                                                       263    25000
 LOGICAL LOG: Log file 1                                          25263     5000
 LOGICAL LOG: Log file 2                                          30263     5000
 LOGICAL LOG: Log file 3                                          35263     5000
 LOGICAL LOG: Log file 4                                          40263     5000
 LOGICAL LOG: Log file 5                                          45263     5000
 LOGICAL LOG: Log file 6                                          50263     5000
 LOGICAL LOG: Log file 7                                          55263     5000
 sysmaster:'gbasedbt'.sysdatabases                                60263        4
 system:'gbasedbt'.syslicenseinfo                                 60267       16
```
其他Dbspace中，Tblspace Tblspace初始extent大小50页：
```
DBspace Usage Report: datadbs1            Owner: gbasedbt  Created: 12/08/2023


 Chunk Pathname                             Pagesize(k)  Size(p)  Used(p)  Free(p)
     2 /data/gbase/testfile1                          2   512000       53   511947

 Description                                                   Offset(p)  Size(p)
 ------------------------------------------------------------- -------- --------
 RESERVED PAGES                                                       0        2
 CHUNK FREELIST PAGE                                                  2        1
 datadbs1:'gbasedbt'.TBLSpace                                         3       50
 FREE                                                                53   511947

 Total Used:       53
 Total Free:   511947
```
Tblspace Tblspace里的每一个页成为一个Partition Page，用于描述一个表空间，结构如下
```
----------------------------------------------------------------------------------
|---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|------------|------------|
|---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
----------------------------------------------------------------------------------
[ offset   24 len  136 ]Slot 1: The partition structure
[ offset  160 len    ? ]Slot 2: Dbspace name,table owner, table name.NLS collation sequence (if any)
[ offset    ? len    ? ]Slot 3: Special column Info
[ offset    ? len    ? ]Slot 4: Key (index) info
[ offset    ? len    ? ]Slot 5: Extent list
[ offset 2024 len    4 ]Slot  5
[ offset 2028 len    4 ]Slot  4
[ offset 2032 len    4 ]Slot  3
[ offset 2036 len    4 ]Slot  2
[ offset 2040 len    4 ]Slot  1
-----------------------------------------------------------------------------------
                                                                      |-timestamp-|
                                                                      |--4Bytes---|
-----------------------------------------------------------------------------------
```
