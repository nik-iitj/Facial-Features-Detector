package com.example.faceyourface;

public class DetectedModel {

    String Id;
    String smilePercent;



    String rightEyePercent;
    String leftEyePercent;

    public DetectedModel(String id, String smilePercent, String rightEyePercent, String leftEyePercent) {
        this.Id = id;
        this.smilePercent = smilePercent;
        this.rightEyePercent = rightEyePercent;
        this.leftEyePercent = leftEyePercent;
    }

    public String getId() {
        return Id;
    }

    public String getSmilePercent() {
        return smilePercent;
    }

    public String getRightEyePercent() {
        return rightEyePercent;
    }

    public String getLeftEyePercent() {
        return leftEyePercent;
    }


}
