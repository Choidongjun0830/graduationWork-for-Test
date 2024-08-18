package graduationWork.server.dto;

import lombok.Data;

@Data
public class EtherPayReceipt {

    private String name;

    private Long timestamp;

    private String hash;

    private String from;

    private String to;

    private String value;

    private String krwValue;

    private String compensationAmount;

    private String compensationAmountEther;

    @Override
    public String toString() {
        return "EtherPayReceipt{" +
                "name='" + name + '\'' +
                ", timestamp=" + timestamp +
                ", hash='" + hash + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", value='" + value + '\'' +
                ", krwValue='" + krwValue + '\'' +
                ", compensationAmount='" + compensationAmount + '\'' +
                ", compensationAmountEther='" + compensationAmountEther + '\'' +
                '}';
    }
}
