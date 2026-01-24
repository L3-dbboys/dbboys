错误信息
```
-34389  Illegal character has been found in the input string.
-34389  在输入串中发现不合法字符。
```

错误原因
```
SQL中存在非法字符，或换行。
```

解决方案
```
检查SQL中是否存在不可见字符。
如果有换行执行以下命令支持换行【onmode -wf ALLOW_NEWLINE=1】
```
