package graduationWork.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WalletUpdateForm {

    @NotBlank(message = "가상 화폐 지갑 주소를 입력해주세요.")
    private String accountAddress;
}
