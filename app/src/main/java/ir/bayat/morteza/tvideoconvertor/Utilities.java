
package ir.bayat.morteza.tvideoconvertor;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaCodecInfo;
import android.os.Build;
import android.os.Handler;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {


    public static Pattern pattern = Pattern.compile("[\\-0-9]+");
    public static SecureRandom random = new SecureRandom();

    public static volatile DispatchQueue stageQueue = new DispatchQueue("stageQueue");
    public static volatile DispatchQueue globalQueue = new DispatchQueue("globalQueue");
    public static volatile DispatchQueue searchQueue = new DispatchQueue("searchQueue");
    public static volatile DispatchQueue phoneBookQueue = new DispatchQueue("photoBookQueue");

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    static {
        try {
            File URANDOM_FILE = new File("/dev/urandom");
            FileInputStream sUrandomIn = new FileInputStream(URANDOM_FILE);
            byte[] buffer = new byte[1024];
            sUrandomIn.read(buffer);
            sUrandomIn.close();
            random.setSeed(buffer);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

//    public native static int pinBitmap(Bitmap bitmap);
//    public native static void unpinBitmap(Bitmap bitmap);
//    public native static void blurBitmap(Object bitmap, int radius, int unpin, int width, int height, int stride);
//    public native static void calcCDT(ByteBuffer hsvBuffer, int width, int height, ByteBuffer buffer);
//    public native static boolean loadWebpImage(Bitmap bitmap, ByteBuffer buffer, int len, BitmapFactory.Options options, boolean unpin);
    public native static int  convertVideoFrame(ByteBuffer src, ByteBuffer dest, int destFormat, int width, int height, int padding, int swap);
//    private native static void aesIgeEncryption(ByteBuffer buffer, byte[] key, byte[] iv, boolean encrypt, int offset, int length);
//    public native static void aesCtrDecryption(ByteBuffer buffer, byte[] key, byte[] iv, int offset, int length);
//    public native static void aesCtrDecryptionByteArray(byte[] buffer, byte[] key, byte[] iv, int offset, int length, int n);
//    public native static String readlink(String path);

    public static void aesIgeEncryption(ByteBuffer buffer, byte[] key, byte[] iv, boolean encrypt, boolean changeIv, int offset, int length) {
//        aesIgeEncryption(buffer, key, changeIv ? iv : iv.clone(), encrypt, offset, length);
    }

    public static Integer parseInt(String value) {
        if (value == null) {
            return 0;
        }
        Integer val = 0;
        try {
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                String num = matcher.group(0);
                val = Integer.parseInt(num);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return val;
    }

    public static Long parseLong(String value) {
        if (value == null) {
            return 0L;
        }
        Long val = 0L;
        try {
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                String num = matcher.group(0);
                val = Long.parseLong(num);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return val;
    }

    public static String parseIntToString(String value) {
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String hex) {
        if (hex == null) {
            return null;
        }
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static boolean isGoodPrime(byte[] prime, int g) {
        if (!(g >= 2 && g <= 7)) {
            return false;
        }

        if (prime.length != 256 || prime[0] >= 0) {
            return false;
        }

        BigInteger dhBI = new BigInteger(1, prime);

        if (g == 2) { // p mod 8 = 7 for g = 2;
            BigInteger res = dhBI.mod(BigInteger.valueOf(8));
            if (res.intValue() != 7) {
                return false;
            }
        } else if (g == 3) { // p mod 3 = 2 for g = 3;
            BigInteger res = dhBI.mod(BigInteger.valueOf(3));
            if (res.intValue() != 2) {
                return false;
            }
        } else if (g == 5) { // p mod 5 = 1 or 4 for g = 5;
            BigInteger res = dhBI.mod(BigInteger.valueOf(5));
            int val = res.intValue();
            if (val != 1 && val != 4) {
                return false;
            }
        } else if (g == 6) { // p mod 24 = 19 or 23 for g = 6;
            BigInteger res = dhBI.mod(BigInteger.valueOf(24));
            int val = res.intValue();
            if (val != 19 && val != 23) {
                return false;
            }
        } else if (g == 7) { // p mod 7 = 3, 5 or 6 for g = 7.
            BigInteger res = dhBI.mod(BigInteger.valueOf(7));
            int val = res.intValue();
            if (val != 3 && val != 5 && val != 6) {
                return false;
            }
        }

        String hex = bytesToHex(prime);
        if (hex.equals("C71CAEB9C6B1C9048E6C522F70F13F73980D40238E3E21C14934D037563D930F48198A0AA7C14058229493D22530F4DBFA336F6E0AC925139543AED44CCE7C3720FD51F69458705AC68CD4FE6B6B13ABDC9746512969328454F18FAF8C595F642477FE96BB2A941D5BCD1D4AC8CC49880708FA9B378E3C4F3A9060BEE67CF9A4A4A695811051907E162753B56B0F6B410DBA74D8A84B2A14B3144E0EF1284754FD17ED950D5965B4B9DD46582DB1178D169C6BC465B0D6FF9CA3928FEF5B9AE4E418FC15E83EBEA0F87FA9FF5EED70050DED2849F47BF959D956850CE929851F0D8115F635B105EE2E4E15D04B2454BF6F4FADF034B10403119CD8E3B92FCC5B")) {
            return true;
        }

        BigInteger dhBI2 = dhBI.subtract(BigInteger.valueOf(1)).divide(BigInteger.valueOf(2));
        return !(!dhBI.isProbablePrime(30) || !dhBI2.isProbablePrime(30));
    }

    public static boolean isGoodGaAndGb(BigInteger g_a, BigInteger p) {
        return !(g_a.compareTo(BigInteger.valueOf(1)) != 1 || g_a.compareTo(p.subtract(BigInteger.valueOf(1))) != -1);
    }

    public static boolean arraysEquals(byte[] arr1, int offset1, byte[] arr2, int offset2) {
        if (arr1 == null || arr2 == null || offset1 < 0 || offset2 < 0 || arr1.length - offset1 > arr2.length - offset2 || arr1.length - offset1 < 0 || arr2.length - offset2 < 0) {
            return false;
        }
        boolean result = true;
        for (int a = offset1; a < arr1.length; a++) {
            if (arr1[a + offset1] != arr2[a + offset2]) {
                result = false;
            }
        }
        return result;
    }

    public static byte[] computeSHA1(byte[] convertme, int offset, int len) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(convertme, offset, len);
            return md.digest();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return new byte[20];
    }

    public static byte[] computeSHA1(ByteBuffer convertme, int offset, int len) {
        int oldp = convertme.position();
        int oldl = convertme.limit();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            convertme.position(offset);
            convertme.limit(len);
            md.update(convertme);
            return md.digest();
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            convertme.limit(oldl);
            convertme.position(oldp);
        }
        return new byte[20];
    }

    public static byte[] computeSHA1(ByteBuffer convertme) {
        return computeSHA1(convertme, 0, convertme.limit());
    }

    public static byte[] computeSHA1(byte[] convertme) {
        return computeSHA1(convertme, 0, convertme.length);
    }

    public static byte[] computeSHA256(byte[] convertme) {
        return computeSHA256(convertme, 0, convertme.length);
    }

    public static byte[] computeSHA256(byte[] convertme, int offset, int len) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(convertme, offset, len);
            return md.digest();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return new byte[32];
    }

    public static byte[] computeSHA256(byte[] b1, int o1, int l1, ByteBuffer b2, int o2, int l2) {
        int oldp = b2.position();
        int oldl = b2.limit();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(b1, o1, l1);
            b2.position(o2);
            b2.limit(l2);
            md.update(b2);
            return md.digest();
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            b2.limit(oldl);
            b2.position(oldp);
        }
        return new byte[32];
    }

    public static long bytesToLong(byte[] bytes) {
        return ((long) bytes[7] << 56) + (((long) bytes[6] & 0xFF) << 48) + (((long) bytes[5] & 0xFF) << 40) + (((long) bytes[4] & 0xFF) << 32)
                + (((long) bytes[3] & 0xFF) << 24) + (((long) bytes[2] & 0xFF) << 16) + (((long) bytes[1] & 0xFF) << 8) + ((long) bytes[0] & 0xFF);
    }

    public static String MD5(String md5) {
        if (md5 == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int a = 0; a < array.length; a++) {
                sb.append(Integer.toHexString((array[a] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            FileLog.e(e);
        }
        return null;
    }

    // video edit start
    private QualityChooseView qualityChooseView;
//    private PickerBottomLayoutViewer qualityPicker;
//    private RadialProgressView progressView;
//    private VideoTimelinePlayView videoTimelineView;
    private AnimatorSet qualityChooseViewAnimation;

    private int selectedCompression = 1;
    private int compressionsCount = -1;
    private int previousCompression;

    private int rotationValue;
    private int originalWidth;
    private int originalHeight;
    private int resultWidth;
    private int resultHeight;
    private int bitrate;
    private int originalBitrate;
    private float videoDuration;
    private boolean videoHasAudio;
    private long startTime;
    private long endTime;
    private long audioFramesSize;
    private long videoFramesSize;
    private int estimatedSize;
    private long estimatedDuration;
    private long originalSize;
    private VideoObject obj ;
    private Runnable currentLoadingVideoRunnable;
    //private MessageObject videoPreviewMessageObject;
    private boolean tryStartRequestPreviewOnFinish;
    private boolean loadInitialVideo;
    private boolean inPreview;
    private int previewViewEnd;
    private boolean requestingPreview;
    private ImageView compressItem;
    private String currentSubtitle;

    public class QualityChooseView extends View {

        private Paint paint;
        private TextPaint textPaint;

        private int circleSize;
        private int gapSize;
        private int sideSide;
        private int lineSize;

        private boolean moving;
        private boolean startMoving;
        private float startX;

        private  Context context;
        private int startMovingQuality;

        public QualityChooseView(Context context) {
            super(context);

            this.context = context;
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(AndroidUtilities.dp(12));
            textPaint.setColor(0xffcdcdcd);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                getParent().requestDisallowInterceptTouchEvent(true);
                for (int a = 0; a < compressionsCount; a++) {
                    int cx = sideSide + (lineSize + gapSize * 2 + circleSize) * a + circleSize / 2;
                    if (x > cx - AndroidUtilities.dp(15) && x < cx + AndroidUtilities.dp(15)) {
                        startMoving = a == selectedCompression;
                        startX = x;
                        startMovingQuality = selectedCompression;
                        break;
                    }
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (startMoving) {
                    if (Math.abs(startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        moving = true;
                        startMoving = false;
                    }
                } else if (moving) {
                    for (int a = 0; a < compressionsCount; a++) {
                        int cx = sideSide + (lineSize + gapSize * 2 + circleSize) * a + circleSize / 2;
                        int diff = lineSize / 2 + circleSize / 2 + gapSize;
                        if (x > cx - diff && x < cx + diff) {
                            if (selectedCompression != a) {
                                selectedCompression = a;
                                didChangedCompressionLevel(false,context);
                                invalidate();
                            }
                            break;
                        }
                    }
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (!moving) {
                    for (int a = 0; a < compressionsCount; a++) {
                        int cx = sideSide + (lineSize + gapSize * 2 + circleSize) * a + circleSize / 2;
                        if (x > cx - AndroidUtilities.dp(15) && x < cx + AndroidUtilities.dp(15)) {
                            if (selectedCompression != a) {
                                selectedCompression = a;
                                didChangedCompressionLevel(true,context);
                                invalidate();
                            }
                            break;
                        }
                    }
                } else {
                    if (selectedCompression != startMovingQuality) {
                        requestVideoPreview(1);
                    }
                }
                startMoving = false;
                moving = false;
            }
            return true;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            circleSize = AndroidUtilities.dp(12);
            gapSize = AndroidUtilities.dp(2);
            sideSide = AndroidUtilities.dp(18);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (compressionsCount != 1) {
                lineSize = (getMeasuredWidth() - circleSize * compressionsCount - gapSize * 8 - sideSide * 2) / (compressionsCount - 1);
            } else {
                lineSize = (getMeasuredWidth() - circleSize * compressionsCount - gapSize * 8 - sideSide * 2);
            }
            int cy = getMeasuredHeight() / 2 + AndroidUtilities.dp(6);
            for (int a = 0; a < compressionsCount; a++) {
                int cx = sideSide + (lineSize + gapSize * 2 + circleSize) * a + circleSize / 2;
                if (a <= selectedCompression) {
                    paint.setColor(0xff53aeef);
                } else {
                    paint.setColor(0x66ffffff);
                }
                String text;
                if (a == compressionsCount - 1) {
                    text = Math.min(originalWidth, originalHeight) + "p";
                } else if (a == 0) {
                    text = "240p";
                } else if (a == 1) {
                    text = "360p";
                } else if (a == 2) {
                    text = "480p";
                } else {
                    text = "720p";
                }
                float width = textPaint.measureText(text);
                canvas.drawCircle(cx, cy, a == selectedCompression ? AndroidUtilities.dp(8) : circleSize / 2, paint);
                canvas.drawText(text, cx - width / 2, cy - AndroidUtilities.dp(16), textPaint);
                if (a != 0) {
                    int x = cx - circleSize / 2 - gapSize - lineSize;
                    canvas.drawRect(x, cy - AndroidUtilities.dp(1), x + lineSize, cy + AndroidUtilities.dp(2), paint);
                }
            }
        }
    }

    public void updateMuteButton() {
//        if (videoPlayer != null) {
//            videoPlayer.setMute(muteVideo);
//        }
//        if (!videoHasAudio) {
//            muteItem.setEnabled(false);
//            muteItem.setClickable(false);
//            muteItem.setAlpha(0.5f);
//        } else {
//            muteItem.setEnabled(true);
//            muteItem.setClickable(true);
//            muteItem.setAlpha(1.0f);
//            if (muteVideo) {
//                actionBar.setSubtitle(null);
//                muteItem.setImageResource(R.drawable.volume_off);
//                muteItem.setColorFilter(new PorterDuffColorFilter(0xff3dadee, PorterDuff.Mode.MULTIPLY));
//                if (compressItem.getTag() != null) {
//                    compressItem.setClickable(false);
//                    compressItem.setAlpha(0.5f);
//                    compressItem.setEnabled(false);
//                }
//                videoTimelineView.setMaxProgressDiff(30000.0f / videoDuration);
//            } else {
//                muteItem.setColorFilter(null);
//                actionBar.setSubtitle(currentSubtitle);
//                muteItem.setImageResource(R.drawable.volume_on);
//                if (compressItem.getTag() != null) {
//                    compressItem.setClickable(true);
//                    compressItem.setAlpha(1.0f);
//                    compressItem.setEnabled(true);
//                }
//                videoTimelineView.setMaxProgressDiff(1.0f);
//            }
//        }
    }

    private void didChangedCompressionLevel(boolean request , Context activity) {
        SharedPreferences preferences = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("compress_video2", selectedCompression);
        editor.commit();
        updateWidthHeightBitrateForCompression();
        updateVideoInfo();
        if (request) {
            requestVideoPreview(1);
        }
    }

    private void updateVideoInfo() {
//        if (actionBar == null) {
//            return;
//        }
//        if (compressionsCount == 0) {
//            actionBar.setSubtitle(null);
//            return;
//        }
//
//        if (selectedCompression == 0) {
//            compressItem.setImageResource(R.drawable.video_240);
//        } else if (selectedCompression == 1) {
//            compressItem.setImageResource(R.drawable.video_360);
//        } else if (selectedCompression == 2) {
//            compressItem.setImageResource(R.drawable.video_480);
//        } else if (selectedCompression == 3) {
//            compressItem.setImageResource(R.drawable.video_720);
//        } else if (selectedCompression == 4) {
//            compressItem.setImageResource(R.drawable.video_1080);
//        }
//
//        elineView.getLeftProgress()) * videoDuration);

//        int width;
//        int height;
//
//        if (compressItem.getTag() == null || selectedCompression == compressionsCount - 1) {
//            width = rotationValue == 90 || rotationValue == 270 ? originalHeight : originalWidth;
//            height = rotationValue == 90 || rotationValue == 270 ? originalWidth : originalHeight;
//            estimatedSize = (int) (originalSize * ((float) estimatedDuration / videoDuration));
//        } else {
//            width = rotationValue == 90 || rotationValue == 270 ? resultHeight : resultWidth;
//            height = rotationValue == 90 || rotationValue == 270 ? resultWidth : resultHeight;
////            estimatedDuration = (long) Math.ceil((videoTimelineView.getRightProgress() -
////                videoTim
//            estimatedSize = (int) ((audioFramesSize + videoFramesSize) * ((float) estimatedDuration / videoDuration));
//            estimatedSize += estimatedSize / (32 * 1024) * 16;
//        }

//        if (videoTimelineView.getLeftProgress() == 0) {
//            startTime = -1;
//        } else {
//            startTime = (long) (videoTimelineView.getLeftProgress() * videoDuration) * 1000;
//        }
//        if (videoTimelineView.getRightProgress() == 1) {
//            endTime = -1;
//        } else {
//            endTime = (long) (videoTimelineView.getRightProgress() * videoDuration) * 1000;
//        }

//        String videoDimension = String.format("%dx%d", width, height);
//        int minutes = (int) (estimatedDuration / 1000 / 60);
//        int seconds = (int) Math.ceil(estimatedDuration / 1000) - minutes * 60;
//        String videoTimeSize = String.format("%d:%02d, ~%s", minutes, seconds, AndroidUtilities.formatFileSize(estimatedSize));
//        currentSubtitle = String.format("%s, %s", videoDimension, videoTimeSize);
        //actionBar.setSubtitle(muteVideo ? null : currentSubtitle);
    }

    private void requestVideoPreview(int request) {
        if (obj != null) {
            MediaController.getInstance().cancelVideoConvert(obj);
        }
        boolean wasRequestingPreview = requestingPreview && !tryStartRequestPreviewOnFinish;
        requestingPreview = false;
        loadInitialVideo = false;
//        progressView.setVisibility(View.INVISIBLE);
        if (request == 1) {
            if (selectedCompression == compressionsCount - 1) {
                tryStartRequestPreviewOnFinish = false;
                if (!wasRequestingPreview) {
//                    preparePlayer(currentPlayingVideoFile, false, false);
                } else {
//                    progressView.setVisibility(View.VISIBLE);
                    loadInitialVideo = true;
                }
            } else {
                requestingPreview = true;
//                releasePlayer();
                if (obj == null) {
//                    TLRPC.TL_message message = new TLRPC.TL_message();
//                    message.id = 0;
//                    message.message = "";
//                    message.media = new TLRPC.TL_messageMediaEmpty();
//                    message.action = new TLRPC.TL_messageActionEmpty();
                    obj = new VideoObject();
                   // obj.attachPath = new File(FileLoader.getInstance().getDirectory(FileLoader.MEDIA_DIR_CACHE), "video_preview.mp4").getAbsolutePath();
                    obj.videoEditedInfo = new VideoEditedInfo();
                    obj.videoEditedInfo.rotationValue = rotationValue;
                    obj.videoEditedInfo.originalWidth = originalWidth;
                    obj.videoEditedInfo.originalHeight = originalHeight;
                   // obj.videoEditedInfo.originalPath = currentPlayingVideoFile.getAbsolutePath();
                }
                long start = obj.videoEditedInfo.startTime = startTime;
                long end = obj.videoEditedInfo.endTime = endTime;
                if (start == -1) {
                    start = 0;
                }
                if (end == -1) {
                    end = (long) (videoDuration * 1000);
                }
                if (end - start > 5000000) {
                    obj.videoEditedInfo.endTime = start + 5000000;
                }
                obj.videoEditedInfo.bitrate = bitrate;
                obj.videoEditedInfo.resultWidth = resultWidth;
                obj.videoEditedInfo.resultHeight = resultHeight;
                if (!MediaController.getInstance().scheduleVideoConvert(obj, true)) {
                    tryStartRequestPreviewOnFinish = true;
                }
                requestingPreview = true;
//                progressView.setVisibility(View.VISIBLE);
            }
        } else {
            tryStartRequestPreviewOnFinish = false;
            if (request == 2) {
//                preparePlayer(currentPlayingVideoFile, false, false);
            }
        }
//        containerView.invalidate();
    }

    private void updateWidthHeightBitrateForCompression() {
        if (compressionsCount <= 0) {
            return;
        }
        if (selectedCompression >= compressionsCount) {
            selectedCompression = compressionsCount - 1;
        }
        if (selectedCompression != compressionsCount - 1) {
            float maxSize;
            int targetBitrate;
            switch (selectedCompression) {
                case 0:
                    maxSize = 432.0f;
                    targetBitrate = 400000;
                    break;
                case 1:
                    maxSize = 640.0f;
                    targetBitrate = 900000;
                    break;
                case 2:
                    maxSize = 848.0f;
                    targetBitrate = 1100000;
                    break;
                case 3:
                default:
                    targetBitrate = 2500000;
                    maxSize = 1280.0f;
                    break;
            }
            float scale = originalWidth > originalHeight ? maxSize / originalWidth : maxSize / originalHeight;
            resultWidth = Math.round(originalWidth * scale / 2) * 2;
            resultHeight = Math.round(originalHeight * scale / 2) * 2;
            if (bitrate != 0) {
                bitrate = Math.min(targetBitrate, (int) (originalBitrate / scale));
                videoFramesSize = (long) (bitrate / 8 * videoDuration / 1000);
            }

            obj.videoEditedInfo.resultHeight = resultHeight;
            obj.videoEditedInfo.resultWidth  = resultWidth;
            obj.videoEditedInfo.bitrate  = bitrate;
            obj.videoEditedInfo.originalWidth = originalWidth;
            obj.videoEditedInfo.originalHeight = originalHeight;
            obj.videoEditedInfo.muted = false;
            obj.videoEditedInfo.endTime = endTime;
            obj.videoEditedInfo.estimatedDuration = estimatedDuration;
            obj.videoEditedInfo.rotationValue = rotationValue;
            obj.videoEditedInfo.startTime = startTime;
            obj.videoEditedInfo.roundVideo = true;

            FileLog.e( "Path : " + obj.videoEditedInfo.originalHeight + " " + obj.videoEditedInfo.originalWidth);

        }
    }

//    private void showQualityView(final boolean show) {
//        if (show) {
//            previousCompression = selectedCompression;
//        }
//        if (qualityChooseViewAnimation != null) {
//            qualityChooseViewAnimation.cancel();
//        }
//        qualityChooseViewAnimation = new AnimatorSet();
//        if (show) {
//            qualityChooseView.setTag(1);
//            qualityChooseViewAnimation.playTogether(
//                    ObjectAnimator.ofFloat(pickerView, "translationY", 0, AndroidUtilities.dp(152)),
//                    ObjectAnimator.ofFloat(bottomLayout, "translationY", -AndroidUtilities.dp(48), AndroidUtilities.dp(104))
//            );
//        } else {
//            qualityChooseView.setTag(null);
//            qualityChooseViewAnimation.playTogether(
//                    ObjectAnimator.ofFloat(qualityChooseView, "translationY", 0, AndroidUtilities.dp(166)),
//                    ObjectAnimator.ofFloat(qualityPicker, "translationY", 0, AndroidUtilities.dp(166)),
//                    ObjectAnimator.ofFloat(bottomLayout, "translationY", -AndroidUtilities.dp(48), AndroidUtilities.dp(118))
//            );
//        }
//        qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (!animation.equals(qualityChooseViewAnimation)) {
//                    return;
//                }
//                qualityChooseViewAnimation = new AnimatorSet();
//                if (show) {
//                    qualityChooseView.setVisibility(View.VISIBLE);
//                    qualityPicker.setVisibility(View.VISIBLE);
//                    qualityChooseViewAnimation.playTogether(
//                            ObjectAnimator.ofFloat(qualityChooseView, "translationY", 0),
//                            ObjectAnimator.ofFloat(qualityPicker, "translationY", 0),
//                            ObjectAnimator.ofFloat(bottomLayout, "translationY", -AndroidUtilities.dp(48))
//                    );
//                } else {
//                    qualityChooseView.setVisibility(View.INVISIBLE);
//                    qualityPicker.setVisibility(View.INVISIBLE);
//                    qualityChooseViewAnimation.playTogether(
//                            ObjectAnimator.ofFloat(pickerView, "translationY", 0),
//                            ObjectAnimator.ofFloat(bottomLayout, "translationY", -AndroidUtilities.dp(48))
//                    );
//                }
//                qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        if (animation.equals(qualityChooseViewAnimation)) {
//                            qualityChooseViewAnimation = null;
//                        }
//                    }
//                });
//                qualityChooseViewAnimation.setDuration(200);
//                qualityChooseViewAnimation.setInterpolator(new AccelerateInterpolator());
//                qualityChooseViewAnimation.start();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                qualityChooseViewAnimation = null;
//            }
//        });
//        qualityChooseViewAnimation.setDuration(200);
//        qualityChooseViewAnimation.setInterpolator(new DecelerateInterpolator());
//        qualityChooseViewAnimation.start();
//    }

    public VideoObject processOpenVideo(final String videoPath, final Context activity ) {

        obj = new VideoObject();
        obj.videoEditedInfo.originalPath = videoPath;

        if (currentLoadingVideoRunnable != null) {
            Utilities.globalQueue.cancelRunnable(currentLoadingVideoRunnable);
            currentLoadingVideoRunnable = null;
        }
        //videoPreviewMessageObject = null;
        setCompressItemEnabled(false, true);
         boolean muteVideo = false;
        //videoTimelineView.setVideoPath(videoPath);
        compressionsCount = -1;
        rotationValue = 0;
        File file = new File(videoPath);
        originalSize = file.length();

        Utilities.globalQueue.postRunnable(currentLoadingVideoRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentLoadingVideoRunnable != this) {
                    return;
                }
                TrackHeaderBox trackHeaderBox = null;
                boolean isAvc = true;
                try {
                    IsoFile isoFile = new IsoFile(videoPath);
                    List<Box> boxes = Path.getPaths(isoFile, "/moov/trak/");

                    Box boxTest = Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/mp4a/");
                    FileLog.e( "Path isoFile : " + isoFile.toString());
                    if (boxTest == null) {
                        FileLog.d("video hasn't mp4a atom");
                    }

                    boxTest = Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/avc1/");
                    if (boxTest == null) {
                        FileLog.d("video hasn't avc1 atom");
                        isAvc = false;
                    }

                    audioFramesSize = 0;
                    videoFramesSize = 0;
                    for (int b = 0; b < boxes.size(); b++) {
                        if (currentLoadingVideoRunnable != this) {
                            return;
                        }
                        Box box = boxes.get(b);
                        TrackBox trackBox = (TrackBox) box;
                        long sampleSizes = 0;
                        long trackBitrate = 0;
                        try {
                            MediaBox mediaBox = trackBox.getMediaBox();
                            MediaHeaderBox mediaHeaderBox = mediaBox.getMediaHeaderBox();
                            SampleSizeBox sampleSizeBox = mediaBox.getMediaInformationBox().getSampleTableBox().getSampleSizeBox();
                            long[] sizes = sampleSizeBox.getSampleSizes();
                            for (int a = 0; a < sizes.length; a++) {
                                if (currentLoadingVideoRunnable != this) {
                                    return;
                                }
                                sampleSizes += sizes[a];
                            }
                            videoDuration = (float) mediaHeaderBox.getDuration() / (float) mediaHeaderBox.getTimescale();
                            trackBitrate = (int) (sampleSizes * 8 / videoDuration);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                        if (currentLoadingVideoRunnable != this) {
                            return;
                        }
                        TrackHeaderBox headerBox = trackBox.getTrackHeaderBox();
                        if (headerBox.getWidth() != 0 && headerBox.getHeight() != 0) {
                            if (trackHeaderBox == null || trackHeaderBox.getWidth() < headerBox.getWidth() || trackHeaderBox.getHeight() < headerBox.getHeight()) {
                                trackHeaderBox = headerBox;
                                originalBitrate = bitrate = (int) (trackBitrate / 100000 * 100000);
                                if (bitrate > 900000) {
                                    bitrate = 900000;
                                }
                                //obj.videoEditedInfo.bitrate = bitrate;
                                videoFramesSize += sampleSizes;
                            }
                        } else {
                            audioFramesSize += sampleSizes;
                        }
                        obj.videoEditedInfo.bitrate = bitrate;//todo mey be changed.
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                    isAvc = false;
                }
                if (trackHeaderBox == null) {
                    FileLog.d("video hasn't trackHeaderBox atom");
                    isAvc = false;
                }
                final boolean isAvcFinal = isAvc;
                final TrackHeaderBox trackHeaderBoxFinal = trackHeaderBox;
                if (currentLoadingVideoRunnable != this) {
                    return;
                }
                currentLoadingVideoRunnable = null;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (activity == null) {
                            return;
                        }
                        videoHasAudio = isAvcFinal;
                        if (isAvcFinal) {
                            Matrix matrix = trackHeaderBoxFinal.getMatrix();
                            if (matrix.equals(Matrix.ROTATE_90)) {
                                rotationValue = 90;
                            } else if (matrix.equals(Matrix.ROTATE_180)) {
                                rotationValue = 180;
                            } else if (matrix.equals(Matrix.ROTATE_270)) {
                                rotationValue = 270;
                            } else {
                                rotationValue = 0;
                            }
                            resultWidth = originalWidth = (int) trackHeaderBoxFinal.getWidth();
                            resultHeight = originalHeight = (int) trackHeaderBoxFinal.getHeight();

                            videoDuration *= 1000;

                            SharedPreferences preferences = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                            selectedCompression = preferences.getInt("compress_video2", 1);
                            if (originalWidth > 1280 || originalHeight > 1280) {
                                compressionsCount = 5;
                            } else if (originalWidth > 848 || originalHeight > 848) {
                                compressionsCount = 4;
                            } else if (originalWidth > 640 || originalHeight > 640) {
                                compressionsCount = 3;
                            } else if (originalWidth > 480 || originalHeight > 480) {
                                compressionsCount = 2;
                            } else {
                                compressionsCount = 1;
                            }
                            updateWidthHeightBitrateForCompression();

                            setCompressItemEnabled(compressionsCount > 1, true);
                            FileLog.e("compressionsCount = " + compressionsCount + " w = " + originalWidth + " h = " + originalHeight);
                            if (Build.VERSION.SDK_INT < 18 && compressItem.getTag() != null) {
                                try {
                                    MediaCodecInfo codecInfo = MediaController.selectCodec(MediaController.MIME_TYPE);
                                    if (codecInfo == null) {
                                        FileLog.d("no codec info for " + MediaController.MIME_TYPE);
                                        setCompressItemEnabled(false, true);
                                    } else {
                                        String name = codecInfo.getName();
                                        if (name.equals("OMX.google.h264.encoder") ||
                                                name.equals("OMX.ST.VFM.H264Enc") ||
                                                name.equals("OMX.Exynos.avc.enc") ||
                                                name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") ||
                                                name.equals("OMX.MARVELL.VIDEO.H264ENCODER") ||
                                                name.equals("OMX.k3.video.encoder.avc") ||
                                                name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                            FileLog.e("unsupported encoder = " + name);
                                            setCompressItemEnabled(false, true);
                                        } else {
                                            if (MediaController.selectColorFormat(codecInfo, MediaController.MIME_TYPE) == 0) {
                                                FileLog.d("no color format for " + MediaController.MIME_TYPE);
                                                setCompressItemEnabled(false, true);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    setCompressItemEnabled(false, true);
                                    FileLog.e(e);
                                }
                            }
                           // qualityChooseView.invalidate();
                        } else {
                            compressionsCount = 0;
                        }

                        updateVideoInfo();
                        updateMuteButton();
                    }
                },new Handler());
            }
        });


        return  obj;
    }

    private void setCompressItemEnabled(boolean enabled, boolean animated) {
//        if (compressItem == null) {
//            return;
//        }
//        if (enabled && compressItem.getTag() != null || !enabled && compressItem.getTag() == null) {
//            return;
//        }
//        compressItem.setTag(enabled ? 1 : null);
//        compressItem.setEnabled(enabled);
//        compressItem.setClickable(enabled);
//        if (compressItemAnimation != null) {
//            compressItemAnimation.cancel();
//            compressItemAnimation = null;
//        }
//        if (animated) {
//            compressItemAnimation = new AnimatorSet();
//            compressItemAnimation.playTogether(ObjectAnimator.ofFloat(compressItem, "alpha", enabled ? 1.0f : 0.5f));
//            compressItemAnimation.setDuration(180);
//            compressItemAnimation.setInterpolator(decelerateInterpolator);
//            compressItemAnimation.start();
//        } else {
//            compressItem.setAlpha(enabled ? 1.0f : 0.5f);
//        }
    }

}
