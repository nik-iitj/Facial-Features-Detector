package com.example.faceyourface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DetectedAdapter extends RecyclerView.Adapter<DetectedAdapter.ViewHolder> {


    Context context;
    List<DetectedModel> detectedList;

    public DetectedAdapter(Context context, List<DetectedModel>detectedList) {
        this.context = context;
        this.detectedList=detectedList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detected_layout,parent,false);




        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        if(detectedList!=null && detectedList.size()>0){

            DetectedModel model = detectedList.get(position);
            holder.imgId.setText(model.getId());
            holder.smileId.setText(model.getSmilePercent());
            holder.leftEyeId.setText(model.getLeftEyePercent());
            holder.rightEyeId.setText(model.getRightEyePercent());

        }



    }

    @Override
    public int getItemCount() {
        return detectedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView imgId,smileId,leftEyeId,rightEyeId;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imgId = itemView.findViewById(R.id.imgId);
            smileId=itemView.findViewById(R.id.smileId);
            leftEyeId=itemView.findViewById(R.id.leftEyeId);
            rightEyeId=itemView.findViewById(R.id.rightEyeId);



        }
    }
}
