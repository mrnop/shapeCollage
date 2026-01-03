package com.isarainc.frame.size;

public class Size {
	
	
	private String name;
    private boolean scale;
	private int width;
	private int height;
	
	public Size(String name,boolean scale, int width, int height) {
		super();
        this.scale = scale;
		this.name = name;
		this.width = width;
		this.height = height;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    public boolean isScale() {
        return scale;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }

    public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
}
