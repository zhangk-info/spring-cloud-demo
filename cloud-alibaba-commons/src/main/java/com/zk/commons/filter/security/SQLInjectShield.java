package com.zk.commons.filter.security;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLInjectShield {

    public static String SQL_KEY_WORDS = "'|and|exec|execute|insert|select|delete|update|count|drop|%|chr|mid|master|truncate|" +
            "char|declare|sitename|net user|xp_cmdshell|;|or|-|+|,|like'|and|exec|execute|insert|create|drop|" +
            "table|from|grant|use|group_concat|column_name|" +
            "information_schema.columns|table_schema|union|where|select|delete|update|order|by|count|" +
            "chr|mid|master|truncate|char|declare|or|;|-|--|,|like|//|/|%|#";

    public static String KES_SYMBOL = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
    private static Pattern p = Pattern.compile(KES_SYMBOL);

    public static boolean contain(String str) {
        Matcher m = p.matcher(str);
        return m.find();
    }


    public static String shield(String param) {
        if (StringUtils.isBlank(param)) {
            return null;
        }

        // 去掉'|"|;|\字符
        param = StringUtils.replace(param, "'", "");
        param = StringUtils.replace(param, "\"", "");
        param = StringUtils.replace(param, ";", "");
        param = StringUtils.replace(param, "\\", "");
        param = StringUtils.replace(param, "%2c", "");

        String[] values = param.split(" ");
        String[] badStrs = SQL_KEY_WORDS.split("\\|");
        for (int i = 0; i < badStrs.length; i++) {
            for (int j = 0; j < values.length; j++) {
                if (values[j].equalsIgnoreCase(badStrs[i])) {
                    values[j] = "forbid";
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i == values.length - 1) {
                sb.append(values[i]);
            } else {
                sb.append(values[i] + " ");
            }
        }
        param = sb.toString();
        return param;
    }

}
