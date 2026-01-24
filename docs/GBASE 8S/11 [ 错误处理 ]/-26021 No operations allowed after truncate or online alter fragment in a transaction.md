---
hide_title: false
sidebar_position: 26021
---
错误信息
```
-26021  No operations allowed after truncate or online alter fragment in a transaction.
-26021  在截断或联机变更分段后事务中不允许执行任何操作
```

错误原因
```
显示声明事务中，truncate table后面只能是commit或rollback，不能再有其他sql
```

解决方案
```
显示声明事务中，truncate table后面不要再跟其他sql，直接commit或rollback
```



