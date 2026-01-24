### 下载  
【[GSDK_3.6.3_3X2_1_1.1.0_1_eff9f1_RHEL6_x86_64.tar](
https://dl.gbase8s.com:9088/GSDK/x86_64/GSDK_3.6.3_3X2_1_1.1.0_1_eff9f1_RHEL6_x86_64.tar)】    

### 解压
GSDK解压到/root/GSDK
```
tar -xvf GSDK_3.6.3_3X2_1_1.1.0_1_eff9f1_RHEL6_x86_64.tar
mv GSDK_3.6.3_3X2_1_1.1.0_1_eff9f1_RHEL6_x86_64 GSDK
```
### 设置环境变量
设置环境变量GBASEDBTDIR、LD_LIBRARY_PATH
```
export GBASEDBTDIR=/root/GSDK/lib
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/root/GSDK/lib
```