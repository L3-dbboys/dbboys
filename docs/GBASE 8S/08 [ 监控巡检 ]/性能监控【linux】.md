下载脚本【[GBase8smon.sh](https://dl.gbase8s.com:9088/Scripts/GBase8smon.sh)】
```shell
#!/bin/bash
###################################################################################
# filename: GBase8smon.sh
# Last modified by: L3 2024-04-17
# support OS: Linux
# support database version: GBase 8s V8.x
# useage: sh GBase8smon.sh 5 100  #每5秒收集一次，收集100次
###################################################################################
# 以下信息，收集一次
if [ $# -lt 2 ]; then
  echo "Useage:sh gen.sh <interval> <count>"
  exit 0
else
  INTERVAL=$1
  COUNT=$2
fi
GENDATADIR=gendata_$(date +%Y%m%d%H%M%S)
mkdir -p ${GENDATADIR}
cd ${GENDATADIR}
dmesg > dmesg.txt
free -m > free_m.txt
onstat -V > onstat_V.txt
onstat -d > onstat_d.txt
onstat -g seg > onstat_g_seg.txt
onstat -g env > onstat_g_env.txt
onstat -g osi > onstat_g_osi.txt
onstat -c > onstat_c.txt
onstat -g cluster > onstat_g_cluster.txt
onstat -g cmsm > onstat_g_cmsm.txt
ps -aux |grep cmsm > cm_mem.txt

# 以下信息，根据输入参数循环收集
for i in `seq $COUNT`
do
tmpdir=$(date +%Y%m%d%H%M%S)
mkdir $tmpdir
cd $tmpdir
onstat -g ses 0 > onstat_g_ses_0.txt
onstat -g stk > onstat_g_stk.txt
onstat -u > onstat_u.txt
onstat -x > onstat_x.txt
onstat -g ckp > onstat_g_ckp.txt
onstat -g ath > onstat_g_ath.txt
onstat -p > onstat_p.txt
onstat -g sql > onstat_g_sql.txt
vmstat > vmstat.txt
mpstat -P ALL > mpstat_P_ALL.txt
sar -d > sar_d.txt
cd ..
sleep $INTERVAL
done
cd ..
tar -cvf ${GENDATADIR}.tar ${GENDATADIR} >/dev/null 2>&1
rm -rf ${GENDATADIR}
echo "gen.sh finished!"
echo "datafile is:"${GENDATADIR}.tar
```
