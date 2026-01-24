### 版本要求
数据库版本：GBase8sV8.8_3.6.3及以上  
sqlalchemy版本：2.0.0及以上  
python版本：3.8及以上  

### 安装依赖包  

```shell
pip install gbase8sdb==0.2.1
pip install gbase8s-sqlalchemy==2.0.1
pip install SQLAlchemy==2.0.0
```

### 安装GSDK
参考【[客户端GSDK安装及环境变量配置](../02-安装配置/客户端GSDK安装及环境变量配置.md)】

### 代码示例

```python
from sqlalchemy import create_engine, text, MetaData
from sqlalchemy.orm import sessionmaker, scoped_session
if __name__ == '__main__':
    try:
        username = 'gbasedbt'
        password = 'GBase123'
        ip = '192.168.17.111'
        port = 9088
        dbname = 'gbasedb'
        servername = 'gbase01'
        url = f'gbase8s+gbase8sdb://{username}:{password}@{ip}:{port}/{dbname}?GBASEDBTSERVER={servername}&DB_LOCALE=zh_CN.utf8'
        engine = create_engine(url, echo=True,pool_timeout=10)
        ScopedSession = scoped_session(sessionmaker(bind=engine))
        db = ScopedSession()
    except Exception as e:
        print("发生错误:", str(e))
    try:
        sql=text(f"SELECT tabname FROM systables")
        result = db.execute(sql)
        data=result.fetchall()
        for it in data:
            tabname = it[0]
            print("表名:", tabname)
    except Exception as e:
        print("发生错误:", str(e))
```
更多示例，参考【[PYTHON使用SQLALCHEMY示例](PYTHON使用SQLALCHEMY示例.md)】