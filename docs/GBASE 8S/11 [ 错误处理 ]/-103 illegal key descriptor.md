---
hide_title: false
sidebar_position: 103
---
错误信息
```
-211    Cannot read system catalog <error-text>.
-103    ISAM error: illegal key descriptor (too many parts or too long).
```

错误原因
```
数据库创建时的GL_USEGLU环境变量与当前启动时的环境变量不一致
```

解决方案
```
设置正确的GL_USEGLU环境变量，并重启数据库
```

参考处理流程：  
1、查看数据库创建的GL环境变量，testdb改为报此错误的库名
```
echo "select is_nls from sysdatabases where name='testdb'"|dbaccess sysmaster -
```
2、修改GL_USEGLU环境变量并重启数据库，如第一步输出为1，设置环境变量为1，反之unset
```
export GL_USEGLU=1  或 unset GL_USEGLU
onmode -ky
oninit -v
```