package io.jawg.osmcontributor.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mapbox.mapboxsdk.location.LocationServices;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.jawg.osmcontributor.api.ReportAPI;

import io.jawg.osmcontributor.R;
import io.jawg.osmcontributor.model.event.CreateNewReportEvent;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

public class NewReportActivity extends AppCompatActivity {

    ImageCapture imageCapture;
    String imageFilePath;
    ReportAPI reportAPI;
    EventBus eventBus;

    @BindView(R.id.send_button)
    Button sendReportButton;

    @BindView(R.id.image_view)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);
        ButterKnife.bind(this);
        eventBus = EventBus.getDefault();
        reportAPI = new ReportAPI(this);
        eventBus.register(reportAPI);

        imageCapture = new ImageCapture(this);
        View.OnClickListener sendButtonListener = new SendReport(this);

        sendReportButton.setOnClickListener(sendButtonListener);

        //Open the camera to take a picture

        imageView.setOnClickListener(imageCapture);
        imageCapture.onClick(imageView);
    }




    /**
     * This method handles the return on activity. Here the only activty that may be called is the
     * camera one.
     * On success, it will display the image taken in the image view.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCapture.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK)
        {

            try {
                FileInputStream imageStored = new FileInputStream(imageFilePath);
                Bitmap bitmapImage = BitmapFactory.decodeStream(imageStored);
                imageView.setImageDrawable(null);
                imageView.setImageBitmap(bitmapImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * This class is here to handle the camera and capture a picture
     */
    private class ImageCapture implements View.OnClickListener {
        static final int REQUEST_TAKE_PHOTO = 1;

        Context parent;

        public ImageCapture(Context parentIntent) {
            parent = parentIntent;
        }

        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent();

        }

        /**
         * This method will call the intent to take a picture
         * The result of the picture handling will be done in onActivityRe
         */
        private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    Uri photoURI = FileProvider.getUriForFile(parent,
                            "com.hackaton4if.h4112.jack_ceparou.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    // TODO carry the error to the user and explain what went wrong
                }
            }
        }

        /**
         * Creates a file to store the picture in the application directory.
         *
         * @return an image file
         * @throws IOException
         */
        protected File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp;
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            // Save a file: path for use with ACTION_VIEW intents
            imageFilePath = image.getAbsolutePath();
            return image;
        }
    }
    /**
     * This class handles the click on the 'send' button located at the bottom of the screen.
     * Checks on the content filled by the user are performed. If fields are missing the user will be
     * prompted to fill them.
     * If all the fields are good, the report is sent.
     * If a problem occurs in this last step (network issues etc ...), the user is warned.
     */
    private class SendReport implements View.OnClickListener {
        NewReportActivity parent;   //Parent continer that will be handled by this class
        String serverAddress = "http://jcerv.heptacle.fr/";
        String createReportAddr = "api/createReport";
        String updateReportAddr = "api/updateReport";
        String uploadImageAddr = "api/uploadImage/";

        /**
         * What happens when the 'send' button is clicked.
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            if (compulsoryFieldsAreFilled()) {   //Try and send the report using an async task
                final EditText title = (EditText) findViewById(R.id.IssueName);
                final EditText description = (EditText) findViewById(R.id.IssueDetails);

                String issueTitle =  title.getText().toString();
                String issueDescription = description.getText().toString();

                LocationServices service = LocationServices.getLocationServices(parent);
                service.toggleGPS(true);
                if ( service.isGPSEnabled() ) {
                    Location loc = service.getLastLocation();

                    CreateNewReportEvent event = new CreateNewReportEvent(issueTitle, issueDescription, loc.getLatitude(), loc.getLongitude(), imageFilePath);

                    eventBus.post(event);   //The event is posted, it will be carried to the ReportAPI class
                    parent.onDestroy();
                } else {
                    //TODO there is something wrong with the gps
                }

            } else {   //TODO Kindky ask the user to give a title to its new issue

                final EditText title = (EditText) findViewById(R.id.IssueName);
                title.performClick();
            } final ImageView imageView = (ImageView) findViewById(R.id.image);
        }

        boolean compulsoryFieldsAreFilled() {
            final EditText title = (EditText) findViewById(R.id.IssueName);
            return title.getText().length() > 0;
        }

        public SendReport(NewReportActivity parentContainer) {
            parent = parentContainer;
        }


    }
}

