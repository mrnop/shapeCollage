package com.isarainc.arts;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Save {
    @SerializedName("name")
    String name;

    transient String path;

    @SerializedName("art")
    String art;

    @SerializedName("author")
    String author;

    @SerializedName("created")
    Date created;

    @SerializedName("updated")
    Date updated;

    @SerializedName("ready")
    Boolean ready;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Boolean getReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    @Override
    public String toString() {
        return "Save{" +
                "name='" + name + '\'' +
                ", art='" + art + '\'' +
                ", author='" + author + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}
