package com.isarainc.arts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.isarainc.util.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ArtManager {
    public static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    private static final String TAG = "ArtManager";
    private static ArtManager instance;
    private final Context mContext;

    private ArtManager(Context context) {
        this.mContext = context;
    }


    public static ArtManager getInstance(Context context) {
        if (instance == null) {
            instance = new ArtManager(context);
        }
        return instance;
    }

    public static boolean assetExists(Context context, String path) {
        boolean bAssetOk = false;
        try {
            InputStream stream = context.getAssets().open(path);
            stream.close();
            bAssetOk = true;
        } catch (IOException e) {
            Log.w(TAG, "assetExists failed: " + e.toString());
        }
        return bAssetOk;
    }

    public File getWorkDir() {
        File dir = null;
        if (Utils.isSDCARDMounted()) {
            dir = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "Android"
                            + File.separator + "data"
                            + File.separator + mContext.getPackageName()
                            + File.separator + "workspace");
        } else {
            dir = new File(
                    Environment.getDataDirectory().getPath()
                            + File.separator + mContext.getPackageName()
                            + File.separator + "workspace");
        }
        return dir;
    }

    public List<Art> listArts() {
        List<Art> arts = new LinkedList<>();
        try {
            String[] dirs = mContext.getAssets().list("arts");
            for (String dir : dirs) {

                if (assetExists(mContext, "arts/" + dir + "/meta.json")) {
                    try {
                        Art art = loadArt(dir);
                        arts.add(art);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"art=" +arts);
        return arts;
    }

    /**
     * @param artPath
     * @param file
     * @return
     */
    public Bitmap loadArtBitmap(String artPath, String file) {
        InputStream istr = null;
        Bitmap bitmap = null;

        try {
            istr = mContext.getAssets().open("arts/" + artPath + "/" + file);
            bitmap = BitmapFactory.decodeStream(istr);

        } catch (IOException e) {
            Log.w(TAG, "loadArtBitmap ", e);
            e.printStackTrace();
        } finally {
            if (istr != null) {
                try {
                    istr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public Art loadArt(String path) {
        InputStream istr;
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            istr = mContext.getAssets().open("arts/" + path + "/meta.json");
            br = new BufferedReader(new InputStreamReader(istr));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            Log.w(TAG, "loadArt ", e);
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
       // Log.d(TAG, "sb =" + sb);
        Art art = gson.fromJson(sb.toString(), Art.class);
        Log.d(TAG, "art =" + art + " on path " + path);
        if (art != null) {
            art.setPath(path);
        }

        return art;
    }


    public List<Save> listSaves() {
        List<Save> saves = new LinkedList<>();
        File workDir = getWorkDir();
        File dirs[] = workDir.listFiles();
        if (dirs != null) {
            for (File dir : dirs) {
                File meta = new File(dir, "meta.json");
                if (meta.exists()) {
                    Save save = loadSave(dir.getName());
                    if (save != null) {
                        Art art = loadArt(save.getArt());
                        if (art == null) {
                            deleteSave(dir.getName());
                        }
                        saves.add(0, save);
                    } else {
                        deleteSave(dir.getName());
                    }
                }
            }
        }
        Collections.sort(saves, new Comparator<Save>() {
            @Override
            public int compare(Save save, Save t1) {
                return t1.getUpdated().compareTo(save.getUpdated());
            }
        });
        return saves;
    }

    public String nextFileName(String art) {
        File dir = getWorkDir();
        int i = 1;
        while (true) {
            File file = new File(dir, String.format(Locale.getDefault(), "%s%d", art, i));
            if (!file.exists()) {
                return file.getName();
            }
            i++;
        }
    }

    public Save loadSave(String path) {
        File workDir = getWorkDir();
        File file = new File(workDir, path + File.separator + "meta.json");

        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            Log.w(TAG, "loadSave ", e);
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Save save = gson.fromJson(sb.toString(), Save.class);
        //  Log.d(TAG, "save =" + save);
        if (save != null) {
            save.setPath(path);

        }
        return save;

    }


    public void createArt(String art, String path) {
        File workDir = getWorkDir();
        File dir = new File(workDir, path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Save save = new Save();
        save.setName(path);
        save.setPath(path);
        save.setAuthor("Me");
        save.setCreated(new Date());
        save.setUpdated(new Date());
        save.setArt(art);
        try {
            Writer output = null;
            File file = new File(dir, "meta.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(gson.toJson(save));
            output.close();
            //Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void updateSave(Save save) {
        File workDir = getWorkDir();
        File dir = new File(workDir, save.getPath());

        save.setUpdated(new Date());
        try {
            Writer output = null;
            File file = new File(dir, "meta.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(gson.toJson(save));
            output.close();
            //Toast.makeText(mContext, "Composition saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public void deleteSave(String name) {
        File workDir = getWorkDir();
        File dir = new File(workDir, name);
        // Log.d(TAG, "deleteSave " + dir);
        if (dir.exists()) {
            deleteRecursive(dir);
        }
    }



}
