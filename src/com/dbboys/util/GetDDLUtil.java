package com.dbboys.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dbboys.vo.ColumnsInfo;


// 单个存储过程或者函数
class ProcedureInfo{
    String ProcName;        // name
    String ProcBoday;       // proc boday
    /*
     * 函数/过程标识(int 4字节)：
     * 1, 最低位开始第三位是1时（位与4值为4时），SQLMODE=Oracle，
     */
    int ProcFlags;

    /**
     * 返回存储过程/函数的数据库模式
     * @return
     */
    public String getProcSqlMode(){
        if((this.ProcFlags & 4) == 4){
            return "Oracle";
        }
        return "GBase";
    }

    @Override
    public String toString(){
        return "ProcName: " + this.ProcName + "\n" +
                "ProcBoday: " + this.ProcBoday;
    }
}

// 单个视图
class ViewInfo{
    String ViewName;        // name
    String ViewBoday;       // view ddl
    /*
     * 表标识(smallint两字节)：
     * 1, 最高位开始第一位是1时（位与16384值为16384时），SQLMODE=Oracle，
     */
    int Flags;

    /**
     * 返回视图的数据库模式
     * @return
     */
    public String getViewSqlMode(){
        if ((this.Flags & 16384) == 16384) {
            return "Oracle";
        }
        return "GBase";
    }

    public String toString(){
        return "ViewName: " + this.ViewName + "\n" +
                "ViewBody: " + this.ViewBoday;
    }
}

// 单个同义词
class SynonymInfo{
    String SynType;         // type: public(P) or private (S)
    String SynOwner;        // owner
    String SynName;         // name
    String RServerName;     // remote servername
    String RDbName;         // remote dbname
    String ROwner;          // remote table owner
    String RTabName;        // remote table
    String LOwner;          // local table owner
    String LTabName;        // local table
    /*
     * 表标识(smallint两字节)：
     * 1, 最高位开始第一位是1时（位与16384值为16384时），SQLMODE=Oracle，
     */
    int Flags;

    /**
     * 返回同义词的数据库模式
     * @return
     */
    public String getSynonymSqlMode(){
        if ((this.Flags & 16384) == 16384) {
            return "Oracle";
        }
        return "GBase";
    }

    public String toString(){
        return "SynType: " + this.SynType + "\n" +
                "SynOwner: " + this.SynOwner + "\n" +
                "SynName: " + this.SynName + "\n" +
                "RServerName: " + this.RServerName + "\n" +
                "RDbName: " + this.RDbName + "\n" +
                "ROwner: " + this.ROwner + "\n" +
                "RTabName: " + this.RTabName + "\n" +
                "LOwner: " + this.LOwner + "\n" +
                "LTabName: " + this.LTabName;
    }
}

// 单个序列
class SequenceInfo{
    String SeqOwner;    // owner
    String SeqName;     // name
    long StartVal;      // start value
    long IncVal;        // increment value
    long MaxVal;        // max value
    long MinVal;        // min value
    String IsCycle;     // 0 nocycle, 1 cycle;
    long cache;         // 0 nocache
    String IsOrder;     // 0 noorder, 1 order;
    /*
     * 表标识(smallint两字节)：
     * 1, 最高位开始第一位是1时（位与16384值为16384时），SQLMODE=Oracle，
     */
    int Flags;

    /**
     * 返回序列的数据库模式
     * @return
     */
    public String getSequenceSqlMode(){
        if ((this.Flags & 16384) == 16384) {
            return "Oracle";
        }
        return "GBase";
    }

    public String toString() {
        return "SeqName: " + this.SeqName + "\n";
    }
}

// 单个触发器
class TriggerInfo{
    String TriggerName;     // name
    String TriggerBody;     // boday

    public String toString(){
        return "TriggerName: " + this.TriggerName + "\n" +
                "TriggerBoday: " + this.TriggerBody;
    }
}

// 单个索引
class IndexInfo{
    String IndexName;
    String IndexOwner;
    String TableName;
    String TableOwner;
    String IndexType;           // 索引类型
    String IndexCluster;        // 是否cluster类型
    String IndexCols;           // 索引字段列表
    public String toString(){
        return "IndexName: " + this.IndexName;
    }
}

// 分片定义
/*
   Strategy 分段分布策略的类型的代码：
   R = 循环分段存储策略
   E = 基于表达式的分段存储策略
   I = IN DBSPACE 子句指定作为分段存储策略一部分的存储位置
   N = 时间间隔（或滚动窗口）分段存储策略
   L = 列表分段存储策略
   T = 基于表的分段存储策略
   H = 表是表层次结构内的子表
 */
class FragmentInfo{
    String FragName;        // 分片名称
    int ColNo;
    String Strategy;        // 分片策略
    int Evalpos;            // 序号
    String Exprtext;        // 分片表达式
    int Flags;              // 标识
    String Dbspace;         // 所在dbs名称
    String Partition;       // 分片名称

    public String toString(){
        return "FragName: " + this.FragName + "\n" +
                "ColNo: " + this.ColNo + "\n" +
                "Strategy: " + this.Strategy + "\n" +
                "Evalpos: " + this.Evalpos + "\n" +
                "Exprtext: " + this.Exprtext + "\n" +
                "Flags: " + this.Flags + "\n" +
                "Dbspace: " + this.Dbspace + "\n" +
                "Partition: " + this.Partition;
    }
}

// 表基础信息
class TableInfo{
    String TableName;           // 表名
    String TableCatalog;        // catalog
    String TableOwner;          // 属主
    String lockType;            // 锁类型 P 页锁， R 行锁， B 页锁和行锁
    int firstExtSize;           // 首区段大小
    int nextExtSize;            // 下一区段大小
    String TableComm;           // 表注释
    String TableType;           // 表类型：T 表, E 外部表, V 视图, Q 序列, P 专用同义词, S 公共同义词
    @Deprecated
    String TableSqlMode;        // 建表模式：Oracle, GBase, MySql
    /*
     * 表标识(smallint两字节)：
     * 1, 最高位开始第一位是1时（位与16384值为16384时），SQLMODE=Oracle，
     * 2，最高位开始第二位是1时（位与8192值为8192时），事务级（commit delete）；为0时则为 会话级（COMMIT PRESERVE）
     * 3，最商位开始第三位是1时（位与4096值为4096时），全局临时表；为0时则为默认的永久表
     */
    int Flags;
    int dbVersion;

    /**
     * 返回 SQLMODE
     * @return
     */
    public String getTableSqlMode(){
        if ((this.Flags & 16384) == 16384) {
            return "Oracle";
        }
        return "GBase";
    }

    /**
     * 返回 全局临时表 标识
     * @return
     */
    public String getTableGlobalTemporary(){
        if ((this.Flags & 4096) == 4096) {
            return "GLOBAL TEMPORARY";
        }
        return "";
    }

    /**
     * 返回 全局临时表 级别
     * @return
     */
    public String getTableGlobalTemporaryLevel(){
        if ((this.Flags & 8192) == 8192) {
            return "ON COMMIT DELETE ROWS";
        }
        return "ON COMMIT PRESERVE ROWS";
    }

    /**
     * 返回表锁类型
     * @return
     */
    public String getLockType(){
        if("P".equals(this.lockType)){
            return "PAGE";
        } else if("B".equals(this.lockType)){
            return "PAGE,ROW";
        }
        return "ROW";
    }

    @Override
    public String toString(){
        return "Tablename: " + this.TableName + "\n" +
                "TableCatalog: " + this.TableCatalog + "\n" +
                "TableOwner: " + this.TableOwner + "\n" +
                "TableSqlMode: " + this.TableSqlMode;
    }
}


// 检查约束
class CheckInfo{
    String constrName;          // check约束名
    String checkText;           // check约束内容
    public String toString(){
        return "CheckName: " + this.constrName + "\n" +
                "CheckText: " + this.checkText;
    }
}

// 包含主键及唯一约束
class PrimaryKeyInfo{
    String constrName;          // 约束名
    String constrType;          // 约束类型
    String idxCols;             // 索引字段
    public String toString(){
        return "constrName: " + this.constrName + "\n" +
                "constrType: " + this.constrType + "\n" +
                "index columns: " + this.idxCols;
    }
}

// 多个索引信息，用于生成表的ddl
class IndexesInfo{
    String idxName;
    String idxOwner;
    String idxType;
    String idxCluster;
    String idxCols;
    public String toString(){
        return "idxName: " + this.idxName;
    }
}

// 外部表外部信息定义
class ExtTableInfo{
    String TableName;
    String FormatType;          // FORMAT值，格式类型：D:定界符（delimited）（默认），F:固定长度（fixed），I:内部使用（informix/gbasedbt）
    String CodeSet;             // ? 定界符类型 ?
    String RecordDelimiter;     // RECORDEND值，记录分隔符    默认  "\n"
    String FieldDelimiter;      // DELIMITER值，字段分隔符    默认  "|"
    String DateFormat;          // DBDATE值
    String MoneyFormat;         // DBMONEY值，货币格式
    int MaxErrors;               // MAXERRORS值，
    String RejectFile;          // REJECTFILE值，拒绝文件类型
    int Flags;                  // 0：Escape off; 2: Escape on; (默认) 4: DELUXE; 8: Express，可0/2 + 4/8
    int NumDfiles;              // 指明外部存储定义
    public String toString(){
        return "TableName: " + this.TableName;
    }
}

// 外部表存储定义（可能多行）
class ExtTableDfiles{
    String TableName;
    String DataFile;
    String BlobDir;
    String ClobDir;
    public String toString(){
        return "TableName: " + this.TableName + "\n" +
                "DataFile: " + this.DataFile;
    }
}

// 外键定义
class ForeignKeyInfo{
    String FKName;
    String FKOwner;
    String FKTabname;
    String FKCols;
    String FKIdxName;
    String PKOwner;
    String PKTabname;
    String PKCols;
    String PKIdxName;
    public String toString(){
        return "FKName: " + this.FKName;
    }
}


public class GetDDLUtil {

    /**
     * 删除前后空格
     * @param str
     * @return
     */
    private static String trim(String str){
        if (str == null){
            return null;
        } else {
            return str.trim();
        }
    }

    /**
     * 去除字符串右侧的'\0'，需要考虑null值
     * @param str
     * @return
     */
    private static String rtrimascii0(String str){
        if(str == null){
            return null;
        }
        int i = str.length();
        while (i > 0 && str.charAt(i-1) == '\0'){        // '\0' 表示ascii 0
            i--;
        }
        return str.substring(0,i);
    }

    /**
     * 根据参数判断是否需要加双引号
     * @param str
     * @return
     */
    private static String getName(String str){
        String parttern_delimident = "^[a-z_][a-z0-9_]*$";
        if(Pattern.matches(parttern_delimident,str)){
            return str;
        } else {
            return "\"" + str + "\"";
        }
    }

    /**
     * 分片表的分片信息
     * @param connection
     * @param tablename
     * @return
     * @throws SQLException
     */
    private static ArrayList<FragmentInfo> getTableFragmentInfo(Connection connection, String tablename) throws SQLException {
        ArrayList<FragmentInfo> arrayList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sqlstr = "SELECT frag.colno, frag.strategy, frag.evalpos, frag.exprtext, frag.flags, frag.dbspace, frag.partition " +
                "FROM sysfragments frag, systables tab " +
                "WHERE frag.tabid = tab.tabid " +
                "AND tab.tabname = ? " +
                "AND frag.fragtype = 'T' " +
                "ORDER BY frag.evalpos";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tablename);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            FragmentInfo tableFragmentInfo = new FragmentInfo();
            tableFragmentInfo.FragName = tablename;
            tableFragmentInfo.ColNo = resultSet.getInt("colno");
            tableFragmentInfo.Strategy = resultSet.getString("strategy");
            tableFragmentInfo.Evalpos = resultSet.getInt("evalpos");
            tableFragmentInfo.Exprtext = resultSet.getString("exprtext");
            tableFragmentInfo.Flags = resultSet.getInt("flags");
            tableFragmentInfo.Dbspace = resultSet.getString("dbspace");
            tableFragmentInfo.Partition = resultSet.getString("partition");
            arrayList.add(tableFragmentInfo);
        }
        resultSet.close();
        preparedStatement.close();
        return arrayList;
    }

    /**
     * resultset转Columnsinfo列表
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static ArrayList<ColumnsInfo> resultSet2ColumnsInfoList(ResultSet resultSet, int dbVersion) throws SQLException {
        ArrayList<ColumnsInfo> arrayList = new ArrayList<>();
        int sizeofarrayList = 0;
        // 存在多行的情况，需要考虑使用 insert or update 方式
        while(resultSet.next()){
            ColumnsInfo columnsInfo = new ColumnsInfo();
            columnsInfo.setColNo(resultSet.getInt("colno"));
                sizeofarrayList = arrayList.size();
                // default值存在多行表示一个值时
                if(sizeofarrayList > 0 && arrayList.get(sizeofarrayList-1).getColNo() == columnsInfo.getColNo()){
                columnsInfo.setColName(arrayList.get(sizeofarrayList-1).getColName());
                columnsInfo.setColType(arrayList.get(sizeofarrayList-1).getColType());
                columnsInfo.setColLength(arrayList.get(sizeofarrayList-1).getColLength());
                columnsInfo.setTypeP(arrayList.get(sizeofarrayList-1).getTypeP());
                columnsInfo.setTypeS(arrayList.get(sizeofarrayList-1).getTypeS());
                columnsInfo.setIsNullable(arrayList.get(sizeofarrayList-1).isIsNullable());
                columnsInfo.setIsPK(arrayList.get(sizeofarrayList-1).isIsPK());
                columnsInfo.setColDefType(arrayList.get(sizeofarrayList-1).getColDefType());
                columnsInfo.setColDef(arrayList.get(sizeofarrayList-1).getColDef() + trim(resultSet.getString("coldef")));
                columnsInfo.setColComm(arrayList.get(sizeofarrayList-1).getColComm());
                columnsInfo.setIsAutoincrement(arrayList.get(sizeofarrayList-1).isIsAutoincrement());
                arrayList.set(sizeofarrayList-1,columnsInfo);
            } else {
                columnsInfo.setColName(resultSet.getString("colname"));
                columnsInfo.setColType(getColTypeName(resultSet.getInt("coltype"), resultSet.getInt("collength"), 0, 0, resultSet.getString("sxname")));
                columnsInfo.setColLength(getLength(columnsInfo.getColType(), resultSet.getInt("collength"), dbVersion));
                columnsInfo.setTypeP(getPrecision(columnsInfo.getColType(), resultSet.getInt("collength")));
                columnsInfo.setTypeS(getScale(columnsInfo.getColType(), resultSet.getInt("collength")));
                columnsInfo.setIsNullable((resultSet.getInt("isnullable") == 1) ? true : false);
                columnsInfo.setIsPK((resultSet.getInt("ispk") == 1) ? true : false);
                columnsInfo.setColDefType(resultSet.getString("coldeftype"));
                columnsInfo.setColDef(trim(resultSet.getString("coldef")));
                columnsInfo.setColComm(trim(resultSet.getString("colcomm")));
                columnsInfo.setIsAutoincrement((resultSet.getInt("isautoincrement") == 1) ? true : false);
                arrayList.add(columnsInfo);
            }
        }
        return arrayList;
    }

    /**
     * 依据collength返回datetime/interval的类型的后半部分（eg: year to second)
     * @param collength
     * @return
     */
    private static String getDTColTypeName(int collength){
        String coltypeName = null;
        String[] dtname = {"YEAR","","MONTH","","DAY","","HOUR","","MINUTE","","SECOND","FRACTION(1)","FRACTION(2)","FRACTION(3)","FRACTION(4)","FRACTION(5)"};
        int mylength = 0;
        mylength = (collength % 256) / 16;
        coltypeName = " " + dtname[mylength];
        mylength = collength % 16;
        coltypeName = coltypeName + " TO " + dtname[mylength];
        return coltypeName;
    }

    /**
     * 仅日期长度
     * @param coltype
     * @return
     */
    @Deprecated
    private static int getDTLength(String coltype){
        int mylength = 0;
        if     ("YEAR TO DAY".equals(coltype)) { mylength=10; }
        else if("YEAR TO HOUR".equals(coltype)) { mylength=13; }
        else if("YEAR TO MINUTE".equals(coltype)) { mylength=16; }
        else if("YEAR TO SECOND".equals(coltype)) { mylength=19; }
        else if("YEAR TO FRACTION(1)".equals(coltype)) { mylength=21; }
        else if("YEAR TO FRACTION(2)".equals(coltype)) { mylength=22; }
        else if("YEAR TO FRACTION(3)".equals(coltype)) { mylength=23; }
        else if("YEAR TO FRACTION(4)".equals(coltype)) { mylength=24; }
        else if("YEAR TO FRACTION(5)".equals(coltype)) { mylength=25; }
        else if("HOUR TO HOUR".equals(coltype)) { mylength=2; }
        else if("HOUR TO MINUTE".equals(coltype)) { mylength=5; }
        else if("HOUR TO SECOND".equals(coltype)) { mylength=8; }
        else if("HOUR TO FRACTION(1)".equals(coltype)) { mylength=10; }
        else if("HOUR TO FRACTION(2)".equals(coltype)) { mylength=11; }
        else if("HOUR TO FRACTION(3)".equals(coltype)) { mylength=12; }
        else if("HOUR TO FRACTION(4)".equals(coltype)) { mylength=13; }
        else if("HOUR TO FRACTION(5)".equals(coltype)) { mylength=14; }
        return mylength;
    }

    /**
     * 仅日期长度。 从collength的值计算字符显示长度
     * @param collength
     * @return
     */
    private static int getDTLength(int collength){
        int mylength = 2;
        int first = (collength % 256) / 16;
        int last  = collength % 16;
        // 分别对应 yyyy,-,mm,-,dd,' ',hh24,':',mi,':',ss,'.1',2,3,4,5
        int[] len = {4,5,7,8,10,11,13,14,16,17,19,21,22,23,24,25};
        // 当first为0（year）时，长度即为last的长度。
        if (first == 0){
            mylength = len[last];
        } else {
            mylength = len[last] - len[first-1];
        }
        return mylength;
    }

    /**
     * 原计划获取字段长度。
     * @param coltype
     * @param collength
     * @return
     */
    @Deprecated
    private static int getLength(String coltype, int collength){
        return getLength(coltype,collength,3);
    }

    /**
     * 获取字段长度，按版本区分
     * @param coltype
     * @param collength
     * @param dbver
     * @return
     */
    private static int getLength(String coltype, int collength, int dbver){
        int mycollen = collength;
        if     ("SMALLINT".equals(coltype) || "BOOLEAN".equals(coltype)){ mycollen=5; }
        else if("INTEGER".equals(coltype) || "SERIAL".equals(coltype) || "DATE".equals(coltype)){ mycollen=10; }
        else if("INT8".equals(coltype) || "SERIAL8".equals(coltype) || "BIGINT".equals(coltype) || "BIGSERIAL".equals(coltype)){ mycollen=19; }
        else if("FLOAT".equals(coltype)){ mycollen=17; }
        else if("SMALLFLOAT".equals(coltype)){ mycollen=7; }
        else if("DECIMAL".equals(coltype) || "MONEY".equals(coltype)){ mycollen=collength/256; }
        else if("TEXT".equals(coltype) || "BYTE".equals(coltype) || "BLOB".equals(coltype) || "CLOB".equals(coltype)) { mycollen=2147483647; }
        // collength = (min_space * 256) + max_size （对于2.0及之前），collength = (min_space * 65536) + max_size（3.0及之后）
        else if("VARCHAR".equals(coltype) || "NVARCHAR".equals(coltype) || "VARCHAR2".equals(coltype) || "NVARCHAR2".equals(coltype)){
            if (dbver == 3) {
                if(collength > 0){
                    mycollen = collength%65536;
                } else {
                    mycollen = Long.valueOf((collength + 4294967296L) % 65536).intValue();
                }
            } else {
                if(collength > 0){
                    mycollen=collength%256;
                } else {
                    mycollen = (collength + 65536) % 256;
                }
            }
        }
        else if(coltype.startsWith("DATETIME")){ mycollen=getDTLength(collength); }
        return mycollen;
    }

    /**
     * 返回数据类型名称。除40、41需要使用扩展类型外，其它都可通过coltype,collength计算得到。
     * @param coltype
     * @param collength
     * @param extended_id
     * @param df
     * @param extypename
     * @return
     */
    private static String getColTypeName(int coltype, int collength, int extended_id, int df, String extypename){
        String coltypeName = null;
        int mycoltype = coltype % 256;
        if     (mycoltype == 0){ coltypeName = "CHAR"; }
        else if(mycoltype == 1){ coltypeName = "SMALLINT"; }
        else if(mycoltype == 2){ coltypeName = "INTEGER"; }
        else if(mycoltype == 3){ coltypeName = "FLOAT"; }
        else if(mycoltype == 4){ coltypeName = "SMALLFLOAT"; }
        else if(mycoltype == 5){ coltypeName = "DECIMAL"; }
        else if(mycoltype == 6){ coltypeName = "SERIAL"; }
        else if(mycoltype == 7){ coltypeName = "DATE"; }
        else if(mycoltype == 8){ coltypeName = "MONEY"; }
        else if(mycoltype == 9){ coltypeName = "NULL"; }
        else if(mycoltype == 10){ coltypeName = "DATETIME" + getDTColTypeName(collength); }
        else if(mycoltype == 11){ coltypeName = "BYTE"; }
        else if(mycoltype == 12){ coltypeName = "TEXT"; }
        else if(mycoltype == 13){ coltypeName = "VARCHAR"; }
        else if(mycoltype == 14){ coltypeName = "INTERVAL" + getDTColTypeName(collength); }
        else if(mycoltype == 15){ coltypeName = "NCHAR"; }
        else if(mycoltype == 16){ coltypeName = "NVARCHAR"; }
        else if(mycoltype == 17){ coltypeName = "INT8"; }
        else if(mycoltype == 18){ coltypeName = "SERIAL8"; }
        else if(mycoltype == 19){ coltypeName = "SET(LVARCHAR)"; }
        else if(mycoltype == 20){ coltypeName = "MULTISET(SENDRECEIVE)"; }
        else if(mycoltype == 21){ coltypeName = "LIST"; }
        else if(mycoltype == 22){ coltypeName = "ROW"; }
        else if(mycoltype == 23){ coltypeName = "COLLECTION"; }
        else if(mycoltype == 24){ coltypeName = "ROWREF"; }
        else if(mycoltype == 40 || mycoltype == 41){ coltypeName = extypename.toUpperCase(); }
        else if(mycoltype == 42){ coltypeName = "REFSERIAL8"; }
        else if(mycoltype == 52){ coltypeName = "BIGINT"; }
        else if(mycoltype == 53){ coltypeName = "BIGSERIAL"; }
        else if(mycoltype == 63){ coltypeName = "VARCHAR2"; }
        else if(mycoltype == 64){ coltypeName = "NVARCHAR2"; }
        else if(mycoltype == 65){ coltypeName = "TIMESTAMP WITH TIME ZONE"; }
        return coltypeName;
    }

    /**
     * 获取标度。。  需补充及测试
     * @param coltype
     * @param collength
     * @return
     */
    private static int getScale(String coltype, int collength){
        int mys = 0;
        if ("DECIMAL".equals(coltype) || "MONEY".equals(coltype)) { mys=collength%256 ;}
        else if("VARCHAR".equals(coltype) || "NVARCHAR".equals(coltype) || "VARCHAR2".equals(coltype) ||
                "NVARCHAR".equals(coltype)) { mys=collength/65536; }
        return mys;
    }

    /**
     * 获取精度。。  需补充及测试
     * @param coltype
     * @param collength
     * @return
     */
    private static int getPrecision(String coltype, int collength){
        int myp = 0;
        if ("DECIMAL".equals(coltype) || "MONEY".equals(coltype)) { myp=collength/256; }
        else if("FLOAT".equals(coltype) || "SMALLFLOAT".equals(coltype)) {  myp=2; }
        else if("VARCHAR".equals(coltype) || "NVARCHAR".equals(coltype) || "VARCHAR2".equals(coltype) ||
                "NVARCHAR".equals(coltype) || "LVARCHAR".equals(coltype)) { myp=collength%256; }
        return myp;
    }

    /**
     * 获取表信息
     * @param connection
     * @param tablename
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static TableInfo getTableInfo(Connection connection, String tablename) throws SQLException {
        TableInfo tableInfo = new TableInfo();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int dbVersion = 3; // 默认数据库JDBC版本
        String sqlstr = "select t.tabname,dbinfo('dbname') as tablecatalog, t.owner as tableowner,t.locklevel as locktype, " +
                "t.fextsize as firstextsize, t.nextsize as nextextsize, c.comments as tablecomm, t.tabtype as tabletype," +
                "t.flags as tableflags " +
                "from systables t left join syscomments c on t.tabname = c.tabname " +
                "where t.tabname = ? ";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tablename);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            tableInfo.TableCatalog = resultSet.getString("tablecatalog");
            tableInfo.TableOwner = trim(resultSet.getString("tableowner"));
            tableInfo.lockType = trim(resultSet.getString("locktype"));
            tableInfo.firstExtSize = resultSet.getInt("firstextsize");
            tableInfo.nextExtSize = resultSet.getInt("nextextsize");
            tableInfo.TableComm = trim(resultSet.getString("tablecomm"));
            tableInfo.TableType = trim(resultSet.getString("tabletype"));
            tableInfo.Flags = resultSet.getInt("tableflags");
        }
        tableInfo.TableName = tablename;
        resultSet.close();

        String jdbcVersion = connection.getMetaData().getDriverVersion();
        dbVersion=Integer.parseInt(jdbcVersion.substring(0,1));
        tableInfo.dbVersion = dbVersion;

        preparedStatement.close();
        return tableInfo;
    }
    //added by L3 20260205，用于返回字段列表
    public static ArrayList<ColumnsInfo> getColInfo(Connection connection,String tabname) throws SQLException {
        ArrayList<ColumnsInfo> arrayList = new ArrayList<ColumnsInfo>();
        TableInfo tableInfo=getTableInfo(connection, tabname);
        arrayList=getColInfo(connection,tableInfo);
        return arrayList;
    }

    /**
     * 获取字段信息
     * @param connection
     * @param tablename
     * @param delimident
     * @throws SQLException
     */
    private static ArrayList<ColumnsInfo> getColInfo(Connection connection,TableInfo tableInfo) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<ColumnsInfo> arrayList = null;
        String sqlstr = null;

        // 对于default默认值，C=Current，L=Literal value,N=Null,S=Dbservername or Sitename，T=Today, U=User
        // 对于虚拟表，sysdefaultsexpr可能多行定义
        sqlstr = "SELECT " +
                "   sc.colno colno " +
                "  ,sc.colname colname " +
                "  ,sc.coltype,sc.collength " +
                "  ,CASE WHEN mod(sc.coltype,256) in (1,2,52,17,6,18,53,5,8) THEN 10 WHEN mod(sc.coltype,256) in (3,4) THEN 2 ELSE 0 END as typep " +
                "  ,CASE WHEN mod(sc.coltype,256) in (5,8) THEN MOD(sc.collength,256) ELSE 0 END as types " +
                "  ,CASE WHEN bitand(sc.coltype,256) = 256 THEN 0 ELSE 1 END as isnullable " +
                "  ,CASE WHEN sc.colattr = 128 THEN 1 ELSE 0 END as ispk " +
                "  ,df.type as coldeftype " +
                "  ,CASE df.type " +
                "         WHEN 'L' THEN get_default_value(sc.coltype, sc.extended_id, sc.collength, df.default::lvarchar(256))::VARCHAR(254) " +
                "         WHEN 'C' THEN 'current year to second'::VARCHAR(254) " +
                "         WHEN 'S' THEN 'dbservername'::VARCHAR(254) " +
                "         WHEN 'U' THEN 'user'::VARCHAR(254) " +
                "         WHEN 'T' THEN 'today'::VARCHAR(254) " +
                "         WHEN 'E' THEN de.default::VARCHAR(254) || ' ' " +
                "         ELSE          NULL::VARCHAR(254) " +
                "   END as coldef " +
                "  ,cc.comments as colcomm " +
                "  ,CASE WHEN mod(sc.coltype,256) in (6,18,53) THEN 1 ELSE 0 END as ISAUTOINCREMENT " +
                "  ,sx.name sxname " +
                "FROM systables t " +
                "LEFT JOIN syscolumns sc ON t.tabid = sc.tabid " +
                "LEFT JOIN syscolcomments cc ON (t.tabname = cc.tabname AND sc.colname = cc.colname) " +
                "LEFT JOIN sysdefaults df ON (t.tabid = df.tabid AND sc.colno = df.colno) " +
                "LEFT JOIN sysdefaultsexpr de ON (t.tabid = de.tabid AND sc.colno = de.colno and de.type='T') " +
                "LEFT JOIN sysxtdtypes sx ON (sx.type = mod(sc.coltype,256) AND sx.extended_id = sc.extended_id) " +
                "WHERE t.tabname = ? " +
                "ORDER BY sc.colno;";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tableInfo.TableName);
        resultSet = preparedStatement.executeQuery();
        // 调用函数处理resultset
        arrayList = resultSet2ColumnsInfoList(resultSet,tableInfo.dbVersion);
        resultSet.close();
        preparedStatement.close();
        return arrayList;
    }

    /**
     * 有精度和标度的数据类型需要处理。
     * @param coltype
     * @param collength
     * @param typep
     * @param types
     * @return
     */
    private static String getColTypeName(String coltype, int collength, int typep, int types){
        String coltypename = coltype;
        // 有最小字段长度，需处理
        if (coltype.startsWith("VARCHAR") || coltype.startsWith("NVARCHAR")) {
            if (types > 0){
                coltypename = coltypename + "(" + collength + "," + types + ")";
            } else {
                coltypename = coltypename + "(" + collength + ")";
            }
        } else if ("DECIMAL".equals(coltype) || "MONEY".equals(coltype)){
            if (types == 255){
                coltypename = coltypename + "(" + typep + ")";
            } else {
                coltypename = coltypename + "(" + typep + "," + types + ")";
            }
        } else if ("LVARCHAR".equals(coltype) || "CHAR".equals(coltype) || "NCHAR".equals(coltype)) {
            coltypename = coltypename + "(" + collength + ")";
        }
        return coltypename;
    }

    /**
     * 获取约束信息
     * @param connection
     * @param tablename
     * @return
     * @throws SQLException
     * 更新 2025-06-16，修复check显示不足问题
     */
    private static ArrayList<CheckInfo> getCheck(Connection connection,String tablename) throws SQLException {
        ArrayList<CheckInfo> checkInfoArrayList = null;
        String sqlstr = "SELECT con.constrname, chk.seqno as checkseqno, chk.checktext as checktext " +
                "FROM sysconstraints con, syschecks chk, systables t " +
                "WHERE con.constrid = chk.constrid " +
                "AND con.tabid = t.tabid " +
                "AND t.tabname = ? " +
                "AND con.constrtype = 'C' " +
                "AND chk.TYPE = 'T' " +
                "ORDER BY con.constrname, chk.seqno";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tablename);
        ResultSet resultSet = preparedStatement.executeQuery();
        checkInfoArrayList = resultSet2CheckInfoList(resultSet);
        resultSet.close();
        preparedStatement.close();
        return checkInfoArrayList;
    }

    /**
     * 获取check信息列表
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static ArrayList<CheckInfo> resultSet2CheckInfoList(ResultSet resultSet) throws SQLException {
        ArrayList<CheckInfo> arrayList = new ArrayList<>();
        int sizeofarrayList = 0;
        while(resultSet.next()) {
            CheckInfo checkInfo = new CheckInfo();
            checkInfo.constrName = resultSet.getString("constrname");
            sizeofarrayList = arrayList.size();
            if (sizeofarrayList > 0 && arrayList.get(sizeofarrayList - 1).constrName.equals(checkInfo.constrName)) {
                checkInfo.constrName = arrayList.get(sizeofarrayList - 1).constrName;
                checkInfo.checkText = arrayList.get(sizeofarrayList - 1).checkText + trim(resultSet.getString("checkText"));
                arrayList.set(sizeofarrayList - 1, checkInfo);
            } else {
                checkInfo.checkText = trim(resultSet.getString("checkText"));
                arrayList.add(checkInfo);
            }
        }
        // System.out.println(arrayList);
        return arrayList;
    }

    /**
     * 获取主键索引信息
     * @param connection
     * @param arrayList
     * @param tablename
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static ArrayList<PrimaryKeyInfo> getPrimarykey(Connection connection,ArrayList<ColumnsInfo> arrayList,String tablename) throws SQLException {
        ArrayList<PrimaryKeyInfo> primaryKeyArrayList = new ArrayList<>();
        String sqlstr = "SELECT con.constrname,con.constrtype,idx.indexkeys::lvarchar as idxcols " +
                "FROM sysconstraints con, sysindices idx, systables t " +
                "WHERE con.idxname = idx.idxname " +
                "AND con.tabid = t.tabid " +
                "AND t.tabname = ? " +
                "AND con.constrtype in ('P','U')";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tablename);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            PrimaryKeyInfo primaryKeyInfo = new PrimaryKeyInfo();
            primaryKeyInfo.idxCols = getIdxCols(connection, resultSet.getString("idxcols"),getColNameListByColumnsInfo(arrayList));
            primaryKeyInfo.constrName = resultSet.getString("constrname");
            primaryKeyInfo.constrType = resultSet.getString("constrtype");
            primaryKeyArrayList.add(primaryKeyInfo);
        }
        resultSet.close();
        preparedStatement.close();
        return primaryKeyArrayList;
    }

    /**
     * 从ColumnsInfo中获取字段名称列表
     * @param arrayList
     * @return
     */
    private static ArrayList<String> getColNameListByColumnsInfo(ArrayList<ColumnsInfo> arrayList){
        ArrayList<String> colnamelist = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++){
            colnamelist.add(arrayList.get(i).getColName());
        }
        return colnamelist;
    }

    /**
     * 表的所有索引列表
     * @param connection
     * @param arrayList
     * @param tablename
     * @return
     * @throws SQLException
     */
    private static ArrayList<IndexesInfo> getIndexesInfo(Connection connection,ArrayList<ColumnsInfo> arrayList,String tablename) throws SQLException {
        ArrayList<IndexesInfo> indexInfoArrayList = new ArrayList<>();
        String sqlstr = "SELECT idx.idxname, idx.owner as idxowner, idx.idxtype, idx.clustered as idxcluster, idx.indexkeys::lvarchar as idxcols " +
                "FROM sysindices idx, systables t  " +
                "WHERE idx.tabid = t.tabid  " +
                "AND t.tabname = ?  ;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tablename);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            IndexesInfo indexesInfo = new IndexesInfo();
            indexesInfo.idxName = resultSet.getString("idxname");
            indexesInfo.idxOwner = trim(resultSet.getString("idxowner"));
            indexesInfo.idxType = resultSet.getString("idxtype");
            indexesInfo.idxCluster = resultSet.getString("idxcluster");
            indexesInfo.idxCols = getIdxCols(connection, resultSet.getString("idxcols"),getColNameListByColumnsInfo(arrayList));
            indexInfoArrayList.add(indexesInfo);
        }
        resultSet.close();
        preparedStatement.close();
        return indexInfoArrayList;
    }

    /**
     * 默认值处理。
     * @param coltype
     * @param coldeftype
     * @param coldef
     * @return
     */
    private static String getDefaults(String coltype,String coldeftype, String coldef){
        String strdef = null;
        if ("E".equals(coldeftype)){
            // include function, add '(' and ')'
            if (Pattern.matches(".+\\(.*\\)", coldef)){
                strdef = "(" + coldef + ")";
            } else {
                strdef = coldef;
            }
        } else if ("L".equals(coldeftype)) {
            // INT..., DEC...,BIG...,SMALL
            if ("SMALLINT".equals(coltype) || "INTEGER".equals(coltype) || "SERIAL".equals(coltype) || "SERIAL8".equals(coltype) ||
                    "INT8".equals(coltype) || "BIGSERIAL".equals(coltype) || "BIGINT".equals(coltype) || "FLOAT".equals(coltype) ||
                    "SMALLFLOAT".equals(coltype) || "MONEY".equals(coltype) || "DECIMAL".equals(coltype)) {
                strdef = coldef;

            } else {
                strdef = "\'" + coldef.replace("'", "''") + "\'";
            }
        } else if ("N".equals(coldeftype)){
            strdef = "NULL";
        } else {
            strdef = coldef;
        }
        return strdef;
    }

    /**
     * 获取触发器列表
     * @param connection
     * @param tablename
     * @return
     * @throws SQLException
     */
    private static ArrayList<String> getTriggerList(Connection connection, String tablename) throws SQLException {
        ArrayList<String> arrayList = new ArrayList<>();
        String sqlstr = "SELECT trigname  " +
                "FROM systriggers tri, systables t " +
                "WHERE tri.tabid = t.tabid " +
                "AND t.tabname = ? ;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tablename);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            arrayList.add(resultSet.getString("trigname"));
        }
        resultSet.close();
        preparedStatement.close();
        return arrayList;
    }

    /**
     * 获取外部表定义，来源于sysexternal
     * @param connection
     * @param tablename
     * @return
     */
    private static ExtTableInfo getExtTableInfo(Connection connection, String tablename) throws SQLException {
        ExtTableInfo extTableInfo = new ExtTableInfo();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sqlstr = "SELECT t.tabname as tablename, e.fmttype as formattype, e.codeset as codeset, e.recdelim as recorddelimiter, " +
                "e.flddelim as fielddelimiter, e.datefmt as dateformat, e.moneyfmt as moneyformat, e.maxerrors as maxerrors, " +
                "e.rejectfile as rejectfile, e.flags as flags, e.ndfiles as numdfiles " +
                "FROM systables t, sysexternal e " +
                "WHERE t.tabid = e.tabid " +
                "AND t.tabname = ? ";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tablename);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            extTableInfo.FormatType = resultSet.getString("formattype");
            extTableInfo.CodeSet = resultSet.getString("codeset");
            extTableInfo.RecordDelimiter = resultSet.getString("recorddelimiter");
            extTableInfo.FieldDelimiter = resultSet.getString("fielddelimiter");
            extTableInfo.DateFormat = resultSet.getString("dateformat");
            extTableInfo.MoneyFormat = resultSet.getString("moneyformat");
            extTableInfo.MaxErrors = resultSet.getInt("maxerrors");
            extTableInfo.RejectFile = resultSet.getString("rejectfile");
            extTableInfo.Flags = resultSet.getInt("flags");
            extTableInfo.NumDfiles = resultSet.getInt("numdfiles");
        }
        extTableInfo.TableName = tablename;
        resultSet.close();
        preparedStatement.close();
        return extTableInfo;
    }

    /**
     * 返回外部表数据存储文件（多行）
     * @param connection
     * @param tablename
     * @return
     * @throws SQLException
     */
    private static ArrayList<ExtTableDfiles> getExtTableDfiles(Connection connection, String tablename) throws SQLException {
        ArrayList<ExtTableDfiles> arrayList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sqlstr = "SELECT t.tabname as tablename,e.dfentry as datafile, e.blobdir as blobdir, e.clobdir as clobdir " +
                "FROM systables t, sysextdfiles e " +
                "WHERE t.tabid = e.tabid " +
                "AND t.tabname = ? ";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tablename);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            ExtTableDfiles extTableDfiles = new ExtTableDfiles();
            extTableDfiles.TableName = resultSet.getString("tablename");
            extTableDfiles.DataFile = resultSet.getString("datafile");
            extTableDfiles.BlobDir = resultSet.getString("blobdir");
            extTableDfiles.ClobDir = resultSet.getString("clobdir");
            arrayList.add(extTableDfiles);
        }
        return arrayList;
    }

    /**
     * 获取外键信息表（可能多行）
     * @param connection
     * @param tablename
     * @return
     * @throws SQLException
     */
    private static ArrayList<ForeignKeyInfo> getForeignKeyInfo(Connection connection, String tablename) throws SQLException {
        ArrayList<ForeignKeyInfo> arrayList = new ArrayList<>();
        String sqlstr = "SELECT fk_c.constrname,fk_t.owner AS fkowner, fk_t.tabname AS fktabname, fk_i.indexkeys::lvarchar AS fk_keys, " +
                "fk_c.idxname as fkidxname, pk_t.owner AS pkowner, pk_t.tabname AS pktabname, pk_i.indexkeys::lvarchar AS pk_keys," +
                "pk_i.idxname as pkidxname " +
                "FROM sysconstraints fk_c, systables fk_t, sysindices fk_i,sysreferences fk_r, " +
                "     sysconstraints pk_c, systables pk_t, sysindices pk_i " +
                "WHERE fk_c.tabid = fk_t.tabid  " +
                "AND fk_t.tabname = ? " +
                "AND fk_c.constrtype = 'R' " +
                "AND fk_c.idxname = fk_i.idxname " +
                "AND fk_c.constrid = fk_r.constrid " +
                "AND fk_r.PRIMARY = pk_c.constrid " +
                "AND pk_c.tabid = pk_t.tabid " +
                "AND pk_c.idxname = pk_i.idxname;";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tablename);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            ForeignKeyInfo foreignKeyInfo = new ForeignKeyInfo();
            foreignKeyInfo.FKName = resultSet.getString("constrname");
            foreignKeyInfo.FKOwner = resultSet.getString("fkowner");
            foreignKeyInfo.FKTabname = resultSet.getString("fktabname");
            foreignKeyInfo.FKCols = resultSet.getString("fk_keys");
            foreignKeyInfo.FKIdxName = resultSet.getString("fkidxname");
            foreignKeyInfo.PKOwner = resultSet.getString("pkowner");
            foreignKeyInfo.PKTabname = resultSet.getString("pktabname");
            foreignKeyInfo.PKCols = resultSet.getString("pk_keys");
            foreignKeyInfo.PKIdxName = resultSet.getString("pkidxname");
            arrayList.add(foreignKeyInfo);
        }
        resultSet.close();
        preparedStatement.close();
        return arrayList;
    }

    /**
     * 生成ddl语句，能调用printview, printsequenc,printsynonym等可从systables读取的信息。
     * @param connection
     * @param tablename
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static String printTable(Connection connection,String tablename) throws SQLException, ClassNotFoundException {
        String parttern_constraint = "^[cur]\\d+_\\d+";              // u=unique,r=reference,c=check
        TableInfo tableInfo = getTableInfo(connection,tablename);
        String sqlmode = tableInfo.getTableSqlMode();
        String ddl = "SET ENVIRONMENT SQLMODE '" + sqlmode + "';\n";
        // 视图，序列、同义词暂时只打印自己的信息
        if ("V".equalsIgnoreCase(tableInfo.TableType)){
            return printView(connection,tablename);
        } else if("Q".equalsIgnoreCase(tableInfo.TableType)) {
            return printSequence(connection,tablename);
        } else if("P".equalsIgnoreCase(tableInfo.TableType) || "S".equalsIgnoreCase(tableInfo.TableType)) {
            return printSynonym(connection, tablename);
        }

        ddl = ddl + "CREATE ";
        // global temporary
        ddl = ddl + tableInfo.getTableGlobalTemporary() + " ";
        // external
        if("E".equals(tableInfo.TableType)){
            ddl = ddl + "EXTERNAL TABLE ";
        } else {
            ddl = ddl + "TABLE ";
        }
        // OWNER
        if (tableInfo.TableOwner==null){
            return "";
        }
        ddl = ddl + "\"" + tableInfo.TableOwner + "\".";

        ddl = ddl + getName(tableInfo.TableName) + " (\n";

        ArrayList<CheckInfo> checkInfoArrayList = getCheck(connection,tablename);
        ArrayList<ColumnsInfo> columnsInfoArrayList = getColInfo(connection,tableInfo);
        ArrayList<PrimaryKeyInfo> primaryKeyInfoArrayList = getPrimarykey(connection,columnsInfoArrayList,tablename);
        ArrayList<IndexesInfo> indexesInfoArrayList = getIndexesInfo(connection,columnsInfoArrayList,tablename);
        ArrayList<FragmentInfo> tableFragmentInfoArrayList = getTableFragmentInfo(connection,tablename);
        ArrayList<String> triggerList = getTriggerList(connection,tablename);
        ArrayList<ForeignKeyInfo> foreignKeyInfoArrayList = getForeignKeyInfo(connection,tablename);

        // 按顺序处理各个类型
        for (int i=0;i<columnsInfoArrayList.size();i++){
            // 字段名
            ddl = ddl + "  " + getName(columnsInfoArrayList.get(i).getColName());
            // 字段类型
            ddl = ddl + " " + getColTypeName(columnsInfoArrayList.get(i).getColType(),columnsInfoArrayList.get(i).getColLength(),columnsInfoArrayList.get(i).getTypeP(),columnsInfoArrayList.get(i).getTypeS());
            // 是否为 NOT NULL
            if (! columnsInfoArrayList.get(i).isIsNullable()){
                ddl = ddl + " NOT NULL";
            }
            // 默认值，依据ColDefType处理
            if (columnsInfoArrayList.get(i).getColDefType() != null){
                ddl = ddl + " DEFAULT " + getDefaults(columnsInfoArrayList.get(i).getColType(),columnsInfoArrayList.get(i).getColDefType(),columnsInfoArrayList.get(i).getColDef());
            }
            // 非最后一个字段，后面加上 ,\n
            if (i<columnsInfoArrayList.size()-1){
                ddl = ddl + ",\n";
            }
        }
        // 检查约束
        if (checkInfoArrayList.size() > 0){
            for(int i=0;i<checkInfoArrayList.size();i++){
                // Oracle模式下，constraint在前
                if ("Oracle".equalsIgnoreCase(sqlmode)){
                    if (!Pattern.matches(parttern_constraint, checkInfoArrayList.get(i).constrName)) {
                        ddl = ddl + ",\n  CONSTRAINT " + getName(checkInfoArrayList.get(i).constrName);
                        ddl = ddl + "  CHECK " + checkInfoArrayList.get(i).checkText;
                    } else {
                        ddl = ddl + ",\n  CHECK " + checkInfoArrayList.get(i).checkText;
                    }
                    // default GBase模式
                } else {
                    ddl = ddl + ",\n  CHECK " + checkInfoArrayList.get(i).checkText;
                    if (!Pattern.matches(parttern_constraint, checkInfoArrayList.get(i).constrName)) {
                        ddl = ddl + " CONSTRAINT " + getName(checkInfoArrayList.get(i).constrName);
                    }
                }
            }
        }
        // 主键约束及唯一约束
        if (primaryKeyInfoArrayList.size() > 0){
            for(int i=0;i<primaryKeyInfoArrayList.size();i++){
                // Oracle模式
                if ("Oracle".equalsIgnoreCase(sqlmode)){
                    if (!Pattern.matches(parttern_constraint, primaryKeyInfoArrayList.get(i).constrName)) {
                        ddl = ddl + ",\n  CONSTRAINT " + getName(primaryKeyInfoArrayList.get(i).constrName);
                        if ("P".equals(primaryKeyInfoArrayList.get(i).constrType)) {
                            ddl = ddl + "  PRIMARY KEY(";
                        } else if ("U".equals(primaryKeyInfoArrayList.get(i).constrType)) {
                            ddl = ddl + "  UNIQUE(";
                        }
                        ddl = ddl + primaryKeyInfoArrayList.get(i).idxCols + ")";
                    } else {
                        if ("P".equals(primaryKeyInfoArrayList.get(i).constrType)) {
                            ddl = ddl + ",\n  PRIMARY KEY(";
                        } else if ("U".equals(primaryKeyInfoArrayList.get(i).constrType)) {
                            ddl = ddl + ",\n  UNIQUE(";
                        }
                        ddl = ddl + primaryKeyInfoArrayList.get(i).idxCols + ")";
                    }
                    // default GBase模式
                } else {
                    if ("P".equals(primaryKeyInfoArrayList.get(i).constrType)) {
                        ddl = ddl + ",\n  PRIMARY KEY(";
                    } else if ("U".equals(primaryKeyInfoArrayList.get(i).constrType)) {
                        ddl = ddl + ",\n  UNIQUE(";
                    }
                    ddl = ddl + primaryKeyInfoArrayList.get(i).idxCols + ")";
                    if (!Pattern.matches(parttern_constraint, primaryKeyInfoArrayList.get(i).constrName)) {
                        ddl = ddl + "  CONSTRAINT " + getName(primaryKeyInfoArrayList.get(i).constrName);
                    }
                }
            }
        }
        ddl = ddl + "\n) ";

        // E 外部表处理，不考虑maxrows，TODO：没处理外部数据类型对应
        if ("E".equals(tableInfo.TableType)){
            ExtTableInfo extTableInfo = getExtTableInfo(connection,tableInfo.TableName);
            ArrayList<ExtTableDfiles> extTableDfilesArrayList = getExtTableDfiles(connection,tableInfo.TableName);
            if (extTableInfo==null){
                return ddl + ";";
            }
            ddl = ddl + "\nUSING ( \n  DATAFILES(\n";
            // DATAFILE
            for(int i=0;i<extTableInfo.NumDfiles;i++){
                ddl = ddl + "    '" + trim(extTableDfilesArrayList.get(i).DataFile);
                if (extTableDfilesArrayList.get(i).BlobDir != null && ! "".equals(trim(extTableDfilesArrayList.get(i).BlobDir))){
                    ddl = ddl + ";BLOBDIR:" + trim(extTableDfilesArrayList.get(i).BlobDir);
                }
                if (extTableDfilesArrayList.get(i).ClobDir != null && ! "".equals(trim(extTableDfilesArrayList.get(i).ClobDir))){
                    ddl = ddl + ";CLOBDIR:" + trim(extTableDfilesArrayList.get(i).ClobDir);
                }
                // 是否最后一行
                if(i==(extTableInfo.NumDfiles-1)){
                    ddl = ddl + "'\n  ),\n";
                } else {
                    ddl = ddl + "',\n";
                }
            }

            // FORMAT
            if ("D".equals(extTableInfo.FormatType)){
                ddl = ddl + "  FORMAT 'DELIMITED',\n";
            } else if ("F".equals(extTableInfo.FormatType)){
                ddl = ddl + "  FORMAT 'FIXED',\n";
            } else if ("I".equals(extTableInfo.FormatType)){
                ddl = ddl + "  FORMAT 'GBASEDBT',\n";           // gbasedbt or informix
            }
            // DELIMITER
            if (extTableInfo.FieldDelimiter != null){
                ddl = ddl + "  DELIMITER '" + extTableInfo.FieldDelimiter + "',\n";
            }
            // RECORDEND
            if (extTableInfo.RecordDelimiter != null){
                ddl = ddl + "  RECORDEND '" + extTableInfo.RecordDelimiter + "',\n";
            }
            // DBDATE
            if (extTableInfo.DateFormat != null){
                ddl = ddl + "  DBDATE '" + trim(extTableInfo.DateFormat) + "',\n";
            }
            // DBMONEY
            if (extTableInfo.MoneyFormat != null){
                ddl = ddl + "  DBMONEY '" + trim(extTableInfo.MoneyFormat) + "',\n";
            }
            // MAXERRORS
            if (extTableInfo.MaxErrors != -1 ){
                ddl = ddl + "  MAXERRORS " + extTableInfo.MaxErrors + ",\n";
            }
            // REJECTFILE
            if (extTableInfo.RejectFile != null){
                ddl = ddl + "  REJECTFILE '" + trim(extTableInfo.RejectFile) + "',\n";
            }
            // flags
            if ((extTableInfo.Flags & 4) == 4){
                ddl = ddl + "  DELUXE,\n";
            } else if((extTableInfo.Flags & 8) == 8){
                ddl = ddl + "  EXPRESS,\n";
            }
            if ((extTableInfo.Flags & 2) == 2){
                ddl = ddl + "  ESCAPE ON\n);";
            } else {
                ddl = ddl + "  ESCAPE OFF\n);";
            }


        } else {        // T 普通表
            // 分片规则，Exprtext 的结果可能不对。
            if (tableFragmentInfoArrayList.size() > 0) {
                ddl = ddl + buildFragmentString(tableFragmentInfoArrayList);
            }

            // 全局临时表级别
            if ("".equals(tableInfo.getTableGlobalTemporary())){
                ddl = ddl + "\n";
            } else {
                ddl = ddl + "\n" + tableInfo.getTableGlobalTemporaryLevel() + " ";
            }
            // 区段大小及锁模式
            ddl = ddl + "EXTENT SIZE " + tableInfo.firstExtSize + " NEXT SIZE " + tableInfo.nextExtSize;
            ddl = ddl + " LOCK MODE " + tableInfo.getLockType() + ";\n";

            // 索引，去除约束创建的索引
            if (indexesInfoArrayList.size() > 0) {
                for (int i = 0; i < indexesInfoArrayList.size(); i++) {
                    if (indexesInfoArrayList.get(i).idxName.startsWith(" ")) {
                        continue;
                    }
                    ddl = ddl + "\nCREATE";
                    if ("U".equals(indexesInfoArrayList.get(i).idxType)) {
                        ddl = ddl + " UNIQUE INDEX";
                    } else if ("C".equals(indexesInfoArrayList.get(i).idxCluster)) {
                        ddl = ddl + " CLUSTER INDEX";
                    } else {
                        ddl = ddl + " INDEX";
                    }
                    ddl = ddl + " \"" + indexesInfoArrayList.get(i).idxOwner + "\"." + getName(indexesInfoArrayList.get(i).idxName) + " ON";
                    ddl = ddl + " \"" + tableInfo.TableOwner + "\"." + getName(tableInfo.TableName) + "(" + indexesInfoArrayList.get(i).idxCols + ")";
                    ddl = ddl + buildFragmentString(getIndexFragmentInfo(connection, indexesInfoArrayList.get(i).idxName)) + ";";
                }
            }
            ddl = ddl + "\n";

            // 外键
            if (foreignKeyInfoArrayList.size() > 0){
                for (int i = 0; i< foreignKeyInfoArrayList.size(); i++){
                    ddl = ddl + "\nALTER TABLE \"" + trim(foreignKeyInfoArrayList.get(i).FKOwner) + "\"." + getName(foreignKeyInfoArrayList.get(i).FKTabname);
                    // Oracle模式下 constraint在前
                    if ("Oracle".equalsIgnoreCase(sqlmode)){
                        ddl = ddl + " ADD CONSTRAINT ";
                        if (! Pattern.matches(parttern_constraint,foreignKeyInfoArrayList.get(i).FKName)){
                            ddl = ddl + getName(foreignKeyInfoArrayList.get(i).FKName);
                        }
                        ddl = ddl + "FOREIGN KEY(";
                    } else {
                        ddl = ddl + " ADD CONSTRAINT (FOREIGN KEY(";
                    }

                    // 外键字段
                    ArrayList<String> fkarrayList = getColNameListByColumnsInfo(columnsInfoArrayList);
                    ddl = ddl + getIdxCols(foreignKeyInfoArrayList.get(i).FKCols,fkarrayList) + ") ";

                    // 对应的主键or唯一键字段
                    ArrayList<String> pkarrayList = getColnameListByTablename(connection,foreignKeyInfoArrayList.get(i).PKTabname);
                    ddl = ddl + "REFERENCES \"" + trim(foreignKeyInfoArrayList.get(i).PKOwner) + "\"." + getName(foreignKeyInfoArrayList.get(i).PKTabname);
                    ddl = ddl + "(" + getIdxCols(foreignKeyInfoArrayList.get(i).PKCols,pkarrayList) + "))";

                    // GBase模式下 constraint在后
                    if ("GBase".equalsIgnoreCase(sqlmode)) {
                        if (!Pattern.matches(parttern_constraint, foreignKeyInfoArrayList.get(i).FKName)) {
                            ddl = ddl + " CONSTRAINT " + getName(foreignKeyInfoArrayList.get(i).FKName);
                        }
                    }
                    ddl = ddl + ";\n";
                }
            }


            // 表注释
            if (tableInfo.TableComm != null) {
                ddl = ddl + "\nCOMMENT ON TABLE \"" + tableInfo.TableOwner + "\"." + getName(tablename) + " IS '" +
                        tableInfo.TableComm.replace("'", "''") + "';";
            }
            // 字段注释
            for (int i = 0; i < columnsInfoArrayList.size(); i++) {
                if (columnsInfoArrayList.get(i).getColComm() != null) {
                    ddl = ddl + "\nCOMMENT ON COLUMN \"" + tableInfo.TableOwner + "\"." + getName(tablename) + "." +
                            getName(columnsInfoArrayList.get(i).getColName()) + " IS '" +
                            columnsInfoArrayList.get(i).getColComm().replace("'", "''") + "';";
                }
            }

            ddl = ddl + "\n";
            // 触发器
            for (int i = 0; i < triggerList.size(); i++) {
                ddl = ddl + "\n" + printTrigger(connection, triggerList.get(i));
            }
        }

        return ddl;
    }

    /**
     * 内部函数编号
     */
    final static HashMap<Integer,String> getInnerFuncName;
    static {
        getInnerFuncName = new HashMap<>();
        getInnerFuncName.put(-1010,"abs");
        getInnerFuncName.put(-1015,"mod");
        getInnerFuncName.put(-173,"substr");
        getInnerFuncName.put(-201,"ascii");
        getInnerFuncName.put(-205,"to_number");
        getInnerFuncName.put(-212,"ceil");
        getInnerFuncName.put(-213,"floor");
        getInnerFuncName.put(-224,"degrees");
        getInnerFuncName.put(-225,"radians");
        getInnerFuncName.put(-227,"instr");
        getInnerFuncName.put(-231,"reverse");
        getInnerFuncName.put(-233,"len");
        getInnerFuncName.put(-234,"substrb");
        getInnerFuncName.put(-45,"length");
        getInnerFuncName.put(-46,"octet_length");
        getInnerFuncName.put(-47,"char_length");
        getInnerFuncName.put(-48,"upper");
        getInnerFuncName.put(-49,"lower");
        getInnerFuncName.put(-50,"initcap");
    }

    /**
     * 索引分片信息
     * @param connection
     * @param indexname
     * @return
     * @throws SQLException
     */
    private static ArrayList<FragmentInfo> getIndexFragmentInfo(Connection connection, String indexname) throws SQLException {
        ArrayList<FragmentInfo> arrayList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sqlstr = "SELECT frag.colno, frag.strategy, frag.evalpos, frag.exprtext, frag.flags, frag.dbspace, frag.partition " +
                "FROM sysfragments frag " +
                "WHERE frag.indexname = ? " +
                "AND frag.fragtype = 'I' " +
                "ORDER BY frag.evalpos";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,indexname);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            FragmentInfo indexFragmentInfo = new FragmentInfo();
            indexFragmentInfo.FragName = indexname;
            indexFragmentInfo.ColNo = resultSet.getInt("colno");
            indexFragmentInfo.Strategy = resultSet.getString("strategy");
            indexFragmentInfo.Evalpos = resultSet.getInt("evalpos");
            indexFragmentInfo.Exprtext = resultSet.getString("exprtext");
            indexFragmentInfo.Flags = resultSet.getInt("flags");
            indexFragmentInfo.Dbspace = resultSet.getString("dbspace");
            indexFragmentInfo.Partition = resultSet.getString("partition");
            arrayList.add(indexFragmentInfo);
        }
        resultSet.close();
        preparedStatement.close();
        return arrayList;
    }

    /**
     * 生成分片信息
     * @param arrayList
     * @return
     */
    private static String buildFragmentString(ArrayList<FragmentInfo> arrayList) {
        String ddl = " \nFRAGMENT BY ";
        String fragtype = "";
        String fragcolumn = "";         // for List and range, column
        String fraginterval = "";       // for range, interval
        String fragdbslist = "";        // for range, dbspace list or function name;
        String fragdetech = "";         // for range, detech or discard
        int numfragments = 0;           // for range, num

        if (arrayList.size() == 0) {
            return "";
        }
        /**
         * 分片类型是I,表示按表分片规则
         * 分片类型是R(round robin)和E(expression)时，evalpos从0开始；
         * 分片类型是L(List)时，evalpos有-3，exprtext用于定义list字段
         * 分片类型是N(raNge-iNterval (or rolliNg wiNdow))时，需要考虑的就比较多了：
         *     evalpos如果有-4时，colno表示rolling的分片数量，flags值为16表示detach, 8表示discard
         *     evalpos为-3时,exprtext用于定义range字段。  -- 同list
         *     evalpos为-2时,exprtext用于定义interval时长
         *     evalpos为-1时,flags值为4096时：exprtext表示空间；为36874时：exprtext表示使用的函数名称； colno表示有几个空间或者1个函数。
         * evalpos :
         * -1 = 时间间隔分段的数据库空间列表
         * -2 = 时间间隔值
         * -3 = 分段存储键
         * -4 = 滚动窗口分段
         * 按 LIST 的分段存储也使用值 -3。
         */
        for (int i = 0; i < arrayList.size(); i++) {
            if (i == 0) {
                fragtype = arrayList.get(0).Strategy;
            }
            if (arrayList.get(i).Evalpos == 0) {
                break;
            }
            // rolling 窗口数量，及 rolling detach / discard
            if (arrayList.get(i).Evalpos == -4) {
                numfragments = arrayList.get(i).ColNo;
                if (arrayList.get(i).Flags == 16) {
                    fragdetech = " DETEAH";
                } else if (arrayList.get(i).Flags == 8) {
                    fragdetech = " DISCARD";
                }
                // list or range 字段，可能是函数处理后的
            } else if (arrayList.get(i).Evalpos == -3) {
                fragcolumn = arrayList.get(i).Exprtext;
            } else if (arrayList.get(i).Evalpos == -2) {
                fraginterval = arrayList.get(i).Exprtext;
            } else if (arrayList.get(i).Evalpos == -1) {
                // switch ?
                if (arrayList.get(i).Flags == 4096) {
                    fragdbslist = arrayList.get(i).Exprtext;        // dbspaces list
                } else if (arrayList.get(i).Flags == 36864) {
                    fragdbslist = arrayList.get(i).Exprtext + "()"; // function
                }
            }
        }
        if ("I".equals(fragtype)) {                     // in dbspace
            ddl = " IN " + arrayList.get(0).Dbspace;
        } else if ("T".equals(fragtype)){               // 索引使用表的分片表达式
            ddl = "";
        } else if ("R".equals(fragtype)){
            ddl = ddl + "ROUND ROBIN \n";
            for(int i=0;i<arrayList.size();i++){
                ddl = ddl + "PARTITION " + arrayList.get(i).Partition + " IN " + arrayList.get(i).Dbspace;
                if (i<arrayList.size()-1){
                    ddl = ddl + ",\n";
                }
            }
        } else if ("E".equals(fragtype)){
            ddl = ddl + "EXPRESSION \n";
            for(int i=0;i<arrayList.size();i++){
                ddl = ddl + "PARTITION " + arrayList.get(i).Partition + " " + arrayList.get(i).Exprtext + " IN " + arrayList.get(i).Dbspace;
                if (i<arrayList.size()-1){
                    ddl = ddl + ",\n";
                }
            }
        } else if ("L".equals(fragtype)){
            ddl = ddl + "LIST(" + fragcolumn + ") \n";
            for(int i=0;i<arrayList.size();i++){
                if (arrayList.get(i).Evalpos < 0){
                    continue;
                }
                ddl = ddl + "PARTITION " + arrayList.get(i).Partition + " " + arrayList.get(i).Exprtext + " IN " + arrayList.get(i).Dbspace;
                if (i<arrayList.size()-1){
                    ddl = ddl + ",\n";
                }
            }
        } else if ("N".equals(fragtype)){
            ddl = ddl + "RANGE(" + fragcolumn + ") INTERVAL(" + fraginterval + ") \n";
            if (numfragments > 0){
                ddl = ddl + "ROLLING (" + numfragments + " FRAGMENTS) " + fragdetech + " \n";
            }
            ddl = ddl + "STORE IN (" + fragdbslist + ") \n";
            for(int i=0;i<arrayList.size();i++){
                if (arrayList.get(i).Evalpos < 0){
                    continue;
                }
                ddl = ddl + "PARTITION " + arrayList.get(i).Partition + " " + arrayList.get(i).Exprtext + " IN " + arrayList.get(i).Dbspace;
                if (i<arrayList.size()-1){
                    ddl = ddl + ",\n";
                }
            }
        }
        return ddl;
    }

    /**
     * 索引列表转字段信息
     * @param idxColsString
     * @param arrayList
     * @return
     */
    private static String getIdxCols(Connection connection,String idxColsString,ArrayList<String> colnameList) throws SQLException {
        // idxColsString: <-234>(1, '2') [1], -3 [1]
        // 索引字段（函数索引字段）以，为分隔符，<-234>(1, '2', '4') [1]表示 函数号-234（内置函数），对应的字段是1，-表示desc排序，'2','4'用于多值参数的函数，[1] 是读取方式（默认该值）；-3表示第三个字段desc排序。
        String idxcols = "";
        String funcname = "";
        String funcparam = "";
        String sortby = ",";
        for (String cols : idxColsString.trim().split(",(?![^()]*+\\))")){      // 以逗号分割，但是不包含括号内的逗号的正则表达式
            String tmpstr = cols.trim();
            if (tmpstr.startsWith("<")){            // function index, 以"<"开头表示函数索引
                // 通过函数序号获取函数名
                int funcnum = Integer.valueOf(tmpstr.substring(1,tmpstr.indexOf(">")));
                if (funcnum < 0) {      // 内部函数
                    funcname = getInnerFuncName.get(funcnum);
                } else {                // 自定义函数
                    String sqlstr = "select procname from sysprocedures where procid = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(sqlstr);
                    preparedStatement.setInt(1,funcnum);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while(resultSet.next()){
                        funcname = resultSet.getString("procname");
                    }
                    if (resultSet != null){
                        resultSet.close();
                    }
                    if (preparedStatement != null){
                        preparedStatement.close();
                    }
                }
                idxcols = idxcols + funcname + "(";
                // 获取字段 及 排序方式 及多参函数参数
                String tmpstr_func = tmpstr.substring(tmpstr.indexOf("(")+1,tmpstr.indexOf(")"));
                int colno = 0;
                if (tmpstr_func.indexOf(",") > 0){  // 多值参数，有其它参数
                    colno = Integer.valueOf(tmpstr_func.substring(0,tmpstr_func.indexOf(",")));
                    funcparam = tmpstr_func.substring(tmpstr_func.indexOf(",")+1).trim();
                } else {                            // 单值
                    colno = Integer.valueOf(tmpstr_func);
                }
                if (colno < 0){
                    sortby = " DESC,";
                }
                idxcols = idxcols + getName(colnameList.get(Math.abs(colno) - 1));
                if ("".equals(funcparam)){
                    idxcols = idxcols + ")";
                } else {
                    idxcols = idxcols + "," + funcparam + ")";
                }
                idxcols = idxcols + sortby;
            } else {                                // 普通字段 -3 [1]
                int colno = Integer.valueOf(tmpstr.substring(0,tmpstr.indexOf(" ")));
                if (colno < 0){
                    sortby = " DESC,";
                }
                idxcols = idxcols + getName(colnameList.get(Math.abs(colno) - 1)) + sortby;
            }
        }
        if (idxcols.length() > 0){
            idxcols = idxcols.substring(0,idxcols.length()-1);
        }
        return idxcols;
    }

    /**
     * 返回索引字段信息，不包含函数索引
     * @param idxColsString
     * @param colnameList
     * @return
     */
    private static String getIdxCols(String idxColsString,ArrayList<String> colnameList) {
        String idxcols = "";
        String sortby = ",";
        for (String cols : idxColsString.trim().split(",(?![^()]*+\\))")){
            String tmpstr = cols.trim();
            // 普通字段 -3 [1]
            int colno = Integer.valueOf(tmpstr.substring(0,tmpstr.indexOf(" ")));
            if (colno < 0){
                sortby = " DESC,";
            }
            idxcols = idxcols + getName(colnameList.get(Math.abs(colno) - 1)) + sortby;
        }
        if (idxcols.length() > 0){
            idxcols = idxcols.substring(0,idxcols.length()-1);
        }
        return idxcols;
    }

    /**
     * 获取指定索引所需的信息
     * @param connection
     * @param indexname
     * @return
     * @throws SQLException
     */
    private static IndexInfo getIndexInfo(Connection connection, String indexname, ArrayList<String> colnameList) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sqlstr = null;
        IndexInfo indexInfo = new IndexInfo();
        // 索引
        sqlstr = "SELECT i.owner idxowner,i.idxname,t.owner as tabowner, t.tabname,i.idxtype,i.clustered as idxcluster,i.indexkeys::lvarchar as indexkeys " +
                "FROM sysindices i,systables t  " +
                "WHERE i.idxname = ? " +
                "AND i.tabid = t.tabid";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,indexname);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            indexInfo.IndexName = resultSet.getString("idxname");
            indexInfo.IndexOwner = resultSet.getString("idxowner");
            indexInfo.TableName = resultSet.getString("tabname");
            indexInfo.TableOwner = resultSet.getString("tabowner");
            indexInfo.IndexType = resultSet.getString("idxtype");
            indexInfo.IndexCluster = resultSet.getString("idxcluster");
            indexInfo.IndexCols = resultSet.getString("indexkeys");
        }

        if (indexInfo == null || indexInfo.IndexName == null){
            return null;
        }
        indexInfo.IndexCols = getIdxCols(connection,indexInfo.IndexCols,colnameList);
        resultSet.close();
        preparedStatement.close();
        return indexInfo;
    }

    /**
     * 通过索引名获取表字段列表
     * @param connection
     * @param indexname
     * @return
     * @throws SQLException
     */
    private static ArrayList<String> getColnameListByIndexname(Connection connection, String indexname) throws SQLException {
        // 所在表的所有字段
        ArrayList<String> colnameList = new ArrayList<String>();
        String sqlstr = "SELECT col.colname " +
                "FROM syscolumns col, sysindexes idx " +
                "WHERE col.tabid = idx.tabid " +
                "AND idx.idxname = ? " +
                "ORDER BY col.colno ";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,indexname);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            colnameList.add(resultSet.getString("colname"));
        }
        resultSet.close();
        preparedStatement.close();
        return colnameList;
    }

    /**
     * 通过表名获取表字段列表
     * @param connection
     * @param tablename
     * @return
     * @throws SQLException
     */
    private static ArrayList<String> getColnameListByTablename(Connection connection, String tablename) throws SQLException {
        // 所在表的所有字段
        ArrayList<String> colnameList = new ArrayList<String>();
        String sqlstr = "SELECT col.colname " +
                "FROM syscolumns col, systables t " +
                "WHERE col.tabid = t.tabid " +
                "AND t.tabname = ? " +
                "ORDER BY col.colno ";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,tablename);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            colnameList.add(resultSet.getString("colname"));
        }
        resultSet.close();
        preparedStatement.close();
        return colnameList;
    }

    /**
     * 打印索引语句
     * @param connection
     * @param indexname
     * @return
     * @throws SQLException
     */
    public static String printIndex(Connection connection, String indexname) throws SQLException {
        ArrayList<String> colnamelist = getColnameListByIndexname(connection,indexname);
        IndexInfo indexInfo = getIndexInfo(connection,indexname,colnamelist);
        if (indexInfo == null || indexInfo.IndexName == null){
            return "";
        }
        String ddl = "CREATE";
        // 索引类型
        if("U".equals(indexInfo.IndexType)) {
            ddl = ddl + " UNIQUE INDEX";
        } else if("C".equals(indexInfo.IndexCluster)){
            ddl = ddl + " CLUSTER INDEX";
        } else {
            ddl = ddl + " INDEX";
        }
        // 索引名称  属主.索引名
        ddl = ddl + " \"" + indexInfo.IndexOwner.trim()+ "\"." + getName(indexInfo.IndexName) + " ON";
        // 表名(索引字段（函数索引字段）列表)
        ddl = ddl + " \"" + indexInfo.TableOwner.trim() + "\"." + getName(indexInfo.TableName) + "(" + indexInfo.IndexCols + ")";
        // 索引分片规则或者存储
        ddl = ddl + buildFragmentString(getIndexFragmentInfo(connection,indexname)) + ";";
        return ddl;
    }

    /**
     * 结果集转触发器ddl
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static TriggerInfo resultSet2TriggerInfo(ResultSet resultSet) throws SQLException {
        TriggerInfo triggerInfo = new TriggerInfo();
        String triggerbody = "";
        while(resultSet.next()){
            triggerInfo.TriggerName = resultSet.getString("trigname");
            triggerbody = triggerbody + rtrimascii0(resultSet.getString("trigbody"));
        }
        triggerInfo.TriggerBody = triggerbody;
        return triggerInfo;
    }

    /**
     * 获取触发器信息
     * @param connection
     * @param triggername
     * @return
     * @throws SQLException
     */
    private static TriggerInfo getTriggerInfo(Connection connection, String triggername) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sqlstr = null;
        TriggerInfo triggerInfo = null;
        sqlstr = "SELECT tri.trigname,bdy.data as trigbody  " +
                "FROM systriggers tri, systrigbody bdy " +
                "WHERE tri.trigid = bdy.trigid " +
                "AND tri.trigname = ? " +
                "AND bdy.datakey IN ('A','D')" +
                "ORDER BY bdy.datakey DESC,bdy.seqno ASC;";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,triggername);
        resultSet = preparedStatement.executeQuery();
        triggerInfo = resultSet2TriggerInfo(resultSet);
        resultSet.close();
        preparedStatement.close();
        return triggerInfo;
    }

    /**
     * 打印触发器
     * @param connection
     * @param triggername
     * @return
     * @throws SQLException
     */
    public static String printTrigger(Connection connection, String triggername) throws SQLException {
        TriggerInfo triggerInfo = getTriggerInfo(connection,triggername);
        if (triggerInfo == null || triggerInfo.TriggerName == null){
            return "";
        }
        return triggerInfo.TriggerBody;
    }

    /**
     * 获取序列信息
     * @param connection
     * @param sequencename
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static SequenceInfo getSequenceInfo(Connection connection,String sequencename) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sqlstr = null;
        SequenceInfo sequenceInfo = new SequenceInfo();
        sqlstr = "SELECT t.owner AS seqowner,t.tabname AS seqname,seq.start_val AS startval, " +
                "  seq.inc_val AS incval,seq.max_val AS maxval,seq.min_val AS minval, " +
                "  seq.cycle AS iscycle, seq.cache AS cache,seq.order AS isorder, t.flags  " +
                "FROM systables t, syssequences seq " +
                "WHERE t.tabid = seq.tabid " +
                "AND t.tabname = ? " +
                "AND t.tabtype = 'Q'";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,sequencename);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            sequenceInfo.SeqOwner = resultSet.getString("seqowner");
            sequenceInfo.SeqName = resultSet.getString("seqname");
            sequenceInfo.StartVal = resultSet.getLong("startval");
            sequenceInfo.IncVal = resultSet.getLong("incval");
            sequenceInfo.MaxVal = resultSet.getLong("maxval");
            sequenceInfo.MinVal = resultSet.getLong("minval");
            sequenceInfo.IsCycle = resultSet.getString("iscycle");
            sequenceInfo.cache = resultSet.getLong("cache");
            sequenceInfo.IsOrder = resultSet.getString("isorder");
        }
        resultSet.close();
        preparedStatement.close();
        return sequenceInfo;
    }

    /**
     * 打印序列
     * @param connection
     * @param synonymname
     * @param delimident
     * @return
     * @throws SQLException
     */
    public static String printSequence(Connection connection, String sequencename) throws SQLException {
        SequenceInfo sequenceInfo = getSequenceInfo(connection,sequencename);
        String ddl = "SET ENVIRONMENT SQLMODE '" + sequenceInfo.getSequenceSqlMode() + "';\nCREATE SEQUENCE ";
        if (sequenceInfo == null || sequenceInfo.SeqName == null){
            return "";
        }
        // owner
        ddl = ddl + "\"" + sequenceInfo.SeqOwner.trim() + "\".";
        // seqname
        ddl = ddl + getName(sequenceInfo.SeqName);
        // start with
        if(sequenceInfo.StartVal > 0){
            ddl = ddl + " START WITH " + sequenceInfo.StartVal;
        }
        // increment by
        ddl = ddl + " INCREMENT BY " + sequenceInfo.IncVal;
        // maxvalue
        ddl = ddl + " MAXVALUE " + sequenceInfo.MaxVal;
        // minvalue
        ddl = ddl + " MINVALUE " + sequenceInfo.MinVal;
        // cycle
        if ("1".equals(sequenceInfo.IsCycle)){
            ddl = ddl + " CYCLE";
        }
        // cache
        if (sequenceInfo.cache > 0){
            ddl = ddl + " CACHE " + sequenceInfo.cache;
        } else {
            ddl = ddl + " NOCACHE";
        }
        // order
        if ("1".equals(sequenceInfo.IsOrder)){
            ddl = ddl + " ORDER";
        } else {
            ddl = ddl + " NOORDER";
        }

        return ddl + ";";
    }

    /**
     * 获取同义词信息
     * @param connection
     * @param synonymname
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static SynonymInfo getSynonymInfo(Connection connection,String synonymname) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        SynonymInfo synonymInfo = new SynonymInfo();
        String sqlstr = null;
        sqlstr = "select t1.tabtype AS syntype,t1.owner AS synowner,t1.tabname AS synname, " +
                "    s.servername AS rservername,s.dbname AS rdbname,s.owner AS rowner,s.tabname AS rtabname, " +
                "    syn.owner AS lowner,syn.tabname AS ltabname, t1.flags " +
                "from syssyntable s  " +
                "LEFT JOIN systables syn ON s.btabid = syn.tabid, systables t1 " +
                "WHERE s.tabid = t1.tabid " +
                "AND t1.tabname = ?";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,synonymname);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            synonymInfo.SynType = resultSet.getString("syntype");
            synonymInfo.SynOwner = resultSet.getString("synowner");
            synonymInfo.SynName = resultSet.getString("synname");
            synonymInfo.RServerName = resultSet.getString("rservername");
            synonymInfo.RDbName = resultSet.getString("rdbname");
            synonymInfo.ROwner = resultSet.getString("rowner");
            synonymInfo.RTabName = resultSet.getString("rtabname");
            synonymInfo.LOwner = resultSet.getString("lowner");
            synonymInfo.LTabName = resultSet.getString("ltabname");
            synonymInfo.Flags = resultSet.getInt("flags");
        }
        resultSet.close();
        preparedStatement.close();
        return synonymInfo;
    }

    /**
     * 打印同义词
     * @param connection
     * @param synonymname
     * @param delimident
     * @return
     * @throws SQLException
     */
    public static String printSynonym(Connection connection, String synonymname) throws SQLException {
        SynonymInfo synonymInfo = getSynonymInfo(connection,synonymname);
        String ddl = "SET ENVIRONMENT SQLMODE '" + synonymInfo.getSynonymSqlMode() + "';\nCREATE ";
        if (synonymInfo == null || synonymInfo.SynName == null){
            return "";
        }
        // private or public
        if ("P".equals(synonymInfo.SynType)){   // S = Public synonym, P = Private synonym
            ddl = ddl + "PRIVATE ";
        }
        // owner
        ddl = ddl + "SYNONYM \"" + synonymInfo.SynOwner.trim() + "\".";
        // name
        ddl = ddl + getName(synonymInfo.SynName) + " FOR ";
        // 同义词指向remote
        if (synonymInfo.RTabName != null){      // 同义远程表
            // remote dbname
            ddl = ddl + synonymInfo.RDbName.trim();
            // remote servername, can other db
            if (synonymInfo.RServerName != null && ! "".equals(synonymInfo.RServerName)){
                ddl = ddl + "@" + synonymInfo.RServerName.trim();
            }
            // remote table owner
            ddl = ddl + ":\"" + synonymInfo.ROwner.trim() + "\".";
            // remote table name
            ddl = ddl + getName(synonymInfo.RTabName);
            // 同义词指向本库
        } else {
            // owner
            ddl = ddl + "\"" + synonymInfo.LOwner.trim() + "\".";
            // table name
            ddl = ddl + getName(synonymInfo.LTabName);
        }
        return ddl + ";";
    }

    /**
     * 获取视图定义信息
     * @param connection
     * @param viewname
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static ViewInfo getViewInfo(Connection connection,String viewname) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ViewInfo viewInfo = new ViewInfo();
        String sqlstr = null;
        String viewbody = "";
        int flags = 0;
        sqlstr = "select v.viewtext as viewtext, t.flags " +
                "from systables t,sysviews v " +
                "where t.tabid = v.tabid " +
                "and t.tabtype = 'V' " +
                "and t.tabname = ? " +
                "order by v.seqno";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,viewname);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            viewbody = viewbody + rtrimascii0(resultSet.getString("viewtext"));
            flags = resultSet.getInt("flags");
        }
        if ("".equals(viewbody)){
            return null;
        }
        viewInfo.ViewName = viewname;
        viewInfo.ViewBoday = viewbody;
        viewInfo.Flags = flags;
        resultSet.close();
        preparedStatement.close();
        return viewInfo;
    }

    /**
     * 打印视图定义
     * @param connection
     * @param viewname
     * @param delimident
     * @return
     * @throws SQLException
     */
    public static String printView(Connection connection, String viewname) throws SQLException {
        ViewInfo viewInfo = getViewInfo(connection,viewname);
        String viewsql = "SET ENVIRONMENT SQLMODE '" + viewInfo.getViewSqlMode() + "';\n";
        if (viewInfo == null || viewInfo.ViewName == null){
            return "";
        }
        return viewsql + viewInfo.ViewBoday;
    }

    /**
     * 获取存储过程、函数信息定义。
     * @param connection
     * @param procname
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static ProcedureInfo getProcInfo (Connection connection,String procdefine) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ProcedureInfo procedureInfo = new ProcedureInfo();
        String sqlstr = null;
        String procbody = "";
        String[] tmpproc = null;
        String procname = null;
        int numargs = 0;
        String paramtypes = "";
        int isexists = 0;
        int procflags = 0;

        // procdefine: func1(integer,integer)
        tmpproc = procdefine.split("\\(");
        procname = tmpproc[0].trim();
        if (tmpproc.length == 1){
            paramtypes = "";
        } else {
            tmpproc = tmpproc[1].toLowerCase().split("\\)");
            tmpproc = tmpproc[0].split(",");
            numargs = tmpproc.length;
            for(String str:tmpproc){
                paramtypes = paramtypes + "," + str.trim();
            }
            paramtypes = paramtypes.substring(1);
        }

        sqlstr = "select count(*) as isexists from sysprocedures where procname = ?";
        preparedStatement = connection.prepareStatement(sqlstr);
        preparedStatement.setString(1,procname);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            isexists = resultSet.getInt(1);
        }
        if (isexists == 0){                 // 不存在
            return null;
        } else if (isexists == 1){          //  单个
            sqlstr = "select p.procname,b.seqno,b.data procbody,p.procflags " +
                    "from sysprocedures p, sysprocbody b " +
                    "where p.procid = b.procid " +
                    "and p.procname = ? " +
                    "and p.mode in ('O','o') " +
                    "and b.datakey = 'T' " +
                    "order by b.seqno";
            preparedStatement = connection.prepareStatement(sqlstr);
            preparedStatement.setString(1,procname);
        } else if (isexists > 1){           // 多个函数
            sqlstr = "select p.procname,b.seqno,b.data procbody,p.procflags " +
                    "from sysprocedures p, sysprocbody b " +
                    "where p.procid = b.procid " +
                    "and p.procname = ? " +
                    "and p.mode in ('O','o') " +
                    "and p.numargs =  ? " +
                    "and rtn_param_out(paramtypes) = ? " +
                    "and b.datakey = 'T' " +
                    "order by b.seqno";
            preparedStatement = connection.prepareStatement(sqlstr);
            preparedStatement.setString(1,procname);
            preparedStatement.setInt(2,numargs);
            preparedStatement.setString(3,paramtypes);
        }
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            procbody = procbody + rtrimascii0(resultSet.getString("procbody"));
            procflags = resultSet.getInt("procflags");
        }
        if ("".equals(procbody)){
            return null;
        }
        procedureInfo.ProcName = procname;
        procedureInfo.ProcBoday = procbody.trim();
        procedureInfo.ProcFlags = procflags;
        resultSet.close();
        preparedStatement.close();
        return procedureInfo;
    }

    /**
     * 打印存储过程信息
     * @param connection
     * @param procdefine
     * @param delimident
     * @return
     * @throws SQLException
     */
    public static String printProcedure(Connection connection, String procdefine) throws SQLException {
        ProcedureInfo procedureInfo = getProcInfo(connection,procdefine);
        if (procedureInfo == null || procedureInfo.ProcName == null){
            return "";
        }
        String procsql = "SET ENVIRONMENT SQLMODE '" + procedureInfo.getProcSqlMode() + "';\n";
        return procsql + procedureInfo.ProcBoday;
    }

    //added by L3 20260124
    public static String printPackage (Connection connection,String procname) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String packageString="";

        //data字段是nchar(256),可能存在空白区域没有空格填充，导致查询出来的数据无法正常解析，使用substr截取目前看没有问题
        //preparedStatement = connection.prepareStatement("select b.procid,substr(b.data,0,length(b.data)) from sysprocedures p, sysprocbody b where p.procid = b.procid and p.mode='O' and retsize=0 and datakey='T' and p.procname=? order by b.procid,b.seqno");
        preparedStatement = connection.prepareStatement("select b.procid,b.data from sysprocedures p, sysprocbody b where p.procid = b.procid and p.mode='O' and retsize=0 and datakey='T' and p.procname=? order by b.procid,b.seqno");

        preparedStatement.setString(1,procname);
        resultSet = preparedStatement.executeQuery();
        int lastprocId=0;
        int preprocId=0;
        while (resultSet.next()){
            lastprocId=resultSet.getInt(1);
            if(preprocId==0){
                preprocId=lastprocId;
            }
            if(preprocId!=lastprocId){  //包头与包体之间增加执行
                packageString+="\n/\n";
            }
            packageString += resultSet.getString(2).replaceAll("\u0000", "");
            preprocId=lastprocId;
        }
        packageString+="\n/\n";

        if (resultSet != null){
            resultSet.close();
        }
        if (preparedStatement != null){
            preparedStatement.close();
        }

        return packageString;
    }

}
