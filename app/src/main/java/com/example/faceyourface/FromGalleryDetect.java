package com.example.faceyourface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FromGalleryDetect extends AppCompatActivity {

    Bitmap bitmap,mutable;
    ImageView imageView;
    InputImage image;
    ProgressBar progressBar;
    Button button,execute;

    DetectedAdapter adapter;

    RecyclerView recyclerView;

    List<DetectedModel> detectedList;
    int degree,flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_gallery_detect);



        String filePath=getIntent().getStringExtra("path");
        File file = new File(filePath);
        bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
        Bitmap k =bitmap.copy(Bitmap.Config.ARGB_8888,true);
        mutable = Bitmap.createScaledBitmap(k,1080,1575,false);


        flag=0;
        degree = 90;
        execute = findViewById(R.id.execute);

        recyclerView = findViewById(R.id.recyclerView);

        detectedList = new ArrayList<>();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        imageView = findViewById(R.id.photo);


        image=InputImage.fromBitmap(mutable,0);

        imageView.setImageBitmap(mutable);

        progressBar = findViewById(R.id.progressBar);
        button=findViewById(R.id.rotate);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matrix matrix=new Matrix();
                matrix.postRotate(degree);
                degree = degree+90;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(mutable, 1080,1575, false);
                mutable = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                image=InputImage.fromBitmap(mutable,0);

                imageView.setImageBitmap(mutable);



            }
        });

        execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==0){
                    executeClassification();

                } else{
                    Toast.makeText(FromGalleryDetect.this, "Face is already classified", Toast.LENGTH_SHORT).show();
                }

            }
        });






    }






    public void executeClassification(){
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE).enableTracking()
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .build();

        com.google.mlkit.vision.face.FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

        Task<List<Face>> result=detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(@NonNull @NotNull List<Face> faces) {

                if(faces.size()==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(FromGalleryDetect.this);


                    progressBar.setVisibility(View.GONE);
                    builder
                            .setTitle("No Face Found :(")
                            .setMessage("Please select the options of your Wish")


                            .setPositiveButton("Try rotating Image ", null
                            )


                            .setNegativeButton("Exit to Home screen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    finish();

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{

                    flag=1;
                    String smile="",lefteye="",righteye="",id="";
                    progressBar.setVisibility(View.VISIBLE);

                    for(Face face : faces){

                        Canvas canvas = new Canvas(mutable);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.BLUE);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(5);

                        Rect bounds = face.getBoundingBox();

                        canvas.drawRect(bounds.left,bounds.top,bounds.right,bounds.bottom,paint);


                        float rotY= face.getHeadEulerAngleY();
                        float rotZ= face.getHeadEulerAngleZ();




                        List<FaceContour>allContour = face.getAllContours();


                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor(Color.RED);

                        for(FaceContour i : allContour){
                            List<PointF>contour = i.getPoints();

                            for(PointF j : contour){

                                canvas.drawCircle(j.x,j.y,7,paint);

                            }
                        }
                        if (face.getTrackingId() != null) {
                            int ID = face.getTrackingId();
                            id= Integer.toString(ID);
                        }
                        paint.setTextSize(50);

                        canvas.drawText("Id " +id,bounds.left,bounds.bottom,paint);



                        imageView.setImageBitmap(mutable);

                        if(face.getSmilingProbability()!=null){
                            float smileProbability = face.getSmilingProbability()*100;
                            smile = Float.toString(smileProbability);
                        }
                        if(face.getRightEyeOpenProbability()!=null){

                            float righteyeopen = face.getRightEyeOpenProbability()*100;
                            righteye= Float.toString(righteyeopen);



                        }
                        if(face.getLeftEyeOpenProbability()!=null){

                            float lefteyeopen = face.getLeftEyeOpenProbability()*100;
                            lefteye= Float.toString(lefteyeopen);

                        }




                        detectedList.add(new DetectedModel(id,smile,lefteye,righteye));




                    }
                    adapter = new DetectedAdapter(getApplicationContext(),detectedList);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);


                }




            }
        });



    }
}