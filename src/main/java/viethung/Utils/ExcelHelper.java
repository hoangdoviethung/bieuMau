package viethung.Utils;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.format.datetime.DateFormatter;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


public class ExcelHelper {

    private final static String DARK_GREEN_COLOR = "007D4C";


    private static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class);


    public static boolean isSheetNotEmpty(HSSFSheet sheet) {
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            HSSFRow row = (HSSFRow) rows.next();
            Iterator<Cell> cells = row.cellIterator();
            while (cells.hasNext()) {
                HSSFCell cell = (HSSFCell) cells.next();
                if (!cell.getStringCellValue().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSheetNotEmpty(XSSFSheet sheet) {
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            XSSFRow row = (XSSFRow) rows.next();
            Iterator<Cell> cells = row.cellIterator();
            while (cells.hasNext()) {
                XSSFCell cell = (XSSFCell) cells.next();
                if (!cell.getStringCellValue().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }


    public static <T> List<T> sheetToPOJO(Sheet sheet, Class<T> beanClass, int headerRowNum) throws Exception {

        DataFormatter formatter = new DataFormatter(Locale.US);
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        // collecting the column headers as a Map of header names to column indexes
        Map<Integer, String> colHeaders = new HashMap<>();
        Row row = sheet.getRow(headerRowNum);

        Row headerRow = sheet.getRow(headerRowNum);

        List<String> listColumnNames = Arrays.stream(beanClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelColumn.class)
                        && Objects.equals(field.getAnnotation(ExcelColumn.class).columnType(), Constant.ColumnType.LISTED_COLUMN))
                .map(f -> f.getAnnotation(ExcelColumn.class).name())
                .collect(Collectors.toList());

        for (Cell cell : row) {
            int colIdx = cell.getColumnIndex();
            String value = formatter.formatCellValue(cell, evaluator);
            colHeaders.put(colIdx, value);
        }

        // collecting the content rows
        List<T> result = new ArrayList<>();
        String cellValue;
        Date date;
        BigDecimal num;
        for (int r = headerRowNum + 1; r <= sheet.getLastRowNum(); r++) {
            row = sheet.getRow(r);
            if (row == null) row = sheet.createRow(r);
            T bean = beanClass.getDeclaredConstructor().newInstance();

            List<String> cellValueList = new ArrayList<>();

            for (Map.Entry<Integer, String> entry : colHeaders.entrySet()) {
                int colIdx = entry.getKey();
                Cell cell = row.getCell(colIdx);
                if (cell == null) cell = row.createCell(colIdx);
                cellValue = formatter.formatCellValue(cell, evaluator).trim(); // string values and formatted numbers

                boolean isHasNext = addColValueToListAndCheckHasNext(cellValueList, cellValue, listColumnNames,
                        headerRow, colIdx, formatter, evaluator);
                if (isHasNext)
                    continue;

                // make some differences for numeric or formula content
                date = null;
                num = null;
                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    if (DateUtil.isCellDateFormatted(cell)) { // date
                        date = cell.getDateCellValue();
                    } else { // other numbers
                        num = BigDecimal.valueOf(cell.getNumericCellValue());
                    }
                } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
                    // if formula evaluates to numeric
                    if (evaluator.evaluateFormulaCellEnum(cell) == CellType.NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) { // date
                            date = cell.getDateCellValue();
                        } else { // other numbers
                            num = BigDecimal.valueOf(cell.getNumericCellValue());
                        }
                    }
                }

                // fill the bean
                for (Field f : beanClass.getDeclaredFields()) {
                    if (!f.isAnnotationPresent(ExcelColumn.class)) {
                        continue;
                    }
                    ExcelColumn ec = f.getAnnotation(ExcelColumn.class);
                    //check header name (exp: Main Functions 1) has field name (exp: Main Functions )
                    if (Constant.ColumnType.LISTED_COLUMN.equals(ec.columnType()) &&
                            entry.getValue().toLowerCase().trim().contains(ec.name().toLowerCase().trim())) {
                        f.setAccessible(true);
                        f.set(bean, cellValueList);
                        //reset for next excel list
                        cellValueList = new ArrayList<>();
                        break;
                    }
                    if (entry.getValue().trim().equalsIgnoreCase(ec.name().trim())) {
                        f.setAccessible(true);
                        if (f.getType() == String.class) {
                            f.set(bean, cellValue);
                        } else if (f.getType() == Boolean.class) {
                            if(cellValue.trim().equalsIgnoreCase("Yes") || cellValue.trim().equalsIgnoreCase("Y")) {
                                cellValue = "True";
                            }
                            f.set(bean, Boolean.valueOf(cellValue));
                        } else if (f.getType() == Timestamp.class) {
                            f.set(bean, date != null ? Timestamp.from(date.toInstant()) : null);
                        } else if (f.getType() == Date.class) {
                            f.set(bean, date);
                        } else if (f.getType() == Double.class) {
                            if(num == null) {
                                f.set(bean, null);
                            } else {
                                f.set(bean, Double.parseDouble(num.toString()));
                            }
                        } else {// this is for all other; Integer, Boolean, ...
                            if (!"".equals(cellValue)) {
                                f.set(bean, num);
                            }
                        }
                    }
                }
            }
            result.add(bean);
        }

        return result;

    }

    static boolean addColValueToListAndCheckHasNext(List<String> cellValueList, String cellValue,
                                                    List<String> listColNames,
                                                    Row headerRow, int currentColumnIdx,
                                                    DataFormatter dataFormatter, FormulaEvaluator evaluator) {
        String tempColName;

        Cell currentHeaderCell = headerRow.getCell(currentColumnIdx);
        String currentHeaderColumnName = dataFormatter.formatCellValue(currentHeaderCell, evaluator) == null ? StringUtils.EMPTY
                : dataFormatter.formatCellValue(currentHeaderCell, evaluator);

        Cell nextHeaderCell = headerRow.getCell(currentColumnIdx + 1);
        String nextHeaderColumnName = dataFormatter.formatCellValue(nextHeaderCell, evaluator);

        //find name contain in header cell
        Optional<String> tempColNameOpt = listColNames.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .filter(colName -> currentHeaderColumnName.toLowerCase().contains(colName))
                .findAny();

        if (tempColNameOpt.isPresent()) {

            tempColName = tempColNameOpt.get();
            cellValueList.add(cellValue);

            return nextHeaderColumnName.toLowerCase().contains(tempColName);
        }
        return false;
    }

    public static <T> void pojoToSheet(Sheet sheet, List<T> rows) throws Exception {
        if (rows.size() > 0) {
            Row row;
            Cell cell;
            int r = 0;
            int c = 0;
            int colCount;
            Map<String, Object> properties;
            DataFormat dataFormat = sheet.getWorkbook().createDataFormat();

            Class<?> beanClass = rows.get(0).getClass();

            // header row
            row = sheet.createRow(r++);
            for (Field f : beanClass.getDeclaredFields()) {
                if (!f.isAnnotationPresent(ExcelColumn.class)) {
                    continue;
                }
                ExcelColumn ec = f.getAnnotation(ExcelColumn.class);
                cell = row.createCell(c++);
                // do formatting the header row
                properties = new HashMap<>();
                properties.put(CellUtil.FILL_PATTERN, FillPatternType.SOLID_FOREGROUND);
                properties.put(CellUtil.FILL_FOREGROUND_COLOR, IndexedColors.GREEN.getIndex());
                CellUtil.setCellStyleProperties(cell, properties);
                cell.setCellValue(ec.name());
            }

            colCount = c;

            // contents
            for (T bean : rows) {
                c = 0;
                row = sheet.createRow(r++);
                for (Field f : beanClass.getDeclaredFields()) {
                    cell = row.createCell(c++);
                    if (!f.isAnnotationPresent(ExcelColumn.class)) {
                        continue;
                    }
                    ExcelColumn ec = f.getAnnotation(ExcelColumn.class);
                    // do number formatting the contents
                    String numberFormat = ec.numberFormat();
                    properties = new HashMap<>();
                    properties.put(CellUtil.DATA_FORMAT, dataFormat.getFormat(numberFormat));
                    CellUtil.setCellStyleProperties(cell, properties);

                    f.setAccessible(true);
                    Object value = f.get(bean);
                    if (value != null) {
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Date) {
                            cell.setCellValue((Date) value);
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        }
                    }
                }
            }

            // auto size columns
            for (int col = 0; col < colCount; col++) {
                sheet.autoSizeColumn(col);
            }
        }
    }

    public static ByteArrayOutputStream createAndDownloadDocument(Resource inputResource, Map<String, Object> data) throws IOException {
        try (InputStream input = inputResource.getInputStream()) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Context context = new Context();

            for (Map.Entry<String, Object> element : data.entrySet()) {
                context.putVar(element.getKey(), element.getValue());
            }
            JxlsHelper.getInstance().processTemplate(input, outStream, context);
            return outStream;
        }
    }

    public static ByteArrayOutputStream createAndDownloadDocumentWithDynamicColumn(Resource inputResource, Map<String, Object> data, String propertiesStr) throws IOException {
        try (InputStream input = inputResource.getInputStream()) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Context context = new Context();

            for (Map.Entry<String, Object> element : data.entrySet()) {
                context.putVar(element.getKey(), element.getValue());
            }
            JxlsHelper.getInstance().processGridTemplateAtCell(input, outStream, context, propertiesStr, "JobDescriptionDB!A1");
            return outStream;
        }
    }


    public final static int BUF_SIZE = 1024;
    public static void copyFile(File in, File out) throws Exception {
        try (FileInputStream fis = new FileInputStream(in); FileOutputStream fos = new FileOutputStream(out)) {
            byte[] buf = new byte[BUF_SIZE];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
    }

    private static void setCellValue(int i, int j, XSSFSheet sheet, Object value, XSSFCellStyle cellRowStyle) {
        // khai bao hang
        XSSFRow currentRow = sheet.getRow(i);
        if (currentRow == null) {
            currentRow = sheet.createRow(i);
            currentRow.setHeightInPoints(25);
        }
        // khai bao o
        XSSFCell fDateCell = currentRow.getCell(j);
        if (fDateCell == null) {
            fDateCell = currentRow.createCell(j);
        }
        if (value != null) {
            switch (value.getClass().getName()) {
                case "java.util.Date":
                    fDateCell.setCellValue(String.valueOf(new DateFormatter((String) value)));
                    break;
                case "java.lang.Integer":
                    fDateCell.setCellValue((int) value);
                    break;
                case "java.lang.Float":
                    fDateCell.setCellValue((float) value);
                    break;
                case "java.lang.Double":
                    fDateCell.setCellValue((double) value);
                    break;
                default:
                    fDateCell.setCellValue((String) value);
                    break;
            }
        } else {
            fDateCell.setCellValue("");
        }
        fDateCell.setCellStyle(cellRowStyle);
    }
}


