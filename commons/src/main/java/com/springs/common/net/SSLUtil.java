package com.springs.common.net;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLUtil {
    public static X509TrustManager unSafeTrustManager = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };
    public static HostnameVerifier UnSafeHostnameVerifier = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public SSLUtil() {
    }

    public static SSLParams getSslSocketFactory() {
        return getSslSocketFactoryBase((X509TrustManager)null, (InputStream)null, (String)null);
    }

    public static SSLParams getSslSocketFactory(X509TrustManager trustManager) {
        return getSslSocketFactoryBase(trustManager, (InputStream)null, (String)null);
    }

    public static SSLParams getSslSocketFactory(InputStream... certificates) {
        return getSslSocketFactoryBase((X509TrustManager)null, (InputStream)null, (String)null, certificates);
    }

    public static SSLParams getSslSocketFactory(InputStream bksFile, String password, InputStream... certificates) {
        return getSslSocketFactoryBase((X509TrustManager)null, bksFile, password, certificates);
    }

    public static SSLParams getSslSocketFactory(InputStream bksFile, String password, X509TrustManager trustManager) {
        return getSslSocketFactoryBase(trustManager, bksFile, password);
    }

    private static SSLParams getSslSocketFactoryBase(X509TrustManager trustManager, InputStream bksFile, String password, InputStream... certificates) {
        SSLParams sslParams = new SSLParams();

        try {
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            X509TrustManager manager;
            if (trustManager != null) {
                manager = trustManager;
            } else if (trustManagers != null) {
                manager = chooseTrustManager(trustManagers);
            } else {
                manager = unSafeTrustManager;
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, new TrustManager[]{manager}, (SecureRandom)null);
            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = manager;
            return sslParams;
        } catch (NoSuchAlgorithmException var9) {
            throw new AssertionError(var9);
        } catch (KeyManagementException var10) {
            throw new AssertionError(var10);
        }
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile != null && password != null) {
                KeyStore clientKeyStore = KeyStore.getInstance("BKS");
                clientKeyStore.load(bksFile, password.toCharArray());
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(clientKeyStore, password.toCharArray());
                return kmf.getKeyManagers();
            } else {
                return null;
            }
        } catch (Exception var4) {
            Log.e("ssl", var4.getMessage());
            return null;
        }
    }

    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates != null && certificates.length > 0) {
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load((KeyStore.LoadStoreParameter)null);
                int index = 0;
                InputStream[] var4 = certificates;
                int var5 = certificates.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    InputStream certStream = var4[var6];
                    String certificateAlias = Integer.toString(index++);
                    Certificate cert = certificateFactory.generateCertificate(certStream);
                    keyStore.setCertificateEntry(certificateAlias, cert);

                    try {
                        if (certStream != null) {
                            certStream.close();
                        }
                    } catch (IOException var11) {
                        Log.e("ssl", var11.getMessage());
                    }
                }

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);
                return tmf.getTrustManagers();
            } catch (Exception var12) {
                Log.e("ssl", var12.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        TrustManager[] var1 = trustManagers;
        int var2 = trustManagers.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            TrustManager trustManager = var1[var3];
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager)trustManager;
            }
        }

        return null;
    }

    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager trustManager;

        public SSLParams() {
        }
    }
}