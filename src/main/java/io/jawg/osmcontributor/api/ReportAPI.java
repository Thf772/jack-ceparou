package io.jawg.osmcontributor.api;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.io.Files;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.jawg.osmcontributor.model.event.CreateNewReportEvent;
import io.jawg.osmcontributor.utils.Box;

/**
 * This class provides the methods to call our own API the report issues via the application
 */
public class ReportAPI {
    public static final String SERVER_ADDRESS = "http://jcerv.heptacle.fr/";
    public static final String CREATE_REPORT_ADDRESS = "http://jcerv.heptacle.fr/api/createReport?";
    public static final String UPDATE_REPORT_ADDRESS = "http://jcerv.heptacle.fr/api/updateReport";
    public static final String UPLOAD_IMAGE_ADDRESS = "http://jcerv.heptacle.fr/api/uploadImage/";
    public static final String GET_REPORTS_ADDRESS = "http://jcerv.heptacle.fr/api/findReports";

    RequestQueue queue;

    /**
     * This method will call the server and create a new report.
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCreateNewReport(final CreateNewReportEvent event) {
        Log.w("REPORT API", "Event to create report carried succsessfully");

        final String charset = "UTF-8";
        String params;
        try {
            params = String.format("type=%s&" + "latitude=%s&" + "longitude=%s&" + "name=%s&" + "description=%s",
                    URLEncoder.encode("OB", charset),
                    URLEncoder.encode(event.getLatitude().toString(), charset),
                    URLEncoder.encode(event.getLongitude().toString(), charset),
                    URLEncoder.encode(event.getTitle(), charset),
                    URLEncoder.encode(event.getDescription(), charset));

            // Request a string response from the provided URL.
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, CREATE_REPORT_ADDRESS + params, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.w("RESPONSE", response.toString());

                            String id = null;
                            try {
                                id = response.get("id").toString();
                                uploadImage(id, event.getImageFilePath());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            uploadImage(id, event.getImageFilePath());

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w("RESPONSE_ERROR", error);
                    String id = null;
                    /*try {
                      //  id = response.get("id").toString();
                      //  uploadImage(id, event.getImageFilePath());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    uploadImage(id, event.getImageFilePath());*/
                }
            });

            queue.add(stringRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void uploadImage (String id, final String imageFilePath){
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, UPLOAD_IMAGE_ADDRESS + id, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        // response
                        Log.d("Response", jsonObject.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "images/JPG");
                //headers.put("Accept", "application/json");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "images/JPG";
            }

            @Override
            public byte[] getBody() {

                try {

                    byte[] data = Files.readBytes(new File(imageFilePath), null);

                    return data;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        queue.add(putRequest);
    }



    public ReportAPI (Context c) {
        queue = Volley.newRequestQueue(c);
    }

    public static List<IssueMarker> getListOfIssues(Box box) {

        return null;
    }
}