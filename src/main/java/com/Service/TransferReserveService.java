package com.Service;

import com.Utils.DateUtils;
import com.Utils.NumToVietnameseWordUtils;
import com.dto.Field;
import com.dto.ShowModelDTO;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class TransferReserveService {
    public byte[] exportPdf(List<ShowModelDTO> showModelDTOS) throws JRException {
        log.debug("Start exportPdf transfer_reserve info {}", showModelDTOS);

        if (showModelDTOS != null && showModelDTOS.size() > 0) {
            try {
                List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
                // khai báo các parameter
                Map<String, Object> parameters = new HashMap<>();
                String type = showModelDTOS.get(0).getType();

                for (int i = 0; i < showModelDTOS.size() - 1; i++) {
                    JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("reports/ReverseReport.jrxml").getInputStream());
                    parameters.put("customerId", showModelDTOS.get(i).getMaKH());
                    parameters.put("fullName", showModelDTOS.get(i).getTenKhachHang());
                    parameters.put("phone", showModelDTOS.get(i).getSdt());
                    parameters.put("address", showModelDTOS.get(i).getDiaChi());
                    parameters.put("identifierNumber", showModelDTOS.get(i).getGttt());
                    parameters.put("identifierDateOfIssue", showModelDTOS.get(i).getNgayCap());
                    parameters.put("identifierPlaceOfIssue", showModelDTOS.get(i).getNoiCap());
                    parameters.put("coCode", showModelDTOS.get(i).getCoCode());

                    String cur = showModelDTOS.get(i).getType();
                    String total = showModelDTOS.get(i).getTongSoTienChi();
                    if ("VND".equals(cur)) {
                        String totalView = NumToVietnameseWordUtils.convertToCommas(total);
                        Long totalD = Long.parseLong(total);
                        String totalWords = NumToVietnameseWordUtils.num2String(totalD);
                        parameters.put("total", totalView + " " + cur);
                        parameters.put("totalWords", totalWords + " đồng");
                    } else {
                        parameters.put("total", NumToVietnameseWordUtils.convertToCommasUSD(total) + " " + cur);
                        parameters.put("totalWords", NumToVietnameseWordUtils.num2StringEURO(total, cur));
                    }
                    parameters.put("detailOfPayment", showModelDTOS.get(i).getNoiDung());
                    //parameters.put("logo", ClassLoader.getSystemResourceAsStream("images/logo.png"));
                    parameters.put("logo", new ClassPathResource("images/logo.png").getInputStream());
                    parameters.put("transactionT24", showModelDTOS.get(i).getSoDG());
                    parameters.put("createDate", showModelDTOS.get(i).getNgayGio());
                    parameters.put("user", showModelDTOS.get(i).getUser());
                    parameters.put("cashier", showModelDTOS.get(i).getNguoiPheDuyet());
                    parameters.put("teller", showModelDTOS.get(i).getNguoiNhapLieu());
                    parameters.put("authorier", showModelDTOS.get(i).getNguoiPheDuyet());
                    parameters.put("bangke", showModelDTOS.get(i).getBangKe());
                    parameters.put("nguoiNhan", showModelDTOS.get(i).getNguoiNhanTien());
                    if ("VND".equals(cur)) {
                        parameters.put("bangChuCai", NumToVietnameseWordUtils.num2String(Long.valueOf(showModelDTOS.get(i).getTongCong())) + " đồng");
                    } else {
                        parameters.put("bangChuCai", NumToVietnameseWordUtils.num2StringEURO(showModelDTOS.get(i).getTongCong(), cur));
                    }
                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
                    jasperPrintList.add(jasperPrint);
                }
                // check trường hợp có bảng kê
                ShowModelDTO showModelDTO = showModelDTOS.get(showModelDTOS.size() - 1);
                if (showModelDTO.getFields() != null && showModelDTO.getFields().size() > 0) {
                    JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("reports/InventoryReverseReport.jrxml").getInputStream());
                    parameters.put("customerId", showModelDTO.getMaKH());
                    parameters.put("fullName", showModelDTO.getTenKhachHang());
                    parameters.put("phone", showModelDTO.getSdt());
                    parameters.put("address", showModelDTO.getDiaChi());
                    parameters.put("identifierNumber", showModelDTO.getGttt());
                    parameters.put("identifierDateOfIssue", showModelDTO.getNgayCap());
                    parameters.put("identifierPlaceOfIssue", showModelDTO.getNoiCap());
                    parameters.put("coCode", showModelDTO.getCoCode());
                    parameters.put("ngayThang", DateUtils.getDayCurrent());
                    String cur = showModelDTO.getType();
                    parameters.put("detailOfPayment", showModelDTO.getNoiDung());
                    parameters.put("logo", new ClassPathResource("images/logo.png").getInputStream());
                    parameters.put("transactionT24", showModelDTO.getSoDG());
                    parameters.put("createDate", showModelDTO.getNgayGio());
                    parameters.put("user", showModelDTO.getUser());
                    parameters.put("cashier", showModelDTO.getNguoiPheDuyet());
                    parameters.put("teller", showModelDTO.getNguoiNhapLieu());
                    parameters.put("authorier", showModelDTO.getNguoiPheDuyet());
                    parameters.put("bangke", showModelDTO.getBangKe());
                    parameters.put("nguoiNhan", showModelDTO.getNguoiNhanTien());
                    parameters.put("tongCong", NumToVietnameseWordUtils.convertToCommas(showModelDTO.getTongCong()));
                    if("VND".equals(cur)) {
                        parameters.put("bangChuCai", NumToVietnameseWordUtils.num2String(Long.valueOf(showModelDTO.getTongCong())) + " đồng");
                    }else {
                        parameters.put("bangChuCai", NumToVietnameseWordUtils.num2StringEURO(showModelDTO.getTongCong(),cur));
                    }
                    parameters.put("loaiBangKe", showModelDTO.getLoaiBangKe());

                    List<Field> listItems = showModelDTO.getFields();
                    if (listItems != null && listItems.size() > 0) {
                        listItems.forEach(inventory -> {
                            String menhGia = inventory.getMenhGia();
                            inventory.setMenhGia(NumToVietnameseWordUtils.convertToCommasUSD(menhGia));
                            String thanhTien = inventory.getThanhTien();
                            inventory.setThanhTien(NumToVietnameseWordUtils.convertToCommasUSD(thanhTien));
                        });
                        // khởi tạo data source
                        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listItems);
                        parameters.put("listInventory", dataSource);
                    }


                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
                    jasperPrintList.add(jasperPrint);
                } else {
                    JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("reports/ReverseReport.jrxml").getInputStream());
                    parameters.put("customerId", showModelDTO.getMaKH());
                    parameters.put("fullName", showModelDTO.getTenKhachHang());
                    parameters.put("phone", showModelDTO.getSdt());
                    parameters.put("address", showModelDTO.getDiaChi());
                    parameters.put("identifierNumber", showModelDTO.getGttt());
                    parameters.put("identifierDateOfIssue", showModelDTO.getNgayCap());
                    parameters.put("identifierPlaceOfIssue", showModelDTO.getNoiCap());
                    parameters.put("coCode", showModelDTO.getCoCode());

                    String cur = showModelDTO.getType();
                    String total = showModelDTO.getTongSoTienChi();
                    if ("VND".equals(cur)) {
                        String totalView = NumToVietnameseWordUtils.convertToCommas(total);
                        Long totalD = Long.parseLong(total);
                        String totalWords = NumToVietnameseWordUtils.num2String(totalD);
                        parameters.put("total", totalView + " " + cur);
                        parameters.put("totalWords", totalWords + " đồng");
                    } else {
                        parameters.put("total", NumToVietnameseWordUtils.convertToCommasUSD(total) + " " + cur);
                        parameters.put("totalWords", NumToVietnameseWordUtils.num2StringEURO(total, cur));
                    }
                    parameters.put("detailOfPayment", showModelDTO.getNoiDung());
                    //parameters.put("logo", ClassLoader.getSystemResourceAsStream("images/logo.png"));
                    parameters.put("logo", new ClassPathResource("images/logo.png").getInputStream());
                    parameters.put("transactionT24", showModelDTO.getSoDG());
                    parameters.put("createDate", showModelDTO.getNgayGio());
                    parameters.put("user", showModelDTO.getUser());
                    parameters.put("cashier", showModelDTO.getNguoiPheDuyet());
                    parameters.put("teller", showModelDTO.getNguoiNhapLieu());
                    parameters.put("authorier", showModelDTO.getNguoiPheDuyet());
                    parameters.put("bangke", showModelDTO.getBangKe());
                    parameters.put("nguoiNhan", showModelDTO.getNguoiNhanTien());
                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
                    jasperPrintList.add(jasperPrint);
                }

                JRPdfExporter exporter = new JRPdfExporter();
                ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfOutputStream));

                SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
                reportConfig.setSizePageToContent(true);
                reportConfig.setForceLineBreakPolicy(false);
                exporter.exportReport();
                return pdfOutputStream.toByteArray();
            } catch (JRException | IOException e) {
                throw new JRException(e.getMessage());
            }
        } else {
            return null;
        }
    }

}
