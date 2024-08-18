package graduationWork.server.email.service;

import graduationWork.server.domain.UserInsurance;
import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.repository.InsuranceRepository;
import graduationWork.server.repository.UserInsuranceRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserInsuranceRepository userInsuranceRepository;
    private final InsuranceRepository insuranceRepository;

    @Value("${spring.mail.username}")
    private String sender;

    @Transactional
    public void sendAddressEmail(Long userInsuranceId, String subject) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);
        String to = userInsurance.getUser().getEmail();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(buildSendAddressEmailContent(userInsurance), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendJoinEmail(Long userInsuranceId, String subject) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);
        userInsurance.setStatus(InsuranceStatus.JOINED);
        String to = userInsurance.getUser().getEmail();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(buildJoinEmailContent(userInsurance), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendCompensatingApplyEmail(Long userInsuranceId, String subject) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);
        String to = userInsurance.getUser().getEmail();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(buildCompensatingApplyEmailContent(userInsurance), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendAdminCompensatingEmail(Long userInsuranceId, String subject) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);
        userInsurance.setCompensationStatus(CompensationStatus.COMPENSATED);
        String to = userInsurance.getUser().getEmail();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(buildAdminCompensatingEmailContent(userInsurance), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendCompensationEmail(Long userInsuranceId, String subject) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);
        String to = userInsurance.getUser().getEmail();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(buildCompensationEmailContent(userInsurance), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //보험 가입 신청시에 입금 주소 보내기
    private String buildSendAddressEmailContent(UserInsurance userInsurance) {
        StringBuilder content = new StringBuilder();

        String reason = userInsurance.getReason();
        content.append("<div style=\"max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif;\">")
                .append("<div style=\"border: 1px solid #ddd; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 20px;\">")
                .append("<h2 style=\"text-align: center; color: #007bff;\">보험 가입 신청 완료</h2>")
                .append("<h3 style=\"text-align: center; color: #007bff;\">아래 주소로 보험료를 입금해주세요.</h3>")
                .append("<h3 style=\"text-align: center; color: #007bff;\">0xE564Bd624b37C8a91E40C0fBb9D9a4058d7F6981</h3>")
                .append("<div style=\"border-top: 1px solid #ddd; padding-top: 10px;\">")
                .append("<p><strong>보험명:</strong> ").append(userInsurance.getInsurance().getName()).append("</p>")
                .append("<p><strong>회원명:</strong> ").append(userInsurance.getUser().getUsername()).append("</p>")
                .append("<p><strong>가입일:</strong> ").append(userInsurance.getRegisterDate()).append("</p>")
                .append("<p><strong>시작일:</strong> ").append(userInsurance.getStartDate()).append("</p>")
                .append("<p><strong>종료일:</strong> ").append(userInsurance.getEndDate()).append("</p>")
                .append("<p><strong>가입료(원):</strong> ").append(userInsurance.getRegisterPrice()).append("</p>")
                .append("<p><strong>가입료(이더리움):</strong> ").append(userInsurance.getEtherRegisterPrice()).append("</p>")
                .append("</div>")
                .append("</div>")
                .append("</div>");
        return content.toString();
    }

    //관리자가 입금 확인 시에 가입 완료 메일
    private String buildJoinEmailContent(UserInsurance userInsurance) {
        StringBuilder content = new StringBuilder();

        String reason = userInsurance.getReason();
        log.info("Email" + userInsurance.toString());
        content.append("<div style=\"max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif;\">")
                .append("<div style=\"border: 1px solid #ddd; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 20px;\">")
                .append("<h2 style=\"text-align: center; color: #007bff;\">보험 가입 완료</h2>")
                .append("<div style=\"border-top: 1px solid #ddd; padding-top: 10px;\">")
                .append("<p><strong>보험명:</strong> ").append(userInsurance.getInsurance().getName()).append("</p>")
                .append("<p><strong>회원명:</strong> ").append(userInsurance.getUser().getUsername()).append("</p>")
                .append("<p><strong>가입일:</strong> ").append(userInsurance.getRegisterDate()).append("</p>")
                .append("<p><strong>시작일:</strong> ").append(userInsurance.getStartDate()).append("</p>")
                .append("<p><strong>종료일:</strong> ").append(userInsurance.getEndDate()).append("</p>")
                .append("<p><strong>가입료(원):</strong> ").append(userInsurance.getRegisterPrice()).append("</p>")
                .append("<p><strong>가입료(이더리움):</strong> ").append(userInsurance.getEtherRegisterPrice()).append("</p>")
                .append("</div>")
                .append("</div>")
                .append("</div>");
        return content.toString();
    }

    //상담 후 보상
    private String buildCompensatingApplyEmailContent(UserInsurance userInsurance) {
        StringBuilder content = new StringBuilder();

        String reason = userInsurance.getReason();

        content.append("<div style=\"max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif;\">")
                .append("<div style=\"border: 1px solid #ddd; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 20px;\">")
                .append("<h2 style=\"text-align: center; color: #007bff;\">상담 후 보상 진행 신청 완료</h2>")
                .append("<div style=\"border-top: 1px solid #ddd; padding-top: 10px;\">")
                .append("<p><strong>보험명:</strong> ").append(userInsurance.getInsurance().getName()).append("</p>")
                .append("<p><strong>회원명:</strong> ").append(userInsurance.getUser().getUsername()).append("</p>")
                .append("<p><strong>신청일:</strong> ").append(userInsurance.getApplyDate()).append("</p>")
                .append("<p><strong>신청 사유:</strong> ").append(userInsurance.getReason()).append("</p>")
                .append("<p><strong>발생일:</strong> ").append(userInsurance.getOccurrenceDate()).append("</p>")
                .append("<p><strong>보상 금액(원):</strong> ").append(userInsurance.getCompensationAmount()).append("</p>")
                .append("<p><strong>보상 금액(이더리움):</strong> ").append(userInsurance.getCompensationAmountEther()).append("</p>")
                .append("</div>")
                .append("</div>")
                .append("</div>");
        return content.toString();
    }

    //보상 진행 완료 시에 메일 보내기. 상담 보상 진행 옵션일 때 관리자가 보내는 보상 진행중 메일.
    private String buildAdminCompensatingEmailContent(UserInsurance userInsurance) {
        StringBuilder content = new StringBuilder();

        String reason = userInsurance.getReason();

        content.append("<div style=\"max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif;\">")
                .append("<div style=\"border: 1px solid #ddd; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 20px;\">")
                .append("<h2 style=\"text-align: center; color: #007bff;\">보험 보상 진행을 위한 메일 전송</h2>")
                .append("<div style=\"border-top: 1px solid #ddd; padding-top: 10px;\">")
                .append("<p><strong>보험명:</strong> ").append(userInsurance.getInsurance().getName()).append("</p>")
                .append("<p><strong>회원명:</strong> ").append(userInsurance.getUser().getUsername()).append("</p>")
                .append("<p><strong>신청일:</strong> ").append(userInsurance.getApplyDate()).append("</p>")
                .append("<p><strong>신청 사유:</strong> ").append(userInsurance.getReason()).append("</p>")
                .append("<p><strong>발생일:</strong> ").append(userInsurance.getOccurrenceDate()).append("</p>")
                .append("<p><strong>보상 금액(원):</strong> ").append(userInsurance.getCompensationAmount()).append("</p>")
                .append("<p><strong>보상 금액(이더리움):</strong> ").append(userInsurance.getCompensationAmountEther()).append("</p>")
                .append("</div>")
                .append("</div>")
                .append("</div>");
        return content.toString();
    }

    //자동 보상 진행 시에 보상 완료 메일
    private String buildCompensationEmailContent(UserInsurance userInsurance) {
        StringBuilder content = new StringBuilder();

        String reason = userInsurance.getReason();

        content.append("<div style=\"max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif;\">")
                .append("<div style=\"border: 1px solid #ddd; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 20px;\">")
                .append("<h2 style=\"text-align: center; color: #007bff;\">보험 보상 완료</h2>")
                .append("<div style=\"border-top: 1px solid #ddd; padding-top: 10px;\">")
                .append("<p><strong>보험명:</strong> ").append(userInsurance.getInsurance().getName()).append("</p>")
                .append("<p><strong>회원명:</strong> ").append(userInsurance.getUser().getUsername()).append("</p>")
                .append("<p><strong>신청일:</strong> ").append(userInsurance.getApplyDate()).append("</p>")
                .append("<p><strong>신청 사유:</strong> ").append(userInsurance.getReason()).append("</p>")
                .append("<p><strong>발생일:</strong> ").append(userInsurance.getOccurrenceDate()).append("</p>")
                .append("<p><strong>보상 금액(원):</strong> ").append(userInsurance.getCompensationAmount()).append("</p>")
                .append("<p><strong>보상 금액(이더리움):</strong> ").append(userInsurance.getCompensationAmountEther()).append("</p>")
                .append("</div>")
                .append("</div>")
                .append("</div>");
        return content.toString();
    }
}
