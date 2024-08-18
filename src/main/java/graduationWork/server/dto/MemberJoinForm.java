package graduationWork.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberJoinForm {

    @NotBlank(message = "이름을 입력해주세요.")
    private String username;

    @NotBlank(message = "로그인 아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    private String walletAddress;

    private String zipCode;

    private String roadAddress;

    private String detailAddress;
}
