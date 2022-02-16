package com.info.Excel;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

public class ExcelUpload {
    //合并单元格地址
    private static List<CellRangeAddress> mergedRegions;
    //表头字段集合
    private static List<String> headerList;


    public static List<String> getHeaderList() {
        return headerList;
    }

    public static void setMergedRegions(List<CellRangeAddress> mergedRegions) {
        ExcelUpload.mergedRegions = mergedRegions;
    }

    public static void setHeaderList(List<String> headerList) {
        ExcelUpload.headerList = headerList;
    }

    public static List<CellRangeAddress> getMergedRegions() {
        return mergedRegions;
    }

    /**
     * 上传数据的方法
     * @param file
     * @param dataRow 数据开始行
     * @param headerRow 表头所在行
     * @return
     * @throws IOException
     */
    public static Map<Object, List<Object>> loadData(MultipartFile file,int headerRow, int dataRow) throws IOException {
        Workbook workbook = null;
        //判断Excel版本
        if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        } else {
            workbook = new XSSFWorkbook(file.getInputStream());
        }
        Sheet sheet = workbook.getSheetAt(0);
        int totalRows = sheet.getLastRowNum();
        Map<Object, List<Object>> map = new HashMap<Object, List<Object>>();
        int serial = 1;
        mergedRegions = sheet.getMergedRegions();
        headerList = new ArrayList<>();
        for (int i = 0; i <= totalRows; i++) {
            Row row = sheet.getRow(i);
            List<Object> list = new ArrayList<Object>();
            int totalCells = sheet.getRow(i).getLastCellNum();
            if(i==headerRow-1){
                for (int j = 0; j <totalCells; j++) {
                    headerList.add(row.getCell(j).getRichStringCellValue().getString());
                }
            }else if (sheet.getRow(i) != null && i >= dataRow-1) {
                for (int j = 0; j < totalCells; j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) {
                        list.add("");
                        continue;
                    }
                    switch (cell.getCellType()) {
                        case STRING:
                            list.add(cell.getRichStringCellValue().getString());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                list.add(cell.getDateCellValue());
                            } else {
                                list.add(cell.getNumericCellValue());
                            }
                            break;
                        case BOOLEAN:
                            list.add(cell.getBooleanCellValue());
                            break;
                        case FORMULA:
                            list.add(cell.getCellFormula());
                            break;
                        default:
                            list.add("");
                            break;
                    }
                }
                map.put(serial++, list);
            }
        }
        return map;
    }
}
