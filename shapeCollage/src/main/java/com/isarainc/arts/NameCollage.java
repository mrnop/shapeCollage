package com.isarainc.arts;

import com.google.gson.annotations.SerializedName;

public class NameCollage extends Collage{


    @SerializedName("gridSize")
    int gridSize;

    @SerializedName("collageSize")
    int collageSize;

    @SerializedName("text")
    String text;

    @SerializedName("font")
    String font;


    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public int getCollageSize() {
        return collageSize;
    }

    public void setCollageSize(int collageSize) {
        this.collageSize = collageSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
