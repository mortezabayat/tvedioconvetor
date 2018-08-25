package com.morteza.videocompressor.example

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.morteza.videocompressor.Config
import com.morteza.videocompressor.MediaController
import com.morteza.videocompressor.VideoInfo
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URISyntaxException
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() , SeekBar.OnSeekBarChangeListener {

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        Log.e("SeekBar " , String.format("progress %d  fromUser %s " ,progress,fromUser));
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        Log.e("SeekBar " , String.format("onStartTrackingTouch"));
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        Log.e("SeekBar " , String.format("onStopTrackingTouch"));
    }


    private val RESULT_CODE_COMPRESS_VIDEO = 3
    private val TAG = javaClass.simpleName
    private lateinit var tempFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createConvertVideoFolder()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        seekBar.setOnSeekBarChangeListener(this);

        btnSelectVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            //Intent(Intent.ACTION_GET_CONTENT);
            intent.type = "video/*"
            startActivityForResult(intent, RESULT_CODE_COMPRESS_VIDEO)

            textView.setText(R.string.please_notice)
        }

        btnCompressVideo.setOnClickListener {
            compress(seekBar.progress, muteSwitch.isChecked)

            var chars =  textView.text;
            textView.text = String.format("Compress Level %d \n %s" , seekBar.progress ,chars) + "\n"
            btnCompressVideo.isEnabled = false
        }

    }


    @SuppressLint("LongLogTag")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(reqCode: Int, resCode: Int, data: Intent?) {
        if (resCode == Activity.RESULT_OK && data != null) {

            val uri = data.data

            if (reqCode == RESULT_CODE_COMPRESS_VIDEO) {
                if (uri != null) {
                    val cursor = requireContext().contentResolver.query(uri, null, null, null, null, null)

                    try {
                        if (cursor != null && cursor.moveToFirst()) {

                            val displayName = cursor.getString(
                                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            Log.i(TAG, "Display Name: $displayName")

                            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                            val size = if (!cursor.isNull(sizeIndex)) {
                                cursor.getString(sizeIndex)
                            } else {
                                "Unknown"
                            }
                            Log.i(TAG, "Size: $size")

                            tempFile = File(getFilePath(requireContext(), uri)!!)

                            Log.e(TAG, "path: " + tempFile.path)
                            //FileUtils.saveTempFile(displayName, this, uri);

                            //                            new Thread(new Runnable() {
                            //                                @Override
                            //                                public void run() {
                            //                                    MediaController.getInstance().processVideo(tempFile.getAbsolutePath());
                            //                                }
                            //                            }).start();
                            val (width, higth) = getWidthAndHeight(tempFile.path)
                            val compressionsCount = MediaController.getCompressionsCount(width, higth)
                            seekBar.progress = 0
                            seekBar.max = compressionsCount - 1;
                            editText.setText(tempFile.path)

                            btnCompressVideo.isEnabled = true

                        }
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    } finally {
                        cursor?.close()
                    }
                }
            }
        }
    }

    fun compress(selectedCompression: Int, checked: Boolean) {
        //MediaController.getInstance().scheduleVideoConvert(tempFile.getPath());
        VideoCompressor(selectedCompression, checked).execute()
    }

    fun createConvertVideoFolder() {
//        var f = File(Environment.getExternalStorageDirectory(),
//                File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME)
//        f.mkdirs()
        val folder = File(Environment.getExternalStorageDirectory(),
                File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR)
        if (!folder.exists()) folder.mkdirs()
    }


    private fun getWidthAndHeight(path: String): Pair<Int, Int> {
        val retriever = MediaMetadataRetriever()
        try {
            FileInputStream(path).use { `is` ->
                val fd = `is`.fd
                retriever.setDataSource(fd)
            }
        } catch (fileEx: FileNotFoundException) {
            fileEx.printStackTrace()
            throw IllegalArgumentException()
        } catch (ioEx: IOException) {
            throw IllegalArgumentException()
        }


        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)

        retriever.release()
        return Integer.valueOf(width) to Integer.valueOf(height)

    }


    internal inner class VideoCompressor(val selectedCompression: Int, val mute: Boolean) : AsyncTask<Void, Void, Boolean>() {


        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
            Log.d(TAG, "Start video compression")
        }

        override fun doInBackground(vararg voids: Void): Boolean? {
            val random = Random()
            val destFile = File(Environment.getExternalStorageDirectory(),
                    File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME
                            + Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + File.separator
                            + random.nextInt(10000) + ".mp4")

            val videoInfo = VideoInfo(tempFile.path, destFile.path, selectedCompression, mute)
            return MediaController.getInstance().convertVideo(videoInfo)
        }

        override fun onPostExecute(compressed: Boolean?) {
            super.onPostExecute(compressed)
            progressBar.visibility = View.GONE
            if (compressed!!) {
                btnCompressVideo.isEnabled = true
                textView.text = "Compression successfully!"
                Log.d(TAG, "Compression successfully!")
            }
        }
    }

    @SuppressLint("NewApi")
    fun getFilePath(context: Context, uri: Uri): String? {
        var mUri = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.applicationContext, mUri)) {
            if (isExternalStorageDocument(mUri)) {
                val docId = DocumentsContract.getDocumentId(mUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(mUri)) {
                val id = DocumentsContract.getDocumentId(mUri)
                mUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
            } else if (isMediaDocument(mUri)) {
                val docId = DocumentsContract.getDocumentId(mUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    mUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    mUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    mUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
        if ("content".equals(mUri.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver
                        .query(mUri, projection, selection, selectionArgs, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
            } finally {
                cursor?.close()
            }

        } else if ("file".equals(mUri.scheme, ignoreCase = true)) {
            return mUri.path
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

}
