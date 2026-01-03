package com.isarainc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by mrnop on 1/26/2016 AD.
 */
public class SaveTask extends AsyncTask<Object, Void, Boolean> {
    public interface OnPictureSavedListener {
        void onPictureSaved(Uri uri);
    }

    private final OnPictureSavedListener mListener;
    private final Handler mHandler;
    private Context context;

    public SaveTask(Context context,
                    final OnPictureSavedListener listener) {
        this.context = context;
        mListener = listener;
        mHandler = new Handler();
    }

    @Override
    protected Boolean doInBackground(final Object... params) {
        Bitmap bitmap = (Bitmap) params[0];
        String folderName = (String)  params[1];
        String fileName = (String)  params[2];
        Boolean ret=saveImage(folderName, fileName, bitmap);
        return ret;
    }

    public File getSaveFile(final String folderName, String filename) {

        if (Utils.isSDCARDMounted()) {
            File path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, folderName + "/" + filename + ".png");

            return file;
        } else {
            File path=new File(Environment.getDataDirectory().getPath());
            File file = new File(path, folderName + "/" + filename + ".png");
            return file;
        }
    }

    public Boolean saveImage(final String folderName, final String fileName, final Bitmap image) {

        File file = getSaveFile(folderName,fileName);
        try {
            file.getParentFile().mkdirs();
            image.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
            MediaScannerConnection.scanFile(context,
                    new String[]{
                            file.toString()
                    }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(final String path, final Uri uri) {
                            if (mListener != null) {
                                mHandler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        mListener.onPictureSaved(uri);
                                    }
                                });
                            }
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return  false;
        }
        return true;
    }
}

