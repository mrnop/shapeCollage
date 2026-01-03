package com.isarainc.arts;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;


public class Collage extends Art.Value {
    public transient static final String TYPE_PICTURE="picture";
    public transient static final String TYPE_MULTIPLE_PICTURE="multiple_picture";


    @SerializedName("blackground")
    String blackground;


    public String getBlackground() {
        return blackground;
    }

    public void setBlackground(String blackground) {
        this.blackground = blackground;
    }


    @Override
    public void init() {

    }

    /*
        @SerializedName("layers")
        List<JsonElement> layers = new LinkedList<>();

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public List<JsonElement> getLayers() {
            return layers;
        }

        public void setLayers(List<JsonElement> layers) {
            this.layers = layers;
        }

        transient private List<LayerModel> mLayers = new LinkedList<>();

        public void addLayer(LayerModel layer) {
            mLayers.add(layer);
        }

        public List<LayerModel> getInitLayers() {
            return mLayers;
        }

        @Override
        public void init() {
            if (mLayers.size() != layers.size()) {
                for (JsonElement jsonElement : layers) {
                    LayerModel l = Art.gson.fromJson(jsonElement, LayerModel.class);
                    LayerModel layer = null;
                    try {
                        layer = (LayerModel) Art.gson.fromJson(jsonElement,
                                Class.forName( getClass().getCanonicalName() + "$" +  l.getType()));
                        addLayer(layer);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        */
    public static class LayerModel {
        @SerializedName("type")
        String type;

        @SerializedName("x")
        int x;

        @SerializedName("y")
        int y;


        @SerializedName("angle")
        float angle;

        @SerializedName("scale")
        float scale=1.0f;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }


        public float getAngle() {
            return angle;
        }

        public void setAngle(float angle) {
            this.angle = angle;
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        @Override
        public String toString() {
            return "LayerModel{" +
                    ", type='" + type + '\'' +
                    ", x=" + x +
                    ", y=" + y +
                    ", angle=" + angle +
                    ", scale=" + scale +
                    '}';
        }
    }

    public static class TextStyle{
        @SerializedName("style")
        String style;

        @SerializedName("font")
        String font;

        @SerializedName("colors")
        List<String> colors = new LinkedList<>();

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getFont() {
            return font;
        }

        public void setFont(String font) {
            this.font = font;
        }

        public List<String> getColors() {
            return colors;
        }

        public void setColors(List<String> colors) {
            this.colors = colors;
        }
    }

    public static class Sticker extends LayerModel {
        @SerializedName("stickerType")
        String stickerType;

        @SerializedName("folder")
        String folder;

        @SerializedName("path")
        String path;

        public String getStickerType() {
            return stickerType;
        }

        public void setStickerType(String stickerType) {
            this.stickerType = stickerType;
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

        @Override
        public String toString() {
            return "Sticker{" +
                    "stickerType='" + stickerType + '\'' +
                    ", folder='" + folder + '\'' +
                    ", path='" + path + '\'' +
                    "} " + super.toString();
        }
    }


}
