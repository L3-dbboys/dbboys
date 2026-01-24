---
hide_title: false
sidebar_position: 588
---
错误信息
```
-588    Invalid host variable number.
-588    主变量号无效。
```

错误原因
```
绑定变量参数个数超过32766个
```

解决方案
```
减少绑定变量个数到<=32766个
```

以下程序可重现：
```
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
        //drop table if exists
        String tabname="t";
        String dropTab="drop table if exists "+tabname;
        conn.createStatement().execute(dropTab);
        System.out.println("Table "+tabname+" droped.");

        //create table
        String createTab="create table "+tabname+"(id varchar(50))";
        conn.createStatement().execute(createTab);
        System.out.println("Table "+tabname+" created.");

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM t WHERE id IN (");
        //for (int i = 0; i < 32765 ; i++) { //正常
        for (int i = 0; i < 40000 ; i++) { //报错

                sqlBuilder.append("?").append(", ");
        }
        sqlBuilder.append("?)");
        String sql = sqlBuilder.toString();
        System.out.println(sql);
        pstmt = (PreparedStatement) conn.prepareStatement(sql);

        //for (int i = 1; i <= 32766;i++) { //正常
        for (int i = 1; i <= 40001;i++) { //报错

                pstmt.setObject(i, "4");
        }

        ResultSet rs=pstmt.executeQuery();
        while(rs.next()){
            System.out.println(rs.getString(1));
        }
        System.out.println("Data select finished.");
    }
}
```

