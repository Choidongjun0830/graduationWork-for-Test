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

//    @PostConstruct
//    public void init() {
//        initFlightService.initFlights();
//    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitFlightService {

        private final EntityManager em;

        public void initFlights() {
            Flight flight1 = new Flight();
            flight1.setDeparture("ICN");
            flight1.setDestination("SFO");
            flight1.setDepartureDate(LocalDateTime.of(2024, 7, 20, 10, 30));
            flight1.setFlightNum("KE081");
            flight1.setStatus(FlightStatus.DELAYED);
            em.persist(flight1);

            Flight flight2 = new Flight();
            flight2.setDeparture("ICN");
            flight2.setDestination("LAX");
            flight2.setDepartureDate(LocalDateTime.of(2024, 7, 21, 12, 00));
            flight2.setFlightNum("KE017");
            flight2.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight2);

            Flight flight3 = new Flight();
            flight3.setDeparture("ICN");
            flight3.setDestination("NRT");
            flight3.setDepartureDate(LocalDateTime.of(2024, 7, 22, 14, 45));
            flight3.setFlightNum("KE701");
            flight3.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight3);

            Flight flight4 = new Flight();
            flight4.setDeparture("ICN");
            flight4.setDestination("CDG");
            flight4.setDepartureDate(LocalDateTime.of(2024, 7, 23, 16, 30));
            flight4.setFlightNum("KE901");
            flight4.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight4);

            Flight flight5 = new Flight();
            flight5.setDeparture("ICN");
            flight5.setDestination("HAN");
            flight5.setDepartureDate(LocalDateTime.of(2024, 7, 25, 12, 30));
            flight5.setFlightNum("OZ765");
            flight5.setStatus(FlightStatus.CANCELLED);
            em.persist(flight5);

            Flight flight6 = new Flight();
            flight6.setDeparture("ICN");
            flight6.setDestination("OKA");
            flight6.setDepartureDate(LocalDateTime.of(2024, 8, 3, 10, 30));
            flight6.setFlightNum("OZ172");
            flight6.setStatus(FlightStatus.DELAYED);
            em.persist(flight6);


            Flight flight7 = new Flight();
            flight7.setDeparture("ICN");
            flight7.setDestination("CJU");
            flight7.setDepartureDate(LocalDateTime.of(2024, 7, 25, 15, 30));
            flight7.setFlightNum("7C111");
            flight7.setStatus(FlightStatus.DELAYED);
            em.persist(flight7);

            Flight flight8 = new Flight();
            flight8.setDeparture("ICN");
            flight8.setDestination("SFO");
            flight8.setDepartureDate(LocalDateTime.of(2024, 8, 20, 10, 30));
            flight8.setFlightNum("KE081");
            flight8.setStatus(FlightStatus.DELAYED);
            em.persist(flight8);

            Flight flight9 = new Flight();
            flight9.setDeparture("ICN");
            flight9.setDestination("LAX");
            flight9.setDepartureDate(LocalDateTime.of(2024, 8, 21, 12, 00));
            flight9.setFlightNum("KE017");
            flight9.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight9);

            Flight flight10 = new Flight();
            flight10.setDeparture("ICN");
            flight10.setDestination("NRT");
            flight10.setDepartureDate(LocalDateTime.of(2024, 8, 22, 14, 45));
            flight10.setFlightNum("KE701");
            flight10.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight10);

            Flight flight11 = new Flight();
            flight11.setDeparture("ICN");
            flight11.setDestination("CDG");
            flight11.setDepartureDate(LocalDateTime.of(2024, 8, 23, 16, 30));
            flight11.setFlightNum("KE901");
            flight11.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight11);

            Flight flight12 = new Flight();
            flight12.setDeparture("ICN");
            flight12.setDestination("HAN");
            flight12.setDepartureDate(LocalDateTime.of(2024, 8, 25, 12, 30));
            flight12.setFlightNum("OZ765");
            flight12.setStatus(FlightStatus.CANCELLED);
            em.persist(flight12);

            Flight flight13 = new Flight();
            flight13.setDeparture("ICN");
            flight13.setDestination("OKA");
            flight13.setDepartureDate(LocalDateTime.of(2024, 9, 3, 10, 30));
            flight13.setFlightNum("OZ172");
            flight13.setStatus(FlightStatus.DELAYED);
            em.persist(flight13);

            Flight flight14 = new Flight();
            flight14.setDeparture("ICN");
            flight14.setDestination("CJU");
            flight14.setDepartureDate(LocalDateTime.of(2024, 8, 25, 15, 30));
            flight14.setFlightNum("7C111");
            flight14.setStatus(FlightStatus.DELAYED);
            em.persist(flight14);

            Flight flight15 = new Flight();
            flight15.setDeparture("ICN");
            flight15.setDestination("SFO");
            flight15.setDepartureDate(LocalDateTime.of(2024, 9, 20, 10, 30));
            flight15.setFlightNum("KE081");
            flight15.setStatus(FlightStatus.DELAYED);
            em.persist(flight15);

            Flight flight16 = new Flight();
            flight16.setDeparture("ICN");
            flight16.setDestination("LAX");
            flight16.setDepartureDate(LocalDateTime.of(2024, 9, 21, 12, 00));
            flight16.setFlightNum("KE017");
            flight16.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight16);

            Flight flight17 = new Flight();
            flight17.setDeparture("ICN");
            flight17.setDestination("NRT");
            flight17.setDepartureDate(LocalDateTime.of(2024, 9, 22, 14, 45));
            flight17.setFlightNum("KE701");
            flight17.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight17);

            Flight flight18 = new Flight();
            flight18.setDeparture("ICN");
            flight18.setDestination("CDG");
            flight18.setDepartureDate(LocalDateTime.of(2024, 9, 23, 16, 30));
            flight18.setFlightNum("KE901");
            flight18.setStatus(FlightStatus.SCHEDULED);
            em.persist(flight18);

            Flight flight19 = new Flight();
            flight19.setDeparture("ICN");
            flight19.setDestination("HAN");
            flight19.setDepartureDate(LocalDateTime.of(2024, 9, 25, 12, 30));
            flight19.setFlightNum("OZ765");
            flight19.setStatus(FlightStatus.CANCELLED);
            em.persist(flight19);

            Flight flight20 = new Flight();
            flight20.setDeparture("ICN");
            flight20.setDestination("OKA");
            flight20.setDepartureDate(LocalDateTime.of(2024, 10, 3, 10, 30));
            flight20.setFlightNum("OZ172");
            flight20.setStatus(FlightStatus.DELAYED);
            em.persist(flight20);

            Flight flight21 = new Flight();
            flight21.setDeparture("ICN");
            flight21.setDestination("CJU");
            flight21.setDepartureDate(LocalDateTime.of(2024, 9, 25, 15, 30));
            flight21.setFlightNum("7C111");
            flight21.setStatus(FlightStatus.DELAYED);
            em.persist(flight21);
        }
    }
}
