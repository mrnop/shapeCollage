package com.isarainc.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.isarainc.shapecollage.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public interface LoadingTaskFinishedListener {
	void onTaskFinished();


}
