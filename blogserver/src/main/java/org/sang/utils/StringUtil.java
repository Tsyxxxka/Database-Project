package org.sang.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.util.DigestUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

//字符串过滤工具类
@Slf4j
public final class StringUtil {
    private StringUtil(){
    }

    private static String key = "and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+";
    private static Set<String> notAllowedKeyWords = new HashSet<String>(0);
    private static String replacedString="INVALID";
    static {
        String[] keyStr = key.split("\\|");
        for (String str : keyStr) {
            notAllowedKeyWords.add(str);
        }
    }

    /**
     * 防xss攻击
     */
    public static String cleanXSS(String valueBefore) {
        String valueAfter = valueBefore.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        valueAfter = valueAfter.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        valueAfter = valueAfter.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        valueAfter = valueAfter.replaceAll("'", "& #39;");
        valueAfter = valueAfter.replaceAll("eval\\((.*)\\)", "");
        valueAfter = valueAfter.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        valueAfter = valueAfter.replaceAll("script", "");
        return valueAfter;
    }

    /**
     * 防sql注入
     */
    public static String cleanSqlKeyWords(String valueBefore) {
        String valueAfter = valueBefore;
        for (String keyword : notAllowedKeyWords) {
            if (valueAfter.length() > keyword.length() + 4
                    && (valueAfter.contains(" "+keyword)||valueAfter.contains(keyword+" ")||valueAfter.contains(" "+keyword+" "))) {
                valueAfter = StringUtils.replace(valueAfter, keyword, replacedString);
            }
        }
        return valueAfter;
    }

    public static String generateRandomString(int length)
    {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            StringBuffer sb = new StringBuffer();
            for(int i=0; i<length; i++)
            {
                sb.append(String.valueOf(random.nextInt(10)));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("fail to generateRandomString, the detail is:" + e);
            return null;
        }
    }
}

