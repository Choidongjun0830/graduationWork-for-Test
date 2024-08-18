package graduationWork.server.service;

import graduationWork.server.domain.Flight;
import graduationWork.server.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;

    @Transactional
    public Long addFlight(Flight flight) {
        flightRepository.save(flight);
        return flight.getId();
    }

    public Flight findOne(Long id) {
        return flightRepository.findById(id);
    }

    public Flight getFlight(String flightNumber, LocalDateTime departureDate) {
        return flightRepository.findByFlightNumDepartureDate(flightNumber, departureDate);
    }
}
