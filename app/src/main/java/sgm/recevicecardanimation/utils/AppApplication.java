package sgm.recevicecardanimation.utils;

import android.app.Application;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import sgm.recevicecardanimation.R;

/**
 * Created by Tuấn Sơn on 19/7/2017.
 */

public class AppApplication extends Application {
    public static String TAG = "AppApplication";
    public static String LICENSE_KEY = "";
    // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    public static final String MERCHANT_ID = null;

    public static final int TIMEOUT_DEFAULT = 30000;
    private RequestQueue mRequestQueue;
    private static AppApplication sInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        if (!AppConstants.isTestMode)
            LICENSE_KEY = getString(R.string.base64_key);
    }

    public static synchronized AppApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack(null, createSslSocketFactory()));
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, int timeoutInMillis) {
        getRequestQueue().add(req.setRetryPolicy(
                new DefaultRetryPolicy(timeoutInMillis, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setShouldCache(false);
        addToRequestQueue(req, TIMEOUT_DEFAULT);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void cancelAllRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    private static SSLSocketFactory createSslSocketFactory() {
        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};

        SSLContext sslContext = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
        }

        return sslSocketFactory;
    }

}
