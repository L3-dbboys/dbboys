---
hide_title: false
sidebar_position: 2
---
1、下载安装介质【[GBase8sV8.8_TL_3.6.3_3X2_1_783c8d_RHEL6_x86_64.tar](https://dl.gbase8s.com:9088/SERVER/X86/GBase8sV8.8_TL_3.6.3_3X2_1_783c8d_RHEL6_x86_64/GBase8sV8.8_TL_3.6.3_3X2_1_783c8d_RHEL6_x86_64.tar)】【试用期1年】

2、创建【gbasedbt】用户组和用户，使用【root】用户执行：
```
groupadd gbasedbt -g 500
useradd gbasedbt -u 500 -g gbasedbt -m -d /home/gbasedbt
echo "GBase123" | passwd --stdin gbasedbt
```
3、解压安装介质，静默安装，安装到目录【/opt/gbase】，【root】用户执行：
```
tar -xvf GBase8s*.tar
./ids_install -i silent -DLICENSE_ACCEPTED=TRUE -DUSER_INSTALL_DIR=/opt/gbase
```
4、设置【gbasedbt】用户环境变量，【gbasedbt】用户执行：
```
cd
cat >>.bash_profile << EOF
export GBASEDBTDIR=/opt/gbase  # 安装目录为/opt/gbase
export GBASEDBTSERVER=gbase01  # 实例名为gbase01
export DB_LOCALE=zh_cn.utf8  # 默认字符集为utf8
export CLIENT_LOCALE=zh_cn.utf8 
export GL_USEGLU=1
export PATH=\$GBASEDBTDIR/bin:/usr/bin:\${PATH}:.
EOF
source .bash_profile
```
5、创建onconfig配置文件，【gbasedbt】用户执行：
```
cp $GBASEDBTDIR/etc/onconfig.std  $GBASEDBTDIR/etc/onconfig
sed -i "s#^DBSERVERNAME.*#DBSERVERNAME $GBASEDBTSERVER#g" $GBASEDBTDIR/etc/onconfig
sed -i "s#^ROOTPATH.*#ROOTPATH $GBASEDBTDIR/tmp/rootdbschk001#g" $GBASEDBTDIR/etc/onconfig
```
6、创建sqlhosts文件，【gbasedbt】用户执行：
```
echo "$GBASEDBTSERVER onsoctcp 0.0.0.0 9088" >>/opt/gbase/etc/sqlhosts
```
7、创建数据文件，【gbasedbt】用户执行：
```
touch $GBASEDBTDIR/tmp/rootdbschk001
chmod 660 $GBASEDBTDIR/tmp/rootdbschk001
```
8、初始化数据库，【gbasedbt】用户执行：
```
oninit -ivyw
```
9、创建数据库，【gbasedbt】用户执行：
```
dbaccess - -<<!
create database gbasedb with log;
!
```
数据库安装完成并创建了实例及数据库，本次安装实例信息如下：
```
实例名：gbase01
IP：   监听本机所有IP
端口：  9088
库名：  gbasedb
用户：  gbasedbt
密码：  GBase123
```