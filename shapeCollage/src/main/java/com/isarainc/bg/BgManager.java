package com.isarainc.bg;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.isarainc.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class BgManager {
	private List<String> shapesFromAssets = new LinkedList<String>();
	private static BgManager instance;

	private Context context;

	public static BgManager getInstance(Context context) {
		if (instance == null) {
			instance = new BgManager(context);
		}

		return instance;
	}

	private BgManager(Context context) {
		super();
		this.context = context;


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

	
	public Bitmap BgTempImage(String fileName) {
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
	
	public Bitmap loadThumbImage(String fileName) {
		Drawable drawable;
		try {
			drawable = Drawable.createFromResourceStream(context.getResources(),new TypedValue(), context.getResources().getAssets().open("backgrounds/" + fileName), null);
			Bitmap thumb=Utils.tileBitmap((BitmapDrawable) drawable, 300, 300);
			
			return thumb;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	public Bitmap loadBgImage(String fileName) {
		AssetManager assetManager = context.getAssets();
		InputStream istr;
		try {
			istr = assetManager.open("backgrounds/" + fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(istr);
			return bitmap;
		} catch (OutOfMemoryError ome) {
			System.gc();
			try {
				istr = assetManager.open("backgrounds/" + fileName);
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

	public List<String> listBgs() {
		if (shapesFromAssets.isEmpty()) {
			try {
				String list[] = context.getAssets().list("backgrounds");
				if (list != null)
					for (int i = 0; i < list.length; i++) {
						if(list[i].endsWith(".png") || list[i].endsWith(".jpg")){
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
