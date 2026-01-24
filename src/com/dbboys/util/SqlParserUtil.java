package com.dbboys.util;
import java.util.*;
import java.util.regex.*;

public class SqlParserUtil {
    public static final Pattern STRING_PATTERN = Pattern.compile("'([^'\\\\]*(\\\\.[^'\\\\]*)*)'"+"|" + "'[\\s\\S]*");
    public static final Pattern DOUBLE_STRING_PATTERN = Pattern.compile("\"[^\"]*\""+"|" + "\"[\\s\\S]*");
    public static final Pattern FANYINHAO_STRING_PATTERN = Pattern.compile("`[^`]*`"+"|" + "`[\\s\\S]*");
    public static final Pattern COMMENT_PATTERN = Pattern.compile("--[^\n]*" + "|"+"/\\*[\\s\\S]*?\\*/"+"|"+"/\\*[\\s\\S]*" +"|"+"\\{[\\s\\S]*?\\}");

    public static String formatSql(String sql) {

        Map<String, String> placeholders = new HashMap<>();
        int[] index = {0};

        // 1. 保护字符串和注释
        sql = protectPattern(sql, STRING_PATTERN, placeholders, index);
        sql = protectPattern(sql, DOUBLE_STRING_PATTERN, placeholders, index);
        sql = protectPattern(sql, FANYINHAO_STRING_PATTERN, placeholders, index);
        sql = protectPattern(sql, COMMENT_PATTERN, placeholders, index);
        // 1. 去掉多余空格

        // 3. 用栈处理括号缩进
        StringBuilder result = new StringBuilder();
        Stack<Integer> indentStack = new Stack<>();
        int currentIndent = 0;
        //String tab = "\t";
        String tab = "    ";


        String[] sqls = sql.split("(?<=;)");
        for(String subsql:sqls){
            String checkSql=subsql.trim().replaceAll("[ \\t]+", " ")    // 连续空格/tab → 单个空格
                    .replaceAll("\\s*([=<>+*/-])\\s*", " $1 ") // 操作符两边保留一个空格
                    .replaceAll("\\s+", " ")       // 再压缩一次多余空格
                    .trim();
            //去掉(后面及)前面的空格
            checkSql = checkSql.replaceAll("(?i)(\\()\\s+", "$1").replaceAll("(?i)\\s+(\\))", "$1");
            //去掉两个符合之间的空格
            checkSql = checkSql.replaceAll("([=<>+*/-]) ([=<>+*/-])", "$1$2");

            if(checkSql.toLowerCase().startsWith("select ")
                    || checkSql.toLowerCase().startsWith("with ")
                    || checkSql.toLowerCase().startsWith("create table ")
                    || checkSql.toLowerCase().startsWith("create view ")
                    || checkSql.toLowerCase().startsWith("create index ")
                    || checkSql.toLowerCase().startsWith("create sequence ")
                    || checkSql.toLowerCase().startsWith("create trigger ")
                    || checkSql.toLowerCase().startsWith("alter table ")
                    || checkSql.toLowerCase().startsWith("alter fragment ")
                    || checkSql.toLowerCase().startsWith("rename ")
                    || checkSql.toLowerCase().startsWith("commnet on ")
                    || checkSql.toLowerCase().startsWith("delete from ")
                    || checkSql.toLowerCase().startsWith("drop ")
                    || checkSql.toLowerCase().startsWith("insert ")
                    || checkSql.toLowerCase().startsWith("update ")
                    || checkSql.toLowerCase().startsWith("delete ")
                    || checkSql.toLowerCase().startsWith("truncate ")
            ){
                subsql=checkSql;
                String[] tokens = subsql.split("(?=\\()|(?<=\\))"); // 按括号拆分
                String appendSql="";
                for (String token : tokens) {
                    if (token.isEmpty()) continue;
                    if (token.matches("(?i)^\\(\\s*select[\\s\\S]+")||(token.contains("(")&&!token.contains(")"))) {
                        currentIndent++;
                        indentStack.push(currentIndent);
                        token=token.replaceAll("(?i)(__PLACEHOLDER_COMMENT_[0-9]+__)\n?\\s*","\n"+tab.repeat(currentIndent+1)+"$0\n"+tab.repeat(currentIndent+1));
                        token="\n"+tab.repeat(currentIndent)+"("+"\n"+tab.repeat(currentIndent)+token.substring(1).trim();
                        token=token.replaceAll("\\s*,\\s*",",\n"+tab.repeat(currentIndent+1)).replaceAll("(?i)\\b(FROM|LEFT JOIN|WHERE|GROUP BY|ORDER BY|JOIN|RIGHT JOIN|UNION)\\b","\n"+tab.repeat(currentIndent)+"$0").replaceAll("(?i)\\b(AND|OR|ON|HAVING)\\b","\n"+tab.repeat(currentIndent+1)+"$0");
                        token=token.replaceAll(";",";\n");
                        if(token.contains(")")){
                            indentStack.pop();
                            currentIndent--;
                            token=token.replaceAll("\\)","\n"+tab.repeat(currentIndent+1)+")");
                            token=token.replaceAll(";",";\n");
                        }
                    } else if (token.contains(")")&&!token.contains("(")) {
                        token=token.replaceAll("(?i)(__PLACEHOLDER_COMMENT_[0-9]+__)\n?\\s*","\n"+tab.repeat(currentIndent+1)+"$0\n"+tab.repeat(currentIndent+1));
                        //token="\n"+tab.repeat(currentIndent)+token.replaceAll("\\)","\n"+tab.repeat(currentIndent)+")");
                        token=token.replaceAll("\\)","\n"+tab.repeat(currentIndent)+")");
                        token=token.replaceAll("\\s*,\\s*",",\n"+tab.repeat(currentIndent+1)).replaceAll("(?i)\\b(FROM|LEFT JOIN|WHERE|GROUP BY|ORDER BY|JOIN|RIGHT JOIN|UNION)\\b","\n"+tab.repeat(currentIndent)+"$0").replaceAll("(?i)\\b(AND|OR|ON|HAVING)\\b","\n"+tab.repeat(currentIndent+1)+"$0");
                        token=token.replaceAll(";",";\n");
                        if (!indentStack.isEmpty()) {
                            currentIndent = indentStack.pop();
                            currentIndent--;
                        }
                    } else {
                        //token=" "+token;
                        token = token.replaceAll("(?i)(__PLACEHOLDER_COMMENT_[0-9]+__)\n?\\s*", "\n" + tab.repeat(currentIndent + 1) + "$0\n"+tab.repeat(currentIndent + 1));
                        if(token.contains("(")&&token.contains(")")){
                            token=token.replaceAll("\\s*,\\s*",",").replaceAll("(?i)\\b(FROM|LEFT JOIN|WHERE|GROUP BY|ORDER BY|JOIN|RIGHT JOIN|UNION)\\b","\n"+tab.repeat(currentIndent)+"$0").replaceAll("(?i)\\b(AND|OR|ON|HAVING)\\b","\n"+tab.repeat(currentIndent+1)+"$0");
                        }else {
                            token = token.replaceAll("\\s*,\\s*", ",\n" + tab.repeat(currentIndent + 1)).replaceAll("(?i)\\b(FROM|LEFT JOIN|WHERE|GROUP BY|ORDER BY|JOIN|RIGHT JOIN|UNION)\\b", "\n" + tab.repeat(currentIndent) + "$0").replaceAll("(?i)\\b(AND|OR|ON|HAVING)\\b", "\n" + tab.repeat(currentIndent + 1) + "$0");
                        }
                        token=token.replaceAll(";",";\n");
                    }
                    appendSql+=token;
                }

                //视图as select换行
                appendSql = appendSql.replaceAll("(?i)(as\\s+)(select\\s+)", "$1\n$2");

                //union后换行
                appendSql = appendSql.replaceAll("(?i)(\\s*)(union\\s+\\w*\\s*)(select\\s+)", "$1$2\n$1$3");
                //去掉函数括号的缩进
                appendSql = appendSql.replaceAll("(?i)(\\w+)\\t+\\(", "$1(");

                //)后面的select换行,主要用于with as语句后的select
                appendSql = appendSql.replaceAll("(?i)(\\s*)\\)\\s*(\\bselect\\b\\s+)", "$1)\n"+"$2");
                //(后面首行缩进调整
                appendSql = appendSql.replaceAll("(?i)(\\(\\n)(\\s+(?!select\\b)\\w+)", "$1"+tab+"$2");
                result.append(appendSql);
            }else{
                result.append("\n"+subsql);
            }
        }
        sql=result.toString();
        sql=sql.replaceAll(";", ";\n");
        sql=sql.replaceAll("\\n\\s*\\n", "\n") // 去掉多余空行
                .trim();


        //把括号后面的别名放到括号后面，避免对不齐
        //sql = sql.replaceAll("(?i)\\)\\s+([as ]?\\w+\\s+,?)", ") $1");
        //sql = sql.replaceAll("(?i)\\)\\s+([as ]?\\w+\\s+)\\s+(FROM|,)", ") $1");
        //sql = sql.replaceAll("(?i)\\)\\s+((as )?\\w+\\s+(,|FROM))", ") $1");



        //恢复注释和字符串
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }

        return sql;

    }


    public static String upperSql(String sql) {
        Map<String, String> placeholders = new HashMap<>();
        int[] index = {0};
        sql = protectPattern(sql, STRING_PATTERN, placeholders, index);
        sql = protectPattern(sql, DOUBLE_STRING_PATTERN, placeholders, index);
        sql = protectPattern(sql, FANYINHAO_STRING_PATTERN, placeholders, index);
        sql = protectPattern(sql, COMMENT_PATTERN, placeholders, index);
        sql=sql.toUpperCase();
        //恢复注释和字符串
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }
        return sql;
    }

    public static String lowerSql(String sql) {
        Map<String, String> placeholders = new HashMap<>();
        int[] index = {0};
        sql = protectPattern(sql, STRING_PATTERN, placeholders, index);
        sql = protectPattern(sql, DOUBLE_STRING_PATTERN, placeholders, index);
        sql = protectPattern(sql, FANYINHAO_STRING_PATTERN, placeholders, index);
        sql = protectPattern(sql, COMMENT_PATTERN, placeholders, index);
        sql=sql.toLowerCase().replaceAll("__placeholder_","__PLACEHOLDER_");
        //恢复注释和字符串
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }
        return sql;
    }
    private static String protectPattern(String sql, Pattern pattern, Map<String, String> placeholders, int[] index) {
        Matcher matcher = pattern.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String placeholder="";
            String match="";
            if(pattern.equals(COMMENT_PATTERN)){
                placeholder = "__PLACEHOLDER_COMMENT_" + index[0] + "__";
                match = matcher.group();
            }else{
                placeholder = "__PLACEHOLDER_" + index[0] + "__";
                match = matcher.group();
            }
            placeholders.put(placeholder, match);
            matcher.appendReplacement(sb, placeholder);
            index[0]++;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    public static void main(String[] args) {
        String sql = """
                
                select count(*),a,b,c,max(aaa)
                  from (select distinct a.business_key as businessKey,
                                        a.wfinstcode as wfinstCode,
                                        b.wfinstcode as wfinstCodef,
                                        a.wfinstname as wfinstName,
                                        a.req_type as status, ( select count(*),* from dual)
                                        a.node_name as name,
                                        case
                                          when u7.username is not null then
                                           u7.username || '|' || a.allname
                                          else
                                           a.allname
                                        end as username7,
                                        case
                                          when a.implement_type = '1' then
                                           '厂商'
                                          when a.implement_type = '0' then
                                           '自主开发'
                                        END as type,
                                        u3.username as username1,
                                        u4.fullpath as orgname1,
                                        a.req_createdate,
                                        u0.username as username2,
                                        u2.orgname as orgname2,
                                        u5.username as username3,
                                        u6.orgname as orgname3,
                                        '是' as ntype,
                                                                gbase_to_date(a.time1, '%Y-%m-%d %H:%M:%S') as time1,
                                        gbase_to_date(a.time2, '%Y-%m-%d %H:%M:%S') as time2,
                                        gbase_to_date(a.time3, '%Y-%m-%d %H:%M:%S') as time3,
                                        gbase_to_date(a.time4, '%Y-%m-%d %H:%M:%S') as time4,
                                        gbase_to_date(a.time5, '%Y-%m-%d %H:%M:%S') as time5,
                                        gbase_to_date(a.time6, '%Y-%m-%d %H:%M:%S') as time6,
                                        gbase_to_date(a.time7, '%Y-%m-%d %H:%M:%S') as time7,
                                        gbase_to_date(a.time8, '%Y-%m-%d %H:%M:%S') as time8,
                                        gbase_to_date(a.time9, '%Y-%m-%d %H:%M:%S') as time9,
                 nvl(a.time10,(select\s
                                           max(ac4.start_time_)
                                      from act_hi_procinst ac3
                                      left join act_hi_actinst ac4
                                        on ac4.proc_inst_id_ = ac3.proc_inst_id_
                                       and ac4.act_id_ = 'dev_test'
                                     where ac3.business_key_=a.business_key)) as time10,
                                        nvl(a.time11, (select\s
                                           max(ac4.start_time_)
                                      from act_hi_procinst ac3
                                      left join act_hi_actinst ac4
                                        on ac4.proc_inst_id_ = ac3.proc_inst_id_
                                       and ac4.act_id_ = 'dev_test'
                                     where ac3.business_key_=a.business_key)) as time11,
                
                                        gbase_to_date(a.time12, '%Y-%m-%d %H:%M:%S') as time12,
                                        gbase_to_date(a.closetime, '%Y-%m-%d %H:%M:%S') as time13,
                                        ii.attrvalue as surveydate,
                                        u8.username as username8,
                                        a.accesstime as accesstime,
                                        a.breqmanagername as username9,
                                        a.dev_date as devdate,
                                       -- cc.plandate as plandate,
                                                           (select a,b,c,d,
                                           max(ar.attrvalue) plandate
                                      from tb_comm_online ol,aaa bb,ccc d
                                      left join tb_bpm_instanceattr ar
                                        on ar.business_key = ol.businesskey
                                       and ar.attrcode = 'plandate'
                                    where ol.swbusinesskey = a.business_key) as plandate,
                                        a.demandsources as demandsources,
                                        a.develop as developer
                          from TB_RPT_REQ_BASE_INFO a
                          left join tb_bpm_instance b
                            on b.business_key = a.top_busikey
                          left join tb_comm_user u0
                            on a.req_manager = u0.userid
                          left join tb_comm_organization u2
                            on u0.orgcode = u2.orgcode
                          left join tb_comm_user u3
                            on b.createuserid = u3.userid
                          left join tb_comm_organization u4
                 on  u3.orgcode = CASE
                        WHEN u3.orgcode LIKE 'JL000%' THEN SUBSTR(u3.orgcode, 1, 8)
                        ELSE SUBSTR(u3.orgcode, 1, 5)
                    END AND u4.orgcode = u3.orgcode
                                          \s
                
                          left join tb_comm_user u5
                            on a.leanman = u5.userid
                          left join tb_comm_organization u6
                            on u5.orgcode = u6.orgcode
                          left join tb_comm_user u7
                            on u7.userid = a.assignee
                          left join tb_bpm_instanceattr ii
                            on ii.business_key = a.business_key
                           and ii.attrcode = 'surveydate'
                          left join tb_comm_user u8
                            on u8.userid = ii.createuserid
                         where b.wfinstcode is not null
                     )
                """;
        sql= """
                select * from t
                where id in(
                select1 id,  --这是id --换一行
                --sjfdlskj
                --adssf
                lksajdlf,aaa
                from tt
  /*kslsj
        safdsa        
    sdfsa            
                */
                )
                """;
    }
}
