chunk1保留页12页，位置位于chunk1前12页
```
DBspace Usage Report: rootdbs             Owner: gbasedbt  Created: 12/08/2023


 Chunk Pathname                             Pagesize(k)  Size(p)  Used(p)  Free(p)
     1 /data/gbase/rootdbschk001                      2   100000    72938    27062

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
 sysmaster:'gbasedbt'.systables                                   60283        8
 sysmaster:'gbasedbt'.syscolumns                                  60291       32
```
chunk1保留页用途及页头
![img1.png](img/img1.png)
```
###################################################################################
|---page0----|  存储版权版本相关信息，无需扩展
-----------------------------------------------------------------------------------
|---page1----|  存储部分重要配置参数，无需扩展
-----------------------------------------------------------------------------------
|---page2----|  轮询存储最新，次新检查点	#不够时只可在rootdbs首个chunk中扩展，只扩展一个 
|---page3----|  及逻辑日志状态信息       #extent，每次扩展1页，无可用连续页则整个extent   
--------------------------------------#移动，相同功能page成对扩展且扩展extent相邻。    
|---page4----|  存储空间信息            #增加逻辑日志报错：root chunk is almost full  
|---page5----|                        #创建空间报错：ISAM error: no free disk space
-----------------------------------------------------------------------------------
|---page6----|  存储chunk信息          #不够时可在rootdbs中的任意chunk中扩展，页头记录了
|---page7----|  按空间号chunk号排序	   #扩展extent chunk号，page2、3、4、5未记录
-----------------------------------------------------------------------------------
|---page8----|  镜像chunk信息，行为与page6、7类似
|---page9----|
-----------------------------------------------------------------------------------
|---page10---|  存储备份信息及高可用集群状态切换信息，无需扩展
|---page11---|
###################################################################################
-----------------------------------保留页页头差异-------------------------------------

page0、1、10、11，最后8字节未使用
-----------------------------------------------------------------------------------
|---offset---|chunk-|cksum-|sts|psz|flags-|frptr-|frcnt-|--nxoffset--|----nxpgs---|
|---4Bytes---|--2B--|--2B--|-1B|-1B|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
-----------------------------------------------------------------------------------

page2、3、4、5页头，nxoffset为扩展extent在chunk1中的偏移量，nxpgs为extent页数
|---offset---|chunk-|cksum-|sts|psz|flags-|frptr-|frcnt-|--nxoffset--|----nxpgs---|
|---4Bytes---|--2B--|--2B--|-1B|-1B|--2B--|--2B--|--2B--|---4Bytes---|----4Bytes--|
-----------------------------------------------------------------------------------

page6、7、8、9页头，nxchk为扩展extent chunk号，nxoffset为偏移量，nxpgs为extent页数
-----------------------------------------------------------------------------------
|---offset---|chunk-|cksum-|sts|psz|flags-|frptr-|frcnt-|--nxoffset--|nxchk-|nxpgs|
|---4Bytes---|--2B--|--2B--|-1B|-1B|--2B--|--2B--|--2B--|---4Bytes---|--2B--|--2B-|
-----------------------------------------------------------------------------------
###################################################################################

```
chunk2 ... chunkN保留页为chunk起始位置2页，空白页未使用
```
 Chunk Pathname                             Pagesize(k)  Size(p)  Used(p)  Free(p)
     2 /data/gbase/rootdbschk002                      2     5000       23     4977

 Description                                                   Offset(p)  Size(p)
 ------------------------------------------------------------- -------- --------
 RESERVED PAGES                                                       0        2
 CHUNK FREELIST PAGE                                                  2        1
 FREE                                                                 3        8
 sysadmin:'gbasedbt'.mon_prof                                        11        8
 sysadmin:'gbasedbt'.mon_prof_idx1                                   19       12
 FREE                                                                31     4969

 Total Used:       23
 Total Free:     4977
```