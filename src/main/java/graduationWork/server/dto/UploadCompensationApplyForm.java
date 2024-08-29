package graduationWork.server.dto;

import graduationWork.server.enumurate.CompensationOption;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class UploadCompensationApplyForm {

    @NotEmpty(message = "보험 서류를 하나 이상 업로드해야 합니다.") //초기화를 해서 NullPointException이 일어나지 않도록.
    private List<MultipartFile> insuranceDocuments = new ArrayList<>();
}
