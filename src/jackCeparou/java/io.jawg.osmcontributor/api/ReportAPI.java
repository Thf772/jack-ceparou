package io.jawg.osmcontributor.api;

import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    @@Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCreateNewReport(CreateNewReportEvent event)
    {
        Log.w("REPORT API", "Event to create report carried succsessfully");
    }
}