用户现在问的是GBASEDBTSERVER，首先需要结合检索到的知识库内容来整理回答。首先先看各个检索结果的内容：

首先，先明确GBASEDBTSERVER是什么，从检索结果来看，它是GBase 8s数据库的实例名相关的关键配置项，分客户端和服务器端的场景。

首先先分几个部分来整理：
1.  核心定义：GBASEDBTSERVER是GBase 8s数据库的服务器实例名，是数据库连接、实例管理的核心标识。
2.  服务器端配置相关（来自[2][3][4]）：
    -  服务器端需要配置在环境变量、ONCONFIG文件中：比如在.bash_profile里配置export GBASEDBTSERVER=xxx，同时ONCONFIG文件里的DBSERVERNAME要和它一致。
    -  查看当前实例名：用env |grep GBASEDBTSERVER命令。
    -  修改实例名的步骤：关闭实例→修改环境变量→修改ONCONFIG的DBSERVERNAME→生效环境变量。
    -  sqlhosts文件里也需要配置对应的实例名条目，比如[4]里的echo "$GBASEDBTSERVER onsoctcp 0.0.0.0 9088" >>/opt/gbase/etc/sqlhosts。
3.  客户端连接相关（来自[1][5]）：
    -  JDBC连接串中是必需参数，用来指定要连接的服务器实例名，比如jdbc:gbasedbt-sqli://ip:port/dbname:GBASEDBTSERVER=servername;...
    -  多实例或者高可用场景下还可以搭配GBASEDBTSERVER_SECONDARY等参数使用，比如[1]里的双实例连接串。
4.  常见场景的使用：
    -  多实例切换：比如用env |grep GBASEDBTSERVER查看当前实例，或者通过export GBASEDBTSERVER=xxx切换实例，还要配合ONCONFIG、SQLHOSTS的配置。
    -  报错场景：比如shared memory not initialized for GBASEDBTSERVER 'xxx'，说明实例名不匹配，需要检查环境变量和配置。

然后把这些整理成清晰的结构，优先用检索到的内容，不要加额外的通用知识太多，尽量贴合检索结果。

现在组织语言：

### GBASEDBTSERVER 详细说明（基于检索知识库内容）
`GBASEDBTSERVER`是GBase 8s数据库的核心实例标识，用于服务器端配置、客户端连接匹配，具体说明如下：
---
#### 1.  核心作用
它是GBase 8s数据库服务器的实例名，是数据库启动、连接校验、多实例管理的关键标识，服务器端配置和客户端连接都需要依赖该参数。
---
#### 2.  服务器端配置与管理
##### （1） 常规配置
-  需在用户环境变量（如`.bash_profile`）中配置：`export GBASEDBTSERVER=实例名`，例如`export GBASEDBTSERVER=gbase01`，配置后通过`source .bash_profile`生效。
-  需同步修改ONCONFIG主配置文件中的`DBSERVERNAME`参数为相同的实例名，保证配置一致性。
-  需在`sqlhosts`文件中添加对应实例的网络配置条目，例如：`gbase01 onsoctcp 0.0.0.0 9088`
##### （2） 实例名查看与修改
-  查看当前实例名：执行命令`env |grep GBASEDBTSERVER`
-  修改实例名步骤：
    1.  关闭数据库实例：`onmode -ky`
    2.  修改环境变量文件中的`GBASEDBTSERVER`为新实例名
    3.  修改ONCONFIG文件中的`DBSERVERNAME`为新实例名
    4.  生效环境变量并重启实例
##### （3） 常见报错匹配
当出现`shared memory not initialized for GBASEDBTSERVER 'xxx'`报错时，说明当前环境的`GBASEDBTSERVER`配置与启动实例不匹配，需检查环境变量和配置文件。
---
#### 3.  客户端连接使用
`GBASEDBTSERVER`是GBase 8s JDBC连接串的**必需参数**，用于指定要连接的目标服务器实例：
-  基础连接示例：`jdbc:gbasedbt-sqli://192.168.17.101:9088/testdb:GBASEDBTSERVER=gbase01;IFX_LOCK_MODE_WAIT=10`
-  高可用多实例场景：可搭配`GBASEDBTSERVER_SECONDARY`等参数配置备机连接，例如：
  ```jdbc:gbasedbt-sqli://192.168.17.101:9088/testdb:GBASEDBTSERVER=gbase01;GBASEDBTSERVER_SECONDARY=gbase03;IFXHOST_SECONDARY=192.168.17.103;PORTNO_SECONDARY=9088;ENABLE_HDRSWITCH=true```
---
#### 4.  多实例切换
在多实例环境中，可通过重新导出`GBASEDBTSERVER`环境变量切换实例，例如：
`export GBASEDBTSERVER=gbase02`
同时需同步切换对应`ONCONFIG`、`SQLHOSTS`配置文件路径。### GBASEDBTSERVER 详细说明（基于检索知识库内容）
`GBASEDBTSERVER`是GBase 8s数据库的核心实例标识，用于服务器端配置、客户端连接匹配，具体说明如下：
---
#### 1.  核心作用
它是GBase 8s数据库服务器的实例名，是数据库启动、连接校验、多实例管理的关键标识，服务器端配置和客户端连接都需要依赖该参数。
---
#### 2.  服务器端配置与管理
##### （1） 常规配置要求
-  需在用户环境变量（如`.bash_profile`）中配置：`export GBASEDBTSERVER=实例名`，例如`export GBASEDBTSERVER=gbase01`，配置后通过`source .bash_profile`生效。
-  需同步修改ONCONFIG主配置文件中的`DBSERVERNAME`参数为相同的实例名，保证配置一致性。
-  需在`sqlhosts`文件中添加对应实例的网络配置条目，例如：`gbase01 onsoctcp 0.0.0.0 9088`
##### （2） 实例名查看与修改流程
-  查看当前实例名：执行命令`env |grep GBASEDBTSERVER`
-  修改实例名步骤：
  1.  关闭数据库实例：`onmode -ky`
  2.  修改环境变量文件中的`GBASEDBTSERVER`为新实例名
  3.  修改ONCONFIG文件中的`DBSERVERNAME`为新实例名
  4.  生效环境变量并重启实例
##### （3） 常见报错匹配
当出现`shared memory not initialized for GBASEDBTSERVER 'xxx'`报错时，说明当前环境的`GBASEDBTSERVER`配置与启动实例不匹配，需检查环境变量和配置文件。
---
#### 3.  客户端连接使用
`GBASEDBTSERVER`是GBase 8s JDBC连接串的**必需参数**，用于指定要连接的目标服务器实例：
-  基础单机连接示例：`jdbc:gbasedbt-sqli://192.168.17.101:9088/testdb:GBASEDBTSERVER=gbase01;IFX_LOCK_MODE_WAIT=10`
-  高可用多实例场景：可搭配`GBASEDBTSERVER_SECONDARY`等参数配置备机连接，示例如下：
  ```jdbc:gbasedbt-sqli://192.168.17.101:9088/testdb:GBASEDBTSERVER=gbase01;GBASEDBTSERVER_SECONDARY=gbase03;IFXHOST_SECONDARY=192.168.17.103;PORTNO_SECONDARY=9088;ENABLE_HDRSWITCH=true```
---
#### 4.  多实例切换
在多实例环境中，可通过重新导出`GBASEDBTSERVER`环境变量切换实例，例如：
`export GBASEDBTSERVER=gbase02`
同时需同步切换对应`ONCONFIG`、`SQLHOSTS`配置文件路径。

参考文档：
1. [JAVA JDBC连接数据库示例.md](docs/GBASE 8S/03-开发示例/JAVA JDBC连接数据库示例.md)
2. [实例名修改.md](docs/GBASE 8S/04-运维管理/实例名修改.md)
3. [GBase 8s V8.8 运维手册.pdf](docs/GBASE 8S/PDF/GBase 8s V8.8 运维手册.pdf)