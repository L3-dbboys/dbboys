### LINUX安装GSDK
下载【[GBASE 8S GSDK LINUX X86_64最新版](https://www.dbboys.com/dl/gbase8s/gsdk/x86/latest.tar)】【[GBASE 8S GSDK LINUX ARM最新版](https://www.dbboys.com/dl/gbase8s/gsdk/arm/latest.tar)】    

解压GSDK解压到/root/GSDK
```
tar -xvf GSDK_3.6.3_3X2_1_1.1.0_1_eff9f1_RHEL6_x86_64.tar
mv GSDK_3.6.3_3X2_1_1.1.0_1_eff9f1_RHEL6_x86_64 GSDK
```
设置环境变量GBASEDBTDIR、LD_LIBRARY_PATH
```
export GBASEDBTDIR=/root/GSDK/lib
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/root/GSDK/lib
```
### WINDOWS安装GSDK
下载【[GBASE 8S GSDK WINDOWS 最新版](https://www.dbboys.com/dl/gbase8s/gsdk/win/latest.zip)】
解压GSDK解压到D:\GSDK
新建系统环境变量GBASEDBTDIR，设置为
```
D:\GSDK\lib
```

系统环境变量path，增加

```
D:\GSDK\lib
```