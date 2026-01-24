page4
```
     ##################################################################################
     PAGE PHYSICAL ADDRESS:    1:4                         PAGESIZE:2048 (BYTES)
     ----------------------------------------------------------------------------------
     PAGE TYPE:     *Root reserved page *Physical log page 
     ----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     ----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|--nxoffset--|----nxpgs---|
     |---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     ----------------------------------------------------------------------------------
     |4           |1     |0xf628|1     |0x1800|312   |1728  |0           |0           |

     PAGE DATA: (292 BYTES USED,1728 BYTES FREE)
     ----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 288 )
     [ offset   24 len    4 ]DBspace Number                  1
     [ offset   28 len    4 ]Flags                           0x40001
     [ offset   32 len    4 ]DBspace First Chunk             1
     [ offset   36 len    4 ]Number Of Chunks                1
     [ offset   40 len    4 ]Create Time                     2023-12-08 08:07:17
     [ offset   44 len    4 ]Partition Table Page Number     14
     [ offset   48 len    4 ]Partition Of Tmpsbspace         0x0
     [ offset   52 len    4 ]Pagesize (Byte)                 2048
     [ offset   56 len    4 ]BLOB Columns Referencing        0
     [ offset   60 len    4 ]Level 0 Archive Began           0
     [ offset   64 len    4 ]Level 0 Time Stamp              0
     [ offset   68 len    4 ]Level 0 Logical Log Unique Id   0
     [ offset   72 len    4 ]Level 0 Logical Log Position    0x0
     [ offset   76 len    4 ]Level 1 Archive Began           0
     [ offset   80 len    4 ]Level 1 Time Stamp              0
     [ offset   84 len    4 ]Level 1 Logical Log Unique Id   0
     [ offset   88 len    4 ]Level 1 Logical Log Position    0x0
     [ offset   92 len    4 ]Level 2 Archive Began           0
     [ offset   96 len    4 ]Level 2 Time Stamp              0
     [ offset  100 len    4 ]Level 2 Logical Log Unique Id   0
     [ offset  104 len    4 ]Level 2 Logical Log Position    0x0
     [ offset  108 len    4 ]Logical Log Unique Id           0
     [ offset  112 len    4 ]Logical Log Position            0x0
     [ offset  116 len    4 ]Oldest Logical Log Unique Id    4
     [ offset  120 len    4 ]Last Logical Log Unique Id      0
     [ offset  124 len    4 ]Time Of Last Restore            0
     [ offset  128 len    4 ]PIT Used To Terminate Replay    (null)
     [ offset  132 len    4 ]Expand Size (Chunk Create)      100
     [ offset  136 len    4 ]Expand size (Chunk Extend)      10000
     [ offset  140 len   12 ]Unused                          
     [ offset  152 len  128 ]DBspace Name                    rootdbs                        
     [ offset  280 len   32 ]DBspace Owner                   gbasedbt                       

     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len= 288

     PAGE FOOTER: (4 BYTES)
     ----------------------------------------------------------------------------------
                                                                          |-timestamp-|
                                                                          |--4Bytes---|
     ----------------------------------------------------------------------------------
                                                                          |    0x3f62e|
     ##################################################################################
```
page5
```
     ##################################################################################
     PAGE PHYSICAL ADDRESS:    1:5                         PAGESIZE:2048 (BYTES)
     ----------------------------------------------------------------------------------
     PAGE TYPE:     *Root reserved page *Physical log page 
     ----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     ----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|--nxoffset--|----nxpgs---|
     |---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     ----------------------------------------------------------------------------------
     |5           |1     |0xdffa|1     |0x1800|312   |1728  |0           |0           |

     PAGE DATA: (292 BYTES USED,1728 BYTES FREE)
     ----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 288 )
     [ offset   24 len    4 ]DBspace Number                  1
     [ offset   28 len    4 ]Flags                           0x40001
     [ offset   32 len    4 ]DBspace First Chunk             1
     [ offset   36 len    4 ]Number Of Chunks                1
     [ offset   40 len    4 ]Create Time                     2023-12-08 08:07:17
     [ offset   44 len    4 ]Partition Table Page Number     14
     [ offset   48 len    4 ]Partition Of Tmpsbspace         0x0
     [ offset   52 len    4 ]Pagesize (Byte)                 2048
     [ offset   56 len    4 ]BLOB Columns Referencing        0
     [ offset   60 len    4 ]Level 0 Archive Began           0
     [ offset   64 len    4 ]Level 0 Time Stamp              0
     [ offset   68 len    4 ]Level 0 Logical Log Unique Id   0
     [ offset   72 len    4 ]Level 0 Logical Log Position    0x0
     [ offset   76 len    4 ]Level 1 Archive Began           0
     [ offset   80 len    4 ]Level 1 Time Stamp              0
     [ offset   84 len    4 ]Level 1 Logical Log Unique Id   0
     [ offset   88 len    4 ]Level 1 Logical Log Position    0x0
     [ offset   92 len    4 ]Level 2 Archive Began           0
     [ offset   96 len    4 ]Level 2 Time Stamp              0
     [ offset  100 len    4 ]Level 2 Logical Log Unique Id   0
     [ offset  104 len    4 ]Level 2 Logical Log Position    0x0
     [ offset  108 len    4 ]Logical Log Unique Id           0
     [ offset  112 len    4 ]Logical Log Position            0x0
     [ offset  116 len    4 ]Oldest Logical Log Unique Id    2
     [ offset  120 len    4 ]Last Logical Log Unique Id      0
     [ offset  124 len    4 ]Time Of Last Restore            0
     [ offset  128 len    4 ]PIT Used To Terminate Replay    (null)
     [ offset  132 len    4 ]Expand Size (Chunk Create)      100
     [ offset  136 len    4 ]Expand size (Chunk Extend)      10000
     [ offset  140 len   12 ]Unused                          
     [ offset  152 len  128 ]DBspace Name                    rootdbs                        
     [ offset  280 len   32 ]DBspace Owner                   gbasedbt                       

     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len= 288

     PAGE FOOTER: (4 BYTES)
     ----------------------------------------------------------------------------------
                                                                          |-timestamp-|
                                                                          |--4Bytes---|
     ----------------------------------------------------------------------------------
                                                                          |    0x1dfff|
     ##################################################################################
```
Flags说明
```
Flags (hex) – The possible values for dbspace flags are:
0x0001       空间不包含镜像chunks
0x0002       空间使用了镜像chunks
0x0004       空间禁用了镜像chunks
0x0008       空间新增了镜像
Flags specific to blobspaces:
0x0010       空间是简单大对象空间BLOBspace
0x0020       BLOBspace使用了可移动设备
0x0040       BLOBspace使用了固定设备
0x0080       BLOBspace被删除
0x0100       BLOBspace使用了STAGEBLOB参数指定光盘，新版本已不再使用该参数
Other flags:
0x0200       空间正在物理恢复
0x0400       空间已完成物理恢复
0x0800       空间正在逻辑恢复
0x1000       A table in the dbspace was dropped
0x2000       临时空间
0x4000       空间正在备份
0x8000       智能大对象空间
0x10000      Either the physical or logical log has changed

0x20000      初始化后空间或chunk保留页发生过变化
0x40000      空间包含大chunk
0x80000      空间中的chunk被重命名了
0x100000     SSC备机上的SDS_TEMPDBS的临时空间
0x200000     SSC备机上的DBSPACETEMP 配置参数的空间
0x400000     The dbspace was externally backed up
0x800000     Dbspace is being defragmented
0x1000000    使用onspace -c -P创建的物理日志空间
```