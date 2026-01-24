### 说明
GBase 8s V8.8_3.6.3以上版本支持两种sqlmode(gbase和oracle），默认gbase模式。设置sqlmode=oracle可以兼容oracle语法。

### 示例1-库全局设置
连接到该库的所有连接默认等待该时间。
```
dbaccess gbasedb -<<!
create procedure public.sysdbopen()
set environment sqlmode 'oracle';
end procedure;
!
```
创建以上存储过程后，连接到gbasedb的所有连接默认支持oracle语法。

### 示例2-当前会话设置
```
dbaccess gbasedb -<<!
set environment sqlmode 'oracle';
update ...
delete ...
!
```
在会话中执行set environment sqlmode 'oracle';该会后后续所有操作支持oracle语法。

### 示例3-在JDBC URL设置所有连接锁等待
```
URL_STRING = "jdbc:gbasedbt-sqli://192.168.1.1:9088/gbasedb:GBASEDBTSERVER=gbase01;sqlmode=oracle";
```
sqlmode=oracle,该URL的连接所有操作支持oracle语法。

### 示例4-在GBase Data Studio中设置锁等待时间
在已创建的连接上右键--编辑连接--驱动属性--右键--添加新属性--名称填入sqlmode，值填入oracle，工具sql语法支持oracle语法。