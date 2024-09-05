package graduationWork.server.controller;

import graduationWork.server.domain.Address;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.AddressUpdateForm;
import graduationWork.server.dto.MemberJoinForm;
import graduationWork.server.dto.PasswordUpdateForm;
import graduationWork.server.dto.WalletUpdateForm;
import graduationWork.server.repository.InsuranceRepository;
import graduationWork.server.security.PasswordEncoder;
import graduationWork.server.service.UserInsuranceService;
import graduationWork.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserInsuranceService userInsuranceService;
    private final InsuranceRepository insuranceRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${etherscan.contract.address}")
    private String contractAddress;

    @GetMapping("/join")
    public String joinForm(@ModelAttribute MemberJoinForm memberJoinForm) {
        return "users/joinMemberForm";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute MemberJoinForm memberJoinForm, BindingResult bindingResult, Model model) {

        if(userService.checkLoginIdUnique(memberJoinForm.getLoginId())) {
            bindingResult.rejectValue("loginId", "loginIdUnique");
            return "users/joinMemberForm";
        }

        if(bindingResult.hasErrors()) {
            return "users/joinMemberForm";
        }

        Address address = new Address(memberJoinForm.getZipCode(), memberJoinForm.getRoadAddress(), memberJoinForm.getDetailAddress());

        User user = new User();
        user.setUsername(memberJoinForm.getUsername());
        user.setLoginId(memberJoinForm.getLoginId());
        user.setPassword(memberJoinForm.getPassword());
        user.setEmail(memberJoinForm.getEmail());
        user.setWalletAddress(memberJoinForm.getWalletAddress());
        user.setAddress(address);

        userService.join(user);
        return "redirect:/";
    }

    @GetMapping("/user/info")
    public String memberInfo(@ModelAttribute User user,  Model model, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
        return "users/userInfo";
    }

    @GetMapping("/user/passwordUpdate")
    public String passwordUpdateForm(@ModelAttribute PasswordUpdateForm passwordUpdateForm) {
        return "users/passwordUpdateForm";
    }

    @PostMapping("/user/passwordUpdate") //여기서 새로운 패스워드 두번 입력 같은지 검증
    public String passwordUpdate(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                 @Validated @ModelAttribute PasswordUpdateForm passwordUpdateForm,
                                 BindingResult bindingResult) {

        String typedCurrentPassword = passwordUpdateForm.getCurrentPassword();
        String encodeTyped = passwordEncoder.encode(loginUser.getLoginId(), typedCurrentPassword);

        if(!loginUser.getPassword().equals(encodeTyped)) {
            bindingResult.rejectValue("currentPassword", "currentPassword.error");
            return "users/passwordUpdateForm";
        }

        if(!passwordUpdateForm.getNewPassword().equals(passwordUpdateForm.getNewPasswordConfirm())) {
            bindingResult.rejectValue("newPasswordConfirm","passwordUpdateError");
            return "users/passwordUpdateForm";
        }

        if(bindingResult.hasErrors()){
            return "users/passwordUpdateForm";
        }

        boolean updatePassword = userService.updatePassword(loginUser.getId(), passwordUpdateForm);
        if(updatePassword){
            return "redirect:/user/info";
        }else {
            return "users/passwordUpdateForm";
        }
    }

    @GetMapping("/user/addressUpdate")
    public String addressUpdateForm(@ModelAttribute AddressUpdateForm addressUpdateForm) {
        return "users/addressUpdateForm";
    }

    @PostMapping("/user/addressUpdate")
    public String addressUpdate(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                @Validated @ModelAttribute AddressUpdateForm addressUpdateForm,
                                BindingResult bindingResult, HttpSession session) {

        Address address = new Address(addressUpdateForm.getZipCode(), addressUpdateForm.getRoadAddress(), addressUpdateForm.getDetailAddress());
        userService.updateAddress(loginUser.getId(), address);

        //업데이트 후에 loginUser 정보 갱신
        User updatedUser = userService.findOne(loginUser.getId());
        session.setAttribute(SessionConst.LOGIN_USER, updatedUser);

        return "redirect:/user/info";
    }

    @GetMapping("/user/walletAccountUpdate")
    public String walletAccountUpdateForm(@ModelAttribute WalletUpdateForm walletUpdateForm) {
        return "users/accountUpdateForm";
    }

    @PostMapping("/user/walletAccountUpdate")
    public String walletAccountUpdate(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                      @Validated @ModelAttribute WalletUpdateForm walletUpdateForm,
                                      BindingResult bindingResult, HttpSession session) {

        if(bindingResult.hasErrors()) {
            return "users/accountUpdateForm";
        }

        userService.updateWalletAddress(loginUser.getId(), walletUpdateForm.getAccountAddress());

        //업데이트 후에 loginUser 정보 갱신
        User updatedUser = userService.findOne(loginUser.getId());
        session.setAttribute(SessionConst.LOGIN_USER, updatedUser);

        return "redirect:/user/info";
    }

    @GetMapping("/user/insurances")
    public String getUserInsurances(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,Model model) {
        Long loginUserId = loginUser.getId();

        List<UserInsurance> userInsurances = userInsuranceService.findUserInsurances(loginUserId);
        model.addAttribute("contractAddress", contractAddress);
        model.addAttribute("userInsurances", userInsurances);
        return "users/insuranceListsForUser";
    }

    //유저가 가입한 보험의 세부 정보
    @GetMapping("/user/insurances/{userInsuranceId}")
    public String getUserInsuranceDetails(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                            @PathVariable("userInsuranceId") Long userInsuranceId, Model model) {

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        model.addAttribute("userInsurance", userInsurance);
        return "insurance/userInsuranceDetails";
    }
}
