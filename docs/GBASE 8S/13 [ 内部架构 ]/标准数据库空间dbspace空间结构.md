---
hide_title: false
sidebar_position: 11
---
dbspace空间结构
```
DBspace Usage Report: datadbs01           Owner: gbasedbt  Created: 12/14/2023


 Chunk Pathname                             Pagesize(k)  Size(p)  Used(p)  Free(p)
     3 /data/gbase/datadbs01chk001                   16     1000       53      947

 Description                                                   Offset(p)  Size(p)
 ------------------------------------------------------------- -------- --------
 RESERVED PAGES                                                       0        2
 CHUNK FREELIST PAGE                                                  2        1
 datadbs01:'gbasedbt'.TBLSpace                                        3       50
 FREE                                                                53      947

 Total Used:       53
 Total Free:      947


 Chunk Pathname                             Pagesize(k)  Size(p)  Used(p)  Free(p)
     4 /data/gbase/datadbs01chk002                   16     1000        3      997

 Description                                                   Offset(p)  Size(p)
 ------------------------------------------------------------- -------- --------
 RESERVED PAGES                                                       0        2
 CHUNK FREELIST PAGE                                                  2        1
 FREE                                                                 3      997

 Total Used:        3
 Total Free:      997
 ```
每个chunk前两个RESERVED PAGES为空白页，未使用
每个chunk初始化一个CHUNK FREELIST PAGE
每个dbspace初始化一个TBLSpace TBLSpace
![norootchunk.png](/img/norootchunk.png)