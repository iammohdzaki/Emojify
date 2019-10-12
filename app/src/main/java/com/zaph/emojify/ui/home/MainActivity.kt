package com.zaph.emojify.ui.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.zaph.emojify.R
import com.zaph.emojify.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zaph.emojify.utils.REQUEST_STORAGE_PERMISSION
import android.provider.MediaStore
import android.content.Intent
import java.io.File
import com.zaph.emojify.utils.BitmapUtils
import java.io.IOException
import androidx.core.content.FileProvider
import com.zaph.emojify.ui.emojify.Emojifier
import com.zaph.emojify.utils.FILE_PROVIDER_AUTHORITY
import com.zaph.emojify.utils.REQUEST_IMAGE_CAPTURE


class MainActivity : BaseActivity() , View.OnClickListener{

    private var mTempPhotoPath: String? = null
    private var mResultsBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init(){
        setOnClickListeners(this,fbLetsEmojify,fbSaveEmojify,fbShareEmojify,ivClearImage)
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.fbLetsEmojify -> {
                emojifyMe()
            }
            R.id.fbSaveEmojify -> {
                // Delete the temporary image file
                BitmapUtils.deleteImageFile(this, mTempPhotoPath!!)

                // Save the image
                BitmapUtils.saveImage(this, mResultsBitmap!!)
            }
            R.id.fbShareEmojify -> {
                // Delete the temporary image file
                BitmapUtils.deleteImageFile(this, mTempPhotoPath!!)

                // Save the image
                BitmapUtils.saveImage(this, mResultsBitmap!!)

                // Share the image
                BitmapUtils.shareImage(this, mTempPhotoPath!!)
            }
            R.id.ivClearImage -> {
                clearImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_IMAGE_CAPTURE -> {
                if(resultCode == Activity.RESULT_OK){
                    processAndSetImage()
                }else{
                    BitmapUtils.deleteImageFile(this,mTempPhotoPath!!)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchCamera()
                } else {
                    // If you do not get permission, show a Toast
                    showToast("Permission Denied")
                }
            }

        }
    }

    /**
     * Checks For Permissions and Launches the camera app.
     */
    private fun emojifyMe(){
        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION)
        } else {
            // Launch the camera if the permission exists
            launchCamera()
        }
    }

    /**
     * Creates a temporary image file and captures a picture to store in it.
     */
    private fun launchCamera(){
        // Create the capture image intent
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if(takePictureIntent.resolveActivity(packageManager) != null){
            // Create the temporary File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = BitmapUtils.createTempImageFile(this)
            } catch (ex: IOException) {
                // Error occurred while creating the File
                ex.printStackTrace()
            }

            // Continue only if the File was successfully created
            if(photoFile != null){
                // Get the path of the temporary file
                mTempPhotoPath = photoFile.absolutePath

                // Get the content URI for the image file
                val photoURI = FileProvider.getUriForFile(
                    this,
                    FILE_PROVIDER_AUTHORITY,
                    photoFile
                )

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Method for processing the captured image and setting it to the TextView.
     */
    private fun processAndSetImage(){
        animation_view.visibility=View.GONE
        cvParent.visibility = View.VISIBLE
        fbShareEmojify.visibility=View.VISIBLE
        fbSaveEmojify.visibility = View.VISIBLE


        // Resample the saved image to fit the ImageView
        mResultsBitmap = BitmapUtils.resamplePic(this,mTempPhotoPath!!)

        Emojifier.detectFaces(this,mResultsBitmap!!)
        // Set the new bitmap to the ImageView
        ivEmojify.setImageBitmap(mResultsBitmap)
    }

    /**
     *  Resets the app to original state.
     */
    private fun clearImage(){
        // Clear the image and toggle the view visibility
        ivEmojify.setImageResource(0)
        animation_view.visibility=View.VISIBLE
        cvParent.visibility = View.GONE
        fbShareEmojify.visibility=View.GONE
        fbSaveEmojify.visibility = View.GONE

        // Delete the temporary image file
        BitmapUtils.deleteImageFile(this, mTempPhotoPath!!)
    }


}
