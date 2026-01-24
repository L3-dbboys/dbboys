---
hide_title: false
sidebar_position: 9
---

1、全量恢复，包括物理恢复和逻辑恢复，【gbasedbt】用户执行：
```
onbar -r
```
2、物理恢复，【gbasedbt】用户执行：
```
onbar -r -p
```
物理恢复仅恢复到数据备份的时间点，不包含数据备份之后的变更。

3、逻辑恢复，【gbasedbt】用户执行：
```
onbar -r -l
```
逻辑恢复包含物理备份之后的变更操作。

4、恢复到指定时间点
```
export GL_DATETIME=%iY-%m-%d %H:%M:%S
onbar -r -t "2024-04-28 01:00:00"
```