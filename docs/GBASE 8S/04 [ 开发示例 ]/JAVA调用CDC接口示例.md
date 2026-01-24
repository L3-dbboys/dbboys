```java
/*
 ***************************************************************************
 *
 * filename: Main.java
 * Last modified by: L3 2024-01-03
 * support database version: GBase 8s V8.x
 * useage:
 * javac Main.java
 * export CLASSPATH=$CLASSPATH:gbasedbtjdbc_x.x.x_x_xxxxxx.jar
 * java Main
 *
 ***************************************************************************
 */
import com.gbasedbt.jdbc.IfxSmartBlob;
import com.gbasedbt.lang.IfxToJavaType;
import com.gbasedbt.lang.IfxTypes;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

public class Main {
    public static final int CDC_REC_BEGINTX = 1;
    public static final int CDC_REC_COMMTX = 2;
    public static final int CDC_REC_RBTX = 3;
    public static final int CDC_REC_INSERT = 40;
    public static final int CDC_REC_DELETE = 41;
    public static final int CDC_REC_UPDBEF = 42;
    public static final int CDC_REC_UPDAFT = 43;
    public static final int CDC_REC_DISCARD = 62;
    public static final int CDC_REC_TRUNCATE = 119;
    public static final int CDC_REC_TABSCHEMA = 200;
    public static final int CDC_REC_TIMEOUT = 201;
    public static final int CDC_REC_ERROR = 202;
    public static int VARCHAR_DES_LEN=2;  // varchar长度描述符符长度，最新版本为2，低版本为1

    public static HashMap<Integer, CDCMetadataRecord> metaList = new LinkedHashMap<Integer, CDCMetadataRecord>();

    private static String URL_STRING = "jdbc:gbasedbt-sqli://192.168.17.101:9088/syscdcv1:GBASEDBTSERVER=gbase01;IFX_LOCK_MODE_WAIT=10;DBDATE=Y4MD-";
    //单机连接串
    private static String user = "gbasedbt";
    private static String password = "GBase123";
    static Statement stmt;
    static PreparedStatement pstmt;
    static CallableStatement cstmt;
    static ResultSet rs;
    static Connection conn;

    public static void main(String[] args) throws Exception {
        try {
            Class.forName("com.gbasedbt.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("FAILED: failed to load GBase 8s JDBC driver.");
            return;
        }
        try {
            conn = DriverManager.getConnection(URL_STRING, user, password);
        } catch (SQLException e) {
            System.out.println("FAILED: failed to connect: " + e.toString());
            return;
        }

        // 通过JDBC版本，判断对应数据库版本记录varchar长度字节数
        int DBVERSION=3; // 默认JDBC版本
        String jdbcVersion = conn.getMetaData().getDriverVersion();
        DBVERSION=Integer.parseInt(jdbcVersion.substring(0,1));
        VARCHAR_DES_LEN=(DBVERSION==3?2:1);

        stmt = conn.createStatement();
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_opensess(\"gbase01\", 0, 300, 1, 1, 0)");
        rs = cstmt.executeQuery();
        rs.next();
        Integer sessionID = rs.getInt(1);
        if (sessionID < 0) {
            int errCode = rs.getInt(1);
            cstmt.close();
            rs.close();
        }
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_set_fullrowlogging('testdb:gbasedbt.testtab1', 0)");
        cstmt.executeQuery();
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_set_fullrowlogging('testdb:gbasedbt.testtab2', 0)");
        cstmt.executeQuery();
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_set_fullrowlogging('testdb:gbasedbt.testtab3', 0)");
        cstmt.executeQuery();
        stmt.execute("database testdb");
        stmt.execute("drop table if exists testtab1");
        stmt.execute("drop table if exists testtab2");
        stmt.execute("drop table if exists testtab3");
        stmt.execute("create table testtab1(c_smallint smallint,c_integer integer,c_bigint bigint,c_int8 int8,c_serial serial)");
        stmt.execute("create table testtab2(c_serial8 serial8,c_date date,c_boolean boolean,c_char char(20),c_nchar nchar(10),c_varchar varchar(255),c_nvarchar nvarchar(100),c_lvarchar lvarchar)");
        stmt.execute("create table testtab3(c_bigserial bigserial,c_float float,c_smallfloat smallfloat,c_decimal decimal(10,2),c_money money(10,2),c_datetime datetime year to fraction(5))");

        stmt.execute("database syscdcv1");
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_set_fullrowlogging('testdb:gbasedbt.testtab1', 1)");
        cstmt.executeQuery();
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_set_fullrowlogging('testdb:gbasedbt.testtab2', 1)");
        cstmt.executeQuery();
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_set_fullrowlogging('testdb:gbasedbt.testtab3', 1)");
        cstmt.executeQuery();
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_startcapture(?, 0,'testdb:gbasedbt.testtab1','c_smallint,c_integer,c_bigint,c_int8,c_serial',?)");
        cstmt.setInt(1, sessionID);
        cstmt.setInt(2, 1); // 设置tabid
        cstmt.executeQuery();
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_startcapture(?, 0,'testdb:gbasedbt.testtab2','c_serial8,c_date,c_boolean,c_char,c_nchar,c_varchar,c_nvarchar,c_lvarchar',?)");
        cstmt.setInt(1, sessionID);
        cstmt.setInt(2, 2); // 设置tabid
        cstmt.executeQuery();
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_startcapture(?, 0,'testdb:gbasedbt.testtab3','c_bigserial,c_float,c_smallfloat,c_decimal,c_money,c_datetime',?)");
        cstmt.setInt(1, sessionID);
        cstmt.setInt(2, 3); // 设置tabid
        cstmt.executeQuery();
        cstmt = conn.prepareCall("EXECUTE FUNCTION cdc_activatesess(?, 0)");
        cstmt.setInt(1, sessionID);
        cstmt.executeQuery();
        System.out.println("CDC复制开始，sessionID：" + sessionID);
        // 插入数据，产生cdc日志
        stmt.execute("database testdb");
        stmt.execute("insert into testtab1 values(1,2,3,-4611686018427388000,100)");
        stmt.execute("insert into testtab2 values(4611686018427388000,current,'t','chartext','nchartext','varchartext','nvarchartext','lvarchartext')");
        stmt.execute("insert into testtab3 values(4611686018427388000,'9.999999999','8.88888888888',10.8888888888889,5.58,'2023-12-20 15:20:10.98765')");

        // 定义解析需要的变量
        byte[] bytes_per_read = new byte[64 * 1024]; // 每次读取大对象字节数
        int bytes_left_over_in_previous_buf = 0; // 上次解析后，剩余不足一条记录的字节数
        byte[] databuf = new byte[128 * 1024];  // 存放上次未解析的数据+下次读取的大对象字节
        ByteBuffer buffer = ByteBuffer.allocate(128 * 1024);  // 存放databuf，方便解析
        int recs = 0; // 解析记录行数

        // 一直循环读取解析大对象，读取到数据就开始解析
        while (1 == 1) {
            // 如果上次有不足一条记录未解析完的数据，放在databuf的前面位置
            if (bytes_left_over_in_previous_buf > 0) {
                for (int i = 0; i < bytes_left_over_in_previous_buf; i++) {
                    databuf[i] = buffer.get();
                }
            }
            // 创建大对象
            IfxSmartBlob smartBlob = new IfxSmartBlob(conn);
            // 每次读取最多64k数据
            int bytesread = smartBlob.IfxLoRead(sessionID, bytes_per_read, bytes_per_read.length);
            // 读取的数据拼接到上次未解析的数据后面
            System.arraycopy(bytes_per_read, 0, databuf, bytes_left_over_in_previous_buf, bytesread);
            // 数据存放到ByteBuffer对象，方便解析
            buffer = ByteBuffer.wrap(databuf);

            // 如果读取到了数据，数据长度和上次未解析的数据相加，并循环解析，
            // 理论上肯定会读到数据，没有数据会一直等待到TIMEOUT，最后会读到TIMEOUT记录
            if (bytesread > 0) {
                int bytes_in_buf;  // buffer里数据的长度
                bytes_in_buf = bytesread + bytes_left_over_in_previous_buf;
                bytes_left_over_in_previous_buf = 0;

                // 如果buffer里有数据，开始解析
                while (bytes_in_buf > 0) {
                    int hdrsize, payloadsize;
                    hdrsize = buffer.getInt(); //页头第一部分，buffer postion往后移动4 bytes
                    payloadsize = buffer.getInt(); // 页头第二部分，buffer postion往后移动4 bytes

                    // 如果buffer里的数据长度大于等于页头记录的页头长度+内容长度，表示数据包含至少一条记录，开始解析
                    if (bytes_in_buf >= hdrsize + payloadsize) {
                        int payload_type = buffer.getInt(); //页头第三部分，buffer postion往后移动4 bytes
                        int recordType = buffer.getInt();  //页头第四部分，buffer postion往后移动4 bytes
                        int bytes_in_this_rec = hdrsize + payloadsize;
                        if (bytes_in_buf < bytes_in_this_rec)
                            // 66是唯一的类型，如果不是，可能出错了
                            if (payload_type != 66) {
                                System.out.println("未知的记录类型，请检查是否出错！");
                                exit(-1);
                            }

                        // 读取一条记录到recodeBytes，调用函数解析
                        buffer.position(buffer.position() - 16);  // 回到页开始位置
                        byte[] recodeBytes = new byte[bytes_in_this_rec]; // 每次读取大对象字节数
                        buffer.get(recodeBytes);  // 从buffer读取一条记录到recodeBytes
                        process_record(recodeBytes);  // 调用解析函数解析recode
                        // 解析记录结束

                        recs++;  // 总记录数
                        System.out.println("解析总记录数：" + recs);  // 打印总记录数

                        // 解析过的数据，从需要解析的数据长度里减去
                        bytes_in_buf -= bytes_in_this_rec;
                    }
                    // buffer里的数据小于页头里记录一条记录的长度，不解析，放下一次读取后拼接解析
                    else {
                        // 如果不解析数据，buffer位置需要回退到读取页头两个int之前，减去8
                        buffer.position(buffer.position() - 8);
                        bytes_left_over_in_previous_buf = bytes_in_buf;
                        bytes_in_buf = 0;
                    }
                }
                // 内层while循环结束
            }
            // 如果没有读取到数据，肯定是出错了，退出程序
            else {
                System.out.println("读取不到数据，退出程序！");
                exit(-1);
            }
        }
        //外层while循环结束
    }

    // 获取记录类型对应的说明
    public static String get_cdc_rectype_name(Integer rectype_num) {
        String rectype_name = null;
        switch (rectype_num) {
            case CDC_REC_BEGINTX:
                rectype_name = "CDC_REC_BEGINTX";
                break;
            case CDC_REC_COMMTX:
                rectype_name = "CDC_REC_COMMTX";
                break;
            case CDC_REC_RBTX:
                rectype_name = "CDC_REC_RBTX";
                break;
            case CDC_REC_INSERT:
                rectype_name = "CDC_REC_INSERT";
                break;
            case CDC_REC_DELETE:
                rectype_name = "CDC_REC_DELETE";
                break;
            case CDC_REC_UPDBEF:
                rectype_name = "CDC_REC_UPDBEF";
                break;
            case CDC_REC_UPDAFT:
                rectype_name = "CDC_REC_UPDAFT";
                break;
            case CDC_REC_DISCARD:
                rectype_name = "CDC_REC_DISCARD";
                break;
            case CDC_REC_TRUNCATE:
                rectype_name = "CDC_REC_TRUNCATE";
                break;
            case CDC_REC_TABSCHEMA:
                rectype_name = "CDC_REC_TABSCHEMA";
                break;
            case CDC_REC_TIMEOUT:
                rectype_name = "CDC_REC_TIMEOUT";
                break;
            case CDC_REC_ERROR:
                rectype_name = "CDC_REC_ERROR";
                break;
            default:
                break;
        }
        return rectype_name;
    }


    // 解析记录
    public static void process_record(byte[] recodeBytes) throws ParseException {
        ByteBuffer buffer = ByteBuffer.allocate(128 * 1024);
        buffer = ByteBuffer.wrap(recodeBytes);

        // 所有记录通用页头，4个int 16字节
        int hdrsize = buffer.getInt(); //页头第一部分，buffer postion往后移动4 bytes
        int payloadsize = buffer.getInt(); // 页头第二部分，buffer postion往后移动4 bytes
        int payload_type = buffer.getInt(); //页头第三部分，buffer postion往后移动4 bytes
        int recordType = buffer.getInt();  //页头第四部分，buffer postion往后移动4 bytes
        System.out.println("##############################页头##############################");
        System.out.print("页头长度：" + hdrsize);  // 打印记录类型
        System.out.print("，内容长度：" + payloadsize);  // 打印记录类型
        System.out.print("，记录类型：" + payload_type);  // 打印记录类型
        System.out.println("，操作类型：" + get_cdc_rectype_name(recordType));  // 打印记录类型
        System.out.println("---------------------------------------------------------------");

        // 解析内容
        int txn_LSN_high;
        int txn_LSN_low;
        int txn_txnid;
        int txn_userData;
        int txn_time;
        switch (recordType) {
            case CDC_REC_BEGINTX:
            case CDC_REC_COMMTX:
            case CDC_REC_RBTX:
                txn_LSN_high = buffer.getInt();
                txn_LSN_low = buffer.getInt();
                txn_txnid = buffer.getInt();
                txn_userData = buffer.getInt();
                txn_time = buffer.getInt();
                System.out.println("日志号：" + txn_LSN_high);
                //System.out.println("日志位置："+Integer.toHexString(txn_LSN_low));
                System.out.println("日志位置：" + (txn_LSN_low));
                System.out.println("事务号：" + txn_txnid);
                System.out.println("表ID：" + txn_userData);
                // System.out.println("执行时间："+txn_time);
                Date date = new Date();
                date.setTime(txn_time * 1000L);
                System.out.println("执行时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
                break;
            case CDC_REC_INSERT:
            case CDC_REC_DELETE:
            case CDC_REC_UPDBEF:
            case CDC_REC_UPDAFT:
                txn_LSN_high = buffer.getInt();
                txn_LSN_low = buffer.getInt();
                txn_txnid = buffer.getInt();
                txn_userData = buffer.getInt();
                int chg_flag = buffer.getInt();

                System.out.println("日志号：" + txn_LSN_high);
                System.out.println("日志位置：" + (txn_LSN_low));
                System.out.println("事务号：" + txn_txnid);
                System.out.println("表ID：" + txn_userData);
                System.out.println("FLag：" + chg_flag);

                CDCMetadataRecord mtrecode = metaList.get(txn_userData);

                // 读取可变长度列长度
                int[] varcharLen = new int[mtrecode.getVarLengthCols()];
                for (int i = 0; i < mtrecode.getVarLengthCols(); i++) {
                    varcharLen[i] = intByteChange(buffer.getInt());
                }

                // 解析固定长度列
                for (int i = 0; i < mtrecode.getFixedLengthCols(); i++) {
                    String colName = mtrecode.getColNames().get(i);
                    Object colValue = parseFixedLengthCol(buffer, mtrecode.getColTypes().get(colName), mtrecode.getColLengths().get(colName), mtrecode.getColPrecisions().get(colName));
                    String colDef = null;
                    if (mtrecode.getColLengths().get(colName) != null) {
                        if (mtrecode.getColPrecisions().get(colName) != null) {
                            colDef = colName + " " + mtrecode.getColTypes().get(colName) + "(" + mtrecode.getColLengths().get(colName) + "," + mtrecode.getColPrecisions().get(colName) + ")";
                        } else {
                            colDef = colName + " " + mtrecode.getColTypes().get(colName) + "(" + mtrecode.getColLengths().get(colName) + ")";
                        }
                    } else {
                        colDef = colName + " " + mtrecode.getColTypes().get(colName);
                    }
                    if (mtrecode.getColTypes().get(colName).equals("date")) {
                        LocalDate currentDate = LocalDate.parse("1899-12-31"); // 初始化日期
                        LocalDate newDate = currentDate.plusDays((int) colValue); // 加上date值
                        colValue = newDate.toString();
                        //System.out.printf("列定义：%-32s列值：%s\n",colDef,newDate);
                    }
                    System.out.printf("列定义：%-32s列值：%s\n", colDef, colValue);
                }

                // 解析可变长度列
                for (int i = 0; i < mtrecode.getVarLengthCols(); i++) {
                    String colName = mtrecode.getColNames().get(i + mtrecode.getFixedLengthCols());
                    Object colValue = parseVariableLengthCol(buffer, mtrecode.getColTypes().get(colName), varcharLen[i]);
                    String colDef = null;
                    if (mtrecode.getColLengths().get(colName) != null) {
                        if (mtrecode.getColPrecisions().get(colName) != null) {
                            colDef = colName + " " + mtrecode.getColTypes().get(colName) + "(" + mtrecode.getColLengths().get(colName) + "," + mtrecode.getColPrecisions().get(colName) + ")";
                        } else {
                            colDef = colName + " " + mtrecode.getColTypes().get(colName) + "(" + mtrecode.getColLengths().get(colName) + ")";
                        }
                    } else {
                        colDef = colName + " " + mtrecode.getColTypes().get(colName);
                    }
                    System.out.printf("列定义：%-32s列值：%s\n", colDef, colValue);
                }

                break;
            case CDC_REC_DISCARD:
                txn_LSN_high = buffer.getInt();
                txn_LSN_low = buffer.getInt();
                txn_txnid = buffer.getInt();
                System.out.println("日志号：" + txn_LSN_high);
                System.out.println("日志位置：" + (txn_LSN_low));
                System.out.println("事务号：" + txn_txnid);
                break;
            case CDC_REC_TRUNCATE:
                txn_LSN_high = buffer.getInt();
                txn_LSN_low = buffer.getInt();
                txn_txnid = buffer.getInt();
                txn_userData = buffer.getInt();
                System.out.println("日志号：" + txn_LSN_high);
                System.out.println("日志位置：" + (txn_LSN_low));
                System.out.println("事务号：" + txn_txnid);
                System.out.println("表ID：" + txn_userData);
                break;
            case CDC_REC_TABSCHEMA:  // CDC启动首先会写入此记录
                // 记录表meta信息，后面解析insert/update/delete会用到
                CDCMetadataRecord metaRecode = new CDCMetadataRecord(buffer);
                System.out.println("表ID：" + metaRecode.getUserData());
                System.out.println("保留标记：" + metaRecode.getFlags());
                System.out.println("固定长度列总长度：" + metaRecode.getFixedLengthSize());
                System.out.println("固定长度列数：" + metaRecode.getFixedLengthCols());
                System.out.println("可变长度列数：" + metaRecode.getVarLengthCols());
                System.out.println("复制列：" + metaRecode.getColsCreateStmt());

                metaList.put(metaRecode.getUserData(), metaRecode);
                break;
            case CDC_REC_TIMEOUT:
                break;
            case CDC_REC_ERROR:
                break;
            default:
                break;
        }
        System.out.println("##############################页尾##############################");
    }

    // 大小端顺序对换，部分数据默认读取会出现大小端错误
    private static int intByteChange(int inputVal) {
        byte[] bytes = new byte[4];
        bytes[0]= (byte) (inputVal>>24&0xFF);
        bytes[1]= (byte) (inputVal>>16&0xFF);
        bytes[2]= (byte) (inputVal>>8&0xFF);
        bytes[3]= (byte) (inputVal&0xFF);
        return  bytes[3]&0xFF<<24|bytes[2]&0xFF<<16|bytes[1]&0xFF<<8|bytes[0]&0xFF;
    }

    // 解析定长字段
    private static Object parseFixedLengthCol(ByteBuffer payload, String colType, Integer colLength, Integer colPrecision) throws ParseException {

        int ifxType = IfxTypes.FromIfxNameToIfxType(colType.split(" ")[0]);
        byte[] b;
        switch (ifxType) {
            case IfxTypes.IFX_TYPE_SMALLINT:
                return payload.getShort();
            case IfxTypes.IFX_TYPE_INT:
            case IfxTypes.IFX_TYPE_SERIAL:
            case IfxTypes.IFX_TYPE_DATE:
                return payload.getInt();
            case IfxTypes.IFX_TYPE_BIGINT:
            case IfxTypes.IFX_TYPE_BIGSERIAL:
                return payload.getLong();
            case IfxTypes.IFX_TYPE_INT8:
            case IfxTypes.IFX_TYPE_SERIAL8:
                Short flag = payload.getShort();
                byte[] bs_low = new byte[4];
                byte[] bs_high = new byte[4];
                payload.get(bs_low);
                payload.get(bs_high);
                Long int8Value = (long) ((bs_high[0] & 0xffL) << 56 | (bs_high[1] & 0xffL) << 48 | (bs_high[2] & 0xffL) << 40 | (bs_high[3] & 0xffL) << 32 | (bs_low[0] & 0xffL) << 24 | (bs_low[1] & 0xffL) << 16 | (bs_low[2] & 0xffL) << 8 | (bs_low[3] & 0xffL));
                if (flag == -1) int8Value *= -1;
                return int8Value;
            case IfxTypes.IFX_TYPE_BOOL:
                byte isNull = payload.get();
                byte value = payload.get();
                return isNull == 1 ? null : (value == 1?"t":"f");
            case IfxTypes.IFX_TYPE_CHAR:
            case IfxTypes.IFX_TYPE_NCHAR:
                b = new byte[colLength];
                payload.get(b);
                return new String(b).trim();
            case IfxTypes.IFX_TYPE_FLOAT:
                return payload.getDouble();
            case IfxTypes.IFX_TYPE_SMFLOAT:
                return payload.getFloat();
            case IfxTypes.IFX_TYPE_DECIMAL:
            case IfxTypes.IFX_TYPE_MONEY:
                //int colLen=colLength>31?31:colLength;
                int DECLEN = 0;
                if (colPrecision % 2 != 0 || colPrecision == null) {
                    DECLEN = (colLength + 4) / 2; // 先根据列定义计算出实际长度
                } else {
                    DECLEN = (colLength + 3) / 2; // 先根据列定义计算出实际长度
                }
                b = new byte[DECLEN];
                payload.get(b);
                return IfxToJavaType.IfxToJavaDecimal(b, colPrecision != null ? (short) colPrecision.intValue() : (short) 0);
            case IfxTypes.IFX_TYPE_DATETIME:
            case IfxTypes.IFX_TYPE_INTERVAL:
                int DATETIMELEN = 0;
                // 获取的精度为fraction的精度
                if (colLength == null) {
                    switch (colType) {
                        case "datetime year to year":
                            DATETIMELEN = 3;
                            break;
                        case "datetime year to month":
                            DATETIMELEN = 4;
                            break;
                        case "datetime year to day":
                            DATETIMELEN = 5;
                            break;
                        case "datetime year to hour":
                            DATETIMELEN = 6;
                            break;
                        case "datetime year to minute":
                            DATETIMELEN = 7;
                            break;
                        case "datetime year to second":
                            DATETIMELEN = 8;
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (colLength) {
                        case 1:
                        case 2:
                            DATETIMELEN = 9;
                            break;
                        case 3:
                        case 4:
                            DATETIMELEN = 10;
                            break;
                        case 5:
                            DATETIMELEN = 11;
                            break;
                        default:
                            break;
                    }
                }
                b = new byte[DATETIMELEN];
                payload.get(b);
                BigDecimal decimal = IfxToJavaType.IfxToJavaDecimal(b, colPrecision != null ? (short) colPrecision.intValue() : (short) 0);
                String datetimeStr = decimal.toString();
                // System.out.println("decimalStr:"+datetimeStr);

                // 获取日期字符串
                if (colLength == null) {
                    switch (colType) {
                        case "datetime year to year":
                            datetimeStr = datetimeStr.substring(0, 4);
                            break;
                        case "datetime year to month":
                            datetimeStr = datetimeStr.substring(0, 4) + "-" + datetimeStr.substring(4, 6);
                            break;
                        case "datetime year to day":
                            datetimeStr = datetimeStr.substring(0, 4) + "-" + datetimeStr.substring(4, 6) + "-" + datetimeStr.substring(6, 8);
                            break;
                        case "datetime year to hour":
                            datetimeStr = datetimeStr.substring(0, 4) + "-" + datetimeStr.substring(4, 6) + "-" + datetimeStr.substring(6, 8) + " " + datetimeStr.substring(8, 10);
                            break;
                        case "datetime year to minute":
                            datetimeStr = datetimeStr.substring(0, 4) + "-" + datetimeStr.substring(4, 6) + "-" + datetimeStr.substring(6, 8) + " " + datetimeStr.substring(8, 10) + ":" + datetimeStr.substring(10, 12);
                            break;
                        case "datetime year to second":
                            datetimeStr = datetimeStr.substring(0, 4) + "-" + datetimeStr.substring(4, 6) + "-" + datetimeStr.substring(6, 8) + " " + datetimeStr.substring(8, 10) + ":" + datetimeStr.substring(10, 12) + ":" + datetimeStr.substring(12, 14);
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (colLength) {
                        case 1:
                            datetimeStr = datetimeStr.substring(0, 4) + "-" + datetimeStr.substring(4, 6) + "-" + datetimeStr.substring(6, 8) + " " + datetimeStr.substring(8, 10) + ":" + datetimeStr.substring(10, 12) + ":" + datetimeStr.substring(12, 14) + datetimeStr.substring(14, 16);
                            break;
                        case 2:
                            datetimeStr = datetimeStr.substring(0, 4) + "-" + datetimeStr.substring(4, 6) + "-" + datetimeStr.substring(6, 8) + " " + datetimeStr.substring(8, 10) + ":" + datetimeStr.substring(10, 12) + ":" + datetimeStr.substring(12, 14) + datetimeStr.substring(14, 17);
                            break;
                        case 3:
                            datetimeStr = datetimeStr.substring(0, 4) + "-" + datetimeStr.substring(4, 6) + "-" + datetimeStr.substring(6, 8) + " " + datetimeStr.substring(8, 10) + ":" + datetimeStr.substring(10, 12) + ":" + datetimeStr.substring(12, 14) + datetimeStr.substring(14, 18);
                            break;
                        case 4:
                            datetimeStr = datetimeStr.substring(0, 4) + "-" + datetimeStr.substring(4, 6) + "-" + datetimeStr.substring(6, 8) + " " + datetimeStr.substring(8, 10) + ":" + datetimeStr.substring(10, 12) + ":" + datetimeStr.substring(12, 14) + datetimeStr.substring(14, 19);
                            break;
                        case 5:
                            datetimeStr = datetimeStr.substring(0, 4) + "-" + datetimeStr.substring(4, 6) + "-" + datetimeStr.substring(6, 8) + " " + datetimeStr.substring(8, 10) + ":" + datetimeStr.substring(10, 12) + ":" + datetimeStr.substring(12, 14) + datetimeStr.substring(14, 20);
                            break;
                        default:
                            break;
                    }
                }
                return datetimeStr;
            // 获取日期字符串结束
                /*
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSSSS");
                decimalStr = decimalStr.length() > 14 ? decimalStr : decimalStr + ".00000";
                return sdf.parse(decimalStr);

                 */
            default:
                return "";
        }
    }

    // 解析可变长度列
    private static Object parseVariableLengthCol(ByteBuffer payload, String colType, int varcharLen) {

        int ifxType = IfxTypes.FromIfxNameToIfxType(colType.split(" ")[0]);
        switch (ifxType) {
            case IfxTypes.IFX_TYPE_NVCHAR:
            case IfxTypes.IFX_TYPE_VARCHAR:
                byte[] varDesLen=new byte[VARCHAR_DES_LEN]; // 根据数据库版本定义varchar字段长度描述符长度，默认为2，低版本为1
                payload.get(varDesLen);
                int length = 0;
                if(VARCHAR_DES_LEN==2){
                    length=(int)varDesLen[0]&0xFF<<8|varDesLen[1]&0xFF;
                }else{
                    length=(int)varDesLen[0];
                }
                byte[] b = new byte[length];
                payload.get(b);
                return length == 1 && b[0] == '\0' ? null : new String(b);
            case IfxTypes.IFX_TYPE_LVARCHAR:
                byte lb[] = new byte[3];
                payload.get(lb);
                byte[] bt = new byte[varcharLen - 3];  // lvarchar的长度包含3个lvarchar描述符，减去
                payload.get(bt);
                return varcharLen == 1 && bt[0] == '\0' ? null : new String(bt);
            default:
                return "";
        }

    }

}

class CDCMetadataRecord {

    private int userData;
    private int flags;
    private int fixedLengthSize;
    private int fixedLengthCols;
    private int varLengthCols;
    private String colsCreateStmt;
    private Vector<String> colNames;
    private HashMap<String, String> colTypes;
    private HashMap<String, Integer> colLengths;
    private HashMap<String, Integer> colPrecisions;

    public CDCMetadataRecord(ByteBuffer buffer) {
        userData = buffer.getInt();

        // Must be 0
        flags = buffer.getInt();

        fixedLengthSize = buffer.getInt();

        fixedLengthCols = buffer.getInt();

        varLengthCols = buffer.getInt();
        byte[] coldesc = new byte[buffer.remaining()];
        buffer.get(coldesc);
        colsCreateStmt = new String(coldesc);
        parseColsCreateStmt();
    }

    private void parseColsCreateStmt() {
        colNames = new Vector<String>();
        colTypes = new LinkedHashMap<String, String>();
        colLengths = new LinkedHashMap<String, Integer>();
        colPrecisions = new LinkedHashMap<String, Integer>();
        Pattern pattern = Pattern.compile("(\\w+)\\s+(?:([\\w\\s]+)(?:\\(([^)]*)\\))?)[,$]?", Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(this.colsCreateStmt);
        while (matcher.find()) {
            String colName = matcher.group(1);
            colNames.add(colName);
            colTypes.put(colName, matcher.group(2));
            if (matcher.groupCount() > 2 && matcher.group(3) != null) {
                String lenPrec = matcher.group(3);
                if (lenPrec.contains(",")) {
                    colLengths.put(colName, Integer.parseInt(lenPrec.split(",")[0]));
                    colPrecisions.put(colName, Integer.parseInt(lenPrec.split(",")[1]));
                } else {
                    colLengths.put(colName, Integer.parseInt(lenPrec));
                    colPrecisions.put(colName, null);
                }
            } else {
                colLengths.put(colName, null);
                colPrecisions.put(colName, null);
            }
        }
    }

    public boolean hasUserData() {
        return true;
    }

    public int getUserData() {
        return userData;
    }

    public int getFlags() {
        return flags;
    }

    public int getFixedLengthSize() {
        return fixedLengthSize;
    }

    public int getFixedLengthCols() {
        return fixedLengthCols;
    }

    public int getVarLengthCols() {
        return varLengthCols;
    }

    public String getColsCreateStmt() {
        return colsCreateStmt;
    }

    public Vector<String> getColNames() {
        return colNames;
    }

    public HashMap<String, String> getColTypes() {
        return colTypes;
    }

    public HashMap<String, Integer> getColLengths() {
        return colLengths;
    }

    public HashMap<String, Integer> getColPrecisions() {
        return colPrecisions;
    }


}
```