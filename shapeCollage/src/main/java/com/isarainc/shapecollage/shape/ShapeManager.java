package com.isarainc.shapecollage.shape;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.isarainc.shapecollage.DaoMaster;
import com.isarainc.shapecollage.DaoSession;
import com.isarainc.shapecollage.ShapeInfoDao;
import com.isarainc.stickers.MySQLiteOpenHelper;

import com.isarainc.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapeManager {
	private Map<String, List<ShapeInfo>> bundleShapes = new HashMap<String, List<ShapeInfo>>();

	private static ShapeManager instance;
	private Context context;

    private ShapeInfoDao infoDao;

	public static ShapeManager getInstance(Context context) {
		if (instance == null) {
			instance = new ShapeManager(context);
		}

		return instance;
	}

	private ShapeManager(Context context) {
		super();
		this.context = context;
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, "sticker-db", null);
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		infoDao= daoSession.getShapeInfoDao();
		String list[];
		try {
			list = context.getAssets().list("shapes");
			if (list != null) {
				for (int i = 0; i < list.length; i++) {
					bundleShapes.put(list[i], null);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String bitmapThumbToFile(File file, Bitmap orgin) {
		Bitmap saveBitmap = Utils.scaleBitmap(orgin, 200,
				200 * orgin.getHeight() / orgin.getWidth());

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

	public Bitmap loadShapeTempImagex(String fileName) {
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

	public Bitmap loadShapeImage(ShapeInfo info) {
		if (info.getType().equals(ShapeInfo.TYPE_CUSTOM)) {

			File picture = new File(info.getPath());

			if (picture.exists()) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inPreferredConfig = Config.ARGB_8888;

				try {
					Bitmap bitmap = BitmapFactory.decodeFile(
							picture.getAbsolutePath(), options);
					return bitmap;
				} catch (OutOfMemoryError oome) {
					System.gc();
					try {
						Bitmap bitmap = BitmapFactory.decodeFile(
								picture.getAbsolutePath(), options);
						return bitmap;
					} catch (OutOfMemoryError oome2) {
						System.gc();
						options.inSampleSize = 3;
						try {
							Bitmap bitmap = BitmapFactory.decodeFile(
									picture.getAbsolutePath(), options);
							return bitmap;
						} catch (Exception e) {

						}
					}
				}
			}

		} else {
			AssetManager assetManager = context.getAssets();
			InputStream istr;
			try {
				istr = assetManager.open("shapes/" + info.getPath());
				Bitmap bitmap = BitmapFactory.decodeStream(istr);
				return bitmap;
			} catch (OutOfMemoryError ome) {
				System.gc();
				try {
					istr = assetManager.open("shapes/" + info.getPath());
					Bitmap bitmap = BitmapFactory.decodeStream(istr);
					return bitmap;
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		return null;

	}

	public void addCustomShape(ShapeInfo info) {
		List<ShapeInfo> shapes = bundleShapes.get("custom");
		if (shapes == null) {
			shapes = new ArrayList<ShapeInfo>();
			bundleShapes.put("custom", shapes);
		}
		shapes.add(info);
	}

	public void delete(ShapeInfo info) {
		File file = new File(info.getPath());
		file.delete();
		List<ShapeInfo> shapes = bundleShapes.get("custom");
		for (int i = 0; i < shapes.size(); i++) {
			ShapeInfo s = shapes.get(i);
			if (info.getPath().equals(s.getPath())) {
				shapes.remove(i);
				break;
			}
		}

	}

	public List<String> getShapeSets() {
		List<String> list = new ArrayList<String>();
		for (String key : bundleShapes.keySet()) {
			list.add(key);
		}
		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
		});
		return list;
	}

	/**
	 * 
	 * @param folder
	 * @return
	 */
	public List<ShapeInfo> listShapes(String folder) {
		List<ShapeInfo> shapes = bundleShapes.get(folder);
		if (shapes != null && !shapes.isEmpty()) {
			return shapes;
		} else {
			shapes = new ArrayList<ShapeInfo>();
			bundleShapes.put(folder, shapes);
		}
		// Load custom shape
		if ("custom".equals(folder)) {
			File dir = null;
			if (Utils.isSDCARDMounted()) {
				dir = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "Android" + File.separator + "data"
						+ File.separator + context.getPackageName()
						+ File.separator + "shapes" + File.separator + "custom");
			} else {
				dir = new File(Environment.getDataDirectory() + File.separator
						+ "Android" + File.separator + "data" + File.separator
						+ context.getPackageName() + File.separator + "shapes" + File.separator
						+ "custom");
			}
			// Log.d(TAG, dir.getAbsolutePath());
			if (dir.exists()) {
				File slist[] = dir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						return filename.endsWith(".png");
					}

				});
				for (int i = 0; i < slist.length; i++) {
					ShapeInfo info = new ShapeInfo();
					info.setInfo(slist[i].getName());
					info.setFolder(folder);
					info.setType(ShapeInfo.TYPE_CUSTOM);
					info.setPath(slist[i].getAbsolutePath());
					shapes.add(info);
				}
			}

		} else {
			// load assets
			try {
				String list[] = context.getAssets().list("shapes/" + folder);
				if (list != null) {
					for (int i = 0; i < list.length; i++) {
						if (list[i].endsWith(".png")) {
							ShapeInfo info = new ShapeInfo();
							info.setInfo(list[i]);
							info.setFolder(folder);
							info.setType(ShapeInfo.TYPE_BUNDLE);
							info.setPath(folder + "/" + list[i]);
							shapes.add(info);
							// shapes.add(folder + "/" + list[i]);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return shapes;

	}

	public List<ShapeInfo> getRecents() {
		List<ShapeInfo> infos = infoDao.queryBuilder().list();
		Collections.sort(infos, new Comparator<ShapeInfo>() {

			@Override
			public int compare(ShapeInfo lhs, ShapeInfo rhs) {
				return rhs.getCreated().compareTo(lhs.getCreated());
			}

		});
		if (infos.size() >= 50) {
			for (int i = 50; i < infos.size(); i++) {
				infoDao.delete(infos.get(i));
			}
		}
		return infoDao.queryBuilder().list();
	}

	public void insert(ShapeInfo info) {
		info.setCreated(new Date());
		infoDao.insert(info);

	}

	public void update(ShapeInfo info) {
		ShapeInfo mInfo = infoDao.queryBuilder().where(ShapeInfoDao.Properties.Info.eq(info.getInfo())
				, ShapeInfoDao.Properties.Path.eq(info.getPath())).unique();
		if (mInfo != null) {
			mInfo.setCreated(new Date());
			infoDao.update(mInfo);
		} else {
			info.setCreated(new Date());
			infoDao.insert(info);
		}
	}
}
