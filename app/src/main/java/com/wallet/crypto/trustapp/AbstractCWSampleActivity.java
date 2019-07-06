package com.wallet.crypto.trustapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wallet.crypto.trustapp.utility.AppExecutors;
import com.wallet.crypto.trustapp.utility.CWLog;
import com.wallet.crypto.trustapp.utility.CoinTypeUtil;
import com.wallet.crypto.trustapp.utility.ETHUtil;
import com.wallet.crypto.trustapp.utility.HexUtil;
import com.samsung.android.sdk.coldwallet.CWBuildType;
import com.samsung.android.sdk.coldwallet.CWErrorCode;
import com.samsung.android.sdk.coldwallet.CWResult;
import com.samsung.android.sdk.coldwallet.CWServiceAdapter;
import com.samsung.android.sdk.coldwallet.CWServiceCallback;
import com.samsung.android.sdk.coldwallet.ICWWallet;

import org.bitcoinj.core.ECKey;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public abstract class AbstractCWSampleActivity extends AppCompatActivity implements TextWatcher {

    private static final String TAG = AbstractCWSampleActivity.class.getSimpleName();
    public static final String CHANNEL_ID = "deeplink";
    private final String mApplicationId = "APPID0004";

    public static final String HD_PATH_ETH = "m/44'/60'/0'/0/";
    public static final String HD_PATH_BTC = "m/44'/0'/0'/0/";
    private static final int TOKEN_GET_XPUB_KEY_FOR_ETH = 10001;
    private static final int TOKEN_GET_XPUB_KEY_LIST = 10003;
    private static final int TOKEN_SIGN_ETH = 10004;
    private static final int TOKEN_CHECK_APP_VERSION = 10005;
    private static final int TOKEN_SIGN_MESSAGE = 10006;

    private static final String KEY_USER_DATA_SAMPLE = "user_data_sample";

    protected int mCurrentCurrencyType = 60;

    private TextView mCallbackText;
    private TextView mIndexTextView;
    protected TextView mExtPubKeyTextView;
    protected TextView mPubKeyTextView;
    private TextView mUnsignedTxTextView;
    protected TextView mTxTextView;
    protected TextView mChainCodeTextView;
    private TextView mMaximumFeeTextView;
    protected TextView mSignedMsgTextView;

    protected EditText mValueEditText;
    protected EditText mGasPriceEditText;
    protected EditText mGasLimitEditText;
    protected EditText mUnSignedMsgEditText;
    protected EditText mTokenAmountEditText;
    protected EditText mContractAddressEditText;

    protected byte[] mCurrentPubX;
    protected byte[] mCurrentPub;
    protected String mEthAddress;
    protected byte[] mCurrentUnsignedTx;
    protected byte[] signedEthTx;
    protected byte[] signedMessage;

    protected AppExecutors mExecutors;

    protected LinearLayout mLayoutETHTest;

    protected LinearLayout mLayoutETHAddress;

    protected int mIndex = 0;


    private static final String CWS_PKG_NAME = "com.samsung.android.coldwalletservice";
    public static final String CWS_CLASS_NAME = CWS_PKG_NAME + ".core.CWWalletService";
    public static final String CWHOME_CLASS_NAME = CWS_PKG_NAME + ".ui.CWHomeActivity";

    private View.OnClickListener mCheckInitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle userData = new Bundle();
            userData.putString(KEY_USER_DATA_SAMPLE, "restore wallet from home");
            try {
                boolean initialized = false;
                String walletKey = mServiceAdapter.getWalletKey();
                if (!TextUtils.isEmpty(walletKey)) {
                    initialized = true;
                } else {
                    /* checkWallet is deprecated */
                    if (mServiceAdapter.checkWallet()) {
                        initialized = true;
                    }
                }
                if (initialized)
                    mCallbackText.setText("Wallet is created, walletKey=" + walletKey);
                else
                    mCallbackText.setText("Wallet is not created");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener mCheckAppVersionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle userData = new Bundle();
            userData.putString(KEY_USER_DATA_SAMPLE, "check app version from home");
            try {
                mServiceAdapter.checkAppVersion(mCallback, TOKEN_CHECK_APP_VERSION, userData);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


    private View.OnClickListener mSetOmisegoAddress = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mContractAddressEditText.setText("0xd26114cd6EE289AccF82350c8d8487fedB8A0C07");
        }
    };

    private View.OnClickListener mSetUnknown20TokenAddress = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Token Nexo (ERC20) Unknown
            mContractAddressEditText.setText("0xb62132e35a6c13ee1ee0f84dc5d40bad8d815206");
        }
    };

    private View.OnClickListener mSet721TokenAddress = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //ERC721 cryptokitty
            mContractAddressEditText.setText("0x06012c8cf97bead5deae237070f9587f8e7a266d");
        }
    };

    private View.OnClickListener mGetXPubKeyWalletListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle userData = new Bundle();
            userData.putString(KEY_USER_DATA_SAMPLE, "get public key from home");
            try {
                String hdpath;
                if (mCurrentCurrencyType == 0) { // ETH default
                    hdpath = HD_PATH_BTC + mIndex;
                } else {
                    hdpath = HD_PATH_ETH + mIndex;
                }
                mServiceAdapter.getXPublicKey(mCallback, TOKEN_GET_XPUB_KEY_FOR_ETH, hdpath, userData);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener mGetXPubKeyListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle userData = new Bundle();
            userData.putString(KEY_USER_DATA_SAMPLE, "get public key from home");
            try {
                String hdpath;
                if (mCurrentCurrencyType == 0) { // ETH default
                    hdpath = HD_PATH_BTC + mIndex;
                } else {
                    hdpath = HD_PATH_ETH + mIndex;
                }
                ArrayList<String> hdPathList = new ArrayList<>();
                hdPathList.add(hdpath);
                mServiceAdapter.getXPublicKeyList(mCallback, TOKEN_GET_XPUB_KEY_LIST, hdPathList, userData);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * This implementation is used to receive callbacks from the remote
     * service.
     */
    protected CWServiceCallback mCallback = new CWServiceCallback() {
        /**
         * This is called by the remote service regularly to tell us about
         * new values.  Note that IPC calls are dispatched through a thread
         * pool running in each process, so the code executing here will
         * NOT be running in our main thread like most other things -- so,
         * to update the UI, we need to use a Handler to hop over there.
         */

        @Override
        public void onResponse(int reqeustId, int token, int errorCode, Bundle userData, Bundle result) throws RemoteException {
            CWLog.d(TAG, String.format(Locale.ENGLISH, "onResponse token=%d error=%d", token, errorCode));

            if (token == TOKEN_CHECK_APP_VERSION) {
                if (errorCode == CWErrorCode.OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(AbstractCWSampleActivity.this, String.format(Locale.ENGLISH, "onResponse token=%d latest version", token, errorCode), Toast.LENGTH_LONG).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(AbstractCWSampleActivity.this, String.format(Locale.ENGLISH, "onResponse token=%d error=%d", token, errorCode), Toast.LENGTH_LONG).show();
                    });
                }
                return;
            }

            if (errorCode < 0) {
                runOnUiThread(() -> {
                    Toast.makeText(AbstractCWSampleActivity.this, String.format(Locale.ENGLISH, "onResponse token=%d error=%d", token, errorCode), Toast.LENGTH_LONG).show();
                });
                return;
            }

            switch (token) {
                case TOKEN_GET_XPUB_KEY_FOR_ETH:
                    byte[] ethXPubKey = result.getByteArray(CWResult.EXTRAS_KEY_RESULT_PUBKEY);
                    if (ethXPubKey != null) {
                        updatePubKey(ethXPubKey);
                        CWLog.d(TAG, String.format(Locale.ENGLISH, "onResponse xPubKey=%s len=%d ethAddr=%s",
                                HexUtil.toHexString(ethXPubKey), ethXPubKey.length, mEthAddress));
                    }
                    break;
                case TOKEN_GET_XPUB_KEY_LIST:
                    ArrayList<String> resultStringArrayList = result.getStringArrayList(CWResult.EXTRAS_KEY_RESULT_PUBKEY_LIST);
                    if (resultStringArrayList != null && !resultStringArrayList.isEmpty()) {
                        byte[] sample = HexUtil.toBytes(resultStringArrayList.get(0));
                        updatePubKey(sample);
                        CWLog.d(TAG, String.format(Locale.ENGLISH, "onResponse xPubKey=%s len=%d ethAddr=%s",
                                HexUtil.toHexString(sample), sample.length, mEthAddress));
                    }
                    break;
                case TOKEN_SIGN_ETH:
                    signedEthTx = result.getByteArray(CWResult.EXTRAS_KEY_RESULT_SIGNED_TX);
                    if (signedEthTx != null) {
                        handlingSignedETHTx(signedEthTx);
                        CWLog.d(TAG, "onResponse, signedTx=" + HexUtil.toHexString(signedEthTx));
                    }
                    break;
                case TOKEN_SIGN_MESSAGE:
                    signedMessage = result.getByteArray(CWResult.EXTRAS_KEY_RESULT_SIGNED_TX);
                    if (signedMessage != null) {
                        handlingSignedMessage(signedMessage);
                        CWLog.d(TAG, "onResponse, signedTx=" + HexUtil.toHexString(signedEthTx));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    protected abstract void handlingSignedETHTx(byte[] signedEthTx);

    protected abstract void handlingSignedMessage(byte[] signedMessage);

    private View.OnClickListener mGetSupportedCoinsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                int[] supportedCoins = mServiceAdapter.getSupportedCoins();

                StringBuilder sb = new StringBuilder();
                sb.append("Supported coins").append('\n');
                for (int i = 0; i < supportedCoins.length; i++) {
                    sb.append('[').append(i).append("] ").append(CoinTypeUtil.coinType2String(supportedCoins[i])).append('\n');
                }

                String s = sb.toString();

                mCallbackText.setText(s);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceAdapter = new CWServiceAdapter(service, mApplicationId, CWBuildType.RELEASE);
            mCallbackText.setText("Connected");
            mIsBound = true;

            Toast.makeText(AbstractCWSampleActivity.this,
                    "Remote service connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceAdapter = null;
            mCallbackText.setText("Disconnected");

            Toast.makeText(AbstractCWSampleActivity.this,
                    "Remote service disconnected", Toast.LENGTH_SHORT).show();
        }
    };
    private boolean mIsBound = false;
    protected CWServiceAdapter mServiceAdapter;

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent();
        intent.setAction(ICWWallet.class.getName());
        intent.setClassName(CWS_PKG_NAME, CWS_CLASS_NAME);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIsBound) {
            mIsBound = false;
            unbindService(mServiceConnection);
        }
    }

    // ----------------------------------------------------------------------
    // Code showing how to deal with callbacks.
    // ----------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        mExecutors = AppExecutors.getInstance();

        Button goWallet = findViewById(R.id.goWallet);
        goWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(CWS_PKG_NAME, CWHOME_CLASS_NAME);
                startActivity(intent);
            }
        });

        Button goReset = findViewById(R.id.goDeeplink);
        goReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AbstractCWSampleActivity.this, DeepLinkActivity.class);
                startActivity(intent);
            }
        });


        Button checkWalletButton = findViewById(R.id.checkInitWallet);
        checkWalletButton.setOnClickListener(mCheckInitListener);

        Button checkAppVersionButton = findViewById(R.id.check_app_version);
        checkAppVersionButton.setOnClickListener(mCheckAppVersionListener);

        Button getXPubKeyButton = findViewById(R.id.getXPubKey);
        getXPubKeyButton.setOnClickListener(mGetXPubKeyWalletListener);

        Button getXPubKeyListButton = findViewById(R.id.getXPubKeyList);
        getXPubKeyListButton.setOnClickListener(mGetXPubKeyListListener);

        mCallbackText = findViewById(R.id.callback);
        mCallbackText.setText("Not attached.");

        mIndexTextView = findViewById(R.id.tv_index);
        mPubKeyTextView = findViewById(R.id.tv_public_key);
        mExtPubKeyTextView = findViewById(R.id.tv_ext_public_key);

        mValueEditText = findViewById(R.id.et_eth_value);
        mGasPriceEditText = findViewById(R.id.et_eth_gas_price);
        mGasLimitEditText = findViewById(R.id.et_eth_gas_limit);
        mUnSignedMsgEditText = findViewById(R.id.et_unsigned_msg);

        mUnsignedTxTextView = findViewById(R.id.tv_unsigned_raw_transaction);
        mTxTextView = findViewById(R.id.tv_raw_transaction);

        mChainCodeTextView = findViewById(R.id.tv_chain_code);
        mMaximumFeeTextView = findViewById(R.id.tv_maximum_fee);

        Button getSupportedCoinButton = findViewById(R.id.get_supported_coin);
        getSupportedCoinButton.setOnClickListener(mGetSupportedCoinsListener);

        mGasPriceEditText.addTextChangedListener(this);
        mGasLimitEditText.addTextChangedListener(this);

        mTokenAmountEditText = findViewById(R.id.tokenAmount);
        mContractAddressEditText = findViewById(R.id.contractAddress);
        Button getOmisegoContract = findViewById(R.id.getOmisego);
        getOmisegoContract.setOnClickListener(mSetOmisegoAddress);

        Button getUnknownTokenContract = findViewById(R.id.getUnknownToken);
        getUnknownTokenContract.setOnClickListener(mSetUnknown20TokenAddress);

        Button getERC721TokenContract = findViewById(R.id.get721token);
        getERC721TokenContract.setOnClickListener(mSet721TokenAddress);

        mLayoutETHTest = findViewById(R.id.ll_eth_test);

        mLayoutETHAddress = findViewById(R.id.ll_eth_address);

        mSignedMsgTextView = findViewById(R.id.tv_signed_message);

        mIndex = 0;

        mLayoutETHAddress.setVisibility((mCurrentCurrencyType == 60) ? View.VISIBLE : View.GONE);

        updateMaximumFee();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DeepLink Test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager == null)
                return;
            notificationManager.createNotificationChannel(channel);
        }
    }

    public byte[] getUnsignedEthTx(String address) {
        try {
            BigInteger nonce = BigInteger.ZERO;
            double ethValue = Double.valueOf(mValueEditText.getText().toString());
            BigDecimal weiValue = BigDecimal.valueOf(ethValue).multiply(BigDecimal.valueOf(1, -18));
            BigInteger gasPrice = new BigInteger(mGasPriceEditText.getText().toString());
            BigInteger gasLimit = new BigInteger(mGasLimitEditText.getText().toString());
            Log.d(TAG, "GP : " + gasPrice.toString() + " GL : " + gasLimit.toString());
            RawTransaction rawTrx = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, "0x683ad1627c6c9acf851ae882258d1c507c2c48c4", weiValue.toBigInteger());

            return TransactionEncoder.encode(rawTrx);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public byte[] getUnsignedEthTxWithData(String address){
        try {
            BigInteger nonce = BigInteger.ZERO;
            double ethValue = Double.valueOf(mValueEditText.getText().toString());
            BigDecimal weiValue = BigDecimal.valueOf(ethValue).multiply(BigDecimal.valueOf(1, -18));
            BigInteger gasPrice = new BigInteger(mGasPriceEditText.getText().toString());
            BigInteger gasLimit = new BigInteger(mGasLimitEditText.getText().toString());
            Log.d(TAG, "GP : " + gasPrice.toString() + " GL : " + gasLimit.toString());
            String data = "0xb69ef8a8";
            String to = "0x071ff4399fa3eaab2cc879c7640a5bd2158f79a1";
            RawTransaction rawTrx = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, weiValue.toBigInteger(), data);

            return TransactionEncoder.encode(rawTrx);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public byte[] getUnsignedEthTxWithGeneratedData(String address){
        try {
            BigInteger nonce = BigInteger.ZERO;
            double ethValue = Double.valueOf(mValueEditText.getText().toString());
            BigDecimal weiValue = BigDecimal.valueOf(ethValue).multiply(BigDecimal.valueOf(1, -18));
            BigInteger gasPrice = new BigInteger(mGasPriceEditText.getText().toString());
            BigInteger gasLimit = new BigInteger(mGasLimitEditText.getText().toString());
            Log.d(TAG, "GP : " + gasPrice.toString() + " GL : " + gasLimit.toString());

            String contractAddress = mContractAddressEditText.getText().toString();
            BigInteger tokenAmount = BigDecimal.valueOf(Double.valueOf(mTokenAmountEditText.getText().toString())).multiply(BigDecimal.valueOf(10).pow(18)).toBigInteger();
            String transferToAddress = "0x071ff4399fa3eaab2cc879c7640a5bd2158f79a1";

            Log.d(TAG, "getUnsignedEthTxWithGeneratedData() contractAddress = " + contractAddress
                    + " tokenAmount = " + tokenAmount);

            Function function = ETHUtil.createEthTransferData(transferToAddress, tokenAmount);
            String encodedFunction = FunctionEncoder.encode(function);
            Log.d(TAG, "getUnsignedEthTxWithGeneratedData encodedFunction = " + encodedFunction);

            RawTransaction rawTrx = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, weiValue.toBigInteger(), encodedFunction);

            return TransactionEncoder.encode(rawTrx);

        } catch (Exception e) {
            return new byte[0];
        }
    }

    public void actionUpIndex(View view) {
        mIndex++;
        if (mIndex >= 100) {
            mIndex = 100;
        }
        updateIndexTextView();
        clearPubkeyTextView();
    }

    public void actionDownIndex(View view) {
        mIndex--;
        if (mIndex <= 0) {
            mIndex = 0;
        }


        updateIndexTextView();
        clearPubkeyTextView();
    }

    public void actionShowBTCTest(View view) {
        // do nothing
    }


    public void actionSignETH(View view){
        Bundle userData = new Bundle();
        userData.putString(KEY_USER_DATA_SAMPLE, "sign ethereum from home");

        if (mEthAddress == null || mEthAddress.isEmpty()) {
            Toast.makeText(getApplication(), "Need to get Public key first", Toast.LENGTH_SHORT).show();
            return;
        }



        CWLog.d(TAG, "Try sign Eth");
        byte[] unsignedEthTx = getUnsignedEthTx(mEthAddress);
        String tag = (String)view.getTag();
        if(tag != null && tag.equals("SIGN_DATA")){
            unsignedEthTx = getUnsignedEthTxWithData(mEthAddress);
        }

        if(tag != null && tag.equals("GENERATE_AND_SIGN_DATA")){
            CWLog.d(TAG, "GENERATE_AND_SIGN_DATA");
            unsignedEthTx = getUnsignedEthTxWithGeneratedData(mEthAddress);
        }

        mCurrentUnsignedTx = unsignedEthTx;
        mUnsignedTxTextView.setText(HexUtil.toHexString(unsignedEthTx));

        try {
            String hdpath = HD_PATH_ETH + mIndex;

            mServiceAdapter.requestSignEthTransaction(mCallback, TOKEN_SIGN_ETH, unsignedEthTx, hdpath, userData);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void actionSignPersonalMsg(View view){
        Bundle userData = new Bundle();
        userData.putString(KEY_USER_DATA_SAMPLE, "sign ethereum from home");

        if (mEthAddress == null || mEthAddress.isEmpty()) {
            Toast.makeText(getApplication(), "Need to get Public key first", Toast.LENGTH_SHORT).show();
            return;
        }


        CWLog.d(TAG, "Try personal sign message");
        byte[] unSignedMsg = mUnSignedMsgEditText.getText().toString().getBytes();

        try {
            String hdpath = HD_PATH_ETH + mIndex;

            mServiceAdapter.requestSignPersonalMessage(mCallback, TOKEN_SIGN_MESSAGE, unSignedMsg, hdpath, userData);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void updateIndexTextView() {
        mIndexTextView.setText(String.valueOf(mIndex));
    }

    protected void clearPubkeyTextView() {
        mCurrentPubX = null;
        mCurrentPub = null;
        mEthAddress = null;

        runOnUiThread(() -> {
            mPubKeyTextView.setText("");
            mExtPubKeyTextView.setText("");
            mChainCodeTextView.setText("");
            mLayoutETHAddress.setVisibility((mCurrentCurrencyType == 60) ? View.VISIBLE : View.GONE);

        });
    }

    private void updatePubKey(byte[] pubx) {
        CWLog.d(TAG, HexUtil.toHexString(pubx));
        CWLog.d(TAG, "pubkey Length :" + pubx.length);

        mCurrentPubX = pubx;

        byte[] pub = Arrays.copyOfRange(pubx, 0, 33);
        byte[] chainCode = Arrays.copyOfRange(pubx, 33, 65);
        mCurrentPub = pub;

        byte[] decompKey = ETHUtil.decompressCompressedKey(pub);
        if (decompKey != null) {
            byte[] ethAddress = Keys.getAddress(decompKey);
            final String ethAddrStr = HexUtil.toHexString(ethAddress);
            mEthAddress = ethAddrStr;
            CWLog.d(TAG, ethAddrStr);

            ECKey pubkey = ECKey.fromPublicOnly(pub);

            runOnUiThread(() -> {
                mPubKeyTextView.setText(ethAddrStr);
                mExtPubKeyTextView.setText(HexUtil.toHexString(pub));
                mChainCodeTextView.setText(HexUtil.toHexString(chainCode));
                mLayoutETHAddress.setVisibility((mCurrentCurrencyType == 60) ? View.VISIBLE : View.GONE);
            });
        }


    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        // update Max Fee
        checkMaximumValue();
        updateMaximumFee();
    }

    private void checkMaximumValue() {
        final BigInteger MAX_GAS_PRICE = new BigInteger("1099511627775");
        final BigInteger MAX_GAS_LIMIT = new BigInteger("4294967295");

        String gasPriceStr = mGasPriceEditText.getText().toString();
        String gasLimitStr = mGasLimitEditText.getText().toString();

        if (gasPriceStr.isEmpty() || gasPriceStr.length() == 0) {
            gasPriceStr = "0";
        }

        if (gasLimitStr.isEmpty() || gasLimitStr.length() == 0) {
            gasLimitStr = "0";
        }

        BigInteger gasPrice = new BigInteger(gasPriceStr);
        BigInteger gasLimit = new BigInteger(gasLimitStr);

        if (gasPrice.compareTo(MAX_GAS_PRICE) > 0) {
            //  0xFFFFFFFFFF
            gasPrice = MAX_GAS_PRICE;
            gasPriceStr = gasPrice.toString();
            mGasPriceEditText.setText(gasPriceStr);

        }
        if (gasLimit.compareTo(MAX_GAS_LIMIT) > 0) {
            gasLimit = MAX_GAS_LIMIT;
            gasLimitStr = gasLimit.toString();
            mGasLimitEditText.setText(gasLimitStr);
        }
    }

    private void updateMaximumFee() {
        String gasPriceStr = mGasPriceEditText.getText().toString();
        String gasLimitStr = mGasLimitEditText.getText().toString();

        if (gasPriceStr.isEmpty() || gasPriceStr.length() == 0) {
            gasPriceStr = "0";
        }

        if (gasLimitStr.isEmpty() || gasLimitStr.length() == 0) {
            gasLimitStr = "0";
        }

        BigInteger gasPrice = new BigInteger(gasPriceStr);
        BigInteger gasLimit = new BigInteger(gasLimitStr);
        BigInteger maxFee = gasPrice.multiply(gasLimit);

        BigDecimal weiValue = new BigDecimal(maxFee).multiply(BigDecimal.valueOf(1, 18));
        String maxFeeStr = weiValue.toPlainString();
        mMaximumFeeTextView.setText(maxFeeStr);
    }
}
