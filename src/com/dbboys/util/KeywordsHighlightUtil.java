package com.dbboys.util;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordsHighlightUtil {

    public  static StyleSpans<Collection<String>> applyHighlightingInfo(String text) {
        String[] KEYWORDS = new String[] {
                "Instance Boot Information","System Information","Connection Information"
        };
        String[] FUNCTIONS = new String[] {
                "Database Type","JDBC Driver","IP Address","Port","Database User","Driver Properties","Database Version","Connection Name"
                /*
                ,"DBDELIMITER","DBPATH","DBPRINT","DBTEMP","GBASEDBTDIR","GBASEDBTSERVER","GBASEDBTSQLHOSTS",
                "GBASEDBTTERM","ONCONFIG","PATH","TERM","TERMCAP","LANG","LC_COLLATE","LC_CTYPE","LC_MONETARY","LC_NUMERIC",
                "LC_TIME","IGNORE_UNDERFLOW","GL_USEGLU","DB_LOCALE","CLIENT_LOCALE","SERVER_LOCALE","SHELL","LKNOTIFY","LOCKDOWN",
                "NODEFDAC"

                 */
        };
        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();
        //Pattern PATTERN = Pattern.compile("(?i)\\b(select|insert|values|as|update|delete|from|where|group|by|order|in|and|or|exists|not|case|when|then|else|end|into|create|alter|drop|table|index|view)\\b");
        String KEYWORD_PATTERN = "(?i)\\b(" + String.join("|", KEYWORDS) + ")\\b";
        String FUNCTION_PATTERN = "(?i)\\b(" + String.join("|", FUNCTIONS) + ")\\b";
        String PAREN_PATTERN = "\\(|\\)";
        String BRACE_PATTERN = "\\{|\\}";
        String BRACKET_PATTERN = "\\[|\\]";
        String SEMICOLON_PATTERN = "\\;";
        String STRING_PATTERN = "'([^'\\\\]*(\\\\.[^'\\\\]*)*)'"+"|" + "'[\\s\\S]*";
        String DOUBLE_STRING_PATTERN = "\"[^\"]*\""+"|" + "\"[\\s\\S]*";
        String FANYINHAO_STRING_PATTERN = "`[^`]*`"+"|" + "`[\\s\\S]*";
        //String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"+ "|" + "/\\*(.|\\n)*" ; //可能堆栈溢出
        String COMMENT_PATTERN = "#[^\n]*" ;
        Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                        + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
                        + "|(?<FANYINHAO>" + FANYINHAO_STRING_PATTERN + ")"
                        + "|(?<PAREN>" + PAREN_PATTERN + ")"
                        + "|(?<BRACE>" + BRACE_PATTERN + ")"
                        + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                        + "|(?<STRING>" + STRING_PATTERN + ")"
                        + "|(?<DOUBLESTRING>" + DOUBLE_STRING_PATTERN + ")"
                        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        );
        Matcher matcher = PATTERN.matcher(text);
        int lastEnd = 0;
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("FUNCTION") != null ? "function" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("DOUBLESTRING") != null ? "doublestring" :
                    matcher.group("FANYINHAO") != null ? "fanyinhao" :
                    matcher.group("COMMENT") != null ? "comment" : null; /* never happens */
            assert styleClass != null;
            builder.add(Collections.emptyList(), matcher.start() - lastEnd);
            builder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastEnd = matcher.end();
        }
        builder.add(Collections.emptyList(), text.length() - lastEnd);
        StyleSpans<Collection<String>> spans = builder.create();
        return  spans;
        //codeArea.setStyleSpans(0, spans);
    }

    public  static StyleSpans<Collection<String>> applyHighlightingOnlinelog(String text) {
        String[] KEYWORDS = new String[] {
                "Started","On-Line"
        };
        String[] ERRORS = new String[] {
                "err","failed","modified","Stopped","warning","allocated","full","long","down","Died","Aborting","Abort"
        };
        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();
        //Pattern PATTERN = Pattern.compile("(?i)\\b(select|insert|values|as|update|delete|from|where|group|by|order|in|and|or|exists|not|case|when|then|else|end|into|create|alter|drop|table|index|view)\\b");
        String KEYWORD_PATTERN = "(?i)\\b(" + String.join("|", KEYWORDS) + ")\\b";
        String FUNCTION_PATTERN = "(?i)\\b(" + String.join("|", ERRORS) + ")\\b";
        Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                        + "|(?<ERRORS>" + FUNCTION_PATTERN + ")"
        );
        Matcher matcher = PATTERN.matcher(text);
        int lastEnd = 0;
        while (matcher.find()) {
            String styleClass =
                            matcher.group("ERRORS") != null ? "error" :null;
            assert styleClass != null;
            builder.add(Collections.emptyList(), matcher.start() - lastEnd);
            builder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastEnd = matcher.end();
        }
        builder.add(Collections.emptyList(), text.length() - lastEnd);
        StyleSpans<Collection<String>> spans = builder.create();
        return  spans;
        //codeArea.setStyleSpans(0, spans);
    }



    public static StyleSpans<Collection<String>> applyHighlighting(String text) {
        String[] KEYWORDS = new String[] {
                "SELECT", "FROM", "WHERE", "INSERT", "UPDATE","SET", "DELETE", "CREATE", "DROP", "ALTER", "TABLE", "VIEW",
                "INTO", "VALUES", "JOIN", "INNER", "LEFT", "RIGHT", "FULL","IN", "ON", "AND", "OR", "NOT", "NULL", "AS",
                "replace","ENGINE","DEFAULT","InnoDB","CHARSET","index","TO","GROUP","BY","ORDER","HAVING","LIMIT"
                ,"OFFSET","SKIP","FIRST","ROWNUM","ROWID","PRIMARY","key","CHANGE","COLUMN","DISTINCT","BEGIN","END","PROCEDURE"
                ,"DEFINE","CASE","WHEN","THEN","LET","EXECUTE","IMMEDIATE","IF","ELSE","ELIF","WHILE","FOR","TEMP","WITH","NO","LOG"
                ,"LIKE","UNION","ALL","ASC","DESC","EXISTS","ROLLBACK","WORK","COMMIT","environment","sqlmode","pdqpriority","grant",
                "revoke","usage","language","spl","public","row","type","implicit","using","btree", "extent", "size","next"
                ,"lock","mode","check","constraint","raw","external","sameas","datafiles","standard","merge","database","sequence"
                ,"synonym","private","function","TRIGGER","AFTER","BEFORE","return","ENABLED","DISABLED","UNIQUE","TRUNCATE","CALL"
                ,"OUT","INOUT","statistics","low","high","medium","force","FRAGMENT","EXPRESSION","PARTITION","PAGE","COMMENT"
        };
        String[] FUNCTIONS = new String[] {
                "AVG", "SUM", "MAX","MIN","median","stddev","variance","ascii","substr","instr","length","lengthb",
                "lower","upper","ltrim","rtrim","trim","replace","lpad","rpad","to_number","to_date","abs","floor",
                "mod","power","round","sign","sqrt","sysdate","current","add_months","last_day","cast",
                "STR_TO_DATE","INT","INT8","BIGINT","SERIAL","SERIAL8","CHAR","VARCHAR","VARCHAR2","NVARCHAR","NCHAR","NUMBER","DECIMAL","TEXT"
                ,"BYTE","FLOAT","BLOB","CLOB","JSON","DATE","DATETIME","TIMESTAMP","DATE_FORMAT","TO_CHAR","TO_DATE","ROUND"
                ,"FLOOR","concat","SYS_GUID","UUID","COUNT","INTEGER","LVARCHAR","YEAR","MONTH","DAY","HOUR","MINUTE","SECOND","FRACTION"
                ,"sys_refcursor"
        };
        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();
        //Pattern PATTERN = Pattern.compile("(?i)\\b(select|insert|values|as|update|delete|from|where|group|by|order|in|and|or|exists|not|case|when|then|else|end|into|create|alter|drop|table|index|view)\\b");
        String DATETIME_PATTERN="(?i)\\b(year|month|day|hour|minute|second|FRACTION)\\s+to\\s+(year|month|day|hour|minute|second|FRACTION)\\b";
        String KEYWORD_PATTERN = "(?i)\\b(" + String.join("|", KEYWORDS) + ")\\b";
        String FUNCTION_PATTERN = "(?i)\\b(" + String.join("|", FUNCTIONS) + ")\\b";
        String NUMBER_PATTERN = "(?<![a-z])+\\b\\d+(\\.\\d+)?+\\b(?![a-z])";
        String PAREN_PATTERN = "\\(|\\)";
        //String BRACE_PATTERN = "\\{|\\}";
        String BRACKET_PATTERN = "\\[|\\]";
        String SEMICOLON_PATTERN = "\\;";
        String STRING_PATTERN = "'([^'\\\\]*(\\\\.[^'\\\\]*)*)'"+"|" + "'[\\s\\S]*";
        String DOUBLE_STRING_PATTERN = "\"[^\"]*\""+"|" + "\"[\\s\\S]*";
        String FANYINHAO_STRING_PATTERN = "`[^`]*`"+"|" + "`[\\s\\S]*";
        //String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"+ "|" + "/\\*(.|\\n)*" ; //可能堆栈溢出
        String COMMENT_PATTERN = "--[^\n]*" + "|"+"/\\*[\\s\\S]*?\\*/"+"|"+"/\\*[\\s\\S]*" +"|"+"\\{[\\s\\S]*?\\}";//正常，未堆栈溢出

        Pattern PATTERN = Pattern.compile(
                "(?<DATETIME>" + DATETIME_PATTERN + ")"
                +"|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                        + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
                        + "|(?<FANYINHAO>" + FANYINHAO_STRING_PATTERN + ")"
                        + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                        + "|(?<PAREN>" + PAREN_PATTERN + ")"
                        //+ "|(?<BRACE>" + BRACE_PATTERN + ")"
                        + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                        + "|(?<STRING>" + STRING_PATTERN + ")"
                        + "|(?<DOUBLESTRING>" + DOUBLE_STRING_PATTERN + ")"
                        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"

        );
        Matcher matcher = PATTERN.matcher(text);
        int lastEnd = 0;
        while (matcher.find()) {
            String styleClass =
                    matcher.group("DATETIME") != null ? "function" :
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("FUNCTION") != null ? "function" :
                    matcher.group("NUMBER") != null ? "number" :
                    matcher.group("PAREN") != null ? "paren" :
                    // matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("DOUBLESTRING") != null ? "doublestring" :
                    matcher.group("FANYINHAO") != null ? "fanyinhao" :
                    matcher.group("COMMENT") != null ? "comment" : null; /* never happens */
            assert styleClass != null;
            builder.add(Collections.emptyList(), matcher.start() - lastEnd);
            builder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastEnd = matcher.end();
        }
        builder.add(Collections.emptyList(), text.length() - lastEnd);
        StyleSpans<Collection<String>> spans = builder.create();
        return  spans;
        //codeArea.setStyleSpans(0, spans);
    }
}
