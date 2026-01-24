错误信息
```
-240 Could not delete a row.
-111: ISAM error:  no record found.
-240 不能删除行。
-111    ISAM错误: 没找到记录。
```

错误原因
```
索引损坏
```

解决方案
```
1、oncheck -cDI 库名:表名 
2、重建损坏索引
```

oncheck -cDI输出参考：
```
oneheck-cDI testdb:testtab
Validating indexes for testdb:gbasedbt.testtab send audit...
Index idx_testtab
Index fragment partition indexdbs01 in DBspace indexdbs01 
ERROR:No data row exists for btree item.
Btree item contains fragid 0x900075 rowid 0x203, key value:
Key:33620231:015010270129200321080909354"1:"03202064161917276C905D1027016218":2:20210318:20210318:
Fragid 0x900075 Rowid 0x203 contains key value;
Key:33620231:0150102701202003210000093F54":1:"03202004161932270C005D1027016218":2:20210318:20210318:
ISAM error: illegal key descriptor (too many parts or too long).
Please Drop and Recreate Index idx_testtab for testdb:gbasedbt.testtab
```