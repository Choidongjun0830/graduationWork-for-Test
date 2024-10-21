package graduationWork.server.ether;

import graduationWork.server.domain.Transactions;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.EtherPayReceipt;
import graduationWork.server.email.service.EmailService;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.service.TransactionsService;
import graduationWork.server.service.UserInsuranceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentChecker {

    private final EtherscanApiClient etherscanApiClient;
    private final UserInsuranceService userInsuranceService;
    private final TransactionsService transactionsService;
    private final EmailService emailService;

    @Value("${etherscan.contract.address}")
    private String contractAddress;

    @Transactional
    @Scheduled(fixedRate = 15000) //1분마다 실행
    public void checkPayment() {
        checkUserPayments();
    }

    private void checkUserPayments() {
        List<UserInsurance> allPendingUserInsurances = userInsuranceService.findAllPendingUserInsurances();
        log.info("Pending user insurances: {}", allPendingUserInsurances);
        for (UserInsurance userInsurance : allPendingUserInsurances) {
            log.info("Checking user insurance: {}", userInsurance);
            String from = userInsurance.getUser().getWalletAddress();
            String etherRegisterPrice = userInsurance.getEtherRegisterPrice();

            log.info("from: {}", from);
            log.info("Ether register price: {}", etherRegisterPrice);

            EtherPayReceipt etherPayReceipt = etherscanApiClient.checkPayment(from, Double.parseDouble(etherRegisterPrice));
            log.info("payment received: {}", etherPayReceipt);
            if (etherPayReceipt != null) {
                //거래 상태 업데이트
                userInsurance.setStatus(InsuranceStatus.JOINED);
                //트랜잭션 테이블 생각해보기
                String name = userInsurance.getInsurance().getName() + " 가입 - " + userInsurance.getUser().getUsername();
                transactionsService.save(name, userInsurance.getId(), userInsurance.getUser().getId(), from, contractAddress, etherRegisterPrice, etherPayReceipt);

                notifyUser(userInsurance.getId());
            }
        }
    }

    private void notifyUser(Long userInsuranceId) {
        String sub = "보험 가입 완료";
        emailService.sendJoinEmail(userInsuranceId, sub);
    }
}
