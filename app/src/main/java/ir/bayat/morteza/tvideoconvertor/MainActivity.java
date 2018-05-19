package ir.bayat.morteza.tvideoconvertor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

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


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        Log.e("START TIME ", currentDateandTime);

        VideoObject videoObject = new VideoObject();

        videoObject.videoEditedInfo.originalPath = "file:///android_asset/" + "ff.mp4";

        mediaController = MediaController.getInstance();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                Log.e("START TIME 1za ", currentDateandTime);
                Log.d("START", "handleMessage " + msg.what + " in " + Thread.currentThread());
            }
        };

        mediaController.setBaseActivity(this, handler);

        mediaController.scheduleVideoConvert(videoObject);
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
