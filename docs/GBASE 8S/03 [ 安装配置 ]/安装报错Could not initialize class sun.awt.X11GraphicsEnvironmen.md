错误信息
```
java.lang.NoclassDefFoundError:Could not initialize class sun.awt.X11GraphicsEnvironmen
```

错误原因
```
Xll转发未关闭
```

解决方案
```
关闭x11转发,options->session options->Remote/X11->Forward X11 packets去掉勾选
```
