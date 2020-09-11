package com.dong.empty.global.util.excel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author caishaodong
 * @Date 2020-09-10 16:59
 * @Description
 **/
public class ExcelDataUtil {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static <T> void export(LinkedHashMap<String, String> fieldNameMap, List<T> objects, String fileName, HttpServletResponse response)
            throws NoSuchFieldException, IllegalAccessException, IOException {

        // 表格数据
        List<List<Object>> dataList = new ArrayList<>();
        // 列名称
        List<Object> headRowData = new ArrayList<>();
        // 属性名
        LinkedList fieldNameList = new LinkedList();

        // 把列名称放在第一行
        Iterator<Map.Entry<String, String>> mapIterator = fieldNameMap.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<String, String> next = mapIterator.next();
            headRowData.add(next.getKey());
            fieldNameList.add(next.getValue());
        }
        dataList.add(headRowData);

        // 填充属性值
        for (Object obj : objects) {
            // 获取每一行的属性值
            List<Object> rowData = new ArrayList<>();
            Iterator<String> iterator = fieldNameList.iterator();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                // 获取属性
                Field field = obj.getClass().getDeclaredField(fieldName);
                // 获取属性值
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value instanceof LocalDateTime) {
                    value = DateTimeFormatter.ofPattern(PATTERN).format((LocalDateTime) value);
                } else if (value instanceof Date) {
                    value = new SimpleDateFormat(PATTERN).format(value);
                }

                rowData.add(value);
            }
            dataList.add(rowData);
        }

        // 参数准备
        List<ExcelSheetPO> list = new ArrayList<>();
        ExcelSheetPO excelSheetPO = new ExcelSheetPO();
        excelSheetPO.setTitle("title");
        excelSheetPO.setDataList(dataList);
        list.add(excelSheetPO);

        // 导出
        ExcelUtil.exportToBrowser(list, fileName + System.currentTimeMillis(), response);

    }
}
