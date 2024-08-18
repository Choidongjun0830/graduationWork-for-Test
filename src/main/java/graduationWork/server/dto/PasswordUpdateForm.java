package graduationWork.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class PasswordUpdateForm {

//    @Length(max=20)
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

//    @Length(max=20)
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    private String newPassword;

//    @Length(max=20)
    @NotBlank(message = "새 비밀번호를 다시 입력해주세요.")
    private String newPasswordConfirm;


    @Override
    public String toString() {
        return "PasswordUpdateForm{" +
                "currentPassword='" + currentPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", newPasswordConfirm='" + newPasswordConfirm + '\'' +
                '}';
    }
}
