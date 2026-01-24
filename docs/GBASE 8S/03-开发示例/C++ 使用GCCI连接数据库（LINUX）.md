### 版本要求
数据库版本：GBase8sV8.8_3.6.3及以上  
### 安装GSDK
参考【[客户端GSDK安装及环境变量配置](../02-安装配置/客户端GSDK安装及环境变量配置.md)】

### 示例

gcci.cpp
```cpp
#include <iostream>
#include <string.h>
#include <gcci.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

using namespace gbase::gcci;
using namespace std;

const string username = "gbasedbt";
const string password = "GBase123";
Environment* pEnv;
Connection* pConn;

int main(int          argc,
    char* argv[])
{
    string conn_str;
    //连接单机
    conn_str = "gbase8s:host=192.168.17.101;SERVICE=9088;PROTOCOL=onsoctcp;DATABASE=testdb;GBASEDBTSERVER=gbase01;sqlmode=gbase;timeout=60;gci_factory=4;";
    //连接集群
    conn_str = "gbase8s:GBASEDBTSERVER=db_group;DATABASE=testdb;sqlh_file=/root/sqlhosts;sqlmode=gbase;timeout=60;gci_factory=4;";
       /* sqlhosts  文件格式
       db_group        group   -       -
       gbase01 onsoctcp 192.168.17.101 9088 g=db_group
       gbase02 onsoctcp 192.168.17.102 9088 g=db_group
       */
    //Connect database
    pEnv = Environment::createEnvironment(Environment::Mode::DEFAULT);
    try
    {
        pConn = pEnv->createConnection(username, password, conn_str);
    }
    catch (SQLException& ex)
    {
        cout << ex.getMessage() << endl;
    }

    if (NULL == pConn)
    {
        cout << "Connection Failure" << endl;
        return 0;
    }
    cout << "Connection Success" << endl;

    //Disconnect
    pEnv->terminateConnection(pConn);
    Environment::terminateEnvironment(pEnv);

    cout << "Disconnect Success" << endl;

    return 0;
}
```

设置环境变量：
```
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/GSDK/lib
```
编译
```
g++ -o gcci gcci.cpp -std=c++11 -I/opt/GSDK/include -L/opt/GSDK/lib -lgcci
```
执行
```
./gcci
```
参考输出
```
Connection Success
Disconnect Success
```