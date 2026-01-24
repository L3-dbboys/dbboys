## 安装及配置CSDK
### 安装CSDK
下载【[clientsdk_3.6.3_3X2_1_783c8d_RHEL6_x86_64.tar](
https://dl.gbase8s.com:9088/SERVER/X86/GBase8sV8.8_TL_3.6.3_3X2_1_783c8d_RHEL6_x86_64/clientsdk_3.6.3_3X2_1_783c8d_RHEL6_x86_64.tar)】  

【root】用户执行：解压
```
tar -xvf clientsdk_3.6.3_3X2_1_783c8d_RHEL6_x86_64.tar
```
【root】用户执行：静默安装
```
./installclientsdk -i silent -DLICENSE_ACCEPTED=TRUE -DUSER_INSTALL_DIR=/opt/gbase
```
### 配置CSDK环境变量
【应用用户】执行：
```
export GBASEDBTDIR=/opt/gbase  
export PATH=$PATH:$GBASEDBTDIR/bin
```
### 配置sqlhosts文件
【应用用户】执行：（sqlhosts文件填写需要连接的远端实例信息)
```
cat <<! >$GBASEDBTDIR/etc/sqlhosts  
gbase01 onsoctcp 192.168.17.101 9088  
!
```
## 配置ODBC
### 安装及配置UnixODBC
【root】用户执行：
```
yum install unixODBC
```
### 配置运行程序用户环境变量
【应用用户】执行：
```
export LD_LIBRARY_PATH=${GBASEDBTDIR}/lib:${GBASEDBTDIR}/lib/esql:${GBASEDBTDIR}/lib/cli:$LD_LIBRARY_PATH 
export ODBCINI=$GBASEDBTDIR/etc/odbc.ini 
```
### 配置ODBC配置文件
【应用用户】执行：
```
cat <<! >$ODBCINI
[ODBC Data Sources]  
odbc_demo=GBase 8s ODBC DRIVER  
[odbc_demo]  
Driver=/opt/gbase/lib/cli/iclit09b.so  
Description=GBase 8s ODBC DRIVER  
Database=testdb  
LogonID=gbasedbt  
pwd=GBase123  
Servername=gbase01  
CLIENT_LOCALE=zh_cn.utf8  
DB_LOCALE=zh_cn.utf8  
!
```

## 测试连接
需要用到CSDK的应用用户执行：
```
isql -v odbc_demo
```
出现以下输出表示连接正常，可执行sql语句：
```
[root@dbhost1 ~]# isql -v odbc_demo
+---------------------------------------+
| Connected!                            |
|                                       |
| sql-statement                         |
| help [tablename]                      |
| quit                                  |
|                                       |
+---------------------------------------+
SQL> 
```
