### 安装CSDK
参考【[LINUX安装CSDK及配置ODBC](/docs/03%20%5B%20安装配置%20%5D/客户端安装CSDK【ODBC、C等接口】/LINUX安装CSDK及配置ODBC)】中CSDK安装，无需配置ODBC

### 代码示例
c.ec
```c
#include <stdio.h>

EXEC SQL define FNAME_LEN       31;
EXEC SQL define LNAME_LEN       31;

main()
{
EXEC SQL BEGIN DECLARE SECTION;
    char user[ 32 ]="gbasedbt";
    char password[ 32 ]="GBase123";
    char tabname[ 32 ];
EXEC SQL END DECLARE SECTION;

    printf( "DEMO1 Sample ESQL Program running.\n\n");
    EXEC SQL WHENEVER ERROR STOP;
    EXEC SQL connect to 'testdb' USER :user USING :password;
    EXEC SQL declare democursor cursor for select tabname from systables where tabid>99;
    EXEC SQL open democursor;
    for (;;)
        {
        EXEC SQL fetch democursor into :tabname;
        if (strncmp(SQLSTATE, "00", 2) != 0)
            break;

        printf("%s\n",tabname);
        }

    EXEC SQL close democursor;
    EXEC SQL free democursor;

    EXEC SQL disconnect current;
    printf("\nDEMO1 Sample Program over.\n\n");

   exit(0);
}
```
设置环境变量
```
export LD_LIBRARY_PATH=$GBASEDBTDIR/lib:$GBASEDBTDIR/lib/esql
```
编译
```
esql c.ec -o c
```
执行
```
./c
```