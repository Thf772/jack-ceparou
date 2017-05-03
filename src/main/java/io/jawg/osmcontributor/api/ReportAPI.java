package io.jawg.osmcontributor.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import io.jawg.osmcontributor.model.event.CreateNewReportEvent;

/**
 * This class provides the methods to call our own API the report issues via the application
 */
public class ReportAPI {
    public static final String SERVER_ADDRESS = "http://jcerv.heptacle.fr/";
    public static final String CREATE_REPORT_ADDRESS = "http://jcerv.heptacle.fr/api/createReport?";
    public static final String UPDATE_REPORT_ADDRESS = "http://jcerv.heptacle.fr/api/updateReport?";
    public static final String UPLOAD_IMAGE_ADDRESS = "http://jcerv.heptacle.fr/api/uploadImage/";

    RequestQueue queue;

    /**
     * This method will call the server and create a new report.
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCreateNewReport(CreateNewReportEvent event)
    {
        Log.w("REPORT API", "Event to create report carried succsessfully");

        String charset = "UTF-8";
        String query;
        try {
            query = String.format("type=%s&" + "latitude=%s&" + "longitude=%s&" + "titre=%s&" + "details=%s",
                    URLEncoder.encode("OB",charset),
                    URLEncoder.encode(event.getLatitude().toString(),charset),
                    URLEncoder.encode(event.getLongitude().toString(),charset),
                    URLEncoder.encode(event.getTitle(),charset),
                    URLEncoder.encode(event.getDescription(),charset));

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_REPORT_ADDRESS + query,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.w("RESPONSE",response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w("RESPONSE_ERROR", error);
                }
            });

            queue.add(stringRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void onGet

    public ReportAPI (Context c) {
        queue = Volley.newRequestQueue(c);
    }
}