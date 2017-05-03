package io.jawg.osmcontributor.api;

import android.util.Log;

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
    public static final String CREATE_REPORT_ADDRESS = "http://jcerv.heptacle.fr/api/createReport";
    public static final String UPDATE_REPORT_ADDRESS = "http://jcerv.heptacle.fr/api/updateReport";
    public static final String UPLOAD_IMAGE_ADDRESS = "http://jcerv.heptacle.fr/api/uploadImage/";

    /**
     * This method will call the server and create a new report.
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCreateNewReport(CreateNewReportEvent event)
    {
        Log.w("REPORT API", "Event to create report carried succsessfully");

        try {
            String charset = "UTF-8";
            String query = String.format("type=%s&" + "latitude=%s&" + "longitude=%s&" + "titre=%s&" + "details=%s",
                    URLEncoder.encode("OB",charset),
                    URLEncoder.encode(event.getLatitude().toString(),charset),
                    URLEncoder.encode(event.getLongitude().toString(),charset),
                    URLEncoder.encode(event.getTitle(),charset),
                    URLEncoder.encode(event.getDescription(),charset));

            //Creation of the http request
            URLConnection connection = new URL(CREATE_REPORT_ADDRESS + query).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            InputStream response = connection.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
            //TODO Send a nice error message to the user
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}