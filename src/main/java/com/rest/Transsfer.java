package com.rest;

import com.Service.TransferReserveService;
import com.dto.ShowModelDTO;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/transfer-reverse")
@RequiredArgsConstructor
public class Transsfer {

    private final TransferReserveService transferReserveService;

    @PostMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestBody List<ShowModelDTO> reqDTO) throws JRException {
        List<ShowModelDTO> exportPdfDTO = reqDTO;
        byte[] byteArr = transferReserveService.exportPdf(exportPdfDTO);
        HttpHeaders headers = new HttpHeaders();
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename("Reverse").build();
        headers.setContentDisposition(contentDisposition);
        MediaType mediaType = MediaType.parseMediaType("application/pdf;charset=utf-8");
        headers.setContentType(mediaType);
        return ResponseEntity.ok().headers(headers).body(byteArr);
    }
}
