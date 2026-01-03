package com.isarainc.arts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class Art {
    public static final String TAG = "Art";

    public static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @SerializedName("version")
    String version;

    @SerializedName("type")
    String type;

    @SerializedName("name")
    String name;

    transient String path;

    @SerializedName("description")
    String description;

    @SerializedName("icon")
    String icon;

    @SerializedName("init_value")
    JsonElement initValue;

    transient Value value;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public JsonElement getInitValue() {
        return initValue;
    }

    public void setInitValue(JsonElement initValue) {
        this.initValue = initValue;
    }

    public Value getValue() {
        if(value==null){
            if(initValue!=null){
                try {
                    value = (Value) gson.fromJson(initValue,
                            Class.forName( getClass().getPackage().getName() + "." + type));
                    value.init();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
        initValue = gson.toJsonTree(value);
    }

    public static abstract class Value {

        @SerializedName("width")
        private Integer width;

        @SerializedName("height")
        private Integer height;

        @SerializedName("inputType")
        String inputType ;

        @SerializedName("crop")
        boolean crop ;

        public String getInputType() {
            return inputType;
        }

        public void setInputType(String inputType) {
            this.inputType = inputType;
        }

        public boolean isCrop() {
            return crop;
        }

        public void setCrop(boolean crop) {
            this.crop = crop;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public abstract void init();
    }


    public static class Point {
        @SerializedName("x")
        int x;
        @SerializedName("y")
        int y;

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

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

}
