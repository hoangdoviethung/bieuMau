package viethung.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
    private String chuoi;
    private String so;
    private String ngayGio;
    private String sdt;
    private String error;

    public TestDTO(String chuoi, String so, String ngayGio, String sdt) {
        this.chuoi = chuoi;
        this.so = so;
        this.ngayGio = ngayGio;
        this.sdt = sdt;
    }
}
