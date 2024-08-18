package graduationWork.server.domain;

import graduationWork.server.enumurate.CompensationOption;
import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.enumurate.InsuranceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
public class UserInsurance {

    @Id @GeneratedValue
    @Column(name = "userInsurance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id")
    private Insurance insurance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //보험 가입 날짜
    private LocalDate registerDate;

    //보험 시작 날짜
    private LocalDate startDate;

    //보험 만료일
    private LocalDate endDate;

    //보험 상태 (활성, 만료, 취소) 유저한테 보여주는 것
    @Enumerated(EnumType.STRING)
    private InsuranceStatus status;

    //보상 상태
    @Enumerated(EnumType.STRING)
    private CompensationStatus compensationStatus;

    @Enumerated(EnumType.STRING)
    private CompensationOption compensationOption;

    private String registerPrice;

    private String etherRegisterPrice;

    private String reason;

    private LocalDate occurrenceDate;

    private LocalDate applyDate;

    private String compensationAmount;

    private String compensationAmountEther;

    @OneToMany(mappedBy = "userInsurance")
    private List<UploadFile> files;

    //지급 내역


    //==연관 관계 편의 메서드==//
    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getUserInsurances().contains(this)) {
            user.getUserInsurances().add(this);
        }
    }


    @Override
    public String toString() {
        return "UserInsurance{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }
}
