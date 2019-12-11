package com.qkl.wallet.contract;

import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

public interface IToken {

    RemoteFunctionCall<BigInteger> balanceOf(String arg);

    RemoteFunctionCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value);

}
