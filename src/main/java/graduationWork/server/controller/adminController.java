package graduationWork.server.controller;


import graduationWork.server.domain.UploadFile;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.InsuranceSearch;
import graduationWork.server.dto.UserInsuranceSearchDto;
import graduationWork.server.email.service.EmailService;
import graduationWork.server.file.FileStore;
import graduationWork.server.service.FileService;
import graduationWork.server.service.UserInsuranceService;
import graduationWork.server.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class adminController {

    private final UserService userService;
    private final UserInsuranceService userInsuranceService;
    private final EmailService emailService;
    private final FileStore fileStore;
    private final FileService fileService;

    //보험 보상 신청 리스트
    @GetMapping("/insurance/admin/compensation/requests")
    public String compensationRequests(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
                                       @ModelAttribute InsuranceSearch insuranceSearch, HttpSession session, Model model) throws AccessDeniedException {
        if (!checkRole(session)) {
            return "error/403";
        }

        UserInsuranceSearchDto results = userInsuranceService.findAllUserInsurances(insuranceSearch, pageNo);
        List<UserInsurance> userInsurances = results.getContent();
        long count = results.getCount();
        int totalPages = (int) Math.ceil((double) count / (double) 5);

        model.addAttribute("userInsurances", userInsurances);
        model.addAttribute("count", count);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("totalPages", totalPages);
        return "admin/userInsuranceListForAdmin";
    }

    @GetMapping("/insurance/admin/join/manage")
    public String joinManage(@RequestParam Long userInsuranceId, Model model,
                             HttpSession session) throws AccessDeniedException {
        if (!checkRole(session)) {
            return "error/403";
        }

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        model.addAttribute("userInsurance", userInsurance);
        return "admin/joinManage";
    }

    @GetMapping("/insurance/admin/compensation/manage")
    public String compensationManage(@RequestParam Long userInsuranceId, Model model, HttpSession session) throws AccessDeniedException {
        if (!checkRole(session)) {
            return "error/403";
        }

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        model.addAttribute("userInsurance", userInsurance);
        return "admin/compensationManage";
    }

    @PostMapping("/insurance/admin/sendJoinMail")
    public String sendJoinMail(@RequestParam("userInsuranceId") Long userInsuranceId, Model model, HttpSession session) throws AccessDeniedException {
        if (!checkRole(session)) {
            return "error/403";
        }

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);

        String sub = "보험 가입 완료";
        emailService.sendJoinEmail(userInsuranceId, sub);

        model.addAttribute("message", "이메일이 성공적으로 전송되었습니다.");
        model.addAttribute("userInsurance", userInsurance);

        return "admin/joinEmailSuccess";
    }

    @PostMapping("/insurance/admin/sendCompensationMail")
    public String sendCompensatingMail(@RequestParam("userInsuranceId") Long userInsuranceId, Model model, HttpSession session) throws AccessDeniedException {
        if (!checkRole(session)) {
            return "error/403";
        }

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);

        String sub = "보험 보상 진행을 위한 메일 전송";
        emailService.sendAdminCompensatingEmail(userInsuranceId, sub);

        model.addAttribute("message", "이메일이 성공적으로 전송되었습니다.");
        model.addAttribute("userInsurance", userInsurance);

        return "admin/compensationEmailSuccess";
    }

    @ResponseBody
    @GetMapping("/download/files/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws MalformedURLException {

        UploadFile uploadFile = fileService.findById(fileId);

        String storeFileName = uploadFile.getStoreFileName();
        String uploadFileName = uploadFile.getUploadFileName();

        String fullPath = fileStore.getFullPath(storeFileName);

        UrlResource resource = new UrlResource("file:" + fullPath);
        String encodeUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodeUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    public boolean checkRole(HttpSession session) throws AccessDeniedException {
        User loginUser = (User) session.getAttribute("loginUser");
        return loginUser != null && loginUser.getRole().equals("ROLE_ADMIN");
    }
}
