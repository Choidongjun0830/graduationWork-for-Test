package graduationWork.server.dto;

import graduationWork.server.enumurate.InsuranceType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InsuranceCreateForm {

    private String name;

    private int premium;

    private int  coverageLimit;

    private InsuranceType insuranceType; //국내 여행인지 해외 여행인지
}
