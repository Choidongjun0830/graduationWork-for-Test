package graduationWork.server.dto;

import graduationWork.server.enumurate.CompensationOption;
import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.enumurate.CompensationType;
import graduationWork.server.enumurate.InsuranceType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class InsuranceSearch {

    private String insuranceName;
    private String username;

    @Enumerated(EnumType.STRING)
    private CompensationOption compensationOption;

    @Enumerated(EnumType.STRING)
    private CompensationStatus compensationStatus;
}
