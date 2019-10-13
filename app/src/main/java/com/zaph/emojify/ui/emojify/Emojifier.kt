package com.zaph.emojify.ui.emojify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.Frame
import android.widget.Toast
import com.google.android.gms.vision.face.Face
import com.zaph.emojify.R
import com.zaph.emojify.ui.emojify.Emojifier.Emoji.*
import android.graphics.Canvas


/**
 * Developer : Mohammad Zaki
 * Created On : 12-10-2019
 */

object Emojifier {

    private const val SMILING_PROB_THRESHOLD = .15
    private const val EYE_OPEN_PROB_THRESHOLD = .5
    private const val EMOJI_SCALE_FACTOR = .9f


    /**
     * Method for detecting faces in a bitmap, and drawing emoji depending on the facial
     * expression.
     *
     * @param context The application context.
     * @param bitmap The picture in which to detect the faces.
     */
    fun detectFacesAndOverlayEmoji(context: Context, bitmap: Bitmap): Bitmap {

        // Create the face detector, disable tracking and enable classifications
        val detector = FaceDetector.Builder(context)
            .setTrackingEnabled(false)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .build()

        // Build the frame
        val frame = Frame.Builder().setBitmap(bitmap).build()

        // Detect the faces
        val faces = detector.detect(frame)

        // Initialize result bitmap to original picture
        var resultBitmap = bitmap

        // If there are no faces detected, show a Toast message
        if (faces.size() <= 0) {
            Toast.makeText(context, "No Faces Detected", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Faces:${faces.size()}", Toast.LENGTH_LONG).show()
            for (i in 0.until(faces.size())) {
                val face = faces.valueAt(i)

                var emojiBitmap: Bitmap?
                when (whichEmoji(face)) {
                    SMILE -> {
                        emojiBitmap =
                            BitmapFactory.decodeResource(context.resources, R.drawable.smile)
                    }
                    FROWN -> {
                        emojiBitmap =
                            BitmapFactory.decodeResource(context.resources, R.drawable.frown)
                    }
                    LEFT_WINK -> {
                        emojiBitmap =
                            BitmapFactory.decodeResource(context.resources, R.drawable.leftwink)
                    }
                    RIGHT_WINK -> {
                        emojiBitmap =
                            BitmapFactory.decodeResource(context.resources, R.drawable.rightwink)
                    }
                    LEFT_WINK_FROWN -> {
                        emojiBitmap = BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.leftwinkfrown
                        )
                    }
                    RIGHT_WINK_FROWN -> {
                        emojiBitmap = BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.rightwinkfrown
                        )
                    }
                    CLOSED_EYE_SMILE -> {
                        emojiBitmap =
                            BitmapFactory.decodeResource(context.resources, R.drawable.closed_smile)
                    }
                    CLOSED_EYE_FROWN -> {
                        emojiBitmap =
                            BitmapFactory.decodeResource(context.resources, R.drawable.closed_frown)
                    }
                    else -> {
                        emojiBitmap = null
                        Toast.makeText(context, "No Emoji is Suitable", Toast.LENGTH_SHORT).show();
                    }
                }

                // Add the emojiBitmap to the proper position in the original image
                resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap!!, face)
            }
        }

        detector.release()

        return resultBitmap
    }


    /**
     * Determines the closest emoji to the expression on the face, based on the
     * odds that the person is smiling and has each eye open.
     *
     * @param face The face for which you pick an emoji.
     */
    private fun whichEmoji(face: Face): Emoji {
        val LOG_TAG = "FACE_PROBABILITY"

        Log.d(LOG_TAG, "getClassifications: smilingProb = " + face.isSmilingProbability)
        Log.d(LOG_TAG, "getClassifications: leftEyeOpenProb = " + face.isLeftEyeOpenProbability)
        Log.d(LOG_TAG, "getClassifications: rightEyeOpenProb = " + face.isRightEyeOpenProbability)

        var smiling = face.isSmilingProbability > SMILING_PROB_THRESHOLD
        val leftEyeClosed = face.isLeftEyeOpenProbability < EYE_OPEN_PROB_THRESHOLD
        val rightEyeClosed = face.isRightEyeOpenProbability < EYE_OPEN_PROB_THRESHOLD

        var emoji: Emoji
        if (smiling) {
            emoji = if (leftEyeClosed && !rightEyeClosed) {
                LEFT_WINK
            } else if (rightEyeClosed && !leftEyeClosed) {
                RIGHT_WINK
            } else if (leftEyeClosed) {
                CLOSED_EYE_SMILE
            } else {
                SMILE
            }
        } else {
            emoji = if (leftEyeClosed && !rightEyeClosed) {
                LEFT_WINK_FROWN
            } else if (rightEyeClosed && !leftEyeClosed) {
                RIGHT_WINK_FROWN
            } else if (leftEyeClosed) {
                CLOSED_EYE_FROWN
            } else {
                FROWN
            }
        }

        Log.d(LOG_TAG, "whichEmoji: " + emoji.name)
        return emoji
    }

    private fun addBitmapToFace(backgroundBitmap: Bitmap, emojiBitmap: Bitmap, face: Face): Bitmap {

        // Initialize the results bitmap to be a mutable copy of the original image
        val resultBitmap = Bitmap.createBitmap(
            backgroundBitmap.width,
            backgroundBitmap.height, backgroundBitmap.config
        )

        var emojiScaled=emojiBitmap

        // Scale the emoji so it looks better on the face
        var scaleFactor = EMOJI_SCALE_FACTOR

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        var newEmojiWidth = (face.width * scaleFactor).toInt()
        var newEmojiHeight = (emojiBitmap.height * newEmojiWidth/emojiBitmap.width * scaleFactor).toInt()

        // Scale the emoji
        emojiScaled = Bitmap.createScaledBitmap(emojiBitmap,newEmojiWidth,newEmojiHeight,false)

        // Determine the emoji position so it best lines up with the face
        val emojiPositionX = face.position.x + face.width / 2 - emojiScaled.width / 2
        val emojiPositionY = face.position.y + face.height / 2 - emojiScaled.height / 3

        // Create the canvas and draw the bitmaps to it
        val canvas = Canvas(resultBitmap)
        canvas.drawBitmap(backgroundBitmap,0f,0f,null)
        canvas.drawBitmap(emojiScaled, emojiPositionX, emojiPositionY, null)

        return resultBitmap
    }

    // Enum for all possible Emojis
    private enum class Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }


}