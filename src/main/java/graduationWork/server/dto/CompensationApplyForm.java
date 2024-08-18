package graduationWork.server.dto;

import graduationWork.server.enumurate.CompensationOption;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompensationApplyForm {

    @NotBlank(message = "Email을 입력해주세요.")
    private String email;

    @NotBlank(message = "신청 사유를 선택해주세요.")
    private String reason; //select 박스로 구현

    @NotNull(message = "발생 일자를 선택해주세요.")
    private LocalDate occurrenceDate; //발생 일자

    private LocalDate applyDate;

    @Override
    public String toString() {
        return "CompensationApplyForm{" +
                "email='" + email + '\'' +
                ", reason='" + reason + '\'' +
                ", occurrenceDate=" + occurrenceDate +
                '}';
    }
}
