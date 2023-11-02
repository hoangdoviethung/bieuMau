package viethung.Service;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;
import viethung.Utils.Constant;
import viethung.Utils.ExcelHelper;
import viethung.Utils.ExcelUtil;
import viethung.dto.DailyLimitClosingReportReq;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import viethung.dto.ExcelTest;
import viethung.dto.TestDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {


    public ByteArrayOutputStream exportDailyLimitClosingReportExcel(DailyLimitClosingReportReq req) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (req != null) {
            Resource inputResource;
            Map<String, Object> data = new HashMap<>();
            if (req.getType().equals("BC_DONG_HM_THEO_NGAY")) {
                inputResource = new ClassPathResource("template/EXCEL/BC_DONG_HM_THEO_NGAY.xlsx");
                data.put("fromDate", req.getFromDate());
                data.put("toDate", req.getToDate());
            } else if (req.getType().equals("BC_THE_DONG_THEO_NGAY")) {
                inputResource = new ClassPathResource("template/EXCEL/BC_THE_DONG_THEO_NGAY.xlsx");
            } else { //title:HAN_MUC_CAN_DONG
                inputResource = new ClassPathResource("template/EXCEL/HAN_MUC_CAN_DONG.xlsx");
            }
            data.put("data", req.getBaocaos());
            data.put("currentTime", req.getCurrentTime());
            outputStream = ExcelHelper.createAndDownloadDocument(inputResource, data);
        }
        return outputStream;
    }


    public ByteArrayOutputStream convertListExcel(MultipartFile file) throws Exception {
        // đọc file Excel tạo đối tượng

        ExcelUtil excelUtil = new ExcelUtil();
        Integer totalError = 0;
        Integer totalSuccess = 0;
        Integer total = 0;
        List<TestDTO> testDTOs = getListByFile(file).getTestDTOs();
        for (TestDTO testDTO : testDTOs) {
            StringBuilder error = new StringBuilder();
            if (testDTO.getChuoi().isEmpty()) {
                error.append(Constant.ColumnExcelError.excelString);
            }
            if (!excelUtil.isNumeric(testDTO.getSo())) {
                error.append("----" + Constant.ColumnExcelError.excelNumber);
            }
            if (!excelUtil.isDateTime(testDTO.getNgayGio())) {
                error.append("----" + Constant.ColumnExcelError.excelDatetime);
            }
            if (!excelUtil.isPhoneNumber(testDTO.getSdt())) {
                error.append("----" + Constant.ColumnExcelError.excelPhoneNumber);
            }
            if (!error.toString().isEmpty()) {
                testDTO.setError(error.toString());
                totalError += 1;
            }
        }
        // covert lại giá trị rồi set lại vào list excel
        total = testDTOs.size();
        if (totalError != null) {
            totalSuccess = total - totalError;
        } else {
            totalSuccess = testDTOs.size();
            totalError = 0;
        }
        ExcelTest result = new ExcelTest();
        result.setTotalError(totalError);
        result.setTotal(total);
        result.setTotalSuccess(totalSuccess);
        result.setTestDTOs(testDTOs);

        //xuất ra file excel
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Resource inputResource = new ClassPathResource("template/EXCEL/TEST.xlsx");
            Map<String, Object> data = new HashMap<>();

            data.put("data", result.getTestDTOs());
            data.put("totalError", result.getTotalError());
            data.put("total", result.getTotal());
            data.put("totalSuccess", result.getTotalSuccess());
            outputStream = ExcelHelper.createAndDownloadDocument(inputResource, data);
        } catch (Exception e) {

        }
        return outputStream;
    }


    private ExcelTest getListByFile(MultipartFile file) throws Exception {
        ExcelTest excelTest = new ExcelTest();
        Workbook wb = ExcelUtil.cloneExelXlsxFile(file.getInputStream());
        int sheetcount = wb.getNumberOfSheets();
        for (int i = 0; i < sheetcount; i++) {
            List<List<String>> rowList = new ArrayList<>();
            rowList = ExcelUtil.readFile(wb, i, true);
            List<TestDTO> listError = new ArrayList<>();
            for (int j = 0; j < rowList.size(); j++) {
                TestDTO abc = new TestDTO(
                        rowList.get(j).get(0).replaceAll("[^\\x20-\\x7E]", "").trim(),
                        rowList.get(j).get(1),
                        rowList.get(j).get(2).replaceAll("[^\\x20-\\x7E]", "").trim(),
                        rowList.get(j).get(3));
                listError.add(abc);
            }
            excelTest.setTestDTOs(listError);
        }
        return excelTest;
    }


}


