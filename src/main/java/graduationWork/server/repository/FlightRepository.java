package graduationWork.server.repository;

import graduationWork.server.domain.Flight;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FlightRepository {

    private final EntityManager em;

    public void save(Flight flight) {
        em.persist(flight);
    }

    public Flight findById(Long id) {
        return em.find(Flight.class, id);
    }

    public Flight findByFlightNumDepartureDate(String flightNum, LocalDateTime departureDate) {
        List<Flight> flights = em.createQuery("select f from Flight f where f.flightNum = :flightNum and f.departureDate = :departureDate")
                .setParameter("flightNum", flightNum)
                .setParameter("departureDate", departureDate)
                .getResultList();
        if(flights.isEmpty()) {
            return null;
        }
        return flights.getFirst();
    }
}
