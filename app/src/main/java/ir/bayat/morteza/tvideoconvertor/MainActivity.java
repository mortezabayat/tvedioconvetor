package ir.bayat.morteza.tvideoconvertor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }
    MediaController mediaController;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);

       VideoObject videoObject = new VideoObject();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        Log.e("START TIME ", currentDateandTime);



        mediaController = MediaController.getInstance();


        //Environment.getExternalStorageDirectory().getAbsolutePath() +
        //                "/DCIM/Camera/20180524_133208.mp4"

        Utilities uti = new Utilities();
        String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/Camera/12.mp4").getAbsolutePath();

        Log.e(getClass().getSimpleName(),path);
        videoObject  =  uti.processOpenVideo(path, this);
        videoObject.attachPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/ffnew.mp4";
        Log.e("PAht : ",  videoObject.attachPath );
        Log.e("bitrate : ",  videoObject.videoEditedInfo.bitrate + "." );



        final VideoObject finalVideoObject = videoObject;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("bitrate : ",  finalVideoObject.videoEditedInfo.bitrate + "." );
                mediaController.scheduleVideoConvert(finalVideoObject);

            }
        });

        final VideoObject finalVideoObject1 = videoObject;
        handler = new Handler() {
            public void handleMessage(Message msg) {

                TextView tv = (TextView) findViewById(R.id.sample_text);
                tv.setText(finalVideoObject1.attachPath);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                Log.e("START TIME 1za ", currentDateandTime);
                Log.d("START", "handleMessage " + msg.what + " in " + Thread.currentThread());
            }
        };
        mediaController.setBaseActivity(this, handler);

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        // tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    // public native String stringFromJNI();
}
