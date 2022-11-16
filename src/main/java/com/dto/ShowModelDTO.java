package com.dto;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
public class ShowModelDTO {
    private String coCode;
    private String soDG;
    private String ngayGio;
    private String user;
    private String bangKe;
    private String tenKhachHang;
    private String gttt;
    private String noiDung;
    @NotNull
    private String tongSoTienChi;
    private String bangChu;
    private String ngayCap;
    private String maKH;
    private String noiCap;
    private String sdt;
    private String diaChi;
    private String nguoiNhanTien;
    private String nguoiNhapLieu;
    private String nguoiKiemSoat;
    private String nguoiPheDuyet;


    private String loaiBangKe;

    private String type;
    List<Field> fields;
    private String tongCong;

    public String getTongCong() {
        return this.tongCong;
    }
}
