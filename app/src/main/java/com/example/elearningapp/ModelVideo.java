package com.example.elearningapp;

public class ModelVideo {
    String title, videoUrl;
    private boolean completed;
    public ModelVideo() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public ModelVideo(String title, String videoUrl) {
        this.title = title;
        this.videoUrl = videoUrl;
    }
}
