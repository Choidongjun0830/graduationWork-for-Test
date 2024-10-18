package graduationWork.server.domain;

import graduationWork.server.enumurate.FlightStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Flight {

    //예약 대행사
    @Id @GeneratedValue
    @Column(name = "flight_id")
    private Long id;

    private String departure;

    private String destination;

    private LocalDateTime departureDate;

    private String flightNum;

    @Enumerated(EnumType.STRING)
    private FlightStatus status;

    @Override
    public String toString() {
        return "Flight{" +
                "departure='" + departure + '\'' +
                ", destination='" + destination + '\'' +
                ", departureDate=" + departureDate +
                ", flightNum='" + flightNum + '\'' +
                ", status=" + status +
                '}';
    }
}
