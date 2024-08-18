package graduationWork.server.init;

import graduationWork.server.domain.Flight;
import graduationWork.server.domain.Insurance;
import graduationWork.server.enumurate.FlightStatus;
import graduationWork.server.enumurate.InsuranceType;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class InitFlights {

    private final InitFlightService initFlightService;

    @PostConstruct
    public void init() {
        initFlightService.initFlights();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitFlightService {

        private final EntityManager em;

        public void initFlights() {
            Flight flight1 = new Flight();
            flight1.setDeparture("서울");
            flight1.setDestination("뉴욕");
            flight1.setDepartureDate(LocalDateTime.of(2024, 7, 20, 10, 30));
            flight1.setFlightNum("KE081");
            flight1.setStatus(FlightStatus.DELAYED);
            em.persist(flight1);

            Flight flight2 = new Flight();
            flight2.setDeparture("서울");
            flight2.setDestination("로스앤젤레스");
            flight2.setDepartureDate(LocalDateTime.of(2024, 7, 21, 12, 00));
            flight2.setFlightNum("KE017");
            flight2.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight2);

            Flight flight3 = new Flight();
            flight3.setDeparture("서울");
            flight3.setDestination("도쿄");
            flight3.setDepartureDate(LocalDateTime.of(2024, 7, 22, 14, 45));
            flight3.setFlightNum("KE701");
            flight3.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight3);

            Flight flight4 = new Flight();
            flight4.setDeparture("서울");
            flight4.setDestination("파리");
            flight4.setDepartureDate(LocalDateTime.of(2024, 7, 23, 16, 30));
            flight4.setFlightNum("KE901");
            flight4.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight4);

            Flight flight5 = new Flight();
            flight5.setDeparture("서울");
            flight5.setDestination("치앙마이");
            flight5.setDepartureDate(LocalDateTime.of(2024, 7, 25, 12, 30));
            flight5.setFlightNum("OZ765");
            flight5.setStatus(FlightStatus.CANCELLED);
            em.persist(flight5);

            Flight flight6 = new Flight();
            flight6.setDeparture("서울");
            flight6.setDestination("오키나와");
            flight6.setDepartureDate(LocalDateTime.of(2024, 8, 3, 10, 30));
            flight6.setFlightNum("OZ172");
            flight6.setStatus(FlightStatus.DELAYED);
            em.persist(flight6);


            Flight flight7 = new Flight();
            flight7.setDeparture("서울");
            flight7.setDestination("제주");
            flight7.setDepartureDate(LocalDateTime.of(2024, 7, 25, 15, 30));
            flight7.setFlightNum("7C111");
            flight7.setStatus(FlightStatus.DELAYED);
            em.persist(flight7);
        }
    }
}
