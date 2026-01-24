---
hide_title: false
sidebar_position: 776
---
错误信息
```
-776    Alter fragment error: unable to move rows to new fragmentation scheme.
-772    Record/key doesn't qualify for any table/index fragment.
-776    变更分段错：不能将行移到新的分段存储方案中。
-772    记录/键对任何表/索引分段都不合格。
```
错误原因
```
alter修改分片导致表中已有数据没有合适的存储分片。
```
解决方案
```
修改表分片需确保表中所有已有数据有合适的分片存储。
```

以下操作可重现
|操作序号|session1|
|----|----|
|1|create table t(col1 int);|
|2|insert into t values(1);|
|3|ALTER FRAGMENT ON TABLE t INIT fragment by expression partition p1 col1>100 IN rootdbs,partition p2 col1>200 in rootdbs; |
|4|报错：-776/-772,  col1=1的数据没有指定存储分片|
|5|调整分片规则，增加remainder分片，对于不满足分片条件的数据默认存储在remainder中。如：ALTER FRAGMENT ON TABLE t INIT fragment by expression partition p1 col1>100 IN rootdbs,partition p2 col1>200 in rootdbs,partition p3 REMAINDER in rootdbs;|



