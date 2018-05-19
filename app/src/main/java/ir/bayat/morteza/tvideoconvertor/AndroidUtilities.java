package ir.bayat.morteza.tvideoconvertor;

import android.os.Handler;
import android.os.Message;

class AndroidUtilities {

    public static void runOnUIThread(Runnable runnable ,  Handler applicationHandler) {
        runOnUIThread(runnable, 0, applicationHandler);
    }

    public static void runOnUIThread(Runnable runnable, long delay ,  Handler applicationHandler) {
        if (delay == 0) {
            applicationHandler.post(runnable);
        } else {
            applicationHandler.postDelayed(runnable, delay);
        }

        applicationHandler.sendEmptyMessage(1);
    }
}
