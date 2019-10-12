package com.zaph.emojify.ui.emojify

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.Frame
import android.util.SparseArray
import android.widget.Toast
import com.google.android.gms.vision.face.Face


/**
 * Developer : Mohammad Zaki
 * Created On : 12-10-2019
 */

object Emojifier {

    private val SMILING_PROB_THRESHOLD = .15
    private val EYE_OPEN_PROB_THRESHOLD = .5


    fun detectFaces(context: Context,bitmap: Bitmap){

        val detector = FaceDetector.Builder(context)
            .setTrackingEnabled(false)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .build()

        val frame = Frame.Builder().setBitmap(bitmap).build()
        val faces = detector.detect(frame)

        if (faces.size() <= 0){
            Toast.makeText(context,"No Faces Detected",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context,"Faces:${faces.size()}",Toast.LENGTH_LONG).show()
            for (i in 0.until(faces.size())){
                val face=faces.valueAt(i)
                //Log the classification probabilities for each face.
                whichEmoji(face)
            }
        }

        detector.release()
    }

    private fun whichEmoji(face: Face){
        val LOG_TAG="FACE_PROBABILITY"

        Log.d(LOG_TAG, "getClassifications: smilingProb = " + face.isSmilingProbability)
        Log.d(LOG_TAG, "getClassifications: leftEyeOpenProb = " + face.isLeftEyeOpenProbability)
        Log.d(LOG_TAG, "getClassifications: rightEyeOpenProb = " + face.isRightEyeOpenProbability)

        var smiling=face.isSmilingProbability > SMILING_PROB_THRESHOLD
        val leftEyeClosed = face.isLeftEyeOpenProbability < EYE_OPEN_PROB_THRESHOLD
        val rightEyeClosed = face.isRightEyeOpenProbability < EYE_OPEN_PROB_THRESHOLD

        var emoji:Emoji
        if(smiling){
            emoji = if (leftEyeClosed && !rightEyeClosed){
                Emoji.LEFT_WINK
            }else if(rightEyeClosed && !leftEyeClosed){
                Emoji.RIGHT_WINK
            } else if (leftEyeClosed){
                Emoji.CLOSED_EYE_SMILE
            } else {
                Emoji.SMILE
            }
        } else{
            emoji = if (leftEyeClosed && !rightEyeClosed) {
                Emoji.LEFT_WINK_FROWN
            }  else if(rightEyeClosed && !leftEyeClosed){
                Emoji.RIGHT_WINK_FROWN
            } else if (leftEyeClosed){
                Emoji.CLOSED_EYE_FROWN
            } else {
                Emoji.FROWN
            }
        }

        Log.d(LOG_TAG, "whichEmoji: " + emoji.name)
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