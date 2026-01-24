## 安装前准备
### 安装前准备
	参考【[LINUX安装前准备](LINUX安装前准备.md)】
### 所需安装组件
	HAC集群只支持一个备节点，可不依赖连接管理器自行切换，即需要安装以下一个安装包：
命名规则：【GBase8sV8.[x]_[AEE|TL]_[x.x.x_x]_[hashid]_[os]_[cpu].tar】 如：GBase8sV8.8_AEE_3.3.0_2_36477d_RHEL6_x86_64.tar
使用root用户将安装介质上传到/root目录，使用md5sum验证安装介质完整性，比对MD5值与官方提供的MD5值是否存在差异，如存在差异，介质已损坏，重新下载或传输安装介质：
```
md5sum GBase8sV8.8_AEE_3.3.0_2_36477d_RHEL6_x86_64.tar
```
## 主节点安装配置
### 主节点SERVER安装
	参考【[LINUX单机安装](LINUX单机安装.md)】
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
	配置DRAUTO为2，数据库集群自动切换，【gbasedbt】用户执行：
```
sed -i "s#^DRAUTO.*#DRAUTO             2#g" $GBASEDBTDIR/etc/$ONCONFIG
```
	如集群配置了心跳IP，增加修改心跳别名，【gbasedbt】用户执行：
```
sed -i "s#^DBSERVERALIASES.*#DBSERVERALIASES ha_pri#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^HA_ALIAS.*#HA_ALIAS ha_pri#g" $GBASEDBTDIR/etc/$ONCONFIG
```
### 主节点配置sqlhosts文件
	如无集群心跳，增加备节点实例信息到sqlhosts文件，【gbasedbt】用户执行：
```
cat >$GBASEDBTSQLHOSTS <<EOF
db_group        group   -       -
gbase01      onsoctcp        10.10.10.1      9088    g=db_group
gbase02      onsoctcp        10.10.10.2      9088    g=db_group
EOF
```
	如配置了集群心跳，增加集群实例、心跳实例信息到sqlhosts文件，【gbasedbt】用户执行：
```
cat >$GBASEDBTSQLHOSTS <<EOF
db_group        group   -       -
gbase01      onsoctcp        10.10.10.1      9088    g=db_group
gbase02      onsoctcp        10.10.10.2      9088    g=db_group
ha_group        group   -       -
ha_pri  onsoctcp        192.168.1.1      9099    g=ha_group
ha_hac  onsoctcp        192.168.1.2      9099    g=ha_group
EOF
```
### 主节点重启数据库
	【gbasedbt】用户执行：
```
onmode -ky
oninit -v
```
### 主节点设置为集群主节点
	如未配置集群心跳，【gbasedbt】用户执行：
```
onmode -d primary gbase02
```
	如配置了集群心跳实例，配置集群主节点为心跳实例，【gbasedbt】用户执行：
```
onmode -d primary ha_hac
```
### 主节点备份安装目录
	备份安装目录，用于备机复制安装，【root】用户执行：
```
cd /opt
tar -cvf gbase.tar gbase
```
## 备节点安装配置
### 备节点安装SERVER
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
	集群有心跳IP配置：如主节点心跳IP为192.168.1.1，配置对主节点心跳IP信任即可，【root】用户执行：
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
### 备节点恢复数据
	在主节点，使用【gbasedbt】用户执行：
```
ontape -s -L 0 -t STDIO -F |ssh dbhost02 ". /home/gbasedbt/.bash_profile;ontape -p -t STDIO"
```
### 备节点设置为集群备节点
	如没有心跳网络，配置当前实例名为备节点，【gbase01】为主节点实例名，使用【gbasedbt】用户执行：
```
onmode -d secondary gbase01
```
	如配置了心跳网络，设置心跳实例为备节点，【ha_pri】为主节点心跳实例，使用【gbasedbt】用户执行：
```
onmode -d secondary ha_pri
```