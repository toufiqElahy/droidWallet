package com.wallet.crypto.trustapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.samsung.android.sdk.coldwallet.ScwService;
import com.wallet.crypto.trustapp.C;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.entity.ErrorEnvelope;
import com.wallet.crypto.trustapp.entity.GasSettings;
import com.wallet.crypto.trustapp.entity.Wallet;
import com.wallet.crypto.trustapp.repository.TokenRepository;
import com.wallet.crypto.trustapp.util.BalanceUtils;
import com.wallet.crypto.trustapp.util.WalletUtil;
import com.wallet.crypto.trustapp.viewmodel.ConfirmationViewModel;
import com.wallet.crypto.trustapp.viewmodel.ConfirmationViewModelFactory;
import com.wallet.crypto.trustapp.viewmodel.GasSettingsViewModel;

import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.Single;

public class ConfirmationActivity extends BaseActivity {
    AlertDialog dialog;

    @Inject
    ConfirmationViewModelFactory confirmationViewModelFactory;
    ConfirmationViewModel viewModel;

    private TextView fromAddressText;
    private TextView toAddressText;
    private TextView valueText;
    private TextView gasPriceText;
    private TextView gasLimitText;
    private TextView networkFeeText;
    private Button sendButton;

    private BigInteger amount;
    private int decimals;
    private String contractAddress;
    private boolean confirmationForTokenTransfer = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_confirm);
        toolbar();

        fromAddressText = findViewById(R.id.text_from);
        toAddressText = findViewById(R.id.text_to);
        valueText = findViewById(R.id.text_value);
        gasPriceText = findViewById(R.id.text_gas_price);
        gasLimitText = findViewById(R.id.text_gas_limit);
        networkFeeText = findViewById(R.id.text_network_fee);
        sendButton = findViewById(R.id.send_button);

        sendButton.setOnClickListener(view -> onSend());

        String toAddress = getIntent().getStringExtra(C.EXTRA_TO_ADDRESS);
        contractAddress = getIntent().getStringExtra(C.EXTRA_CONTRACT_ADDRESS);
        amount = new BigInteger(getIntent().getStringExtra(C.EXTRA_AMOUNT));
        decimals = getIntent().getIntExtra(C.EXTRA_DECIMALS, -1);
        String symbol = getIntent().getStringExtra(C.EXTRA_SYMBOL);
        symbol = symbol == null ? C.ETH_SYMBOL : symbol;

        confirmationForTokenTransfer = contractAddress != null;

        toAddressText.setText(toAddress);

        String amountString = "-" + BalanceUtils.subunitToBase(amount, decimals).toPlainString() + " " + symbol;
        valueText.setText(amountString);
        valueText.setTextColor(ContextCompat.getColor(this, R.color.red));

        viewModel = ViewModelProviders.of(this, confirmationViewModelFactory)
                .get(ConfirmationViewModel.class);

        viewModel.defaultWallet().observe(this, this::onDefaultWallet);
        viewModel.gasSettings().observe(this, this::onGasSettings);
        viewModel.sendTransaction().observe(this, this::onTransaction);
        viewModel.progress().observe(this, this::onProgress);
        viewModel.error().observe(this, this::onError);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirmation_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit: {
                viewModel.openGasSettings(ConfirmationActivity.this);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.prepare(confirmationForTokenTransfer);
    }

    private void onProgress(boolean shouldShowProgress) {
        hideDialog();
        if (shouldShowProgress) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_dialog_sending)
                    .setView(new ProgressBar(this))
                    .setCancelable(false)
                    .create();
            dialog.show();
        }
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private static String bytesToHex(byte[] bytes)
    {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        String finalHex = new String(hexChars);
        return finalHex;
    }

    private void onSend() {
        GasSettings gasSettings = viewModel.gasSettings().getValue();

        if (WalletUtil.signedEthTransaction != null) {
            if (!confirmationForTokenTransfer) {
                viewModel.createTransaction(
                        fromAddressText.getText().toString(),
                        toAddressText.getText().toString(),
                        amount,
                        gasSettings.gasPrice,
                        gasSettings.gasLimit);
            } else {
                viewModel.createTokenTransfer(
                        fromAddressText.getText().toString(),
                        toAddressText.getText().toString(),
                        contractAddress,
                        amount,
                        gasSettings.gasPrice,
                        gasSettings.gasLimit);
            }

        } else {

        ////}
//            final Web3j web3j = Web3jFactory.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));
//
//
//                EthGetTransactionCount ethGetTransactionCount = web3j
//                        .ethGetTransactionCount(WalletUtil.addr, DefaultBlockParameterName.LATEST)
//                        .send();
//                BigInteger sb= ethGetTransactionCount.getTransactionCount();
//

        BigInteger noce = new BigInteger("1");  //nonce issue
//            double ethValue = Double.valueOf(mValueEditText.getText().toString());
//            BigDecimal weiValue = BigDecimal.valueOf(ethValue).multiply(BigDecimal.valueOf(1, -18));
//            BigInteger gasPrice = new BigInteger(mGasPriceEditText.getText().toString());
//            BigInteger gasLimit = new BigInteger(mGasLimitEditText.getText().toString());
//            Log.d(TAG, "GP : " + gasPrice.toString() + " GL : " + gasLimit.toString());
//            String data = "0xb69ef8a8";
        String to = !confirmationForTokenTransfer == true ? toAddressText.getText().toString() : contractAddress;
        amount = !confirmationForTokenTransfer == true ? amount : BigInteger.valueOf(0);

        final byte[] data = TokenRepository.createTokenTransferData(to, amount);
        String dt = !confirmationForTokenTransfer == true ? "" : bytesToHex(data);
        RawTransaction rawTrx = RawTransaction.createTransaction(noce, gasSettings.gasPrice, gasSettings.gasLimit, to, amount, dt);

        byte[] encodedUnsignedEthTx = TransactionEncoder.encode(rawTrx);

        //byte[] signedEthTransaction = null;
        ScwService.ScwSignEthTransactionCallback callback =
                new ScwService.ScwSignEthTransactionCallback() {
                    @Override
                    public void onSuccess(byte[] signedEthTransaction) {
                        //signedEthTransaction = signedEthTransaction;
                        WalletUtil.signedEthTransaction = signedEthTransaction;
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        //handle error
                        WalletUtil.signedEthTransaction = null;
                    }
                };
        String hdPath = "m/44'/60'";//"m/44'/60'/0'/0/0";
        ScwService.getInstance().signEthTransaction(callback, encodedUnsignedEthTx, hdPath);
    }
    }

    private void onDefaultWallet(Wallet wallet) {
        fromAddressText.setText(wallet.address);
    }

    private void onTransaction(String hash) {
        hideDialog();
        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.transaction_succeeded)
                .setMessage(hash)
                .setPositiveButton(R.string.button_ok, (dialog1, id) -> {
                    finish();
                })
                .setNeutralButton(R.string.copy, (dialog1, id) -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("transaction hash", hash);
                    clipboard.setPrimaryClip(clip);
                    finish();
                })
                .create();
        dialog.show();
    }

    private void onGasSettings(GasSettings gasSettings) {
        String gasPrice = BalanceUtils.weiToGwei(gasSettings.gasPrice) + " " + C.GWEI_UNIT;
        gasPriceText.setText(gasPrice);
        gasLimitText.setText(gasSettings.gasLimit.toString());

        String networkFee = BalanceUtils.weiToEth(gasSettings
                .gasPrice.multiply(gasSettings.gasLimit)).toPlainString() + " " + C.ETH_SYMBOL;
        networkFeeText.setText(networkFee);
    }

    private void onError(ErrorEnvelope error) {
        hideDialog();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.error_transaction_failed)
                .setMessage(error.message)
                .setPositiveButton(R.string.button_ok, (dialog1, id) -> {
                    // Do nothing
                })
                .create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == GasSettingsViewModel.SET_GAS_SETTINGS) {
            if (resultCode == RESULT_OK) {
                BigInteger gasPrice = new BigInteger(intent.getStringExtra(C.EXTRA_GAS_PRICE));
                BigInteger gasLimit = new BigInteger(intent.getStringExtra(C.EXTRA_GAS_LIMIT));
                GasSettings settings = new GasSettings(gasPrice, gasLimit);
                viewModel.gasSettings().postValue(settings);
            }
        }
    }
}
