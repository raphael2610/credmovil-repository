package pe.com.cmacica.flujocredito.AgenteServicio;



import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyFutureSingleton {

    private static  VolleyFutureSingleton mInstance;
    private RequestQueue mRequestQueue;
    private Context mContext;

    private VolleyFutureSingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static VolleyFutureSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyFutureSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public<CH> void addToRequestQueue(Request<CH> request) {
        getRequestQueue().add(request);
    }


}
