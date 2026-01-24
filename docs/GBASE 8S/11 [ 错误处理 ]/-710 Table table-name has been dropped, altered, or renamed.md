---
hide_title: false
sidebar_position: 710
---
错误信息
```
-710    Table <table-name> has been dropped, altered, or renamed.
-710    已删除、更改或重命名表 <table-name>。
```

  
错误原因：
```
SQL prepare之后，表结构发生了变化
```

解决方案：
```
设置AUTO_REPREPARE为1【onmode -wf AUTO_REPREPARE=1】，如果表结构变化时partnum发生了变化，重连数据库。
```
