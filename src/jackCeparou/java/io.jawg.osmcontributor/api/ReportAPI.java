package io.jawg.osmcontributor.api;

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
     * @return
     */
    public static void createNewReport(String title, String details, String imageFilePath)
    {
        //TODO implement
    }
}