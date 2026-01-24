创建存储过程
```sql
drop procedure testproc;
create procedure testproc(v_in1 int,v_in2 varchar(100),out v_out varchar(100),out p SYS_REFCURSOR)
let v_out=100;
OPEN p FOR 'select skip 10 first 10 tabid,tabname,ustlowts from systables';
end procedure;
```
测试程序
```java
import com.gbasedbt.lang.IfxTypes;

import java.sql.*;
public class Main {

    private static String URL_STRING = "jdbc:gbasedbt-sqli://192.168.17.101:9088/testdb:GBASEDBTSERVER=gbase01;IFX_LOCK_MODE_WAIT=10";

    private static String user = "gbasedbt";
    private static String password = "GBase123";
    static Statement         stmt;
    static PreparedStatement pstmt  = null;
    static Connection conn = null;
    public static void main(String[] args) throws SQLException {
        try {
            Class.forName("com.gbasedbt.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("FAILED: failed to load GBase 8s JDBC driver.");
            return;
        }
        try {
            Long start=System.currentTimeMillis();
            conn = DriverManager.getConnection(URL_STRING,user, password);
            Long finish=System.currentTimeMillis();
            if (!conn.isClosed())
                System.out.println("GBase 8s Connectted Successfully Used "+(finish-start)+"ms");
        } catch (SQLException e) {
            System.out.println("FAILED: failed to connect: " + e.toString());
            return;
        }
        CallableStatement cs = conn.prepareCall("call testproc(?,?,?,?)");
        System.out.println("0");

        // 设置参数
        cs.setInt(1, 123); // 第一个参数
        cs.setString(2, "param2"); // 第二个参数
        System.out.println("1");

        // 注册返回值类型（如果有的话）
        //cs.registerOutParameter(3, java.sql.Types.VARCHAR);
        cs.registerOutParameter(3, Types.VARCHAR);
        cs.registerOutParameter(4, IfxTypes.IFX_TYPE_CURSOR);

        // 执行存储过程
        cs.execute();

        // 获取输出参数
        String result = cs.getString(3);
        System.out.println("Output Parameter: " + 3);
        
        // 获取输出游标
        ResultSet rs=  (ResultSet) cs.getObject(4);
        while(rs.next()){
            System.out.println(rs.getString(2));
        }
    }
}
```