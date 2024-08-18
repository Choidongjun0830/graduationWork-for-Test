package graduationWork.server.domain;

import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.enumurate.InsuranceType;
import graduationWork.server.utils.InsuranceUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter @Setter
public class Insurance { //유저에게 가입된 보험 정보를 나타내는 용
    @Id @GeneratedValue
    @Column(name = "insurance_id")
    private Long id;
    private String name;
    private int premium;
    private String formattedPremium;
    private InsuranceType insuranceType;

    //보장 내역
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> coverageDetails = new ArrayList<>();

    @Transient
    private Map<String, String> coverageMap;

    @PostLoad
    @PostPersist
    public void initCoverageMap() {
        coverageMap = new HashMap<>();
        for(String detail: coverageDetails) {
            Map<String, String> nameAmount = InsuranceUtils.getCoverageNameAmount(detail);
            if(nameAmount != null) {
                coverageMap.put(nameAmount.get("name"), nameAmount.get("amount"));
            }
        }
    }

    public String getCoverageAmount(String coverageName) {
        String amount = coverageMap.get(coverageName);
        return amount != null ? amount : null;
    }

    @Override
    public String toString() {
        return "Insurance{" +
                "name='" + name + '\'' +
                ", premium=" + premium +
                ", formattedPremium='" + formattedPremium + '\'' +
                ", insuranceType=" + insuranceType +
                '}';
    }
}
