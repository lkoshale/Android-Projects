package com.machadalo.audit.extras;

/**
 * Created by Asna Euphoria on 23-01-2016.
 */
public class GalleryData {
    private String filePath, fileName;
    String url;
    public GalleryData() {
    }




    public GalleryData(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.url = filePath+fileName;

    }

    public String getfilePath() {
        return filePath;
    }

    public void setfilePath(String name) {
        this.filePath = name;
    }


    public String getfileName() {
        return fileName;
    }

    public void setfileName(String fileName) {
        this.fileName = fileName;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
