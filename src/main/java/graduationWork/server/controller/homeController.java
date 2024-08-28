package graduationWork.server.controller;

import graduationWork.server.domain.Flight;
import graduationWork.server.domain.Insurance;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.DelayCompensationApplyForm;
import graduationWork.server.dto.DelaySearchApplyForm;
import graduationWork.server.dto.FlightSearchResult;
import graduationWork.server.enumurate.FlightStatus;
import graduationWork.server.service.FlightService;
import graduationWork.server.service.UserInsuranceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class homeController {

    private final FlightService flightService;
    private final UserInsuranceService userInsuranceService;

    @GetMapping("/")
    public String homeLogin(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                            @ModelAttribute("delayForm") DelaySearchApplyForm delayForm,
                            Model model) {
        if(loginUser == null) {
            return "home";
        }

        model.addAttribute("user", loginUser);

        if (loginUser.getRole().equals("ROLE_ADMIN")) {
            List<UserInsurance> userInsurances = userInsuranceService.findAll();
            model.addAttribute("userInsurances", userInsurances);
            return "adminHome";
        } else {
            return "userHome";
        }
    }

    @PostMapping("/")
    public String searchInsurance(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                  @Validated @ModelAttribute("delayForm") DelaySearchApplyForm delayForm,
                                  BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            if(loginUser == null) {
                return "home";
            } else {
                return "userHome";
            }
        }
        log.info("Received flight number: {}, departure date: {}", delayForm.getFlightNum(), delayForm.getDepartureDate());

        String flightNum = delayForm.getFlightNum();
        LocalDateTime departureDate = delayForm.getDepartureDate();
        Flight flight = flightService.getFlight(flightNum, departureDate);

        if(flight == null) {
            return "redirect:/searchResult?result=false";
        } else{
            Long flightId = flight.getId();
            return "redirect:/searchResult?result=true&flightId=" + flightId;
        }
    }

    @GetMapping("/searchResult")
    public String search(@RequestParam Boolean result,
                         @RequestParam(required = false) Long flightId,
                         Model model,
                         @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {

        FlightSearchResult searchResult = new FlightSearchResult();
        searchResult.setResult(result);

        if (result.equals(false)) {
            searchResult.setSearchResult("항공편을 찾을 수 없습니다.");
        } else if (result.equals(true) && flightId != null) {
            Flight findFlight = flightService.findOne(flightId);
            FlightStatus status = findFlight.getStatus();

            String search = "";
            if(status.equals(FlightStatus.DELAYED) || status.equals(FlightStatus.CANCELLED)){
                search = "항공기가 지연되거나 취소되어 보상 신청이 가능합니다.";
            } else {
                search = "항공기가 지연되거나 취소되지 않아 보상 신청이 불가능합니다.";
            }

            searchResult.setDeparture(findFlight.getDeparture());
            searchResult.setDestination(findFlight.getDestination());
            searchResult.setDepartureDate(findFlight.getDepartureDate());
            searchResult.setFlightNum(findFlight.getFlightNum());
            searchResult.setStatus(findFlight.getStatus());
            searchResult.setSearchResult(search);
        }
        Boolean noneLoginFlag = loginUser == null;

        model.addAttribute("searchResult", searchResult);
        model.addAttribute("noneLoginFlag", noneLoginFlag);
        return "insurance/searchResult";
    }

    @GetMapping("/sendEther")
    public String sendEther() {
        return "ether/sendEther";
    }

    @GetMapping("/sendEther/confirmation")
    public String sendConfirmation() {
        return "ether/confirmation"; // 트랜잭션이 완료된 후 이동할 페이지 경로
    }
}
