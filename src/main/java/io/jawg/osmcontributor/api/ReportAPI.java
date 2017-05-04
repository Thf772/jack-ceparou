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
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
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

    public static final String CHARSET = "UTF-8";

    //RequestQueue queue;

    /**
     * This method will call the server and create a new report.
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCreateNewReport(final CreateNewReportEvent event) {
        Log.w("REPORT API", "Event to create report carried succsessfully");


        String params;
        try {
            params = String.format("type=%s&" + "latitude=%s&" + "longitude=%s&" + "name=%s&" + "description=%s",
                    URLEncoder.encode("OB", CHARSET),
                    URLEncoder.encode(event.getLatitude().toString(), CHARSET),
                    URLEncoder.encode(event.getLongitude().toString(), CHARSET),
                    URLEncoder.encode(event.getTitle(), CHARSET),
                    URLEncoder.encode(event.getDescription(), CHARSET));

            URLConnection connection = new URL(CREATE_REPORT_ADDRESS).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", CHARSET);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
            OutputStream output = connection.getOutputStream();
            output.write(params.getBytes(CHARSET));


            InputStream response = connection.getInputStream();

            StringBuilder builder = new StringBuilder();
            int character;
            while ((character = response.read()) != -1) {
                builder.append((char) character);
            }
            String text = builder.toString();
            response.close();

            JSONObject jObject = new JSONObject(text);


            JSONObject value = jObject.getJSONObject("value");

            Integer id = value.getInt("id");
            if (id != null){
                //Let's try to do a put


                URL url = new URL(UPLOAD_IMAGE_ADDRESS + id.toString());
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestMethod("PUT");
                httpCon.setRequestProperty("Content-Type","image/jpeg");
                BufferedOutputStream out = new BufferedOutputStream(
                        httpCon.getOutputStream());
                out.write(Files.toByteArray(new File(event.getImageFilePath())));
                out.close();
                InputStream in = httpCon.getInputStream();

                StringBuilder builder2 = new StringBuilder();
                while ((character = in.read()) != -1) {
                    builder.append((char) character);
                }
                text = builder.toString();
                response.close();

                Log.e("Server Response", text);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<IssueMarker> getListOfIssues(Box box) {
        List<IssueMarker> listIssues = new LinkedList<>();
        try {
            String params = "";
            URLConnection connection = new URL(GET_REPORTS_ADDRESS).openConnection();
            connection.setDoOutput(true); //This is a post
            connection.setRequestProperty("Accept-Charset", CHARSET);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
            OutputStream output = connection.getOutputStream();
            output.write(params.getBytes(CHARSET));

            InputStream response = connection.getInputStream();

            StringBuilder builder = new StringBuilder();
            int character;
            while ((character = response.read()) != -1) {
                builder.append((char) character);
            }
            String text = builder.toString();
            response.close();

            JSONObject jObject = new JSONObject(text);

            JSONArray values = jObject.getJSONArray("value");


            for (int i = 0; i<values.length(); i++)
            {
                JSONObject val = values.getJSONObject(i);
                IssueMarker issue = new IssueMarker(new LatLng(val.getDouble("latitude"),val.getDouble("longitude")), val.getString("name"), val.getString("description"), new Long(i));
                Log.w("Ajout de Issue", issue.title);
                listIssues.add(issue);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listIssues;
    }

    static ReportAPI instance;
    private ReportAPI () {}

    public static ReportAPI getInstance() {
        if (instance == null) {
            instance = new ReportAPI();
        }
        return instance;
    }
}