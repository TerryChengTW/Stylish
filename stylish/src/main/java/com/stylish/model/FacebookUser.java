package com.stylish.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookUser {
    private String id;
    private String name;
    private String email;

    @JsonProperty("picture")
    private Picture pictureObj;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Picture getPictureObj() {
        return pictureObj;
    }

    public void setPictureObj(Picture pictureObj) {
        this.pictureObj = pictureObj;
    }

    public String getPictureUrl() {
        return pictureObj != null && pictureObj.getData() != null ? pictureObj.getData().getUrl() : null;
    }

    public static class Picture {
        private PictureData data;

        public PictureData getData() {
            return data;
        }

        public void setData(PictureData data) {
            this.data = data;
        }
    }

    public static class PictureData {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}