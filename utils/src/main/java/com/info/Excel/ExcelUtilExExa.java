//package com.info.Excel;
//
//import org.apache.poi.hssf.usermodel.*;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.Font;
//import org.apache.poi.ss.usermodel.IndexedColors;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.ss.util.SheetUtil;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.format.annotation.NumberFormat;
//import org.springframework.format.datetime.DateFormatter;
//import org.springframework.format.number.NumberFormatter;
//
//import java.io.ByteArrayOutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map.Entry;
//import java.util.Set;
//
///**
// * Excel生成辅助类(注：支持多线程)
// */
//public class ExcelUtilExExa {
//
//    public ExcelUtilExExa() {
//        //工具类无需对象实例化
//    }
//
//    private HSSFWorkbook wb;
//
//    private CellStyle titleStyle; // 标题行样式
//    private Font titleFont; // 标题行字体
//    private CellStyle dateStyle; // 日期行样式
//    private Font dateFont; // 日期行字体
//    private CellStyle headStyle; // 表头行样式
//    private Font headFont; // 表头行字体
//    private CellStyle contentStyle; // 内容行样式
//    private Font contentFont; // 内容行字体
//
//
//    /**
//     * 自动设置列宽
//     * @param sheet
//     * @param maxColumnIndex
//     */
//    public static void autoSetColumnWith(HSSFSheet sheet,int maxColumnIndex){
//        //sheet.autoSizeColumn(i,true);
//        double adjustFactor = 1.0;
//        if (OSUtil.getOSname().equals(OSType.Windows)){
//            adjustFactor = 1.1;
//        }
//        else if (OSUtil.getOSname().equals(OSType.Linux) || OSUtil.getOSname().equals(OSType.Linux)){
//            adjustFactor = 1.5;
//        }
//        for (int i = 0; i < maxColumnIndex; i++) {
//            double width = SheetUtil.getColumnWidth(sheet, i, true);
//            if (width != -1) {
//                width *= (256 * adjustFactor);
//                int maxColumnWidth = 255 * 256;
//                if (width > maxColumnWidth) {
//                    width = maxColumnWidth;
//                }
//                //logger.info("宽度:" + width);
//                sheet.setColumnWidth(i, (int) (width));
//            }
//        }
//    }
//
//    /**
//     * 导出文件
//     *
//     * @param setInfo
//     * @param outputExcelFileName
//     * @return
//     * @throws java.io.IOException
//     */
//    public boolean export2File(ExcelExportData setInfo,
//                               String outputExcelFileName) throws Exception {
//        return FileUtil.write(outputExcelFileName, export2ByteArray(setInfo),
//                true, true);
//    }
//
//    /**
//     * 导出到byte数组
//     *
//     * @param setInfo
//     * @return
//     * @throws Exception
//     */
//    public byte[] export2ByteArray(ExcelExportData setInfo)
//            throws Exception {
//        return export2Stream(setInfo).toByteArray();
//    }
//
//    /**
//     * 导出到流
//     *
//     * @param setInfo
//     * @return
//     * @throws Exception
//     */
//    public ByteArrayOutputStream export2Stream(ExcelExportData setInfo)
//            throws Exception {
//        init();
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        Set<Entry<String, List<?>>> set = setInfo.getDataMap().entrySet();
//        String[] sheetNames = new String[setInfo.getDataMap().size()];
//        int sheetNameNum = 0;
//        for (Entry<String, List<?>> entry : set) {
//            sheetNames[sheetNameNum] = entry.getKey();
//            sheetNameNum++;
//        }
//        HSSFSheet[] sheets = getSheets(setInfo.getDataMap().size(), sheetNames);
//        int sheetNum = 0;
//        for (Entry<String, List<?>> entry : set) {
//            // Sheet
//            List<?> objs = entry.getValue();
//
//            // 标题行
//            createTableTitleRow(setInfo, sheets, sheetNum);
//
//            // 日期行
//            createTableDateRow(setInfo, sheets, sheetNum);
//
//            // 表头
//            creatTableHeadRow(setInfo, sheets, sheetNum);
//
//            // 表体
//            String[] fieldNames = setInfo.getFieldNames().get(sheetNum);
//
//            int rowNum = 3;
//            for (Object obj : objs) {
//                HSSFRow contentRow = sheets[sheetNum].createRow(rowNum);
//                contentRow.setHeight((short) 280);
//                HSSFCell[] cells = getCells(contentRow, setInfo.getFieldNames()
//                        .get(sheetNum).length);
//                int cellNum = 1; // 去掉一列序号，因此从1开始
//                if (fieldNames != null) {
//                    for (int num = 0; num < fieldNames.length; num++) {
//
//                        Object value = ReflectionUtil.invokeGetterMethod(obj,
//                                fieldNames[num]);
//                        String cellValue = value == null ? "" : value.toString();
//
//                        boolean isNeedSetValue = true;
//                        if (value != null) {
//                            if (value.getClass().equals(Date.class)) {
//                                //日期格式
//                                DateTimeFormat annotation = ReflectionUtil.getAnnotation(obj, fieldNames[num], DateTimeFormat.class);
//                                if (annotation != null) {
//                                    DateFormatter dateFormatter = new DateFormatter();
//                                    dateFormatter.setPattern(annotation.pattern());
//                                    cellValue = dateFormatter.print((Date) value, Locale.CHINESE);
//                                } else {
//                                    cellValue = DateUtil.formatDate((Date) value);
//                                }
//                            } else if (value.getClass().getSuperclass().equals(Number.class)) {
//                                //数字格式
//                                NumberFormat annotation = ReflectionUtil.getAnnotation(obj, fieldNames[num], NumberFormat.class);
//                                if (annotation != null) {
//                                    NumberFormatter formatter = new NumberFormatter();
//                                    formatter.setPattern(annotation.pattern());
//                                    cellValue = formatter.print((Number) value, Locale.CHINESE);
//                                } else {
//                                    cellValue = NumberUtil.formatNumber((Number) value);
//                                }
//                                cells[cellNum].setCellValue(NumberUtil.parseNumber(cellValue, Double.class));
//                                isNeedSetValue = false;
//                            }
//                        }
//                        if (isNeedSetValue) {
//                            cells[cellNum].setCellValue(cellValue);
//                        }
//                        cellNum++;
//                    }
//                }
//                rowNum++;
//            }
//            adjustColumnSize(sheets, sheetNum, fieldNames, setInfo); // 自动调整列宽
//            sheetNum++;
//        }
//        wb.write(outputStream);
//        return outputStream;
//    }
//
//    /**
//     * @Description: 初始化
//     */
//    private void init() {
//        wb = new HSSFWorkbook();
//
//        titleFont = wb.createFont();
//        titleStyle = wb.createCellStyle();
//        dateStyle = wb.createCellStyle();
//        dateFont = wb.createFont();
//        headStyle = wb.createCellStyle();
//        headFont = wb.createFont();
//        contentStyle = wb.createCellStyle();
//        contentFont = wb.createFont();
//
//        initTitleCellStyle();
//        initTitleFont();
//        initDateCellStyle();
//        initDateFont();
//        initHeadCellStyle();
//        initHeadFont();
//        initContentCellStyle();
//        initContentFont();
//    }
//
//    /**
//     * @Description: 自动调整列宽
//     */
//    private void adjustColumnSize(HSSFSheet[] sheets, int sheetNum,
//                                  String[] fieldNames, ExcelExportData setInfo) {
//        for (int i = 0; i < fieldNames.length + 1; i++) {
//            double width = SheetUtil.getColumnWidth(sheets[sheetNum], i, true);
//            if (width != -1) {
//                width *= (256 + 5);
//                int maxColumnWidth = 255 * 256;
//                if (width > maxColumnWidth) {
//                    width = maxColumnWidth;
//                }
//                sheets[sheetNum].setColumnWidth(i, (int) (width));
//            }
//            //sheets[sheetNum].autoSizeColumn(i, true);
//        }
//
//        if (!setInfo.isShowSNColumn()) {
//            sheets[sheetNum].setColumnWidth(0, 0);
//        }
//    }
//
//
//    /**
//     * @Description: 创建标题行(需合并单元格)
//     */
//    private void createTableTitleRow(ExcelExportData setInfo,
//                                     HSSFSheet[] sheets, int sheetNum) {
//        CellRangeAddress titleRange = new CellRangeAddress(0, 0, 0, setInfo
//                .getFieldNames().get(sheetNum).length);
//        sheets[sheetNum].addMergedRegion(titleRange);
//        HSSFRow titleRow = sheets[sheetNum].createRow(0);
//        //titleRow.setHeight((short) 800);
//        titleRow.setHeight((short) 450);
//        HSSFCell titleCell = titleRow.createCell(0);
//        titleCell.setCellStyle(titleStyle);
//        titleCell.setCellValue(setInfo.getTitles()[sheetNum]);
//    }
//
//    /**
//     * @Description: 创建日期行(需合并单元格)
//     */
//    private void createTableDateRow(ExcelExportData setInfo,
//                                    HSSFSheet[] sheets, int sheetNum) {
//        CellRangeAddress dateRange = new CellRangeAddress(1, 1, 0, setInfo
//                .getFieldNames().get(sheetNum).length);
//        sheets[sheetNum].addMergedRegion(dateRange);
//        HSSFRow dateRow = sheets[sheetNum].createRow(1);
//        dateRow.setHeight((short) 350);
//        HSSFCell dateCell = dateRow.createCell(0);
//        dateCell.setCellStyle(dateStyle);
//        dateCell.setCellValue(new SimpleDateFormat("yyyy-MM-dd")
//                .format(new Date()));
//
//        if (!setInfo.isShowDateRow()) {
//            //隐藏日期行
//            dateRow.setZeroHeight(true);
//        }
//    }
//
//    /**
//     * @Description: 创建表头行(需合并单元格)
//     */
//    private void creatTableHeadRow(ExcelExportData setInfo,
//                                   HSSFSheet[] sheets, int sheetNum) {
//        // 表头
//        HSSFRow headRow = sheets[sheetNum].createRow(2);
//        headRow.setHeight((short) 350);
//        // 序号列
//        HSSFCell snCell = headRow.createCell(0);
//        snCell.setCellStyle(headStyle);
//        snCell.setCellValue("序号");
//
//        // 列头名称
//        for (int num = 1, len = setInfo.getColumnNames().get(sheetNum).length; num <= len; num++) {
//            HSSFCell headCell = headRow.createCell(num);
//            headCell.setCellStyle(headStyle);
//            headCell.setCellValue(setInfo.getColumnNames().get(sheetNum)[num - 1]);
//        }
//    }
//
//    /**
//     * @Description: 创建所有的Sheet
//     */
//    private HSSFSheet[] getSheets(int num, String[] names) {
//        HSSFSheet[] sheets = new HSSFSheet[num];
//        for (int i = 0; i < num; i++) {
//            sheets[i] = wb.createSheet(names[i]);
//        }
//        return sheets;
//    }
//
//    /**
//     * @Description: 创建内容行的每一列(附加一列序号)
//     */
//    private HSSFCell[] getCells(HSSFRow contentRow, int num) {
//        HSSFCell[] cells = new HSSFCell[num + 1];
//
//        for (int i = 0, len = cells.length; i < len; i++) {
//            cells[i] = contentRow.createCell(i);
//            cells[i].setCellStyle(contentStyle);
//        }
//
//        // 设置序号列值，因为出去标题行和日期行，所有-2
//        cells[0].setCellValue(contentRow.getRowNum() - 2);
//
//        return cells;
//    }
//
//    /**
//     * @Description: 初始化标题行样式
//     */
//    private void initTitleCellStyle() {
//        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        titleStyle.setFont(titleFont);
//
//        //titleStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.index);
//
//        //titleStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);// 设置背景色
//        //titleStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);// 设置前景色
//        //titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//    }
//
//    /**
//     * @Description: 初始化日期行样式
//     */
//    private void initDateCellStyle() {
//        dateStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
//        dateStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        dateStyle.setFont(dateFont);
//        dateStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.index);
//        dateStyle.setHidden(true);
//    }
//
//    /**
//     * @Description: 初始化表头行样式
//     */
//    private void initHeadCellStyle() {
//        headStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        headStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        headStyle.setFont(headFont);
//
//        headStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
//        headStyle.setBorderBottom(CellStyle.BORDER_THIN);
//        headStyle.setBorderLeft(CellStyle.BORDER_THIN);
//        headStyle.setBorderRight(CellStyle.BORDER_THIN);
////        headStyle.setTopBorderColor(IndexedColors.BLUE.index);
////        headStyle.setBottomBorderColor(IndexedColors.BLUE.index);
////        headStyle.setLeftBorderColor(IndexedColors.BLUE.index);
////        headStyle.setRightBorderColor(IndexedColors.BLUE.index);
//        headStyle.setTopBorderColor(IndexedColors.BLACK.index);
//        headStyle.setBottomBorderColor(IndexedColors.BLACK.index);
//        headStyle.setLeftBorderColor(IndexedColors.BLACK.index);
//        headStyle.setRightBorderColor(IndexedColors.BLACK.index);
//
//        headStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
//        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//    }
//
//    /**
//     * @Description: 初始化内容行样式
//     */
//    private void initContentCellStyle() {
//        contentStyle.setAlignment(CellStyle.ALIGN_CENTER);
//        contentStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        contentStyle.setFont(contentFont);
//        contentStyle.setBorderTop(CellStyle.BORDER_THIN);
//        contentStyle.setBorderBottom(CellStyle.BORDER_THIN);
//        contentStyle.setBorderLeft(CellStyle.BORDER_THIN);
//        contentStyle.setBorderRight(CellStyle.BORDER_THIN);
////        contentStyle.setTopBorderColor(IndexedColors.BLUE.index);
////        contentStyle.setBottomBorderColor(IndexedColors.BLUE.index);
////        contentStyle.setLeftBorderColor(IndexedColors.BLUE.index);
////        contentStyle.setRightBorderColor(IndexedColors.BLUE.index);
//
//        contentStyle.setTopBorderColor(IndexedColors.BLACK.index);
//        contentStyle.setBottomBorderColor(IndexedColors.BLACK.index);
//        contentStyle.setLeftBorderColor(IndexedColors.BLACK.index);
//        contentStyle.setRightBorderColor(IndexedColors.BLACK.index);
//
//        contentStyle.setWrapText(true); // 字段换行
//    }
//
//    /**
//     * @Description: 初始化标题行字体
//     */
//    private void initTitleFont() {
////        titleFont.setFontName("华文楷体");
////        titleFont.setFontHeightInPoints((short) 20);
////        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
////        titleFont.setCharSet(Font.DEFAULT_CHARSET);
////        titleFont.setColor(IndexedColors.BLUE_GREY.index);
//
//        titleFont.setFontName("宋体");
//        titleFont.setFontHeightInPoints((short) 15);
//        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
//        titleFont.setCharSet(Font.DEFAULT_CHARSET);
//        titleFont.setColor(IndexedColors.BLACK.index);
//    }
//
//    /**
//     * @Description: 初始化日期行字体
//     */
//    private void initDateFont() {
////        dateFont.setFontName("隶书");
////        dateFont.setFontHeightInPoints((short) 10);
////        dateFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
////        dateFont.setCharSet(Font.DEFAULT_CHARSET);
////        dateFont.setColor(IndexedColors.BLUE_GREY.index);
//
//        dateFont.setFontName("宋体");
//        dateFont.setFontHeightInPoints((short) 10);
//        dateFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
//        dateFont.setCharSet(Font.DEFAULT_CHARSET);
//        dateFont.setColor(IndexedColors.BLACK.index);
//    }
//
//    /**
//     * @Description: 初始化表头行字体
//     */
//    private void initHeadFont() {
//        headFont.setFontName("宋体");
//        //headFont.setFontHeightInPoints((short) 10);
//        headFont.setFontHeightInPoints((short) 13);
//        headFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
//        headFont.setCharSet(Font.DEFAULT_CHARSET);
//        //headFont.setColor(IndexedColors.BLUE_GREY.index);
//        headFont.setColor(IndexedColors.BLACK.index);
//    }
//
//    /**
//     * @Description: 初始化内容行字体
//     */
//    private void initContentFont() {
//        contentFont.setFontName("宋体");
//        //contentFont.setFontHeightInPoints((short) 10);
//        contentFont.setFontHeightInPoints((short) 10);
//        contentFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
//        contentFont.setCharSet(Font.DEFAULT_CHARSET);
//        //contentFont.setColor(IndexedColors.BLUE_GREY.index);
//        headFont.setColor(IndexedColors.BLACK.index);
//    }
//
//
//}
