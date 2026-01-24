1、执行block检查点，使数据库处于Blocked:ARCHIVE状态
```
onmode -c block
```
2、使用cp或其他命令复制数据库数据文件
3、unblock数据库
```
onmode -c unblock
```
4、将复制的数据文件传输到需要恢复的目标数据库环境
5、目标恢复环境抢救日志
```
ontape -S
```
6、执行外部恢复
```
ontape -p -e
```
7、恢复日志
```
ontape -l
```
参考文档【[GBase 8s 备份与恢复指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Backup_and_Restore_Guide.pdf)】