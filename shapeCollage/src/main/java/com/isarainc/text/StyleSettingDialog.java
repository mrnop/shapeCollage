package com.isarainc.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.isarainc.fonts.Font;
import com.isarainc.fonts.FontAdapter;
import com.isarainc.fonts.FontManager;
import com.isarainc.shapecollage.R;
import com.isarainc.text.styles.TextStyle;
import com.isarainc.util.Utils;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class StyleSettingDialog {

    protected static final String TAG = "StyleSettingDialog";
    private final Context context;
    private final MaterialDialog.Builder builder;
    private final MaterialDialog dialog;
    private final StyleAdapter mStyleAdapter;
    private OnStyleChangeListener onStyleChangeListener;
    private TextPreview textPreview;

    private TextStyle style;
    private String text;
    private String font;
    private int color1;
    private int color2;
    private int color3;
    private int color4;
    private int color5;

    private Button changeColor1;
    private Button changeColor2;
    private Button changeColor3;
    private Button changeColor4;
    private Button changeColor5;
    private Spinner spinnerFont;
    private Spinner spinnerStyle;
    private FontManager fontManager;
    private List<Font> fonts;
    private List<TextStyle> styles = new LinkedList<>();



    public interface OnStyleChangeListener {
        void onStyleChanged(TextStyle style);
    }

    public OnStyleChangeListener getOnStyleChangeListener() {
        return onStyleChangeListener;
    }

    public void setOnStyleChangeListener(
            OnStyleChangeListener onStyleChangeListener) {
        this.onStyleChangeListener = onStyleChangeListener;
    }

    public TextStyle getStyle() {
        return style;
    }

    public void setStyle(TextStyle style) {
        this.style = style;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        mStyleAdapter.setText(text);
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public StyleSettingDialog(final Context context) {
        this.context = context;
        fontManager = FontManager.getInstance(context);
        fonts = fontManager.getAllActives();
        styles = TextStyle.getAvailables();

        builder = new MaterialDialog.Builder(context)
                .canceledOnTouchOutside(false)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .autoDismiss(false)
                .customView(R.layout.f_style, true);
        builder.negativeColor(Color.BLACK);
        builder.positiveColor(Color.BLACK);
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (onStyleChangeListener != null) {
                    onStyleChangeListener.onStyleChanged(style);
                }
                dialog.dismiss();
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        dialog = builder.build();
        mStyleAdapter =new StyleAdapter(context,"text", styles);
        textPreview = (TextPreview) dialog.findViewById(R.id.drawView);

        spinnerFont = (Spinner) dialog.findViewById(R.id.spinnerFont);
        spinnerFont.setAdapter(new FontAdapter(context, fonts));
        spinnerFont.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                font = fonts.get(position).getName();
                style.setFont(font);
                textPreview.getStyle().setFont(font);
                textPreview.invalidate();
                mStyleAdapter.setFont(font);
                mStyleAdapter.notifyDataSetChanged();
                new GenPreviewTask().execute();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        changeColor1 = (Button) dialog.findViewById(R.id.changeColor1);

        changeColor1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                if (fm.findFragmentByTag("ColorPickerDialogFragment") == null) {
                    ColorPickerDialogFragment newFragment = new ColorPickerDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("oldColor", style.getColor(0));
                    newFragment.setArguments(args);
                    newFragment.show(fm, "ColorPickerDialogFragment");
                    newFragment
                            .setOnColorChangedListener(new OnColorChangedListener() {

                                @Override
                                public void onColorChanged(int color) {
                                    color1 = color;
                                    textPreview.getStyle().setColor(0, color);
                                    changeColor1.setBackgroundColor(style
                                            .getColor(0));
                                    textPreview.invalidate();
                                    mStyleAdapter.setColor1(color);
                                    mStyleAdapter.notifyDataSetChanged();

                                    new GenPreviewTask().execute();

                                }

                            });
                }
            }
        });
        changeColor2 = (Button) dialog.findViewById(R.id.changeColor2);

        changeColor2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                if (fm.findFragmentByTag("ColorPickerDialogFragment") == null) {
                    ColorPickerDialogFragment newFragment = new ColorPickerDialogFragment();
                    Bundle args = new Bundle();

                    args.putInt("oldColor", style.getColor(1));
                    newFragment.setArguments(args);
                    newFragment.show(fm, "ColorPickerDialogFragment");
                    newFragment
                            .setOnColorChangedListener(new OnColorChangedListener() {

                                @Override
                                public void onColorChanged(int color) {
                                    color2 = color;
                                    textPreview.getStyle().setColor(1, color);
                                    changeColor2.setBackgroundColor(style
                                            .getColor(1));
                                    textPreview.invalidate();
                                    mStyleAdapter.setColor2(color);
                                    mStyleAdapter.notifyDataSetChanged();
                                    new GenPreviewTask().execute();


                                }

                            });
                }
            }
        });
        changeColor3 = (Button) dialog.findViewById(R.id.changeColor3);

        changeColor3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                if (fm.findFragmentByTag("ColorPickerDialogFragment") == null) {
                    ColorPickerDialogFragment newFragment = new ColorPickerDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("oldColor", style.getColor(2));
                    newFragment.setArguments(args);
                    newFragment.show(fm, "ColorPickerDialogFragment");
                    newFragment
                            .setOnColorChangedListener(new OnColorChangedListener() {

                                @Override
                                public void onColorChanged(int color) {
                                    color3 = color;
                                    textPreview.getStyle().setColor(2, color);
                                    changeColor3.setBackgroundColor(style
                                            .getColor(2));
                                    textPreview.invalidate();
                                    mStyleAdapter.setColor3(color);
                                    mStyleAdapter.notifyDataSetChanged();
                                    new GenPreviewTask().execute();
                                }

                            });
                }
            }
        });
        changeColor4 = (Button) dialog.findViewById(R.id.changeColor4);


        changeColor4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                if (fm.findFragmentByTag("ColorPickerDialogFragment") == null) {
                    ColorPickerDialogFragment newFragment = new ColorPickerDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("oldColor", style.getColor(3));
                    newFragment.setArguments(args);
                    newFragment.show(fm, "ColorPickerDialogFragment");
                    newFragment
                            .setOnColorChangedListener(new OnColorChangedListener() {

                                @Override
                                public void onColorChanged(int color) {
                                    color4 = color;
                                    textPreview.getStyle().setColor(3, color);
                                    changeColor4.setBackgroundColor(style
                                            .getColor(3));
                                    textPreview.invalidate();
                                    mStyleAdapter.setColor4(color);
                                    mStyleAdapter.notifyDataSetChanged();
                                    new GenPreviewTask().execute();

                                }

                            });
                }
            }
        });
        changeColor5 = (Button) dialog.findViewById(R.id.changeColor5);

        changeColor5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                if (fm.findFragmentByTag("ColorPickerDialogFragment") == null) {
                    ColorPickerDialogFragment newFragment = new ColorPickerDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("oldColor", style.getColor(4));
                    newFragment.setArguments(args);
                    newFragment.show(fm, "ColorPickerDialogFragment");
                    newFragment
                            .setOnColorChangedListener(new OnColorChangedListener() {

                                @Override
                                public void onColorChanged(int color) {
                                    color5 = color;
                                    textPreview.getStyle().setColor(4, color);
                                    changeColor5.setBackgroundColor(style
                                            .getColor(4));
                                    textPreview.invalidate();
                                    mStyleAdapter.setColor5(color);
                                    mStyleAdapter.notifyDataSetChanged();
                                    new GenPreviewTask().execute();

                                }

                            });
                }
            }
        });

        spinnerStyle = (Spinner) dialog.findViewById(R.id.spinnerStyle);

        spinnerStyle.setAdapter(mStyleAdapter);
        spinnerStyle.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                try {
                    style = (TextStyle) styles.get(position).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                textPreview.setStyle(style);
                if (style.colorCount() > 1) {
                    changeColor2.setVisibility(View.VISIBLE);
                } else {
                    changeColor2.setVisibility(View.GONE);
                }
                if (style.colorCount() > 2) {
                    changeColor3.setVisibility(View.VISIBLE);
                } else {
                    changeColor3.setVisibility(View.GONE);
                }
                if (style.colorCount() > 3) {
                    changeColor4.setVisibility(View.VISIBLE);
                } else {
                    changeColor4.setVisibility(View.GONE);
                }
                if (style.colorCount() > 4) {
                    changeColor5.setVisibility(View.VISIBLE);
                } else {
                    changeColor5.setVisibility(View.GONE);
                }

                textPreview.invalidate();
                // textPreview.save();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


    }

    public void show() {
        if (dialog != null) {

            textPreview.setText(text);
            textPreview.setStyle(style);
            color1 = style.getColor(0);
            color2 = style.getColor(1);
            color3 = style.getColor(2);
            color4 = style.getColor(3);
            color5 = style.getColor(4);

            new GenPreviewTask().execute();
            int stylePosition = 0;
            for (int i = 0; i < styles.size(); i++) {
                TextStyle s = styles.get(i);
                // Log.d(TAG, "style=" + style +",s=" +s +":" +(s == style));
                if (s.getClass().getSimpleName()
                        .equals(style.getClass().getSimpleName())) {
                    stylePosition = i;
                    break;
                }
            }
            int fontPosition = 0;
            for (int i = 0; i < fonts.size(); i++) {
                String f = fonts.get(i).getName();
                //Log.d(TAG, (f == font) + ":" + "font=" + font + ",f=" + f);
                if (f.equals(font)) {
                    fontPosition = i;
                    break;
                }
            }

            spinnerFont.setSelection(fontPosition);
            changeColor1.setBackgroundColor(style.getColor(0));
            if (style.colorCount() > 1) {
                changeColor2.setVisibility(View.VISIBLE);
            } else {
                changeColor2.setVisibility(View.GONE);
            }
            changeColor2.setBackgroundColor(style.getColor(1));
            if (style.colorCount() > 2) {
                changeColor3.setVisibility(View.VISIBLE);
            } else {
                changeColor3.setVisibility(View.GONE);
            }
            changeColor3.setBackgroundColor(style.getColor(2));
            if (style.colorCount() > 3) {
                changeColor4.setVisibility(View.VISIBLE);
            } else {
                changeColor4.setVisibility(View.GONE);
            }
            changeColor4.setBackgroundColor(style.getColor(3));

            if (style.colorCount() > 4) {
                changeColor5.setVisibility(View.VISIBLE);
            } else {
                changeColor5.setVisibility(View.GONE);
            }
            changeColor5.setBackgroundColor(style.getColor(4));
            spinnerStyle.setSelection(stylePosition);

            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    private class GenPreviewTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < styles.size(); i++) {
                try {
                    Bitmap saveBitmap = Bitmap.createBitmap(150, 60,
                            Bitmap.Config.ARGB_8888);
                    Canvas saveCanvas = new Canvas(saveBitmap);
                    TextStyle style = styles.get(i);
                    if (text != null && text.length() > 12) {
                        style.setSize(24);
                    } else {
                        style.setSize(32);
                    }
                    style.setColor(0, color1);
                    style.setColor(1, color2);
                    style.setColor(2, color3);
                    style.setColor(3, color4);
                    style.setColor(4, color5);

                    style.setFont(font);
                    style.draw(saveCanvas, 75, 25, text);
                    OutputStream fOut = null;
                    File file = getSaveFile(style.getType().toLowerCase());

                    fOut = new FileOutputStream(file);
                    saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    saveBitmap.recycle();
                    fOut.flush();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } catch (Throwable e) {
                    e.printStackTrace();

                }
            }
            return null;

        }

        private File getSaveFile(String filename) {

            if (Utils.isSDCARDMounted()) {
                File dir = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "Android" + File.separator + "data"
                        + File.separator + context.getPackageName() + File.separator
                        + "styles");

                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File f = new File(dir, filename + ".png");
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();

                }
                return f;
            } else {
                File dir = new File(Environment.getDataDirectory()
                        + File.separator + File.separator + "Android"
                        + File.separator + "data" + File.separator
                        + context.getPackageName() + File.separator + "styles");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File f = new File(dir, filename + ".png");
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();

                }
                return f;
            }
        }

        protected void onProgressUpdate(Void... progress) {

        }

    }
}
