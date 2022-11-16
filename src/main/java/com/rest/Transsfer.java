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
    /*
    *
    *
    *
    *
    * [
    {
        "soDG": "SEATELLER20_FEERE_19102022_1668847461",
        "ngayGio": "19-10-2022 15:52:08",
        "user": "SEATELLER20",
        "bangKe": "",
        "tenKhachHang": "TRAN THI YEN",
        "gttt": "163042021",
        "noiDung": "The replaceAll() method doesn't change the contents of the original string, it returns a new string with all matches replaced.",
        "tongSoTienChi": "100000",
        "bangChu": "",
        "ngayCap": "27/10/2020",
        "maKH": "14892607",
        "noiCap": "CA NAM DINH dfdfdfdf",
        "sdt": "0855254514",
        "diaChi": "22 ngo 210, doi can KHI KI VAM KIM LU HAI BAN TRUNG HF, QUAN BA DINH, HA NOI CITY",
        "nguoiNhanTien": "TRAN THI YEN",
        "nguoiNhapLieu": "NGUYEN THI THANH PHUONG",
        "nguoiKiemSoat": "",
        "nguoiPheDuyet": "",
        "coCode": "Chi nhánh Sở giao dịch (VN0010002)",
        "type": "VND",
        "tongCong": "100000"
    },
    {
        "soDG": "SEATELLER20_FEERE_19102022_1668847461",
        "ngayGio": "19-10-2022 15:52:08",
        "user": "SEATELLER20",
        "bangKe": "",
        "tenKhachHang": "TRAN THI YEN",
        "gttt": "163042021",
        "noiDung": "note",
        "tongSoTienChi": "100000",
        "bangChu": "",
        "ngayCap": "27/10/2020",
        "maKH": "14892607",
        "noiCap": "CA NAM DINH dfdfdfdf",
        "sdt": "0855254514",
        "diaChi": "22 ngo 210, doi can KHI KI VAM KIM1 LU HAI BAN TRUNG HF",
        "nguoiNhanTien": "TRAN THI YEN",
        "nguoiNhapLieu": "NGUYEN THI THANH PHUONG",
        "nguoiKiemSoat": "",
        "nguoiPheDuyet": "",
        "coCode": "Chi nhánh Sở giao dịch (VN0010002)",
        "loaiBangKe": "Bảng kê các loại tiền lĩnh (VND)",
        "type": "VND",
        "tongCong": "100000",
        "fields": [
            {
                "menhGia": 20000,
                "soTo": 5,
                "thanhTien": 100000
            }
        ]
    }
]*/
}
