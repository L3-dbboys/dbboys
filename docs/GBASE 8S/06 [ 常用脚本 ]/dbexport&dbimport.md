### 使用dbexport导出数据库
dbexport工具将数据库卸载到文本文件，并创建数据库的模式文件。可通过dbimport使用该模式文件在其他服务器中重新创建数据库。
示例：dbexport导出testdb数据库
```
dbexport testdb -l;
```
注意：在使用dbexport导出数据库之前，必须禁用select触发器。dbexport程序在导出期间运行select语句。Select语句触发器可能会修改数据库内容。

dbexport导出数据库，在当前目录生成以数据库名开头的文件夹，名字为dbname.exp, dbname为您导出的数据库名，如testdb，该文件夹包含数据库的所有数据，用于dbimport导入。

### 使用dbimport导入数据库
dbimport工具从文本文件创建数据库并将数据导入数据库。
将dbexport导出的dbname.exp传输到目标服务器，在dbname.exp所在当前目录执行dbimport导入数据库。
示例：dbimport导入testdb数据库到datadbs01：
```
dbimport testdb -d datadbs01;
```
dbimport导入完成后，数据库无事务日志记录，通过ondblog命令修改testdb数据库的日志模式。关于数据库日志模式，请参考数据库管理中相关章节。
示例：将testdb数据库日志模式修改为unbuf日志模式：
```
ondblog unbuf testdb;
onbar -b -F;
```
注意：导入数据库时，请使用与创建该数据库时使用的相同的环境变量，否则导入可能失败。
如需导入为其他名称数据库，将dbname.exp重命名为dbname_new.exp及该文件夹下相关sql文件重命名为dbname_new.sql
示例：将testdb数据库导入为testdb1，存放于datadbs01空间：
```
mv testdb.exp testdb1.exp
mv testdb1.exp/testdb.sql testdb1.exp/testdb1.sql
mv testdb1.exp/testdb_ora.sql testdb1.exp/testdb1_ora.sql
dbimport testdb -d datadbs01;
ondblog unbuf testdb1;
onbar -b -F;
```

