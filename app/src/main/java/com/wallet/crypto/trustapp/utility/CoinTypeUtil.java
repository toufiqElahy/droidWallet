package com.wallet.crypto.trustapp.utility;

import com.samsung.android.sdk.coldwallet.CWCoinType;

public class CoinTypeUtil {
    public static String coinType2String(int coinType) {
        String ret;
        switch (coinType) {
            case CWCoinType.BTC:
                ret = "Bitcoin";
                break;
            case CWCoinType.ETH:
                ret = "Ether";
                break;
            default:
                ret = String.format("Unknown coin type, type=%x", coinType);
                break;
        }
        return ret;
    }
}
