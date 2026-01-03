package com.isarainc.arts;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;


public class ShapeCollage extends Collage {

    private static final String TAG = "ShapeCollage";
    @SerializedName("shape")
    JsonElement shape;

    @SerializedName("grid")
    boolean grid;

    @SerializedName("gridSize")
    int gridSize;

    @SerializedName("collageSize")
    int collageSize;

    @SerializedName("borderColor")
    String borderColor;

    @SerializedName("borderSize")
    int borderSize;

    @SerializedName("square")
    boolean square = true;

    transient Shape shapeData;

    public JsonElement getShape() {
        return shape;
    }

    public void setShape(JsonElement shape) {
        this.shape = shape;
    }

    public boolean isGrid() {
        return grid;
    }

    public void setGrid(boolean grid) {
        this.grid = grid;
    }

    public boolean isSquare() {
        return square;
    }

    public void setSquare(boolean square) {
        this.square = square;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public int getCollageSize() {
        return collageSize;
    }

    public void setCollageSize(int collageSize) {
        this.collageSize = collageSize;
    }

    public Shape getShapeData() {
        Log.d(TAG, "shape=" + shape);
        if (shapeData == null) {
            if (shape != null) {
                Shape sh = Art.gson.fromJson(shape, Shape.class);

                try {
                    Log.d(TAG, "class=" + getClass().getCanonicalName() + "$" + sh.shapeType);
                    shapeData = (Shape) Art.gson.fromJson(shape,
                            Class.forName(getClass().getCanonicalName() + "$" + sh.shapeType));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return shapeData;
    }

    public void setShapeData(Shape shapeData) {
        this.shapeData = shapeData;
        shape = Art.gson.toJsonTree(shapeData);
    }


    public static class Shape {
        @SerializedName("shapeType")
        String shapeType;

        public String getShapeType() {
            return shapeType;
        }

        public void setShapeType(String shapeType) {
            this.shapeType = shapeType;
        }

    }

    public static class Bundle extends Shape {

        @SerializedName("folder")
        String folder;

        @SerializedName("path")
        String path;

        public String getShapeType() {
            return "Bundle";
        }


        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class Image extends Shape {

        @SerializedName("path")
        String path;

        public String getShapeType() {
            return "Image";
        }


        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class Text extends Shape {

        @SerializedName("text")
        String text;

        @SerializedName("font")
        String font;

        public String getShapeType() {
            return "text";
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
}
