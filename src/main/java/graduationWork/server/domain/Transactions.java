package graduationWork.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Transactions {

    @Id @GeneratedValue
    @Column(name = "transaction_id")
    private Long id;

    private String name;

    private Long timestamp;

    private String hash;

    private String fromAddress;

    private String toAddress;

    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserInsurance userInsurance;
}
