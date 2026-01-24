package com.dbboys.service;

import com.dbboys.app.Main;
import com.dbboys.vo.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlparserService {
    private String STRING_PATTERN = "'([^'\\\\]*(\\\\.[^'\\\\]*)*)'"+"|" + "'[\\s\\S]*";
    private String DOUBLE_STRING_PATTERN = "\"[^\"]*\""+"|" + "\"[\\s\\S]*";
    private String FANYINHAO_STRING_PATTERN = "`[^`]*`"+"|" + "`[\\s\\S]*";
    //String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"+ "|" + "/\\*(.|\\n)*" ; //可能堆栈溢出
    private String COMMENT_PATTERN = "--[^\n]*" + "|"+"/\\*[\\s\\S]*?\\*/"+"|"+"/\\*[\\s\\S]*" +"|"+"\\{[\\s\\S]*?\\}";//正常，未堆栈溢出
    private String MULTI_LINE_START=
            //gbase创建存储过程或函数
            "(?i)\\bcreate\\s+\\bprocedure\\s+(if\\s+not\\s+exists\\s+)?([a-zA-Z0-9_$.\"]*)\\s*\\([\\s\\S]*\\)\\s+(?!(as|is)\\b)"+"|"+
            "(?i)\\bcreate\\s+\\bfunction\\s+(if\\s+not\\s+exists\\s+)?([a-zA-Z0-9_$.\"]*)\\s*\\([\\s\\S]*\\)\\s+\\bRETURNING\\s+[a-zA-Z_][a-zA-Z0-9_$.]*\\s+(?!(as|is)\\b)"+"|"+
            //oracle 创建包或存储过程函数
            "(?i)\\bcreate\\s+(OR\\s+REPLACE\\s+)?(package|procedure)\\s+(body\\s+)?([a-zA-Z_][a-zA-Z0-9_$.]*)\\s*(\\([\\s\\S]*?\\)\\s+)?(AS|IS)"+"|"+
            "(?i)\\bcreate\\s+(OR\\s+REPLACE\\s+)?(function)\\s+([a-zA-Z0-9_$.]*)\\s*\\([\\s\\S]*?\\)\\s+return\\s+([a-zA-Z0-9_$.]*)\\s+(AS|IS)";
            //匿名块
    private String NO_NAME_BLOCK="(?i)^\\s*\\b(begin)(?!\\s*(;|work))|(?i)^\\s*\\b(DECLARE)(?!\\s*;)";
    private String MULTI_LINE_END=
            //gbase多行结束
            "(?i)\\bend\\s+(procedure|function)\\s*;?"+"|"+  //不能判断end开头，如果只有一个语句就结束，并非是end开头
            //oracle多行结束
            "(?i)\\bend\\b\\s+([a-zA-Z_][a-zA-Z0-9_$.]*)?\\s*/"+"|"+
            "^\\s*/";  //单行/提交
    private String DROP_DATABASE="(?i)(?:drop\\s+)+database\\s+(\\w+)";
    private String CREATE_DATABASE="(?i)(?:create\\s+)?database\\s+(?<dbname>(\\w+))";
    private Pattern pattern;
    private Matcher matcher;
    

    public Sql modifySql(Sql sql,String add_sql){
        if (!sql.getSqlRemainder().trim().isEmpty()){
            add_sql=sql.getSqlRemainder()+add_sql;
            sql.setSqlRemainder("");
        }
        sql.setSqlEnd(false);

        pattern=Pattern.compile(
                STRING_PATTERN + "|" + DOUBLE_STRING_PATTERN + "|" + COMMENT_PATTERN
        );
        Matcher matcherAll = pattern.matcher(add_sql);
        String check_text=add_sql.trim();

        if(matcherAll.find()){
            check_text = matcherAll.replaceAll("").trim();
        }

        //单条sql
        if (sql.getSqlstr().isEmpty()) {
            if (check_text.toUpperCase().startsWith("SELECT") || check_text.toUpperCase().startsWith("WITH")) {
                //如果包含into则不是select
                pattern = Pattern.compile("(?i)\\binto\\b");
                matcher = pattern.matcher(check_text);
                if (matcher.find()) {
                    sql.setSqlType("SELECT_INTO");
                } else {
                    sql.setSqlType("SELECT");
                }
                sql.setSqlEnd(true);
            }else if(check_text.toUpperCase().startsWith("CALL") || check_text.toUpperCase().startsWith("EXECUTE")){
                sql.setSqlType("CALL");
                sql.setSqlEnd(true);
            }
            else{
                //判断是否存储过程开头
                pattern=Pattern.compile(
                        STRING_PATTERN + "|" + DOUBLE_STRING_PATTERN + "|" + COMMENT_PATTERN
                                + "|(?<START>" + MULTI_LINE_START + ")"
                );
                matcher = pattern.matcher(add_sql);
                while (matcher.find()) {
                    if (matcher.group("START") != null) {
                        sql.setSqlType("MULTI_LINE_SQL");
                        break;
                    }
                };

                //判断是否匿名块
                pattern=Pattern.compile(
                        STRING_PATTERN + "|" + DOUBLE_STRING_PATTERN + "|" + COMMENT_PATTERN
                                + "|(?<BLOCK>" + NO_NAME_BLOCK + ")"
                );
                matcher = pattern.matcher(add_sql);
                while (matcher.find()) {
                    if (matcher.group("BLOCK") != null) {
                        sql.setSqlType("CALL_BLOCK");
                        //add_sql="CALL "+add_sql;
                        break;
                    }
                };

                //判断是否在一条语句内结束
                pattern=Pattern.compile(
                        STRING_PATTERN + "|" + DOUBLE_STRING_PATTERN + "|" + COMMENT_PATTERN
                                + "|(?<END>" + MULTI_LINE_END + ")"
                );
                matcher = pattern.matcher(add_sql);
                while (matcher.find()) {
                    if (matcher.group("END") != null) {
                        sql.setSqlRemainder(add_sql.substring(matcher.end("END")));
                        add_sql=add_sql.substring(0,matcher.end("END")-1);
                        sql.setSqlEnd(true);
                        break;
                    }
                };

                //如果不是创建存储过程或包，sql为单条，标记结束
                if(!sql.getSqlType().equals("MULTI_LINE_SQL")&&!sql.getSqlType().equals("CALL_BLOCK")){
                    //单条语句，标记为已结束
                    sql.setSqlEnd(true);
                    //判断是否为建库语句，如果是，设置语句类型为切库语句
                    pattern=Pattern.compile(
                            STRING_PATTERN + "|" + DOUBLE_STRING_PATTERN + "|" + COMMENT_PATTERN +"|" +DROP_DATABASE
                                    + "|" + CREATE_DATABASE
                    );
                    matcher = pattern.matcher(add_sql);
                    while (matcher.find()) {
                        if (matcher.group("dbname") != null) {
                            sql.setSqlType("DATABASE " + matcher.group("dbname").toLowerCase());
                        }
                    }
                }


            }
            //第一次的语句，直接设置
            sql.setSqlStr(add_sql);

        //后续，如果不是第一个语句，那就是多语句
        }else{
            //存储过程判断是否结束
            if(sql.getSqlType().equals("MULTI_LINE_SQL")||sql.getSqlType().equals("CALL_BLOCK")){
                pattern=Pattern.compile(
                        STRING_PATTERN + "|" + DOUBLE_STRING_PATTERN + "|" + COMMENT_PATTERN
                                + "|(?<END>" + MULTI_LINE_END + ")"
                );
                matcher = pattern.matcher(add_sql);
                while (matcher.find()) {
                    if(matcher.group("END")!=null){
                        sql.setSqlRemainder(add_sql.substring(matcher.end("END")));
                        add_sql=add_sql.substring(0,matcher.end("END")-1);
                        sql.setSqlEnd(true);
                        break;
                    }
                }
                sql.setSqlStr(sql.getSqlstr()+add_sql);
            }

        }

        return sql;
    }

    public Boolean sqlContrainCommit(String remainderSql){
        Boolean result = false;
        pattern=Pattern.compile(
                STRING_PATTERN + "|" + DOUBLE_STRING_PATTERN + "|" + COMMENT_PATTERN
                        + "|(?<END>" + MULTI_LINE_END + ")"
        );
        matcher = pattern.matcher(remainderSql);
        while (matcher.find()) {
            if(matcher.group("END")!=null)
            result=true;
            break;
        }


        if(!result){
            Boolean contrainBegin=false;
            pattern=Pattern.compile(
                    STRING_PATTERN + "|" + DOUBLE_STRING_PATTERN + "|" + COMMENT_PATTERN
                            + "|(?<START>" + MULTI_LINE_START + ")"
            );
            matcher = pattern.matcher(remainderSql);
            while (matcher.find()) {
                if(matcher.group("START")!=null)
                    contrainBegin=true;
                break;
            }
            if(!contrainBegin&&!remainderSql.trim().isEmpty()){
                result=true;
            }
        }

        return  result;
    }

    public Boolean sqlContrainMoreThanOneCommit(String remainderSql){
        Boolean result = false;
        pattern=Pattern.compile(
                STRING_PATTERN + "|" + DOUBLE_STRING_PATTERN + "|" + COMMENT_PATTERN
                        + "|(?<END>" + MULTI_LINE_END + ")"
        );
        int count = 0;
        matcher = pattern.matcher(remainderSql);
        while (matcher.find()) {
            if(matcher.group("END")!=null)
            count++;
            if(count>1)
            result=true;
        }
        return  result;
    }
    public String getFromTable(String sql){
        String fromTable=null;
        Pattern PATTERN = Pattern.compile(
                "(?<STRING>" + STRING_PATTERN + ")"
                        + "|(?<DOUBLESTRING>" + DOUBLE_STRING_PATTERN + ")"
                        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        );
        Matcher matcherAll = PATTERN.matcher(sql);
        String check_text=sql.trim();

        if(matcherAll.find()){
            check_text = matcherAll.replaceAll("").trim();
        }

        // 移除所有配对的括号
        while (check_text.contains("(")) {
            check_text = check_text.replaceAll("\\([^()]*\\)", "");
        }
        Pattern pattern;
        Matcher matcher;
        pattern= Pattern.compile("(?i)\\b(WHERE|ORDER|GROUP)\\b.*",Pattern.DOTALL);
        matcher = pattern.matcher(check_text);
        if(matcher.find()){
            check_text = matcher.replaceAll("").trim();
        }

        pattern = Pattern.compile("(?i)\\bfrom\\b\\s+(\\S+)(?!.*,|.*\\bjoin\\b.*)",Pattern.DOTALL);
        matcher = pattern.matcher(check_text);
        if(matcher.find()){
            fromTable=matcher.group(1).replaceAll(";","");
        }
        return fromTable;
    }


    public List getSelectedCols(String sql,List cols){
        List selectedCols=null;
        // 正则表达式，匹配 SELECT 和不在括号内的 FROM 之间的内容
        String regex = "(?i)SELECT\\s+(.*?)\\s+FROM(?![^(]*\\))";

        Pattern pattern = Pattern.compile(regex,Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);
        String selectContent=null;
        if (matcher.find()) {
            // 获取匹配的内容
            selectContent = matcher.group(1).trim();
        } else {
        }

        String input = selectContent;

        // 使用 StringBuilder 存储分割后的结果
        List<String> result = new ArrayList<>();

        // 临时存储当前正在处理的部分
        StringBuilder currentPart = new StringBuilder();

        // 跟踪括号的嵌套层级
        int parenthesesLevel = 0;

        // 遍历输入字符串
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            // 如果是左括号，增加嵌套层级
            if (ch == '(') {
                parenthesesLevel++;
                currentPart.append(ch);
            }
            // 如果是右括号，减少嵌套层级
            else if (ch == ')') {
                parenthesesLevel--;
                currentPart.append(ch);
            }
            // 如果是逗号且不在括号内，则进行分割
            else if (ch == ',' && parenthesesLevel == 0) {
                result.add(currentPart.toString().replaceAll("(?i)\\b+AS\\b(?s).*", "").trim().replaceAll("\\s[^\\s]*$", "").toLowerCase());
                currentPart.setLength(0);  // 清空当前部分
            }
            else {
                // 其他字符添加到当前部分
                currentPart.append(ch);
            }
        }

        // 添加最后一部分（没有逗号的部分）
        if (currentPart.length() > 0) {
            //currentPart.toString().replaceAll("(?i)\\b+AS\\b.*", "");
            result.add(currentPart.toString().replaceAll("(?i)\\b+AS\\b(?s).*", "").trim().replaceAll("\\s[^\\s]*$", "").toLowerCase());
        }
        for(int i=0;i<result.size();i++){
            if(result.get(i).trim().equals("*")){
                result.remove(i);
                result.addAll(i,cols);
            }
        }
        return result;
    }
}
