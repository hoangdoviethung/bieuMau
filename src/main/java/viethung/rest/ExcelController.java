package viethung.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import viethung.Service.ReportService;
import viethung.dto.DailyLimitClosingReportReq;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import viethung.dto.ExcelTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExcelController {

    @Autowired
    ReportService reportService;

    @PostMapping("/abc") //báo cáo đóng hạn mức theo ngày, bc thẻ đóng theo ngày, hạn mức cần đóng
    public ResponseEntity<byte[]> abc(@RequestParam("file") MultipartFile file) throws Exception {
        ByteArrayOutputStream outputStream = reportService.convertListExcel(file);
        String fileNameExcel = "def";
        byte[] byteArr = outputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileNameExcel + ".xlsx").build();
        headers.setContentDisposition(contentDisposition);
        MediaType mediaType = MediaType.parseMediaType("application/octet-stream");
        headers.setContentType(mediaType);
        return ResponseEntity.ok().headers(headers).body(byteArr);
    }

    @PostMapping("/excel") //báo cáo đóng hạn mức theo ngày, bc thẻ đóng theo ngày, hạn mức cần đóng
    public ResponseEntity<byte[]> exportDailyLimitClosingReportExcel(@RequestBody DailyLimitClosingReportReq reqDTO) throws JRException, IOException {
        ByteArrayOutputStream outputStream = reportService.exportDailyLimitClosingReportExcel(reqDTO);
        String fileNameExcel = "abc";
        byte[] byteArr = outputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileNameExcel + ".xlsx").build();
        headers.setContentDisposition(contentDisposition);
        MediaType mediaType = MediaType.parseMediaType("application/octet-stream");
        headers.setContentType(mediaType);
        return ResponseEntity.ok().headers(headers).body(byteArr);
    }
}
