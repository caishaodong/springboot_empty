package com.dong.empty.global.util.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author caishaodong
 * @Date 2020-09-04 13:36
 * @Description POI工具类
 **/
public class ExcelMergeCellUtil {

    /**
     * 合并单元格测试
     */
    public static void mergeCellTest() {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 设置水平对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置垂直对齐方式
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        // 创建sheet
        HSSFSheet sheet = workbook.createSheet("mergeCell");

        // 创建第一行
        HSSFRow row0 = sheet.createRow(0);
        // 创建第一行第一列
        HSSFCell cell0_0 = row0.createCell(0);
        cell0_0.setCellStyle(cellStyle);
        cell0_0.setCellValue("日期");
        // 创建第一行第二列
        HSSFCell cell0_1 = row0.createCell(1);
        cell0_1.setCellStyle(cellStyle);
        cell0_1.setCellValue("午别");

        // 创建第二行
        HSSFRow row1 = sheet.createRow(1);
        // 创建第二行第一列
        HSSFCell cell1_0 = row1.createCell(0);
        cell1_0.setCellStyle(cellStyle);
        cell1_0.setCellValue("20180412");
        // 创建第二行第二列
        HSSFCell cell1_1 = row1.createCell(1);
        cell1_1.setCellStyle(cellStyle);
        cell1_1.setCellValue("上午");

        // 创建第三行
        HSSFRow row2 = sheet.createRow(2);
        // 创建第三行第一列，虽然要被合并，但是没有单元格的话，不会被加上边框
        HSSFCell cell2_0 = row2.createCell(0);
        cell2_0.setCellStyle(cellStyle);
        // 创建第三行第二列
        HSSFCell cell2_1 = row2.createCell(1);
        cell2_1.setCellStyle(cellStyle);
        cell2_1.setCellValue("下午");

        // 合并单元格，四个参数（起始行，结束行，起始列，结束列）
        // 行和列都是从0开始计数，且起始结束都会合并
        // 这里是合并excel中日期的两行为一行
        CellRangeAddress region = new CellRangeAddress(1, 2, 0, 0);
        sheet.addMergedRegion(region);

        // 创建第四行
        HSSFRow row3 = sheet.createRow(3);
        // 创建第四行第一列
        HSSFCell cell3_0 = row3.createCell(0);
        cell3_0.setCellStyle(cellStyle);
        cell3_0.setCellValue("20180413");
        // 创建第五行
        HSSFRow row4 = sheet.createRow(4);
        // 创建第五行第一列，虽然要被合并，但是没有单元格的话，不会被加上边框
        HSSFCell cell4_0 = row4.createCell(0);
        cell4_0.setCellStyle(cellStyle);
        // 合并第四行和第五行
        CellRangeAddress region2 = new CellRangeAddress(3, 4, 0, 0);
        sheet.addMergedRegion(region2);

        try {
            File file = new File("D:/demo.xls");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        mergeCellTest();
    }
}
