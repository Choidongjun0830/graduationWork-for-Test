package graduationWork.server.service;

import graduationWork.server.domain.Flight;
import graduationWork.server.flightApi.FlightClient;
import graduationWork.server.repository.FlightRepository;
import java.io.IOException;
import java.util.ArrayList;
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
    private final FlightClient flightClient;

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

    @Transactional
    public void setFlightFromOpenApi() throws IOException {
        ArrayList<Flight> flights = flightClient.searchFlights();

        Flight first = flights.getFirst();
        for (Flight flight : flights) {
            flightRepository.save(flight);
        }
    }
}
