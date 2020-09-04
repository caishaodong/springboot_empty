package com.dong.springboot_empty.global.util;

/**
 * @Author caishaodong
 * @Date 2020-08-06 18:09
 * @Description
 **/
public class StringUtil {
    public static boolean isBlank(String str) {
        return "".equals(str) || str == null;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
