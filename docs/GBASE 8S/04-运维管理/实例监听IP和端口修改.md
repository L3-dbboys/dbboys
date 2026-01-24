### 查看当前实例监听端口
```
onstat -g ntt |grep soctcp
```
示例输出：
```
 45a18710 soctcplst        3 11:34:35 11:39:01          127.0.0.1|9088|soctcp            
        45a14be0 soctcppoll       2 11:34:35
```
以上输出表示监听IP为127.0.0.1，端口为9088
### 修改实例端口
修改sqlhosts配置文件中的IP/端口为新的IP/监听端口：
vi $GBASEDBTSQLHOSTS，以下示例监听数据库服务器所有IP，端口为9088：
```
gbase01  onsoctcp   *  9088
```
重启数据库实例【[实例启停](实例启停.md)】
