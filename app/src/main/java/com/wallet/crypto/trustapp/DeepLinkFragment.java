package com.wallet.crypto.trustapp;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.samsung.android.sdk.coldwallet.DeepLink;

public class DeepLinkFragment extends Fragment implements View.OnClickListener {
    private static int pushNumber = 0;

    private Notification.Builder mBuilder;

    public DeepLinkFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initNotificationBuilder();
        View view = inflater.inflate(R.layout.fragment_deeplink, container, false);
        final EditText editCustomDeeplink = (EditText) view.findViewById(R.id.edit_custom_deeplink);
        Button btnCustomDeeplink = (Button) view.findViewById(R.id.button_custom_deeplink);
        btnCustomDeeplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customDeeplink = editCustomDeeplink.getText().toString();
                if (TextUtils.isEmpty(customDeeplink))
                    return;
                startDeepLinkByPush(customDeeplink, v);
            }
        });

        Button btnReset = (Button) view.findViewById(R.id.reset);
        btnReset.setContentDescription(DeepLink.RESET);
        btnReset.setOnClickListener(this);

        Button changePin = (Button) view.findViewById(R.id.change_pin);
        changePin.setContentDescription(DeepLink.CHANGE_PIN);
        changePin.setOnClickListener(this);

        Button btnDisplayWallet = (Button) view.findViewById(R.id.display_wallet);
        btnDisplayWallet.setContentDescription(DeepLink.DISPLAY_WALLET);
        btnDisplayWallet.setOnClickListener(this);

        Button btnNoticeContent = (Button) view.findViewById(R.id.notices);
        btnNoticeContent.setContentDescription(String.format(DeepLink.NOTICE_CONTENT, 3));
        btnNoticeContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "Blockchain KeyStore NotiTest";
                String subtitle = "This is for notification test. Not issue";
                startDeepLinkByPush(v.getContentDescription().toString(), title, subtitle);
            }
        });

        return view;
    }

    private void initNotificationBuilder() {
        mBuilder = new Notification.Builder(getActivity(), AbstractCWSampleActivity.CHANNEL_ID);
        //mBuilder.setLargeIcon(R.drawable.ic_launcher);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setTicker("ticker");
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setContentTitle("Blockchain KeyStore Test");
        mBuilder.setContentText("content");
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
    }


    public void startDeepLinkByPush(String uriString, String title, String content) {
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        startDeepLinkByPush(uriString);
    }


    public void startDeepLinkByPush(String uriString, View v) {
        Button btn = (Button) v;
        mBuilder.setContentTitle(btn.getText());
        mBuilder.setContentText(btn.getContentDescription());
        startDeepLinkByPush(uriString);
    }


    public void startDeepLinkByPush(String uriString) {
        try {
            Uri uri = Uri.parse(uriString);
            Intent displayIntent = new Intent(Intent.ACTION_VIEW, uri);
            displayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, displayIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            NotificationManager notiMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            if (notiMgr != null) {
                notiMgr.notify(pushNumber++, mBuilder.build());
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        startDeepLinkByPush(v.getContentDescription().toString(), v);
    }
}