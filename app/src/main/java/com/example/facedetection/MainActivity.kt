package com.example.facedetection

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btn=findViewById<Button>(R.id.btnCamera)

        btn.setOnClickListener {
            val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            //if camera opens then all works fine
            if(intent.resolveActivity(packageManager)!=null){
                 //take all info from camera and return to main activity then we use startactivityforresult
                 startActivityForResult(intent,123)
            }
            //else something is wrong
            else{
                Toast.makeText(this,"Something is wrong",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //taking the image in the form of bitmap
        if(requestCode==123 && resultCode== RESULT_OK){
            val extras=data?.extras
            val bitmap=extras?.get("data")as? Bitmap
            if(bitmap!=null) {
                detectFace(bitmap)
            }
        }
    }

    fun detectFace(bitmap:Bitmap){
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()


        val detector= FaceDetection.getClient(highAccuracyOpts)

        val image = InputImage.fromBitmap(bitmap, 0)


        //now the image from camera is being processed by machine learning
        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully ,our face is detected sucessfully

                var resultText=" "
                var i=1;
                for(face in faces){
                    resultText="Face Number : $i"+
                            "\nSmile : ${face.smilingProbability?.times(100)}%"+
                            "\nLeft Eye Open : ${face.leftEyeOpenProbability?.times(100)}%"+
                            "\nRight Eye Open : ${face.rightEyeOpenProbability?.times(100)}%"

                    i++
                }

                if(faces.isEmpty()){
                    Toast.makeText(this,"No face Detected",Toast.LENGTH_SHORT).show()
                }
                //else show the result
                else{
                    Toast.makeText(this,resultText,Toast.LENGTH_LONG).show()
                }

            }
            .addOnFailureListener { e ->
                // Task failed with an exception ,face detection is failed

            }
    }
}