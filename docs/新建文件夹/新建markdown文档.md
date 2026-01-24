数据库有以下三种日志模式：
NOLOG  
创建语法：create database mydb in datadbs01;
无日志记录数据库有最好的性能，但不支持事务，适用开发测试。
BUFFERED LOG  
创建语法：create database mydb in datadbs01 with buffered log;
BUFFERED日志模式事务缓存在BUFFER中，BUFFER满了之后才写入磁盘，断电等极端故障可能存在丢失事务的可能，适用对数据完整性要求不高的一般业务系统或开发测试环境，允许故障发生后丢失少量事务。
UNBUFFERED LOG  
创建语法：create database mydb in datadbs01 with log;
UNBUFFERED日志模式在事务提交后立即写入磁盘，具有最高的数据完整性，在极端故障场景下不会丢失数据，生产环境一般建议采用此日志模式。


	NOLOG  
	创建语法：create database mydb in datadbs01;
	无日志记录数据库有最好的性能，但不支持事务，适用开发测试。
	BUFFERED LOG  
	创建语法：create database mydb in datadbs01 with buffered log;
	BUFFERED日志模式事务缓存在BUFFER中，BUFFER满了之后才写入磁盘，断电等极端故障可能存在丢失事务的可能，适用对数据完整性要求不高的一般业务系统或开发测试环境，允许故障发生后丢失少量事务。
	UNBUFFERED LOG  
	创建语法：create database mydb in datadbs01 with log;
	UNBUFFERED日志模式在事务提交后立即写入磁盘，具有最高的数据完整性，在极端故障场景下不会丢失数据，生产环境一般建议采用此日志模式。
[测试连接](https://www.baidu.com)
[测试连接](https://dl.gbase8s.com:9088/DOCUMENT/GBase%208s%20%E5%A4%87%E4%BB%BD%E4%B8%8E%E6%81%A2%E5%A4%8D%E6%8C%87%E5%8D%97.pdf)