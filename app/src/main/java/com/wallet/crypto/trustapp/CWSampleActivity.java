package com.wallet.crypto.trustapp;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wallet.crypto.trustapp.utility.HexUtil;
import com.wallet.crypto.trustapp.utility.PartnerVerifyUtil;

public class CWSampleActivity extends AbstractCWSampleActivity {

    private static final String TAG = CWSampleActivity.class.getSimpleName();
    EditText mPartnerEditText;
    TextView mPartnerCerti;
    TextView mPartnerVersion;

    @Override
    protected void handlingSignedETHTx(byte[] signedEthTx) {
        if(signedEthTx != null && signedEthTx.length > 0) {
            runOnUiThread(() -> {
                String msg = HexUtil.toHexString(signedEthTx);
                mTxTextView.setText(msg);
            });
        }
    }

    @Override
    protected void handlingSignedMessage(byte[] signedMessage) {
        if(signedMessage != null && signedMessage.length > 0) {
            runOnUiThread(() -> {
                String msg = HexUtil.toHexString(signedMessage);
                mSignedMsgTextView.setText(msg);
            });
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.partnerLayout).setVisibility(View.VISIBLE);

        Button partnerButton = findViewById(R.id.partnerBtn);
        partnerButton.setOnClickListener(mBtnPartnerEnter);

        mPartnerEditText = findViewById(R.id.partnerEditText);
        mPartnerCerti = findViewById(R.id.partnerCerti);
        mPartnerVersion = findViewById(R.id.partnerVersion);
    }

    private View.OnClickListener mBtnPartnerEnter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String str = String.valueOf(mPartnerEditText.getText().toString());
            mPartnerCerti.setText(PartnerVerifyUtil.certificateFingerprint(getApplicationContext(), str));
            mPartnerVersion.setText(PartnerVerifyUtil.getVersionName(getApplicationContext(), str));
            Log.d(TAG, "Partner CertificationFingerprint : " + mPartnerCerti.getText());
            Log.d(TAG, "Partner App version : " + mPartnerVersion.getText());
        }
    };
}
