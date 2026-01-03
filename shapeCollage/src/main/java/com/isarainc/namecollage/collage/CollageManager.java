package com.isarainc.namecollage.collage;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class CollageManager {
	private List<String> shapesFromAssets = new LinkedList<String>();
	private static CollageManager instance;
	private AssetManager assetManager;
	private Context context;

	public static CollageManager getInstance(Context context) {
		if (instance == null) {
			instance = new CollageManager(context);
		}

		return instance;
	}

	private CollageManager(Context context) {
		super();
		this.context = context;
		assetManager = context.getAssets();

	}


	public String bitmapThumbToFile(File file, Bitmap orgin) {
		Bitmap saveBitmap = Bitmap.createScaledBitmap(orgin,200,200*orgin.getHeight()/orgin.getWidth(), false);

		OutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			return null;
		}
		return file.getAbsolutePath();

	}

	
	public Bitmap FlowerTempImage(String fileName) {
		try {
	
			Bitmap bitmap = BitmapFactory.decodeFile(fileName);
			return bitmap;
		} catch (OutOfMemoryError ome) {
			System.gc();
			try {
				Bitmap bitmap = BitmapFactory.decodeFile(fileName);
				return bitmap;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;

	}
	public Bitmap loadFlowerImage(String fileName) {
		AssetManager assetManager = context.getAssets();
		InputStream istr;
		try {
			istr = assetManager.open("collage/" + fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(istr);
			return bitmap;
		} catch (OutOfMemoryError ome) {
			System.gc();
			try {
				istr = assetManager.open("collage/" + fileName);
				Bitmap bitmap = BitmapFactory.decodeStream(istr);
				
				return bitmap;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;

	}

	public List<String> listFlowers() {
		if (shapesFromAssets.isEmpty()) {
			try {
				String list[] = context.getAssets().list("collage");
				if (list != null)
					for (int i = 0; i < list.length; i++) {
						if(list[i].endsWith(".png")){
							shapesFromAssets.add(list[i]);
						}
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return shapesFromAssets;
	}


}
