package com.isarainc.namecollage.collage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.isarainc.namecollage.HomeActivity;
import com.isarainc.namecollage.multichoiceadapter.MultiChoiceBaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.isarainc.shapecollage.R;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class CollagePickerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "CollagePickerActivity";

    private CollageManager collageManager;

    private List<String> collages;
    private ArrayList<String> selected = new ArrayList<String>();

    private GridView mGrid;
    private CollagePickListener collagePickListener;

    private CollageAdapter adapter;

    public interface CollagePickListener {
        void onCollagePicked(List<String> collages);
    }

    public CollagePickListener getCollagePickListener() {
        return collagePickListener;
    }

    public void setCollagePickListener(CollagePickListener collagePickListener) {
        this.collagePickListener = collagePickListener;
    }

    public class CollageAdapter extends MultiChoiceBaseAdapter {

        public CollageAdapter(Bundle savedInstanceState) {

            super(savedInstanceState);

        }

        public View getViewImpl(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                int layout = R.layout.i_grid;
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
            }
            ImageView imageView = (ImageView) convertView;
            String collage = collages.get(position);
            imageView.setImageBitmap(collageManager.loadFlowerImage(collage));
            return imageView;

        }

        public final int getCount() {
            return collages.size();
        }

        public final Object getItem(int position) {
            return collages.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.my_action_mode, menu);
            MenuItem selectAll = menu.findItem(R.id.menu_select);
            // set your desired icon here based on a flag if you like
            selectAll.setIcon(new IconicsDrawable(getContext())
                    .icon(GoogleMaterial.Icon.gmd_check)
                    .color(Color.WHITE)
                    .sizeDp(24));
            MenuItem resetItem = menu.findItem(R.id.menu_discard);
            // set your desired icon here based on a flag if you like
            resetItem.setIcon(new IconicsDrawable(getContext())
                    .icon(GoogleMaterial.Icon.gmd_close)
                    .color(Color.WHITE)
                    .sizeDp(24));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.menu_select) {
                // Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();
                List<Long> positions = new ArrayList<Long>(getCheckedItems());
                Collections.sort(positions, Collections.reverseOrder());
                for (long position : positions) {
                    selected.add(collages.get((int) position));
                }
                finishActionMode();
                Intent returnIntent = new Intent();
                returnIntent.putStringArrayListExtra("result", selected);
                setResult(RESULT_OK, returnIntent);
                finish();
                return true;
            }
            if (item.getItemId() == R.id.menu_discard) {
                discardSelectedItems();
                return true;
            }
            return false;
        }

        private void discardSelectedItems() {
            // http://stackoverflow.com/a/4950905/244576
            List<Long> positions = new ArrayList<Long>(getCheckedItems());
            Collections.sort(positions, Collections.reverseOrder());
            for (long position : positions) {
                // selected.remove((int) position);
            }
            finishActionMode();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collageManager = CollageManager.getInstance(this);
        collages = collageManager.listFlowers();
        rebuildList(savedInstanceState);
    }

    private GridView getGridView() {
        return (GridView) findViewById(R.id.myGrid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        MenuItem selectAll = menu.findItem(R.id.menu_select_all);
        // set your desired icon here based on a flag if you like
        selectAll.setIcon(new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_check_all)
                .color(Color.WHITE)
                .sizeDp(24));
        MenuItem resetItem = menu.findItem(R.id.menu_reset_list);
        // set your desired icon here based on a flag if you like
        resetItem.setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_close)
                .color(Color.WHITE)
                .sizeDp(24));
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selected.add(collages.get(position));
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("result", selected);
        setResult(RESULT_OK, returnIntent);
        finish();
        // Toast.makeText(this, "Item click: " + collages.get(position),
        // Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Intent parentActivityIntent = new Intent(this, HomeActivity.class);
            // parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
            // Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(parentActivityIntent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_select_all) {
            selectAll();
            return true;
        } else if (item.getItemId() == R.id.menu_reset_list) {
            rebuildList(null);
            return true;
        }
        return false;
    }

    private void selectAll() {
        for (int i = 0; i < adapter.getCount(); ++i) {
            adapter.setItemChecked(i, true);
        }
    }

    private void rebuildList(Bundle savedInstanceState) {
        adapter = new CollageAdapter(savedInstanceState);
        adapter.setOnItemClickListener(this);
        adapter.setAdapterView(getGridView());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        adapter.save(outState);
    }
}
