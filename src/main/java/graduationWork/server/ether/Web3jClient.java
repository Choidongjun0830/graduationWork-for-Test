package graduationWork.server.ether;

import graduationWork.server.dto.CompensationDto;
import graduationWork.server.email.service.EmailService;
import graduationWork.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class Web3jClient {

    private final UserService userService;
    @Value("${metamask.private.key}")
    private String privateKey; //@Value 지우고 privateKey 넣으면 됨.

    @Value("${etherscan.contract.address}")
    private String contractAddress;

    public CompensationDto fillCompensationAmount(String weiAmount) {
        Web3j web3j = Web3j.build(new HttpService("https://sepolia.infura.io/v3/b209d342ca714c859f2e10e608e22db9"));

        try {
            // Credentials 생성
            Credentials credentials = Credentials.create(privateKey);

            // 네트워크의 기본 가스 가격 가져오기
            BigInteger gasPriceWei = web3j.ethGasPrice().send().getGasPrice();
            BigDecimal gasPriceGwei = Convert.fromWei(new BigDecimal(gasPriceWei), Convert.Unit.GWEI);
            System.out.println("Current Gas Price: " + gasPriceGwei + " Gwei");

            BigDecimal highPriorityGasPriceGwei = gasPriceGwei.add(BigDecimal.valueOf(20));
            System.out.println("High Priority Gas Price: " + highPriorityGasPriceGwei + " Gwei");

            // 고우선순위 가스 가격을 Wei 단위로 변환
            BigInteger highPriorityGasPriceWei = Convert.toWei(highPriorityGasPriceGwei, Convert.Unit.GWEI).toBigIntegerExact();
            System.out.println("High Priority Gas Price: " + highPriorityGasPriceWei + " Wei");
            BigInteger replacementGasPrice = highPriorityGasPriceWei.multiply(BigInteger.valueOf(150)).divide(BigInteger.valueOf(100)); // 기존 가격의 50% 증가

            // 전송할 이더리움 양 설정
            BigInteger amountInWei = new BigInteger(weiAmount);

            BigInteger nonce = web3j.ethGetTransactionCount(
                    credentials.getAddress() , DefaultBlockParameterName.LATEST).send().getTransactionCount();
            log.info(credentials.getAddress());

            // 트랜잭션 생성
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    nonce,
                    replacementGasPrice,
                    BigInteger.valueOf(100000), // 가스 한도
                    contractAddress,
                    amountInWei
            );

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexString = Numeric.toHexString(signedMessage);

            EthSendTransaction fillTransaction = web3j.ethSendRawTransaction(hexString).send();

            CompensationDto compensationDto = new CompensationDto();

            if (fillTransaction.hasError()) {
                // 오류가 있는 경우 로그에 출력
                log.error("Transaction Error: " + fillTransaction.getError().getMessage());
            } else {
                // 오류가 없는 경우 트랜잭션 해시 출력
                String transactionHash = fillTransaction.getTransactionHash();
                log.info("Transaction Hash: " + transactionHash);

                Optional<TransactionReceipt> receiptOptional = Optional.empty();
                int attempts = 0;
                int maxAttempts = 30;
                int sleepTime = 5000;

                while (!receiptOptional.isPresent() && attempts < maxAttempts) {
                    EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
                    receiptOptional = ethGetTransactionReceipt.getTransactionReceipt();
                    if (!receiptOptional.isPresent()) {
                        System.out.println("Waiting for transaction receipt... Attempt: " + (attempts + 1));
                        Thread.sleep(sleepTime);
                        attempts++;
                    }
                }

                if (!receiptOptional.isPresent()) {
                    throw new RuntimeException("Transaction receipt not generated after sending the transaction");
                }

                TransactionReceipt receipt = receiptOptional.get();
                log.info("Transaction receipt: {}", receipt);

                EthBlock ethBlock = web3j.ethGetBlockByHash(receipt.getBlockHash(), false).send();
                EthBlock.Block block = ethBlock.getBlock();
                long timestamp = block.getTimestamp().longValueExact();

                compensationDto.setTimestamp(timestamp);
                compensationDto.setHash(transactionHash);

                log.info("Filling Compensation Amount to Transaction is success");
            }

            return compensationDto;

        } catch (IOException | InterruptedException e) {
            log.error("Error occurred during transaction: {}", e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            web3j.shutdown(); // Web3j 인스턴스 종료
        }
    }

    public CompensationDto sendCompensation(String toAddress, String weiAmount) {
        Web3j web3j = Web3j.build(new HttpService("https://sepolia.infura.io/v3/b209d342ca714c859f2e10e608e22db9"));

        try {
            // Credentials 생성
            Credentials credentials = Credentials.create(privateKey);

            // 네트워크의 기본 가스 가격 가져오기
            BigInteger gasPriceWei = web3j.ethGasPrice().send().getGasPrice();
            BigDecimal gasPriceGwei = Convert.fromWei(new BigDecimal(gasPriceWei), Convert.Unit.GWEI);
            System.out.println("Current Gas Price: " + gasPriceGwei + " Gwei");

            BigDecimal highPriorityGasPriceGwei = gasPriceGwei.add(BigDecimal.valueOf(20));
            System.out.println("High Priority Gas Price: " + highPriorityGasPriceGwei + " Gwei");

            // 고우선순위 가스 가격을 Wei 단위로 변환
            BigInteger highPriorityGasPriceWei = Convert.toWei(highPriorityGasPriceGwei, Convert.Unit.GWEI).toBigIntegerExact();
            System.out.println("High Priority Gas Price: " + highPriorityGasPriceWei + " Wei");

            // 전송할 이더리움 양 설정
            BigInteger amountInWei = new BigInteger(weiAmount);

            // 스마트 컨트랙트 함수 인코딩
            Function sendFunction = new Function(
                    "send",
                    Arrays.asList(new Address(toAddress), new Uint256(amountInWei)),
                    Collections.emptyList()
            );
            String encodedFunction = FunctionEncoder.encode(sendFunction);

            // 가스 사용량 예측
            Transaction simulationTransaction = Transaction.createEthCallTransaction(
                    credentials.getAddress(),
                    contractAddress,
                    encodedFunction
            );
            BigInteger estimatedGasLimit = web3j.ethEstimateGas(simulationTransaction).send().getAmountUsed();
            BigInteger newGasLimit = estimatedGasLimit.add(BigInteger.valueOf(10000)); // 여유 있는 가스 한도
            System.out.println("Estimated Gas Limit: " + estimatedGasLimit);
            System.out.println("New Gas Limit: " + newGasLimit);

            // 트랜잭션 리버트 여부 확인
            EthCall ethCall = web3j.ethCall(simulationTransaction, DefaultBlockParameterName.LATEST).send();
            if (ethCall.isReverted()) {
                String revertReason = ethCall.getRevertReason();
                if (revertReason != null) {
                    log.info("Transaction reverted with reason: " + revertReason);
                } else {
                    log.info("Transaction reverted without a specified reason.");
                }
                return null;
            }

            // Nonce 설정
            BigInteger nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount();

            BigInteger replacementGasPrice = highPriorityGasPriceWei.multiply(BigInteger.valueOf(150)).divide(BigInteger.valueOf(100)); // 기존 가격의 25% 증가
            System.out.println("Replacement Gas Price: " + replacementGasPrice);

            // 트랜잭션 생성
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    replacementGasPrice,
                    newGasLimit,
                    contractAddress,
                    BigInteger.ZERO,
                    encodedFunction
            );

            // 트랜잭션 전송
            RawTransactionManager rawTransactionManager = new RawTransactionManager(web3j, credentials);
            EthSendTransaction transactionResponse = rawTransactionManager.signAndSend(rawTransaction);

            if (transactionResponse.hasError()) {
                log.error("Transaction Error: " + transactionResponse.getError().getMessage());
                return null;
            }

            // 트랜잭션 해시 출력
            String transactionHash = transactionResponse.getTransactionHash();
            log.info("Transaction Hash: " + transactionHash);

            // 트랜잭션 영수증 확인 (동기 처리)
            Optional<TransactionReceipt> receiptOptional = Optional.empty();
            int attempts = 0;
            int maxAttempts = 30;
            int sleepTime = 5000;

            while (!receiptOptional.isPresent() && attempts < maxAttempts) {
                EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
                receiptOptional = ethGetTransactionReceipt.getTransactionReceipt();
                if (!receiptOptional.isPresent()) {
                    System.out.println("Waiting for transaction receipt... Attempt: " + (attempts + 1));
                    Thread.sleep(sleepTime);
                    attempts++;
                }
            }

            if (!receiptOptional.isPresent()) {
                throw new RuntimeException("Transaction receipt not generated after sending the transaction");
            }

            TransactionReceipt receipt = receiptOptional.get();
            log.info("Transaction receipt: {}", receipt);

            EthBlock ethBlock = web3j.ethGetBlockByHash(receipt.getBlockHash(), false).send();
            EthBlock.Block block = ethBlock.getBlock();
            long timestamp = block.getTimestamp().longValueExact();

            CompensationDto compensationDto = new CompensationDto();
            compensationDto.setTimestamp(timestamp);
            compensationDto.setHash(transactionHash);

            log.info("Compensation is success");

            return compensationDto;

        } catch (IOException | InterruptedException e) {
            log.error("Error occurred during transaction: {}", e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            web3j.shutdown(); // Web3j 인스턴스 종료
        }
    }

}
