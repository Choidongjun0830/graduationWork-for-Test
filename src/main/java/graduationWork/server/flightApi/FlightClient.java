package graduationWork.server.flightApi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graduationWork.server.domain.Flight;
import graduationWork.server.enumurate.FlightStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;

@Slf4j
@Component
public class FlightClient {

    @Value("${openapi.private.key}")
    private String privateKey;

    public ArrayList<Flight> searchFlights() throws IOException {

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B551177/StatusOfPassengerFlightsOdp/getPassengerDeparturesOdp"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + privateKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("from_time","UTF-8") + "=" + URLEncoder.encode("0000", "UTF-8")); /*검색 시작 시간 (HHMM)*/
        urlBuilder.append("&" + URLEncoder.encode("to_time","UTF-8") + "=" + URLEncoder.encode("2400", "UTF-8")); /*검색 종료 시간 (HHMM)*/
        urlBuilder.append("&" + URLEncoder.encode("lang","UTF-8") + "=" + URLEncoder.encode("K", "UTF-8")); /*국문=K, 영문=E, 중문=C, 일문=J, Null=K*/
        urlBuilder.append("&" + URLEncoder.encode("type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*응답유형 [xml, json] default=xml*/
        System.out.println("urlBuilder = " + urlBuilder);
        URL url = new URL(urlBuilder.toString());
        System.out.println("url = " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        int responseCode = conn.getResponseCode();
        log.info("Response code: {}", responseCode);


        if(responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //JSON 파싱
            ObjectMapper objectMapper =  new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.toString());

            JsonNode bodyNode = jsonResponse.path("response").path("body");
            JsonNode itemsNode = bodyNode.path("items");

            if(itemsNode.isArray() && itemsNode.size() > 0) {
                ArrayList<Flight> flights = new ArrayList<>();
                for (JsonNode itemNode : itemsNode) {
                    String flightId = itemNode.path("flightId").asText();
                    String arrival = itemNode.path("airportCode").asText();
                    String remark = itemNode.path("remark").asText();
                    String departureTime = itemNode.path("scheduleDateTime").asText();
                    String estimatedTime = itemNode.path("estimatedDateTime").asText();

                    Flight flight = new Flight();
                    flight.setFlightNum(flightId);
                    flight.setDeparture("ICN");
                    flight.setDestination(arrival);
                    flight.setStatus(null);

                    LocalDate today = LocalDate.now();
                    int year = today.getYear();
                    int month = today.getMonthValue();
                    int day = today.getDayOfMonth();

                    int departureHour = Integer.parseInt(departureTime.substring(0, 2));
                    int departureMin = Integer.parseInt(departureTime.substring(3, 4));
                    int departureTimeInMin = departureHour * 60 + departureMin;

                    int estimatedHour = Integer.parseInt(estimatedTime.substring(0, 2));
                    int estimatedMin = Integer.parseInt(estimatedTime.substring(3, 4));
                    int estimatedTimeInMin = estimatedHour * 60 + estimatedMin;

                    if (remark.equals("결항")) {
                        flight.setStatus(FlightStatus.CANCELLED);
                    } else if (remark.equals("지연") && (estimatedTimeInMin - departureTimeInMin > 180)) {
                        flight.setStatus(FlightStatus.DELAYED);
                    } else {
                        flight.setStatus(FlightStatus.SCHEDULED);
                    }

                    LocalDateTime departureDate = LocalDateTime.of(year, month, day, departureHour, departureMin);
                    flight.setDepartureDate(departureDate);

                    flights.add(flight);
                }
                return flights;
            } else {
                log.info("No flights found");
            }
        } else {
            log.error("Failed to get data. Response code: {}", responseCode);
        }
        return null;
    }
}
