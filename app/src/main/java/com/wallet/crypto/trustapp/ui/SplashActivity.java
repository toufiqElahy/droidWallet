package com.wallet.crypto.trustapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.samsung.android.sdk.coldwallet.*;
import com.wallet.crypto.trustapp.BuildConfig;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.entity.Wallet;
import com.wallet.crypto.trustapp.router.ManageWalletsRouter;
import com.wallet.crypto.trustapp.router.TransactionsRouter;
import com.wallet.crypto.trustapp.util.WalletUtil;
import com.wallet.crypto.trustapp.viewmodel.SplashViewModel;
import com.wallet.crypto.trustapp.viewmodel.SplashViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    @Inject
    SplashViewModelFactory splashViewModelFactory;
    SplashViewModel splashViewModel;

    private void ScwDeepLinkGALAXY_STORE() {
        Uri uri = Uri.parse(ScwDeepLink.GALAXY_STORE);
        Intent displayIntent = new Intent(Intent.ACTION_VIEW, uri);
        displayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void ScwDeepLinkMain() {
        Uri uri = Uri.parse(ScwDeepLink.MAIN);
        Intent displayIntent = new Intent(Intent.ACTION_VIEW, uri);
        displayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs1 = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        String txt = prefs1.getString("addr", null);
        WalletUtil.addr = txt;//"0x61A4660b9A48337b9e245a0Ec11E618fB44eB0Ff";

        ScwService scwServiceInstance = ScwService.getInstance();
        if(scwServiceInstance==null){

            finish();
            Toast.makeText(this, "Device Not Supported.", Toast.LENGTH_LONG).show();
            System.exit(0);
            return;
        }

        int keystoreApiLevel = scwServiceInstance.getKeystoreApiLevel();
        boolean isKeystoreApiSupported = keystoreApiLevel > 0;
        if(!isKeystoreApiSupported){//update
            ScwDeepLinkGALAXY_STORE();
        }


        String seedHash = scwServiceInstance.getSeedHash();
        boolean initialized = (seedHash != null && seedHash.length() > 0);

        if (!initialized) {
            ScwDeepLinkMain();

            LinearLayout linearLayout = new LinearLayout(this);
            Button button = new Button(this);
            button.setText(" Press to Use the App ");
            button.setTextSize(20);
            button.setGravity(Gravity.CENTER);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String seedHash = scwServiceInstance.getSeedHash();
                    boolean initialized = (seedHash != null && seedHash.length() > 0);

                    if (!initialized) {
                        ScwDeepLinkMain();
                    }else {
                        finish();
                        startActivity(getIntent());
                    }
                }
            });

            linearLayout.addView(button);

            this.setContentView(linearLayout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            AndroidInjection.inject(this);
            super.onCreate(savedInstanceState);
        } else {
            SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
            String restoredText = prefs.getString("seedHash", null);

            if (!seedHash.equals(restoredText)) {
                ScwService.ScwGetAddressListCallback callback =
                        new ScwService.ScwGetAddressListCallback() {
                            @Override
                            public void onSuccess(List<String> addressList) {
                                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                                editor.putString("seedHash", seedHash);
                                WalletUtil.addr = addressList.get(0);
                                editor.putString("addr", addressList.get(0));
                                editor.apply();
                            }

                            @Override
                            public void onFailure(int errorCode) {
                                //handle errors
                            }
                        };
                String hdpath1 = WalletUtil.hdPath;

                ArrayList<String> hdPathList = new ArrayList<>();
                hdPathList.add(hdpath1);

                scwServiceInstance.getAddressList(callback, hdPathList);
            }

        ////

        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

        splashViewModel = ViewModelProviders.of(this, splashViewModelFactory)
                .get(SplashViewModel.class);
        splashViewModel.wallets().observe(this, this::onWallets);
    }
    }

    private void onWallets(Wallet[] wallets) {
        // Start home activity
        if (wallets.length == 0) {
            new ManageWalletsRouter().open(this, true);
        } else {
            new TransactionsRouter().open(this, true);
        }
    }

}
