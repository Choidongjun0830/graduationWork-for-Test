package graduationWork.server.dto;

import graduationWork.server.domain.UserInsurance;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
public class UserInsuranceSearchDto {

    private List<UserInsurance> content;
    private long count;
}
