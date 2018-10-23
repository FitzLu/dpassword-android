package io.nebulas.examples;

import java.math.BigInteger;

import io.nebulas.account.AccountManager;
import io.nebulas.core.Address;
import io.nebulas.core.Transaction;
import io.nebulas.core.TransactionBinaryPayload;
import io.nebulas.core.TransactionCallPayload;
import io.nebulas.core.TransactionDeployPayload;
import io.nebulas.crypto.keystore.PrivateKey;
import io.nebulas.crypto.keystore.PublicKey;

//import rpcpb.ApiServiceGrpc;

public class TransactionExample {

    private static byte[] passphrase = "passphrase".getBytes();

    public static void main(String args[]) throws Exception {
        AccountManager manager = new AccountManager();

        // binary tx
        int chainID = 100; //1 mainet,1001 testnet, 100 default private
        Address from = manager.newAccount(passphrase);
        Address to = manager.newAccount("topassphrase".getBytes());
        BigInteger value = new BigInteger("0");
        long nonce = 1; // nonce = from.nonce + 1
        Transaction.PayloadType payloadType = Transaction.PayloadType.BINARY;
        byte[] payload = new TransactionBinaryPayload(null).toBytes();
        BigInteger gasPrice = new BigInteger("1000000"); // 0 < gasPrice < 10^12
        BigInteger gasLimit = new BigInteger("20000"); // 20000 < gasPrice < 50*10^9
        Transaction tx = new Transaction(chainID, from, to, value, nonce, payloadType, payload, gasPrice, gasLimit);
        manager.signTransaction(tx,passphrase);
        byte[] rawData = tx.toProto();
        // senrawTransaction with @rawData
        // https://github.com/nebulasio/wiki/blob/master/rpc.md#sendrawtransaction
        SendRawTransaction(rawData);

        // deploy tx
        payloadType = Transaction.PayloadType.DEPLOY;
        payload = new TransactionDeployPayload("js", "var demo = 1;", "").toBytes();
        // deploy from == to
        tx = new Transaction(chainID, from, from, value, nonce, payloadType, payload, gasPrice, gasLimit);
        manager.signTransaction(tx,passphrase);
        rawData = tx.toProto();
        // senrawTransaction with @rawData
        // https://github.com/nebulasio/wiki/blob/master/rpc.md#sendrawtransaction
        SendRawTransaction(rawData);

        // call tx
        payloadType = Transaction.PayloadType.CALL;
        payload = new TransactionCallPayload("function", "").toBytes();
        // call to = contract address
        to = Address.ParseFromString("n1g6JZsQS1uRUySdwvuFJ7FYT4dFoyoSN5q");
        tx = new Transaction(chainID, from, from, value, nonce, payloadType, payload, gasPrice, gasLimit);
        manager.signTransaction(tx,passphrase);
        rawData = tx.toProto();
        // senrawTransaction with @rawData
        // https://github.com/nebulasio/wiki/blob/master/rpc.md#sendrawtransaction
        SendRawTransaction(rawData);
    }

    private static void SendRawTransaction(byte[] data) throws Exception {
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
//        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 8684).usePlaintext().build();
//        Rpc.SendRawTransactionRequest request = Rpc.SendRawTransactionRequest
//                .newBuilder()
//                .setData(ByteString.copyFrom(data))
//                .build();

//        ApiServiceGrpc.ApiServiceBlockingStub apiServiceStub = ApiServiceGrpc.newBlockingStub(channel);
//        Rpc.SendTransactionResponse response = apiServiceStub.sendRawTransaction(request);
//        System.out.println(response);

//        ApiServiceGrpc.ApiServiceStub serviceStub = ApiServiceGrpc.newStub(channel);
//        final CountDownLatch finishLatch = new CountDownLatch(1);
//        StreamObserver<Rpc.SendTransactionResponse> observer = new StreamObserver<Rpc.SendTransactionResponse>() {
//            @Override
//            public void onNext(Rpc.SendTransactionResponse value) {
//                System.out.println(value);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                finishLatch.countDown();
//            }
//
//            @Override
//            public void onCompleted() {
//                finishLatch.countDown();
//            }
//        };
//
//        serviceStub.sendRawTransaction(request,observer);
    }

//    获取网络信息，主要是为了拿到chain id，这个请求在app启动是访问一次就够了
    private void getNebState() {
        String url = "http://47.97.220.86:18685/v1/user/nebstate";
        // 使用get访问url，得到的结果参见https://github.com/nebulasio/wiki/blob/master/rpc.md#getnebstate
        // 返回的结果中的chain_id保存起来，后面的请求有用
    }

//    查询账户状态，参数是地址，返回账户的余额和nonce
    private void getAccountState(String address) {
        String url = "http://47.97.220.86:18685/v1/user/nebstate";
        // 使用post访问url， 参数是address，返回的结果参见https://github.com/nebulasio/wiki/blob/master/rpc.md#getaccountstate
        // 保存好余额和nonce，比如保存到User对象，User.nonce = http_result.nonce
    }

//    进入主界面后，获取账户所有的密码信息，定义"密码信息"为一个array，比如
    // ["www.baidu.com","lihao","123456"]，其中第一个元素表示网站或者app的名称，第二个元素表示用户名，第三个元素表示密码
    private void getAllPasswords(String address, PrivateKey privateKey) {
//        // 第一步先访问http://47.97.220.86:18685/v1/user/getGasPrice拿到最新的getGasPrice
//        // 第二步访问http://47.97.220.86:18685/v1/user/call，调用合约的get方法
//        String url = "http://47.97.220.86:18685/v1/user/call";
//        // 使用post访问url，post的参数为
//        String from = address; // from就是用户的地址
//        String to = "n1zxWwXaukexTsGBkA1kkSruY185C6CiDLG"; // 这是我们测试网络的合约地址
//        BigInteger value = new BigInteger("0"); // 交易的value为0，表示不会向to转入nas币
//        BigInteger nonce = User.nonce + 1; // 无需再访问v1/user/nebstate拿到最新的nonce，每次构造交易之后只需将User.nonce+1即可
//        BigInteger gasPrice = 第一步获取的gasPrice;
//        BigInteger gasLimit = new BigInteger("2000000"); // 固定使用2000000就行
//        Map<String, String> contract = new HashMap<>();
//        contract.put("function", "get"); // 要调用的方法名叫get
//        contract.put("args", ""); // get方法无需参数
//        // 得到的response为
//        // {
//        //   "result": "{\"datahash1\":\"data1\"}",
//        //   "execute_err": "insufficient balance",
//        //   estimate_gas: "22208"
//        //}
//        // 其中result是一个json字符串，这个json字符串中的每个元素的value都是密文，key都是这段密文的hash
//        List<String> passwordInfoList = new ArrayList<>();
//        // 将result进行json decode得到一个json对象
//        for(String dataHash, data : resultJson) {
//            String decryptedData = Decrypt(data, privateKey);// 使用私钥对公钥加密的data进行解密
//            passwordInfoList.add(decryptedData);
//        }
//        // 如果请求成功User.nonce加1
//        User.nonce += 1;
//        // 返回passwordInfoList
    }

//    存储新的密码信息
    private void saveNewPassword(String address, String passwordInfo, PrivateKey privateKey, PublicKey pubkey) {
//        // 第一步先访问http://47.97.220.86:18685/v1/user/getGasPrice拿到最新的getGasPrice
//        // 第二步构造一个transaction
//        String from = address; // from就是用户的地址
//        String to = "n1zxWwXaukexTsGBkA1kkSruY185C6CiDLG"; // 这是我们测试网络的合约地址
//        BigInteger value = new BigInteger("0"); // 交易的value为0，表示不会向to转入nas币
//        BigInteger nonce = User.nonce + 1; // 无需再访问v1/user/nebstate拿到最新的nonce，每次构造交易之后只需将User.nonce+1即可
//        BigInteger gasPrice = 第一步获取的gasPrice;
//        BigInteger gasLimit = new BigInteger("2000000"); // 固定使用2000000就行
//        // 使用pubkey对passwordInfo进行加密得到encryptedData
//        String encryptedData = Encryt(passwordInfo, pubkey);
//        String hash = Sha256(encryptedData); // 使用sha256计算hash
//        Map<String, String> contract = new HashMap<>();
//        contract.put("function", "save"); // 要调用的方法名叫save
//        contract.put("args", "[hash, encryptedData]"); // save方法有两个参数，第一个是data的hash，第二个是data
//        // 第三步使用私钥对这个transaction进行签名，得到签名后的data
//        String rawData = Transaction.sign(privateKey);
//        // 第四步将签名后的data发送出去，访问http://47.97.220.86:18685/v1/user/rawtransaction
//        // 其中post的data就是rawData，如果请求成功会得到txhash，参考https://github.com/nebulasio/wiki/blob/master/rpc.md#sendrawtransaction
//        User.nonce += 1;
//        // 将这个txhash添加到内存中一个观察队列中，这个观察队列存放的都是未上链的交易，我们需要定期比如3秒1次访问
//        // https://github.com/nebulasio/wiki/blob/master/rpc.md#gettransactionreceipt来获取观察队列中未确认的交易的状态变化
    }
}
