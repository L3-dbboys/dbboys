## 概述	
	数据库安装部署前需要对先决条件进行准备配置，包括操作系统的安装、参数配置、网络配置、存储配置和数据库安装介质的准备。
## 1、硬件要求
### 1.1、服务器要求
GBase 8s为集中式OLTP数据库，单机需要1台服务器，共享磁盘集群需要2台服务器，主备集群需要2台服务器，共享磁盘+主备两地三中心集群需要3台服务器，支持在物理服务器或虚拟化服务器上部署，物理服务器稳定性和性能均要强于虚拟机，条件允许或对稳定性要求较高的业务系统，要求使用物理服务器，不建议在DOCKER环境部署生产数据库。
如使用虚拟机，在创建虚拟机时，要求创建为1物理cpu多核，而不是多物理cpu单核（可在虚拟机创建好后使用lscpu命令查看，Socket(s)为物理cpu个数），多物理cpu可能会导致虚拟机的numa架构在使用完一个节点内存后使用swap，从而导致性能问题。
使用虚拟机情况下，要求配置CPU内核数不小于8，内存不小于16GB。
### 1.2、CPU芯片要求
GBase 8s数据库支持以下CPU型号：
|CPU架构|CPU型号|
|----|----|
|x86|intel、amd、海光、兆芯|
|POWER|IBM POWER系列|
|Itanium|HP-UX Itanium系列|
|SPARC|Solaris SPARC系列|
|ARM|飞腾系列、鲲鹏系列|
|MIPS|龙芯3A、龙芯3B系列|
### 1.3、内存要求
内存配置要求不小于CPU内核数*2，swap配置不小于4GB。
### 1.4、存储要求
数据库为高IO基础软件，对存储IO性能要求较高，建议使用中高端存储（随机读写IOPS在5000以上），如有SSD存储，优先考虑使用SSD存储，RAID采用RAID5或者RAID10模式，数据盘存储容量根据实际数据大小分配（建议分配500GB以上），可使用裸设备或文件系统，如使用文件系统，采用xfs格式文件系统。如没有独立的备份设备，需规划能保留7天备份数据的磁盘空间用于备份。如数据库部署在云主机，划分磁盘应选择高IO高性能类型磁盘。
**注意：如计划配置SSC共享磁盘数据库集群，必须有独立的存储设备，映射到两台服务器以实现磁盘共享。**

### 1.5、网络要求
数据库网络要求千兆以上网卡，服务网络应当使用不少于两条物理线路的双网卡绑定，如配置了数据库集群，要求配置独立于服务器网络的专用心跳网络（可直连），同样使用双网卡绑定。网卡硬件厂商应提供网卡相应操作系统的网卡驱动，保证ethtool查看网卡信息可正常显示网卡状态信息。

单机数据库服务器需要配置一个物理IP，要求网络防火墙开放应用与数据库服务器之间的端口9088长连接。
数据库集群每台服务器需要配置两个物理IP（一个服务IP，一个心跳IP），网络防火墙开放数据库集群节点之间及应用与数据库节点之间端口9088、9099、9200、9300长连接。
### 1.6、备份设备要求
优先使用第三方备份软件进行统一备份策略管理，GBase 8s支持NBU、爱数、鼎甲等备份软件。如没有第三方备份软件，要求准备独立于数据存储以外的存储设备对数据库进行备份，确保生产数据及备份数据不存储于同一存储设备。备份存储可使用中低端存储。

## 2、操作系统版本要求
GBase 8s数据库支持以下操作系统：
<table>
    <tr>
        <th align="left">序号</th>   <!-- 左对齐 -->
        <th align="left">系统</th> <!-- 居中对其（默认）-->
        <th align="left">版本要求</th>  <!-- 右对齐-->
        <th align="left">依赖包要求</th>  <!-- 右对齐-->
    </tr>
    <tr>
        <td>1</td>
        <td>AIX</td>
        <td>6.02以上</td>
        <td>-</td>
    </tr>
        <tr>
        <td>2</td>
        <td>HP-UX</td>
        <td>11.23以上</td>
        <td >-</td>
    </tr>
    <tr>
        <td>3</td>
        <td>Solaris</td>
        <td>11以上</td>
        <td>-</td>
    </tr>
    <tr>
        <td>4</td>
        <td>CentOS</td>
        <td>7.0以上</td>
        <td rowspan="8">
        依赖包版本不低于以下版本：<br />
        unzip 6.00<br />
libaio-0.3.107-10.el6.x86_64<br />
libgcc-4.4.6-3.el6.x86_64<br />
libstdc++-4.4.6-3.el6.x86_64<br />
ncurses-5.7-3.20090208.el6.x86_64<br />
pam-1.1.1-10.el6.x86_64
        </td>
    </tr>
    <tr>
        <td>5</td>
        <td>RedHat</td>
        <td>7.0以上</td>
    </tr>
    <tr>
        <td>6</td>
        <td>SUSE</td>
        <td>11以上</td>
    </tr>
    <tr>
        <td>7</td>
        <td>Asianux</td>
        <td>9.0以上</td>
    </tr>
    <tr>
        <td>8</td>
        <td>Debian</td>
        <td>10以上</td>
    </tr>
    <tr>
        <td>9</td>
        <td>Ubuntu</td>
        <td>16以上</td>
    </tr>
    <tr>
        <td>10</td>
        <td>Kylin</td>
        <td>V10以上</td>
    </tr>
    <tr>
        <td>11</td>
        <td>UOS</td>
        <td>V20以上</td>
    </tr>
</table>

使用以下命令检查是否设少相关包：
```
rpm -qa |grep unzip 
rpm -qa |grep libaio 
rpm -qa |grep libgcc
rpm -qa |grep libstdc
rpm -qa |grep ncurses
rpm -qa |grep pam
```
如果缺少相应的，在有apt源或者yum源时，可直接安装。如，使用yum安装：
```
yum -y install unzip glibc-devel ncurses-libs libnsl libaio
```
## 3、系统配置要求
### 3.1、规范命名主机名
数据库日志归档及数据库备份文件命名均依赖系统主机名称，如现场主机命名无已有规范，数据库服务器命名参照dbhost[xx]规则命名，xx为两位数字序号，如：
```
echo "dbhost01">/etc/hostname
hostname dbhost01
```
同时修改/etc/hosts文件，修改对应IP的机器名，如：
```
10.10.10.10 dbhost01
```
如计划配置集群心跳，集群心跳机器名参照dbhahost[xx]规则命名，xx为两位数字序号，如在/etc/hosts中配置：
```
192.168.1.1 dbhahost01
```
### 3.2、优化内核参数
root用户执行ipcs -l检查系统内核参数，输出要求不小于以下值：
```
------ Shared Memory Limits --------
max number of segments = 4096
max seg size (kbytes) = 4294967296
max total shared memory (kbytes) = 4398046511104
min seg size (bytes) = 1

------ Semaphore Limits --------
max number of arrays = 128
max semaphores per array = 250
max semaphores system wide = 32000
max ops per semop call = 32
```
如小于以上配置，修改/etc/sysctl.conf,修改完成后sysctl -p生效：
```
cat <<! >>/etc/sysctl.conf
kernel.shmmax=4398046511104
kernel.shmall=4294967296
kernel.shmmni=4096
kernel.sem=250 32000  32 128
!
sysctl -p
```
调整ulimit相关参数：
```
cat <<! >>/etc/security/limits.conf
*               soft    nproc   65536
*               hard    nproc   65536
*               soft    nofile  1048576
*               hard    nofile  1048576
!
```
如果用户nofile和nproc设置值不生效，需要修改/etc/systemd/system.conf配置文件：
```
sed -i "s/#DefaultLimitNOFILE.*/DefaultLimitNOFILE=1048576/g" /etc/systemd/system.conf
sed -i "s/#DefaultLimitNPROC.*/DefaultLimitNPROC=65536/g" /etc/systemd/system.conf
```
### 3.3、关闭系统防火墙
系统防火墙需关闭或放开数据库监听端口9088，关闭系统防火墙：
```
systemctl stop firewalld.service
systemctl disable firewalld.service
```
如系统要求不可关闭防火墙，执行以下命令放开相关端口，单机放开9088，集群放开9088、9099、9200、9300：
```
firewall-cmd --permanent --zone=public --add-port=9088/tcp
firewall-cmd --permanent --zone=public --add-port=9099/tcp
firewall-cmd --permanent --zone=public --add-port=9200/tcp
firewall-cmd --permanent --zone=public --add-port=9300/tcp
firewall-cmd --reload
```
### 3.4、修改系统DNS配置
如无特殊需求，要求修改系统DNS配置，避免出现数据库连接缓慢现象：
```
sed -i "s#^hosts.*#hosts:      files#g" /etc/nsswitch.conf
```
如不存在/etc/nsswitch.conf文件，则忽略此步。
### 3.5、关闭removeIPC
RemoveIPC在7.x以上部分版本Linux默认配置为yes，要求配置为no，避免出现信号量被删除导致数据库宕机：
```
#检查当前配置，如显示为yes需修改配置关闭RemoveIPC
loginctl show-session | grep RemoveIPC
#修改配置文件
sed -i "s/^#RemoveIPC.*/RemoveIPC=no/g" /etc/systemd/logind.conf
#重启服务
systemctl daemon-reload 
systemctl restart systemd-logind
```
### 3.6、启动系统时钟同步
配置时钟同步，修改对应IP（10.10.10.10）为NTP服务器IP：
```
sed -i "s/^server.*/#&/g" /etc/chrony.conf
sed -i "s/#hwtimestamp.*/hwtimestamp */g" /etc/chrony.conf
sed -i "s/#local stratum.*/local stratum 10/g" /etc/chrony.conf
sed -i "/#server 3.*/a server 10.10.10.10 iburst" /etc/chrony.conf
```
启动时钟同步服务：
```
systemctl enable chronyd.service
systemctl start chronyd.service
```
查看同步状态：
```
systemctl status chronyd.service 
timedatectl && date && hwclock
```
如时钟误差较大，调整系统时钟
```
#修改硬件时间
hwclock --set --date “2023-02-22 19:10:30”
#同步系统时间和硬件时间
hwclock --hctosys
#保存时钟
clock -w
#重启系统
reboot
```
### 3.7关闭SELINUX
修改/etc/selinux/config配置文件，修改SELINUX的值为disabled，重启系统：
```
sed -i "s#^SELINUX=.*#SELINUX=disabled#g" /etc/selinux/config
reboot
```
## 4、数据库软件命名规则及下载
### 4.1安装包命名规则
<table>
    <tr>
        <th align="left">序号</th>   <!-- 左对齐 -->
        <th align="left">类型</th> <!-- 居中对其（默认）-->
        <th align="left">安装包命名</th>  <!-- 右对齐-->
        <th align="left">备注</th>  <!-- 右对齐-->
    </tr>
    <tr>
        <td>1</td>
        <td>server安装包</td>
        <td>命名规则：<br />
`GBase8sV8.[x]_[AEE|TL]_[x.x.x_x]_[hashid]_[os]_[cpu].tar`<br /><br />
示例1：在RHEL6+x86_64平台编译的版本，适用于x86平台<br />
GBase8sV8.8_AEE_3.3.0_2_36477d_RHEL6_x86_64.tar<br /><br />
示例2：在Kylin10_FT2000PLUS 平台编译的版本，适用于飞腾平台<br />
GBase8sV8.8_AEE_3.3.0_2_0971827_Kylin10_FT2000PLUS.tar<br /><br />
示例3：在CentOS7_KP920平台编译的版本，适用于鲲鹏平台<br />
GBase8sV8.8_AEE_3.3.0_2X4_1_025862_CentOS7_KP920.tar<br /><br />
数据库server安装包包含数据库所有功能，包括高可用相关组件，AEE为生产版本，TL为试用版本，试用期限1年，如无特殊要求，建议使用GBase8sV8.8_AEE_3.3.0以上版本</td>
        <td>如未使用SSC共享磁盘集群且要求自动切换，下载本安装包安装即可，无需安装其他组件。</td>
    </tr>
    <tr>
        <td>2</td>
        <td>csdk安装包</td>
        <td>命名规则：<br />
`clientsdk_[x.x.x_x]_[hashid]_[os]_[cpu].tar`<br /><br />
示例1：在RHEL6+x86_64平台编译的版本，适用于x86平台<br />
clientsdk_3.3.0_2_36477d_RHEL6_x86_64.tar<br /><br />
示例2：在Kylin10_FT2000PLUS 平台编译的版本，适用于飞腾平台<br />
clientsdk_3.3.0_2_097182_Kylin10_FT2000PLUS.tar<br /><br />
示例3：在CentOS7_KP920平台编译的版本，适用于鲲鹏平台<br />
clientsdk_3.3.0_2X4_1_025862_CentOS7_KP920.tar<br /><br />
csdk安装包包含数据库c、odbc等接口，如应用系统使用了这些接口连接数据库，需在应用服务器安装csdk，csdk也包含了连接管理器组件，如数据库集群需要使用连接管理器仲裁切换，需要安装csdk。csdk不区分生产和试用版本，csdk版本要求与server版本严格匹配一致使用，即hashid和server一致。</td>
        <td>如使用SSC共享磁盘集群且要求自动切换，需安装csdk。</td>
    </tr>
    <tr>
        <td>3</td>
        <td>jdbc驱动</td>
        <td>命名规则：<br />
`gbasedbtjdbc_[x.x.x_x]_[hashid].jar`<br /><br />
示例1：
gbasedbtjdbc_3.3.0_2_36477d.jar<br /><br />
jdbc驱动版本要求与server版本严格匹配一致使用，即hashid和server一致。jdbc不区分生产和试用版本。</td>
        <td></td>
    </tr>
</table>


## 5、实例配置及命名规范
<table>
    <tr>
        <th align="left">序号</th>   <!-- 左对齐 -->
        <th align="left">配置项</th> <!-- 居中对其（默认）-->
        <th align="left">规范</th>  <!-- 右对齐-->
        <th align="left">示例</th>  <!-- 右对齐-->
    </tr>
    <tr>
        <td>1</td>
        <td>GBASEDBTDIR</td>
        <td>/opt/gbase</td>
        <td></td>
    </tr>
    <tr>
        <td>2</td>
        <td>GBASEDBTSERVER</td>
        <td>gbase[xx]</td>
        <td>gbase01、gbase02</td>
    </tr>
    <tr>
        <td>3</td>
        <td>DBSERVERALIASES</td>
        <td>ha_[type][xx]</td>
        <td>ha_pri、ha_ssc、ha_hac、ha_rhac，如有多个ssc备机或rhac备机，后面加上序号，如ha_ssc01、ha_rhac01</td>
    </tr>
    <tr>
        <td>4</td>
        <td>ONCONFIG</td>
        <td>onconfig.`${GBASEDBTSERVER}`</td>
        <td>onconfig.gbase01</td>
    </tr>
     <tr>
        <td>5</td>
        <td>GBASEDBTSQLHOSTS</td>
        <td>$GBASEDBTDIR/etc/sqlhosts.`${GBASEDBTSERVER}`</td>
        <td>sqlhosts.gbase01</td>
    </tr>
    <tr>
        <td>6</td>
        <td>DB_LOCALE</td>
        <td>zh_CN.utf8</td>
        <td>默认使用utf8字符集</td>
    </tr>
    <tr>
        <td>7</td>
        <td>CLIENT_LOCALE</td>
        <td>zh_CN.utf8</td>
        <td>默认使用utf8字符集</td>
    </tr>
    <tr>
        <td>8</td>
        <td>GL_USEGLU</td>
        <td>1</td>
        <td></td>
    </tr>
    <tr>
        <td>9</td>
        <td>空间命名规则</td>
        <td>根空间：rootdbs<br />
物理日志空间：plogdbs<br />
逻辑日志空间：llogdbs<br />
临时空间：tempdbs[xx]<br />
智能大对象空间：sbspace[xx]<br />
数据空间：datadbs[xx]<br />
索引空间：indxdbs[xx]<br />
磁盘心跳空间：ssc_alt_comm<br /></td>
        <td>rootdbs<br />
plogdbs<br />
llogdbs<br />
tempdbs01、tempdbs02<br />
sbspace01<br />
datadbs01、datadbs02<br />
indxdbs01</td>
    </tr>
    <tr>
        <td>10</td>
        <td>数据文件命名规则</td>
        <td>`${dbspacename}`chk[xxx]</td>
        <td>rootdbschk001<br />
plogdbschk001<br />
llogdbschk001<br />
tempdbs01chk001<br />
sbspace01chk001<br />
datadbs01chk001<br />
indxdbs01chk001</td>
    </tr>
    <tr>
        <td>11</td>
        <td>数据文件路径</td>
        <td>/data/gbase</td>
        <td></td>
    </tr>
    <tr>
        <td>12</td>
        <td>备份磁盘路径</td>
        <td>/backup</td>
        <td></td>
    </tr>
</table>


