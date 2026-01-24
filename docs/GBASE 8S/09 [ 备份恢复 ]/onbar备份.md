---
hide_title: false
sidebar_position: 7
---

onbar备份策略一般由备份软件制定和发起。

以下示例为数据库主动发起：
1、全量备份（0级备份）,【gbasedbt】用户执行：
```
onbar -b -w -L 0
```
2、增量备份（1级备份）,【gbasedbt】用户执行：
```
onbar -b -w -L 1
```
3、增量备份（2级备份）,【gbasedbt】用户执行：
```
onbar -b -w -L 2
```
4、日志备份（日志归档）,【gbasedbt】用户执行：
```
onbar -b -l
```