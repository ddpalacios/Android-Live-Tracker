package com.example.cta_map;

public class ListItem {

    public String getTrainLine() {
        return TrainLine;
    }

    public void setTrainLine(String trainLine) {
        TrainLine = trainLine;
    }

    private String TrainLine;
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    private int image;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    private String subtitle;

    public String getTrain_dir() {
        return train_dir;
    }

    public void setTrain_dir(String train_dir) {
        this.train_dir = train_dir;
    }

    public String getTrain_dir_label() {
        return train_dir_label;
    }

    public void setTrain_dir_label(String train_dir_label) {
        this.train_dir_label = train_dir_label;
    }

    private String train_dir_label;
    private String train_dir;
    private String title;

    public String getMapID() {
        return MapID;
    }

    public void setMapID(String mapID) {
        MapID = mapID;
    }

    private String MapID;

    public String getDirection_id() {
        return direction_id;
    }

    public void setDirection_id(String direction_id) {
        this.direction_id = direction_id;
    }

    private String direction_id;
}
