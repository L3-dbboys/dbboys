### 自动建表及批量入库示例
```python
import time
from datetime import datetime

from sqlalchemy import create_engine, text, MetaData, Column, Integer, String, TIMESTAMP, VARCHAR, CHAR
from sqlalchemy.orm import sessionmaker, scoped_session
from sqlalchemy.ext.declarative import declarative_base
if __name__ == '__main__':
    try:
        username = 'gbasedbt'
        password = 'GBase123'
        ip = '192.168.17.110'
        port = 9088
        dbname = 'gbasedb'
        servername = 'gbase01'
        url = f'gbase8s+gbase8sdb://{username}:{password}@{ip}:{port}/{dbname}?GBASEDBTSERVER={servername}&DB_LOCALE=zh_CN.utf8'
        engine = create_engine(url, echo=False,pool_timeout=10)
        ScopedSession = scoped_session(sessionmaker(bind=engine))
        db = ScopedSession()
    except Exception as e:
        print("发生错误:", str(e))
    Base = declarative_base()
   # db.execute(text('SET ENVIRONMENT IFX_NETBUF_SIZE \'32767\''))
    class TestTab(Base):
        __tablename__ = 'testtab'
        id = Column(Integer, autoincrement=True, primary_key=True)
        col1 = Column(TIMESTAMP, index=True, comment='测试字段1')
        col2 =Column(VARCHAR(128),index=True,comment='测试字段2')
        col3 = Column(CHAR(3),comment = '测试字段3')
        col4 = Column(VARCHAR(1500),nullable = False,comment = '测试字段4')

    # 创建表
    Base.metadata.create_all(engine)
    # 方法1：使用ORM模型对象插入
    cycle_start = time.time()
    start_time = time.time()
    total_inserted = 0
    total_records=500000
    batch_size=1000
    try:
        for i in range(500000):
            testTab = TestTab(id=0,
                                col1=f'device_{(i) % 100}',
                                col2=datetime.now(),
                                col3=f'M{(i ) % 9 + 1:02d}',
                                col4=f'原始消息内容_{i }_' + 'x' * 1500)
            db.add(testTab)
            # print(time.time()-)
            # 提交事务才会真正写入数据库
            if (i % 10 == 0):
                cycle_start = time.time()
                db.commit()
                print(time.time() - cycle_start)
    except Exception as e:
        print("发生错误:", str(e))

```
### 多线程入库示例
某些场景下需要快速入库性能，单线程可能满足不了需求，可使用多线程处理，提高入库性能，可参考以下代码
```python
import time
import threading
from queue import Queue
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor

from sqlalchemy import create_engine, Column, Integer, TIMESTAMP, VARCHAR, CHAR, insert
from sqlalchemy.orm import sessionmaker, declarative_base

# ========== 数据库初始化 ==========
username = 'gbasedbt'
password = 'GBase123'
ip = '192.168.17.110'
port = 9088
dbname = 'gbasedb'
servername = 'gbase01'
url = f'gbase8s+gbase8sdb://{username}:{password}@{ip}:{port}/{dbname}?GBASEDBTSERVER={servername}&DB_LOCALE=zh_CN.utf8'

engine = create_engine(url, echo=False, pool_size=10, max_overflow=20)
SessionLocal = sessionmaker(bind=engine)

Base = declarative_base()

class TestTab(Base):
    __tablename__ = 'testtab'
    id = Column(Integer, autoincrement=True, primary_key=True)
    col1 = Column(TIMESTAMP, index=True)
    col2 = Column(VARCHAR(128), index=True)
    col3 = Column(CHAR(3))
    col4 = Column(CHAR(1500), nullable=False)

Base.metadata.create_all(engine)

# ========== 队列 ==========
#data_queue = Queue(maxsize=10000)  # 防止无限堆积
data_queue = Queue()  # 防止无限堆积

# ========== 插入函数（每个线程用独立 Session） ==========
def insert_batch(rows):
    session = SessionLocal()
    stmt = insert(TestTab)
    try:
        session.execute(stmt, rows)   # executemany 批量插入
        session.commit()
        print(f"[{threading.current_thread().name}] 插入 {len(rows)} 行,队列剩余{ data_queue.qsize()} 行")
    except Exception as e:
        print("插入失败:", e)
        session.rollback()
    finally:
        session.close()

# ========== 消费者主循环 ==========
def consumer_worker(batch_size=500, workers=4):
    """不断从队列取数据，攒批交给线程池执行插入"""
    buffer = []
    executor = ThreadPoolExecutor(max_workers=workers)
    while True:
        try:
            item = data_queue.get(timeout=1)  # 阻塞等待数据
            buffer.append(item)
            if len(buffer) >= batch_size:
                # 提交给线程池
                executor.submit(insert_batch, buffer.copy())
                buffer.clear()
        except Exception:
            # 如果队列暂时没有数据，刷掉缓冲
            if buffer:
                executor.submit(insert_batch, buffer.copy())
                buffer.clear()

# ========== 模拟生产者（每秒 500 行） ==========
def producer_worker():
    counter = 0
    while True:
        for _ in range(10000):  # 每秒生产 500 行
            data_queue.put({
                "col1": f"device_{counter % 100}",
                "col2": datetime.now(),
                "col3": f"M{(counter % 9) + 1:02d}",
                "col4": f"原始消息内容_{counter}_" + "x" * 1480
            })
            counter += 1
        time.sleep(1)

# ========== 主程序 ==========
if __name__ == '__main__':
    threading.Thread(target=producer_worker, daemon=True).start()
    consumer_worker(batch_size=500, workers=5)  # 多线程批量插入

```