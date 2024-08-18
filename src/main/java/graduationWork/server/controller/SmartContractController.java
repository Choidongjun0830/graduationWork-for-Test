package graduationWork.server.controller;

import graduationWork.server.domain.Transactions;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.EtherPayReceipt;
import graduationWork.server.ether.UpbitApiClient;
import graduationWork.server.ether.Web3jClient;
import graduationWork.server.service.TransactionsService;
import graduationWork.server.service.UserInsuranceService;
import graduationWork.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SmartContractController {

    private final TransactionsService transactionsService;
    private final UpbitApiClient upbitApiClient;
    private final UserInsuranceService userInsuranceService;
    private final Web3jClient web3jClient;

    @Value("${etherscan.contract.address}")
    private String contractAddress;

    //유저가 보험 가입료 입금한 영수증 내역 보여주기
    @GetMapping("/user/insurances/{userInsuranceId}/deposit/receipt")
    public String showPayPremium(@SessionAttribute(name = SessionConst.LOGIN_USER, required = true) User loginUser,
                                @PathVariable Long userInsuranceId, Model model) {

        String userWalletAddress = loginUser.getWalletAddress();
        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        List<Transactions> userTransactions = transactionsService.findByFromAndValue(userWalletAddress, userInsurance.getEtherRegisterPrice());
        Transactions transaction = userTransactions.get(0);
        model.addAttribute("transaction", transaction);
        return "ether/depositReceipt";
    }

    //유저가 받은 보험 보상 영수증 내역 보여주기
    @GetMapping("/user/insurances/{userInsuranceId}/compensation/receipt")
    public String showCompensationReceipt(@SessionAttribute(name = SessionConst.LOGIN_USER, required = true) User loginUser,
                                 @PathVariable Long userInsuranceId, Model model) {

        String userWalletAddress = loginUser.getWalletAddress();
        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        List<Transactions> userTransactions = transactionsService.findByFromToValue(contractAddress, userWalletAddress, userInsurance.getCompensationAmountEther());
        Transactions transaction = userTransactions.get(0);
        model.addAttribute("transaction", transaction);
        return "ether/compensationReceipt";
    }
}
