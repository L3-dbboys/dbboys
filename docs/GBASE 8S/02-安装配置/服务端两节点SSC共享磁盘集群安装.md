## 安装前准备
### 安装前准备
参考【[服务端安装前准备](服务端安装前准备.md)】
### 所需安装组件
共享磁盘数据库集群安装，除了安装数据库SERVER外，还需安装CSDK，CSDK中包含了共享磁盘集群仲裁切换的连接管理器组件，即需要安装以下两个安装包：
|命名规范|示例名|
|---|---|---|
|GBase8sV8.[x]_[AEE/TL]_[x.x.x_x]_[hashid]_[os]_[cpu].tar|GBase8sV8.8_AEE_3.3.0_2_36477d_RHEL6_x86_64.tar|
|clientsdk_[x.x.x_x]_[hashid]_[os]_[cpu].tar|clientsdk_3.3.0_2_36477d_RHEL6_x86_64.tar|
使用root用户将安装介质上传到/root目录，使用md5sum验证安装介质完整性，比对MD5值与官方提供的MD5值是否存在差异，如存在差异，介质已损坏，重新下载或传输安装介质：
```
md5sum GBase8sV8.8_AEE_3.3.0_2_36477d_RHEL6_x86_64.tar
md5sum clientsdk_3.3.0_2_36477d_RHEL6_x86_64.tar
```

## 主节点安装配置
### 主节点共享磁盘分区及绑定裸设备
共享磁盘集群的磁盘共享，使用存储设备同时挂载到主备节点，在主节点分区及绑定裸设备，再备节点同步分区及绑定裸设备，实现共享，因此共享磁盘集群主备节点的磁盘无需格式化文件系统，无需挂载。
root用户执行以下命令，对磁盘进行分区，parted分区大小比数据库空间实际计算大小可能会小，在分区时建议对每个空间所需大小上浮10%左右，不够1G以1G算，每个分区为数据库的一个chunk，commdbschk001用于创建集群心跳空间，不小于1G：
```
parted /dev/mapper/mpatha mklabel gpt
parted /dev/mapper/mpatha mkpart rootdbschk001 0 3G
parted /dev/mapper/mpatha mkpart plogdbschk001 3G 9G
parted /dev/mapper/mpatha mkpart llogdbschk001 9G 20G
parted /dev/mapper/mpatha mkpart commdbschk001 20G 22G
parted /dev/mapper/mpatha mkpart tempdbs01chk001 22G 33G
parted /dev/mapper/mpatha mkpart tempdbs02chk001 33G 44G
parted /dev/mapper/mpatha mkpart tempdbs03chk001 44G 55G
parted /dev/mapper/mpatha mkpart tempdbs04chk001 55G 66G
parted /dev/mapper/mpatha mkpart datadbs01chk001 66G 176G
parted /dev/mapper/mpatha mkpart sbspace01chk001 176G 187G
```
绑定裸设备，root用户执行（RUN后面的raw路径以实际情况而定，可能是/usr/sbin/raw）：
```
cat <<! >/usr/lib/udev/rules.d/60-raw-gbase.rules
ACTION=="add", KERNEL=="/dev/mapper/mpatha1", RUN+="/usr/bin/raw /dev/raw/raw1 %N"
ACTION=="add", KERNEL=="/dev/mapper/mpatha2", RUN+="/usr/bin/raw /dev/raw/raw2 %N"
ACTION=="add", KERNEL=="/dev/mapper/mpatha3", RUN+="/usr/bin/raw /dev/raw/raw3 %N"
ACTION=="add", KERNEL=="/dev/mapper/mpatha4", RUN+="/usr/bin/raw /dev/raw/raw4 %N"
ACTION=="add", KERNEL=="/dev/mapper/mpatha5", RUN+="/usr/bin/raw /dev/raw/raw5 %N"
ACTION=="add", KERNEL=="/dev/mapper/mpatha6", RUN+="/usr/bin/raw /dev/raw/raw6 %N"
ACTION=="add", KERNEL=="/dev/mapper/mpatha7", RUN+="/usr/bin/raw /dev/raw/raw7 %N"
ACTION=="add", KERNEL=="/dev/mapper/mpatha8", RUN+="/usr/bin/raw /dev/raw/raw8 %N"
ACTION=="add", KERNEL=="/dev/mapper/mpatha9", RUN+="/usr/bin/raw /dev/raw/raw9 %N"
ACTION=="add", KERNEL=="/dev/mapper/mpatha10", RUN+="/usr/bin/raw /dev/raw/raw10 %N"
KERNEL=="raw1", OWNER="gbasedbt" GROUP="gbasedbt", MODE="0660"
KERNEL=="raw2", OWNER="gbasedbt" GROUP="gbasedbt", MODE="0660"
KERNEL=="raw3", OWNER="gbasedbt" GROUP="gbasedbt", MODE="0660"
KERNEL=="raw4", OWNER="gbasedbt" GROUP="gbasedbt", MODE="0660"
KERNEL=="raw5", OWNER="gbasedbt" GROUP="gbasedbt", MODE="0660"
KERNEL=="raw6", OWNER="gbasedbt" GROUP="gbasedbt", MODE="0660"
KERNEL=="raw7", OWNER="gbasedbt" GROUP="gbasedbt", MODE="0660"
KERNEL=="raw8", OWNER="gbasedbt" GROUP="gbasedbt", MODE="0660"
KERNEL=="raw9", OWNER="gbasedbt" GROUP="gbasedbt", MODE="0660"
KERNEL=="raw10", OWNER="gbasedbt" GROUP="gbasedbt", MODE="0660"
!
```
重新加载配置文件，【root】用户执行：
```
udevadm control --reload-rules
```
重新加载磁盘，root用户执行：
```
partprobe /dev/mapper/mpatha
```
执行以下命令显示裸设备，即表示绑定成功，root用户执行：
```
raw -qa
```
### 主节点挂载临时空间盘/data
集群临时空间及page空间不能使用裸设备，集群每个节点需单独挂载一块数据盘，用于存储集群临时空间，共享磁盘链接等，大小不小于单个临时空间大小的1倍。该盘可以是共享磁盘的一个分区，或使用单独一块磁盘挂载，如使用共享磁盘的一个分区挂载，【root】用户执行：
```shell
parted /dev/mapper/mpatha mkpart sbspace01chk001 187G 207G  #用于主节点
parted /dev/mapper/mpatha mkpart sbspace01chk001 207G 227G  #用于备节点
mkfs.xfs /dev/mapper/mpatha11
blkid /dev/mapper/mpatha11  #158d0f4d-5606-4546-9b0a-6bddd97b1aeb
mkdir /data
mount /dev/mapper/mpatha11 /data
mkdir /data/gbase
chown gbasedbt:gbasedbt /data/gbase
echo "UUID=158d0f4d-5606-4546-9b0a-6bddd97b1aeb /data xfs     defaults        0 0>>/etc/fstab
```
### 主节点创建裸设备链接
【gbasedbt】用户执行：
```
cd /data/gbase
ln -s /dev/raw/raw1 rootdbschk001
ln -s /dev/raw/raw2 plogdbschk001
ln -s /dev/raw/raw3 llogdbschk001
ln -s /dev/raw/raw4 commdbschk001
ln -s /dev/raw/raw5 tempdbs01chk001
ln -s /dev/raw/raw6 tempdbs02chk001
ln -s /dev/raw/raw7 tempdbs03chk001
ln -s /dev/raw/raw8 tempdbs04chk001
ln -s /dev/raw/raw9 datadbs01chk001
ln -s /dev/raw/raw10 sbspace01chk001
```
### 主节点SERVER安装
参考【[服务端单节点安装](服务端单节点安装.md)】
2.1-2.3、3.3-3.12安装配置主节点，其中3.6-3.11创建空间相关章节，由于已使用裸设备链接，不再需要在创建空间前创建文件及修改权限，其他操作无需变动。
### 主节点关闭数据库
由于计划安装CSDK到server同一目录【/opt/gbase】，安装前关闭server，【gbasedbt】用户执行：
```
onmode -ky
```
### 主节点CSDK安装
在主节点安装CSDK到server同一路径【/opt/gbase】，【root】用户执行：解压
```
tar -xvf clientsdk_3.3.0_2_36477d_RHEL6_x86_64.tar
```
【root】用户执行：静默安装
```
./installclientsdk -i silent -DLICENSE_ACCEPTED=TRUE -DUSER_INSTALL_DIR=/opt/gbase
```
### 主节点配置信任
集群无心跳IP配置：如备节点的服务IP为10.10.10.2，【root】用户执行：
```
#在/etc/hosts中添加备机机器名
echo "10.10.10.2      dbhost02">>/etc/hosts
#在/etc/hosts.equiv中添加信任
echo "dbhost02      gbasedbt">>/etc/hosts.equiv
```
集群有心跳IP配置：如备机心跳IP为192.168.1.2，主节点配置对备节点心跳IP信任即可，【root】用户执行：
```
#在/etc/hosts中添加备机心跳机器名
echo "192.168.1.2      dbhahost02">>/etc/hosts
#在/etc/hosts.equiv中添加信任
echo "dbhahost02      gbasedbt">>/etc/hosts.equiv
```
### 主节点onconfig参数调整
设置【SDS_TEMPDBS】临时空间，该空间不能使用裸设备，同时页大小与数据库空间页大小保持一致，大小不小于【DBSPACETEMP】参数单个大小，如以下配置为16k页大小20G的集群临时空间，集群如发生切换，数据库使用此临时空间替代【DBSPACETEMP】参数配置的临时空间，配置DRAUTO为3，使用连接管理器CM自动切换集群，【gbasedbt】用户执行：
```
sed -i "s#^SDS_TEMPDBS.*#SDS_TEMPDBS             sdstmpdbs,/data/gbase/sdstmpdbs,16,0,20480000#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^SDS_PAGING.*#SDS_PAGING              /data/gbase/page1,/data/gbase/page2#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^SDS_ALTERNATE.*#SDS_ALTERNATE             ssc_alt_comm#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^DRAUTO.*#DRAUTO             3#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^SDS_FLOW_CONTROL.*#SDS_FLOW_CONTROL        -1#g" $GBASEDBTDIR/etc/$ONCONFIG
```
如集群配置了心跳IP，增加修改心跳别名，【gbasedbt】用户执行：
```
sed -i "s#^DBSERVERALIASES.*#DBSERVERALIASES ha_pri#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^HA_ALIAS.*#HA_ALIAS ha_pri#g" $GBASEDBTDIR/etc/$ONCONFIG
```
### 主节点配置sqlhosts文件
如无集群心跳，增加集群实例及cm实例信息到sqlhosts文件，【gbasedbt】用户执行：
```
cat >$GBASEDBTSQLHOSTS <<EOF
db_group        group   -       -
gbase01      onsoctcp        10.10.10.1      9088    g=db_group
gbase02      onsoctcp        10.10.10.2      9088    g=db_group
cm_read         group   -       -
oltp_read1      onsoctcp        10.10.10.1      9200    g=cm_read
oltp_read2      onsoctcp        10.10.10.2      9200    g=cm_read
cm_update       group   -       -
oltp_update1    onsoctcp        10.10.10.1      9300    g=cm_update
oltp_update2    onsoctcp        10.10.10.2      9300    g=cm_update
EOF
```
如配置了集群心跳，增加集群实例、心跳实例及cm实例信息到sqlhosts文件，【gbasedbt】用户执行：
```
cat >$GBASEDBTSQLHOSTS <<EOF
db_group        group   -       -
gbase01      onsoctcp        10.10.10.1      9088    g=db_group
gbase02      onsoctcp        10.10.10.2      9088    g=db_group
ha_group        group   -       -
ha_pri  onsoctcp        192.168.1.1      9099    g=ha_group
ha_ssc  onsoctcp        192.168.1.2      9099    g=ha_group
cm_read         group   -       -
oltp_read1      onsoctcp        10.10.10.1      9200    g=cm_read
oltp_read2      onsoctcp        10.10.10.2      9200    g=cm_read
cm_update       group   -       -
oltp_update1    onsoctcp        10.10.10.1      9300    g=cm_update
oltp_update2    onsoctcp        10.10.10.2      9300    g=cm_update
EOF
```
### 主节点启动数据库
【gbasedbt】用户执行：
```
oninit -v
```
### 主节点设置为集群主节点
如未配置集群心跳，【gbasedbt】用户执行：
```
onmode -d set SDS primary gbase01
```
如配置了集群心跳实例，配置集群主节点为心跳实例，【gbasedbt】用户执行：
```
onmode -d set SDS primary ha_pri
```
### 主节点创新心跳空间
如下示例，创建1GB磁盘心跳空间，并切换一个日志生效，【gbasedbt】用户执行：
```
onspaces -c -b ssc_alt_comm -p /data/gbase/commdbschk001 -g 16k -o 0 -s 1000000
```
切换一个日志，使空间生效，【gbasedbt】用户执行：
```
onmode -l
```
### 主节点配置CM1
连接管理器CM为单独组件，不依赖数据库服务，可独立运行于仲裁服务器，为了节省服务器资源，CM与数据库SERVER运行于同一服务器，CM以组的形式和数据库集群交叉部署，如仲裁CM和主节点配置为不位于同一服务器，确保某一服务器故障集群可正常切换，由于CM和数据库SERVER运行于同一服务器，不再单独配置sqlhosts文件，与server共用同一sqlhosts文件，该文件在前述章节已配置。

设置环境变量，【gbasedbt】用户执行：
```
#设置环境变量
echo "export CMCONFIG=\$GBASEDBTDIR/etc/cmsm.cm1" >>~/.bash_profile
source ~/.bash_profile
```
创建配置文件，【gbasedbt】用户执行：
```
#创建配置文件$GBASEDBTDIR/etc/cmsm.cm1：
NAME		CM1
LOGFILE		${GBASEDBTDIR}/tmp/cm1.log
SQLHOSTS	LOCAL
LOG	1
DEBUG	0

CLUSTER	CLUSTER1
{
  GBASEDBTSERVER	db_group
  SLA oltp_update1 	       DBSERVERS=PRI	WORKERS=8	MODE=redirect	USEALIASES=OFF
  SLA oltp_read1 	       DBSERVERS=SDS	WORKERS=8	MODE=redirect	USEALIASES=OFF
  FOC ORDER=ENABLED TIMEOUT=10 RETRY=3 PRIORITY=2
}
```
### 主节点启动CM1
【gbasedbt】用户执行：
```
oncmsm
```
如显示“Connection Manager started successfully”表示启动成功，可执行【onstat -g cmsm】查看数据库是否已连接到CM。
### 主节点备份安装目录
备份安装目录，用于备机复制安装，【root】用户执行：
```
cd /opt
tar -cvf gbase.tar gbase
```
## 备节点安装配置
### 备节点共享磁盘配置
核对磁盘ID是否与主节点一致，确认为同一块磁盘，【root】用户执行：
```
blkid /dev/mapper/mpatha
```
绑定裸设备，从主节点复制配置文件，【root】用户执行：
```
cd /usr/lib/udev/rules.d
scp dbhost01:/usr/lib/udev/rules.d/60-raw-gbase.rules .
```
重新加载配置文件，【root】用户执行：
```
udevadm control --reload-rules
```
重新加载磁盘，【root】用户执行：
```
partprobe /dev/mapper/mpatha
```
执行以下命令显示裸设备，即表示绑定成功，【root】用户执行：
```
raw -qa
```
### 备节点挂载临时空间磁盘/data
```
mkfs.xfs /dev/mapper/mpatha12
blkid /dev/mapper/mpatha12  #215249ef-bbb4-44a2-a7ed-c5d6f121cc6a
mkdir /data
mount /dev/mapper/mpatha12 /data
mkdir /data/gbase
chown gbasedbt:gbasedbt /data/gbase
echo "UUID=215249ef-bbb4-44a2-a7ed-c5d6f121cc6a /data xfs     defaults        0 0>>/etc/fstab
```
### 备节点创建裸设备链接
【gbasedbt】用户执行：
```
cd /data/gbase
ln -s /dev/raw/raw1 rootdbschk001
ln -s /dev/raw/raw2 plogdbschk001
ln -s /dev/raw/raw3 llogdbschk001
ln -s /dev/raw/raw4 commdbschk001
ln -s /dev/raw/raw5 tempdbs01chk001
ln -s /dev/raw/raw6 tempdbs02chk001
ln -s /dev/raw/raw7 tempdbs03chk001
ln -s /dev/raw/raw8 tempdbs04chk001
ln -s /dev/raw/raw9 datadbs01chk001
ln -s /dev/raw/raw10 sbspace01chk001
```
### 备节点安装SERVER及CSDK
创建用户，【root】用户执行：
```
groupadd gbasedbt -g 500
useradd gbasedbt -u 500 -g gbasedbt -m -d /home/gbasedbt
echo "GBase123" | passwd --stdin gbasedbt
```
设置用户环境变量，【gbasedbt】用户执行：
```
#修改INSTANCE为实例名
INSTANCE=gbase02
cat >>.bash_profile << EOF
export GBASEDBTDIR=/opt/gbase
export GBASEDBTSERVER=${INSTANCE}
export ONCONFIG=onconfig.${INSTANCE}
export GBASEDBTSQLHOSTS=\$GBASEDBTDIR/etc/sqlhosts.${INSTANCE}
export DB_LOCALE=zh_CN.utf8
export CLIENT_LOCALE=zh_CN.utf8
export GL_USEGLU=1
export PATH=\$GBASEDBTDIR/bin:/usr/bin:\${PATH}:.
EOF
source .bash_profile
```
复制主节点安装目录到备机，【root】用户执行：
```
scp dbhost01:/opt/gbase.tar /opt/
cd /opt
tar -xvf gbase.tar
```
### 备节点配置信任
集群无心跳IP配置：如主节点的服务IP为10.10.10.1，【root】用户执行：
```
#在/etc/hosts中添加主节点机器名
echo "10.10.10.1      dbhost01">>/etc/hosts
#在/etc/hosts.equiv中添加信任
echo "dbhost01      gbasedbt">>/etc/hosts.equiv
```
集群有心跳IP配置：如主机心跳IP为192.168.1.1，配置对主节点心跳IP信任即可，【root】用户执行：
```
#在/etc/hosts中添加主节点心跳机器名
echo "192.168.1.1      dbhahost01">>/etc/hosts
#在/etc/hosts.equiv中添加信任
echo "dbhahost01      gbasedbt">>/etc/hosts.equiv
```
### 备节点配置文件调整
【gbasedbt】用户执行：
```
mv $GBASEDBTDIR/etc/onconfig.gbase01 $GBASEDBTDIR/etc/$ONCONFIG
mv $GBASEDBTDIR/etc/sqlhosts.gbase01 $GBASEDBTSQLHOSTS
sed -i s/gbase01/gbase02/g $GBASEDBTDIR/etc/$ONCONFIG
sed -i s/ha_pri/ha_ssc/g $GBASEDBTDIR/etc/$ONCONFIG
```
### 备节点启动数据库
【gbasedbt】用户执行：
```
oninit -v
```
### 备节点配置CM2
CM2优先级设置为1，高于CM1，为仲裁CM。
设置环境变量，【gbasedbt】用户执行：
```
#设置环境变量
echo "export CMCONFIG=\$GBASEDBTDIR/etc/cmsm.cm2" >>~/.bash_profile
source ~/.bash_profile
```
创建配置文件，【gbasedbt】用户执行：
```
#创建配置文件$GBASEDBTDIR/etc/cmsm.cm2：
NAME		CM2
LOGFILE		${GBASEDBTDIR}/tmp/cm2.log
SQLHOSTS	LOCAL
LOG	1
DEBUG	0

CLUSTER	CLUSTER2
{
  GBASEDBTSERVER	db_group
  SLA oltp_update2 	       DBSERVERS=PRI	WORKERS=8	MODE=redirect	USEALIASES=OFF
  SLA oltp_read2 	       DBSERVERS=SDS	WORKERS=8	MODE=redirect	USEALIASES=OFF
  FOC ORDER=ENABLED TIMEOUT=10 RETRY=3 PRIORITY=1
}
```
### 备节点启动CM2
【gbasedbt】用户执行：
```
oncmsm
```
如显示“Connection Manager started successfully”表示启动成功，可执行【onstat -g cmsm】查看数据库是否已连接到CM。
