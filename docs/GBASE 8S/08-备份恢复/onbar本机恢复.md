### 示例1-全量恢复，包括物理恢复和逻辑恢复
```
onbar -r
```
### 示例2-物理恢复
```
onbar -r -p
```
物理恢复仅恢复到数据备份的时间点，不包含数据备份之后的变更。

### 示例3-逻辑恢复
```
onbar -r -l
```
逻辑恢复包含物理备份之后的变更操作。

### 示例4-恢复到指定时间点
```
export GL_DATETIME=%iY-%m-%d %H:%M:%S
onbar -r -t "2024-04-28 01:00:00"
```
参考文档【[GBase 8s 备份与恢复指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Backup_and_Restore_Guide.pdf)】