package com.dbboys.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.dbboys.vo.ColumnsInfo;
import com.dbboys.vo.CheckInfo;
import com.dbboys.vo.ExtTableDfiles;
import com.dbboys.vo.ExtTableInfo;
import com.dbboys.vo.ForeignKeyInfo;
import com.dbboys.vo.FragmentInfo;
import com.dbboys.vo.Index;
import com.dbboys.vo.PrimaryKeyInfo;
import com.dbboys.vo.Procedure;
import com.dbboys.vo.Sequence;
import com.dbboys.vo.Synonym;
import com.dbboys.vo.Table;
import com.dbboys.vo.Trigger;
import com.dbboys.vo.View;


public class DDLRepository {
    private static final int DEFAULT_QUERY_TIMEOUT_SECONDS = 30;

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
        String patternDelimIdent = "^[a-z_][a-z0-9_]*$";
        if(Pattern.matches(patternDelimIdent,str)){
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
        String sql = """
                SELECT frag.colno, frag.strategy, frag.evalpos, frag.exprtext, frag.flags, frag.dbspace, frag.partition
                FROM sysfragments frag, systables tab
                WHERE frag.tabid = tab.tabid
                AND tab.tabname = ?
                AND frag.fragtype = 'T'
                ORDER BY frag.evalpos
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return new ArrayList<>(runner.query(sql, List.of(tablename), resultSet -> {
            FragmentInfo tableFragmentInfo = new FragmentInfo();
            tableFragmentInfo.setFragName(tablename);
            tableFragmentInfo.setColNo(resultSet.getInt("colno"));
            tableFragmentInfo.setStrategy(resultSet.getString("strategy"));
            tableFragmentInfo.setEvalpos(resultSet.getInt("evalpos"));
            tableFragmentInfo.setExprtext(resultSet.getString("exprtext"));
            tableFragmentInfo.setFlags(resultSet.getInt("flags"));
            tableFragmentInfo.setDbspace(resultSet.getString("dbspace"));
            tableFragmentInfo.setPartition(resultSet.getString("partition"));
            return tableFragmentInfo;
        }));
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
    private static Table getTableInfo(Connection connection, String tablename) throws SQLException {
        Table tableInfo = null;
        int dbVersion = 3; // 默认数据库JDBC版本
        String sql = """
                select t.tabname,dbinfo('dbname') as tablecatalog, t.owner as tableowner,t.locklevel as locktype,
                t.fextsize as firstextsize, t.nextsize as nextextsize, c.comments as tablecomm, t.tabtype as tabletype,
                t.flags as tableflags
                from systables t left join syscomments c on t.tabname = c.tabname
                where t.tabname = ?
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        List<Table> tables = runner.query(sql, List.of(tablename), resultSet -> {
            Table rowTableInfo = new Table(tablename);
            rowTableInfo.setTableCatalog(resultSet.getString("tablecatalog"));
            rowTableInfo.setTableOwner(trim(resultSet.getString("tableowner")));
            rowTableInfo.setLockType(trim(resultSet.getString("locktype")));
            rowTableInfo.setFirstExtSize(resultSet.getInt("firstextsize"));
            rowTableInfo.setNextExtSize(resultSet.getInt("nextextsize"));
            rowTableInfo.setTableComm(trim(resultSet.getString("tablecomm")));
            rowTableInfo.setTableTypeCode(trim(resultSet.getString("tabletype")));
            rowTableInfo.setFlags(resultSet.getInt("tableflags"));
            return rowTableInfo;
        });
        if (!tables.isEmpty()) {
            tableInfo = tables.get(0);
        } else {
            tableInfo = new Table(tablename);
        }
        tableInfo.setName(tablename);

        String jdbcVersion = connection.getMetaData().getDriverVersion();
        dbVersion=Integer.parseInt(jdbcVersion.substring(0,1));
        tableInfo.setDbVersion(dbVersion);
        return tableInfo;
    }
    //added by L3 20260205，用于返回字段列表
    public static ArrayList<ColumnsInfo> getColInfo(Connection connection,String tabname) throws SQLException {
        ArrayList<ColumnsInfo> arrayList = new ArrayList<ColumnsInfo>();
        Table tableInfo=getTableInfo(connection, tabname);
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
    private static ArrayList<ColumnsInfo> getColInfo(Connection connection, Table tableInfo) throws SQLException {
        ArrayList<ColumnsInfo> arrayList = new ArrayList<>();
        // 对于default默认值，C=Current，L=Literal value,N=Null,S=Dbservername or Sitename，T=Today, U=User
        // 对于虚拟表，sysdefaultsexpr可能多行定义
        String sql = """
                SELECT
                   sc.colno colno
                  ,sc.colname colname
                  ,sc.coltype,sc.collength
                  ,CASE WHEN mod(sc.coltype,256) in (1,2,52,17,6,18,53,5,8) THEN 10 WHEN mod(sc.coltype,256) in (3,4) THEN 2 ELSE 0 END as typep
                  ,CASE WHEN mod(sc.coltype,256) in (5,8) THEN MOD(sc.collength,256) ELSE 0 END as types
                  ,CASE WHEN bitand(sc.coltype,256) = 256 THEN 0 ELSE 1 END as isnullable
                  ,CASE WHEN sc.colattr = 128 THEN 1 ELSE 0 END as ispk
                  ,df.type as coldeftype
                  ,CASE df.type
                         WHEN 'L' THEN get_default_value(sc.coltype, sc.extended_id, sc.collength, df.default::lvarchar(256))::VARCHAR(254)
                         WHEN 'C' THEN 'current year to second'::VARCHAR(254)
                         WHEN 'S' THEN 'dbservername'::VARCHAR(254)
                         WHEN 'U' THEN 'user'::VARCHAR(254)
                         WHEN 'T' THEN 'today'::VARCHAR(254)
                         WHEN 'E' THEN de.default::VARCHAR(254) || ' '
                         ELSE          NULL::VARCHAR(254)
                   END as coldef
                  ,cc.comments as colcomm
                  ,CASE WHEN mod(sc.coltype,256) in (6,18,53) THEN 1 ELSE 0 END as ISAUTOINCREMENT
                  ,sx.name sxname
                FROM systables t
                LEFT JOIN syscolumns sc ON t.tabid = sc.tabid
                LEFT JOIN syscolcomments cc ON (t.tabname = cc.tabname AND sc.colname = cc.colname)
                LEFT JOIN sysdefaults df ON (t.tabid = df.tabid AND sc.colno = df.colno)
                LEFT JOIN sysdefaultsexpr de ON (t.tabid = de.tabid AND sc.colno = de.colno and de.type='T')
                LEFT JOIN sysxtdtypes sx ON (sx.type = mod(sc.coltype,256) AND sx.extended_id = sc.extended_id)
                WHERE t.tabname = ?
                ORDER BY sc.colno;
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        List<ColumnsInfo> rows = runner.query(sql, List.of(tableInfo.getName()), resultSet -> {
            ColumnsInfo columnsInfo = new ColumnsInfo();
            columnsInfo.setColNo(resultSet.getInt("colno"));
            columnsInfo.setColName(resultSet.getString("colname"));
            columnsInfo.setColType(getColTypeName(resultSet.getInt("coltype"), resultSet.getInt("collength"), 0, 0, resultSet.getString("sxname")));
            columnsInfo.setColLength(getLength(columnsInfo.getColType(), resultSet.getInt("collength"), tableInfo.getDbVersion()));
            columnsInfo.setTypeP(getPrecision(columnsInfo.getColType(), resultSet.getInt("collength")));
            columnsInfo.setTypeS(getScale(columnsInfo.getColType(), resultSet.getInt("collength")));
            columnsInfo.setIsNullable((resultSet.getInt("isnullable") == 1));
            columnsInfo.setIsPK((resultSet.getInt("ispk") == 1));
            columnsInfo.setColDefType(resultSet.getString("coldeftype"));
            columnsInfo.setColDef(trim(resultSet.getString("coldef")));
            columnsInfo.setColComm(trim(resultSet.getString("colcomm")));
            columnsInfo.setIsAutoincrement((resultSet.getInt("isautoincrement") == 1));
            return columnsInfo;
        });

        for (ColumnsInfo columnsInfo : rows) {
            int size = arrayList.size();
            if (size > 0 && arrayList.get(size - 1).getColNo() == columnsInfo.getColNo()) {
                ColumnsInfo last = arrayList.get(size - 1);
                columnsInfo.setColName(last.getColName());
                columnsInfo.setColType(last.getColType());
                columnsInfo.setColLength(last.getColLength());
                columnsInfo.setTypeP(last.getTypeP());
                columnsInfo.setTypeS(last.getTypeS());
                columnsInfo.setIsNullable(last.isIsNullable());
                columnsInfo.setIsPK(last.isIsPK());
                columnsInfo.setColDefType(last.getColDefType());
                columnsInfo.setColDef(last.getColDef() + trim(columnsInfo.getColDef()));
                columnsInfo.setColComm(last.getColComm());
                columnsInfo.setIsAutoincrement(last.isIsAutoincrement());
                arrayList.set(size - 1, columnsInfo);
            } else {
                arrayList.add(columnsInfo);
            }
        }
        return arrayList;
    }

    private static String getTableSqlMode(Table tableInfo) {
        if ((tableInfo.getFlags() & 16384) == 16384) {
            return "Oracle";
        }
        return "GBase";
    }

    private static String getTableGlobalTemporary(Table tableInfo) {
        if ((tableInfo.getFlags() & 4096) == 4096) {
            return "GLOBAL TEMPORARY";
        }
        return "";
    }

    private static String getTableGlobalTemporaryLevel(Table tableInfo) {
        if ((tableInfo.getFlags() & 8192) == 8192) {
            return "ON COMMIT DELETE ROWS";
        }
        return "ON COMMIT PRESERVE ROWS";
    }

    private static String getTableLockType(Table tableInfo) {
        if("P".equals(tableInfo.getLockType())){
            return "PAGE";
        } else if("B".equals(tableInfo.getLockType())){
            return "PAGE,ROW";
        }
        return "ROW";
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
        String sql = """
                SELECT con.constrname, chk.seqno as checkseqno, chk.checktext as checktext
                FROM sysconstraints con, syschecks chk, systables t
                WHERE con.constrid = chk.constrid
                AND con.tabid = t.tabid
                AND t.tabname = ?
                AND con.constrtype = 'C'
                AND chk.TYPE = 'T'
                ORDER BY con.constrname, chk.seqno
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        List<CheckInfo> rows = runner.query(sql, List.of(tablename), resultSet -> {
            CheckInfo checkInfo = new CheckInfo();
            checkInfo.setConstrName(resultSet.getString("constrname"));
            checkInfo.setCheckText(trim(resultSet.getString("checkText")));
            return checkInfo;
        });

        ArrayList<CheckInfo> arrayList = new ArrayList<>();
        for (CheckInfo checkInfo : rows) {
            int size = arrayList.size();
            if (size > 0 && arrayList.get(size - 1).getConstrName().equals(checkInfo.getConstrName())) {
                CheckInfo merged = new CheckInfo();
                merged.setConstrName(arrayList.get(size - 1).getConstrName());
                merged.setCheckText(arrayList.get(size - 1).getCheckText() + checkInfo.getCheckText());
                arrayList.set(size - 1, merged);
            } else {
                arrayList.add(checkInfo);
            }
        }
        return arrayList;
    }

    /**
     * 获取主键索引信息
     * @param connection
     * @param columns
     * @param tablename
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static ArrayList<PrimaryKeyInfo> getPrimaryKey(Connection connection, ArrayList<ColumnsInfo> columns, String tablename) throws SQLException {
        String sql = """
                SELECT con.constrname,con.constrtype,idx.indexkeys::lvarchar as idxcols
                FROM sysconstraints con, sysindices idx, systables t
                WHERE con.idxname = idx.idxname
                AND con.tabid = t.tabid
                AND t.tabname = ?
                AND con.constrtype in ('P','U')
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return new ArrayList<>(runner.query(sql, List.of(tablename), resultSet -> {
            PrimaryKeyInfo primaryKeyInfo = new PrimaryKeyInfo();
            primaryKeyInfo.setIdxCols(getIdxCols(connection, resultSet.getString("idxcols"), getColNameListByColumnsInfo(columns)));
            primaryKeyInfo.setConstrName(resultSet.getString("constrname"));
            primaryKeyInfo.setConstrType(resultSet.getString("constrtype"));
            return primaryKeyInfo;
        }));
    }

    /**
     * 从ColumnsInfo中获取字段名称列表
     * @param columns
     * @return
     */
    private static ArrayList<String> getColNameListByColumnsInfo(ArrayList<ColumnsInfo> columns){
        ArrayList<String> colNameList = new ArrayList<>();
        for(int i=0;i<columns.size();i++){
            colNameList.add(columns.get(i).getColName());
        }
        return colNameList;
    }

    /**
     * 表的所有索引列表
     * @param connection
     * @param columns
     * @param tablename
     * @return
     * @throws SQLException
     */
    private static ArrayList<Index> getIndexesInfo(Connection connection, ArrayList<ColumnsInfo> columns, String tablename) throws SQLException {
        String sql = """
                SELECT idx.idxname, idx.owner as idxowner, idx.idxtype, idx.clustered as idxcluster, idx.indexkeys::lvarchar as idxcols
                FROM sysindices idx, systables t
                WHERE idx.tabid = t.tabid
                AND t.tabname = ?;
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return new ArrayList<>(runner.query(sql, List.of(tablename), resultSet -> {
            Index indexInfo = new Index(resultSet.getString("idxname"));
            indexInfo.setIndexOwner(trim(resultSet.getString("idxowner")));
            indexInfo.setIndexType(resultSet.getString("idxtype"));
            indexInfo.setIndexCluster(resultSet.getString("idxcluster"));
            indexInfo.setIndexCols(getIdxCols(connection, resultSet.getString("idxcols"), getColNameListByColumnsInfo(columns)));
            return indexInfo;
        }));
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
        String sql = """
                SELECT trigname
                FROM systriggers tri, systables t
                WHERE tri.tabid = t.tabid
                AND t.tabname = ?;
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return new ArrayList<>(runner.query(sql, List.of(tablename), resultSet -> resultSet.getString("trigname")));
    }

    /**
     * 获取外部表定义，来源于sysexternal
     * @param connection
     * @param tablename
     * @return
     */
    private static ExtTableInfo getExtTableInfo(Connection connection, String tablename) throws SQLException {
        String sql = """
                SELECT t.tabname as tablename, e.fmttype as formattype, e.codeset as codeset, e.recdelim as recorddelimiter,
                e.flddelim as fielddelimiter, e.datefmt as dateformat, e.moneyfmt as moneyformat, e.maxerrors as maxerrors,
                e.rejectfile as rejectfile, e.flags as flags, e.ndfiles as numdfiles
                FROM systables t, sysexternal e
                WHERE t.tabid = e.tabid
                AND t.tabname = ?
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        ExtTableInfo extTableInfo = runner.queryOne(sql, List.of(tablename), resultSet -> {
            ExtTableInfo row = new ExtTableInfo();
            row.setFormatType(resultSet.getString("formattype"));
            row.setCodeSet(resultSet.getString("codeset"));
            row.setRecordDelimiter(resultSet.getString("recorddelimiter"));
            row.setFieldDelimiter(resultSet.getString("fielddelimiter"));
            row.setDateFormat(resultSet.getString("dateformat"));
            row.setMoneyFormat(resultSet.getString("moneyformat"));
            row.setMaxErrors(resultSet.getInt("maxerrors"));
            row.setRejectFile(resultSet.getString("rejectfile"));
            row.setFlags(resultSet.getInt("flags"));
            row.setNumDfiles(resultSet.getInt("numdfiles"));
            return row;
        });
        if (extTableInfo == null) {
            extTableInfo = new ExtTableInfo();
        }
        extTableInfo.setTableName(tablename);
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
        String sql = """
                SELECT t.tabname as tablename,e.dfentry as datafile, e.blobdir as blobdir, e.clobdir as clobdir
                FROM systables t, sysextdfiles e
                WHERE t.tabid = e.tabid
                AND t.tabname = ?
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return new ArrayList<>(runner.query(sql, List.of(tablename), resultSet -> {
            ExtTableDfiles extTableDfiles = new ExtTableDfiles();
            extTableDfiles.setTableName(resultSet.getString("tablename"));
            extTableDfiles.setDataFile(resultSet.getString("datafile"));
            extTableDfiles.setBlobDir(resultSet.getString("blobdir"));
            extTableDfiles.setClobDir(resultSet.getString("clobdir"));
            return extTableDfiles;
        }));
    }

    /**
     * 获取外键信息表（可能多行）
     * @param connection
     * @param tablename
     * @return
     * @throws SQLException
     */
    private static ArrayList<ForeignKeyInfo> getForeignKeyInfo(Connection connection, String tablename) throws SQLException {
        String sql = """
                SELECT fk_c.constrname,fk_t.owner AS fkowner, fk_t.tabname AS fktabname, fk_i.indexkeys::lvarchar AS fk_keys,
                fk_c.idxname as fkidxname, pk_t.owner AS pkowner, pk_t.tabname AS pktabname, pk_i.indexkeys::lvarchar AS pk_keys,
                pk_i.idxname as pkidxname
                FROM sysconstraints fk_c, systables fk_t, sysindices fk_i,sysreferences fk_r,
                     sysconstraints pk_c, systables pk_t, sysindices pk_i
                WHERE fk_c.tabid = fk_t.tabid
                AND fk_t.tabname = ?
                AND fk_c.constrtype = 'R'
                AND fk_c.idxname = fk_i.idxname
                AND fk_c.constrid = fk_r.constrid
                AND fk_r.PRIMARY = pk_c.constrid
                AND pk_c.tabid = pk_t.tabid
                AND pk_c.idxname = pk_i.idxname;
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return new ArrayList<>(runner.query(sql, List.of(tablename), resultSet -> {
            ForeignKeyInfo foreignKeyInfo = new ForeignKeyInfo();
            foreignKeyInfo.setFkName(resultSet.getString("constrname"));
            foreignKeyInfo.setFkOwner(resultSet.getString("fkowner"));
            foreignKeyInfo.setFkTabname(resultSet.getString("fktabname"));
            foreignKeyInfo.setFkCols(resultSet.getString("fk_keys"));
            foreignKeyInfo.setFkIdxName(resultSet.getString("fkidxname"));
            foreignKeyInfo.setPkOwner(resultSet.getString("pkowner"));
            foreignKeyInfo.setPkTabname(resultSet.getString("pktabname"));
            foreignKeyInfo.setPkCols(resultSet.getString("pk_keys"));
            foreignKeyInfo.setPkIdxName(resultSet.getString("pkidxname"));
            return foreignKeyInfo;
        }));
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
        String patternConstraint = "^[cur]\\d+_\\d+";              // u=unique,r=reference,c=check
        Table tableInfo = getTableInfo(connection,tablename);
        String sqlmode = getTableSqlMode(tableInfo);
        StringBuilder ddl = new StringBuilder();
        ddl.append("SET ENVIRONMENT SQLMODE '").append(sqlmode).append("';\n");
        // 视图，序列、同义词暂时只打印自己的信息
        if ("V".equalsIgnoreCase(tableInfo.getTableTypeCode())){
            return printView(connection,tablename);
        } else if("Q".equalsIgnoreCase(tableInfo.getTableTypeCode())) {
            return printSequence(connection,tablename);
        } else if("P".equalsIgnoreCase(tableInfo.getTableTypeCode()) || "S".equalsIgnoreCase(tableInfo.getTableTypeCode())) {
            return printSynonym(connection, tablename);
        }

        ddl.append("CREATE ");
        // global temporary
        ddl.append(getTableGlobalTemporary(tableInfo)).append(" ");
        // external
        if("E".equals(tableInfo.getTableTypeCode())){
            ddl.append("EXTERNAL TABLE ");
        } else {
            ddl.append("TABLE ");
        }
        // OWNER
        if (tableInfo.getTableOwner()==null){
            return "";
        }
        ddl.append("\"").append(tableInfo.getTableOwner()).append("\".");

        ddl.append(getName(tableInfo.getName())).append(" (\n");

        ArrayList<CheckInfo> checks = getCheck(connection,tablename);
        ArrayList<ColumnsInfo> columns = getColInfo(connection,tableInfo);
        ArrayList<PrimaryKeyInfo> primaryKeys = getPrimaryKey(connection,columns,tablename);
        ArrayList<Index> indexes = getIndexesInfo(connection,columns,tablename);
        ArrayList<FragmentInfo> tableFragments = getTableFragmentInfo(connection,tablename);
        ArrayList<String> triggers = getTriggerList(connection,tablename);
        ArrayList<ForeignKeyInfo> foreignKeys = getForeignKeyInfo(connection,tablename);

        appendColumnsDefinition(ddl, columns);
        appendCheckConstraints(ddl, checks, sqlmode, patternConstraint);
        appendPrimaryConstraints(ddl, primaryKeys, sqlmode, patternConstraint);
        ddl.append("\n) ");

        // E 外部表处理，不考虑maxrows，TODO：没处理外部数据类型对应
        if ("E".equals(tableInfo.getTableTypeCode())){
            appendExternalTableDefinition(connection, ddl, tableInfo);
        } else {        // T 普通表
            appendNormalTableDefinition(
                    connection,
                    ddl,
                    tableInfo,
                    tablename,
                    sqlmode,
                    patternConstraint,
                    columns,
                    indexes,
                    tableFragments,
                    foreignKeys,
                    triggers
            );
        }

        return ddl.toString();
    }

    private static void appendColumnsDefinition(StringBuilder ddl, ArrayList<ColumnsInfo> columns) {
        for (int i = 0; i < columns.size(); i++) {
            ColumnsInfo column = columns.get(i);
            ddl.append("  ").append(getName(column.getColName()));
            ddl.append(" ").append(getColTypeName(
                    column.getColType(),
                    column.getColLength(),
                    column.getTypeP(),
                    column.getTypeS()
            ));
            if (!column.isIsNullable()) {
                ddl.append(" NOT NULL");
            }
            if (column.getColDefType() != null) {
                ddl.append(" DEFAULT ").append(getDefaults(
                        column.getColType(),
                        column.getColDefType(),
                        column.getColDef()
                ));
            }
            if (i < columns.size() - 1) {
                ddl.append(",\n");
            }
        }
    }

    private static void appendCheckConstraints(StringBuilder ddl, ArrayList<CheckInfo> checks, String sqlmode, String patternConstraint) {
        for (CheckInfo check : checks) {
            if ("Oracle".equalsIgnoreCase(sqlmode)) {
                if (!Pattern.matches(patternConstraint, check.getConstrName())) {
                    ddl.append(",\n  CONSTRAINT ").append(getName(check.getConstrName()));
                    ddl.append("  CHECK ").append(check.getCheckText());
                } else {
                    ddl.append(",\n  CHECK ").append(check.getCheckText());
                }
            } else {
                ddl.append(",\n  CHECK ").append(check.getCheckText());
                if (!Pattern.matches(patternConstraint, check.getConstrName())) {
                    ddl.append(" CONSTRAINT ").append(getName(check.getConstrName()));
                }
            }
        }
    }

    private static void appendPrimaryConstraints(StringBuilder ddl, ArrayList<PrimaryKeyInfo> primaryKeys, String sqlmode, String patternConstraint) {
        for (PrimaryKeyInfo primaryKey : primaryKeys) {
            if ("Oracle".equalsIgnoreCase(sqlmode)) {
                if (!Pattern.matches(patternConstraint, primaryKey.getConstrName())) {
                    ddl.append(",\n  CONSTRAINT ").append(getName(primaryKey.getConstrName()));
                    if ("P".equals(primaryKey.getConstrType())) {
                        ddl.append("  PRIMARY KEY(");
                    } else if ("U".equals(primaryKey.getConstrType())) {
                        ddl.append("  UNIQUE(");
                    }
                    ddl.append(primaryKey.getIdxCols()).append(")");
                } else {
                    if ("P".equals(primaryKey.getConstrType())) {
                        ddl.append(",\n  PRIMARY KEY(");
                    } else if ("U".equals(primaryKey.getConstrType())) {
                        ddl.append(",\n  UNIQUE(");
                    }
                    ddl.append(primaryKey.getIdxCols()).append(")");
                }
            } else {
                if ("P".equals(primaryKey.getConstrType())) {
                    ddl.append(",\n  PRIMARY KEY(");
                } else if ("U".equals(primaryKey.getConstrType())) {
                    ddl.append(",\n  UNIQUE(");
                }
                ddl.append(primaryKey.getIdxCols()).append(")");
                if (!Pattern.matches(patternConstraint, primaryKey.getConstrName())) {
                    ddl.append("  CONSTRAINT ").append(getName(primaryKey.getConstrName()));
                }
            }
        }
    }

    private static void appendExternalTableDefinition(Connection connection, StringBuilder ddl, Table tableInfo) throws SQLException {
        ExtTableInfo extTableInfo = getExtTableInfo(connection, tableInfo.getName());
        ArrayList<ExtTableDfiles> extTableFiles = getExtTableDfiles(connection, tableInfo.getName());
        if (extTableInfo == null) {
            ddl.append(";");
            return;
        }
        ddl.append("\nUSING ( \n  DATAFILES(\n");
        for (int i = 0; i < extTableInfo.getNumDfiles(); i++) {
            ddl.append("    '").append(trim(extTableFiles.get(i).getDataFile()));
            if (extTableFiles.get(i).getBlobDir() != null && !"".equals(trim(extTableFiles.get(i).getBlobDir()))) {
                ddl.append(";BLOBDIR:").append(trim(extTableFiles.get(i).getBlobDir()));
            }
            if (extTableFiles.get(i).getClobDir() != null && !"".equals(trim(extTableFiles.get(i).getClobDir()))) {
                ddl.append(";CLOBDIR:").append(trim(extTableFiles.get(i).getClobDir()));
            }
            if (i == (extTableInfo.getNumDfiles() - 1)) {
                ddl.append("'\n  ),\n");
            } else {
                ddl.append("',\n");
            }
        }

        if ("D".equals(extTableInfo.getFormatType())) {
            ddl.append("  FORMAT 'DELIMITED',\n");
        } else if ("F".equals(extTableInfo.getFormatType())) {
            ddl.append("  FORMAT 'FIXED',\n");
        } else if ("I".equals(extTableInfo.getFormatType())) {
            ddl.append("  FORMAT 'GBASEDBT',\n");
        }
        if (extTableInfo.getFieldDelimiter() != null) {
            ddl.append("  DELIMITER '").append(extTableInfo.getFieldDelimiter()).append("',\n");
        }
        if (extTableInfo.getRecordDelimiter() != null) {
            ddl.append("  RECORDEND '").append(extTableInfo.getRecordDelimiter()).append("',\n");
        }
        if (extTableInfo.getDateFormat() != null) {
            ddl.append("  DBDATE '").append(trim(extTableInfo.getDateFormat())).append("',\n");
        }
        if (extTableInfo.getMoneyFormat() != null) {
            ddl.append("  DBMONEY '").append(trim(extTableInfo.getMoneyFormat())).append("',\n");
        }
        if (extTableInfo.getMaxErrors() != -1) {
            ddl.append("  MAXERRORS ").append(extTableInfo.getMaxErrors()).append(",\n");
        }
        if (extTableInfo.getRejectFile() != null) {
            ddl.append("  REJECTFILE '").append(trim(extTableInfo.getRejectFile())).append("',\n");
        }
        if ((extTableInfo.getFlags() & 4) == 4) {
            ddl.append("  DELUXE,\n");
        } else if ((extTableInfo.getFlags() & 8) == 8) {
            ddl.append("  EXPRESS,\n");
        }
        if ((extTableInfo.getFlags() & 2) == 2) {
            ddl.append("  ESCAPE ON\n);");
        } else {
            ddl.append("  ESCAPE OFF\n);");
        }
    }

    private static void appendNormalTableDefinition(
            Connection connection,
            StringBuilder ddl,
            Table tableInfo,
            String tablename,
            String sqlmode,
            String patternConstraint,
            ArrayList<ColumnsInfo> columns,
            ArrayList<Index> indexes,
            ArrayList<FragmentInfo> tableFragments,
            ArrayList<ForeignKeyInfo> foreignKeys,
            ArrayList<String> triggers
    ) throws SQLException {
        if (!tableFragments.isEmpty()) {
            ddl.append(buildFragmentString(tableFragments));
        }

        if ("".equals(getTableGlobalTemporary(tableInfo))) {
            ddl.append("\n");
        } else {
            ddl.append("\n").append(getTableGlobalTemporaryLevel(tableInfo)).append(" ");
        }
        ddl.append("EXTENT SIZE ").append(tableInfo.getFirstExtSize()).append(" NEXT SIZE ").append(tableInfo.getNextExtSize());
        ddl.append(" LOCK MODE ").append(getTableLockType(tableInfo)).append(";\n");

        for (Index index : indexes) {
            if (index.getName().startsWith(" ")) {
                continue;
            }
            ddl.append("\nCREATE");
            if ("U".equals(index.getIndexType())) {
                ddl.append(" UNIQUE INDEX");
            } else if ("C".equals(index.getIndexCluster())) {
                ddl.append(" CLUSTER INDEX");
            } else {
                ddl.append(" INDEX");
            }
            ddl.append(" \"").append(index.getIndexOwner()).append("\".").append(getName(index.getName())).append(" ON");
            ddl.append(" \"").append(tableInfo.getTableOwner()).append("\".").append(getName(tableInfo.getName())).append("(").append(index.getIndexCols()).append(")");
            ddl.append(buildFragmentString(getIndexFragmentInfo(connection, index.getName()))).append(";");
        }
        ddl.append("\n");

        ArrayList<String> fkColumns = getColNameListByColumnsInfo(columns);
        for (ForeignKeyInfo foreignKey : foreignKeys) {
            ddl.append("\nALTER TABLE \"").append(trim(foreignKey.getFkOwner())).append("\".").append(getName(foreignKey.getFkTabname()));
            if ("Oracle".equalsIgnoreCase(sqlmode)) {
                ddl.append(" ADD CONSTRAINT ");
                if (!Pattern.matches(patternConstraint, foreignKey.getFkName())) {
                    ddl.append(getName(foreignKey.getFkName()));
                }
                ddl.append("FOREIGN KEY(");
            } else {
                ddl.append(" ADD CONSTRAINT (FOREIGN KEY(");
            }

            ddl.append(getIdxCols(foreignKey.getFkCols(), fkColumns)).append(") ");

            ArrayList<String> pkColumns = getColNameListByTablename(connection, foreignKey.getPkTabname());
            ddl.append("REFERENCES \"").append(trim(foreignKey.getPkOwner())).append("\".").append(getName(foreignKey.getPkTabname()));
            ddl.append("(").append(getIdxCols(foreignKey.getPkCols(), pkColumns)).append("))");

            if ("GBase".equalsIgnoreCase(sqlmode)) {
                if (!Pattern.matches(patternConstraint, foreignKey.getFkName())) {
                    ddl.append(" CONSTRAINT ").append(getName(foreignKey.getFkName()));
                }
            }
            ddl.append(";\n");
        }

        if (tableInfo.getTableComm() != null) {
            ddl.append("\nCOMMENT ON TABLE \"").append(tableInfo.getTableOwner()).append("\".").append(getName(tablename)).append(" IS '")
                    .append(tableInfo.getTableComm().replace("'", "''")).append("';");
        }
        for (ColumnsInfo column : columns) {
            if (column.getColComm() != null) {
                ddl.append("\nCOMMENT ON COLUMN \"").append(tableInfo.getTableOwner()).append("\".").append(getName(tablename)).append(".")
                        .append(getName(column.getColName())).append(" IS '")
                        .append(column.getColComm().replace("'", "''")).append("';");
            }
        }

        ddl.append("\n");
        for (String triggerName : triggers) {
            ddl.append("\n").append(printTrigger(connection, triggerName));
        }
    }

    /**
     * 内部函数编号
     */
    private static final HashMap<Integer,String> INNER_FUNC_NAME;
    static {
        INNER_FUNC_NAME = new HashMap<>();
        INNER_FUNC_NAME.put(-1010,"abs");
        INNER_FUNC_NAME.put(-1015,"mod");
        INNER_FUNC_NAME.put(-173,"substr");
        INNER_FUNC_NAME.put(-201,"ascii");
        INNER_FUNC_NAME.put(-205,"to_number");
        INNER_FUNC_NAME.put(-212,"ceil");
        INNER_FUNC_NAME.put(-213,"floor");
        INNER_FUNC_NAME.put(-224,"degrees");
        INNER_FUNC_NAME.put(-225,"radians");
        INNER_FUNC_NAME.put(-227,"instr");
        INNER_FUNC_NAME.put(-231,"reverse");
        INNER_FUNC_NAME.put(-233,"len");
        INNER_FUNC_NAME.put(-234,"substrb");
        INNER_FUNC_NAME.put(-45,"length");
        INNER_FUNC_NAME.put(-46,"octet_length");
        INNER_FUNC_NAME.put(-47,"char_length");
        INNER_FUNC_NAME.put(-48,"upper");
        INNER_FUNC_NAME.put(-49,"lower");
        INNER_FUNC_NAME.put(-50,"initcap");
    }

    /**
     * 索引分片信息
     * @param connection
     * @param indexname
     * @return
     * @throws SQLException
     */
    private static ArrayList<FragmentInfo> getIndexFragmentInfo(Connection connection, String indexname) throws SQLException {
        String sql = """
                SELECT frag.colno, frag.strategy, frag.evalpos, frag.exprtext, frag.flags, frag.dbspace, frag.partition
                FROM sysfragments frag
                WHERE frag.indexname = ?
                AND frag.fragtype = 'I'
                ORDER BY frag.evalpos
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return new ArrayList<>(runner.query(sql, List.of(indexname), resultSet -> {
            FragmentInfo indexFragmentInfo = new FragmentInfo();
            indexFragmentInfo.setFragName(indexname);
            indexFragmentInfo.setColNo(resultSet.getInt("colno"));
            indexFragmentInfo.setStrategy(resultSet.getString("strategy"));
            indexFragmentInfo.setEvalpos(resultSet.getInt("evalpos"));
            indexFragmentInfo.setExprtext(resultSet.getString("exprtext"));
            indexFragmentInfo.setFlags(resultSet.getInt("flags"));
            indexFragmentInfo.setDbspace(resultSet.getString("dbspace"));
            indexFragmentInfo.setPartition(resultSet.getString("partition"));
            return indexFragmentInfo;
        }));
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
                fragtype = arrayList.get(0).getStrategy();
            }
            if (arrayList.get(i).getEvalpos() == 0) {
                break;
            }
            // rolling 窗口数量，及 rolling detach / discard
            if (arrayList.get(i).getEvalpos() == -4) {
                numfragments = arrayList.get(i).getColNo();
                if (arrayList.get(i).getFlags() == 16) {
                    fragdetech = " DETEAH";
                } else if (arrayList.get(i).getFlags() == 8) {
                    fragdetech = " DISCARD";
                }
                // list or range 字段，可能是函数处理后的
            } else if (arrayList.get(i).getEvalpos() == -3) {
                fragcolumn = arrayList.get(i).getExprtext();
            } else if (arrayList.get(i).getEvalpos() == -2) {
                fraginterval = arrayList.get(i).getExprtext();
            } else if (arrayList.get(i).getEvalpos() == -1) {
                // switch ?
                if (arrayList.get(i).getFlags() == 4096) {
                    fragdbslist = arrayList.get(i).getExprtext();        // dbspaces list
                } else if (arrayList.get(i).getFlags() == 36864) {
                    fragdbslist = arrayList.get(i).getExprtext() + "()"; // function
                }
            }
        }
        if ("I".equals(fragtype)) {                     // in dbspace
            ddl = " IN " + arrayList.get(0).getDbspace();
        } else if ("T".equals(fragtype)){               // 索引使用表的分片表达式
            ddl = "";
        } else if ("R".equals(fragtype)){
            ddl = ddl + "ROUND ROBIN \n";
            for(int i=0;i<arrayList.size();i++){
                ddl = ddl + "PARTITION " + arrayList.get(i).getPartition() + " IN " + arrayList.get(i).getDbspace();
                if (i<arrayList.size()-1){
                    ddl = ddl + ",\n";
                }
            }
        } else if ("E".equals(fragtype)){
            ddl = ddl + "EXPRESSION \n";
            for(int i=0;i<arrayList.size();i++){
                ddl = ddl + "PARTITION " + arrayList.get(i).getPartition() + " " + arrayList.get(i).getExprtext() + " IN " + arrayList.get(i).getDbspace();
                if (i<arrayList.size()-1){
                    ddl = ddl + ",\n";
                }
            }
        } else if ("L".equals(fragtype)){
            ddl = ddl + "LIST(" + fragcolumn + ") \n";
            for(int i=0;i<arrayList.size();i++){
                if (arrayList.get(i).getEvalpos() < 0){
                    continue;
                }
                ddl = ddl + "PARTITION " + arrayList.get(i).getPartition() + " " + arrayList.get(i).getExprtext() + " IN " + arrayList.get(i).getDbspace();
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
                if (arrayList.get(i).getEvalpos() < 0){
                    continue;
                }
                ddl = ddl + "PARTITION " + arrayList.get(i).getPartition() + " " + arrayList.get(i).getExprtext() + " IN " + arrayList.get(i).getDbspace();
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
                    funcname = INNER_FUNC_NAME.get(funcnum);
                } else {                // 自定义函数
                    String sql = """
                            select procname from sysprocedures where procid = ?
                            """;
                    SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
                    String procName = runner.queryOne(sql, List.of(funcnum), resultSet -> resultSet.getString("procname"));
                    if (procName != null) {
                        funcname = procName;
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
    private static Index getIndexInfo(Connection connection, String indexname, ArrayList<String> colnameList) throws SQLException {
        String sql = """
                SELECT i.owner idxowner,i.idxname,t.owner as tabowner, t.tabname,i.idxtype,i.clustered as idxcluster,i.indexkeys::lvarchar as indexkeys
                FROM sysindices i,systables t
                WHERE i.idxname = ?
                AND i.tabid = t.tabid
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        Index indexInfo = runner.queryOne(sql, List.of(indexname), resultSet -> {
            Index row = new Index();
            row.setName(resultSet.getString("idxname"));
            row.setIndexOwner(resultSet.getString("idxowner"));
            row.setTableName(resultSet.getString("tabname"));
            row.setTableOwner(resultSet.getString("tabowner"));
            row.setIndexType(resultSet.getString("idxtype"));
            row.setIndexCluster(resultSet.getString("idxcluster"));
            row.setIndexCols(resultSet.getString("indexkeys"));
            return row;
        });

        if (indexInfo == null || indexInfo.getName() == null){
            return null;
        }
        indexInfo.setIndexCols(getIdxCols(connection,indexInfo.getIndexCols(),colnameList));
        return indexInfo;
    }

    /**
     * 通过索引名获取表字段列表
     * @param connection
     * @param indexname
     * @return
     * @throws SQLException
     */
    private static ArrayList<String> getColNameListByIndexname(Connection connection, String indexname) throws SQLException {
        String sql = """
                SELECT col.colname
                FROM syscolumns col, sysindexes idx
                WHERE col.tabid = idx.tabid
                AND idx.idxname = ?
                ORDER BY col.colno
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return new ArrayList<>(runner.query(sql, List.of(indexname), resultSet -> resultSet.getString("colname")));
    }

    /**
     * 通过表名获取表字段列表
     * @param connection
     * @param tablename
     * @return
     * @throws SQLException
     */
    private static ArrayList<String> getColNameListByTablename(Connection connection, String tablename) throws SQLException {
        String sql = """
                SELECT col.colname
                FROM syscolumns col, systables t
                WHERE col.tabid = t.tabid
                AND t.tabname = ?
                ORDER BY col.colno
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return new ArrayList<>(runner.query(sql, List.of(tablename), resultSet -> resultSet.getString("colname")));
    }

    /**
     * 打印索引语句
     * @param connection
     * @param indexname
     * @return
     * @throws SQLException
     */
    public static String printIndex(Connection connection, String indexname) throws SQLException {
        ArrayList<String> colNameList = getColNameListByIndexname(connection, indexname);
        Index indexInfo = getIndexInfo(connection, indexname, colNameList);
        if (indexInfo == null || indexInfo.getName() == null){
            return "";
        }
        StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE");
        // 索引类型
        if("U".equals(indexInfo.getIndexType())) {
            ddl.append(" UNIQUE INDEX");
        } else if("C".equals(indexInfo.getIndexCluster())){
            ddl.append(" CLUSTER INDEX");
        } else {
            ddl.append(" INDEX");
        }
        // 索引名称  属主.索引名
        ddl.append(" \"").append(indexInfo.getIndexOwner().trim()).append("\".").append(getName(indexInfo.getName())).append(" ON");
        // 表名(索引字段（函数索引字段）列表)
        ddl.append(" \"").append(indexInfo.getTableOwner().trim()).append("\".").append(getName(indexInfo.getTableName())).append("(").append(indexInfo.getIndexCols()).append(")");
        // 索引分片规则或者存储
        ddl.append(buildFragmentString(getIndexFragmentInfo(connection, indexname))).append(";");
        return ddl.toString();
    }

    /**
     * 获取触发器信息
     * @param connection
     * @param triggername
     * @return
     * @throws SQLException
     */
    private static Trigger getTriggerInfo(Connection connection, String triggername) throws SQLException {
        String sql = """
                SELECT tri.trigname,bdy.data as trigbody
                FROM systriggers tri, systrigbody bdy
                WHERE tri.trigid = bdy.trigid
                AND tri.trigname = ?
                AND bdy.datakey IN ('A','D')
                ORDER BY bdy.datakey DESC,bdy.seqno ASC;
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        List<String[]> rows = runner.query(sql, List.of(triggername), rs -> new String[]{
                rs.getString("trigname"),
                rtrimascii0(rs.getString("trigbody"))
        });
        Trigger triggerInfo = null;
        StringBuilder triggerBody = new StringBuilder();
        for (String[] row : rows) {
            if (triggerInfo == null) {
                triggerInfo = new Trigger(row[0]);
            }
            if (row[1] != null) {
                triggerBody.append(row[1]);
            }
        }
        if (triggerInfo != null) {
            triggerInfo.setTriggerBody(triggerBody.toString());
        }
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
        Trigger triggerInfo = getTriggerInfo(connection, triggername);
        if (triggerInfo == null || triggerInfo.getName() == null){
            return "";
        }
        return triggerInfo.getTriggerBody();
    }

    /**
     * 获取序列信息
     * @param connection
     * @param sequencename
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static Sequence getSequenceInfo(Connection connection, String sequencename) throws SQLException {
        String sql = """
                SELECT t.owner AS seqowner,t.tabname AS seqname,seq.start_val AS startval,
                  seq.inc_val AS incval,seq.max_val AS maxval,seq.min_val AS minval,
                  seq.cycle AS iscycle, seq.cache AS cache,seq.order AS isorder, t.flags
                FROM systables t, syssequences seq
                WHERE t.tabid = seq.tabid
                AND t.tabname = ?
                AND t.tabtype = 'Q'
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return runner.queryOne(sql, List.of(sequencename), rs -> {
            Sequence sequenceInfo = new Sequence(rs.getString("seqname"));
            sequenceInfo.setSeqOwner(rs.getString("seqowner"));
            sequenceInfo.setStartVal(rs.getLong("startval"));
            sequenceInfo.setIncVal(rs.getLong("incval"));
            sequenceInfo.setMaxVal(rs.getLong("maxval"));
            sequenceInfo.setMinVal(rs.getLong("minval"));
            sequenceInfo.setIsCycle(rs.getString("iscycle"));
            sequenceInfo.setCache(rs.getInt("cache"));
            sequenceInfo.setIsOrder(rs.getString("isorder"));
            sequenceInfo.setFlags(rs.getInt("flags"));
            return sequenceInfo;
        });
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
        Sequence sequenceInfo = getSequenceInfo(connection, sequencename);
        if (sequenceInfo == null || sequenceInfo.getName() == null){
            return "";
        }
        StringBuilder ddl = new StringBuilder();
        ddl.append("SET ENVIRONMENT SQLMODE '").append(sequenceInfo.getSequenceSqlMode()).append("';\nCREATE SEQUENCE ");
        // owner
        ddl.append("\"").append(sequenceInfo.getSeqOwner().trim()).append("\".");
        // seqname
        ddl.append(getName(sequenceInfo.getName()));
        // start with
        if(sequenceInfo.getStartVal() > 0){
            ddl.append(" START WITH ").append(sequenceInfo.getStartVal());
        }
        // increment by
        ddl.append(" INCREMENT BY ").append(sequenceInfo.getIncVal());
        // maxvalue
        ddl.append(" MAXVALUE ").append(sequenceInfo.getMaxVal());
        // minvalue
        ddl.append(" MINVALUE ").append(sequenceInfo.getMinVal());
        // cycle
        if ("1".equals(sequenceInfo.getIsCycle())){
            ddl.append(" CYCLE");
        }
        // cache
        if (sequenceInfo.getCache() > 0){
            ddl.append(" CACHE ").append(sequenceInfo.getCache());
        } else {
            ddl.append(" NOCACHE");
        }
        // order
        if ("1".equals(sequenceInfo.getIsOrder())){
            ddl.append(" ORDER");
        } else {
            ddl.append(" NOORDER");
        }

        return ddl.append(";").toString();
    }

    /**
     * 获取同义词信息
     * @param connection
     * @param synonymname
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static Synonym getSynonymInfo(Connection connection, String synonymname) throws SQLException {
        String sql = """
                select t1.tabtype AS syntype,t1.owner AS synowner,t1.tabname AS synname,
                    s.servername AS rservername,s.dbname AS rdbname,s.owner AS rowner,s.tabname AS rtabname,
                    syn.owner AS lowner,syn.tabname AS ltabname, t1.flags
                from syssyntable s
                LEFT JOIN systables syn ON s.btabid = syn.tabid, systables t1
                WHERE s.tabid = t1.tabid
                AND t1.tabname = ?
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        return runner.queryOne(sql, List.of(synonymname), rs -> {
            Synonym synonymInfo = new Synonym(rs.getString("synname"));
            synonymInfo.setSynType(rs.getString("syntype"));
            synonymInfo.setSynOwner(rs.getString("synowner"));
            synonymInfo.setRServerName(rs.getString("rservername"));
            synonymInfo.setRDbName(rs.getString("rdbname"));
            synonymInfo.setROwner(rs.getString("rowner"));
            synonymInfo.setRTabName(rs.getString("rtabname"));
            synonymInfo.setLOwner(rs.getString("lowner"));
            synonymInfo.setLTabName(rs.getString("ltabname"));
            synonymInfo.setFlags(rs.getInt("flags"));
            return synonymInfo;
        });
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
        Synonym synonymInfo = getSynonymInfo(connection, synonymname);
        if (synonymInfo == null || synonymInfo.getName() == null){
            return "";
        }
        StringBuilder ddl = new StringBuilder();
        ddl.append("SET ENVIRONMENT SQLMODE '").append(synonymInfo.getSynonymSqlMode()).append("';\nCREATE ");
        // private or public
        if ("P".equals(synonymInfo.getSynType())){   // S = Public synonym, P = Private synonym
            ddl.append("PRIVATE ");
        }
        // owner
        ddl.append("SYNONYM \"").append(synonymInfo.getSynOwner().trim()).append("\".");
        // name
        ddl.append(getName(synonymInfo.getName())).append(" FOR ");
        // 同义词指向remote
        if (synonymInfo.getRTabName() != null){      // 同义远程表
            // remote dbname
            ddl.append(synonymInfo.getRDbName().trim());
            // remote servername, can other db
            if (synonymInfo.getRServerName() != null && ! "".equals(synonymInfo.getRServerName())){
                ddl.append("@").append(synonymInfo.getRServerName().trim());
            }
            // remote table owner
            ddl.append(":\"").append(synonymInfo.getROwner().trim()).append("\".");
            // remote table name
            ddl.append(getName(synonymInfo.getRTabName()));
            // 同义词指向本库
        } else {
            // owner
            ddl.append("\"").append(synonymInfo.getLOwner().trim()).append("\".");
            // table name
            ddl.append(getName(synonymInfo.getLTabName()));
        }
        return ddl.append(";").toString();
    }

    /**
     * 获取视图定义信息
     * @param connection
     * @param viewname
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static View getViewInfo(Connection connection, String viewname) throws SQLException {
        String sql = """
                select v.viewtext as viewtext, t.flags
                from systables t,sysviews v
                where t.tabid = v.tabid
                and t.tabtype = 'V'
                and t.tabname = ?
                order by v.seqno
                """;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);
        List<String[]> rows = runner.query(sql, List.of(viewname), rs -> new String[]{
                rtrimascii0(rs.getString("viewtext")),
                String.valueOf(rs.getInt("flags"))
        });
        StringBuilder viewBody = new StringBuilder();
        int flags = 0;
        for (String[] row : rows) {
            if (row[0] != null) {
                viewBody.append(row[0]);
            }
            flags = Integer.parseInt(row[1]);
        }
        if (viewBody.length() == 0){
            return null;
        }
        View viewInfo = new View(viewname);
        viewInfo.setViewBoday(viewBody.toString());
        viewInfo.setFlags(flags);
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
        View viewInfo = getViewInfo(connection, viewname);
        if (viewInfo == null || viewInfo.getName() == null){
            return "";
        }
        return "SET ENVIRONMENT SQLMODE '" + viewInfo.getViewSqlMode() + "';\n" + viewInfo.getViewBoday();
    }

    /**
     * 获取存储过程、函数信息定义。
     * @param connection
     * @param procname
     * @param delimident
     * @return
     * @throws SQLException
     */
    private static Procedure getProcInfo(Connection connection, String procdefine) throws SQLException {
        String sql;
        StringBuilder procBody = new StringBuilder();
        String[] tmpProc = null;
        String procname = null;
        int numargs = 0;
        String paramtypes = "";
        int exists = 0;
        int procFlags = 0;
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);

        // procdefine: func1(integer,integer)
        tmpProc = procdefine.split("\\(");
        procname = tmpProc[0].trim();
        if (tmpProc.length == 1){
            paramtypes = "";
        } else {
            tmpProc = tmpProc[1].toLowerCase().split("\\)");
            tmpProc = tmpProc[0].split(",");
            numargs = tmpProc.length;
            for(String str : tmpProc){
                paramtypes = paramtypes + "," + str.trim();
            }
            paramtypes = paramtypes.substring(1);
        }

        sql = """
                select count(*) as isexists from sysprocedures where procname = ?
                """;
        Integer existsValue = runner.queryOne(sql, List.of(procname), rs -> rs.getInt(1));
        exists = existsValue == null ? 0 : existsValue;
        if (exists == 0){                 // 不存在
            return null;
        } else if (exists == 1){          //  单个
            sql = """
                    select p.procname,b.seqno,b.data procbody,p.procflags
                    from sysprocedures p, sysprocbody b
                    where p.procid = b.procid
                    and p.procname = ?
                    and p.mode in ('O','o')
                    and b.datakey = 'T'
                    order by b.seqno
                    """;
            List<String[]> rows = runner.query(sql, List.of(procname), rs -> new String[]{
                    rtrimascii0(rs.getString("procbody")),
                    String.valueOf(rs.getInt("procflags"))
            });
            for (String[] row : rows) {
                if (row[0] != null) {
                    procBody.append(row[0]);
                }
                procFlags = Integer.parseInt(row[1]);
            }
        } else if (exists > 1){           // 多个函数
            sql = """
                    select p.procname,b.seqno,b.data procbody,p.procflags
                    from sysprocedures p, sysprocbody b
                    where p.procid = b.procid
                    and p.procname = ?
                    and p.mode in ('O','o')
                    and p.numargs =  ?
                    and rtn_param_out(paramtypes) = ?
                    and b.datakey = 'T'
                    order by b.seqno
                    """;
            List<String[]> rows = runner.query(sql, List.of(procname, numargs, paramtypes), rs -> new String[]{
                    rtrimascii0(rs.getString("procbody")),
                    String.valueOf(rs.getInt("procflags"))
            });
            for (String[] row : rows) {
                if (row[0] != null) {
                    procBody.append(row[0]);
                }
                procFlags = Integer.parseInt(row[1]);
            }
        }
        if (procBody.length() == 0){
            return null;
        }
        Procedure procedureInfo = new Procedure(procname);
        procedureInfo.setProcBoday(procBody.toString().trim());
        procedureInfo.setProcFlags(procFlags);
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
        Procedure procedureInfo = getProcInfo(connection, procdefine);
        if (procedureInfo == null || procedureInfo.getName() == null){
            return "";
        }
        return "SET ENVIRONMENT SQLMODE '" + procedureInfo.getProcSqlMode() + "';\n" + procedureInfo.getProcBoday();
    }

    //added by L3 20260124
    public static String printPackage(Connection connection, String procname) throws SQLException {
        StringBuilder packageString = new StringBuilder();
        SqlRunner runner = new SqlRunner(connection, DEFAULT_QUERY_TIMEOUT_SECONDS);

        //data字段是nchar(256),可能存在空白区域没有空格填充，导致查询出来的数据无法正常解析，使用substr截取目前看没有问题
        //preparedStatement = connection.prepareStatement("select b.procid,substr(b.data,0,length(b.data)) from sysprocedures p, sysprocbody b where p.procid = b.procid and p.mode='O' and retsize=0 and datakey='T' and p.procname=? order by b.procid,b.seqno");
        List<String[]> rows = runner.query(
                """
                select b.procid,b.data
                from sysprocedures p, sysprocbody b
                where p.procid = b.procid
                and p.mode='O'
                and retsize=0
                and datakey='T'
                and p.procname=?
                order by b.procid,b.seqno
                """,
                List.of(procname),
                rs -> new String[]{String.valueOf(rs.getInt(1)), rs.getString(2)}
        );
        int lastProcId = 0;
        int prevProcId = 0;
        for (String[] row : rows){
            lastProcId = Integer.parseInt(row[0]);
            if (prevProcId == 0) {
                prevProcId = lastProcId;
            }
            if (prevProcId != lastProcId) {  //包头与包体之间增加执行
                packageString.append("\n/\n");
            }
            packageString.append(row[1].replaceAll("\u0000", ""));
            prevProcId = lastProcId;
        }
        packageString.append("\n/\n");

        return packageString.toString();
    }

}




