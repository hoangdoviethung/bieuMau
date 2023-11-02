package viethung.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaoCao {
    private String stt;
    private String maKH;
    private String tenKH;
    private String tenTrenThe;
    private String soTaiKhoan;
    private String loaiThe;
    private String soThe;
    private String ngayDongTheT24;
    private String ngayDongTheW4;
    private String chiNhanhMoThe;
    private String nvDongThe;
    private String tenNVDongThe;
    private String maCNDongThe;
    private String tenCNDongThe;
    private String maHanMuc;
    private String giaTriHanMuc;
    private String tinhTrangDong;
    private String ngayDongHanMuc;
    private String cnDongHanMuc;
    private String nvDongHanMuc;
    private String nvDuyetDongHanMuc;
    private String ghiChuTrenHanMuc;
    private String ghiChu;
    private String ngayDuKienDongHanMuc;

    private String tinhTrangTaiKhoan;
    private String theChinhPhu;
    private String contrStatus;
}
