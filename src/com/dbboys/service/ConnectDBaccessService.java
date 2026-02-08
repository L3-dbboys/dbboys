package com.dbboys.service;

import com.dbboys.app.Main;
import com.dbboys.ctrl.SqlTabController;
import com.dbboys.customnode.CustomSpaceChart;
import com.dbboys.customnode.CustomSqlTab;
import com.dbboys.util.*;
import com.dbboys.vo.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class ConnectDBaccessService {
    private static final Logger log = LogManager.getLogger(ConnectDBaccessService.class);
    public List<BackgroundSqlTask> backSqlTask=new ArrayList();
    //点击连接后，获取连接
    public Connection getConnection(Connect connect) throws Exception{
        Connection connection = null;
        String URL_STRING=null;
        String className=null;
        String jarFilePath=null;
        //根据不同类型数据库，同一类型数据库不同版本驱动可能不兼容，需要加载不同外部驱动
        switch (connect.getDbtype()){
            case "GBASE 8S":
                

                if(connect.getPropByName("GBASEDBTSERVER").isEmpty()){
                    URL_STRING = "jdbc:gbasedbt-sqli://" + connect.getIp() + ":" + connect.getPort() + "/"+connect.getDatabase();
                }else{
                    URL_STRING = "jdbc:gbasedbt-sqli:/"+connect.getDatabase()+":SQLH_TYPE=FILE;SQLH_FILE=extlib/GBASE 8S/sqlhosts;";
                }
                className = "com.gbasedbt.jdbc.Driver";
                jarFilePath = "file:extlib/"+connect.getDbtype()+"/"+connect.getDriver();
                break;
            case "oracle":
                break;
            default:
                break;
        }
        ClassLoader platformClassLoader = ClassLoader.getPlatformClassLoader();  //必须设置，否则可能报java.sql.Driver找不到
        URLClassLoader loader = new URLClassLoader(new URL[]{new URL(jarFilePath)}, platformClassLoader);
        Driver driver = (Driver) Class.forName(className, true, loader).newInstance();
        Properties info = new Properties();
        info.setProperty("user", connect.getUsername());
        info.setProperty("password", connect.getPassword());
        JSONArray jsonArray=new JSONArray(connect.getProps());
        for(int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            if(jsonObject.getString("propValue")!=null&&(!jsonObject.getString("propValue").equals(""))){
                info.setProperty(jsonObject.getString("propName"),jsonObject.getString("propValue"));
            }
        }
        connection = driver.connect(URL_STRING, info);
        log.info("conn info is:"+connection.getMetaData().getURL());
        return connection;
    }


    public void changeCommitMode(Connection conn,String commitmode) throws SQLException {
        if(commitmode.equals("手动提交")){
            conn.setAutoCommit(false);
        }
    }
    public void sessionChangeToGbaseMode(Connection conn) {
        try{
            conn.createStatement().execute("set environment sqlmode 'gbase'");
        }catch(SQLException e){
            //e.printStackTrace();
        }
    }


    public List<String> getSqlMode(Connection conn) {
        List<String> sqlmodes=new  ArrayList();
        //list的第一个元素表示当前sqlmode，后面的表示支持的sqlmode
        //String sqlmode="sqlmode=gbase";


        ResultSet rs=null;
        Statement stmt=null;
        try{
            rs=conn.createStatement().executeQuery("select  * from sysmaster:sysdual");
            stmt=conn.createStatement();
            stmt.execute("set environment sqlmode 'gbase'");
            sqlmodes.add("sqlmode=gbase"); //支持多模式，且当前为gbase模式
            try{
                conn.createStatement().executeUpdate("set environment sqlmode 'mysql'"); //判断是否支持mysql模式
                rs=conn.createStatement().executeQuery("select tabid,tabname from systables group by 1");
                conn.createStatement().executeUpdate("set environment sqlmode 'gbase'");
                sqlmodes.add("sqlmode=oracle");
                sqlmodes.add("sqlmode=mysql");
            }catch(SQLException e1){  //当前是Oracle模式
                sqlmodes.add("sqlmode=oracle");
            }
        }catch(SQLException e){
            if(e.getErrorCode()==-201) //支持多模式，且当前不是gbase模式
            {
                try{
                    rs=conn.createStatement().executeQuery("select tabid,tabname from systables group by 1");//当前是mysql模式，支持mysql及oracle模式
                        sqlmodes.add("sqlmode=mysql");
                        sqlmodes.add("sqlmode=oracle");
                        sqlmodes.add("sqlmode=mysql");
                }catch(SQLException e1){  //当前是Oracle模式
                    sqlmodes.add("sqlmode=oracle");
                    sqlmodes.add("sqlmode=oracle");
                    try{ //判断是否支持mysql模式
                        conn.createStatement().executeUpdate("set environment sqlmode 'mysql'");
                        rs=conn.createStatement().executeQuery("select tabid,tabname from systables group by 1");
                        //支持mysql模式，切换回oracle模式
                        sqlmodes.add("sqlmode=mysql");
                        conn.createStatement().executeUpdate("set environment sqlmode 'oracle'");
                    }catch(SQLException e2){
                        //不支持mysql模式
                    }
                }
            }
            else if(e.getErrorCode()==-19840) //不支持oracle及mysql模式
                sqlmodes.add("sqlmode=none");
                //sqlmode="sqlmode=none";
            else
                e.printStackTrace();
        }finally{
            if(rs!=null)
            try{
                rs.close();
            }catch(SQLException e1){

            }
            if(stmt!=null)
            try{
                stmt.close();
            }catch(SQLException e1){

            }

        }


        return sqlmodes;



    }



    public String setConnectInfo(Connect connect) throws Exception{
                String primaryInstance="";
        Connection connection = getConnection(connect);
            //连接切换到gbase模式
            sessionChangeToGbaseMode(connection);
                /*
                ResultSet rs=connection.createStatement().executeQuery("EXECUTE FUNCTION sysadmin:task(\"onstat\",\"-V\");");
                rs.next();
                connect.setInfo(rs.getString(1));
                rs=connection.createStatement().executeQuery("EXECUTE FUNCTION sysadmin:task(\"onstat\",\"-g osi\");");
                rs.next();
                connect.setInfo(connect.getInfo()+rs.getString(1));
                rs=connection.createStatement().executeQuery("EXECUTE FUNCTION sysadmin:task(\"onstat\",\"-g env\");");
                rs.next();
                connect.setInfo(connect.getInfo()+rs.getString(1));
                */
            //查询数据库信息，包括版本，启动环境，系统配置
            ResultSet rs=null;
            String dbversion=null;
            if(connect.getUsername().equals("gbasedbt")){
                //connection.createStatement().executeUpdate("set environment sqlmode 'gbase'");
                rs=connection.createStatement().executeQuery("EXECUTE FUNCTION sysadmin:task('onstat','-V');");
                rs.next();
                dbversion=rs.getString(1).replace("GBase Database Server Version 12.10.FC4G1","").replace(" Software Serial Number AAA#B000000","").replace("\n","");
                if(!dbversion.contains("GBase8s")){
                    DatabaseMetaData metaData = connection.getMetaData();
                    String databaseProductVersion = metaData.getDatabaseProductVersion();
                    dbversion="GBase8sV"+databaseProductVersion+"_"+dbversion;
                }
            }else{
                dbversion="当前用户无权限获取版本信息，请使用gbasedbt用户连接获取\n";
            }
            connect.setDbversion(dbversion); //保存数据库版本,最后有换行
            String info="##########################################################################################\n";
            info+="Instance Boot Information\n";
            info+="##########################################################################################\n";
            //rs=connection.createStatement().executeQuery("select env_name,case upper(trim(env_value)) when 'ZH_CN.GB18030-2000' then 'zh_CN.5488' when 'ZH_CN.UTF8' then 'zh_CN.57372' else trim(env_value) end from sysmaster:sysenv");
            rs=connection.createStatement().executeQuery("select env_name,trim(env_value) from sysmaster:sysenv");

            while(rs.next()){
                info+=String.format("%-30s",rs.getString(1))+rs.getString(2)+"\n";
                if(rs.getString(1).equals("DB_LOCALE")){
                    //if(connect.getDatabase().equals("gbasedbt")||connect.getDatabase().equals("sys")||connect.getDatabase().equals("sysadmin")||connect.getDatabase().equals("sysmaster")||connect.getDatabase().equals("sysutils")||connect.getDatabase().equals("syscdcv1")){
                    //如果连接默认库是系统表，不设置DB_LOCALE
                    //}else{
                    //编辑连接属性propName在前
                    connect.setProps(connect.getProps().replace("{\"propValue\":\"\",\"propName\":\"DB_LOCALE\"}","{\"propValue\":\""+rs.getString(2).toUpperCase().trim().replace("ZH_CN.GB18030-2000","zh_CN.5488").replace("ZH_CN.UTF8","zh_CN.57372")+"\",\"propName\":\"DB_LOCALE\"}"));
                    //新连接顺序propName在前
                    connect.setProps(connect.getProps().replace("{\"propName\":\"DB_LOCALE\",\"propValue\":\"\"}","{\"propName\":\"DB_LOCALE\",\"propValue\":\""+rs.getString(2).toUpperCase().trim().replace("ZH_CN.GB18030-2000","zh_CN.5488").replace("ZH_CN.UTF8","zh_CN.57372")+"\"}"));
                    //}

                }

            };
            rs.close();
            info+="\n##########################################################################################\n";
            info+="System Information\n";
            info+="##########################################################################################\n";
            rs=connection.createStatement().executeQuery("SELECT * from sysmaster:sysmachineinfo ");
            rs.next();
            for(int i=1;i<=24;i++){
                info+=String.format("%-30s",rs.getMetaData().getColumnName(i));
                info+=rs.getString(i)+"\n";
            }
            rs.close();

            if(!connect.getPropByName("GBASEDBTSERVER").isEmpty()){
                rs=connection.createStatement().executeQuery("select dbservername from dual");
                if(rs.next()){
                    primaryInstance=rs.getString(1);
                }
            }
            rs.close();
            connection.close();
            connect.setInfo(info);

            //设置连接驱动的MD5码
            connect.setDrivermd5(new MD5Util().getMD5Checksum(Paths.get("extlib/"+ connect.getDbtype()+"/"+ connect.getDriver()).toFile().getAbsolutePath()));
            return primaryInstance;
    }




    public List<Database> getDatabases(Connect connect) {
        //这个方法除了左侧连接树需要外，连接数是全部gbase语法，执行sql的界面上方选择数据库时也会调用，不一定是gbase语法
        Connection connection = connect.getConn();
        //connection.createStatement().executeUpdate("set environment sqlmode 'gbase'");
        List<Database> catalogs = new ArrayList<>();
        ResultSet rs = null;
        Statement statement=null;
        try{
            statement = connection.createStatement();
            rs=statement.executeQuery("""
                    select t1.*,t2.allocedsize from(
                    SELECT trim(name) dbname,
                    trim(owner) owner,
                    to_char(created,'YYYY-MM-DD')  created_time,
                    TRIM(DBINFO('dbspace',partnum)) AS dbspace,
                    CASE WHEN is_logging+is_buff_log=1 THEN 'unbuffered'
                         WHEN is_logging+is_buff_log=2 THEN 'buffered'
                         WHEN is_logging+is_buff_log=0 THEN 'nolog'
                    ELSE '' END Logging_mode,
                    is_nls,
                    trim(replace(replace(dbs_collate,'57372','UTF8'),'5488','GB18030-2000'))
                    FROM sysmaster:sysdatabases d,sysmaster:sysdbslocale s where d.name=s.dbs_dbsname
                    order by
                    case when name in ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1','gbasedbt','sys')
                    then 0 else 1 end,name
                    ) t1
                    left join
                    (
                    SELECT
                    trim(st.dbsname) dbname,
                    replace(format_units(sum(sin.ti_nptotal*sd.pagesize),'b'),' ','') allocedsize
                    from
                    sysmaster:systabnames st JOIN sysmaster:systabinfo sin ON  st.partnum=sin.ti_partnum
                    JOIN sysmaster:sysdbspaces sd ON sd.dbsnum = trunc(st.partnum/1048576)
                    GROUP BY 1
                    )t2
                    on t1.dbname=t2.dbname
    
    """);
            while(rs.next()) {
                Database database=new Database(rs.getString(1));
                database.setDbOwner(rs.getString(2));
                database.setDbCreated(rs.getString(3));
                database.setDbSpace(rs.getString(4));
                database.setDbLog(rs.getString(5));
                database.setDbUseGLU(rs.getString(6));
                database.setDbLocale(rs.getString(7));
                database.setDbSize(rs.getString(8));
                catalogs.add(database);
            }
        }catch(SQLException e){
            //e.printStackTrace();
            if(e.getErrorCode()==-201){
                try {
                    rs = statement.executeQuery("""
                    select t1.*,t2.allocedsize from(
                    SELECT trim(name) dbname,
                    trim(owner) owner,
                    to_char(created,'YYYY-MM-DD')  created_time,
                    TRIM(DBINFO('dbspace',partnum)) AS dbspace,
                    CASE WHEN is_logging+is_buff_log=1 THEN 'unbuffered'
                         WHEN is_logging+is_buff_log=2 THEN 'buffered'
                         WHEN is_logging+is_buff_log=0 THEN 'nolog'
                    ELSE '' END Logging_mode,
                    is_nls,
                    trim(replace(replace(dbs_collate,'57372','UTF8'),'5488','GB18030-2000'))
                    FROM sysmaster.sysdatabases d,sysmaster.sysdbslocale s where d.name=s.dbs_dbsname
                    order by
                    case when name in ('sysmaster','sysuser','sysadmin','sysutils','sysha','syscdr','syscdcv1','gbasedbt','sys')
                    then 0 else 1 end,name
                    ) t1
                    left join
                    (
                    SELECT
                    trim(st.dbsname) dbname,
                    replace(format_units(sum(sin.ti_nptotal*sd.pagesize),'b'),' ','') allocedsize
                    from
                    sysmaster.systabnames st JOIN sysmaster.systabinfo sin ON  st.partnum=sin.ti_partnum
                    JOIN sysmaster.sysdbspaces sd ON sd.dbsnum = trunc(st.partnum/1048576)
                    GROUP BY 1
                    )t2
                    on t1.dbname=t2.dbname
    
    """);
                    while (rs.next()) {
                        Database database=new Database(rs.getString(1));
                        database.setDbOwner(rs.getString(2));
                        database.setDbCreated(rs.getString(3));
                        database.setDbSpace(rs.getString(4));
                        database.setDbLog(rs.getString(5));
                        database.setDbUseGLU(rs.getString(6));
                        database.setDbLocale(rs.getString(7));
                        database.setDbSize(rs.getString(8));
                        catalogs.add(database);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                MetadataTreeviewUtil.connectionDisconnected();
            }else{
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "[" + e.getErrorCode() + "]" + e.getMessage());
                });
            }
        }finally {
            if(rs!=null)
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            if(statement!=null)
                try {
                    statement.close();
                } catch (SQLException e) {
                }
        }
        return catalogs;
    }


    public List<User> getUsers(TreeItem treeItem) {
        //先切换为gbase语法
        Connection connection = getMetaSession(treeItem);
        List<User> users = new ArrayList<>();
        ResultSet rs = null;
        Statement statement=null;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("""
                    select username from sysuser:sysusermap where username!='public';
    """);
            while (rs.next()) {
                User user = new User(rs.getString(1));
                users.add(user);
            }
        }catch (SQLException e) {
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }finally {
            if(rs!=null)
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            if(statement!=null)
                try {
                    statement.close();
                } catch (SQLException e) {
                }
        }


        return users;
    }
    //treeview元素激活数据库，首先使用数据管理持有的长连接执行，如果字符集不一样无法切换，断开数据管理的连接重新连接
    public String activeDatabase(Connect connect, Database database, SqlTabController sqlTabController) {
        String DBLocale=database.getDbLocale();
        String result=null;
        Connection connection = connect.getConn();
        try {

            //先关闭手动提交，才能切换数据库
            if(sqlTabController.sql_commitmode_choicebox.getValue().equals("手动提交"))
            {
                connection.setAutoCommit(true);
            }
            //切库之前关闭游标，避免后续关闭连接时报错-267，如果连接已断开，关闭游标可能报错79716导致重复弹出是否需要重新连接，故出现错误在此处处理
            connection.createStatement().executeUpdate("database " + database.getName());
            if(sqlTabController.sql_commitmode_choicebox.getValue().equals("手动提交"))
            {
                connection.setAutoCommit(false);
            }
            connect.setDatabase(database.getName()); //切换后执行sql的库名变更
            result="success";
        }catch (SQLException e) {
            e.printStackTrace();
            //log mode ansi切换为其他字符集库时报错-349

            if(e.getErrorCode() == -23197||e.getErrorCode()==-349) {
                //如果无法切换库，关闭原连接，创建新连接
                try {
                    sqlTabController.closeResultSet();
                    connection.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        //如果有断开连接错误，肯定是连接有resultset或者statement没close
                        AlterUtil.CustomAlert("错误", "["+ex.getErrorCode()+"]"+ex.getMessage());
                    });
                }
                //创建新的连接
                connect.setProps(modifyProps(connect, DBLocale));
                connect.setDatabase(database.getName());
                try {
                    connection = getConnection(connect);
                    if(sqlTabController.sql_commitmode_choicebox.getValue().equals("手动提交"))
                    {
                        connection.setAutoCommit(false);
                    }
                    result="success";
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", ex.toString());
                    });
                }
                connect.setConn(connection);

                //如果不是字符集错误，可能是连接已断开等错误，无权限等，报错
            }
            else if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                result="disconnected";

                e.printStackTrace();
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }
        }
        return result;
    }

    public String modifyProps(Connect connect, String DBlocale){
        String props=null;
        DBlocale = DBlocale.replaceAll("(?i)" + "UTF8", "57372").replaceAll("(?i)" + "GB18030-2000", "5488");
        //String props = "[{\"propName\":\"DB_LOCALE\",\"propValue\":\"" + DBlocale + "\"}]";
        JSONArray jsonArray =new JSONArray(connect.getProps());
        JSONArray jsonArraynew =new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(!jsonObject.getString("propName").equals("DB_LOCALE")){
                jsonArraynew.put(jsonObject);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("propName", "DB_LOCALE");
        jsonObject.put("propValue", DBlocale);
        jsonArraynew.put(jsonObject);
        return jsonArraynew.toString();
    }


    public Boolean testConn(Connect connect) {
        Boolean result = false;
        ResultSet rs = null;
        if(connect.getConn()!=null){
            try {
                rs=connect.getConn().createStatement().executeQuery("select first 1 tabid from systables");
                result=true;
            }catch (SQLException e) {
                e.printStackTrace();

            }finally {
                if(rs!=null){
                    try{rs.close();}catch(SQLException e){
                        rs=null;
                    }
                }
            }
        }
        return result;
    }


    public List<String> getDBspaceForCreateDatabase(Connect connect) throws Exception{
        Connection connection = connect.getConn();
        List<String> dbspaces = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet rs=statement.executeQuery("SELECT name,pgsize,\n" +
                "CASE when extendablechunks >0 THEN 'autoextendable' ELSE free_size||'GB Free' END AS freesize\n" +
                "\n" +
                "from(\n" +
                "SELECT trim(B.name) as name,\n" +
                "CASE  WHEN (sysmaster:bitval(B.flags,'0x10')>0 AND sysmaster:bitval(B.flags,'0x2')>0)\n" +
                "  THEN 'MirroredBlobspace'\n" +
                "  WHEN sysmaster:bitval(B.flags,'0x10')>0  THEN 'Blobspace'\n" +
                "  WHEN sysmaster:bitval(B.flags,'0x2000')>0 AND sysmaster:bitval(B.flags,'0x8000')>0\n" +
                "  THEN 'TempSbspace'\n" +
                "  WHEN sysmaster:bitval(B.flags,'0x2000')>0 THEN 'TempDbspace'\n" +
                "  WHEN (sysmaster:bitval(B.flags,'0x8000')>0 AND sysmaster:bitval(B.flags,'0x2')>0)\n" +
                "  THEN 'MirroredSbspace'\n" +
                "  WHEN sysmaster:bitval(B.flags,'0x8000')>0  THEN 'SmartBlobspace'\n" +
                "  WHEN sysmaster:bitval(B.flags,'0x2')>0    THEN 'MirroredDbspace'\n" +
                "        ELSE   'Dbspace'\n" +
                "END  as dbstype,\n" +
                " round(sum(decode(mdsize,-1,nfree,udfree))*2/1024/1024,2) as free_size,\n" +
                "  TRUNC(MAX(A.pagesize/1024))||\"K Page,\" as pgsize,\n" +
                "  sum(is_extendable) extendablechunks\n" +
                "FROM sysmaster:syschunks A, sysmaster:sysdbstab B\n" +
                "WHERE A.dbsnum = B.dbsnum\n" +
                " GROUP BY name, 2\n" +
                "ORDER BY extendablechunks DESC,free_size DESC)\n" +
                "WHERE dbstype='Dbspace'");
        while(rs.next()) {
            dbspaces.add(rs.getString(1)+"("+rs.getString(2)+rs.getString(3).replace("autoextendable","自动扩展").replace("Free","可用")+")");
        }
        rs.close();
        statement.close();
        return dbspaces;
    }

    //更改默认库
    public Boolean changeDefaultDatabase(Connect connect, Database database) {
        Boolean result=false;
        try {
            //多个statment同时执行executeUpdate会受阻
            connect.getConn().createStatement().executeUpdate("database " + database.getName());
            connect.setDatabase(database.getName());
            connect.setProps(modifyProps(connect,database.getDbLocale()));
            SqliteDBaccessUtil.updateConnect(connect);
        }catch (SQLException e) {
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730) {
                MetadataTreeviewUtil.connectionDisconnected();
            }
            //如果是ansi库切其他字符集库报错为-349
            else if(e.getErrorCode() == -23197||e.getErrorCode()==-349) {
                //如果无法切换库，切回原库
                try {
                    connect.getConn().close();
                    connect.setDatabase(database.getName());
                    connect.setProps(modifyProps(connect,database.getDbLocale()));
                    connect.setConn(getConnection(connect));
                    sessionChangeToGbaseMode(connect.getConn());
                    SqliteDBaccessUtil.updateConnect(connect);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
            else {
                AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
            }


        }
        return result;
    }



    public void executeBackgroundSql(TreeItem<TreeData> treeItem,String sql,String Charset) {
        Task BGTask=new Task() {
            String newsql=sql;
            @Override
            protected Object call() throws Exception {
                UpdateResult updateResult=new UpdateResult();
                Connect connect=new Connect();
                String DatabaseName="";
                String DBLocale="";
                BackgroundSqlTask bgsql=new BackgroundSqlTask();
                if(treeItem.getValue() instanceof DatabaseFolder){
                    connect=MetadataTreeviewUtil.getMetaConnect(treeItem);
                    DatabaseName="sysmaster";
                    DBLocale=Charset;
                }else if(treeItem.getValue() instanceof Database){
                    connect=MetadataTreeviewUtil.getMetaConnect(treeItem);
                    DBLocale=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getDbLocale();
                    if(sql.startsWith("drop")||sql.startsWith("rename")){
                        DatabaseName="sysmaster";
                    }else{
                        DatabaseName=treeItem.getValue().getName();
                    }
                }else{
                    connect=MetadataTreeviewUtil.getMetaConnect(treeItem);
                    DatabaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
                    DBLocale=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getDbLocale();
                }
                Connection conn=newConnection(connect,DatabaseName,DBLocale);
                if(conn!=null) {
                    Long beginTime=System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    try {
                        //如果是统计更新，检查是否有索引，如果有索引，转换为对索引字段的高优
                        if(sql.contains("update statistics for table")&&!sql.equals("update statistics for table force"))
                        {
                            String tabname=sql.split("table ")[1];
                            List indexColList=new ArrayList();
                            PreparedStatement pstmt=conn.prepareStatement("""
                            SELECT
                            trim( case when i.part1 > 0 then( select colname from syscolumns where colno = i.part1 and tabid = i.tabid ) else '' end )|| trim( case when i.part2 > 0 then( select ',' || colname from syscolumns where colno = i.part2 and tabid = i.tabid ) else '' end )|| trim( case when i.part3 > 0 then( select ',' || colname from syscolumns where colno = i.part3 and tabid = i.tabid ) else '' end )|| trim( case when i.part4 > 0 then( select ',' || colname from syscolumns where colno = i.part4 and tabid = i.tabid ) else '' end )|| trim( case when i.part5 > 0 then( select ',' || colname from syscolumns where colno = i.part5 and tabid = i.tabid ) else '' end )|| trim( case when i.part6 > 0 then( select ',' || colname from syscolumns where colno = i.part6 and tabid = i.tabid ) else '' end )|| trim( case when i.part7 > 0 then( select ',' || colname from syscolumns where colno = i.part7 and tabid = i.tabid ) else '' end )|| trim( case when i.part8 > 0 then( select ',' || colname from syscolumns where colno = i.part8 and tabid = i.tabid ) else '' end )|| trim( case when i.part9 > 0 then( select ',' || colname from syscolumns where colno = i.part9 and tabid = i.tabid ) else '' end )|| trim( case when i.part10 > 0 then( select ',' || colname from syscolumns where colno = i.part10 and tabid = i.tabid ) else '' end )|| trim( case when i.part11 > 0 then( select ',' || colname from syscolumns where colno = i.part11 and tabid = i.tabid ) else '' end )|| trim( case when i.part12 > 0 then( select ',' || colname from syscolumns where colno = i.part12 and tabid = i.tabid ) else '' end )|| trim( case when i.part13 > 0 then( select ',' || colname from syscolumns where colno = i.part13 and tabid = i.tabid ) else '' end )|| trim( case when i.part14 > 0 then( select ',' || colname from syscolumns where colno = i.part14 and tabid = i.tabid ) else '' end )|| trim( case when i.part15 > 0 then( select ',' || colname from syscolumns where colno = i.part15 and tabid = i.tabid ) else '' end )|| trim( case when i.part16 > 0 then( select ',' || colname from syscolumns where colno = i.part16 and tabid = i.tabid ) else '' end )\s
                            from
                            systables t  join sysindexes i on
                            t.tabid = i.tabid
                            where
                            t.tabtype = 'T'
                            and tabname=?
                            """
                            );
                            pstmt.setString(1,tabname);
                            ResultSet rs=pstmt.executeQuery();
                            while(rs.next()){
                                for (String col:rs.getString(1).split(",")){
                                    if(!indexColList.contains(col)){
                                        indexColList.add(col);
                                    }
                                }
                            }
                            if(rs!=null)rs.close();
                            if(indexColList.size()>0) {
                                newsql = "update statistics high for table " + tabname + "(" + String.join(",", indexColList) + ") force";
                            }
                        }
                        //获取索引并转换sql完成

                        //更新后台任务界面
                        updateResult.setConnectId(connect.getId());
                        updateResult.setDatabase(DatabaseName);
                        updateResult.setUpdateSql(newsql);
                        updateResult.setStartTime(sdf.format(beginTime));

                        bgsql.setBeginTime(sdf.format(beginTime));
                        bgsql.setConnName(connect.getName());
                        bgsql.setDatabaseName(DatabaseName);
                        bgsql.setSql(newsql);
                        backSqlTask.add(bgsql);
                        Platform.runLater(() -> {
                            Main.mainController.status_backsql_stop_button.setDisable(false);
                            Main.mainController.status_backsql_count_label.setText("有" + backSqlTask.size() + "个后台任务正在运行");
                            Main.mainController.status_backsql_count_label.setStyle("-fx-font-size: 7;-fx-text-fill: black");
                            PopupWindowUtil.sql_task_tableview.getItems().clear();
                            PopupWindowUtil.sql_task_tableview.getItems().addAll(backSqlTask);
                        });
                        //更新后台任务界面结束

                        bgsql.setStmt(conn.createStatement());
                        int affectRows = bgsql.getStmt().executeUpdate(newsql);
                        bgsql.getStmt().close();
                        updateResult.setAffectedRows(affectRows);
                        Long endtime = System.currentTimeMillis();
                        updateResult.setElapsedTime(String.format("%.3f", (endtime - beginTime) / 1000.0) + " sec");
                        updateResult.setEndTime(sdf.format(endtime));
                        updateResult.setMark("界面操作任务,独立事务");
                        SqliteDBaccessUtil.saveSqlHistory(updateResult);
                        //backSqlTask.remove(bgsql);
                        Platform.runLater(() -> {
                            backSqlTask.remove(bgsql);
                            if (backSqlTask.size() == 0) {
                                Main.mainController.status_backsql_stop_button.setDisable(true);
                                Main.mainController.status_backsql_count_label.setText("没有正在运行的后台任务");
                                Main.mainController.status_backsql_count_label.setStyle("-fx-font-size: 7;-fx-text-fill: #888");
                            }
                            PopupWindowUtil.sql_task_tableview.getItems().clear();
                            PopupWindowUtil.sql_task_tableview.getItems().addAll(backSqlTask);
                        });

                    } catch (SQLException e) {
                        if(e.getErrorCode()!=-213){
                            Platform.runLater(() -> {
                                //if(e.getErrorCode()!=-213){
                                AlterUtil.CustomAlert("后台任务错误", "连接名称: " + bgsql.getConnName() + "/" + bgsql.getDatabaseName() + "\n执行任务: " + bgsql.getSql() + "\n错误信息: " + e.getErrorCode() + " " + e.getMessage());
                                //}
                            });
                        }
                        //抛出错误，标记任务失败，不执行onsuccess
                        throw new Exception("error");
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            AlterUtil.CustomAlert("错误", e.toString());
                        });
                        throw new Exception("error");
                    } finally {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        backSqlTask.remove(bgsql);
                        Platform.runLater(() -> {
                            if (backSqlTask.size() == 0) {
                                Main.mainController.status_backsql_stop_button.setDisable(true);
                                Main.mainController.status_backsql_count_label.setText("没有正在运行的后台任务");
                                Main.mainController.status_backsql_count_label.setStyle("-fx-font-size: 7;-fx-text-fill: #888");
                            } else {
                                Main.mainController.status_backsql_count_label.setText("有" + backSqlTask.size() + "个正在运行的后台任务");
                            }
                            PopupWindowUtil.sql_task_tableview.getItems().clear();
                            PopupWindowUtil.sql_task_tableview.getItems().addAll(backSqlTask);
                        });

                    }
                }else{
                    throw new Exception("ERROR");
                }
                return null;
            }
        };
        //确认是否要执行操作
        if(sql.startsWith("drop database")){
            if(AlterUtil.CustomAlertConfirm("删除数据库","确定要删除数据库\""+treeItem.getValue().getName()+"\"吗？")){
                new Thread(BGTask).start();
            }
        }else if(sql.startsWith("drop table")){
            if(AlterUtil.CustomAlertConfirm("删除表","确定要删除表\""+treeItem.getValue().getName()+"\"吗？")){
                new Thread(BGTask).start();
            }
        }else if(sql.startsWith("drop view")){
            if(AlterUtil.CustomAlertConfirm("删除视图","确定要删除视图\""+treeItem.getValue().getName()+"\"吗？")){
                new Thread(BGTask).start();
            }
        }else if(sql.startsWith("drop index")){
            if(AlterUtil.CustomAlertConfirm("删除索引","确定要删除索引\""+treeItem.getValue().getName()+"\"吗？")){
                new Thread(BGTask).start();
            }
        }else if(sql.startsWith("drop sequence")){
            if(AlterUtil.CustomAlertConfirm("删除序列","确定要删除序列\""+treeItem.getValue().getName()+"\"吗？")){
                new Thread(BGTask).start();
            }
        }else if(sql.startsWith("drop synonym")){
            if(AlterUtil.CustomAlertConfirm("删除同义词","确定要删除同义词\""+treeItem.getValue().getName()+"\"吗？")){
                new Thread(BGTask).start();
            }
        }else if(sql.startsWith("drop trigger")){
            if(AlterUtil.CustomAlertConfirm("删除触发器","确定要删除触发器\""+treeItem.getValue().getName()+"\"吗？")){
                new Thread(BGTask).start();
            }
        }else if(sql.startsWith("drop function")){
            if(AlterUtil.CustomAlertConfirm("删除函数","确定要删除函数\""+treeItem.getValue().getName()+"\"吗？")){
                new Thread(BGTask).start();
            }
        }else if(sql.startsWith("drop procedure")) {
            if (AlterUtil.CustomAlertConfirm("删除存储过程", "确定要删除存储过程\"" + treeItem.getValue().getName() + "\"吗？")) {
                new Thread(BGTask).start();
            }
        }else if(sql.startsWith("drop user")) {
            if (AlterUtil.CustomAlertConfirm("删除用户", "确定要删除用户\"" + treeItem.getValue().getName() + "\"吗？")) {
                new Thread(BGTask).start();
            }
        }else if(sql.startsWith("update statistics")){
            if (AlterUtil.CustomAlertConfirm("统计更新", "确定要执行统计更新吗？")) {
                new Thread(BGTask).start();
            }
        }
        else {
            new Thread(BGTask).start();
        }


        BGTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                if (sql.startsWith("rename")) {
                    treeItem.getValue().setName(sql.split(" ")[4]);
                    if(!sql.split(" ")[1].equals("index")) {  //如果不是重命名索引，该层重新排序
                        MetadataTreeviewUtil.reorderTreeview(Main.mainController.databasemeta_treeview, treeItem);
                    }
                    String objectType=sql.split(" ")[1];
                    String msg="";
                    switch (objectType) {
                        case "database":
                            msg = "库\"" + sql.split(" ")[2] + "\"已重命名为\"" + sql.split(" ")[4] + "\"";
                            break;
                        case "table":
                            msg = "表\"" + sql.split(" ")[2] + "\"已重命名为\"" + sql.split(" ")[4] + "\"";
                            break;
                        case "view":
                            msg = "视图\"" + sql.split(" ")[2] + "\"已重命名为\"" + sql.split(" ")[4] + "\"";
                            break;
                        case "index":
                            msg = "索引\"" + sql.split(" ")[2] + "\"已重命名为\"" + sql.split(" ")[4] + "\"";
                            break;
                        case "sequence":
                            msg = "序列\"" + sql.split(" ")[2] + "\"已重命名为\"" + sql.split(" ")[4] + "\"";
                            break;
                        case "synonym":
                            msg = "同义词\"" + sql.split(" ")[2] + "\"已重命名为\"" + sql.split(" ")[4] + "\"";
                            break;
                        case "trigger":
                            msg = "触发器\"" + sql.split(" ")[2] + "\"已重命名为\"" + sql.split(" ")[4] + "\"";
                            break;
                        case "function":
                            msg = "函数\"" + sql.split(" ")[2] + "\"已重命名为\"" + sql.split(" ")[4] + "\"";
                            break;
                        case "procedure":
                            msg = "存储过程\"" + sql.split(" ")[2] + "\"已重命名为\"" + sql.split(" ")[4] + "\"";
                            break;
                        default:
                            break;
                    }
                    NotificationUtil.showNotification(Main.mainController.notice_pane, msg);

                }
                else if (sql.startsWith("drop")) {
                    treeItem.getParent().getChildren().remove(treeItem);
                    String objectType=sql.split(" ")[1];
                    String msg="";
                    switch (objectType) {
                        case "database":
                            msg = "数据库\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        case "table":
                            msg = "表\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        case "view":
                            msg = "视图\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        case "index":
                            msg = "索引\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        case "sequence":
                            msg = "序列\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        case "synonym":
                            msg = "同义词\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        case "trigger":
                            msg = "触发器\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        case "function":
                            msg = "函数\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        case "procedure":
                            msg = "存储过程\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        case "package":
                            msg = "包\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        case "user":
                            msg = "用户\"" + sql.split(" ")[2] + "\"已删除！";
                            break;
                        default:
                            break;

                    }
                    NotificationUtil.showNotification(Main.mainController.notice_pane, msg);

                }
                else if (sql.startsWith("create database")) {
                    NotificationUtil.showNotification(Main.mainController.notice_pane, "数据库["+sql.split(" ")[2]+"]创建成功");
                    treeItem.getChildren().clear();
                    treeItem.setExpanded(false);
                    treeItem.setExpanded(true);
                }else if(sql.startsWith("update")){
                    NotificationUtil.showNotification(Main.mainController.notice_pane, "统计更新执行完成！");
                }else if(sql.startsWith("truncate")){
                    MetadataTreeviewUtil.refreshItem.fire();
                    NotificationUtil.showNotification(Main.mainController.notice_pane, "表\""+sql.split(" ")[2]+"\"已清空！");
                }

                else if(sql.startsWith("set")){
                    if(sql.split(" ")[1].equals("indexes")){
                        if(sql.endsWith("disabled")){
                            ((Index)treeItem.getValue()).setIsdisabled(true);
                            NotificationUtil.showNotification(Main.mainController.notice_pane, "索引\""+sql.split(" ")[2]+"\"已禁用！");
                        }else{
                            ((Index)treeItem.getValue()).setIsdisabled(false);
                            NotificationUtil.showNotification(Main.mainController.notice_pane, "索引\""+sql.split(" ")[2]+"\"已启用！");
                        }
                    }else if(sql.split(" ")[1].equals("triggers")){
                        if(sql.endsWith("disabled")){
                            ((Trigger)treeItem.getValue()).setIsdisabled(true);
                            NotificationUtil.showNotification(Main.mainController.notice_pane, "触发器\""+sql.split(" ")[2]+"\"已禁用！");
                        }else{
                            ((Trigger)treeItem.getValue()).setIsdisabled(false);
                            NotificationUtil.showNotification(Main.mainController.notice_pane, "触发器\""+sql.split(" ")[2]+"\"已启用！");
                        }
                    }
                }else if(sql.startsWith("alter table")){
                    if(sql.contains("type(raw)")){
                        ((Table)treeItem.getValue()).setTableType("raw");
                        NotificationUtil.showNotification(Main.mainController.notice_pane, "表\""+sql.split(" ")[2]+"\"已更改为裸表！");
                    }else{
                        ((Table)treeItem.getValue()).setTableType("standard");
                        NotificationUtil.showNotification(Main.mainController.notice_pane, "表\""+sql.split(" ")[2]+"\"已更改为标注表！");
                    }
                    //Main.mainController.databaseobjects_treeview.refresh();

                }else if(sql.startsWith("create user")){
                    NotificationUtil.showNotification(Main.mainController.notice_pane, "用户\""+sql.split(" ")[2]+"\"已创建！");
                }
                else if(sql.startsWith("alter user")){
                    NotificationUtil.showNotification(Main.mainController.notice_pane, "用户\""+sql.split(" ")[2]+"\"密码已重置！");

                }


            });
        });


    }

    public Connection newConnection(Connect connect, String DatabaseName, String DBLocale) {
        Connection conn=null;
        //创建新的连接用来执行后台任务
        Connect connect1 = new Connect(connect);
        connect1.setDatabase(DatabaseName);
        connect1.setProps(modifyProps(connect, DBLocale));
        try {
            conn=getConnection(connect1);
            sessionChangeToGbaseMode(conn);
        }
        catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
            });
        }catch (Exception e){

        }
        return conn;
    }

    //获取连接，如是可以正常切库，不处理，如果不能，创建新的连接
    public Connection getMetaSession(TreeItem treeItem) {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        String dbLocale=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getDbLocale();
        Connect connect=MetadataTreeviewUtil.getMetaConnect(treeItem);
        Connection conn=connect.getConn();
        try {
            //多个statment同时执行executeUpdate会受阻
            conn.createStatement().executeUpdate("database " + databaseName);
            sessionChangeToGbaseMode(conn); //防止sysdbopen中设置了oracle模式，在执行database后变为oracle模式
        }catch (SQLException e) {
            e.printStackTrace();
            //如果是ansi库切其他字符集库报错为-349
            if(e.getErrorCode() == -23197||e.getErrorCode()==-349) {
                //如果无法切换库，切回原库
                try {
                    conn.createStatement().executeUpdate("database " + connect.getDatabase());
                } catch (SQLException ex) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误" +"", e.toString());
                    });
                }

                //创建新的连接用来查询数据
                Connect connect1 = new Connect(connect);
                connect1.setProps(modifyProps(connect, dbLocale));
                connect1.setDatabase(databaseName);
                try {
                    conn=getConnection(connect1);
                    sessionChangeToGbaseMode(conn);
                }catch (Exception e1) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
                //如果是没有权限，连接切回原库，避免后续刷新列表报没有选中数据库错误
            }

        }
        return conn;
    }
    public ObjectList getDatabaseObjects(TreeItem treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        Connect connect=MetadataTreeviewUtil.getMetaConnect(treeItem);
        ObjectList objectList=new ObjectList();
        List<String> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);
        try{
            //先切库，避免fc中权限问题切库失败导致执行sql报未选择库
            PreparedStatement pstmt = conn.prepareStatement("database ?");
            pstmt.setString(1,databaseName);
            pstmt.executeUpdate();
            //更改为gbase模式，避免如果是新连接，且有sysdbopen中设置了oracle模式，在此次执行切换为了oracle模式
            sessionChangeToGbaseMode(conn);

            pstmt = conn.prepareStatement("""
                    select t1.*,t2.allocedsize from(
                    SELECT trim(name) dbname,
                    trim(owner) owner,
                    to_char(created,'YYYY-MM-DD')  created_time,
                    TRIM(DBINFO('dbspace',partnum)) AS dbspace,
                    CASE WHEN is_logging+is_buff_log=1 THEN 'unbuffered'
                         WHEN is_logging+is_buff_log=2 THEN 'buffered'
                         WHEN is_logging+is_buff_log=0 THEN 'nolog'
                    ELSE '' END Logging_mode,
                    is_nls,
                    trim(replace(replace(dbs_collate,'57372','UTF8'),'5488','GB18030-2000'))
                    FROM sysmaster:sysdatabases d,sysmaster:sysdbslocale s where d.name=s.dbs_dbsname
                    and trim(name)  =?
                    ) t1
                    left join
                    (
                    SELECT
                    trim(st.dbsname) dbname,
                    replace(format_units(sum(sin.ti_nptotal*sin.ti_pagesize),'b'),' ','') allocedsize
                    from
                    sysmaster:systabnames st JOIN sysmaster:systabinfo sin ON  st.partnum=sin.ti_partnum
                    where st.dbsname=?
                    GROUP BY 1
                    )t2
                    on t1.dbname=t2.dbname
    
    """);
            pstmt.setString(1,databaseName);
            pstmt.setString(2,databaseName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Database database=new Database(rs.getString(1));
                database.setDbOwner(rs.getString(2));
                database.setDbCreated(rs.getString(3));
                database.setDbSpace(rs.getString(4));
                database.setDbLog(rs.getString(5));
                database.setDbUseGLU(rs.getString(6));
                database.setDbLocale(rs.getString(7));
                database.setDbSize(rs.getString(8));
                objectList.setInfo(database);
            }
            rs.close();

            String andtype="";
            pstmt = conn.prepareStatement("select count(*) from systables t,syscolumns c where t.tabid=c.tabid and t.tabname='sysprocedures' and c.colname='type'");
            rs=pstmt.executeQuery();
            if(rs.next()){
                if(rs.getInt(1)==1){
                    andtype=" and type==0";
                };
            }
            rs.close();

            String num=null;
            String size=null;
            //系统表/视图
            pstmt = conn.prepareStatement("select count(*) from systables where tabid<=(SELECT tabid FROM systables WHERE tabname = ' VERSION')");
            rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            rs.close();

            pstmt = conn.prepareStatement("select replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')\n" +
                    "from systables s left join sysmaster:systabnames n on s.tabname=trim(n.tabname)\n" +
                    "left join sysmaster:systabinfo i on i.ti_partnum=n.partnum \n" +
                    "where tabid<=(SELECT tabid FROM systables WHERE tabname = ' VERSION') and n.dbsname=?");
            pstmt.setString(1,databaseName);
            rs=pstmt.executeQuery();
            rs.next();
            size=rs.getString(1);
            if(size==null)
                result.add(num);
            else
                result.add(num+"/"+size);
            rs.close();

            //表
            pstmt = conn.prepareStatement("select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype='T'");
            rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            rs.close();

            pstmt = conn.prepareStatement("select replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')\n" +
                    "from systables s left join sysmaster:systabnames n on s.tabname=trim(n.tabname)\n" +
                    "left join sysmaster:systabinfo i on i.ti_partnum=n.partnum \n" +
                    "where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and n.dbsname=?");
            pstmt.setString(1,databaseName);
            rs=pstmt.executeQuery();
            rs.next();
            size=rs.getString(1);
            if(size==null)
                result.add(num);
            else
                result.add(num+"/"+size);
            rs.close();
            //视图
            pstmt = conn.prepareStatement("select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype='V'");
            rs=pstmt.executeQuery();
            rs.next();
            result.add(rs.getString(1)+"个");
            rs.close();
            //索引
            pstmt = conn.prepareStatement("select count(*) from sysindexes i,systables t where i.tabid=t.tabid and t.tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')");
            rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            rs.close();
            pstmt = conn.prepareStatement("select replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')\n" +
                    "from sysindexes s left join sysmaster:systabnames n on trim(s.idxname)=trim(n.tabname)\n" +
                    "left join sysmaster:systabinfo i on i.ti_partnum=n.partnum \n" +
                    "where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')");
            rs=pstmt.executeQuery();
            rs.next();
            size=rs.getString(1);
            if(size==null)
                result.add(num);
            else
                result.add(num+"/"+size);
            rs.close();
            //序列
            pstmt = conn.prepareStatement("select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype in('Q')");
            rs=pstmt.executeQuery();
            rs.next();
            result.add(rs.getString(1)+"个");
            rs.close();
            //同义词
            pstmt = conn.prepareStatement("select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype in('P','S')");
            rs=pstmt.executeQuery();
            rs.next();
            result.add(rs.getString(1)+"个");
            rs.close();
            //触发器
            pstmt = conn.prepareStatement("select count(*) from systriggers");
            rs=pstmt.executeQuery();
            rs.next();
            result.add(rs.getString(1)+"个");
            rs.close();
            //函数,低版本没有type字段
            //pstmt = conn.prepareStatement("SELECT COUNT(distinct procname) FROM sysprocedures WHERE isproc = 'f' and mode='O' and  type==0");
            pstmt = conn.prepareStatement("SELECT COUNT(distinct procname) FROM sysprocedures WHERE isproc = 'f' and mode='O'"+andtype);
            rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1);
            rs.close();
            Integer nrows=0;
            //rs=statement.executeQuery("select count(*) from sysprocbody b, sysprocedures p where b.procid=p.procid and p.isproc = 'f' and p.procid>665 and b.datakey='T'");
            //while(rs.next()){
            //    nrows+=rs.getString(1).split("\n").length;
            //};

            result.add(num+"个");

            //存储过程
            //pstmt = conn.prepareStatement("SELECT COUNT(distinct procname ) FROM sysprocedures WHERE isproc = 't' and mode='O' and  type==0");

            pstmt = conn.prepareStatement("SELECT COUNT(distinct procname ) FROM sysprocedures WHERE isproc = 't' and mode='O'"+andtype);
            rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1);
            rs.close();
            result.add(num+"个");
            pstmt.close();

            //包
            //存储过程
            pstmt = conn.prepareStatement("SELECT COUNT(distinct procname) FROM sysprocedures WHERE mode='O' and retsize=0");
            rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1);
            rs.close();
            result.add(num+"个");
            pstmt.close();
            objectList.setSuccess(true);
        }catch (SQLException e) {
            e.printStackTrace();

            if(e.getErrorCode()==-387){//如果是权限问题，切回原库，避免其他操作报未选中数据库错误
                try {
                    conn.createStatement().executeUpdate("database " + connect.getDatabase());
                    sessionChangeToGbaseMode(conn);
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                    });
                } catch (SQLException ex) {
                }
            }else if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                //if(conn!=TreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }

    //获取库大小，用于刷新库时更新库大小
    public ObjectList getSystemtables(TreeItem treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        ObjectList objectList=new ObjectList();
        List<SysTable> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);
        try{
            String num=null;
            String size=null;
            //系统表/视图
            //ResultSet rs=statement.executeQuery("select count(*) from systables where tabid<100");
            PreparedStatement pstmt= conn.prepareStatement("select count(*) from systables where tabid<=(SELECT tabid FROM systables WHERE tabname = ' VERSION') ");
            ResultSet rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            rs.close();
            pstmt= conn.prepareStatement("select replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')\n" +
                    "from systables s left join sysmaster:systabnames n on s.tabname=trim(n.tabname)\n" +
                    "left join sysmaster:systabinfo i on i.ti_partnum=n.partnum \n" +
                    "where tabid<=(SELECT tabid FROM systables WHERE tabname = ' VERSION')  and n.dbsname=?");
            pstmt.setString(1,databaseName);
            rs=pstmt.executeQuery();
            rs.next();
            size=rs.getString(1);
            objectList.setInfo(num+"/"+size);
            rs.close();
            pstmt= conn.prepareStatement("select '"+databaseName+"' databasename,dt.tabname,max(dt.owner),max(to_char(dt.created,'YYYY-MM-DD')),max(case when dt.tabtype=='V' then 'view' else 'table' end ),max(dt.locklevel) lock_level,\n" +
                    "max(case when dt.partnum==0 then 1 else 0 end) isfragment,sum(ti_nextns) extents,  \n" +
                    "sum(sin.ti_nrows) nrows,max(sin.ti_pagesize) pagesize, sum(sin.ti_nptotal) nptotal, nvl(replace(format_units(sum(sin.ti_nptotal*sin.ti_pagesize),'b'),' ','') ,'0.000B')  total_size, \n" +
                    "sum(sin.ti_npdata) npused,nvl(replace(format_units(sum(sin.ti_npdata*sin.ti_pagesize),'b'),' ',''),'0.000B') used_size \n" +
                    "from systables dt left join sysmaster:systabnames st \n" +
                    "on trim(dt.tabname)=trim(st.tabname) and st.dbsname='"+databaseName+"'" +
                    "left join sysmaster:systabinfo sin on st.partnum=sin.ti_partnum \n" +
                    "where  dt.tabid<=(SELECT tabid FROM systables WHERE tabname = ' VERSION')  \n" +
                    "group by 1,2\n" +
                    "order by  2; ");
            rs=pstmt.executeQuery();
            while(rs.next()) {
                SysTable table=new SysTable(rs.getString(2));
                table.setDbname(rs.getString(1));
                table.setOwner(rs.getString(3));
                table.setCreateTime(rs.getString(4));
                table.setTableType(rs.getString(5));
                table.setLockMode(rs.getString(6));
                table.setIsfragment(rs.getInt(7));
                table.setExtents(rs.getInt(8));
                table.setNrows(rs.getInt(9));
                table.setPagesize(rs.getInt(10));
                table.setNptotal(rs.getInt( 11));
                table.setTotalsize(rs.getString(12));
                table.setNpdata(rs.getInt(13));
                table.setUsedsize(rs.getString(14));
                result.add(table);
            }
            rs.close();
            pstmt.close();
        }catch (SQLException e) {
            e.printStackTrace();
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }

    //获取表
    public ObjectList getTables(TreeItem treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        ObjectList objectList=new ObjectList();
        List<Table> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);
        try{
            String num=null;
            String size=null;
            PreparedStatement pstmt=conn.prepareStatement("select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')  and tabtype='T'");
            ResultSet rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            rs.close();

            pstmt=conn.prepareStatement("select replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')\n" +
                    "from systables s left join sysmaster:systabnames n on s.tabname=trim(n.tabname)\n" +
                    "left join sysmaster:systabinfo i on i.ti_partnum=n.partnum \n" +
                    "where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')  and n.dbsname=?");
            pstmt.setString(1,databaseName);
            rs=pstmt.executeQuery();
            rs.next();
            size=rs.getString(1);
            if(size==null)
                objectList.setInfo(num);
            else
                objectList.setInfo(num+"/"+size);
            rs.close();

            /*
            pstmt=conn.prepareStatement("select '"+databaseName+"' databasename,dt.tabname,max(dt.owner),max(to_char(dt.created,'YYYY-MM-DD')),max(case when dt.flags==16 then 'raw' when dt.flags==32 then 'external' else 'standard' end ),max(dt.locklevel) lock_level,\n" +
                    "max(case when dt.partnum==0 then 1 else 0 end) isfragment,sum(ti_nextns) extents,  \n" +
                    "sum(sin.ti_nrows) nrows,max(sin.ti_pagesize) pagesize, sum(sin.ti_nptotal) nptotal,  replace(format_units(sum(sin.ti_nptotal*sin.ti_pagesize),'b'),' ','')  total_size, \n" +
                    "sum(sin.ti_npdata) npused,replace(format_units(sum(sin.ti_npdata*sin.ti_pagesize),'b'),' ','') used_size \n" +
                    "from systables dt \n" +
                    "left join sysmaster:systabnames st on dt.tabname=st.tabname and st.dbsname='"+databaseName+"'\n" +
                    "left join sysmaster:systabinfo sin on st.partnum=sin.ti_partnum \n" +
                    "where  dt.tabid>=? and dt.tabtype in ('T','E')\n" +
                    "group by 1,2 \n" +
                    "order by  2;");

             */
            pstmt=conn.prepareStatement("""
                    select '"""+databaseName+"""
                    ',tabname,max(owner),max(createtime),max(tabtype),max(locklevel),max(isfragment),
                    sum(ti_nextns) extents,
                    sum(ti_nrows) nrows,max(ti_pagesize) pagesize, sum(ti_nptotal) nptotal,  replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')  total_size,
                    sum(ti_npdata) npused,replace(format_units(sum(ti_npdata*ti_pagesize),'b'),' ','')
                     from
                    (select tabname,owner,to_char(created,'YYYY-MM-DD') createtime,
                    case when t.flags==16 then 'raw' when t.flags==32 then 'external' else 'standard' end tabtype,
                    locklevel,case when t.partnum==0 then 1 else 0 end isfragment,
                    case when t.partnum=0 then f.partn else t.partnum end  as partnum
                    from
                    systables t left join sysfragments f on t.tabid=f.tabid
                    where t.tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype in ('T','E')
                    ) t
                    left join sysmaster:systabinfo i on i.ti_partnum=partnum
                    group by 1,2
                    order by  2
                    """);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                Table table = new Table(rs.getString(2));
                table.setDbname(rs.getString(1));
                table.setOwner(rs.getString(3));
                table.setCreateTime(rs.getString(4));
                table.setTableType(rs.getString(5));
                table.setLockMode(rs.getString(6));
                table.setIsfragment(rs.getInt(7));
                table.setExtents(rs.getInt(8));
                table.setNrows(rs.getInt(9));
                table.setPagesize(rs.getInt(10));
                table.setNptotal(rs.getInt(11));
                table.setTotalsize(rs.getString(12));
                table.setNpdata(rs.getInt(13));
                table.setUsedsize(rs.getString(14));
                result.add(table);
            }
            rs.close();
            pstmt.close();
        }catch (SQLException e) {
            e.printStackTrace();
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }


    //获取表
    public Table getTable(TreeItem<TreeData> treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        Table table=new Table();
        Connection conn=getMetaSession(treeItem);
        try{
            /* systabnames查询太慢，需要400+ms
            PreparedStatement pstmt=conn.prepareStatement("select '"+databaseName+"' databasename,dt.tabname,max(dt.owner),max(to_char(dt.created,'YYYY-MM-DD')),max(case when dt.flags==16 then 'raw' when dt.flags==32 then 'external' else 'standard' end ),max(dt.locklevel) lock_level,\n" +
                    "max(case when dt.partnum==0 then 1 else 0 end) isfragment,sum(ti_nextns) extents,  \n" +
                    "sum(sin.ti_nrows) nrows,max(sin.ti_pagesize) pagesize, sum(sin.ti_nptotal) nptotal,  replace(format_units(sum(sin.ti_nptotal*sin.ti_pagesize),'b'),' ','')  total_size, \n" +
                    "sum(sin.ti_npdata) npused,replace(format_units(sum(sin.ti_npdata*sin.ti_pagesize),'b'),' ','') used_size \n" +
                    "from systables dt \n" +
                    "left join sysmaster:systabnames st on dt.tabname=st.tabname and st.dbsname='"+databaseName+"'\n" +
                    "left join sysmaster:systabinfo sin on st.partnum=sin.ti_partnum \n" +
                    "where  dt.tabname=?"+"\n" +
                    "group by 1,2 \n");

             */
            PreparedStatement pstmt=conn.prepareStatement("""
                    select '"""+databaseName+ """
                    ',max(tabname),max(owner),max(createtime),max(tabtype),max(locklevel),max(isfragment),
                    sum(ti_nextns) extents,
                    sum(ti_nrows) nrows,max(ti_pagesize) pagesize, sum(ti_nptotal) nptotal,  replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')  total_size,
                    sum(ti_npdata) npused,replace(format_units(sum(ti_npdata*ti_pagesize),'b'),' ','')
                     from
                    (select tabname,owner,to_char(created,'YYYY-MM-DD') createtime,
                    case when t.flags==16 then 'raw' when t.flags==32 then 'external' else 'standard' end tabtype,
                    locklevel,case when t.partnum==0 then 1 else 0 end isfragment,
                    case when t.partnum=0 then f.partn else t.partnum end  as partnum
                    from
                    systables t left join sysfragments f on t.tabid=f.tabid
                    where tabname=?) t
                    join sysmaster:systabinfo i on i.ti_partnum=partnum
                    """);
            pstmt.setString(1,treeItem.getValue().getName());
            ResultSet rs=pstmt.executeQuery();
            while (rs.next()) {
                table = new Table(rs.getString(2));
                table.setDbname(rs.getString(1));
                table.setOwner(rs.getString(3));
                table.setCreateTime(rs.getString(4));
                table.setTableType(rs.getString(5));
                table.setLockMode(rs.getString(6));
                table.setIsfragment(rs.getInt(7));
                table.setExtents(rs.getInt(8));
                table.setNrows(rs.getInt(9));
                table.setPagesize(rs.getInt(10));
                table.setNptotal(rs.getInt(11));
                table.setTotalsize(rs.getString(12));
                table.setNpdata(rs.getInt(13));
                table.setUsedsize(rs.getString(14));
            }
            rs.close();
            pstmt.close();
        }catch (SQLException e) {
            e.printStackTrace();
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return table;
    }

    //获取视图
    public ObjectList getViews(TreeItem treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        ObjectList objectList=new ObjectList();
        List<View> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);;
        try{
            PreparedStatement pstmt = conn.prepareStatement("select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')  and tabtype='V'");
            ResultSet rs=pstmt.executeQuery();
            rs.next();
            objectList.setInfo(rs.getString(1)+"个");
            rs.close();

            pstmt = conn.prepareStatement("select '"+databaseName+"',tabname,owner,to_char(created,'YYYY-MM-DD') from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')  and tabtype='V'");
            rs=pstmt.executeQuery();
            while (rs.next()) {
                View view = new View(rs.getString(2));
                view.setDbname(rs.getString(1));
                view.setOwner(rs.getString(3));
                view.setCreateTime(rs.getString(4));
                result.add(view);
            }
            rs.close();
            pstmt.close();
        }catch (SQLException e) {
            e.printStackTrace();
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }


    public ObjectList getIndexes(TreeItem<TreeData> treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        ObjectList objectList=new ObjectList();
        List<Index> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);;
        try{
            String num=null;
            String size=null;
            //ResultSet rs=statement.executeQuery("select count(*) from systables where tabid<100");
            PreparedStatement pstmt = conn.prepareStatement("select count(*) from sysindexes i,systables t where i.tabid=t.tabid and t.tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') ");
            ResultSet rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            rs.close();

            pstmt = conn.prepareStatement("""
                    select replace(format_units(sum(ti_nptotal*ti_pagesize),'b'),' ','')
                    from sysindexes s left join sysmaster:systabnames n on trim(s.idxname)=trim(n.tabname)
                    left join sysmaster:systabinfo i on i.ti_partnum=n.partnum
                    where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')"""
            );
            rs=pstmt.executeQuery();
            rs.next();
            size=rs.getString(1);
            rs.close();
            if(size==null)
                objectList.setInfo(num);
            else
                objectList.setInfo(num+"/"+size);

            pstmt = conn.prepareStatement(
                    """
                    select '"""
                            +databaseName+
                            """
                            ',
                            i.idxname,
                            t.tabname,
                            trim( case when i.part1 > 0 then( select colname from syscolumns where colno = i.part1 and tabid = i.tabid ) else '' end )|| trim( case when i.part2 > 0 then( select ',' || colname from syscolumns where colno = i.part2 and tabid = i.tabid ) else '' end )|| trim( case when i.part3 > 0 then( select ',' || colname from syscolumns where colno = i.part3 and tabid = i.tabid ) else '' end )|| trim( case when i.part4 > 0 then( select ',' || colname from syscolumns where colno = i.part4 and tabid = i.tabid ) else '' end )|| trim( case when i.part5 > 0 then( select ',' || colname from syscolumns where colno = i.part5 and tabid = i.tabid ) else '' end )|| trim( case when i.part6 > 0 then( select ',' || colname from syscolumns where colno = i.part6 and tabid = i.tabid ) else '' end )|| trim( case when i.part7 > 0 then( select ',' || colname from syscolumns where colno = i.part7 and tabid = i.tabid ) else '' end )|| trim( case when i.part8 > 0 then( select ',' || colname from syscolumns where colno = i.part8 and tabid = i.tabid ) else '' end )|| trim( case when i.part9 > 0 then( select ',' || colname from syscolumns where colno = i.part9 and tabid = i.tabid ) else '' end )|| trim( case when i.part10 > 0 then( select ',' || colname from syscolumns where colno = i.part10 and tabid = i.tabid ) else '' end )|| trim( case when i.part11 > 0 then( select ',' || colname from syscolumns where colno = i.part11 and tabid = i.tabid ) else '' end )|| trim( case when i.part12 > 0 then( select ',' || colname from syscolumns where colno = i.part12 and tabid = i.tabid ) else '' end )|| trim( case when i.part13 > 0 then( select ',' || colname from syscolumns where colno = i.part13 and tabid = i.tabid ) else '' end )|| trim( case when i.part14 > 0 then( select ',' || colname from syscolumns where colno = i.part14 and tabid = i.tabid ) else '' end )|| trim( case when i.part15 > 0 then( select ',' || colname from syscolumns where colno = i.part15 and tabid = i.tabid ) else '' end )|| trim( case when i.part16 > 0 then( select ',' || colname from syscolumns where colno = i.part16 and tabid = i.tabid ) else '' end ) as cols,
                            i.idxtype,
                            i.levels,
                            i.nunique,
                            sin.ti_pagesize pagesize,
                            sum(sin.ti_nptotal) nptotal,
                            replace(format_units(sum(sin.ti_nptotal*sin.ti_pagesize),'b'),' ','')  total_size,
                            max(o.state)
                            from
                            systables t join sysindexes i 
                            on t.tabid = i.tabid and t.tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION')
                            join sysobjstate o on  o.tabid=t.tabid and o.name=i.idxname
                            left join sysmaster:systabnames st
                            on trim(i.idxname)=trim(st.tabname) and st.dbsname=?
                            left join sysmaster:systabinfo sin on st.partnum=sin.ti_partnum
                            group by 1,2,3,4,5,6,7,8
                            order by 3,4
                            """);
            pstmt.setString(1,databaseName);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                Index index = new Index(rs.getString(2));
                index.setDatabase(rs.getString(1));
                index.setTabname(rs.getString(3));
                index.setCols(rs.getString(4));
                index.setIdxtype(rs.getString(5));
                index.setLevels(rs.getString(6));
                index.setUniqvalues(rs.getString(7));
                index.setPagesize(rs.getString(8));
                index.setTotalpages(rs.getString(9));
                index.setTotalsize(rs.getString(10));
                index.setIsdisabled(rs.getString(11).equals("E")?false:true);
                result.add(index);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            e.printStackTrace();
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }


    public Index getIndex(TreeItem<TreeData> treeItem)  {
        Index index=new Index();
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        Connection conn=getMetaSession(treeItem);;
        try{
            PreparedStatement pstmt = conn.prepareStatement(
                    """
                    select '"""
                            +databaseName+
                            """
                            ',
                            i.idxname,
                            t.tabname,
                            trim( case when i.part1 > 0 then( select colname from syscolumns where colno = i.part1 and tabid = i.tabid ) else '' end )|| trim( case when i.part2 > 0 then( select ',' || colname from syscolumns where colno = i.part2 and tabid = i.tabid ) else '' end )|| trim( case when i.part3 > 0 then( select ',' || colname from syscolumns where colno = i.part3 and tabid = i.tabid ) else '' end )|| trim( case when i.part4 > 0 then( select ',' || colname from syscolumns where colno = i.part4 and tabid = i.tabid ) else '' end )|| trim( case when i.part5 > 0 then( select ',' || colname from syscolumns where colno = i.part5 and tabid = i.tabid ) else '' end )|| trim( case when i.part6 > 0 then( select ',' || colname from syscolumns where colno = i.part6 and tabid = i.tabid ) else '' end )|| trim( case when i.part7 > 0 then( select ',' || colname from syscolumns where colno = i.part7 and tabid = i.tabid ) else '' end )|| trim( case when i.part8 > 0 then( select ',' || colname from syscolumns where colno = i.part8 and tabid = i.tabid ) else '' end )|| trim( case when i.part9 > 0 then( select ',' || colname from syscolumns where colno = i.part9 and tabid = i.tabid ) else '' end )|| trim( case when i.part10 > 0 then( select ',' || colname from syscolumns where colno = i.part10 and tabid = i.tabid ) else '' end )|| trim( case when i.part11 > 0 then( select ',' || colname from syscolumns where colno = i.part11 and tabid = i.tabid ) else '' end )|| trim( case when i.part12 > 0 then( select ',' || colname from syscolumns where colno = i.part12 and tabid = i.tabid ) else '' end )|| trim( case when i.part13 > 0 then( select ',' || colname from syscolumns where colno = i.part13 and tabid = i.tabid ) else '' end )|| trim( case when i.part14 > 0 then( select ',' || colname from syscolumns where colno = i.part14 and tabid = i.tabid ) else '' end )|| trim( case when i.part15 > 0 then( select ',' || colname from syscolumns where colno = i.part15 and tabid = i.tabid ) else '' end )|| trim( case when i.part16 > 0 then( select ',' || colname from syscolumns where colno = i.part16 and tabid = i.tabid ) else '' end ) as cols,
                            i.idxtype,
                            i.levels,
                            i.nunique,
                            sin.ti_pagesize pagesize,
                            sum(sin.ti_nptotal) nptotal,
                            replace(format_units(sum(sin.ti_nptotal*sin.ti_pagesize),'b'),' ','')  total_size,
                            max(o.state)
                            from
                            systables t join sysindexes i 
                            on t.tabid = i.tabid and i.idxname==?
                            join sysobjstate o on  o.tabid=t.tabid and o.name=i.idxname
                            left join sysmaster:systabnames st
                            on trim(i.idxname)=trim(st.tabname)
                            left join sysmaster:systabinfo sin on st.partnum=sin.ti_partnum
                            group by 1,2,3,4,5,6,7,8
                            order by 3,4
                            """);
            pstmt.setString(1,treeItem.getValue().getName());
            ResultSet rs=pstmt.executeQuery();
            while (rs.next()) {
                index = new Index(rs.getString(2));
                index.setDatabase(rs.getString(1));
                index.setTabname(rs.getString(3));
                index.setCols(rs.getString(4));
                index.setIdxtype(rs.getString(5));
                index.setLevels(rs.getString(6));
                index.setUniqvalues(rs.getString(7));
                index.setPagesize(rs.getString(8));
                index.setTotalpages(rs.getString(9));
                index.setTotalsize(rs.getString(10));
                index.setIsdisabled(rs.getString(11).equals("E")?false:true);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            e.printStackTrace();
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return index;
    }

    public ObjectList getSequences(TreeItem<TreeData> treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        ObjectList objectList=new ObjectList();
        List<Sequence> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);;
        try{
            String num=null;
            //ResultSet rs=statement.executeQuery("select count(*) from systables where tabid<100");
            PreparedStatement pstmt = conn.prepareStatement("select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype in('Q')");
            ResultSet rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            objectList.setInfo(num);
            rs.close();

            pstmt=conn.prepareStatement(
                    "select '"+databaseName+"',tabname as seqname,min_val,max_val,inc_val,cache,cur_serial8,t.created from systables t,syssequences q,sysmaster:sysptnhdr p where t.tabtype='Q' and t.tabid=q.tabid and t.partnum=p.partnum ");
            rs=pstmt.executeQuery();
            while (rs.next()) {
                Sequence sequence = new Sequence(rs.getString(2));
                sequence.setDatabase(rs.getString(1));
                sequence.setMinValue(BigInteger.valueOf(rs.getLong(3)));
                sequence.setMaxValue(BigInteger.valueOf(rs.getLong(4)));
                sequence.setIncValue(BigInteger.valueOf(rs.getLong(5)));
                sequence.setCache(rs.getInt(6));
                sequence.setNextval(BigInteger.valueOf(rs.getLong(7)));
                sequence.setCreated(rs.getString(8));
                result.add(sequence);

            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }


    public ObjectList getSynonyms(TreeItem<TreeData> treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        ObjectList objectList=new ObjectList();
        List<Synonym> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);;
        try{
            String num=null;
            //ResultSet rs=statement.executeQuery("select count(*) from systables where tabid<100");
            PreparedStatement pstmt = conn.prepareStatement("select count(*) from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype in('P','S')");
            ResultSet rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            objectList.setInfo(num);
            rs.close();

            pstmt=conn.prepareStatement(
                    "select '"+databaseName+"',tabname,case tabtype when 'S' then 'PUBLIC' else 'PRIVATE' end,created from systables where tabid>(SELECT tabid FROM systables WHERE tabname = ' VERSION') and tabtype in ('P','S');");
            rs=pstmt.executeQuery();
            while (rs.next()) {
                Synonym synonym = new Synonym(rs.getString(2));
                synonym.setDatabase(rs.getString(1));
                synonym.setSynonymType(rs.getString(3));
                synonym.setCreated(rs.getString(4));
                result.add(synonym);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }

    public ObjectList getTriggers(TreeItem<TreeData> treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        ObjectList objectList=new ObjectList();
        List<Trigger> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);;
        try{
            int systemTabid=100;
            PreparedStatement pstmt = conn.prepareStatement("select tabid  from systables where tabname='dual'");
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()) {
                if(rs.getInt(1)>100){
                    systemTabid=1000;
                }
            }else{
                systemTabid=1000;
            }
            rs.close();

            String num=null;
            //ResultSet rs=statement.executeQuery("select count(*) from systables where tabid<100");
            pstmt = conn.prepareStatement("select count(*) from systriggers");
            rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            objectList.setInfo(num);
            rs.close();

            pstmt=conn.prepareStatement(
                    "select '"+databaseName+"',tabname,trigname,case event when 'S' then 'select' when 'D' then 'delete' when 'U' then 'update' when 'I' then 'insert' end, s.state from systriggers t,sysobjstate s,systables st where t.tabid=st.tabid and s.objtype='T' and s.name=t.trigname");
            rs=pstmt.executeQuery();
            while (rs.next()) {
                Trigger trigger = new Trigger(rs.getString(3));
                trigger.setDatabase(rs.getString(1));
                trigger.setTableName(rs.getString(2));
                trigger.setTriggerType(rs.getString(4));
                trigger.setIsdisabled(rs.getString(5).equals("E")?false:true);
                result.add(trigger);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }


    public Trigger getTrigger(TreeItem<TreeData> treeItem)  {
        Trigger trigger=new Trigger();
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        Connection conn=getMetaSession(treeItem);;
        try{


            PreparedStatement pstmt=conn.prepareStatement(
                    "select '"+databaseName+"',tabname,trigname,case event when 'S' then 'select' when 'D' then 'delete' when 'U' then 'update' when 'I' then 'insert' end, s.state from systriggers t,sysobjstate s,systables st where t.tabid=st.tabid and s.objtype='T' and s.name=t.trigname and t.trigname=?");
            pstmt.setString(1,treeItem.getValue().getName());
            ResultSet rs=pstmt.executeQuery();
            while (rs.next()) {
                trigger = new Trigger(rs.getString(3));
                trigger.setDatabase(rs.getString(1));
                trigger.setTableName(rs.getString(2));
                trigger.setTriggerType(rs.getString(4));
                trigger.setIsdisabled(rs.getString(5).equals("E")?false:true);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return trigger;
    }

    public ObjectList getFunctions(TreeItem<TreeData> treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        ObjectList objectList=new ObjectList();
        List<Function> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);;
        try{
            int systemTabid=100;
            PreparedStatement pstmt = conn.prepareStatement("select tabid  from systables where tabname='dual'");
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()) {
                if(rs.getInt(1)>100){
                    systemTabid=1000;
                }
            }else{
                systemTabid=1000;
            }
            rs.close();

            String andtype="";
            pstmt = conn.prepareStatement("select count(*) from systables t,syscolumns c where t.tabid=c.tabid and t.tabname='sysprocedures' and c.colname='type'");
            rs=pstmt.executeQuery();
            if(rs.next()){
                if(rs.getInt(1)==1){
                    andtype=" and type==0";
                };
            }

            rs.close();

            String num=null;
            //ResultSet rs=statement.executeQuery("select count(*) from systables where tabid<100");
            //pstmt = conn.prepareStatement("SELECT COUNT(distinct procname) FROM sysprocedures WHERE isproc = 'f' and mode='O' and  type==0");
            pstmt = conn.prepareStatement("SELECT COUNT(distinct procname) FROM sysprocedures WHERE isproc = 'f' and mode='O'"+andtype);

            rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            objectList.setInfo(num);
            rs.close();

            pstmt=conn.prepareStatement(
                    //"select distinct '"+databaseName+"',procname,owner FROM sysprocedures WHERE isproc = 'f' and mode='O' and  type==0");

                    "select distinct '"+databaseName+"',procname,owner FROM sysprocedures WHERE isproc = 'f' and mode='O'"+andtype);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                Function function = new Function(rs.getString(2));
                function.setDatabase(rs.getString(1));
                function.setOwner(rs.getString(3));
                result.add(function);
            }
            rs.close();

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }

    public ObjectList getProcedures(TreeItem<TreeData> treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        ObjectList objectList=new ObjectList();
        List<Procedure> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);;
        try{
            int systemTabid=100;
            PreparedStatement pstmt = conn.prepareStatement("select tabid  from systables where tabname='dual'");
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()) {
                if(rs.getInt(1)>100){
                    systemTabid=1000;
                }
            }else{
                systemTabid=1000;
            }
            rs.close();

            String andtype="";
            pstmt = conn.prepareStatement("select count(*) from systables t,syscolumns c where t.tabid=c.tabid and t.tabname='sysprocedures' and c.colname='type'");
            rs=pstmt.executeQuery();
            if(rs.next()){
                if(rs.getInt(1)==1){
                    andtype=" and type==0";
                };
            }
            rs.close();

            String num=null;
            //ResultSet rs=statement.executeQuery("select count(*) from systables where tabid<100");
            //pstmt = conn.prepareStatement("SELECT COUNT(distinct procname) FROM sysprocedures WHERE isproc = 't' and mode='O' and type==0");

            pstmt = conn.prepareStatement("SELECT COUNT(distinct procname) FROM sysprocedures WHERE isproc = 't' and mode='O'"+andtype);
            rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            objectList.setInfo(num);
            rs.close();

            pstmt=conn.prepareStatement(
                    //"select distinct '"+databaseName+"',procname,owner FROM sysprocedures WHERE isproc = 't' and mode='O' and  type==0");

                    "select distinct '"+databaseName+"',procname,owner FROM sysprocedures WHERE isproc = 't' and mode='O'"+andtype);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                Procedure procedure = new Procedure(rs.getString(2));
                procedure.setDatabase(rs.getString(1));
                procedure.setOwner(rs.getString(3));
                result.add(procedure);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }


    public ObjectList getDBPackages(TreeItem<TreeData> treeItem)  {
        String databaseName=MetadataTreeviewUtil.getCurrentDatabase(treeItem).getName();
        ObjectList objectList=new ObjectList();
        List<DBPackage> result=new ArrayList<>();
        objectList.setItems(result);
        Connection conn=getMetaSession(treeItem);;
        try{
            int systemTabid=100;
            PreparedStatement pstmt = conn.prepareStatement("select tabid  from systables where tabname='dual'");
            ResultSet rs=pstmt.executeQuery();
            if(rs.next()) {
                if(rs.getInt(1)>100){
                    systemTabid=1000;
                }
            }else{
                systemTabid=1000;
            }
            rs.close();

            String num=null;
            //ResultSet rs=statement.executeQuery("select count(*) from systables where tabid<100");
            pstmt = conn.prepareStatement("SELECT COUNT(distinct procname) FROM sysprocedures WHERE mode='O' and retsize=0");
            rs=pstmt.executeQuery();
            rs.next();
            num=rs.getString(1)+"个";
            objectList.setInfo(num);
            rs.close();

            pstmt=conn.prepareStatement(
                    "select '"+databaseName+"',procname,owner,count(*) FROM sysprocedures WHERE mode='O' and retsize=0 group by 1,2,3 order by 1,2");
            rs=pstmt.executeQuery();
            while (rs.next()) {
                DBPackage dbpackage = new DBPackage(rs.getString(2));
                dbpackage.setDatabase(rs.getString(1));
                dbpackage.setOwner(rs.getString(3));
                if(rs.getInt(4)==1) {
                    dbpackage.setIsEmpty(true);
                }else{
                    dbpackage.setIsEmpty(false);
                }
                result.add(dbpackage);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return objectList;
    }


    public ArrayList<ColumnsInfo> getCols(TreeItem<TreeData> treeItem){
        ArrayList<ColumnsInfo> arrayList=null;
        Connection conn=getMetaSession(treeItem);
        try{
       arrayList=GetDDLUtil.getColInfo(conn,treeItem.getValue().getName());
       } catch (SQLException e) {
            //e.printStackTrace();
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
       return arrayList;

    }
    public String getDDL(TreeItem<TreeData> treeItem)  {
        String result="";
        Connection conn=getMetaSession(treeItem);
        try{

            if(treeItem.getValue() instanceof Table){
                result= GetDDLUtil.printTable(conn,treeItem.getValue().getName());
            }
            else if(treeItem.getValue() instanceof View){
                result= String.valueOf(GetDDLUtil.printView(conn,treeItem.getValue().getName()));
            }
            else if(treeItem.getValue() instanceof Index){
                result= String.valueOf(GetDDLUtil.printIndex(conn,treeItem.getValue().getName()));
            }
            else if(treeItem.getValue() instanceof Sequence){
                result= String.valueOf(GetDDLUtil.printSequence(conn,treeItem.getValue().getName()));
            }
            else if(treeItem.getValue() instanceof Synonym){
                result= String.valueOf(GetDDLUtil.printSynonym(conn,treeItem.getValue().getName()));
            }
            else if(treeItem.getValue() instanceof Trigger){
                result= String.valueOf(GetDDLUtil.printTrigger(conn,treeItem.getValue().getName()));
            }
            else if(treeItem.getValue() instanceof Function||treeItem.getValue() instanceof Procedure){
                result= String.valueOf(GetDDLUtil.printProcedure(conn,treeItem.getValue().getName()));
            }
            else if(treeItem.getValue() instanceof DBPackage){
                result= String.valueOf(GetDDLUtil.printPackage(conn,treeItem.getValue().getName()));
            }
            else if(treeItem.getValue() instanceof PackageFunction||treeItem.getValue() instanceof PackageProcedure){
                result= String.valueOf(GetDDLUtil.printPackageFunction(((DBPackage)treeItem.getParent().getValue()).getDDL(),treeItem.getValue().getName()));
            }


        } catch (SQLException e) {
            //e.printStackTrace();
            if(e.getErrorCode()==-79716||e.getErrorCode()==-79730){
                Platform.runLater(() -> {
                    if (AlterUtil.CustomAlertConfirm("错误", "数据库已断开连接，是否需要重新连接？")) {
                        MetadataTreeviewUtil.reconnectItem(treeItem);
                    }
                });
            }else{
                Platform.runLater(() -> {
                    AlterUtil.CustomAlert("错误", "["+e.getErrorCode()+"]"+e.getMessage());
                });
            }

        }
        catch(Exception e){
            Platform.runLater(() -> {
                AlterUtil.CustomAlert("错误", e.toString());
            });
        }finally  {
            if(conn!=MetadataTreeviewUtil.getMetaConnect(treeItem).getConn()){
                try {
                    conn.close();
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlterUtil.CustomAlert("错误", e.toString());
                    });
                }
            }
        }
        return result;
    }


    public void modifyChunkExtendAble(Connect connect,int chunkId,boolean toExtendAble) throws Exception {
        Connection conn = null;
        conn = getConnection(connect);
        sessionChangeToGbaseMode(conn);
        if(toExtendAble){
            conn.createStatement().execute("EXECUTE FUNCTION sysadmin:task (\"modify chunk extendable on\","+chunkId+")");
        }else{
            conn.createStatement().execute("EXECUTE FUNCTION sysadmin:task (\"modify chunk extendable off\","+chunkId+")");
        }
                conn.close();
    }

    public void unLimitedSpaceSize(Connect connect,String dbspace) throws Exception {
        Connection conn = null;
        conn = getConnection(connect);
        sessionChangeToGbaseMode(conn);
        conn.createStatement().execute("EXECUTE FUNCTION sysadmin:task (\"modify space sp_sizes\",\""+dbspace+"\",\""+10+"\",\""+10000+"\",\""+0+"\")");
        conn.close();
    }

    public List<List<CustomSpaceChart.SpaceUsage>> getInstanceDbspaceInfo(Connect connect) throws Exception {
        List result=new ArrayList();
        Connection conn=null;

            conn=getConnection(connect);
            sessionChangeToGbaseMode(conn);
            List<CustomSpaceChart.SpaceUsage> dbspaceList =new ArrayList();
            List<CustomSpaceChart.SpaceUsage> chunkList =new ArrayList();
            List<CustomSpaceChart.SpaceUsage> databaseList =new ArrayList();
            List<CustomSpaceChart.SpaceUsage> tabList =new ArrayList();

            String sql= """

                    SELECT
          A.dbsnum as No,
          case when is_temp==1 then '[T]' else '' end
          ||
          case when is_sbspace==1 then '[S]'
          when is_blobspace==1 then '[B]'
          else '' end||
          case when max_size>0 then '[L]' else '' end||
          trim(B.name)||'['||round(A.pagesize/1024)||'k]' as label,trim(B.name) as name,
          sum(is_extendable),
          round(sum(case when is_sbchunk==1 then udsize else chksize end)*2/1024/1024,2)  as data_SIZE ,
          --round(sum(decode(mdsize,-1,chksize,udsize))*2/1024/1024,2)  as data_SIZE ,
          round(sum(case when is_sbchunk==1 then udsize-udfree when is_blobchunk==1 then chksize-nfree*a.pagesize/2048  else chksize-nfree end)*2/1024/1024,2) as dataused_size
          --round(sum(decode(mdsize,-1,chksize,udsize))*2/1024/1024-sum(decode(mdsize,-1,nfree,udfree))*2/1024/1024,2) as dataused_size
          ,sum(e.extents),
          round(sum(decode(mdsize,-1,0,mdsize))*2/1024/1024,2)  as Meta_SIZE ,
          round(sum(decode(mdsize,-1,0,mdsize))*2/1024/1024-sum(decode(mdsize,-1,0,nfree))*2/1024/1024,2) as metaused_size,
          max(max_size)/1024
          FROM sysmaster:syschunks A join sysmaster:sysdbspaces B on A.dbsnum = B.dbsnum
          left join (select chunk,count(*)-1 as extents from sysmaster:sysextents group by chunk) e on E.chunk=A.chknum
          group by 1,2,3
          order by 1
          
""";
            PreparedStatement pstmt=conn.prepareStatement(sql);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next()){
                Double total=rs.getDouble(5);
                Double metaSize=rs.getDouble(8);
                if(metaSize>0){
                    total=metaSize+total;
                }
                CustomSpaceChart.SpaceUsage spaceUsage=
                new CustomSpaceChart.SpaceUsage(
                        rs.getInt(1),
                        (String) rs.getString(2),
                        (String) rs.getString(3),   // name
                        rs.getInt(4),
                        total,   //total
                        (Double) rs.getDouble(6), //used
                        rs.getInt(7),
                        0,0,metaSize,rs.getDouble(9)   // total
                );
                spaceUsage.setLimitSize(rs.getDouble(10));
                dbspaceList.add(spaceUsage);
            }
            result.add(dbspaceList);

            sql= """
SELECT A.dbsnum as No,A.chknum,
trim(fname)||' [ '||trim(B.name)||' ] ' as label,trim(fname) as filename,
is_extendable,
round((case when is_sbchunk==1 then udsize else chksize end)*2/1024/1024,2)  as data_SIZE ,
round((case when is_sbchunk==1 then udsize-udfree when is_blobchunk==1 then chksize-nfree*a.pagesize/2048  else chksize-nfree end)*2/1024/1024,2) as dataused_size,
e.extents,chksize,
chksize -nfree,
round((decode(mdsize,-1,0,mdsize))*2/1024/1024,2)  as Meta_SIZE ,
round((decode(mdsize,-1,0,mdsize))*2/1024/1024-(decode(mdsize,-1,0,nfree))*2/1024/1024,2) as metaused_size
FROM sysmaster:syschunks A join sysmaster:sysdbspaces B on A.dbsnum = B.dbsnum
left join (select chunk,count(*)-1 as extents from sysmaster:sysextents group by chunk) e on E.chunk=A.chknum
order by 1,2

""";
            pstmt=conn.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while(rs.next()){
                Double total=rs.getDouble(6);
                Double metaSize=rs.getDouble(11);
                if(metaSize>0){
                    total=metaSize+total;
                }
                CustomSpaceChart.SpaceUsage spaceUsage=
                        new CustomSpaceChart.SpaceUsage(
                                rs.getInt(2),
                                (String) rs.getString(3),
                                (String) rs.getString(4),   // name
                                (rs.getInt(5)),
                                total,
                                (Double) rs.getDouble(7),
                                rs.getInt(8),
                                rs.getInt(9),rs.getInt(10) ,metaSize,rs.getDouble(12)  // total
                        );
                chunkList.add(spaceUsage);
            }
            result.add(chunkList);

        sql= """
select dbsname,round(sum(sin.ti_nptotal*sd.pagesize/1024/1024/1024),2) total_size,
 round(sum(sin.ti_npused*sd.pagesize/1024/1024/1024),2) used_size
from
sysmaster:systabnames st JOIN sysmaster:systabinfo sin ON  st.partnum=sin.ti_partnum
JOIN sysmaster:sysdbspaces sd ON sd.dbsnum = trunc(st.partnum/1048576) and sd.name!=st.dbsname
where dbsname!='system'
group by dbsname
order by total_size desc
""";
        pstmt=conn.prepareStatement(sql);
        rs=pstmt.executeQuery();
        while(rs.next()){
            CustomSpaceChart.SpaceUsage spaceUsage=
                    new CustomSpaceChart.SpaceUsage(0,
                            (String) rs.getString(1),
                            (String) rs.getString(1),   // name
                            0,
                            (Double) rs.getDouble(2),   // total
                            (Double) rs.getDouble(3),  //used
                            0,
                            0,
                            0 ,0,0  // total
                    );
            databaseList.add(spaceUsage);
        }
        result.add(databaseList);

            //LO_hdr_partn每个chunk有一个，可能重复，需拼接一个partnum
            sql= """

select first 20
sin.ti_nptotal nptotal,trim(st.dbsname)||':'||
case when trim(st.tabname)=='LO_hdr_partn' or trim(st.tabname)=='LO_ud_free' then
trim(st.tabname)||'['||st.partnum||']' else trim(st.tabname) end
,
 round(sin.ti_nptotal*sd.pagesize/1024/1024/1024,2) total_size,
 round(sin.ti_npused*sd.pagesize/1024/1024/1024,2) used_size,
    sin.ti_nptotal,
    sin.ti_npdata
from
sysmaster:systabnames st JOIN sysmaster:systabinfo sin ON  st.partnum=sin.ti_partnum
JOIN sysmaster:sysdbspaces sd ON sd.dbsnum = trunc(st.partnum/1048576)
where sin.ti_nptotal>0
order by ti_nptotal desc
""";
            pstmt=conn.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while(rs.next()){
                CustomSpaceChart.SpaceUsage spaceUsage=
                        new CustomSpaceChart.SpaceUsage(0,
                                (String) rs.getString(2),
                                (String) rs.getString(2),   // name
                                0,
                                (Double) rs.getDouble(3),   // used
                                (Double) rs.getDouble(4),1,
                                rs.getInt(5),
                                rs.getInt(6) ,0,0  // total
                        );
                tabList.add(spaceUsage);
            }
            result.add(tabList);
            rs.close();
            pstmt.close();
            conn.close();

        return result;
    }


    public double getMaxDbspaceUsed(Connect connect) throws Exception {
        double result=0;
        Connection conn=null;
        conn=getConnection(connect);
        sessionChangeToGbaseMode(conn);

        String sql= """

                    SELECT first 1
                    
                              trim(B.name) as name,
                              sum(is_extendable),
                    case when
                    round(
                    (sum(case when is_sbchunk==1 then udsize-udfree when is_blobchunk==1 then chksize-nfree*a.pagesize/2048  else chksize-nfree end)*2/1024/1024)
                             / (sum(case when is_sbchunk==1 then udsize else chksize end)*2/1024/1024)*100,2)
                    
                    >
                    round(sum(decode(mdsize,-1,0,mdsize-nfree))/sum(decode(mdsize,-1,1,mdsize))*100,2)
                    then
                    round(
                    (sum(case when is_sbchunk==1 then udsize-udfree when is_blobchunk==1 then chksize-nfree*a.pagesize/2048  else chksize-nfree end)*2/1024/1024)
                             / (sum(case when is_sbchunk==1 then udsize else chksize end)*2/1024/1024)*100,2)
                    else round(sum(decode(mdsize,-1,0,mdsize-nfree))/sum(decode(mdsize,-1,1,mdsize))*100,2)
                    end
                    as percent
                    
                              ,sum(e.extents)
                    
                              FROM sysmaster:syschunks A join sysmaster:sysdbspaces B on A.dbsnum = B.dbsnum
                              left join (select chunk,count(*)-1 as extents from sysmaster:sysextents group by chunk) e on E.chunk=A.chknum
                              group by 1
                    having sum(is_extendable) =0
                    and sum(e.extents)!=0
                    order by percent desc;
          
""";
        PreparedStatement pstmt=conn.prepareStatement(sql);
        ResultSet rs=pstmt.executeQuery();
        rs.next();
        result=rs.getDouble(3);

        rs.close();
        pstmt.close();
        conn.close();

        return result;
    }

}
