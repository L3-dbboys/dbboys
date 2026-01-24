错误信息
```
windows csdk查询乱码
```


错误原因
```
新版csdk不再支持en_us.819字符集库显示中文
```


解决方案

GBASE SERVER:
```
export IFMX_UNDOC_B168163=1
cd $GBASEDBTDIR/gls/lc11 
cp ./en_us/0333.lco ./zh_cn
onmode -ky
oninit -v
```
ODBC CLIENT:
```
DB_LOCALE=zh_cn.GB18030-2000
CLIENT_LOCALE=zh_cn.GB18030-2000
```