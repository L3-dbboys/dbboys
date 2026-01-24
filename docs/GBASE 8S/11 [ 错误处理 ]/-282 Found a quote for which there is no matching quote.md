---
hide_title: false
sidebar_position: 282
---
错误信息
```
-282    Found a quote for which there is no matching quote.
-282    发现一个引号没有相匹配的引号。
```

错误原因
```
缺失引号或数据存在换行
```


解决方案
```
检查是否缺失引号，是否存在换行，如存在换行，参数ALLOW_NEWLINE设置为1并重启数据库。
```



