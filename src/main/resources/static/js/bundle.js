const Buffer = require('buffer').Buffer;

// 트랜잭션 정보를 가져오는 함수
const getInfo = () => ({
  fromPrivateKey: document.getElementById('from-private-key').value,
  fromAccount: document.getElementById('from-account').value,
  toAccount: document.getElementById('to-account').value,
  value: document.getElementById('value').value,
  gasPrice: parseInt(document.getElementById('gas-price').value)
});

// 요청 중 플래그
let isRequesting = false;

// 트랜잭션을 보내는 함수
const sendTransaction = () => {
  const gasLimit = 30000; // 가스 한도를 더 높게 설정
  const gWei = 9;

  // 버튼 클릭 이벤트 리스너 등록
  const submitButton = document.getElementById('submit');
  submitButton.addEventListener('click', async () => {
    // 중복 요청 방지
    if (isRequesting) return;

    // 요청 진행 중 플래그 설정
    isRequesting = true;

    try {
      const {
        fromPrivateKey,
        fromAccount,
        toAccount,
        value,
        gasPrice
      } = getInfo();

      // 주소 유효성 검사
      if (!web3.utils.isAddress(fromAccount) || !web3.utils.isAddress(toAccount)) {
        alert('Invalid from or to address');
        isRequesting = false;
        return;
      }

      let nonce = await web3.eth.getTransactionCount(fromAccount, 'pending');
      console.log(`Nonce: ${nonce}`);

      let balance = await web3.eth.getBalance(fromAccount);
      console.log(`Account Balance: ${web3.utils.fromWei(balance, 'ether')} ETH`);

      // 잔액 확인
      if (parseFloat(web3.utils.fromWei(balance, 'ether')) < parseFloat(value)) {
        alert('Insufficient funds');
        isRequesting = false;
        return;
      }

      let rawTx = {
        nonce: web3.utils.toHex(nonce),
        gas: web3.utils.toHex(gasLimit),
        gasPrice: web3.utils.toHex(web3.utils.toWei(gasPrice.toString(), 'gwei')), // 가스 가격 설정
        from: fromAccount,
        to: toAccount,
        value: web3.utils.toHex(web3.utils.toWei(value, 'ether')),
        data: ''
      };
      console.log(`Raw Transaction: ${JSON.stringify(rawTx)}`);

      let tx = new ethereumjs.Tx(rawTx, { chain: 'sepolia' });
      let pk = Buffer.from(fromPrivateKey, 'hex');
      tx.sign(pk);

      let serializedTx = tx.serialize();
      let rawSerializedTx = '0x' + serializedTx.toString('hex');
      console.log(`Serialized Transaction: ${rawSerializedTx}`);

      document.getElementById('transaction-status').innerHTML = "트랜잭션 전송 중...";
      submitButton.disabled = true; // 버튼 비활성화

      web3.eth.sendSignedTransaction(rawSerializedTx)
          .on('transactionHash', (hash) => {
            console.log(`Transaction Hash: ${hash}`);
            alert(`Transaction submitted: https://sepolia.etherscan.io/tx/${hash}`);
            document.getElementById('transaction-status').innerHTML = "트랜잭션이 성공적으로 전송되었습니다.";
            submitButton.disabled = false; // 버튼 활성화
            isRequesting = false; // 요청 완료 플래그 해제
          })
          .on('receipt', (receipt) => {
            console.log(`Transaction Receipt: ${JSON.stringify(receipt)}`);
          })
          .on('error', (error) => {
            console.error(`Error: ${error.message}`);
            alert(`Error: ${error.message}`);
            document.getElementById('transaction-status').innerHTML = "트랜잭션 전송 중 오류가 발생했습니다.";
            submitButton.disabled = false; // 버튼 활성화
            isRequesting = false; // 요청 완료 플래그 해제
          });
    } catch (error) {
      console.error(`Exception: ${error.message}`);
      alert(`Exception: ${error.message}`);
      document.getElementById('transaction-status').innerHTML = "트랜잭션 전송 중 예외가 발생했습니다.";
      submitButton.disabled = false; // 버튼 활성화
      isRequesting = false; // 요청 완료 플래그 해제
    }
  }, { once: true }); // 이벤트 리스너를 한 번만 등록
}

// Sepolia 네트워크에 연결
const web3 = new Web3(new Web3.providers.HttpProvider('https://sepolia.infura.io/v3/YOUR_INFURA_PROJECT_ID'));

// 페이지 로드 후 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', () => {
  sendTransaction();
});
