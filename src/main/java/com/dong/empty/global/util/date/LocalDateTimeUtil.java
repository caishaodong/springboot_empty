package com.dong.empty.global.util.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author caishaodong
 * @Date 2020-09-11 16:08
 * @Description
 **/
public class LocalDateTimeUtil {
    public static final String YEAR = "YEAR";
    public static final String MONTH = "MONTH";
    public static final String DAY = "DAY";

    /**
     * 格式化日期时间
     *
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static String formatDateTimeByPattern(LocalDateTime localDateTime, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

    /**
     * 获取今日日期（yyyyMMdd）
     *
     * @return
     */
    public static Long getLongToday() {
        String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        return Long.parseLong(today);
    }

    /**
     * 获取昨日日期（yyyyMMdd）
     *
     * @return
     */
    public static Long getLongYesterday() {
        String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now().plusDays(-1));
        return Long.parseLong(today);
    }

    /**
     * 获取本周一日期（yyyyMMdd）
     *
     * @return
     */
    public static Long getLongThisMonday() {
        LocalDate thisMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        String thisMondayStr = DateTimeFormatter.ofPattern("yyyyMMdd").format(thisMonday);
        return Long.parseLong(thisMondayStr);
    }

    /**
     * 获取上周一日期（yyyyMMdd）
     *
     * @return
     */
    public static Long getLongLastMonday() {
        LocalDate thisMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate lastMonday = thisMonday.plusDays(-7);
        String lastMondayStr = DateTimeFormatter.ofPattern("yyyyMMdd").format(lastMonday);
        return Long.parseLong(lastMondayStr);
    }

    /**
     * 获取日期中的年月日
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Integer getByLocalDatePattern(String date, String pattern, String type) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        LocalDate localDateTime = LocalDate.parse(date, df);

        Integer value;
        switch (type) {
            case YEAR:
                value = localDateTime.getYear();
                break;
            case MONTH:
                value = localDateTime.getMonthValue();
                break;
            case DAY:
                value = localDateTime.getDayOfMonth();
                break;
            default:
                value = null;
        }
        return value;
    }

    public static void main(String[] args) {
//        System.out.println(getLongToday());
//        System.out.println(getLongYesterday());
//        System.out.println(getLongThisMonday());
//        System.out.println(getLongLastMonday());
        System.out.println(getByLocalDatePattern("20200230", "yyyyMMdd", LocalDateTimeUtil.DAY));
    }
}
