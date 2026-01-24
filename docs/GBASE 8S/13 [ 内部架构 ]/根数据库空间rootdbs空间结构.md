---
hide_title: false
sidebar_position: 10
---
rootdbs空间结构
```

DBspace Usage Report: rootdbs             Owner: gbasedbt  Created: 12/14/2023


 Chunk Pathname                             Pagesize(k)  Size(p)  Used(p)  Free(p)
     1 /data/gbase/rootdbschk001                      2   100000    67928    32072

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
 sysmaster:'gbasedbt'.sysdatabases                                55263        4
 system:'gbasedbt'.syslicenseinfo                                 55267       16
 ......
 FREE                                                             67939    32061

 Total Used:    67936
 Total Free:    32064


 Chunk Pathname                             Pagesize(k)  Size(p)  Used(p)  Free(p)
     2 /data/gbase/rootdbschk002                      2      500        3      497

 Description                                                   Offset(p)  Size(p)
 ------------------------------------------------------------- -------- --------
 RESERVED PAGES                                                       0        2
 CHUNK FREELIST PAGE                                                  2        1
 FREE                                                                 3      497

 Total Used:        3
 Total Free:      497
 ```
![rootchunk.png](/img/rootchunk.png)
首个chunk包含以下区域：
```
RESERVED PAGES  										 #12页，可扩展一个extent，extent每次扩展增长一页
CHUNK FREELIST PAGE  								#初始1页，记录本chunk中extent使用状态
rootdbs:'gbasedbt'.TBLSpace  				#初始250页，每个表1个页记录表extent等信息，partnum=0x100001
sysmaster:'gbasedbt'.sysdatabases    #初始4页，记录实例中所有库,partnum=0x100002
```
![norootchunk.png](/img/norootchunk.png)
新增chunk包含以下区域：
```
RESERVED PAGES  										 #2页，未使用
CHUNK FREELIST PAGE  								#初始1页，记录本chunk中extent使用状态
```