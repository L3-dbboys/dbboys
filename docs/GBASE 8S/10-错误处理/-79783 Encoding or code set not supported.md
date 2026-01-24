错误信息
```
-79783 Encoding or code set not supported.
-79783  不支持编码或代码集。
```

错误原因
```
库中存在不支持的字符集。
```



解决方案
```
连接串增加IFX_USE_STRENC=true
对于不能正常解码的字符，显示？，不设置，如果是datastudio默认不显示整个字段。
```
