package com.qkl.wallet.contract;

import io.reactivex.Flowable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.5.
 */
@SuppressWarnings("rawtypes")
public class MyToken extends Contract {
    private static final String BINARY = "60606040526040516107d13803806107d183398101604052805160805160a05160c051929391820192909101600160a060020a0333166000908152600360209081526040822086905581548551838052601f6002600019610100600186161502019093169290920482018390047f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e56390810193919290918801908390106100c857805160ff19168380011785555b506100f89291505b8082111561015157600081556001016100b4565b828001600101855582156100ac579182015b828111156100ac5782518260005055916020019190600101906100da565b50508060016000509080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061015557805160ff19168380011785555b506101859291506100b4565b5090565b82800160010185558215610145579182015b82811115610145578251826000505591602001919060010190610167565b5050506002555061063590508061019c6000396000f36060604052361561008d5760e060020a600035046306fdde03811461009257806323b872dd146100f5578063313ce5671461012c5780634f694bf71461013a57806370a082311461016b57806395d89b4114610188578063a9059cbb146101ea578063cae9ca511461021e578063dc3080f214610375578063dd62ed3e1461039f578063f8b2cb4f146103c8575b610002565b34610002576040805160008054602060026001831615610100026000190190921691909104601f81018290048202840182019094528383526103ed939083018282801561049c5780601f106104715761010080835404028352916020019161049c565b346100025761045b600435602435604435600160a060020a038316600090815260036020526040812054829010156104a457610002565b346100025761015960025481565b346100025733600160a060020a03166000908152600360205260409020545b60408051918252519081900360200190f35b346100025761015960043560036020526000908152604090205481565b34610002576103ed60018054604080516020600284861615610100026000190190941693909304601f8101849004840282018401909252818152929183018282801561049c5780601f106104715761010080835404028352916020019161049c565b346100025761046f60043560243533600160a060020a0316600090815260036020526040902054819010156105a257610002565b3461000257604080516020604435600481810135601f810184900484028501840190955284845261045b9481359460248035959394606494929391019181908401838280828437509496505050505050506000826004600050600033600160a060020a03168152602001908152602001600020600050600086600160a060020a03168152602001908152602001600020600050819055507fc580975836a6474723779cc3df05b753a9d649f7125258b85e3a94ad063a98e7338430856040518085600160a060020a0316815260200184815260200183600160a060020a03168152602001806020018281038252838181518152602001915080519060200190808383829060006004602084601f0104600302600f01f150905090810190601f16801561035e5780820380516001836020036101000a031916815260200191505b509550505050505060405180910390a19392505050565b34610002576005602090815260043560009081526040808220909252602435815220546101599081565b346100025760046020818152903560009081526040808220909252602435815220546101599081565b3461000257600160a060020a0360043516600090815260036020526040902054610159565b60405180806020018281038252838181518152602001915080519060200190808383829060006004602084601f0104600302600f01f150905090810190601f16801561044d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b604080519115158252519081900360200190f35b005b820191906000526020600020905b81548152906001019060200180831161047f57829003601f168201915b505050505081565b600160a060020a03831660009081526003602052604090205480830110156104cb57610002565b600160a060020a03848116600081815260046020908152604080832033909516808452948252808320549383526005825280832094835293905291909120548301111561051757610002565b600160a060020a03848116600081815260036020908152604080832080548890039055878516808452818420805489019055938352600582528083203390951680845294825291829020805487019055815186815291519293927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a39392505050565b600160a060020a03821660009081526003602052604090205480820110156105c957610002565b33600160a060020a03908116600081815260036020908152604080832080548790039055938616808352918490208054860190558351858152935191937fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929081900390910190a3505056";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_GETMASTERBALANCE = "getMasterBalance";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_APPROVEANDCALL = "approveAndCall";

    public static final String FUNC_SPENTALLOWANCE = "spentAllowance";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_GETBALANCE = "getBalance";

    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event RECEIVEAPPROVAL_EVENT = new Event("ReceiveApproval",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<DynamicBytes>() {}));
    ;

    @Deprecated
    protected MyToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MyToken(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected MyToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected MyToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> name() {
        final Function function = new Function(FUNC_NAME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(FUNC_TRANSFERFROM,
                Arrays.<Type>asList(new Address(160, _from),
                        new Address(160, _to),
                        new Uint256(_value)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getMasterBalance() {
        final Function function = new Function(FUNC_GETMASTERBALANCE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String param0) {
        final Function function = new Function(FUNC_BALANCEOF,
                Arrays.<Type>asList(new Address(160, param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER,
                Arrays.<Type>asList(new Address(160, _to),
                        new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> approveAndCall(String _spender, BigInteger _value, byte[] _extraData) {
        final Function function = new Function(FUNC_APPROVEANDCALL,
                Arrays.<Type>asList(new Address(160, _spender),
                        new Uint256(_value),
                        new DynamicBytes(_extraData)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> spentAllowance(String param0, String param1) {
        final Function function = new Function(FUNC_SPENTALLOWANCE,
                Arrays.<Type>asList(new Address(160, param0),
                        new Address(160, param1)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> allowance(String param0, String param1) {
        final Function function = new Function(FUNC_ALLOWANCE,
                Arrays.<Type>asList(new Address(160, param0),
                        new Address(160, param1)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getBalance(String account) {
        final Function function = new Function(FUNC_GETBALANCE,
                Arrays.<Type>asList(new Address(160, account)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public List<ReceiveApprovalEventResponse> getReceiveApprovalEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(RECEIVEAPPROVAL_EVENT, transactionReceipt);
        ArrayList<ReceiveApprovalEventResponse> responses = new ArrayList<ReceiveApprovalEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ReceiveApprovalEventResponse typedResponse = new ReceiveApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse._token = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse._extraData = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ReceiveApprovalEventResponse> receiveApprovalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, ReceiveApprovalEventResponse>() {
            @Override
            public ReceiveApprovalEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(RECEIVEAPPROVAL_EVENT, log);
                ReceiveApprovalEventResponse typedResponse = new ReceiveApprovalEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse._token = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse._extraData = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ReceiveApprovalEventResponse> receiveApprovalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RECEIVEAPPROVAL_EVENT));
        return receiveApprovalEventFlowable(filter);
    }

    @Deprecated
    public static MyToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MyToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static MyToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MyToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static MyToken load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new MyToken(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static MyToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new MyToken(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<MyToken> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, BigInteger initialWeiValue, BigInteger initialSupply, String tokenName, BigInteger decimalUnits, String tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(initialSupply),
                new Utf8String(tokenName),
                new Uint256(decimalUnits),
                new Utf8String(tokenSymbol)));
        return deployRemoteCall(MyToken.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor, initialWeiValue);
    }

    public static RemoteCall<MyToken> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, BigInteger initialWeiValue, BigInteger initialSupply, String tokenName, BigInteger decimalUnits, String tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(initialSupply),
                new Utf8String(tokenName),
                new Uint256(decimalUnits),
                new Utf8String(tokenSymbol)));
        return deployRemoteCall(MyToken.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor, initialWeiValue);
    }

    @Deprecated
    public static RemoteCall<MyToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, BigInteger initialSupply, String tokenName, BigInteger decimalUnits, String tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(initialSupply),
                new Utf8String(tokenName),
                new Uint256(decimalUnits),
                new Utf8String(tokenSymbol)));
        return deployRemoteCall(MyToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    @Deprecated
    public static RemoteCall<MyToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, BigInteger initialSupply, String tokenName, BigInteger decimalUnits, String tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(initialSupply),
                new Utf8String(tokenName),
                new Uint256(decimalUnits),
                new Utf8String(tokenSymbol)));
        return deployRemoteCall(MyToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }

    public static class ReceiveApprovalEventResponse extends BaseEventResponse {
        public String _from;

        public BigInteger _value;

        public String _token;

        public byte[] _extraData;
    }
}
