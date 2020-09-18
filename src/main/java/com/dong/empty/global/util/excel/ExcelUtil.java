package com.dong.empty.global.util.excel;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dong.empty.global.util.string.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author caishaodong
 * @Date 2020-09-10 16:19
 * @Description Excel工具类
 **/
public class ExcelUtil {
    /**
     * 标题样式
     */
    private final static String STYLE_HEADER = "header";
    /**
     * 表头样式
     */
    private final static String STYLE_TITLE = "title";
    /**
     * 数据样式
     */
    private final static String STYLE_DATA = "data";

    /**
     * 存储样式
     */
    private static HashMap<String, CellStyle> cellStyleMap;

    /**
     * 读取Excel
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static List<ExcelSheetPO> readExcel(MultipartFile file) throws IOException {
        return readExcel(file, null, null);
    }

    /**
     * 读取excel文件里面的内容 支持日期，数字，字符，函数公式，布尔类型
     *
     * @param file
     * @param rowCount
     * @param columnCount
     */
    public static List<ExcelSheetPO> readExcel(MultipartFile file, Integer rowCount, Integer columnCount)
            throws IOException {

        // 根据后缀名称判断excel的版本
        String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        Workbook wb = null;
        if (ExcelVersion.V2003.getSuffix().equals(extName)) {
            wb = new HSSFWorkbook(file.getInputStream());

        } else if (ExcelVersion.V2007.getSuffix().equals(extName)) {
            wb = new XSSFWorkbook(file.getInputStream());

        } else {
            // 无效后缀名称，这里之能保证excel的后缀名称，不能保证文件类型正确，不过没关系，在创建Workbook的时候会校验文件格式
            throw new IllegalArgumentException("Invalid excel version");
        }
        // 开始读取数据
        List<ExcelSheetPO> sheetPOs = new ArrayList<>();
        // 解析sheet
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            List<List<Object>> dataList = new ArrayList<>();
            ExcelSheetPO sheetPO = new ExcelSheetPO();
            sheetPO.setSheetName(sheet.getSheetName());
            sheetPO.setDataList(dataList);
            int readRowCount = 0;
            if (rowCount == null || rowCount > sheet.getPhysicalNumberOfRows()) {
                readRowCount = sheet.getPhysicalNumberOfRows();
            } else {
                readRowCount = rowCount;
            }
            // 解析sheet 的行
            for (int j = sheet.getFirstRowNum(); j < readRowCount; j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                if (row.getFirstCellNum() < 0) {
                    continue;
                }
                int readColumnCount = 0;
                if (columnCount == null || columnCount > row.getLastCellNum()) {
                    readColumnCount = (int) row.getLastCellNum();
                } else {
                    readColumnCount = columnCount;
                }
                List<Object> rowValue = new LinkedList<Object>();
                // 解析sheet 的列
                for (int k = 0; k < readColumnCount; k++) {
                    Cell cell = row.getCell(k);
                    rowValue.add(getCellValue(wb, cell));
                }
                dataList.add(rowValue);
            }
            sheetPOs.add(sheetPO);
        }
        return sheetPOs;
    }

    private static Object getCellValue(Workbook wb, Cell cell) {
        Object columnValue = null;
        if (cell != null) {
            // 格式化 number
            DecimalFormat df = new DecimalFormat("0");
            // String
            // 字符
            // 格式化日期字符串
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 格式化数字
            DecimalFormat nf = new DecimalFormat("0.00");
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    columnValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                        columnValue = df.format(cell.getNumericCellValue());
                    } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                        columnValue = nf.format(cell.getNumericCellValue());
                    } else {
                        columnValue = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    columnValue = cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_BLANK:
                    columnValue = "";
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    // 格式单元格
                    FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
                    evaluator.evaluateFormulaCell(cell);
                    CellValue cellValue = evaluator.evaluate(cell);
                    columnValue = cellValue.getNumberValue();
                    break;
                default:
                    columnValue = cell.toString();
            }
        }
        return columnValue;
    }

    /**
     * 在硬盘上写入excel文件
     *
     * @param version
     * @param excelSheets
     * @param filePath
     */
    public static void createWorkbookAtDisk(ExcelVersion version, List<ExcelSheetPO> excelSheets, String filePath)
            throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        createWorkbookAtOutStream(version, excelSheets, fileOut, true);
    }

    /**
     * 把excel表格写入输出流中，输出流会被关闭
     *
     * @param version
     * @param excelSheets
     * @param outStream
     * @param closeStream 是否关闭输出流
     */
    public static void createWorkbookAtOutStream(ExcelVersion version, List<ExcelSheetPO> excelSheets,
                                                 OutputStream outStream, boolean closeStream) throws IOException {
        if (CollectionUtils.isNotEmpty(excelSheets)) {
            Workbook wb = createWorkBook(version, excelSheets);
            System.out.println("导出");
            wb.write(outStream);
            if (closeStream) {
                outStream.close();
            }
        }
    }

    /**
     * 导出到浏览器
     *
     * @param excelSheets
     * @param fileName
     * @param response
     * @throws IOException
     */
    public static void exportToBrowser(List<ExcelSheetPO> excelSheets, String fileName, HttpServletResponse response) throws IOException {
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment;Filename=" + fileName + ".xlsx");
        OutputStream outputStream = response.getOutputStream();
        Workbook wb = createWorkBook(ExcelVersion.V2007, excelSheets);
        wb.write(outputStream);
        outputStream.close();
    }

    private static Workbook createWorkBook(ExcelVersion version, List<ExcelSheetPO> excelSheets) {
        cellStyleMap = new HashMap<>();
        Workbook wb = createWorkbook(version);
        for (int i = 0; i < excelSheets.size(); i++) {
            ExcelSheetPO excelSheetPO = excelSheets.get(i);
            if (excelSheetPO.getSheetName() == null) {
                excelSheetPO.setSheetName("sheet" + i);
            }
            // 过滤特殊字符
            Sheet tempSheet = wb.createSheet(WorkbookUtil.createSafeSheetName(excelSheetPO.getSheetName()));
            buildSheetData(wb, tempSheet, excelSheetPO, version);
        }
        return wb;
    }

    private static void buildSheetData(Workbook wb, Sheet sheet, ExcelSheetPO excelSheetPO, ExcelVersion version) {
        // 设置行宽
        setColumnWith(sheet, excelSheetPO);
        // 有需要可以自定义title和header
        createTitle(sheet, excelSheetPO, wb, version);
        createHeader(sheet, excelSheetPO, wb, version);
        createBody(sheet, excelSheetPO, wb, version);
    }

    /**
     * 内容
     *
     * @param sheet
     * @param excelSheetPO
     * @param wb
     * @param version
     */
    private static void createBody(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        List<List<Object>> dataList = excelSheetPO.getDataList();
        for (int i = 0; i < dataList.size() && i < version.getMaxRow(); i++) {
            List<Object> values = dataList.get(i);
            Row row = sheet.createRow(2 + i);
//            Row row = sheet.createRow(i);
            for (int j = 0; j < values.size() && j < version.getMaxColumn(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(getStyle(STYLE_DATA, wb));
                cell.setCellValue(values.get(j).toString());
            }
        }

    }

    /**
     * 生成header
     *
     * @param sheet
     * @param excelSheetPO
     * @param wb
     * @param version
     */
    private static void createHeader(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        String[] headers = excelSheetPO.getHeaders();

        if (headers == null) {
            Cell cellHeader = null;
        } else {
            Row row = sheet.createRow(1);
            for (int i = 0; i < headers.length && i < version.getMaxColumn(); i++) {
                Cell cellHeader = row.createCell(i);
                cellHeader.setCellStyle(getStyle(STYLE_HEADER, wb));
                cellHeader.setCellValue(headers[i]);
            }
        }
    }

    /**
     * 生成title
     *
     * @param sheet
     * @param excelSheetPO
     * @param wb
     * @param version
     */
    private static void createTitle(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        if (StringUtil.isBlank(excelSheetPO.getTitle())) {
            return;
        }
        Row titleRow = sheet.createRow(0);
        Cell titleCel = titleRow.createCell(0);
        excelSheetPO.setTitle(excelSheetPO.getTitle());
        titleCel.setCellValue(excelSheetPO.getTitle());
        titleCel.setCellStyle(getStyle(STYLE_TITLE, wb));
        // 限制最大列数
        int column = excelSheetPO.getHeaders().length > version.getMaxColumn() ? version.getMaxColumn() : excelSheetPO.getHeaders().length;
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, column - 1));
    }

    /**
     * 设置行宽
     *
     * @param sheet
     * @param excelSheetPO
     */
    private static void setColumnWith(Sheet sheet, ExcelSheetPO excelSheetPO) {
        sheet.setDefaultRowHeight((short) 400);
        sheet.setDefaultColumnWidth((short) 10);
        Map<Integer, Integer> columnWithMap = excelSheetPO.getColumnWidthMap();
        if (CollectionUtils.isEmpty(columnWithMap)) {
            return;
        }
        Iterator<Map.Entry<Integer, Integer>> iterator = columnWithMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            Integer column = entry.getKey();
            Integer width = entry.getValue();
            sheet.setColumnWidth(column, width * 256);
        }


    }

    /**
     * 获取样式style
     *
     * @param type
     * @param wb
     * @return
     */
    private static CellStyle getStyle(String type, Workbook wb) {

        if (cellStyleMap.containsKey(type)) {
            return cellStyleMap.get(type);
        }

        // 生成一个样式
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        style.setLocked(false);

        if (STYLE_TITLE.equals(type)) {
            style.setAlignment(HorizontalAlignment.CENTER);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 18);
            font.setBold(true);
            font.setFontName("宋体");
            style.setFont(font);
        } else if (STYLE_HEADER.equals(type)) {
            style.setAlignment(HorizontalAlignment.CENTER);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 14);
            font.setBold(true);
            font.setFontName("宋体");
            style.setFont(font);
        } else if (STYLE_DATA.equals(type)) {
            style.setAlignment(HorizontalAlignment.LEFT);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setFontName("宋体");
            style.setFont(font);
        }

        cellStyleMap.put(type, style);
        return style;
    }

    private static Workbook createWorkbook(ExcelVersion version) {
        switch (version) {
            case V2003:
                return new HSSFWorkbook();
            case V2007:
                return new XSSFWorkbook();
        }
        return null;
    }

}
