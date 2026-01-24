### 标准数据库空间chunk free list
每个chunk有一个或多个chunk free list page，初始一个，位于保留页后第一页，每次扩展一页，仅chunk的第一个chunk free list有一个slot，长度8字节，扩展chunk free list无slot
```
     ###################################################################################
     PAGE PHYSICAL ADDRESS:    15:16                        PAGESIZE:16384 (BYTES)
     -----------------------------------------------------------------------------------
     PAGE TYPE:*Physical log page *Chunk free-list page 
     -----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     -----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|exs|psz|flags-|frptr-|frcnt-|----next----|----prev----|
     |---4Bytes---|--2B--|--2B--|-1B|-1B|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     -----------------------------------------------------------------------------------
     |16          |15    |0x559e|126|56 |0x808 |32    |16344 |11512       |0           |

     PAGE DATA:
     -----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 8 )
     [ offset   24 len    8 ]Extent Offset[4B]:        56,Extent Len[4B]:        32
     [ offset   32 len    8 ]Extent Offset[4B]:       120,Extent Len[4B]:        32
     [ offset   40 len    8 ]Extent Offset[4B]:       184,Extent Len[4B]:        32
     ......     
     [ offset 1008 len    8 ]Extent Offset[4B]:      9720,Extent Len[4B]:        32
     [ offset 1016 len    8 ]Extent Offset[4B]:      9784,Extent Len[4B]:        32
     [ offset 1024 len    8 ]Extent Offset[4B]:      9848,Extent Len[4B]:        32

     [ offset 16376 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len=   8

     PAGE FOOTER: (4 BYTES)
     -----------------------------------------------------------------------------------
                                                                          |--timestamp-|
                                                                          |---4Bytes---|
     -----------------------------------------------------------------------------------
                                                                          |     0x65587|
     ###################################################################################

     
     ###################################################################################
     PAGE PHYSICAL ADDRESS:    15:11512                     PAGESIZE:16384 (BYTES)
     -----------------------------------------------------------------------------------
     PAGE TYPE:*Physical log page *Chunk free-list page 
     -----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     -----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|exs|psz|flags-|frptr-|frcnt-|----next----|----prev----|
     |---4Bytes---|--2B--|--2B--|-1B|-1B|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     -----------------------------------------------------------------------------------
     |11512       |15    |0x72d1|236|56 |0x808 |24    |15340 |0           |16          |

     PAGE DATA:
     -----------------------------------------------------------------------------------
     [ offset   24 len    8 ]Extent Offset[4B]:     11520,Extent Len[4B]:        24
     [ offset   32 len    8 ]Extent Offset[4B]:     11576,Extent Len[4B]:        32
     [ offset   40 len    8 ]Extent Offset[4B]:     11640,Extent Len[4B]:        32
     ......
     [ offset 1888 len    8 ]Extent Offset[4B]:     29880,Extent Len[4B]:        32
     [ offset 1896 len    8 ]Extent Offset[4B]:     29944,Extent Len[4B]:        32
     [ offset 1904 len    8 ]Extent Offset[4B]:     30008,Extent Len[4B]:     49992


     PAGE FOOTER: (4 BYTES)
     -----------------------------------------------------------------------------------
                                                                          |--timestamp-|
                                                                          |---4Bytes---|
     -----------------------------------------------------------------------------------
                                                                          |     0x65e20|
     ###################################################################################
```
### 智能大对象空间chunk free list
sbspace chunk free list结构与普通空间一致，记录元数据free extents
```
DBspace Usage Report: sbspace01           Owner: gbasedbt  Created: 12/14/2023


 Chunk Pathname                             Pagesize(k)  Size(p)  Used(p)  Free(p)
    16 /data/gbase/testfile15                         2     5000 see below see below

 Description                                                   Offset(p)  Size(p)
 ------------------------------------------------------------- -------- --------
 RESERVED PAGES                                                       0        2
 CHUNK FREELIST PAGE                                                  2        1
 sbspace01:'gbasedbt'.TBLSpace                                        3       50
 SBLOBSpace FREE USER DATA (AREA 1)                                  53     1305
 SBLOBSpace RESERVED USER DATA (AREA 1)                            1358      988
 sbspace01:'gbasedbt'.sbspace_desc                                 2346        4
 sbspace01:'gbasedbt'.chunk_adjunc                                 2350        4
 sbspace01:'gbasedbt'.LO_ud_free                                   2354       13
 sbspace01:'gbasedbt'.LO_hdr_partn                                 2367       71
 SBLOBSpace FREE META DATA                                         2438      268
 SBLOBSpace RESERVED USER DATA (AREA 2)                            2706      988
 SBLOBSpace FREE USER DATA (AREA 2)                                3694     1306
```
sbspace dd读取chunk free list
dd if=/data/gbase/testfile15 bs=2k count=1 skip=2 |hexdump -C
```
00000000  02 00 00 00 10 00 91 36  01 00 08 08 20 00 d8 07  |.......6.... ...|
00000010  00 00 00 00 00 00 00 00  86 09 00 00 0c 01 00 00  |................|
00000020  00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00  |................|
*
000007f0  00 00 00 00 00 00 00 00  18 00 08 00 8b 36 08 00  |.............6..|
00000800
```
解析chunk free list输出
```
     ###################################################################################
     PAGE PHYSICAL ADDRESS:    16:2                         PAGESIZE:2048 (BYTES)
     -----------------------------------------------------------------------------------
     PAGE TYPE:*Physical log page *Chunk free-list page 
     -----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     -----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|exs|psz|flags-|frptr-|frcnt-|----next----|----prev----|
     |---4Bytes---|--2B--|--2B--|-1B|-1B|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     -----------------------------------------------------------------------------------
     |2           |16    |0x3691|1  |0  |0x808 |32    |2008  |0           |0           |

     PAGE DATA:
     -----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 24 ) ,Slot Length ( 8 )
     [ offset   24 len    8 ]Extent Offset[4B]:      2438,Extent Len[4B]:       268

     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=  24,[2B]Slot->sl_len=   8

     PAGE FOOTER: (4 BYTES)
     -----------------------------------------------------------------------------------
                                                                          |--timestamp-|
                                                                          |---4Bytes---|
     -----------------------------------------------------------------------------------
                                                                          |     0x8368b|
     ###################################################################################
```
### 简单大对象空间chunk free list
简单大对象空间chunk free list未使用，空白页
```
     ###################################################################################
     PAGE PHYSICAL ADDRESS:    17:2                         PAGESIZE:2048 (BYTES)
     -----------------------------------------------------------------------------------
     PAGE TYPE:*Physical log page *Blob chunk free-list bitmap page 
     -----------------------------------------------------------------------------------

     PAGE HEADER: (24 BYTES)
     -----------------------------------------------------------------------------------
     |---offset---|chunk-|cksum-|exs|psz|flags-|frptr-|frcnt-|----next----|----prev----|
     |---4Bytes---|--2B--|--2B--|-1B|-1B|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
     -----------------------------------------------------------------------------------
     |2           |17    |0x3713|0  |0  |0x80d |24    |2020  |0           |0           |

     PAGE DATA:
     -----------------------------------------------------------------------------------
     Slot1: Slot Begin Postion ( 0 ) ,Slot Length ( 0 )

     [ offset 2040 len    4 ]Slot  1: [2B]Slot->sl_ptr=   0,[2B]Slot->sl_len=   0

     PAGE FOOTER: (4 BYTES)
     -----------------------------------------------------------------------------------
                                                                          |--timestamp-|
                                                                          |---4Bytes---|
     -----------------------------------------------------------------------------------
                                                                          |     0x83708|
     ###################################################################################
```
### 附件：chunk free list page结构图
![img10.png](img/img10.png)