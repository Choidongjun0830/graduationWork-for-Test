package graduationWork.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InsuranceJoinDateSelectForm {

    @NotNull(message = "시작 날짜를  입력해주세요.")
    private LocalDate startDate;

    @NotNull(message = "종료 날짜를 입력해주세요.")
    private LocalDate endDate;
}
