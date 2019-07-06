package com.wallet.crypto.trustapp.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


public class PartnerVerifyUtil {
    private static final String TAG = PartnerVerifyUtil.class.getSimpleName();

    public static String certificateFingerprint(Context ctx, String packageName) {
        try {
            //@SuppressLint("WrongConstant")
//            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
//            final Signature[] signatures = packageInfo.signingInfo.getApkContentsSigners();
//            InputStream input = new ByteArrayInputStream(signatures[0].toByteArray());
//            CertificateFactory cf = CertificateFactory.getInstance("X509");
//            X509Certificate cert = (X509Certificate) cf.generateCertificate(input);
//            MessageDigest mdt = MessageDigest.getInstance("SHA256");
//            byte[] certfp = mdt.digest(cert.getEncoded());
//            CWLog.d(TAG, "SHA256 - " + HexUtil.toHexString(certfp));
//            return HexUtil.toHexString(certfp);
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public static String getVersionName(Context ctx, String packageName) {
        try {
            @SuppressLint("WrongConstant")
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getPackageName(Context ctx, int uid) {
        return ctx.getPackageManager().getNameForUid(uid);
    }
}
