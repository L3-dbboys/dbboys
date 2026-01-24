### 配置odbc
参考【[WINDOWS安装CSDK及配置ODBC](/安装配置/客户端安装CSDK【ODBC、C等接口】/WINDOWS安装CSDK及配置ODBC.md)】

### 代码示例
```c#
using System;
using System.Data.Odbc;

namespace OdbcConnectionDemo
{
    class Program
    {
        static void Main(string[] args)
        {
            // 替换为你的实际连接字符串（可使用 DSN 或 DSN-less）
            string connectionString = "DSN=odbc_demo;UID=gbasedbt;PWD=GBase123;";
            //或使用连接串
            //"Driver={GBase ODBC DRIVER};Host=192.168.1.101;Service=9088;Protocol=onsoctcp;Server=gbase01;Database=testdb;Uid=gbasedbt;Pwd=GBase123;"
            

            // 示例 SQL 查询
            string query = "SELECT * FROM my_table";

            using (OdbcConnection connection = new OdbcConnection(connectionString))
            {
                try
                {
                    connection.Open();
                    Console.WriteLine("连接成功！");

                    using (OdbcCommand command = new OdbcCommand(query, connection))
                    using (OdbcDataReader reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            // 打印第一列数据
                            Console.WriteLine(reader[0]);
                        }
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine("连接失败: " + ex.Message);
                }
            }

            Console.WriteLine("按任意键退出...");
            Console.ReadKey();
        }
    }
}
```