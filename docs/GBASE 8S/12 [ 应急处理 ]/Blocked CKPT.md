数据库处于【Blocked:CKPT】状态，表示当前数据库已被检查点阻塞。

【onstat -】命令查看数据库状态类似以下输出：
```
On-Line -- Up 00:08:21 -- 1686980 Kbytes
Blocked:CKPT
```
参考以下步骤处理

1、【gbasedbt】用户执行以下命令：
```
onstat -R
```
参考输出：
```
Buffer pool page size: 16384

8 buffer LRU queue pairs              priority levels
# f/m   pair total     % of    length       LOW      HIGH
 0 F       1250     100.0%     1250       1250          0
 1 m                  0.0%        0          0          0
 2 f       1250     100.0%     1250       1250          0
 3 m                  0.0%        0          0          0
 4 f       1250     100.0%     1250       1250          0
 5 m                  0.0%        0          0          0
 6 f       1250     100.0%     1250       1250          0
 7 m                  0.0%        0          0          0
 8 f       1250     100.0%     1250       1250          0
 9 m                  0.0%        0          0          0
10 f       1250     100.0%     1250       1250          0
11 m                  0.0%        0          0          0
12 f       1250     100.0%     1250       1250          0
13 m                  0.0%        0          0          0
14 f       1250     100.0%     1250       1250          0
15 m                  0.0%        0          0          0
0 dirty, 10000 queued, 10000 total, 16384 hash buckets, 16384 buffer size
start clean at  60.500% (of pair total) dirty, or 756 buffs dirty, stop at
  50.000%
```
重复执行【onstat -R】观察倒数第三行行首的dirty数据是否减少，如在减少，表明检查点正在刷新数据，无需干预，等待检查点执行完成即可。如该数据长时间未变化且数据库长时间处于【Blocked:CKPT】状态，参考【[重启数据库](./重启数据库)】