package com.isarainc.stickers;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.isarainc.shapecollage.R;
import com.mikepenz.iconics.utils.Utils;

import java.util.LinkedList;
import java.util.List;


public class StickerPickerDialog extends DialogFragment {

    private static final String TAG = "StickerPickerDialog";
    private  ViewPager viewPager;
    private  TabLayout tabLayout;
    private  SectionsPagerAdapter mSectionsPagerAdapter;

    private OnImagePickListener onImagePickListener;

    private StickerManager stickerManager;

    public interface OnImagePickListener {
        void onImagePicked(Bitmap icon, StickerInfo info);
    }

    private List<Drawable> imageResId = new LinkedList<>();
    private List<Fragment> fragments = new LinkedList<>();
    private List<String> fragmentTitleList = new LinkedList<>();

    public OnImagePickListener getOnImagePickListener() {
        return onImagePickListener;
    }

    public void setOnImagePickListener(OnImagePickListener onClickListener) {
        this.onImagePickListener = onClickListener;
    }

    public static class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments = new LinkedList<>();
        private List<String> fragmentTitleList = new LinkedList<>();

        public SectionsPagerAdapter(FragmentManager manager,List<Fragment> fragments,List<String> fragmentTitleList) {
            super(manager);
            this.fragments = fragments;
            this.fragmentTitleList = fragmentTitleList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";//fragmentTitleList.get(position);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.f_stickers, container, false);
        stickerManager = StickerManager.getInstance(getContext());



        viewPager = (ViewPager) root.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) root.findViewById(R.id.tabs);



        final List<StickerInfo> infos = stickerManager.getRecents();
        if (!infos.isEmpty()) {
            StickerFragment stickerFragment = new StickerFragment();
            stickerFragment.setOnImagePickListener(new OnImagePickListener() {
                @Override
                public void onImagePicked(Bitmap icon, StickerInfo info) {
                    stickerManager.update(info);
                    if(onImagePickListener!=null){
                        onImagePickListener.onImagePicked(icon,info);
                    }
                    dismiss();
                }
            });
            stickerFragment.setInfos(infos);
            fragments.add(stickerFragment);
            fragmentTitleList.add("recent");
            imageResId.add(ContextCompat.getDrawable(getContext(), R.drawable.ic_recent));

        }
        // Add Bundle Sticker
        List<String> stickerSets = stickerManager.getAssetStickerSets();
        for (int i = 0; i < stickerSets.size(); i++) {
            final String setName = stickerSets.get(i);
            final List<StickerInfo> stickers = stickerManager
                    .loadAssetStickers(setName);

            StickerFragment stickerFragment = new StickerFragment();
            stickerFragment.setOnImagePickListener(new OnImagePickListener() {
                @Override
                public void onImagePicked(Bitmap icon, StickerInfo info) {
                    stickerManager.update(info);
                    if(onImagePickListener!=null){
                        onImagePickListener.onImagePicked(icon,info);
                    }
                    dismiss();
                }
            });

            stickerFragment.setInfos(stickers);
            fragments.add(stickerFragment);
            fragmentTitleList.add(setName);
            Bitmap bitmap = stickerManager.getStickerFromAssets(setName, stickers.get(0).getPath());
            Drawable d = new BitmapDrawable(getActivity().getResources(), bitmap);
            imageResId.add(d);

        }
        // Add Download Sticker
        if (stickerManager.isDownloadable()) {
            final List<String> downloadStickerSets = stickerManager.getStickerSets();

            for (int i = 0; i < downloadStickerSets.size(); i++) {
                final String setName = downloadStickerSets.get(i);
                final List<StickerInfo> stickers = stickerManager
                        .loadStickers(setName);
                if (!stickers.isEmpty()) {

                    StickerFragment stickerFragment = new StickerFragment();
                    stickerFragment.setOnImagePickListener(new OnImagePickListener() {
                        @Override
                        public void onImagePicked(Bitmap icon, StickerInfo info) {
                            stickerManager.update(info);
                            if(onImagePickListener!=null){
                                onImagePickListener.onImagePicked(icon,info);
                            }
                            dismiss();
                        }
                    });

                    stickerFragment.setInfos(stickers);
                    fragments.add(stickerFragment);
                    fragmentTitleList.add(setName);
                    Bitmap bitmap = stickerManager.getStickerFromFile(stickers.get(0).getPath());
                    Drawable d = new BitmapDrawable(getActivity().getResources(), bitmap);
                    imageResId.add(d);

                }
            }
        }

        List<String> bgRemoverStickerSets = stickerManager
                .getRemoverStickerSets();
        for (int i = 0; i < bgRemoverStickerSets.size(); i++) {
            final String setName = bgRemoverStickerSets.get(i);
            final List<StickerInfo> stickers = stickerManager
                    .loadBgRemoverStickers(setName);
            if (!stickers.isEmpty()) {

                StickerFragment stickerFragment = new StickerFragment();
                stickerFragment.setOnImagePickListener(new OnImagePickListener() {
                    @Override
                    public void onImagePicked(Bitmap icon, StickerInfo info) {
                        stickerManager.update(info);
                        if(onImagePickListener!=null){
                            onImagePickListener.onImagePicked(icon,info);
                        }
                        dismiss();
                    }
                });

                stickerFragment.setInfos(stickers);
                fragments.add(stickerFragment);
                fragmentTitleList.add(setName);
                Bitmap bitmap = stickerManager.getStickerFromFile(stickers.get(0).getPath());
                Drawable d = new BitmapDrawable(getActivity().getResources(), bitmap);
                imageResId.add(d);
            }
        }
        // Add Line Sticker
        if (stickerManager.isDetectable()) {
            List<String> lineStickerSets = stickerManager.getLineStickerSets();
            for (int i = 0; i < lineStickerSets.size(); i++) {
                final String setName = lineStickerSets.get(i);
                final List<StickerInfo> stickers = stickerManager
                        .loadBgRemoverStickers(setName);
                if (!stickers.isEmpty()) {

                    StickerFragment stickerFragment = new StickerFragment();
                    stickerFragment.setOnImagePickListener(new OnImagePickListener() {
                        @Override
                        public void onImagePicked(Bitmap icon, StickerInfo info) {
                            stickerManager.update(info);
                            if(onImagePickListener!=null){
                                onImagePickListener.onImagePicked(icon,info);
                            }
                            dismiss();
                        }

                    });

                    stickerFragment.setInfos(stickers);
                    fragments.add(stickerFragment);
                    fragmentTitleList.add(setName);
                    Bitmap bitmap = stickerManager.getStickerFromFile(stickers.get(0).getPath());
                    Drawable d = new BitmapDrawable(getActivity().getResources(), bitmap);
                    imageResId.add(d);
                }
            }
        }

        // Add LineCam Sec Sticker
        List<String> lineCamSecStickerSets = stickerManager
                .getLineCamSecStickerSets();
        for (int i = 0; i < lineCamSecStickerSets.size(); i++) {
            final String setName = lineCamSecStickerSets.get(i);
            final List<StickerInfo> stickers = stickerManager
                    .loadBgRemoverStickers(setName);
            if (!stickers.isEmpty()) {

                StickerFragment stickerFragment = new StickerFragment();
                stickerFragment.setOnImagePickListener(new OnImagePickListener() {
                    @Override
                    public void onImagePicked(Bitmap icon, StickerInfo info) {
                        stickerManager.update(info);
                        if(onImagePickListener!=null){
                            onImagePickListener.onImagePicked(icon,info);
                        }
                        dismiss();
                    }

                });

                stickerFragment.setInfos(stickers);
                fragments.add(stickerFragment);
                fragmentTitleList.add(setName);
                Bitmap bitmap = stickerManager.getStickerFromFile(stickers.get(0).getPath());
                Drawable d = new BitmapDrawable(getActivity().getResources(), bitmap);
                imageResId.add(d);

            }
        }
        // Add LineCam Stamp Sticker
        List<String> lineCamStampStickerSets = stickerManager
                .getLineCamStampStickerSets();
        for (int i = 0; i < lineCamStampStickerSets.size(); i++) {
            final String setName = lineCamStampStickerSets.get(i);
            final List<StickerInfo> stickers = stickerManager
                    .loadBgRemoverStickers(setName);
            if (!stickers.isEmpty()) {

                StickerFragment stickerFragment = new StickerFragment();
                stickerFragment.setOnImagePickListener(new OnImagePickListener() {
                    @Override
                    public void onImagePicked(Bitmap icon, StickerInfo info) {
                        stickerManager.update(info);
                        if(onImagePickListener!=null){
                            onImagePickListener.onImagePicked(icon,info);
                        }
                        dismiss();
                    }
                });

                stickerFragment.setInfos(stickers);
                fragments.add(stickerFragment);
                fragmentTitleList.add(setName);
            }
        }
        // Add MomentCam Sticker
        List<String> momentCamStickerSets = stickerManager
                .getMomentCamStickerSets();
        for (int i = 0; i < momentCamStickerSets.size(); i++) {
            final String setName = momentCamStickerSets.get(i);
            final List<StickerInfo> stickers = stickerManager
                    .loadBgRemoverStickers(setName);
            if (!stickers.isEmpty()) {

                StickerFragment stickerFragment = new StickerFragment();
                stickerFragment.setOnImagePickListener(new OnImagePickListener() {
                    @Override
                    public void onImagePicked(Bitmap icon, StickerInfo info) {
                        stickerManager.update(info);
                        if(onImagePickListener!=null){
                            onImagePickListener.onImagePicked(icon,info);
                        }
                        dismiss();
                    }
                });

                stickerFragment.setInfos(stickers);
                fragments.add(stickerFragment);
                fragmentTitleList.add(setName);
                Bitmap bitmap = stickerManager.getStickerFromFile(stickers.get(0).getPath());
                Drawable d = new BitmapDrawable(getActivity().getResources(), bitmap);
                imageResId.add(d);
            }
        }
        // Add ChatOn Sticker
        List<String> chatOnStickerSets = stickerManager
                .getChatOnStickerSets();
        for (int i = 0; i < chatOnStickerSets.size(); i++) {
            final String setName = chatOnStickerSets.get(i);
            final List<StickerInfo> stickers = stickerManager
                    .loadBgRemoverStickers(setName);
            if (!stickers.isEmpty()) {

                StickerFragment stickerFragment = new StickerFragment();
                stickerFragment.setOnImagePickListener(new OnImagePickListener() {
                    @Override
                    public void onImagePicked(Bitmap icon, StickerInfo info) {
                        stickerManager.update(info);
                        if(onImagePickListener!=null){
                            onImagePickListener.onImagePicked(icon,info);
                        }
                        dismiss();
                    }
                });

                stickerFragment.setInfos(stickers);
                fragments.add(stickerFragment);
                fragmentTitleList.add(setName);
                Bitmap bitmap = stickerManager.getStickerFromFile(stickers.get(0).getPath());
                Drawable d = new BitmapDrawable(getActivity().getResources(), bitmap);
                imageResId.add(d);
            }
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(),fragments,fragmentTitleList);
        viewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(imageResId.get(i));
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                    Utils.convertDpToPx(getContext(),40), Utils.convertDpToPx(getContext(),40));
            imageView.setLayoutParams(lp1);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            tabLayout.getTabAt(i).setCustomView(imageView);

            //TextView tabOne = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
          //  tabOne.setCompoundDrawablesWithIntrinsicBounds(null, imageResId.get(i), null, null);
            tabLayout.getTabAt(i).setCustomView(imageView);
            //tabLayout.getTabAt(i).setIcon(imageResId.get(i));
        }
        return root;

    }



}
