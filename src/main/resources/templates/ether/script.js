const Buffer = require('buffer').Buffer;

// Web3, EthereumJS-TX, Buffer를 사용한 코드 작성


const getInfo = () => ({
  fromPrivateKey: document.getElementById('from-private-key').value,
  fromAccount: document.getElementById('from-account').value,
  toAccount: document.getElementById('to-account').value,
  value: document.getElementById('value').value,
  gasPrice: parseInt(document.getElementById('gas-price').value)
});

const sendTransaction = () => {
  const gasLimit = 21000;
  const gWei = 9;
  document.getElementById('submit').addEventListener('click', async () => {
    try {
      const {
        fromPrivateKey,
        fromAccount,
        toAccount,
        value,
        gasPrice
      } = getInfo();

      if (!web3.utils.isAddress(fromAccount) || !web3.utils.isAddress(toAccount)) {
        alert('Invalid from or to address');
        return;
      }

      let nonce = await web3.eth.getTransactionCount(fromAccount, 'pending');
      console.log(`Nonce: ${nonce}`);

      let balance = await web3.eth.getBalance(fromAccount);
      console.log(`Account Balance: ${web3.utils.fromWei(balance, 'ether')} ETH`);

      let rawTx = {
        nonce: web3.utils.toHex(nonce),
        gas: web3.utils.toHex(gasLimit),
        gasPrice: web3.utils.toHex(gasPrice * (10 ** gWei)),
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

      web3.eth.sendSignedTransaction(rawSerializedTx)
        .on('transactionHash', (hash) => {
          console.log(`Transaction Hash: ${hash}`);
          alert(`Transaction submitted: https://sepolia.etherscan.io/tx/${hash}`);
        })
        .on('receipt', (receipt) => {
          console.log(`Transaction Receipt: ${JSON.stringify(receipt)}`);
        })
        .on('error', (error) => {
          console.error(`Error: ${error.message}`);
          alert(`Error: ${error.message}`);
        });
    } catch (error) {
      console.error(`Exception: ${error.message}`);
      alert(`Exception: ${error.message}`);
    }
  });
}

// Sepolia 네트워크에 연결
const web3 = new Web3(new Web3.providers.HttpProvider('https://sepolia.infura.io/v3/b209d342ca714c859f2e10e608e22db9'));

// 페이지 로드 후 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', () => {
  sendTransaction();
});
