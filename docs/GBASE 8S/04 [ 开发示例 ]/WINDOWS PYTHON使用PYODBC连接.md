### 安装CSDK及配置ODBC 
参考【[WINDOWS安装CSDK及配置ODBC](/docs/03%20%5B%20安装配置%20%5D/客户端安装CSDK【ODBC、C等接口】/WINDOWS安装CSDK及配置ODBC)】

### 安装pyodbc

```shell
C:\Users\wangw\Desktop\a>pip list
Package    Version
---------- -------
JayDeBeApi 1.2.3
jpype1     1.6.0
packaging  25.0
pip        25.0.1

C:\Users\wangw\Desktop\a>pip install pyodbc
Defaulting to user installation because normal site-packages is not writeable
Looking in indexes: https://mirrors.aliyun.com/pypi/simple/
Collecting pyodbc
  Downloading https://mirrors.aliyun.com/pypi/packages/21/7f/3a47e022a97b017ffb73351a1061e4401bcb5aa4fc0162d04f4e5452e4fc/pyodbc-5.2.0-cp312-cp312-win_amd64.whl (69 kB)
Installing collected packages: pyodbc
Successfully installed pyodbc-5.2.0

[notice] A new release of pip is available: 25.0.1 -> 25.2
[notice] To update, run: C:\Users\wangw\AppData\Local\Microsoft\WindowsApps\PythonSoftwareFoundation.Python.3.12_qbz5n2kfra8p0\python.exe -m pip install --upgrade pip

C:\Users\wangw\Desktop\a>
```


### 编写python测试demo

```python
#!/usr/bin/python3 
# filename: TestPython3Pyodbc.py 
 
import sys 
import pyodbc 
 
print("Python Pyodbc测试程序开始运行.\n") 
# use DSN, need PWD key word. 
# conn = pyodbc.connect("DSN=myblog;PWD=123456789") 

# conn = pyodbc.connect("Driver={GBase ODBC DRIVER (64-bit)};Host=47.106.34.174;" +  
# "Service=9088;Protocol=onsoctcp;Server=gbase01;Database=testdb;Uid=gbasedbt;" +  
# "Pwd=GBase123;DB_LOCALE=zh_CN.utf8;CLIENT_LOCALE=zh_CN.utf8") 

conn = pyodbc.connect("Driver={GBase ODBC DRIVER (64-bit)};HOST=192.168.1.92;" +  
"SERV=9083;PROT=onsoctcp;SRVR=myblog;DB=testdb;UID=gbasedbt;PWD=123456789;" +  
"DLOC=zh_CN.utf8;CLOC=zh_CN.utf8") 
 

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

### 运行测试程序

```shell
PS C:\Users\wangw\Desktop\a> python .\odbc_test.py
Python Pyodbc测试程序开始运行.

公司ID: 1       公司名称：      公司地址: 南大通用
公司ID: 2       公司名称：      公司地址: 南大通用北京分公司

Python Pyodbc测试程序结束运行.
PS C:\Users\wangw\Desktop\a>
```