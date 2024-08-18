package graduationWork.server.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    private String zipCode;
    private String roadAddress;
    private String detailAddress;

    public Address() {
    }

    public Address(String zipCode, String roadAddress, String detailAddress) {
        this.zipCode = zipCode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }
}
