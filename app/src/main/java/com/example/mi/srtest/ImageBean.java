package com.example.mi.srtest;

import java.io.File;

public class ImageBean {

    File path;
    boolean isCheck;

    public ImageBean(File path, boolean isCheck) {
        this.path = path;
        this.isCheck = isCheck;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
