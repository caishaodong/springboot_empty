package com.dong.empty.global.util;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public static boolean equals(String str1, String str2) {
        if (Objects.isNull(str1) || Objects.isNull(str2)) {
            return false;
        }
        return str1.equals(str2);
    }

    /**
     * Long类型的jsonArray转化为Long类型的list
     *
     * @param str
     * @return
     */
    public static List<Long> jsonArrayToLongList(String str) {
        List<Long> list = new ArrayList<>();
        if (isBlank(str)) {
            return list;
        }
        JSONArray jsonArray = JSONArray.parseArray(str);
        if (Objects.isNull(jsonArray) || jsonArray.isEmpty()) {
            return list;
        }
        jsonArray.stream().forEach(object -> {
            list.add(Long.valueOf(String.valueOf(object)));
        });
        return list;
    }
}
