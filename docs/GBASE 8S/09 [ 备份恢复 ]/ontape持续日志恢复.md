---
hide_title: false
sidebar_position: 5
---

持续日志恢复，用于在异机持续做日志同步恢复，数据库状态不可用，停止恢复后可用。
1、物理恢复
```
ontape -p
```
2、日志回放
```
ontape -l -C
```
3、停止CLR切换到静默模式
```
ontape -l -X
```