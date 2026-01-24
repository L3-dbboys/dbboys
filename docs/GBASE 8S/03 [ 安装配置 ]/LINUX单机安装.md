---
hide_title: false
sidebar_position: 4
---
## 1、安装前准备
### 1.1、安装前准备
参考【[LINUX安装前准备](LINUX安装前准备.md)】
### 1.2、所需安装组件
HAC集群只支持一个备节点，可不依赖连接管理器自行切换，即需要安装以下一个安装包：
<table>
  <tr>
    <td>`GBase8sV8.[x]_[AEE|TL]_[x.x.x_x]_[hashid]_[os]_[cpu].tar`】</td>
    <td>如：GBase8sV8.8_AEE_3.3.0_2_36477d_RHEL6_x86_64.tar</td>
  </tr>
</table>

使用root用户将安装介质上传到/root目录，使用md5sum验证安装介质完整性，比对MD5值与官方提供的MD5值是否存在差异，如存在差异，介质已损坏，重新下载或传输安装介质：
```
md5sum GBase8sV8.8_AEE_3.3.0_2_36477d_RHEL6_x86_64.tar
```
## 2、安装数据库
### 2.1、创建数据库用户【gbasedbt】
使用【root】创建【gbaesdbt】用户，默认密码【GBase123】，部分系统存在安全加固策略，需使用符合规则的密码：
```
groupadd gbasedbt -g 500
useradd gbasedbt -u 500 -g gbasedbt -m -d /home/gbasedbt
echo "GBase123" | passwd --stdin gbasedbt
```
### 2.2、解压安装介质
使用【root】解压：
```
tar -xvf GBase8s*.tar
```
### 2.3、开始安装
使用【root】静默安装，安装到路径【/opt/gbase】：
```
./ids_install -i silent -DLICENSE_ACCEPTED=TRUE -DUSER_INSTALL_DIR=/opt/gbase
```
## 3、初始化及优化实例


### 3.2、挂载数据盘【/data】
【root】用户格式化数据盘，并挂载到/data目录，将磁盘uid添加到/etc/fstab自动挂载，创建/data/gbase目录，用于存放数据文件：
```
mkfs.xfs /dev/vdb
blkid /dev/vdb
mkdir /data
mount /dev/vdb /data
mkdir /data/gbase
chown gbasedbt:gbasedbt /data/gbase
echo "UUID=158d0f4d-5606-4546-9b0a-6bddd97b1aeb /data xfs     defaults        0 0>>/etc/fstab 
```
### 3.3、配置【gbasedbt】用户环境变量
使用【gbasedbt】用户执行以下命令，设置数据库实例名为【gbase[xx]】，【xx】为两位编号，如：【gbase01】，如为数据库集群，集群中实例节点按顺序以此规则命名。设置默认字符集为【utf8】编码：
```
#修改INSTANCE为实例名
INSTANCE=gbase01
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
### 3.4、配置【ONCONFIG】配置文件
使用【gbasedbt】用户执行以下命令，初始化数据库实例，以此配置参数初始化数据库，要求系统内存在16GB以上。

数据库总内存计算方法：
SHMVIRTSIZE (KB)+LOCKS*144 (Byte)+BUFFERPOOL(size*buffers) (KB)
以下配置共约占用内存4096000KB+10000000*144B+16*512000=4G+1.4G+8G=13.4G

如系统内存不足或系统内存充足，可适当调整【BUFFERPOOL】参数的【buffers】大小用于数据缓存，以充分利用系统资源，建议数据库总内存不超过系统内存的【50%】。
```
DATADIR=/data/gbase
NUMCPU=`onstat -g osi |awk /"online processors"/'{print $5}'`
cp $GBASEDBTDIR/etc/onconfig.std  $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^ROOTPATH.*#ROOTPATH ${DATADIR}/rootdbschk001#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^ROOTSIZE.*#ROOTSIZE 2048000 #g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^DBSERVERNAME.*#DBSERVERNAME $GBASEDBTSERVER#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^NETTYPE.*#NETTYPE soctcp,8,100,NET#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^TAPEDEV.*#TAPEDEV /dev/null#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^LTAPEDEV.*#LTAPEDEV /dev/null#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^MULTIPROCESSOR.*#MULTIPROCESSOR 1#g" $GBASEDBTDIR/etc/$ONCONFIG;
sed -i "s#^CLEANERS.*#CLEANERS 32#g" $GBASEDBTDIR/etc/$ONCONFIG;
sed -i "s#^LOCKS.*#LOCKS 10000000#g" $GBASEDBTDIR/etc/$ONCONFIG;
sed -i "s#^DEF_TABLE_LOCKMODE.*#DEF_TABLE_LOCKMODE row#g" $GBASEDBTDIR/etc/$ONCONFIG;
sed -i "s#^DS_TOTAL_MEMORY.*#DS_TOTAL_MEMORY 4096000#g" $GBASEDBTDIR/etc/$ONCONFIG;
sed -i "s#^SHMVIRTSIZE.*#SHMVIRTSIZE 4096000#g" $GBASEDBTDIR/etc/$ONCONFIG;
sed -i "s#^SHMADD.*#SHMADD 2048000#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^STACKSIZE.*#STACKSIZE 2048#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^SBSPACENAME.*#SBSPACENAME sbspace01#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^DBSPACETEMP.*#DBSPACETEMP tempdbs01,tempdbs02,tempdbs03,tempdbs04#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^VPCLASS cpu.*#VPCLASS cpu,num=${NUMCPU},noage#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^TEMPTAB_NOLOG.*#TEMPTAB_NOLOG 1#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^NS_CACHE.*#NS_CACHE host=0,service=0,user=0,group=0#g" $GBASEDBTDIR/etc/$ONCONFIG
sed -i "s#^DUMPSHMEM.*#DUMPSHMEM 0#g" $GBASEDBTDIR/etc/$ONCONFIG
echo "BUFFERPOOL size=16k,buffers=512000,lrus=64,lru_min_dirty=50,lru_max_dirty=60">>$GBASEDBTDIR/etc/$ONCONFIG
```
### 3.5、配置【sqlhosts】监听文件
使用【gbasedbt】用户执行以下命令，修改【IPADDR】为实际IP（不修改则监听所有IP）：
```
IPADDR=0.0.0.0
cp $GBASEDBTDIR/etc/sqlhosts.std $GBASEDBTSQLHOSTS
echo "$GBASEDBTSERVER onsoctcp ${IPADDR} 9088" >> $GBASEDBTSQLHOSTS
```
### 3.6、初始化实例
使用【gbasedbt】用户执行以下命令，初始化数据库实例：
```
DATADIR=/data/gbase
touch ${DATADIR}/rootdbschk001
chmod 660 ${DATADIR}/rootdbschk001
oninit -ivyw
```
### 3.7、创建物理日志空间并优化物理日志
使用【gbasedbt】用户执行以下命令，物理日志位置从【rootdbs】调整到【plogdbs】，大小【5G】，物理日志大小跟根据系统负荷情况而定，对于生产系统，要求不小于【5G】：
```
DATADIR=/data/gbase
touch ${DATADIR}/plogdbschk001
chmod 660 ${DATADIR}/plogdbschk001
#创建物理日志空间，大小5GB
onspaces -c -d plogdbs -p ${DATADIR}/plogdbschk001 -o 0 -s 5120000
#调整物理日志，将物理日志从rootdbs中转移到plogdbs，大小5GB：
onparams -p -d plogdbs -s 5000000 -y
```
### 3.8、创建逻辑日志空间并优化逻辑日志
使用【gbasedbt】用户执行以下命令，逻辑日志位置从【rootdbs】调整到【llogdbs】，共【100个】，每个大小【100MB】，总计【10GB】：
```
DATADIR=/data/gbase
#创建逻辑日志空间，大小10GB
touch ${DATADIR}/llogdbschk001
chmod 660 ${DATADIR}/llogdbschk001
onspaces -c -d llogdbs -p ${DATADIR}/llogdbschk001 -o 0 -s 10240000
#增加逻辑日志到llogdbs，增加100个，每个100MB，存放于llogdbs：
for i in `seq 100`;do onparams -a -d llogdbs -s 100000;done
#切换当前逻辑日志到第七个以后，释放rootdbs中的前6个日志：
for i in `seq 7`;do onmode -l;done
#执行检查点：
onmode -c
#删除rootdbs中的6个逻辑日志：
for i in `seq 6`;do onparams -d -l $i -y;done
```
### 3.9、创建临时数据库空间
使用【gbasedbt】用户执行以下命令，创建4个临时空间，每个大小【10GB】，创建多个临时空间，部分操作可并行执行于各个临时空间，以提高效率。临时空间名称应与参数【DBSPACETEMP】配置保持一致，多个空间之间以英文逗号分隔，本文中配置参数【DBSPACETEMP】已配置为【tempdbs01,tempdbs02,tempdbs03,tempdbs04】：
```
DATADIR=/data/gbase
#创建四个临时空间，每个10GB
touch ${DATADIR}/tempdbs01chk001
touch ${DATADIR}/tempdbs02chk001
touch ${DATADIR}/tempdbs03chk001
touch ${DATADIR}/tempdbs04chk001
chmod 660 ${DATADIR}/tempdbs01chk001
chmod 660 ${DATADIR}/tempdbs02chk001
chmod 660 ${DATADIR}/tempdbs03chk001
chmod 660 ${DATADIR}/tempdbs04chk001
onspaces -c -d tempdbs01 -p ${DATADIR}/tempdbs01chk001 -o 0 -s 10240000 -k 16 -t
onspaces -c -d tempdbs02 -p ${DATADIR}/tempdbs02chk001 -o 0 -s 10240000 -k 16 -t
onspaces -c -d tempdbs03 -p ${DATADIR}/tempdbs03chk001 -o 0 -s 10240000 -k 16 -t
onspaces -c -d tempdbs04 -p ${DATADIR}/tempdbs04chk001 -o 0 -s 10240000 -k 16 -t
```
### 3.10、创建智能大对象空间
使用【gbasedbt】用户执行以下命令，智能大对象空间用于存储【blob、clob】数据类型，【SBSPACENAME】参数指定默认智能大对象空间，创建的智能大对象空间名称应与此参数值一致，本文中配置参数【SBSPACENAME】已配置为【sbspace01】：
```
DATADIR=/data/gbase
#创建智能大对象数据库空间，大小10GB，如未使用blob、clob数据类型，可不创建
touch ${DATADIR}/sbspace01chk001
chmod 660 ${DATADIR}/sbspace01chk001
onspaces -c -S sbspace01 -p ${DATADIR}/sbspace01chk001 -o 0 -s 10240000 -Df "LOGGING = ON"
```
### 3.11、创建用户数据库空间
使用【gbasedbt】用户执行以下命令，创建数据库空间【datadbs01】，用于存放用户数据，该空间页大小为【16KB】，总大小【100GB】：
```
DATADIR=/data/gbase
touch ${DATADIR}/datadbs01chk001
chmod 660 ${DATADIR}/datadbs01chk001
onspaces -c -d datadbs01 -p ${DATADIR}/datadbs01chk001 -o 0 -s 102400000 -k 16
```
### 3.12、创建默认数据库
使用【gbasedbt】用户执行以下命令，创建默认数据库【gbasedb】，数据存储于【datadbs01】空间：
```
dbaccess - -<<!
create database gbasedb in datadbs01 with log;
!
```
## 4、配置备份
如没有第三方备份软件设备，参考【ontape备份】配置备份。
如有第三方备份软件设备，由备份厂商实施备份。


