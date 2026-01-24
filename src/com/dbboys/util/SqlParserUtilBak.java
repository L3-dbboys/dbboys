package com.dbboys.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParserUtilBak {

    private static final Pattern STRING_PATTERN = Pattern.compile("'([^']|'')*'");
    private static final Pattern LINE_COMMENT_PATTERN = Pattern.compile("--.*?$", Pattern.MULTILINE);
    private static final Pattern BLOCK_COMMENT_PATTERN = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    public static String formatSql(String sql) {
        Map<String, String> placeholders = new HashMap<>();
        int[] index = {0};

        // 1. 保护字符串和注释
        sql = protectPattern(sql, STRING_PATTERN, placeholders, index);
        sql = protectPattern(sql, LINE_COMMENT_PATTERN, placeholders, index);
        sql = protectPattern(sql, BLOCK_COMMENT_PATTERN, placeholders, index);

        //去掉多余空格
        sql=sql.replaceAll("[ \\t]+", " ")    // 连续空格/tab → 单个空格
                .replaceAll("\\s*([(),=<>+*/-])\\s*", " $1 ") // 操作符两边保留一个空格
                .replaceAll("\\s+", " ")       // 再压缩一次多余空格
                .trim();
        // 2. 关键字换行，但不缩进关键字本身
        sql = sql.replaceAll("(?i)\\b(SELECT|FROM|WHERE|ORDER BY|GROUP BY|HAVING|INSERT INTO|VALUES|UPDATE|SET|DELETE)\\b",
                "\n$1");

        // 3. AND/OR 关键字前面缩进一个 Tab
        sql = sql.replaceAll("(?i)\\b(AND|OR)\\b", "\n\t$1");

        // 4. 括号缩进和关键字后内容缩进一个Tab
        sql = indentParenthesesAndKeyword(sql, 1);

        // 5. 去掉多余空行
        sql = sql.replaceAll("\\n\\s*\\n", "\n");

        // 6. 恢复占位符
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }

        return sql.trim();
    }

    private static String protectPattern(String sql, Pattern pattern, Map<String, String> placeholders, int[] index) {
        Matcher matcher = pattern.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String match = matcher.group();
            String placeholder = "__PLACEHOLDER_" + index[0] + "__";
            placeholders.put(placeholder, match);
            matcher.appendReplacement(sb, placeholder);
            index[0]++;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String indentParenthesesAndKeyword(String sql, int tabUnits) {
        StringBuilder result = new StringBuilder();
        Stack<Integer> indentStack = new Stack<>();
        int currentIndent = 0;
        String tab = "\t";

        String[] lines = sql.split("\n");
        for (String line : lines) {
            String trimmed = line.replaceAll("\\s+$", "");;

            if (trimmed.isEmpty()) continue;

            // 如果是关键字
            if (trimmed.matches("(?i)^(SELECT|FROM|WHERE|ORDER BY|GROUP BY|HAVING|INSERT INTO|VALUES|UPDATE|SET|DELETE)$")) {
                result.append(trimmed).append("\n");
                currentIndent = tabUnits;
                result.append(tab.repeat(currentIndent));
                continue;
            }


            // AND/OR 已经缩进处理，直接追加
            for (int i = 0; i < trimmed.length(); i++) {
                char c = trimmed.charAt(i);

                if (c == '(') {

                    if (trimmed.chars().filter(ch -> ch == '\t').count()>0) {
                        currentIndent=(int) trimmed.chars().filter(ch -> ch == '\t').count();
                    }

                    boolean isSubquery = i > 0 && result.toString().toUpperCase().contains("SELECT");
                    if (isSubquery) {
                        result.append("\n").append(tab.repeat(currentIndent)).append(c).append("\n");
                        indentStack.push(currentIndent);
                        currentIndent += tabUnits;
                        result.append(tab.repeat(currentIndent));
                    } else {
                        // 函数调用括号不换行
                        result.append(c);
                        indentStack.push(currentIndent);
                        currentIndent += tabUnits;
                    }
                } else if (c == ')') {
                    if (!indentStack.isEmpty()) {
                        currentIndent = indentStack.pop();
                    }
                    result.append("\n").append(tab.repeat(currentIndent)).append(c);
                } else if (c == ',') {
                    //result.append(",").append(tab.repeat(currentIndent));
                    result.append(",");
                } else {
                    result.append(c);
                }
            }
            result.append("\n");
            result.append(tab.repeat(currentIndent));
        }

        return result.toString();
    }

    public static void main(String[] args) {
        String sql = "SELECT    id,\n   name   , (SELECT    max(   score)   FROM scores WHERE user_id= users.id) AS max_score, " +
                "'hello, world' AS greeting /* block comment */ FROM users /* block comment */ " +
                "WHERE age > 30 AND status = 'active' /* block comment */ ORDER BY name and id in (select a from t where id in (select id from b) and a=b)";

        String formattedSql = formatSql(sql);
        System.out.println(formattedSql);
    }
}
