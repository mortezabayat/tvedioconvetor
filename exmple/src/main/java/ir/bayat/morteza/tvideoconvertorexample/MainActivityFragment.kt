package ir.bayat.morteza.tvideoconvertorexample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*

class MainActivityFragment : Fragment() {


    private val requestCode = 1
    private val MIME_TYPE_VIDEO = "video/*"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fab.setOnClickListener {
            addVideo()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode != this.requestCode || resultCode != Activity.RESULT_OK
                || data == null) return

        val path = data.data.path
    }


    private fun addVideo() {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode)
            return
        }

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = MIME_TYPE_VIDEO
        startActivityForResult(Intent.createChooser(intent, "Pick video from Gallery"),
                requestCode)
    }

}
