```java
import java.sql.*;
public class Main {

    private static String URL_STRING = "jdbc:gbasedbt-sqli://192.168.17.101:9088/testdb:GBASEDBTSERVER=gbase01;IFX_LOCK_MODE_WAIT=10";
    //单机连接串

    //private static String URL_STRING = "jdbc:gbasedbt-sqli://192.168.17.101:9088/testdb:GBASEDBTSERVER=gbase01;GBASEDBTSERVER_SECONDARY=gbase03;IFXHOST_SECONDARY=192.168.17.103;PORTNO_SECONDARY=9088;ENABLE_HDRSWITCH=true;IFX_LOCK_MODE_WAIT=10";
    //集群连接串,第一节点192.168.17.101 端口9088 实例名gbase01，第二节点192.168.17.103 端口9088 实例名gbase03
    //使用该连接串连接数据库，数据库首先连接第一节点gbase01实例，如该实例已切换为备机，会导致应用不可写入，不推荐使用
    //该配置适用HAC、RHAC

    //private static String URL_STRING = "jdbc:gbasedbt-sqli:/testdb:GBASEDBTSERVER=db_group;SQLH_TYPE=FILE;SQLH_FILE=D:\\sqlhosts.txt;IFX_LOCK_MODE_WAIT=10";
    //集群连接串，使用配置文件连接数据库组
    //配置文件D:\sqlhosts.txt
    /*
    db_group        group   -       -
    gbase01 onsoctcp 192.168.17.101 9088    g=db_group
    gbase02 onsoctcp 192.168.17.102 9088    g=db_group
    */
    //使用该连接串连接数据库，数据库自动连接到配置文件中数据库组的主节点，推荐使用
    //该配置适用HAC、RHAC
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
        String createTab="create table "+tabname+"(col1 varchar(200))";
        conn.createStatement().execute(createTab);
        System.out.println("Table "+tabname+" created.");

        //insert data to table
        String InsertTab="insert into t values(?)";
        pstmt = (PreparedStatement) conn.prepareStatement(InsertTab);
        pstmt.setString(1,"天津南大通用数据技术股份有限公司");
        pstmt.executeUpdate();
        System.out.println("Data inserted into "+tabname+". nrows=1.");

        //select data from table
        String SelectTab="select * from t where col1=?";
        pstmt = (PreparedStatement) conn.prepareStatement(SelectTab);
        pstmt.setString(1,"天津南大通用数据技术股份有限公司");
        ResultSet rs=pstmt.executeQuery();
        while(rs.next()){
            System.out.println(rs.getString(1));
        }
        System.out.println("Data select finished.");
    }
}
```
参考文档【[GBase 8s JDBC Driver 程序员指南.pdf](https://www.dbboys.com/dl/gbase8s/docs/JDBC_Driver_Programmer_Guide.pdf)】