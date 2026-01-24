
## 写入半边汉字非法字符到数据库
#设置环境变量,远程终端字符集使用UTF8
```
export DB_LOCALE=zh_cn.utf8
export CLIENT_LOCALE=zh_cn.utf8
export GL_USEGLU=1
onmode -ky
oninit -v
```
#创建库及表

```
dbaccess - -<<!
create database testdb with log;
create table t(col1 varchar(32));
insert into t values('天津南大通用数据技术股份有限公司');
!
```
#找出t表中数据页
```
oncheck -pt testdb:t |awk '/partnum/ {print "oncheck -pp "$3 " 1"}' |sh |awk -F ":| " 'NR==2 {print "chunknumber:"$1" 2koffset:"$2}'
```
输出
```
chunknumber:3 2koffset:48
```

#关闭数据库
```
onmode -ky
```
#读取需要改动字节偏移量48*2048+56，将最后两个空格写入”股“的前两个字节
```
dd if=/data/gbase/rootdbschk002 skip=48 count=1 bs=2k |hexdump -C
00000000  30 00 00 00 03 00 f6 33  01 00 01 08 3a 00 be 07  |0......3....:...|
00000010  00 00 00 00 00 00 00 00  00 20 e5 a4 a9 e6 b4 a5  |......... ......|
00000020  e5 8d 97 e5 a4 a7 e9 80  9a e7 94 a8 e6 95 b0 e6  |................|
00000030  8d ae e6 8a 80 e6 9c af  20 20 00 00 00 00 00 00  |........  ......|
00000040  00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00  |................|
*
000007f0  00 00 00 00 00 00 00 00  18 00 22 00 c3 33 06 00  |.........."..3..|
00000800
```
#将最后两个字节改为”股“的前两个字节
```
printf "股" | dd of=/data/gbase/rootdbschk002 bs=1 seek=98360 count=2 conv=notrunc
```
#读取确认已改
```
dd if=/data/gbase/rootdbschk002 skip=48 count=1 bs=2k |hexdump -C
00000000  30 00 00 00 03 00 f6 33  01 00 01 08 3a 00 be 07  |0......3....:...|
00000010  00 00 00 00 00 00 00 00  00 20 e5 a4 a9 e6 b4 a5  |......... ......|
00000020  e5 8d 97 e5 a4 a7 e9 80  9a e7 94 a8 e6 95 b0 e6  |................|
00000030  8d ae e6 8a 80 e6 9c af  e8 82 00 00 00 00 00 00  |................|
00000040  00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00  |................|
*
000007f0  00 00 00 00 00 00 00 00  18 00 22 00 c3 33 06 00  |.........."..3..|
00000800
```
#启动数据库
```
oninit -v
```
#查询数据，最后两个空格被替换为一个汉字的前两个字节，显示为??
```
echo "select * from t"|dbaccess testdb
```

#重现202错误
test.sh:
```
for i in `seq 1000`
do
echo "select nvl(col1,'@N') from t" |dbaccess testdb
done
```
#运行脚本 
```
nohup sh test.sh &
```

### 改写数据页
创建测试表
```
create table t(col1 int,col2 char(20));
insert into t values(2,'abc');
```
查看数据页
```
dd if=/data/gbase/rootdbschk002 skip=48 count=1 bs=2k |hexdump -C 
```
```
00000000  30 00 00 00 03 00 b3 0b  01 00 01 08 30 00 c8 07  |0...........0...|
00000010  00 00 00 00 00 00 00 00  00 00 00 02 61 62 63 20  |............abc |
00000020  20 20 20 20 20 20 20 20  20 20 20 20 20 20 20 20  |                |
00000030  00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00  |................|
*
000007f0  00 00 00 00 00 00 00 00  18 00 18 00 86 0b 06 00  |................|
00000800
```
修改数字2为8
```
printf "\d8" | dd of=/data/gbase/rootdbschk002 bs=1 seek=98328 count=1 conv=notrunc
```
修改字符a为e,seek=48*2048+28
```
printf "\x65" | dd of=/data/gbase/rootdbschk002 bs=1 seek=98332 count=1 conv=notrunc
```
将col2改为“南大通用”
```
printf "南大通用" | dd of=/data/gbase/rootdbschk002 bs=1 seek=98332 count=12 conv=notrunc
```

