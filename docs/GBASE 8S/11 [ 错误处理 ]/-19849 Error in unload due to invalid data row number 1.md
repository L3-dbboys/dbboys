---
hide_title: false
sidebar_position: 19849
---
错误信息:dbexport或unload导出数据报错
```
-19849: Error in unload due to invalid data:row number 1.
```


错误原因
```
text字段中有与库字符集不一致的字符
```



解决方案
```
更改text为正确字符集或使用GBasedatastuio导出数据。
```
