package com.dong.empty.global.util.decimal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @Author caishaodong
 * @Date 2020-09-10 16:01
 * @Description
 **/
public class DecimalFormatUtil {
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DECIMAL_FORMAT = new DecimalFormat();
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }

    public static String format(String pattern, BigDecimal decimal) {
        DECIMAL_FORMAT.applyPattern(pattern);
        return DECIMAL_FORMAT.format(decimal);
    }

    public static void main(String[] args) {
        System.out.println(format("####.##", new BigDecimal("0012.00")));// 12
        System.out.println(format("0000.00", new BigDecimal("0012.00")));// 0012.00
        System.out.println(format("#00.00", new BigDecimal("0012.00")));// 12.00
        System.out.println(format("000.00", new BigDecimal("0012.00")));// 012.00
        System.out.println(format("#.00", new BigDecimal("0012.00")));// 12.00
        System.out.println(format("0.00", new BigDecimal("0012.00")));// 12.00
    }
}
