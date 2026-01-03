package com.isarainc.crop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.io.File;


public class Crop {
    public static final int REQUEST_CROP = 69;
    public static final int RESULT_ERROR = 96;
    private final Intent mCropIntent;
    private final Bundle mCropOptionsBundle;

    public Crop(Uri source, Uri destination) {
        mCropIntent = new Intent();
        mCropOptionsBundle = new Bundle();
        mCropOptionsBundle.putParcelable(CropActivity.IMAGE_URI, source);
        mCropOptionsBundle.putParcelable(CropActivity.OUTPUT_URI, destination);
    }
    public Crop(File file) {
        mCropIntent = new Intent();
        mCropOptionsBundle = new Bundle();
        mCropOptionsBundle.putString(CropActivity.IMAGE_PATH, file.getPath());
    }
    public Intent getIntent(@NonNull Context context) {
        mCropIntent.setClass(context, CropActivity.class);
        mCropIntent.putExtras(mCropOptionsBundle);
        return mCropIntent;
    }
    public static Crop of(@NonNull Uri source, @NonNull Uri destination) {
        return new Crop(source, destination);
    }

    public static Crop of(@NonNull File file) {
        return new Crop(file.getAbsoluteFile());
    }

    public Crop withAspectRatio(int x, int y) {
        mCropOptionsBundle.putInt(CropActivity.ASPECT_X, x);
        mCropOptionsBundle.putInt(CropActivity.ASPECT_Y, y);
        return this;
    }

    public Crop withTitle(String title) {
        mCropOptionsBundle.putString(CropActivity.TITLE, title);
        return this;
    }
    public Crop withScaleIfNeed() {
        mCropOptionsBundle.putBoolean(CropActivity.SCALE_UP_IF_NEEDED, true);
        return this;
    }
    public Crop withCircle() {
        mCropOptionsBundle.putBoolean(CropActivity.CIRCLE_CROP, true);
        return this;
    }
    public Crop withScale() {
        mCropOptionsBundle.putBoolean(CropActivity.SCALE, true);
        return this;
    }

    public Crop withMask(Bitmap mask) {
        mCropOptionsBundle.putParcelable(CropActivity.MASK, mask);
        return this;
    }

    public Crop withOutputSize(int x, int y) {
        mCropOptionsBundle.putInt(CropActivity.OUTPUT_X, x);
        mCropOptionsBundle.putInt(CropActivity.OUTPUT_Y, y);
        return this;
    }
    /**
     * Send the crop Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    public void start(@NonNull Activity activity) {
        start(activity, REQUEST_CROP);
    }

    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    /**
     * Send the crop Intent from a Fragment
     *
     * @param fragment Fragment to receive result
     */
    public void start(@NonNull Context context, @NonNull Fragment fragment) {
        start(context, fragment, REQUEST_CROP);
    }

    /**
     * Send the crop Intent from a support library Fragment
     *
     * @param fragment Fragment to receive result
     */
    public void start(@NonNull Context context, @NonNull androidx.fragment.app.Fragment fragment) {
        start(context, fragment, REQUEST_CROP);
    }

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void start(@NonNull Context context, @NonNull Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Context context, @NonNull androidx.fragment.app.Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }


}
