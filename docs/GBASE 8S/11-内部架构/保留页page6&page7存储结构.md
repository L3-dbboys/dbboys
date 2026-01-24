每个page可存储6个chunk信息，按空间号，chunk号排序，首个page存储5个chunk后，创建第六个chunk时开始扩展extent，可以保证扩展extent的chunk信息始终保存在第一个页里，确保可以从第一个page读取扩展extent信息。
page6
```
     ##################################################################################
     PAGE PHYSICAL ADDRESS:    1:6                         PAGESIZE:2048 (BYTES)
     ----------------------------------------------------------------------------------
     PAGE TYPE:     *Root reserved page *Physical log page 
     ----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     ----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|--nxoffset--|nxchk-|nxpgs|
     |---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|--2B--|--2B-|
     ----------------------------------------------------------------------------------
     |6           |1     |0x5b1a|1     |0x1800|328   |1712  |0           |0     |0    |

     PAGE DATA: (308 BYTES USED,1712 BYTES FREE)
     ----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 304 )
     [ offset   24 len    4 ]Chunk Version                   -32
     [ offset   28 len    2 ]Chunk Number                    1
     [ offset   30 len    2 ]DBspace Next Chunk Number       0
     [ offset   32 len    2 ]Chunk Size                      100000 (p)
     [ offset   36 len    4 ]First Page Offset In Dbspace    0
     [ offset   40 len    4 ]Chunk Free Size                 100000 (p)
     [ offset   44 len    4 ]Overhead For Blobspace Chunk    0
     [ offset   48 len    4 ]Chunk Offset                    0 (p)
     [ offset   52 len    4 ]Unused                          
     [ offset   56 len    4 ]Chunk Flags                     0x10040
     [ offset   60 len    2 ]Chunk PageSize                  2048 (B)
     [ offset   62 len    2 ]Dbspace Number                  1
     [ offset   64 len    2 ]Chunk Name Length               25
     [ offset   66 len  262 ]Chunk Path                      /data/gbase/rootdbschk001

     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len= 304

     PAGE FOOTER: (4 BYTES)
     ----------------------------------------------------------------------------------
                                                                          |-timestamp-|
                                                                          |--4Bytes---|
     ----------------------------------------------------------------------------------
                                                                          |    0x45b19|
     ##################################################################################
```
page7
```
     ##################################################################################
     PAGE PHYSICAL ADDRESS:    1:7                         PAGESIZE:2048 (BYTES)
     ----------------------------------------------------------------------------------
     PAGE TYPE:     *Root reserved page *Physical log page 
     ----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     ----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|nslots|flags-|frptr-|frcnt-|--nxoffset--|nxchk-|nxpgs|
     |---4Bytes---|--2B--|--2B--|--2B--|--2B--|--2B--|--2B--|---4Bytes---|--2B--|--2B-|
     ----------------------------------------------------------------------------------
     |7           |1     |0xf62a|1     |0x1800|328   |1712  |0           |0     |0    |

     PAGE DATA: (308 BYTES USED,1712 BYTES FREE)
     ----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 304 )
     [ offset   24 len    4 ]Chunk Version                   -32
     [ offset   28 len    2 ]Chunk Number                    1
     [ offset   30 len    2 ]DBspace Next Chunk Number       0
     [ offset   32 len    2 ]Chunk Size                      100000 (p)
     [ offset   36 len    4 ]First Page Offset In Dbspace    0
     [ offset   40 len    4 ]Chunk Free Size                 100000 (p)
     [ offset   44 len    4 ]Overhead For Blobspace Chunk    0
     [ offset   48 len    4 ]Chunk Offset                    0 (p)
     [ offset   52 len    4 ]Unused                          
     [ offset   56 len    4 ]Chunk Flags                     0x10040
     [ offset   60 len    2 ]Chunk PageSize                  2048 (B)
     [ offset   62 len    2 ]Dbspace Number                  1
     [ offset   64 len    2 ]Chunk Name Length               25
     [ offset   66 len  262 ]Chunk Path                      /data/gbase/rootdbschk001

     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len= 304

     PAGE FOOTER: (4 BYTES)
     ----------------------------------------------------------------------------------
                                                                          |-timestamp-|
                                                                          |--4Bytes---|
     ----------------------------------------------------------------------------------
                                                                          |    0x3f62f|
     ##################################################################################
```
Chunk Flags说明
```
#以下输出未在任何环境出现
0x0001         RAW设备(已取消?)
0x0002 			  BLOCK设备(已取消?)
0x0004 			  FILE设备(已取消?)
0x0008 			  SYNC异步设备(已取消?)
#以下输出可见
0x0010 				镜像chunk，仅针对mirror chunk
0x0020 				off-line
0x0040 				on-line
0x0080 				chunk正在被删除
0x0100 				新镜像chunk，仅针对mirror chunk
0x0200 				简单大对象空间chunk
0x0400 				Chunk正在被删除
0x0800 				光盘blobspace chunk，STAGEBLOB早期版本存在，新版本已无此参数
0x1000         Chunk状态不一致，如集群备机
0x2000 			  CHAINED - chunk has been chained
0x4000         智能大对象空间chunk 
0x10000 		 	大chunk
0x20000        chunk包含tblspace tblspace extent,仅针对primary chunk，不包含mirror chunk
0x40000        chunk初始化后没有执行过检查点
```