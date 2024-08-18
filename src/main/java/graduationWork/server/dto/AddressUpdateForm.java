package graduationWork.server.dto;

import lombok.Data;

@Data
public class AddressUpdateForm {

    private String zipCode;

    private String roadAddress;

    private String detailAddress;
}
