本示例将【dbhost01】机器的【gbase01】实例备份恢复到【dbhost02】的【gbase02】实例。

1、配置nbu server，在【nbu server】使用【root】操作：
```
[root@nbuserver db]# cd /usr/openv/netbackup/db
[root@nbuserver db]# mkdir altnames
[root@nbuserver db]# cd altnames
[root@nbuserver altnames]# echo "dbhost01">dbhost02
```
2、配置环境变量，目标恢复机器【dbhost02】使用【gbasedbt】用户执行：
```
[gbasedbt@dbhost02 ~]$ export INFXBSA_CLIENT=dbhost01
```
3、恢复配置文件到目标恢复环境，目标恢复机器【dbhost02】使用【gbasedbt】用户执行：
```
[gbasedbt@dbhost02 ~]$ scp dbhost01:/opt/gbase/etc/ixbar.0 /opt/gbase/etc
[gbasedbt@dbhost02 ~]$ scp dbhost01:/opt/gbase/etc/oncfg_gbase01.0 /opt/gbase/etc/oncfg_gbase02.0
```
恢复环境实例名可与备份环境不一致，ixbar中实例名无需修改，如异机恢复，可不复制oncfg_xxx.0，该文件用于抢救未归档日志。

4、执行恢复，目标恢复机器【dbhost02】使用【gbasedbt】用户执行：
```
[gbasedbt@dbhost02 ~]$ onbar -r
```
参考文档【[GBase 8s 备份与恢复指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/Backup_and_Restore_Guide.pdf)】