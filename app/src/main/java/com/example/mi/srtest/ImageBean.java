package com.example.mi.srtest;

import java.io.File;

public class ImageBean {

    File path;
    int selected;

    public ImageBean(File path, int selected) {
        this.path = path;
        this.selected = selected;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public int isCheck() {
        return selected;
    }

    public void setCheck(int check) {
        selected = check;
    }
}
