### 安装CSDK及配置ODBC
参考【[LINUX安装CSDK及配置ODBC](/docs/03%20%5B%20安装配置%20%5D/客户端安装CSDK【ODBC、C等接口】/LINUX安装CSDK及配置ODBC)】

### 编写python测试demo

```python
#!/usr/bin/python3
# filename: TestPython3Pyodbc.py

import sys
import pyodbc

print("Python Pyodbc测试程序开始运行.\n")

# 方法1：
conn = pyodbc.connect("Driver={GBase ODBC DRIVER};Host=192.168.1.92;" +
"Service=9083;Protocol=onsoctcp;Server=myblog;Database=testdb;Uid=public_user;" +
"Pwd=public_passwd;DB_LOCALE=zh_CN.utf8;CLIENT_LOCALE=zh_CN.utf8")

# 方法2：
#conn = pyodbc.connect("Driver={GBase ODBC DRIVER};HOST=192.168.1.92;" +
#"SERV=9083;PROT=onsoctcp;SRVR=myblog;DB=testdb;UID=public_user;PWD=public_passwd;" +
#"DLOC=zh_CN.utf8;CLOC=zh_CN.utf8")

# 方法3：
#conn = pyodbc.connect("DSN=testdb;PWD=public_passwd")

# set connection encoding
conn.setencoding(encoding='UTF-8')

mycursor = conn.cursor()
mycursor.execute("drop table if exists company")

mycursor.execute("create table company(coid serial,coname varchar(255),coaddr varchar(255))")

mycursor.execute("insert into company(coname,coaddr) values (?,?)",'南大通用','天津市海泰绿色产业基地')
mycursor.execute("insert into company(coname,coaddr) values (?,?)",'南大通用北京分公司','北京市朝阳区太阳宫')
mycursor.execute("update company set coaddr = ? where coid = 1", '天津市普天创新园')
conn.commit()

cursor1 = conn.cursor()
cursor1.execute("select * from company")
rows = cursor1.fetchall()

for i, (coid, coname, coaddr) in enumerate(rows):
    print("公司ID: %d \t公司名称：%s\t公司地址: %s" % (coid, str(coname), str(coaddr)))

conn.close()
print("\nPython Pyodbc测试程序结束运行.")
sys.exit(0)


```



### 运行python测试demo

```shell
root@wei92:/tmp# pip3 install pyodbc
root@wei92:/tmp# python a.py
Python Pyodbc测试程序开始运行.

公司ID: 1       公司名称：南大通用      公司地址: 天津市普天创新园
公司ID: 2       公司名称：南大通用北京分公司    公司地址: 北京市朝阳区太阳宫

Python Pyodbc测试程序结束运行.
root@wei92:/tmp#

```