package ds.project.tadaktadakfront

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_navi_home.*
import kotlinx.android.synthetic.main.fragment_navi_home.view.*
import java.io.File
import java.io.IOException
import java.util.*


class NaviHome : Fragment() {
    var selectedImage: Uri? = null
    var selectedBitmap: Bitmap? = null
    val REQUEST_IMAGE_CAPTURE = 2
    lateinit var currentPhotoPath: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        return inflater.inflate(R.layout.fragment_navi_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        btnGallery.setOnClickListener{
            select_imge(it)
            Log.d(TAG,"Log---------1")
        }

        val requestCameraThumbnailLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult() ) {
            val bitmap = it?.data?.extras?.get("data") as Bitmap
            select_ImageView.setImageBitmap(bitmap)

        }

        btnCamera.setOnClickListener{
            capture_imge(it)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            requestCameraThumbnailLauncher.launch(intent)
            Log.d(TAG,"Log---------2")
        }

        btnselect.setOnClickListener {
            activity?.let{
                if(currentPhotoPath!=null) {
                    val intent = Intent(context, ImgActivity::class.java)
                    intent.putExtra("path", currentPhotoPath);
                    startActivity(intent)
                }
            }
        }



    }
    fun select_imge(view: View){
        activity?.let{
        if(ContextCompat.checkSelfPermission(it.applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
          requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        } else {
            val Intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(Intent, 2)
            }
        }
        Log.d(TAG,"Log---------3")
    }

    fun capture_imge(view: View){
        activity?.let{
            if(ContextCompat.checkSelfPermission(it.applicationContext, android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),1)
            }
        }
        Log.d(TAG,"Log---------4")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode ==1){
            if(grantResults.size > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d(TAG,"Log---------5")
    }

    private fun dispatchTakePictureIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "ds.project.tadaktadakfront.fileprovider",
                        photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
        Log.d(TAG,"Log---------6")
    }

    @Throws(IOException::class)
    private fun createImageFile() : File{
        val timeStamp : String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply{
            currentPhotoPath = absolutePath
        }
        Log.d(TAG,"Log---------7")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            lateinit var exif: ExifInterface

            try {
                exif = ExifInterface(currentPhotoPath)
                var exifOrientation = 0
                var exifDegree = 0

                if (exif != null) {
                    exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    exifDegree = exifOrientationToDegress(exifOrientation)
                }

                select_ImageView.setImageBitmap(rotate(bitmap, exifDegree))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG,"Log---------8")
    }

    private fun exifOrientationToDegress(exifOrientation: Int): Int {
        when(exifOrientation){
            ExifInterface.ORIENTATION_ROTATE_90 ->{
                Log.d("rotate","rotate90")
                return 90
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                Log.d("rotate","rotate180")
                return 180
            }
            ExifInterface.ORIENTATION_ROTATE_270 ->{
                Log.d("rotate","rotate270")
                return 270
            }
            else -> {
                Log.d("rotate","rotate0")
                return 0
            }

        }
        Log.d(TAG,"Log---------9")
    }

    private fun rotate(bitmap: Bitmap, degree: Int) : Bitmap {
        Log.d("rotate","init rotate")
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix,true)
        Log.d(TAG,"Log---------10")
    }

}

