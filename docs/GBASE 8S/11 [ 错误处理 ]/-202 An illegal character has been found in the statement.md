---
hide_title: false
sidebar_position: 202
---
错误信息
```
-202    An illegal character has been found in the statement.
-202    在语句中发现一个不合法的字符。
```

错误原因
```
表中存在非法字符，或SQL语句中存在非法字符，如字符串使用中文单引号。
```

解决方案
```
如sql语句中无非法字符，将参数EILSEQ_COMPAT_MODE设置为1并重启数据库。
```

以下sql可重现202错误：
```
select * from systables where tabname=’a’;
```



