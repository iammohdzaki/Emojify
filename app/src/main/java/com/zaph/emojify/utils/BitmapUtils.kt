package com.zaph.emojify.utils

import android.content.Context
import android.graphics.Bitmap
import android.view.WindowManager
import android.util.DisplayMetrics
import android.graphics.BitmapFactory
import java.io.File
import kotlin.math.min
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import java.io.FileOutputStream
import androidx.core.content.FileProvider


/**
 * Developer : Mohammad Zaki
 * Created On : 12-10-2019
 */

object BitmapUtils {



    /**
     * Resamples the captured photo to fit the screen for better memory usage.
     *
     * @param context   The application context.
     * @param imagePath The path of the photo to be resampled.
     * @return The resampled bitmap
     */
    fun resamplePic(context: Context, imagePath: String): Bitmap {

        // Get device screen size information
        val metrics = DisplayMetrics()
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.defaultDisplay.getMetrics(metrics)

        val targetH = metrics.heightPixels
        val targetW = metrics.widthPixels

        // Get the dimensions of the original bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = min(photoW / targetW, photoH / targetH)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(imagePath);
    }

    /**
     * Creates the temporary image file in the cache directory.
     *
     * @return The temporary image file.
     */
    fun createTempImageFile(context: Context): File {

        val timeStamp = DateUtils.getFormattedCurrentDate(DATE_FORMAT)
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.externalCacheDir

        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",   /* suffix */
            storageDir      /* directory */
        )
    }

    /**
     * Deletes image file for a given path.
     *
     * @param context   The application context.
     * @param imagePath The path of the photo to be deleted.
     */
    fun deleteImageFile(context: Context, imagePath: String): Boolean {
        // Get the file
        val imageFile = File(imagePath)

        // Delete the image
        val deleted = imageFile.delete()

        // If there is an error deleting the file, show a Toast
        if (!deleted) {
            val errorMessage = context.getString(com.zaph.emojify.R.string.error_delete)
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        return deleted
    }

    /**
     * Helper method for adding the photo to the system photo gallery so it can be accessed
     * from other apps.
     *
     * @param imagePath The path of the saved image
     */
    private fun galleryAddPic(context: Context, imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    /**
     * Helper method for saving the image.
     *
     * @param context The application context.
     * @param image   The image to be saved.
     * @return The path of the saved image.
     */
    fun saveImage(context: Context, image: Bitmap): String? {

        var savedImagePath: String? = null

        // Create the new file in the external storage
        val timeStamp = DateUtils.getFormattedCurrentDate(DATE_FORMAT)
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir = File(
            "${getExternalStoragePublicDirectory(DIRECTORY_PICTURES)}/Emojify"
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }

        // Save the new Bitmap
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.getAbsolutePath()
            try {
                val fOut = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Add the image to the system gallery
            galleryAddPic(context, savedImagePath)

            // Show a Toast with the save location
            val savedMessage = context.getString(com.zaph.emojify.R.string.saved_message)
            Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
        }

        return savedImagePath
    }

    /**
     * Helper method for sharing an image.
     *
     * @param context   The image context.
     * @param imagePath The path of the image to be shared.
     */
    fun shareImage(context: Context, imagePath: String) {
        // Create the share intent and start the share activity
        val imageFile = File(imagePath)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        val photoURI = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, imageFile)
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI)
        context.startActivity(shareIntent)
    }
}