持续日志恢复，用于在异机持续做日志同步恢复，数据库状态不可用，停止恢复后可用。
### 物理恢复
```
ontape -p
```
### 日志回放
```
ontape -l -C
```
### 停止CLR切换到静默模式
```
ontape -l -X
```
参考文档【[GBase 8s 备份与恢复指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Backup_and_Restore_Guide.pdf)】