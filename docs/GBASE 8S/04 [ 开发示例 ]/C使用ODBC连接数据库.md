### 安装CSDK
参考【[LINUX安装CSDK及配置ODBC](/docs/03%20%5B%20安装配置%20%5D/客户端安装CSDK【ODBC、C等接口】/LINUX安装CSDK及配置ODBC)】

### demo程序通过ODBC连接
$GBSAEDBTDIR/demo/cli/catalog.c  
```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifndef NO_WIN32
#include <io.h>
#include <windows.h>
#include <conio.h>
#endif /*NO_WIN32*/

#include "infxcli.h"

#define BUFFER_LEN      130
#define ERRMSG_LEN      200

SQLCHAR   defDsn[] = "odbc_demo";

SQLINTEGER checkError (SQLRETURN       rc,
                SQLSMALLINT     handleType,
                SQLHANDLE       handle,
                SQLCHAR*            errmsg)
{
    SQLRETURN       retcode = SQL_SUCCESS;

    SQLSMALLINT     errNum = 1;
    SQLCHAR             sqlState[6];
    SQLINTEGER      nativeError;
    SQLCHAR             errMsg[ERRMSG_LEN];
    SQLSMALLINT     textLengthPtr;
 

    if ((rc != SQL_SUCCESS) && (rc != SQL_SUCCESS_WITH_INFO))
    {
        while (retcode != SQL_NO_DATA)
        {
            retcode = SQLGetDiagRec (handleType, handle, errNum, sqlState, &nativeError, errMsg, ERRMSG_LEN, &textLengthPtr);

            if (retcode == SQL_INVALID_HANDLE)
            {
                fprintf (stderr, "checkError function was called with an invalid handle!!\n");
                return 1;
            }

            if ((retcode == SQL_SUCCESS) || (retcode == SQL_SUCCESS_WITH_INFO))
                fprintf (stderr, "ERROR: %d:  %s : %s \n", nativeError, sqlState, errMsg);

            errNum++;
        }

        fprintf (stderr, "%s\n", errmsg);
        return 1;   /* all errors on this handle have been reported */
    }
        else
                return 0;       /* no errors to report */
}




int main (long argc,char* argv[])
{
    /* Declare variables
    */

    /* Handles */            
    SQLHDBC     hdbc;
    SQLHENV     henv;
    SQLHSTMT    hTableStmt;
    SQLHSTMT    hColStmt;
    SQLHSTMT    hPriKeyStmt;

    /* Miscellaneous variables */

    SQLCHAR   dsn[20];   /*name of the DSN used for connecting to the database*/
    SQLRETURN   rc = 0;
    SQLINTEGER   in;


    SQLCHAR  tableName[BUFFER_LEN];
    SQLLEN  cbTableName;

    SQLCHAR  colName[BUFFER_LEN];
    SQLLEN  cbColName;

    SQLCHAR  priKeyName[BUFFER_LEN];
    SQLLEN  cbPriKeyName;


    
    /*  STEP 1. Get data source name from command line (or use default)
    **          Allocate the environment handle and set ODBC version
    **          Allocate the connection handle 
    **          Establish the database connection 
    **          Allocate the statement handle
    */


    /* If(dsn is not explicitly passed in as arg) */
    if (argc != 2)
    {
        /* Use default dsn - odbc_demo */
        fprintf (stdout, "\nUsing default DSN : %s\n", defDsn);
        strcpy ((char *)dsn, (char *)defDsn);
    }
    else
    {
        /* Use specified dsn */
        strcpy ((char *)dsn, (char *)argv[1]);
        fprintf (stdout, "\nUsing specified DSN : %s\n", dsn);
    }

    
    /* Allocate the Environment handle */
    rc = SQLAllocHandle (SQL_HANDLE_ENV, SQL_NULL_HANDLE, &henv);
    if (rc != SQL_SUCCESS)
    {
        fprintf (stdout, "Environment Handle Allocation failed\nExiting!!");
        return (1);
    }


    /* Set the ODBC version to 3.0 */
    rc = SQLSetEnvAttr (henv, SQL_ATTR_ODBC_VERSION, (SQLPOINTER)SQL_OV_ODBC3, 0);
    if (checkError (rc, SQL_HANDLE_ENV, henv, (SQLCHAR *) "Error in Step 1 -- SQLSetEnvAttr failed\nExiting!!"))
                return (1);


    /* Allocate the connection handle */
    rc = SQLAllocHandle (SQL_HANDLE_DBC, henv, &hdbc);
    if (checkError (rc, SQL_HANDLE_ENV, henv, (SQLCHAR *) "Error in Step 1 -- Connection Handle Allocation failed\nExiting!!"))
                return (1);


    /* Establish the database connection */
    rc = SQLConnect (hdbc, dsn, SQL_NTS, (SQLCHAR *) "", SQL_NTS, (SQLCHAR *) "", SQL_NTS);
        if (checkError (rc, SQL_HANDLE_DBC, hdbc, (SQLCHAR *) "Error in Step 1 -- SQLConnect failed\nExiting!!"))
                return (1);


    /* Allocate the statement handles */
    rc = SQLAllocHandle (SQL_HANDLE_STMT, hdbc, &hTableStmt );
    if (checkError (rc, SQL_HANDLE_DBC, hdbc, (SQLCHAR *) "Error in Step 1 -- Statement Handle Allocation failed\nExiting!!"))
                return (1);
    
    rc = SQLAllocHandle (SQL_HANDLE_STMT, hdbc, &hColStmt );
    if (checkError (rc, SQL_HANDLE_DBC, hdbc, (SQLCHAR *) "Error in Step 1 -- Statement Handle Allocation failed\nExiting!!"))
                return (1);
    
    rc = SQLAllocHandle (SQL_HANDLE_STMT, hdbc, &hPriKeyStmt );
    if (checkError (rc, SQL_HANDLE_DBC, hdbc, (SQLCHAR *) "Error in Step 1 -- Statement Handle Allocation failed\nExiting!!"))
                return (1);
    

        fprintf (stdout, "STEP 1 done...connected to database\n");




        /* STEP 2.  Call SQLTables to get table names from the database
    **          For each table in the database, call SQLColumns to get its 
    **          columns and SQLPrimaryKeys to get the columns which comprise
    **          its primary key
    */

    /* Get the names of all the tables in the database */
    rc = SQLTables (hTableStmt, NULL, 0, NULL, 0, NULL, 0, (SQLCHAR *) "TABLE", SQL_NTS); 
    if (checkError (rc, SQL_HANDLE_STMT, hTableStmt, (SQLCHAR *) "Error in Step 2 -- SQLTables failed\n"))
                goto Exit;

    /* Bind the result set column # 3 - 'TABLE_NAME' */
    rc = SQLBindCol (hTableStmt, 3, SQL_C_CHAR, tableName, BUFFER_LEN, &cbTableName);
    if (checkError (rc, SQL_HANDLE_STMT, hTableStmt, (SQLCHAR *) "Error in Step 2 -- SQLBindCol failed\n"))
                goto Exit;


    /* Fetch the data */
    while (1)
    {
            rc = SQLFetch (hTableStmt);
        if (rc == SQL_NO_DATA_FOUND)
                break;
        else if (checkError (rc, SQL_HANDLE_STMT, hTableStmt, (SQLCHAR *) "Error in Step 2 -- SQLFetch failed for table\n"))
                goto Exit;

        /* Display the table name */
        fprintf (stdout, "Table Name %s\n", tableName);


        /* For this table, get all its columns */
        rc = SQLColumns (hColStmt, NULL, 0, NULL, 0, tableName, SQL_NTS, NULL, SQL_NTS); 
        if (checkError (rc, SQL_HANDLE_STMT, hColStmt, (SQLCHAR *) "Error in Step 3 -- SQLColumns failed\n"))
                goto Exit;

        /* Bind the result set column #4 - 'COLUMN_NAME' */
        rc = SQLBindCol (hColStmt, 4, SQL_C_CHAR, colName, BUFFER_LEN, &cbColName);
        if (checkError (rc, SQL_HANDLE_STMT, hColStmt, (SQLCHAR *) "Error in Step 3 -- SQLBindCol failed for column\n"))
                goto Exit;

        /* Fetch and display the column names */
        while (1)
        {
                rc = SQLFetch (hColStmt);
            if (rc == SQL_NO_DATA_FOUND)
                    break;
            else if (checkError (rc, SQL_HANDLE_STMT, hColStmt, (SQLCHAR *) "Error in Step 3 -- SQLFetch failed for column\n"))
                        goto Exit;

            /* Display the results */
            fprintf (stdout, "\tColumn: %s\n", colName);
        }


        /* For this table, get all its primary key columns */
        rc = SQLPrimaryKeys (hPriKeyStmt, NULL, 0, NULL, 0, tableName, SQL_NTS); 
        if (checkError (rc, SQL_HANDLE_STMT, hPriKeyStmt, (SQLCHAR *) "Error in Step 3 -- SQLPrimaryKeys failed\n"))
                goto Exit;

        /* Bind the result set column #4 - 'COLUMN_NAME' */
        rc = SQLBindCol (hPriKeyStmt, 4, SQL_C_CHAR, priKeyName, BUFFER_LEN, &cbPriKeyName);
        if (checkError (rc, SQL_HANDLE_STMT, hPriKeyStmt, (SQLCHAR *) "Error in Step 3 -- SQLBindCol failed for primary key\n"))
                goto Exit;

        /* Fetch and display the column names */
        while (1)
        {
                rc = SQLFetch (hPriKeyStmt);
            if (rc == SQL_NO_DATA_FOUND)
                    break;
            else if (checkError (rc, SQL_HANDLE_STMT, hPriKeyStmt, (SQLCHAR *) "Error in Step 3 -- SQLFetch failed for column\n"))
                        goto Exit;

            /* Display the results */
            fprintf (stdout, "\t\t Primary Key Column: %s\n", priKeyName);
        }

        /* Close the result set cursors for columns and primary keys */
        rc = SQLCloseCursor (hColStmt);
        if (checkError (rc, SQL_HANDLE_STMT, hColStmt, (SQLCHAR *) "Error in Step 3 -- SQLCloseCursor failed for column\n"))
                goto Exit;

        rc = SQLCloseCursor (hPriKeyStmt);
        if (checkError (rc, SQL_HANDLE_STMT, hPriKeyStmt, (SQLCHAR *) "Error in Step 3 -- SQLCloseCursor failed for primary key\n"))
                    goto Exit;
    }

    /* Close the result set cursor for table */
    rc = SQLCloseCursor (hTableStmt);
    if (checkError (rc, SQL_HANDLE_STMT, hTableStmt, (SQLCHAR *) "Error in Step 3 -- SQLCloseCursor failed for table\n"))
                goto Exit;


   
        fprintf (stdout, "STEP 2 done...catalog information obtained from the database\n");




    Exit:

    /* CLEANUP: Close the statement handles
    **          Free the statement handles
    **          Disconnect from the datasource
    **          Free the connection and environment handles
    **          Exit
    */

    /* Close the statement handle */
    SQLFreeStmt (hTableStmt, SQL_CLOSE);
    SQLFreeStmt (hColStmt, SQL_CLOSE);
    SQLFreeStmt (hPriKeyStmt, SQL_CLOSE);

    /* Free the statement handle */
    SQLFreeHandle (SQL_HANDLE_STMT, hTableStmt);
    SQLFreeHandle (SQL_HANDLE_STMT, hColStmt);
    SQLFreeHandle (SQL_HANDLE_STMT, hPriKeyStmt);

        /* Disconnect from the data source */
    SQLDisconnect (hdbc);

    /* Free the environment handle and the database connection handle */
    SQLFreeHandle (SQL_HANDLE_DBC, hdbc);
    SQLFreeHandle (SQL_HANDLE_ENV, henv);

    fprintf (stdout,"\n\nHit <Enter> to terminate the program...\n\n");
    in = getchar ();
    return (rc);
}
```
编译
```shell
rm -f catalog.o
gcc -c -fsigned-char -DNO_WIN32 -O -I/opt/gbase/incl/cli -I/opt/gbase/incl/esql -I/opt/gbase/incl/dmi catalog.c
gcc -o catalog catalog.o -L/opt/gbase/lib/cli -L/opt/gbase/lib/esql -lifcli -lifdmr -L/opt/gbase/lib/esql -lifgls -lifglx -lm -lnsl
```
测试执行
```shell
./catalog
STEP 1 done...connected to database  
```
按回车退出程序  

更多demo参考csdk安装目录/opt/gbase/demo/cli

更多odbc使用参考《GBase 8s V8.8 ODBC Driver 程序员指南》

