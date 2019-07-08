package com.wallet.crypto.trustapp.util;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class WalletUtil {
    public static String addr  = "0x61A4660b9A48337b9e245a0Ec11E618fB44eB0Ff";
    public static byte[] signedEthTransaction=null;
    public static String rpcServerUrl="https://mainnet.infura.io/v3/f165b595cd184b2a848716830f9804b0";
    public static String hdPath="m/44'/60'";

    public static BigDecimal weiToEth(BigInteger wei) {
        return Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER);
    }


}
