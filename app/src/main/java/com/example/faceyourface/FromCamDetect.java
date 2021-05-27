package com.example.faceyourface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class FromCamDetect extends AppCompatActivity {
    CameraKitView cameraKitView;
    ImageView capImage,flipCam;
    boolean isFront;

    Animation forward,backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_cam_detect);

        isFront=false;
        cameraKitView = findViewById(R.id.camera);
        capImage  = findViewById(R.id.capImage);
        flipCam = findViewById(R.id.flipCamera);

        forward = AnimationUtils.loadAnimation(this,R.anim.cam_rotate_forward);
        backward=AnimationUtils.loadAnimation(this,R.anim.cam_rotate_backward);

        capImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                capImage.startAnimation(forward);
                //capImage.startAnimation(backward);

                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, byte[] bytes) {
                        //cameraKitView.onStop();

//                        Bitmap k = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        bitmap = Bitmap.createScaledBitmap(k, cameraKitView.getWidth(), cameraKitView.getHeight(), false);
                        Intent intent = new Intent(getApplicationContext(),FacialFeatures.class);
                        intent.putExtra("bytes",bytes);
                        intent.putExtra("width",cameraKitView.getWidth());
                        intent.putExtra("height",cameraKitView.getHeight());
                        startActivity(intent);
                        //image = InputImage.fromBitmap(bitmap,0);
                        //executeClassification();


                    }
                });
            }
        });

        flipCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCam.startAnimation(forward);
                if(isFront){
                    cameraKitView.setFacing(CameraKit.FACING_BACK);
                    isFront=false;
                } else{
                    cameraKitView.setFacing(CameraKit.FACING_FRONT);
                    isFront = true;
                }
            }
        });










    }



//    public void executeClassification(){
//
//        FaceDetectorOptions highAccuracyOpts =
//                new FaceDetectorOptions.Builder()
//                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
//                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//                        .build();
//
//
//        com.google.mlkit.vision.face.FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
//
//        Task<List<Face>> result=detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
//            @Override
//            public void onSuccess(@NonNull @NotNull List<Face> faces) {
//
//                for(Face face : faces){
//
//                    Rect bounds = face.getBoundingBox();
//                    float rotY= face.getHeadEulerAngleY();
//                    float rotZ= face.getHeadEulerAngleZ();
//                    FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
//
//                    if(leftEar!=null){
//                        PointF leftEarPosition = leftEar.getPosition();
//                        Toast.makeText(FromCamDetect.this,"found" , Toast.LENGTH_SHORT).show();
//                    }
//
//
//                }
//
//
//            }
//        });
//
//
//
//    }







    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}