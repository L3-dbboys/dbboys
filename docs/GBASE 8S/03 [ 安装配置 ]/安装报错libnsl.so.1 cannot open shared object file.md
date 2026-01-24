错误信息
```
libnsl.so.1: cannot open shared object file  #安装报错
```

错误原因
```
高版本系统安装数据库
```

解决方案
```
ln -s /usr/lib64/libnsl.so.2 /usr/lib64/libnsl.so.1
```
