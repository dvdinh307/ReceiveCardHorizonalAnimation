package sgm.recevicecardanimation.utils;

import android.app.Activity;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dinhdv on 11/14/2017.
 */

public class RequestDataUtils {
    private static final int MY_SOCKET_TIMEOUT_MS = 30000;

    /**
     * Using request data from server
     */
    public static void requestData(int method, final Activity activity, String url, final Map<String, String> params, final onResult action) {
        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    try {
                        JSONObject objectResponse = new JSONObject(response);
                        int status = objectResponse.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                        String msg = objectResponse.optString(AppConstants.KEY_PARAMS.MESSAGE.toString());
                        if (status == AppConstants.REQUEST_SUCCESS) {
                            try {
                                JSONObject objectData = objectResponse.getJSONObject(AppConstants.KEY_PARAMS.DATA.toString());
                                if (action != null) {
                                    action.onSuccess(objectData, msg);
                                }
                            } catch (JSONException e) {
                                if (action != null)
                                    action.onFail(status);
                                e.printStackTrace();
                            }
                        } else {
                            if (status == AppConstants.STATUS_REQUEST.ACCOUNT_BLOCKED) {
                                Toast.makeText(activity, "Error account block", Toast.LENGTH_SHORT).show();
                            } else {
                                if (action != null)
                                    action.onFail(status);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (action != null)
                        action.onFail(AppConstants.STATUS_REQUEST.REQUEST_ERROR);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (action != null)
                            action.onFail(AppConstants.STATUS_REQUEST.REQUEST_ERROR);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        request.setHeaders(getAuthHeader());
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppApplication.getInstance().addToRequestQueue(request);
    }

    //    protected abstract void registerEvent();
    protected static HashMap<String, String> getAuthHeader() {
        HashMap<String, String> header = new HashMap<>();
        String auth = "";
        header.put("Authorization", auth);
        return header;
    }


    public interface onResult {
        void onSuccess(JSONObject object, String msg);

        void onFail(int error);
    }
}
