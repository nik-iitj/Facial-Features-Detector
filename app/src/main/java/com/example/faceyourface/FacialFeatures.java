package com.example.faceyourface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class FacialFeatures extends AppCompatActivity {
    Bitmap bitmap;
    ImageView imageView;
    InputImage image;
    ProgressBar progressBar;

    DetectedAdapter adapter;

    RecyclerView recyclerView;

    List<DetectedModel> detectedList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_features);
        byte[] byteArray = getIntent().getByteArrayExtra("bytes");
        Bundle data = getIntent().getExtras();

        int height = data.getInt("height");
        int width = data.getInt("width");
        String h = Integer.toString(height);
        String w = Integer.toString(width);


        Bitmap k = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        bitmap = Bitmap.createScaledBitmap(k,width,height,false);

        imageView = findViewById(R.id.photo);


        image=InputImage.fromBitmap(bitmap,0);

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);

        detectedList = new ArrayList<>();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));






        executeClassification();





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
                    AlertDialog.Builder builder = new AlertDialog.Builder(FacialFeatures.this);


                    progressBar.setVisibility(View.GONE);
                    builder
                            .setTitle("No Image Found :(")
                            .setMessage("Please select the options of your Wish")


                            .setPositiveButton("Go to homepage ", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                }
                            })


                            .setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    finish();

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();




                }
                else{

                    String smile="",lefteye="",righteye="",id="";


                    progressBar.setVisibility(View.VISIBLE);

                    for(Face face : faces){

                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.BLUE);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(5);

                        Rect bounds = face.getBoundingBox();

                        canvas.drawRect(bounds.left,bounds.top,bounds.right,bounds.bottom,paint);

                        //imageView.setImageBitmap(bitmap);

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



                        imageView.setImageBitmap(bitmap);


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


                }




            }
        });



    }
}