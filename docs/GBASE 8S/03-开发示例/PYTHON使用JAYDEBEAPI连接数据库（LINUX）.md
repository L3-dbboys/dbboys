### 安装JayDeBeApi
```shell
pip3 install JayDeBeApi
```

### 编写测试代码

```python
import jaydebeapi

class dbc():
    def __init__(self,ip,port,dbname,sername,user,pwd):
        self.conn = jaydebeapi.connect("com.gbasedbt.jdbc.Driver",
                           f"jdbc:gbasedbt-sqli://{ip}:{port}/{dbname}:GBASEDBTSERVER={sername};DB_LOCALE=zh_CN.utf8;CLIENT_LOCALE=zh_CN.utf8;IFX_LOCK_MODE_WAIT=60",
                           [f"{user}", f"{pwd}"],
                           "gbasedbtjdbc_3.6.3_2_561324.jar")
        self.cursor = self.conn.cursor()

    def select(self,sql):
        self.cursor.execute(sql)
        return self.cursor.fetchall()

    def execute(self,sql):
        self.cursor.execute(sql)

    def close(self):
        self.cursor.close()
        self.conn.close()

# 创建一个数据库连接
conn = dbc("192.168.1.92", "9083", 'testdb', "myblog",'public_user', 'public_passwd');

# 执行数据库操作
conn.execute("drop table if exists t1");
conn.execute("create table t1(id int,name varchar(200))");
conn.execute("insert into t1 values(1,'aaa')");
conn.execute("insert into t1 values(2,'bbbbbb')");
conn.execute("insert into t1 values(3,'ccccccccc')");
rows = conn.select("select * from t1");
for k,(id,name) in enumerate(rows):
    print(id,name)
# 关闭数据库连接
conn.close();

def get_secenes():
    ret = {'code': 200}
    db = dbc("192.168.1.92", "9083", 'testdb', "myblog",'public_user', 'public_passwd');
    try:
        data = db.select("select * from t1")
        array = []
        for it in data:
            array.append({'id':it[0],'name':it[1]})
        ret['data'] = array
    except Exception as e:
        ret['code'] = 500
        ret['msg'] = str(e)
    else:
        pass
    finally:
        db.close()
    return ret

ret = get_secenes()
print(ret)

```

### 执行测试：
```shell
root@wei92:/weidata/temp# python test.py
1 aaa
2 bbbbbb
3 ccccccccc
{'code': 200, 'data': [{'id': 1, 'name': 'aaa'}, {'id': 2, 'name': 'bbbbbb'}, {'id': 3, 'name': 'ccccccccc'}]}
root@wei92:/weidata/temp#
root@wei92:/weidata/temp#
```