### 修改配置文件参数
```
BACKUP_FILTER /usr/bin/gzip
RESTORE_FILTER /usr/bin/gunzip
```

### 修改LTAPESIZE
```
onmonde -wf LTAPESIZE=2048000
```
### 时间与空间对比
|场景|备份时间|占用空间|
|---|---|---|
|不压缩|13分钟|120G|
|压缩|48分钟|12G|
参考文档【[GBase 8s 备份与恢复指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Backup_and_Restore_Guide.pdf)】