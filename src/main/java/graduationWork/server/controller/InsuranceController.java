package graduationWork.server.controller;

import graduationWork.server.domain.*;
import graduationWork.server.dto.*;
import graduationWork.server.email.service.EmailService;
import graduationWork.server.enumurate.CompensationOption;
import graduationWork.server.enumurate.FlightStatus;
import graduationWork.server.enumurate.InsuranceType;
import graduationWork.server.ether.UpbitApiClient;
import graduationWork.server.ether.Web3jClient;
import graduationWork.server.file.FileStore;
import graduationWork.server.service.*;
import graduationWork.server.utils.DateTimeUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class InsuranceController {

    private final UserService userService;
    private final InsuranceService insuranceService;
    private final UserInsuranceService userInsuranceService;
    private final FileStore fileStore;
    private final FlightService flightService;
    private final Web3jClient web3jClient;
    private final TransactionsService transactionsService;
    private final UpbitApiClient upbitApiClient;
    private final EmailService emailService;

    @Value("${etherscan.contract.address}")
    private String contractAddress;

    @GetMapping("/insurance/new")
    public String join(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {

        if (loginUser.getWalletAddress() == null || loginUser.getWalletAddress().isEmpty()) {
            model.addAttribute("msg", "가상 화폐 주소를 등록해야 보험 가입이 가능합니다.");
            model.addAttribute("url", "/");
            return "alert";
        }

        return "insurance/travelTypeSelectFrom";
    }

    @GetMapping("/insurance/new/domestic")
    public String joinDomestic(Model model, HttpSession session) {
        List<Insurance> domesticInsurances = insuranceService.findAllInsurancesByType(InsuranceType.DOMESTIC);
        model.addAttribute("domesticInsurances", domesticInsurances);
        return "insurance/domesticSelect";

    }

    @GetMapping("/insurance/new/overseas")
    public String joinOverseas(Model model, HttpSession session) {
        List<Insurance> overseasInsurances = insuranceService.findAllInsurancesByType(InsuranceType.OVERSEAS);
        model.addAttribute("overseasInsurances", overseasInsurances);
        return "insurance/overseaSelect";
    }

    @GetMapping("/insurance/new/{insuranceId}")
    public String registerInsurance(@PathVariable Long insuranceId,
                                    @ModelAttribute("dateSelectForm")InsuranceJoinDateSelectForm dateSelectForm,
                                    Model model) {
        Insurance findInsurance = insuranceService.findOneInsurance(insuranceId);

        model.addAttribute("insurance", findInsurance);

        return "insurance/registerInsuranceForm";
    }

    @PostMapping("/insurance/new/{insuranceId}")
    public ResponseEntity<?> registerInsuranceProc(@PathVariable Long insuranceId,
                                                   @SessionAttribute(name = SessionConst.LOGIN_USER) User loginUser,
                                                   @Valid @ModelAttribute("dateSelectForm")InsuranceJoinDateSelectForm dateSelectForm,
                                                   BindingResult bindingResult,
                                                   HttpSession session, Model model) {

        Map<String, String> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.info(bindingResult.getFieldError().getDefaultMessage());
                response.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            model.addAttribute("insurance", insuranceService.findOneInsurance(insuranceId));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // 보험 가입 처리
        Long loginUserId = loginUser.getId();
        LocalDate startDate = dateSelectForm.getStartDate();
        LocalDate endDate = dateSelectForm.getEndDate();

        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now()) || startDate.isAfter(endDate)) {
            bindingResult.rejectValue("endDate","insuranceJoinDateError", "시작 날짜와 종료 날짜의 입력이 잘못되었습니다.");
            model.addAttribute("insurance", insuranceService.findOneInsurance(insuranceId));
            if (bindingResult.hasErrors()) {
                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                    response.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        Long userInsuranceId = userInsuranceService.joinApplyInsurance(insuranceId, loginUserId, startDate, endDate); // 가입 신청

        session.setAttribute("userInsuranceId", userInsuranceId);

        // JSON 형태로 리다이렉트 URL 반환
        return ResponseEntity.ok().body("{\"redirectUrl\":\"/insurance/new/confirm\"}");
    }


    @GetMapping("/insurance/new/confirm")
    public String registerConfirm(Model model, HttpSession session) {

        Long userInsuranceId = (Long) session.getAttribute("userInsuranceId");
        if(userInsuranceId == null) {
            return "redirect:/insurance/new";
        }

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);

        model.addAttribute("contractAddress", contractAddress);
        model.addAttribute("userInsurance", userInsurance);

        return "insurance/joinApplySuccess";
    }

    //여기까지 보험 신청 아래는 보험 보상 신청//

    @GetMapping("insurance/compensation/apply")
    public String compensationForm(@RequestParam Long userInsuranceId,
                                   @ModelAttribute("form") CompensationApplyForm form,
                                   Model model,
                                   HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser.getWalletAddress() == null || loginUser.getWalletAddress().isEmpty()) {
            model.addAttribute("msg", "가상 화폐 주소를 등록해야 보상 신청이 가능합니다.");
            model.addAttribute("url", "/user/info");
            return "alert";
        }

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);

        form.setEmail(loginUser.getEmail());
        model.addAttribute("userInsurance", userInsurance);

        Map<String, String> coverageMap = userInsurance.getInsurance().getCoverageMap();
        if (coverageMap == null) {
            coverageMap = new HashMap<>();
        }
        model.addAttribute("coverageMap", coverageMap);

        return "insurance/compensationApply";
    }

    @PostMapping("insurance/compensation/apply")
    public String compensationApply(@RequestParam Long userInsuranceId,
                                    @Validated @ModelAttribute("form") CompensationApplyForm form,
                                    BindingResult bindingResult,
                                    Model model,
                                    HttpSession session) {

        LocalDate occurrenceDate = form.getOccurrenceDate();
        if (!isDateValid(occurrenceDate, userInsuranceId)) {
            bindingResult.rejectValue("occurrenceDate", "InvalidOccurrenceDate", "발생 일자가 가입 기간에 속하지 않습니다. 다시 입력해주세요.");
        }

        if(bindingResult.hasErrors()) {
            UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
            model.addAttribute("userInsurance", userInsurance);
            model.addAttribute("coverageMap", userInsurance.getInsurance().getCoverageMap());
            return "insurance/compensationApply";
        }

        User loginUser = (User) session.getAttribute("loginUser");
        session.setAttribute("applyForm", form);
        userInsuranceService.applyFirstCompensationForm(userInsuranceId, loginUser.getId(), form);
        if (form.getReason().equals("항공기 및 수하물 지연 보상")) {
            return "redirect:/insurance/compensation/apply/flightDelay?userInsuranceId=" + userInsuranceId;
        }
        else {
            return "redirect:/insurance/compensation/apply/upload?userInsuranceId=" + userInsuranceId;
        }
    }

    @GetMapping("insurance/compensation/apply/flightDelay")
    public String flightDelayForm(@RequestParam Long userInsuranceId,
                                  @ModelAttribute("delayForm") DelayCompensationApplyForm delayForm,
                                  Model model, HttpSession session) {

        model.addAttribute("userInsuranceId", userInsuranceId);
        return "insurance/flightCompensationApply";
    }

    @ResponseBody
    @PostMapping("insurance/compensation/apply/flightDelay")
    public ResponseEntity<?> flightDelayApply(@RequestParam Long userInsuranceId,
                                              @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                              @Valid @ModelAttribute("delayForm") DelayCompensationApplyForm delayForm,
                                              BindingResult bindingResult,
                                              Model model,
                                              HttpSession session) {

        model.addAttribute("userInsuranceId", userInsuranceId);
        Map<String, String> response = new HashMap<>();

        LocalDate localDateDepartureDate = delayForm.getDepartureDate().toLocalDate();
        LocalDate occurrenceDate = userInsuranceService.findOne(userInsuranceId).getOccurrenceDate();
        if(!occurrenceDate.isEqual(localDateDepartureDate)) {
            bindingResult.rejectValue("departureDate", "InvalidDepartureDate", "발생 일자와 항공편의 예정 출발 시간이 일치하지 않습니다.");
        }

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                response.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String formFlightNum = delayForm.getFlightNum();
        LocalDateTime formDepartureDate = delayForm.getDepartureDate();

        Flight findFlight = flightService.getFlight(formFlightNum, formDepartureDate);
        String dateTime = DateTimeUtils.formatDateTime(formDepartureDate);

        // 항공편이 없을 때
        if (findFlight == null) {
            bindingResult.rejectValue("flightNum", "flightNotFound", new Object[]{formFlightNum, dateTime}, "존재하는 항공편이 없습니다.");
            if (bindingResult.hasErrors()) {
                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                    response.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        // 항공편이 예정 상태일 때
        if (findFlight.getStatus() == FlightStatus.SCHEDULED) {
            bindingResult.rejectValue("flightNum", "flightDelayApplyNotDelayedNotCancelled", new Object[]{formFlightNum, dateTime}, "항공편의 상태가 지연이나 취소가 아닙니다.");
            if (bindingResult.hasErrors()) {
                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                    response.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        // 모든 검증을 통과한 경우
        userInsuranceService.applyDelayCompensation(userInsuranceId, loginUser.getId(), delayForm);

        // JSON 형태로 리다이렉트 URL 반환
        return ResponseEntity.ok(Map.of("redirectUrl", "/insurance/compensation/apply/flightDelay/confirm?userInsuranceId=" + userInsuranceId));
    }

    //보상 완료
    @GetMapping( "/insurance/compensation/apply/flightDelay/confirm")
    public String flightCompensationConfirm(@RequestParam Long userInsuranceId, Model model,
                                            @SessionAttribute(name = SessionConst.LOGIN_USER) User loginUser) {

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        CompensationOption option = userInsurance.getCompensationOption();
        String compensationAmountEther = userInsurance.getCompensationAmountEther();
        String compensationAmount = userInsurance.getCompensationAmount();
        BigInteger compensationAmountInWei = Convert.toWei(compensationAmountEther, Convert.Unit.ETHER).toBigInteger();

        User user = userInsurance.getUser();

        if (option == CompensationOption.OPTION_AUTO) {
            String amount = userInsurance.getCompensationAmount();
            String userWalletAddress = user.getWalletAddress();

            CompensationDto fillCompensationAmount = web3jClient.fillCompensationAmount(String.valueOf(compensationAmountInWei));
            CompensationDto compensationDto = web3jClient.sendCompensation(userInsurance.getUser().getWalletAddress(), String.valueOf(compensationAmountInWei));
            Long timestamp = compensationDto.getTimestamp();
            String hash = compensationDto.getHash();
            String name = userInsurance.getInsurance().getName() + " 보상 - " + userInsurance.getUser().getUsername();

            EtherPayReceipt etherPayReceipt = new EtherPayReceipt();
            etherPayReceipt.setName(name);
            etherPayReceipt.setTimestamp(timestamp);
            etherPayReceipt.setFrom(contractAddress);
            etherPayReceipt.setTo(userWalletAddress);
            etherPayReceipt.setHash(hash);
            etherPayReceipt.setValue(compensationAmountEther);
            etherPayReceipt.setKrwValue(compensationAmount);

            model.addAttribute("etherPayReceipt", etherPayReceipt);
            //DB에 트랜잭션 저장까지
            transactionsService.save(name, userInsuranceId, user.getId(), contractAddress, userWalletAddress, compensationAmountEther, etherPayReceipt);
            //보상 완료 이메일 전송
            emailService.sendCompensationEmail(userInsuranceId, "보험 보상 완료");

            return "insurance/flightAutoCompensationConfirm";
        }
        else if (option == CompensationOption.OPTION_EMAIL) {
            model.addAttribute("userInsurance", userInsuranceService.findOne(userInsuranceId));
            emailService.sendCompensatingApplyEmail(userInsuranceId, "상담 후 보상 진행 신청 완료");
            return "insurance/emailCompensationConfirm";
        }
        else{
            return "redirect:/";
        }
    }

    //파일 업로드 폼
    @GetMapping("/insurance/compensation/apply/upload")
    public String uploadForm(@RequestParam Long userInsuranceId,
                             @ModelAttribute("uploadForm") UploadCompensationApplyForm uploadForm, Model model, HttpSession session) {

        model.addAttribute("userInsuranceId", userInsuranceId);
        return "insurance/fileUploadForm";
    }

    //파일 업로드
    @PostMapping("/insurance/compensation/apply/upload")
    public ResponseEntity<?> upload(@RequestParam Long userInsuranceId,
                                    @SessionAttribute(name = SessionConst.LOGIN_USER) User loginUser,
                                    @ModelAttribute("uploadForm") UploadCompensationApplyForm uploadForm,
                                    BindingResult bindingResult) throws IOException {

        Map<String, String> response = new HashMap<>();

        if (uploadForm.getInsuranceDocuments().isEmpty() || uploadForm.getInsuranceDocuments().stream().allMatch(MultipartFile::isEmpty)) {
            bindingResult.rejectValue("insuranceDocuments", "insuranceDocuments.empty", "보험 서류를 하나 이상 제출해야 합니다.");
        }

        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.info(fieldError.getDefaultMessage());
                response.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


        // 보상 신청 업로드 처리
        userInsuranceService.applyUploadCompensation(userInsuranceId, loginUser.getId(), uploadForm);

        // JSON 형태로 리다이렉트 URL 반환
        response.put("redirectUrl", "/insurance/compensation/apply/upload/confirm?userInsuranceId=" + userInsuranceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("insurance/compensation/apply/upload/confirm")
    public String uploadConfirm(@RequestParam Long userInsuranceId, Model model) {
        emailService.sendCompensatingApplyEmail(userInsuranceId, "상담 후 보상 진행 신청 완료");

        model.addAttribute("userInsurance", userInsuranceService.findOne(userInsuranceId));
        return "insurance/emailCompensationConfirm";
    }

    //보험 보장 내역
    @GetMapping("/insurance/details/{insuranceId}")
    public String insuranceDetails(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                   @PathVariable Long insuranceId, Model model) {

        Insurance insurance = insuranceService.findOneInsurance(insuranceId);
        model.addAttribute("insurance", insurance);

        return "insurance/insuranceDetails";
    }

    @GetMapping("/insurance/domestic")
    public String domesticInsuranceLists(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                         Model model) {
        List<Insurance> domesticInsurances = insuranceService.findAllInsurancesByType(InsuranceType.DOMESTIC);
        model.addAttribute("domesticInsurances", domesticInsurances);

        return "insurance/domesticInsuranceLists";
    }

    @GetMapping("/insurance/oversea")
    public String overseaInsuranceLists(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                        Model model) {
        List<Insurance> overseasInsurances = insuranceService.findAllInsurancesByType(InsuranceType.OVERSEAS);
        model.addAttribute("overseasInsurances", overseasInsurances);

        return "insurance/overseaInsuranceLists";
    }

    private boolean isDateValid(LocalDate date, Long userInsuranceId) {
        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        return !date.isBefore(userInsurance.getStartDate()) // 시작일과 같거나 이후
                && !date.isAfter(userInsurance.getEndDate()); // 종료일과 같거나 이전
    }
}