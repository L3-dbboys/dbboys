下载【[clientsdk_3.6.3_3X2_1_0ac16b_WIN2003_x86_64.ZIP](
https://dl.gbase8s.com:9088/WINCSDK/clientsdk_3.6.3_3X2_1_0ac16b_WIN2003_x86_64.ZIP)】  
### 安装CSDK
解压后双击【installclientsdk.exe】 
![1.png](/GBASE 8S/img/WINDOWS安装CSDK/1.png)
![1.png](docs/GBASE 8S/img//WINDOWS安装CSDK/2.png)
![1.png](/img//WINDOWS安装CSDK/3.png)
![1.png](/img//WINDOWS安装CSDK/4.png)
![1.png](/img//WINDOWS安装CSDK/5.png)
![1.png](/img//WINDOWS安装CSDK/6.png)

### 配置ODBC
【控制面板】-【系统和安全】-【管理工具】-【ODBC数据源(64位)】
-【系统DNS】-【添加】
![1.png](/img//WINDOWS安装CSDK/7.png)
![1.png](/img//WINDOWS安装CSDK/8.png)  
![1.png](/img//WINDOWS安装CSDK/9.png)  
Server Name：实例名  
Host Name：IP地址  
Service：端口  
Protocol：onsoctcp  
Options：不填  
User Id：用户名  
Password：密码  
![1.png](/img//WINDOWS安装CSDK/10.png)
Client Locale：zh_cn.utf8  
Database Locale:zh_cn.utf8  
如果不是utf8字符集，可能为zh_cn.gb18030-2000
![1.png](/img//WINDOWS安装CSDK/11.png)
![1.png](/img//WINDOWS安装CSDK/12.png)