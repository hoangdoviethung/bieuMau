package viethung.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExcelTest {
    private Integer totalError;
    private Integer totalSuccess;
    private Integer total;
    private List<TestDTO> testDTOs;
}
