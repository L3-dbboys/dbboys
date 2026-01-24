## 安装前准备
### 安装前准备
参考【[服务端两节点SSC共享磁盘集群安装](服务端两节点SSC共享磁盘集群安装.md)】安装两节点SSC共享磁盘集群，在此集群中添加RHAC节点即可。

### 集群架构图
SSC+RHAC集群为GBase 8s两地三中心最佳实践方案，SSC集群集群切换后可避免数据丢失，RHAC为完全异步集群，保证数据同步的同时，最大限度降低对主节点影响，其架构图如下图：
![img13.png](img/img13.png)
该集群共三个数据库节点，其中共享磁盘SSC集群两个节点位于中心机房，RHAC节点位于同城灾备或异地灾备机房，同时配置3个连接管理器实例，组成连接管理器高可用组，其中cm2位于SSC备节点，并设置为最高优先级仲裁CM，如cm2不可用，次高优先级cm1将提升为仲裁CM。
## SSC集群节点配置
### SSC集群节点配置信任
集群无心跳IP配置：如RHAC备节点的服务IP为【10.10.10.3】，【root】用户分别在SSC主备节点执行：
```
#在/etc/hosts中添加备机机器名
echo "10.10.10.3      dbhost03">>/etc/hosts
#在/etc/hosts.equiv中添加信任
echo "dbhost03      gbasedbt">>/etc/hosts.equiv
```
集群有心跳IP配置：如RHAC备机心跳IP为【192.168.1.3】，主节点配置对备节点心跳IP信任即可，【root】用户分别在SSC主备节点执行：
```
#在/etc/hosts中添加备机心跳机器名
echo "192.168.1.3      dbhahost03">>/etc/hosts
#在/etc/hosts.equiv中添加信任
echo "dbhahost03      gbasedbt">>/etc/hosts.equiv
```
### SSC集群节点onconfig参数调整
修改LOG_INDEX_BUILDS为1，RSS_FLOW_CONTROL为-1，【gbasedbt】用户分别在SSC集群主备节点执行：
```
onmode -wf LOG_INDEX_BUILDS=1
onmode -wf RSS_FLOW_CONTROL =-1
```
### 主节点配置sqlhosts文件
如无集群心跳，增加集群实例及cm实例信息到sqlhosts文件，【gbasedbt】用户分别在SSC主备节点执行（增加RHAC节点信息及cm3信息）：
```
cat >$GBASEDBTSQLHOSTS <<EOF
db_group        group   -       -
gbase01      onsoctcp        10.10.10.1      9088    g=db_group
gbase02      onsoctcp        10.10.10.2      9088    g=db_group
gbase03      onsoctcp        10.10.10.3      9088    g=db_group
cm_read         group   -       -
oltp_read1      onsoctcp        10.10.10.1      9200    g=cm_read
oltp_read2      onsoctcp        10.10.10.2      9200    g=cm_read
oltp_read3      onsoctcp        10.10.10.3      9200    g=cm_read
cm_update       group   -       -
oltp_update1    onsoctcp        10.10.10.1      9300    g=cm_update
oltp_update2    onsoctcp        10.10.10.2      9300    g=cm_update
oltp_update3    onsoctcp        10.10.10.3      9300    g=cm_update
EOF
```
如配置了集群心跳，增加集群实例、心跳实例及cm实例信息到sqlhosts文件，【gbasedbt】用户分别在SSC集群主备节点执行（增加RHAC节点信息）：
```
cat >$GBASEDBTSQLHOSTS <<EOF
db_group        group   -       -
gbase01      onsoctcp        10.10.10.1      9088    g=db_group
gbase02      onsoctcp        10.10.10.2      9088    g=db_group
gbase03      onsoctcp        10.10.10.3      9088    g=db_group
ha_group        group   -       -
ha_pri  onsoctcp        192.168.1.1      9099    g=ha_group
ha_ssc  onsoctcp        192.168.1.2      9099    g=ha_group
ha_rhac  onsoctcp        192.168.1.3      9099    g=ha_group
cm_read         group   -       -
oltp_read1      onsoctcp        10.10.10.1      9200    g=cm_read
oltp_read2      onsoctcp        10.10.10.2      9200    g=cm_read
oltp_read3      onsoctcp        10.10.10.3      9200    g=cm_read
cm_update       group   -       -
oltp_update1    onsoctcp        10.10.10.1      9300    g=cm_update
oltp_update2    onsoctcp        10.10.10.2      9300    g=cm_update
oltp_update3    onsoctcp        10.10.10.3      9300    g=cm_update
EOF
```
### SSC集群主节点增加RHAC节点
如未配置集群心跳，【gbase03】为RHAC备节点实例名，【gbasedbt】用户在主节点执行：
```
onmode -d add RSS gbase03
```
如配置了集群心跳实例，配置集群主节点为心跳实例，【ha_rhac】为备节点心跳实例，【gbasedbt】用户执行：
```
onmode -d add RSS ha_rhac
```
### SSC集群主节点备份安装目录
备份安装目录，用于RHAC节点复制安装，【root】用户执行：
```
cd /opt
tar -cvf gbase.tar gbase
```
## RHAC备节点安装配置
### RHAC备节点安装SERVER及CSDK
创建用户，【root】用户执行：
```
groupadd gbasedbt -g 500
useradd gbasedbt -u 500 -g gbasedbt -m -d /home/gbasedbt
echo "GBase123" | passwd --stdin gbasedbt
```
设置用户环境变量，【gbasedbt】用户执行：
```
#修改INSTANCE为实例名
INSTANCE=gbase03
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
### RHAC备节点配置信任
集群无心跳IP配置：如SSC集群节点的服务IP为10.10.10.1、10.10.10.2，【root】用户执行：
```
#在/etc/hosts中添加主节点机器名
echo "10.10.10.1      dbhost01">>/etc/hosts
echo "10.10.10.2      dbhost02">>/etc/hosts
#在/etc/hosts.equiv中添加信任
echo "dbhost01      gbasedbt">>/etc/hosts.equiv
echo "dbhost02      gbasedbt">>/etc/hosts.equiv
```
集群有心跳IP配置：如SSC集群节点心跳IP为192.168.1.1、192.168.1.2，【root】用户执行：
```
#在/etc/hosts中添加主节点心跳机器名
echo "192.168.1.1      dbhahost01">>/etc/hosts
echo "192.168.1.2      dbhahost01">>/etc/hosts
#在/etc/hosts.equiv中添加信任
echo "dbhahost01      gbasedbt">>/etc/hosts.equiv
echo "dbhahost02      gbasedbt">>/etc/hosts.equiv
```
### RHAC备节点配置文件调整
【gbasedbt】用户执行：
```
mv $GBASEDBTDIR/etc/onconfig.gbase01 $GBASEDBTDIR/etc/$ONCONFIG
mv $GBASEDBTDIR/etc/sqlhosts.gbase01 $GBASEDBTSQLHOSTS
sed -i "s#gbase01#gbase03#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s/ha_pri#ha_rhac#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^SDS_ENABLE.*#SDS_ENABLE#g" $GBASEDBTDIR/etc/$ONCONFIG
```
### RHAC备节点恢复数据
在主节点，使用【gbasedbt】用户执行：
```
ontape -s -L 0 -t STDIO -F |ssh dbhost03 ". /home/gbasedbt/.bash_profile;ontape -p -t STDIO"
```
### RHAC备节点设置为集群备节点
如没有心跳网络，配置当前实例名为备节点，【gbase01】为主节点实例名，使用【gbasedbt】用户执行：
```
onmode -d RSS gbase01
```
如配置了心跳网络，设置心跳实例为备节点，【ha_pri】为主节点心跳实例，使用【gbasedbt】用户执行：
```
onmode -d RSS ha_pri
```
### RHAC备节点配置CM3
设置环境变量，【gbasedbt】用户执行：
```
#设置环境变量
echo "export CMCONFIG=\$GBASEDBTDIR/etc/cmsm.cm3" >>~/.bash_profile
source ~/.bash_profile
```
创建配置文件，【gbasedbt】用户执行：
```
#创建配置文件$GBASEDBTDIR/etc/cmsm.cm3：
NAME		CM3
LOGFILE		${GBASEDBTDIR}/tmp/cm3.log
SQLHOSTS	LOCAL
LOG	1
DEBUG	0

CLUSTER	CLUSTER3
{
  GBASEDBTSERVER	db_group
  SLA oltp_update3 	       DBSERVERS=PRI	WORKERS=8	MODE=redirect	USEALIASES=OFF
  SLA oltp_read3 	       DBSERVERS=SDS	WORKERS=8	MODE=redirect	USEALIASES=OFF
  FOC ORDER=ENABLED TIMEOUT=10 RETRY=3 PRIORITY=3
}
```
### RHAC备节点启动CM3
【gbasedbt】用户执行：
```
oncmsm
```
如显示“Connection Manager started successfully”表示启动成功，可执行【onstat -g cmsm】查看数据库是否已连接到CM。
