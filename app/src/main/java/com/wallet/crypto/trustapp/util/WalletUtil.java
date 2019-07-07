package com.wallet.crypto.trustapp.util;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class WalletUtil {
    public static String addr  = "0x61A4660b9A48337b9e245a0Ec11E618fB44eB0Ff";

    public static BigDecimal weiToEth(BigInteger wei) {
        return Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER);
    }


}
