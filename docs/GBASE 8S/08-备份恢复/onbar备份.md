onbar备份策略一般由备份软件制定和发起。以下示例由数据库主动发起：

### 示例1-全量备份（0级备份）
```
onbar -b -w -L 0
```
### 示例2-增量备份（1级备份）
```
onbar -b -w -L 1
```
### 示例3-增量备份（2级备份）
```
onbar -b -w -L 2
```
### 示例4-日志备份（日志归档）
```
onbar -b -l
```
参考文档【[GBase 8s 备份与恢复指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Backup_and_Restore_Guide.pdf)】