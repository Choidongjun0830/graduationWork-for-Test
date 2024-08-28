package graduationWork.server.dto;

import graduationWork.server.enumurate.CompensationOption;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DelaySearchApplyForm {

    @NotBlank(message = "항공편명을 입력해주세요.")
    private String flightNum;

    @NotNull(message = "출발 날짜와 시각을 입력해주세요.")
    private LocalDateTime departureDate;
}
