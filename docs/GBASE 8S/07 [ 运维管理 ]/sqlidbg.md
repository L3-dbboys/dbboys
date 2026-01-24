### Server端开启sqlidebug
增加vp：
```
onmode -p +1 sqli_dbg
```
每个session在$INFORMIXDIR/tmp下生成一个sqli.sessionid的文件
解析debug文件：
```
sqliprint -o trace.out sqli.30
```

### ODBC（win7 64位系统只对64位odbc有效）

windows:
设置系统级环境变量:
SQLIDEBUG=2:C:\\a.txt
odbc程序运行后会在c盘产生a.txt_数字字母组合的文件（a.txt_3864_1196_b85b58）。
创建C:\\debug.txt文件。
解析文件：cmd中执行：>sqliprt C:\\a.txt_3864_1196_b85b58 >>C:\\debug.txt
可以将解析结果重定向到磁盘的debug.txt
注意：这里，所有的盘符都是两个反斜杠！！

linux：
export SQLIDEBUG=2:/root/lfl/odbc.txt
cd $INFORMIXDIR/bin
./sqliprint /root/lfl/odbc.txt_7750_0_1ce37f0 >/root/lfl/odbc.txt

### JDBC:
通过在URI中增加SQLIDEBUG参数实现。URI格式参考："jdbc:informix-
sqli://172.16.3.15:9088/testdb:user=informix;password=informix;informixserver=gbaseserver;SQLIDEBUG=C:\\JDBCTrace.txt";
java

程序运行过程中自动产生C:\\JDBCTrace.txtXXX文件，例如，JDBCTrace.txt1492053131600.0通过sqliprt来解析：
sqliprt C:\\JDBCTrace.txt1492053131600.0 >>C:\\trace.txt

