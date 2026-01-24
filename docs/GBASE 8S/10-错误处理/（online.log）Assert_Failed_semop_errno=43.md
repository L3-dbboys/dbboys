错误信息
```
14:45:01  Assert Failed: semop: errno = 43
```
错误原因
```
信号量不够
```

解决方案
```
vi /etc/sysctl.conf  
#添加或修改
kernel.sem = 250 32000 100 4096
sysctl -p  #设置生效
```
