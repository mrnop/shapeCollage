package com.isarainc.stickers;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.isarainc.shapecollage.DaoMaster;
import com.isarainc.shapecollage.DaoSession;
import com.isarainc.shapecollage.StickerDao;
import com.isarainc.shapecollage.StickerInfoDao;
import com.isarainc.util.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StickerManager {
    private static final String TAG = "StickerManager";
    private static StickerManager instance;

    private Map<String, List<StickerInfo>> lineStickers = new HashMap<String, List<StickerInfo>>();
    private Map<String, List<StickerInfo>> lineCamSecStickers = new HashMap<String, List<StickerInfo>>();
    private Map<String, List<StickerInfo>> lineCamStampStickers = new HashMap<String, List<StickerInfo>>();
    private Map<String, List<StickerInfo>> momentcamStickers = new HashMap<String, List<StickerInfo>>();
    private Map<String, List<StickerInfo>> bgRemoverStickers = new HashMap<String, List<StickerInfo>>();
    private Map<String, List<StickerInfo>> chatOnStickers = new HashMap<String, List<StickerInfo>>();
    private Map<String, List<StickerInfo>> stickers = new HashMap<String, List<StickerInfo>>();
    private Map<String, List<StickerInfo>> downloadStickers = new HashMap<String, List<StickerInfo>>();
    private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());

    private Context context;
    private StickerDao dao;
    private StickerInfoDao infoDao;
    private StickerListener stickerListener;
    private boolean downloadable = true;
    private boolean detectable = true;

    public interface StickerListener {
        void onUpdated(Sticker sticker);

        void onAdded(Sticker sticker);
    }

    public StickerListener getStickerListener() {
        return stickerListener;
    }

    public void setStickerListener(StickerListener stickerListener) {
        this.stickerListener = stickerListener;
    }

    public static StickerManager getInstance(Context context) {
        if (instance == null) {
            instance = new StickerManager(context);
        }
        return instance;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }

    public boolean isDetectable() {
        return detectable;
    }

    public void setDetectable(boolean detectable) {
        this.detectable = detectable;
    }

    private StickerManager(Context context) {
        this.context = context;
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, "sticker-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        dao = daoSession.getStickerDao();
        ;
        infoDao = daoSession.getStickerInfoDao();
        String list[];
        try {
            list = context.getAssets().list("stickers");
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    stickers.put(list[i], null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Utils.isSDCARDMounted()) {
            File bgRemoverDir = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "Android"
                            + File.separator + "data"
                            + File.separator + "com.isarainc.bgremove"
                            + File.separator + "items");
            if (bgRemoverDir.exists()) {
                File[] files = bgRemoverDir.listFiles();
                if (files != null && files.length > 0) {
                    bgRemoverStickers.put("bgRemover", null);
                    Log.d(TAG, "has bgRemoverStickers");
                }
            }
            if (downloadable) {
                // Log.d(TAG, "load download sticker");
                File downloadDir = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + "Android" + File.separator
                                + "data" + File.separator
                                + context.getPackageName() + File.separator + "stickers");
                // Log.d(TAG, "dir " +dir);
                if (downloadDir.exists()) {
                    File[] dirs = downloadDir.listFiles();

                    for (File dir : dirs) {
                        // Log.d(TAG, "dir " + dir);
                        if (dir.isDirectory() && dir.list() != null
                                && dir.list().length > 0) {
                            downloadStickers.put(dir.getName(), null);

                        }
                    }

                }
            }
            if (detectable) {
                File lineDir = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + "Android" + File.separator
                                + "data" + File.separator
                                + "jp.naver.line.android" + File.separator
                                + "stickers");

                Log.d(TAG,"lineDir=" + lineDir);
                if (lineDir.exists()) {
                    File[] dirs = lineDir.listFiles();
                    if (dirs != null) {
                        for (File dir : dirs) {
                            // Log.d(TAG, "dir " +dir);
                            boolean add = false;
                            if (dir != null && dir.isDirectory()) {
                                for (File file : dir.listFiles()) {
                                    // Log.d(TAG, "file " +file);
                                    try {
                                        Integer.parseInt(file.getName());
                                        add = true;
                                        break;
                                    } catch (Exception e) {

                                    }
                                }
                            }
                            if (add) {
                                // Log.d(TAG, "lineStickers " + dir.getName());
                                lineStickers.put(dir.getName(), null);
                            }

                        }

                    }
                }
                File lineCamStampDir = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + "Android" + File.separator
                                + "data" + File.separator
                                + "jp.naver.linecamera.android"
                                + File.separator + "files" + File.separator
                                + "stamp");

                if (lineCamStampDir.exists()) {
                    File[] files = lineCamStampDir.listFiles();
                    if (files != null && files.length > 0) {
                        lineCamStampStickers.put("stamp", null);
                        Log.d(TAG, "has lineCamStampStickers");
                    }
                }
                File momencamDir = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + "MomanCamera"
                                + File.separator + "MomentCam_Drawing");

                if (momencamDir.exists()) {
                    File[] files = momencamDir.listFiles();
                    if (files != null && files.length > 0) {
                        momentcamStickers.put("momentcam", null);
                        Log.d(TAG, "has momentcamStickers");
                    }
                }

                File lineCamSecDir = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + "Android" + File.separator
                                + "data" + File.separator
                                + "jp.naver.linecamera.android"
                                + File.separator + "files" + File.separator
                                + "section");

                if (lineCamSecDir.exists()) {
                    File[] dirs = lineCamSecDir.listFiles();

                    for (File dir : dirs) {
                        // Log.d(TAG, "dir " +dir);
                        boolean add = false;
                        if (dir != null && dir.isDirectory()) {
                            if (dir.listFiles() != null
                                    && dir.listFiles().length > 0) {
                                add = true;
                            }
                        }
                        if (add) {
                            // Log.d(TAG, "lineStickers " + dir.getName());
                            lineCamSecStickers.put(dir.getName(), null);
                        }

                    }

                }

                File chatOnDir = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + "Android" + File.separator
                                + "data" + File.separator + "com.sec.chaton"
                                + File.separator + "cache" + File.separator
                                + "anicon");

                if (chatOnDir.exists()) {
                    File[] dirs = chatOnDir.listFiles();

                    for (File dir : dirs) {
                        // Log.d(TAG, "dir " +dir);
                        boolean add = false;
                        if (dir != null && dir.isDirectory()) {
                            if (dir.listFiles() != null
                                    && dir.listFiles().length > 0) {
                                add = true;
                            }

                        }
                        if (add) {
                            // Log.d(TAG, "add " +dir);
                            chatOnStickers.put(dir.getName(), null);
                        }

                    }

                }
            }
        }

    }

    public List<String> getAssetStickerSets() {
        List<String> list = new ArrayList<String>();
        for (String key : stickers.keySet()) {
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

    public List<String> getStickerSets() {
        List<String> list = new ArrayList<String>();
        for (String key : downloadStickers.keySet()) {
            list.add(key);
        }
        return list;
    }

    public List<String> getLineStickerSets() {
        List<String> list = new ArrayList<String>();
        for (String key : lineStickers.keySet()) {
            list.add(key);
        }
        return list;
    }

    public List<String> getLineCamSecStickerSets() {
        List<String> list = new ArrayList<String>();
        for (String key : lineCamSecStickers.keySet()) {
            list.add(key);
        }
        return list;
    }

    public List<String> getLineCamStampStickerSets() {
        List<String> list = new ArrayList<String>();
        for (String key : lineCamStampStickers.keySet()) {
            list.add(key);
        }
        return list;
    }

    public List<String> getRemoverStickerSets() {
        List<String> list = new ArrayList<String>();
        for (String key : bgRemoverStickers.keySet()) {
            list.add(key);
        }
        return list;
    }

    public List<String> getMomentCamStickerSets() {
        List<String> list = new ArrayList<String>();
        for (String key : momentcamStickers.keySet()) {
            list.add(key);
        }
        return list;
    }

    public List<String> getChatOnStickerSets() {
        List<String> list = new ArrayList<String>();
        for (String key : chatOnStickers.keySet()) {
            list.add(key);
        }
        return list;
    }

    /**
     * @return
     */
    public List<StickerInfo> loadLineStickers(String folder) {
        List<StickerInfo> images = lineStickers.get(folder);
        if (images != null && !images.isEmpty()) {
            return images;
        } else {
            images = new ArrayList<StickerInfo>();
            lineStickers.put(folder, images);
        }

        // Try to load line sticker
        if (Utils.isSDCARDMounted()) {
            File dir = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Android" + File.separator + "data"
                    + File.separator + "jp.naver.line.android" + File.separator
                    + "stickers" + File.separator + folder);

            // Log.d(TAG, dir.getAbsolutePath());
            if (dir.exists()) {
                File slist[] = dir.listFiles();
                for (int i = 0; i < slist.length; i++) {
                    try {
                        Integer.parseInt(slist[i].getName());
                        StickerInfo info = new StickerInfo();
                        info.setInfoName(slist[i].getName());
                        info.setFolder(folder);
                        info.setType(StickerInfo.TYPE_LINE);
                        info.setPath(slist[i].getAbsolutePath());
                        images.add(info);
                    } catch (Exception e) {

                    }

                }
            }

        }

        return images;

    }

    public List<StickerInfo> loadLineCamSecStickers(String folder) {
        List<StickerInfo> images = lineCamSecStickers.get(folder);
        if (images != null && !images.isEmpty()) {
            return images;
        } else {
            images = new ArrayList<StickerInfo>();
            lineCamSecStickers.put(folder, images);
        }

        // Try to load chaton sticker
        if (Utils.isSDCARDMounted()) {
            File dir = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Android" + File.separator + "data"
                    + File.separator + "jp.naver.linecamera.android"
                    + File.separator + "files" + File.separator + "section"
                    + File.separator + folder);

            // Log.d(TAG, dir.getAbsolutePath());
            if (dir.exists()) {
                File slist[] = dir.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith(".png");
                    }

                });
                for (int i = 0; i < slist.length; i++) {

                    StickerInfo info = new StickerInfo();
                    info.setInfoName(slist[i].getName());
                    info.setFolder(folder);
                    info.setType(StickerInfo.TYPE_LINE_CAMERA_SECTION);
                    info.setPath(slist[i].getAbsolutePath());
                    images.add(info);

                }
            }

        }

        return images;

    }

    public List<StickerInfo> loadLineCamStampStickers(String folder) {
        List<StickerInfo> images = lineCamStampStickers.get(folder);
        if (images != null && !images.isEmpty()) {
            return images;
        } else {
            images = new ArrayList<StickerInfo>();
            lineCamStampStickers.put(folder, images);
        }

        if (Utils.isSDCARDMounted()) {
            File dir = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Android" + File.separator + "data"
                    + File.separator + "jp.naver.linecamera.android"
                    + File.separator + "files" + File.separator + "stamp");

            // Log.d(TAG, dir.getAbsolutePath());
            if (dir.exists()) {
                File slist[] = dir.listFiles();
                for (int i = 0; i < slist.length; i++) {
                    if (!slist[i].getName().startsWith(".")) {
                        StickerInfo info = new StickerInfo();
                        info.setInfoName(slist[i].getName());
                        info.setFolder(folder);
                        info.setType(StickerInfo.TYPE_LINE_CAMERA_STAMP);
                        info.setPath(slist[i].getAbsolutePath());
                        images.add(info);
                    }

                }
            }

        }

        return images;

    }

    public List<StickerInfo> loadBgRemoverStickers(String folder) {
        List<StickerInfo> images = bgRemoverStickers.get(folder);
        if (images != null && !images.isEmpty()) {
            return images;
        } else {
            images = new ArrayList<StickerInfo>();
            bgRemoverStickers.put(folder, images);
        }

        if (Utils.isSDCARDMounted()) {
            File dir = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "Android"
                            + File.separator + "data"
                            + File.separator + "com.isarainc.bgremove"
                            + File.separator + "items");

            // Log.d(TAG, dir.getAbsolutePath());
            if (dir.exists()) {
                File slist[] = dir.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith(".png");
                    }

                });
                for (int i = 0; i < slist.length; i++) {
                    if (!slist[i].getName().startsWith(".")) {
                        StickerInfo info = new StickerInfo();
                        info.setInfoName(slist[i].getName());
                        info.setFolder(folder);
                        info.setType(StickerInfo.TYPE_BGREMOVER);
                        info.setPath(slist[i].getAbsolutePath());
                        images.add(info);
                    }

                }
            }

        }

        return images;

    }

    public List<StickerInfo> loadMomentCamStickers(String folder) {
        List<StickerInfo> images = momentcamStickers.get(folder);
        if (images != null && !images.isEmpty()) {
            return images;
        } else {
            images = new ArrayList<StickerInfo>();
            momentcamStickers.put(folder, images);
        }

        if (Utils.isSDCARDMounted()) {
            File dir = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "MomanCamera" + File.separator
                    + "MomentCam_Drawing");

            // Log.d(TAG, dir.getAbsolutePath());
            if (dir.exists()) {
                File slist[] = dir.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith(".jpg");
                    }

                });
                for (int i = 0; i < slist.length; i++) {
                    if (!slist[i].getName().startsWith(".")) {
                        StickerInfo info = new StickerInfo();
                        info.setInfoName(slist[i].getName());
                        info.setFolder(folder);
                        info.setType(StickerInfo.TYPE_MOMENTCAM);
                        info.setPath(slist[i].getAbsolutePath());
                        images.add(info);
                    }

                }
            }

        }

        return images;

    }

    public List<StickerInfo> loadChatOnStickers(String folder) {
        List<StickerInfo> images = chatOnStickers.get(folder);
        if (images != null && !images.isEmpty()) {
            return images;
        } else {
            images = new ArrayList<StickerInfo>();
            chatOnStickers.put(folder, images);
        }

        // Try to load chaton sticker
        if (Utils.isSDCARDMounted()) {
            File dir = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Android" + File.separator + "data"
                    + File.separator + "com.sec.chaton" + File.separator
                    + "cache" + File.separator + "anicon" + File.separator
                    + folder);

            // Log.d(TAG, dir.getAbsolutePath());
            if (dir.exists()) {
                File slist[] = dir.listFiles();
                for (int i = 0; i < slist.length; i++) {
                    if (!slist[i].getName().startsWith(".")) {
                        StickerInfo info = new StickerInfo();
                        info.setInfoName(slist[i].getName());
                        info.setFolder(folder);
                        info.setType(StickerInfo.TYPE_CHATON);
                        info.setPath(slist[i].getAbsolutePath());
                        images.add(info);
                    }

                }
            }

        }

        return images;

    }

    public List<StickerInfo> loadAssetStickers(String folder) {
        List<StickerInfo> images = stickers.get(folder);
        if (images != null && !images.isEmpty()) {
            return images;
        } else {
            images = new ArrayList<StickerInfo>();
            stickers.put(folder, images);
        }
        try {
            String list[] = context.getAssets().list("stickers/" + folder);
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    StickerInfo info = new StickerInfo();
                    info.setInfoName(list[i]);
                    info.setFolder(folder);
                    info.setType(StickerInfo.TYPE_BUNDLE);
                    info.setPath(list[i]);
                    images.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }

    public List<StickerInfo> loadStickers(String folder) {
        List<StickerInfo> images = downloadStickers.get(folder);
        if (images != null && !images.isEmpty()) {
            return images;
        } else {
            images = new ArrayList<StickerInfo>();
            downloadStickers.put(folder, images);
        }

        // Try to load from download
        if (Utils.isSDCARDMounted()) {
            File dir = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Android" + File.separator + "data"
                    + File.separator + context.getPackageName() + File.separator + "stickers"
                    + File.separator + folder);
            // Log.d(TAG, dir.getAbsolutePath());
            if (dir.exists()) {
                File slist[] = dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.endsWith(".png");
                    }

                });
                for (int i = 0; i < slist.length; i++) {
                    StickerInfo info = new StickerInfo();
                    info.setInfoName(slist[i].getName());
                    info.setFolder(folder);
                    info.setType(StickerInfo.TYPE_DOWNLOAD);
                    info.setPath(slist[i].getAbsolutePath());
                    images.add(info);
                }
            }

        }

        return images;
    }
    public Bitmap getStickerBitmap(StickerInfo info) {
        if(StickerInfo.TYPE_BUNDLE.equals(info.getType())){
            return  getStickerFromAssets(info.getFolder(),info.getPath());
        }else{
            return getStickerFromFile(info.getPath());
        }

    }


    public Bitmap getStickerFromFile( String path) {
        Bitmap bitmap = null;
        File picture = new File(path);
        if (picture.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            try {
                bitmap = BitmapFactory.decodeFile(
                        picture.getAbsolutePath(), options);
            } catch (OutOfMemoryError oome) {
                System.gc();
                try {
                    bitmap = BitmapFactory.decodeFile(
                            picture.getAbsolutePath(), options);

                } catch (OutOfMemoryError oome2) {
                    System.gc();
                    options.inSampleSize = 3;
                    try {
                        bitmap = BitmapFactory.decodeFile(
                                picture.getAbsolutePath(), options);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return null;
    }

    public Bitmap getStickerFromAssets(String folder, String fileName) {
        AssetManager assetManager = context.getAssets();
        InputStream istr;
        try {
            istr = assetManager.open("stickers/" + folder + "/" + fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            return bitmap;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    public Sticker getSticker(String ref) {
        return dao.queryBuilder().where(StickerDao.Properties.RefNo.eq(ref)).unique();
    }

    public List<Sticker> listShopStickers() {
        return dao.queryBuilder().list();

    }

    /*
        public void checkUpdateSticker() {
            String url = "http://mylb-750921322.us-east-1.elb.amazonaws.com/ci/index.php/nn/stickers/format/json";

            aq.ajax(url, JSONArray.class, this, "updateStickerCb");
        }

        public void updateStickerCb(String url, JSONArray jsonArray,
                AjaxStatus status) {
            // progress.setVisibility(View.INVISIBLE);
            if (jsonArray != null) {
                // successful ajax call
                Log.d(TAG, url + ":" + jsonArray + ":" + status.getMessage());

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String id = jsonObj.getString("id");

                        String name = jsonObj.getString("name");
                        String lang = jsonObj.getString("country");
                        String remoteUrl = jsonObj.getString("url");
                        String dateStr = jsonObj.getString("created");
                        int active = jsonObj.getInt("active");
                        Date created = fmt.parse(dateStr);
                        Sticker sticker = new Sticker();
                        sticker.setRefNo(id);
                        sticker.setStickerName(name);
                        sticker.setActive(active == 1);
                        sticker.setCreated(created);
                        sticker.setRemoteUrl(remoteUrl);

                        Sticker mySticker = getSticker(sticker.getRefNo());
                        if (mySticker != null) {
                            mySticker.setStickerName(name);
                            mySticker.setCreated(created);
                            mySticker.setRemoteUrl(remoteUrl);
                            mySticker.setActive(active == 1);
                            if (stickerListener != null) {
                                stickerListener.onUpdated(mySticker);
                            }
                        } else {
                            insert(sticker);
                            if (stickerListener != null) {
                                stickerListener.onAdded(sticker);
                            }
                        }
                        // stickers.add(sticker);
                        // adapter.notifyDataSetChanged();

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }

            } else {

                Log.d(TAG, url + ":" + jsonArray + ":" + status.getMessage());
                // Toast.makeText(this,
                // "Cannot connect to our shop, please try again",
                // Toast.LENGTH_LONG).show();
            }

        }
    */
    public void insert(Sticker sticker) {
        dao.insert(sticker);
        downloadStickers.put(sticker.getStickerName(), null);
    }

    public void update(Sticker sticker) {
        dao.update(sticker);
        downloadStickers.put(sticker.getStickerName(), null);
    }

    public boolean exists(String ref) {
        return getSticker(ref) != null;
    }

    public List<StickerInfo> getRecents() {
        List<StickerInfo> infos = infoDao.queryBuilder().list();
        Collections.sort(infos, new Comparator<StickerInfo>() {

            @Override
            public int compare(StickerInfo lhs, StickerInfo rhs) {
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

    public void insert(StickerInfo info) {
        info.setCreated(new Date());
        infoDao.insert(info);

    }

    public void update(StickerInfo info) {
        StickerInfo mInfo = infoDao.queryBuilder().where(StickerInfoDao.Properties.InfoName.eq(info.getInfoName())
                , StickerInfoDao.Properties.Path.eq(info.getPath())).unique();
        if (mInfo != null) {
            mInfo.setCreated(new Date());
            infoDao.update(mInfo);
        } else {
            info.setCreated(new Date());
            infoDao.insert(info);
        }
    }

}
