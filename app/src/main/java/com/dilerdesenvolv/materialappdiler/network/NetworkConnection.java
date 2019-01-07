package com.dilerdesenvolv.materialappdiler.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dilerdesenvolv.materialappdiler.domain.WrapObjToNetwork;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * Created by T-Gamer on 13/08/2016.
 */
public class NetworkConnection {

    private static NetworkConnection mInstance;
    private Context mContext;
    private RequestQueue mRequestQueue;

    public NetworkConnection(Context c) {
        mContext = c;
        mRequestQueue = getRequestQueue();
    }

    public static NetworkConnection getInstance(Context c) {
        if (mInstance == null) {
            mInstance = new NetworkConnection(c.getApplicationContext());
        }

        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }

        return mRequestQueue;
    }

    public <T> void addRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public void execute(final Transaction transaction, final String tag) {
        WrapObjToNetwork obj = transaction.doBefore();
        Gson gson = new Gson();

        if (obj == null) {
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("jsonObject", gson.toJson(obj));

        Log.i("LOG", "JSON SENT" + gson.toJson(obj));

        CustomRequest request = new CustomRequest(Request.Method.POST,
                "",
                params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        transaction.doAfter(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "erro NetworkConnection onErrorResponse " + error.getMessage(), Toast.LENGTH_SHORT).show();

                        transaction.doAfter(null);
                    }
                });
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequestQueue(request);
    }

}
