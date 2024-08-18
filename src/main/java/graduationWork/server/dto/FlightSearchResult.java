package graduationWork.server.dto;

import graduationWork.server.enumurate.FlightStatus;
import graduationWork.server.utils.DateTimeUtils;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightSearchResult {

    private String departure;

    private String destination;

    private String flightNum;

    private LocalDateTime departureDate;

    private FlightStatus status;

    private String searchResult;

    private Boolean result;

    public String getFormattedDepartureDate() {
        return DateTimeUtils.formatDateTime(this.departureDate);
    }

    public String getFormattedStatus() {
        if(status == FlightStatus.DELAYED) {
            return "지연";
        } else if (status == FlightStatus.SCHEDULED) {
            return "정시 출발";
        } else{
            return "취소";
        }
    }
}
