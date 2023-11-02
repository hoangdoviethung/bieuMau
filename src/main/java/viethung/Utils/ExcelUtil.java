package viethung.Utils;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtil {

    public static Workbook cloneExelXlsxFile(InputStream inputStream) throws Exception {
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException ex) {
            throw new Exception();
        }
        return workbook;
    }
    public static List<List<String>> readFile(Workbook workbook, int sheetIndex, boolean readUnicodeChar, Boolean... readEmptyRow) {
        List<List<String>> result = new ArrayList<>();
        Sheet sheet0 = workbook.getSheetAt(sheetIndex);
        Iterator<Row> rowIterator = sheet0.rowIterator();
        int i = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (readEmptyRow.length == 0) {
                List<String> items = readRow(row, readUnicodeChar);
                if (!items.isEmpty()) {
                    result.add(items);
                }
            } else {
                List<String> items = readRow(row, readUnicodeChar, readEmptyRow);
                result.add(items);
            }
        }
        return result;
    }

    public static List<String> readRow(Row row, boolean readUnicodeChar, Boolean... readEmptyRow) {
        FormulaEvaluator evaluator = row.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        List<String> items = new ArrayList<>();
        boolean isRowHasData = false;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                items.add("");
            } else {
                if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    evaluator.evaluateFormulaCell(cell);
                    cell.setCellType(cell.getCachedFormulaResultType());
                }
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        items.add(String.valueOf(cell.getDateCellValue().getTime()));
                    } else {
                        DecimalFormat decimalFormat = new DecimalFormat("#.###");
                        items.add(decimalFormat.format(cell.getNumericCellValue()));
                    }
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String txt = cell.getStringCellValue();
                    if (!readUnicodeChar) {
                        txt = txt.replaceAll("\\P{Print}", "");
                    }
                    items.add(txt);
                } else {
                    cell.setCellType(Cell.CELL_TYPE_BLANK);
                    items.add("");
                }
            }
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) isRowHasData = true;
        }
        if (readEmptyRow.length == 0) {
            if (!isRowHasData) items.clear();
        }
        return items;
    }

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }


    // check dinh dang sdt
    public boolean isPhoneNumber(String strPhoneNumber) {
        Pattern pattern = Pattern.compile("^\\d{10}$");
        return pattern.matcher(strPhoneNumber).matches();
    }

    // check dd/MM/yyyy
    public boolean isDateTime(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate parsedDate = LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
