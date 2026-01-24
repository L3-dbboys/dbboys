---
hide_title: false
sidebar_position: 1
---

### 配置备份
挂载备份盘（备份盘尽量不与数据盘存放同一存储设备），【root】用户执行，UUID修改为blkid命令的输出ID，如没有单独备份盘，建议在数据盘【/data】下创建目录存放备份数据：
```shell
#有备份盘，参考以下配置命令：
mkfs.xfs /dev/vdc
blkid /dev/vdc
mkdir /backup
mount /dev/vdc /backup
chown gbasedbt:gbasedbt /backup
chmod 775 /backup
echo " UUID=158d0f4d-5606-4546-9b0a-6bddd97b1aeb /data xfs     defaults        0 0>>/etc/fstab
#无单独备份盘，参考以下配置命令
mdir -p /data/backup
chown gbasedbt:gbasedbt /data/backup
chmod 775 /data/backup
ln -s /data/backup /backup
```
【gbasedbt】用户调整备份参数：
```shell
BACKUPDIR=/backup
onmode -wf TAPEDEV=$BACKUPDIR
onmode -wf LTAPEDEV=$BACKUPDIR
onmode -wf TAPEBLK=2048
onmode -wf LTAPEBLK=2048
```
### 配置自动备份
1、配置定时物理备份，【gbasedbt】用户执行以下命令生成备份脚本：
```shell
cat <<! >/opt/gbase/etc/backup.sh
#!/bin/bash
. /home/gbasedbt/.bash_profile
onstat - |grep "On-Line" >/dev/null
if [ \$? -ne 1 ]
then
DATE=\`date\`
echo "Level 0 backup of "\$GBASEDBTSERVER" strat at "\$DATE
ontape -s -L 0
DATE=\`date\`
echo "Level 0 backup of "\$GBASEDBTSERVER" completed at "\$DATE
find /backup -mtime +7 -type f ! -name *.sh ! -name *.log |xargs rm -rf
fi
exit 0
!
chmod 775 /opt/gbase/etc/backup.sh
```
【gbasedbt】用户执行【crontab -e】部署以下定时任务，每天0点执行0级备份，删除7天以前备份：
```shell
0 0 * * * /opt/gbase/etc/backup.sh >>/opt/gbase/tmp/backup.log 2>&1
```
如【gbasedbt】用户无crontab权限，使用【root】添加：【echo "gbasedbt">>/etc/cron.allow】

2、配置日志自动备份
【gbasedbt】用户执行：
```shell
onmode -wf LTAPEDEV=/dev/tapedev #设置为非/dev/null
sed -i "s#^BACKUP_CMD.*#BACKUP_CMD=\"ontape -a -d\" #g" $GBASEDBTDIR/etc/log_full.sh
onmode -wf ALARMPROGRAM=$GBASEDBTDIR/etc/log_full.sh
```
### 手动备份
1、执行全量物理备份（0级）
```
ontape -s -L 0
```
备份文件存储于TAPEDEV 参数指定的目录，命名规则为“主机名_实例编号_L0”，如 dbhost01_0_L0

2、执行增量物理备份（1级别）
```
ontape -s -L 1
```
备份文件存储于TAPEDEV 参数指定的目录，命名规则为“主机名_实例编号_L1”，如 dbhost01_0_L1

3、执行增量物理备份（2级别）
```
ontape -s -L 2
```
备份文件存储于TAPEDEV 参数指定的目录，命名规则为“主机名_实例编号_L2”，如 dbhost01_0_L2

4、备份逻辑日志
```
ontape -a -d
```
备份文件存储于LTAPEDEV 参数指定的目录，命名规则为“主机名_实例编号_Logxxxxxxxxxx”，如 dbhost01_0_0000000012