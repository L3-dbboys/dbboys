---
hide_title: false
sidebar_position: 79716
---
错误信息
```
-79716  System or internal error.
-79716  系统错误或内部错误。
或应用返回错误：Connection not established
```

错误原因
```
数据库重启或连接被防火墙kill，应用报此错误。
```


解决方案
```
排查是否做过数据库重启操作，防火墙长连接是否放开。
```

【root】用户执行，如确认防火墙原因，且防火墙规则无法调整，参考以下方式调整：
```
echo "net.ipv4.tcp_keepalive_time=300">>/etc/sysctl.conf
echo "net.ipv4.tcp_keepalive_intvl=60">>/etc/sysctl.conf
echo "net.ipv4.tcp_keepalive_probes=3">>/etc/sysctl.conf
sysctl -p
```
【gbasedbt】用户执行，配置数据库sqlhosts文件，增加参数k=1保持连接活动：
```
db_group  group  -  -  i=1  k=1
nodesrv1  onsoctcp  172.16.3.1  9088  g=db_group,k=1
nodesrv2  onsoctcp  172.16.3.2  9088  g=db_group,k=1
```
【gbasedbt】用户执行，重启数据库：
```
onmode -ky
oninit -v
```
