```java
import java.sql.*;
import java.util.Date;

public class Main {
    private static String URL_STRING = "jdbc:gbasedbt-sqli://192.168.17.101:9088/testdb:GBASEDBTSERVER=gbase01;IFX_LOCK_MODE_WAIT=10;";
    private static String user = "gbasedbt";
    private static String password = "GBase123";
    static Statement         stmt;
    static PreparedStatement pstmt  = null;
    static Connection conn = null;
    public static void main(String[] args)  {
        try {
            Class.forName("com.gbasedbt.jdbc.Driver");
        } catch (Exception e) {
            return;
        }
        try {
            conn = DriverManager.getConnection(URL_STRING,user, password);
            if (!conn.isClosed())
                System.out.println("illegal character check program start at "+new Date());
        } catch (SQLException e) {
            System.out.println("FAILED: failed to connect: " + e.toString());
            return;
        }

	if(args.length==3){
		checkCol(args[0].toString(),args[1].toString(),args[2].toString());
	}else if(args.length==2){
            	checkTab(args[0].toString(),args[1].toString());
	}else{
		checkDB(args[0].toString());
	}
    }
    public static String getScanSql(String tabname,String colname) throws SQLException {
        String SQL=null;
        if(colname!=null){
            SQL="select c.colname from syscolumns c,systables t where t.tabid=c.tabid and mod(coltype,256) in(0,13,15,16,40) and colname='"+colname+"' and tabname='"+tabname+"'";
        }else{
            SQL="select c.colname from syscolumns c,systables t where t.tabid=c.tabid and mod(coltype,256) in(0,13,15,16,40) and tabname='"+tabname+"'";
        }
        String scanSql="select ";
        ResultSet rs=conn.createStatement().executeQuery(SQL);
        while(rs.next()){
            scanSql+=rs.getString(1)+"::lvarchar as "+rs.getString(1)+",";
        }
        rs.close();
        scanSql+=" 1 from "+tabname;
        return scanSql;
    }
    public static void checkDB(String dbname){
        Long start=System.currentTimeMillis();
        Long finish=System.currentTimeMillis();
        String tabname=null;
        Integer charcols=null;
        ResultSet tabscan=null;
        Integer tabcount=null;
        Integer tabseq=0;
        Integer rownumber=null;
        String scanSql=null;
        String errorColName=null;
        try {
            Statement stmt=conn.createStatement();
            Statement stmt1=conn.createStatement();
            stmt.execute("database "+dbname);
            ResultSet rs= stmt.executeQuery("select count(*) from systables t where tabid>99 and tabtype='T' and exists(select 1 from syscolumns c where c.tabid=t.tabid and coltype in(0,13,15,16,40) )");
            while(rs.next()){
                tabcount=rs.getInt(1);
            }
            System.out.println("Total count of tables with char/varchar/nchar/nvarchar/lvarchar columns:"+tabcount);
            rs= stmt.executeQuery("select tabname from systables t where tabid>99 and tabtype='T' and exists(select 1 from syscolumns c where c.tabid=t.tabid and coltype in(0,13,15,16,40) )");
            while(rs.next()){
                tabseq++;
                tabname=rs.getString(1);
                System.out.println("----------------------------------------------------------------------");
                System.out.println("check table "+dbname+":"+tabname+" start...seq is "+tabseq+"/"+tabcount);
                start=System.currentTimeMillis();
                scanSql=getScanSql(tabname,null);
                tabscan=stmt1.executeQuery(scanSql);
                rownumber=0;
                charcols=tabscan.getMetaData().getColumnCount()-1;
                while(tabscan.next()){
                    rownumber++;
                    if(rownumber%10000==0){
                        finish=System.currentTimeMillis();
                        System.out.println("table "+tabname+" "+rownumber+" rows checked,used time:"+(finish-start)+"ms");
                        //start=finish;
                    }
                    for(Integer i=1;i<=charcols;i++){
                        try {
                            tabscan.getObject(i);
                        }catch (SQLException e) {
                            errorColName=tabscan.getMetaData().getColumnName(i);
                            System.out.println("illegal character postion:tabname:"+tabname+".rownumber:"+rownumber+".column:"+errorColName);
                            System.out.println("use the follow sql to select the illegal character row:");
                            System.out.println("select skip "+(rownumber-1)+" first 1 "+errorColName+"::lvarchar as "+errorColName+"_error,* from "+tabname+";");
                        }
                    }
                }
                finish=System.currentTimeMillis();
                System.out.println("check table "+tabname+" finished."+rownumber+" rows checked,used time:"+(finish-start)+"ms.");
                System.out.println("----------------------------------------------------------------------");
                System.out.println("");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        System.out.println("illegal character check program finished at "+new Date());
    }

    public static void checkTab(String dbname,String tabname){
        Long start=System.currentTimeMillis();
        Long finish=System.currentTimeMillis();
        Integer charcols=null;
        ResultSet tabscan=null;
        Integer rownumber=null;
        String scanSql=null;
        String errorColName=null;
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("database " + dbname);
            System.out.println("----------------------------------------------------------------------");
            System.out.println("check table " +dbname+":"+ tabname + " start...");
            start = System.currentTimeMillis();
            scanSql = getScanSql(tabname,null);
            if (scanSql.substring(0, 9).equals("select  1") ) {
                System.out.println("table " + tabname + " not exists or does not have any char columns,not scan");
            }else{

            tabscan = stmt.executeQuery(scanSql);
            rownumber = 0;
            charcols = tabscan.getMetaData().getColumnCount() - 1;
            while (tabscan.next()) {
                rownumber++;
                if (rownumber % 10000 == 0) {
                    finish = System.currentTimeMillis();
                    System.out.println("table " + tabname + " " + rownumber + " rows checked,used time:" + (finish - start) + "ms");
                    //start=finish;
                }
                for (Integer i = 1; i <= charcols; i++) {
                    try {
                        tabscan.getObject(i);
                    } catch (SQLException e) {
                        errorColName = tabscan.getMetaData().getColumnName(i);
                        System.out.println("illegal character postion:tabname:" + tabname + ".rownumber:" + rownumber + ".column:" + errorColName);
                        System.out.println("use the follow sql to select the illegal character row:");
                        System.out.println("select skip " + (rownumber - 1) + " first 1 " + errorColName + "::lvarchar as " + errorColName + "_error,* from " + tabname + ";");
                    }
                }
            }
            finish = System.currentTimeMillis();
            System.out.println("check table " + tabname + " finished." + rownumber + " rows checked,used time:" + (finish - start) + "ms.");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("");
        }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        System.out.println("illegal character check program finished at "+new Date());
    }

    public static void checkCol(String dbname,String tabname,String colname){
        Long start=System.currentTimeMillis();
        Long finish=System.currentTimeMillis();
        Integer charcols=null;
        ResultSet tabscan=null;
        Integer rownumber=null;
        String scanSql=null;
        String errorColName=null;
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("database " + dbname);
            System.out.println("----------------------------------------------------------------------");
            System.out.println("check column " +dbname+":"+ tabname + "("+colname+") start...");
            start = System.currentTimeMillis();
            scanSql = getScanSql(tabname,colname);
            if (scanSql.substring(0, 9).equals("select  1") ) {
                System.out.println("table " + tabname + " does not exists or column "+colname+" not exists or column is not char,not scan");
            }else{

                tabscan = stmt.executeQuery(scanSql);
                rownumber = 0;
                charcols = tabscan.getMetaData().getColumnCount() - 1;
                while (tabscan.next()) {
                    rownumber++;
                    if (rownumber % 10000 == 0) {
                        finish = System.currentTimeMillis();
                        System.out.println("table " + tabname + " " + rownumber + " rows checked,used time:" + (finish - start) + "ms");
                        //start=finish;
                    }
                    for (Integer i = 1; i <= charcols; i++) {
                        try {
                            tabscan.getObject(i);
                        } catch (SQLException e) {
                            errorColName = tabscan.getMetaData().getColumnName(i);
                            System.out.println("illegal character postion:tabname:" + tabname + ".rownumber:" + rownumber + ".column:" + errorColName);
                            System.out.println("use the follow sql to select the illegal character row:");
                            System.out.println("select skip " + (rownumber - 1) + " first 1 " + errorColName + "::lvarchar as " + errorColName + "_error,* from " + tabname + ";");
                        }
                    }
                }
                finish = System.currentTimeMillis();
                System.out.println("check table " + tabname + " finished." + rownumber + " rows checked,used time:" + (finish - start) + "ms.");
                System.out.println("----------------------------------------------------------------------");
                System.out.println("");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        System.out.println("illegal character check program finished at "+new Date());
    }
}

```
运行：
```
javac Main.java
export CLASSPATH=$CLASSPATH:gbasedbtjdbc_3.3.0_2_36477d.jar
java Main [dbname] [tabname] [colname]
```
参考输出（全库，扫描100GB约1小时）：
```
[gbasedbt@dbhost1 202]$ java Main testdb
illegal character check program start at Fri Mar 10 15:27:07 CST 2023
Total count of tables with char/varchar/nchar/nvarchar/lvarchar columns:10
----------------------------------------------------------------------
check table testdb:t start...seq is 1/10
illegal character postion:tabname:t.rownumber:1.column:col1
use the follow sql to select the illegal character row:
select skip 0 first 1 col1::lvarchar as col1_error,* from t;
table t 10000 rows checked,used time:134ms
table t 20000 rows checked,used time:167ms
table t 30000 rows checked,used time:190ms
table t 40000 rows checked,used time:224ms
table t 50000 rows checked,used time:240ms
table t 60000 rows checked,used time:257ms
table t 70000 rows checked,used time:274ms
table t 80000 rows checked,used time:292ms
table t 90000 rows checked,used time:309ms
table t 100000 rows checked,used time:330ms
table t 110000 rows checked,used time:348ms
table t 120000 rows checked,used time:363ms
table t 130000 rows checked,used time:380ms
illegal character postion:tabname:t.rownumber:131073.column:col1
use the follow sql to select the illegal character row:
select skip 131072 first 1 col1::lvarchar as col1_error,* from t;
check table t finished.131073 rows checked,used time:382ms.
----------------------------------------------------------------------

----------------------------------------------------------------------
check table testdb:t6 start...seq is 2/10
illegal character postion:tabname:t6.rownumber:1.column:col1
use the follow sql to select the illegal character row:
select skip 0 first 1 col1::lvarchar as col1_error,* from t6;
check table t6 finished.1 rows checked,used time:6ms.
----------------------------------------------------------------------

----------------------------------------------------------------------
check table testdb:t2 start...seq is 3/10
check table t2 finished.1 rows checked,used time:6ms.
----------------------------------------------------------------------

----------------------------------------------------------------------
check table testdb:t3 start...seq is 4/10
check table t3 finished.7 rows checked,used time:7ms.
----------------------------------------------------------------------

----------------------------------------------------------------------
check table testdb:t4 start...seq is 5/10
check table t4 finished.1 rows checked,used time:5ms.
----------------------------------------------------------------------

----------------------------------------------------------------------
check table testdb:t5 start...seq is 6/10
illegal character postion:tabname:t5.rownumber:1.column:col1
use the follow sql to select the illegal character row:
select skip 0 first 1 col1::lvarchar as col1_error,* from t5;
check table t5 finished.1 rows checked,used time:7ms.
----------------------------------------------------------------------

----------------------------------------------------------------------
check table testdb:company start...seq is 7/10
illegal character postion:tabname:company.rownumber:1.column:coaddr
use the follow sql to select the illegal character row:
select skip 0 first 1 coaddr::lvarchar as coaddr_error,* from company;
illegal character postion:tabname:company.rownumber:2.column:coname
use the follow sql to select the illegal character row:
select skip 1 first 1 coname::lvarchar as coname_error,* from company;
check table company finished.2 rows checked,used time:15ms.
----------------------------------------------------------------------

----------------------------------------------------------------------
check table testdb:t7 start...seq is 8/10
check table t7 finished.0 rows checked,used time:6ms.
----------------------------------------------------------------------

----------------------------------------------------------------------
check table testdb:t9 start...seq is 9/10
illegal character postion:tabname:t9.rownumber:1.column:col1
use the follow sql to select the illegal character row:
select skip 0 first 1 col1::lvarchar as col1_error,* from t9;
check table t9 finished.1 rows checked,used time:7ms.
----------------------------------------------------------------------

----------------------------------------------------------------------
check table testdb:t10 start...seq is 10/10
illegal character postion:tabname:t10.rownumber:1.column:col1
use the follow sql to select the illegal character row:
select skip 0 first 1 col1::lvarchar as col1_error,* from t10;
check table t10 finished.1 rows checked,used time:7ms.
----------------------------------------------------------------------

illegal character check program finished at Fri Mar 10 15:27:07 CST 2023
```