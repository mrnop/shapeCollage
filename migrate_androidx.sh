#!/bin/bash
# Migrate common Android support libraries to AndroidX

# Define the root of source code
SRC_DIR="/Volumes/Data/Dev/ShapeCollage"

# Function to replace text recursively
replace_text() {
    local search="$1"
    local replace="$2"
    echo "Replacing $search with $replace..."
    find "$SRC_DIR" -name "*.java" -print0 | xargs -0 sed -i '' "s/$search/$replace/g"
}

# App Components
replace_text "android.support.v7.app.AppCompatActivity" "androidx.appcompat.app.AppCompatActivity"
replace_text "android.support.v4.app.Fragment" "androidx.fragment.app.Fragment"
replace_text "android.support.v4.app.DialogFragment" "androidx.fragment.app.DialogFragment"
replace_text "android.support.v4.app.FragmentManager" "androidx.fragment.app.FragmentManager"
replace_text "android.support.v4.app.FragmentTransaction" "androidx.fragment.app.FragmentTransaction"
replace_text "android.support.v4.app.FragmentActivity" "androidx.fragment.app.FragmentActivity"
replace_text "android.support.v4.app.ListFragment" "androidx.fragment.app.ListFragment"
replace_text "android.support.v7.app.ActionBar" "androidx.appcompat.app.ActionBar"
replace_text "android.support.v7.app.AlertDialog" "androidx.appcompat.app.AlertDialog"
replace_text "android.support.v4.app.ActivityCompat" "androidx.core.app.ActivityCompat"
replace_text "android.support.v4.app.NotificationCompat" "androidx.core.app.NotificationCompat"

# Widgets
replace_text "android.support.v7.widget.RecyclerView" "androidx.recyclerview.widget.RecyclerView"
replace_text "android.support.v7.widget.CardView" "androidx.cardview.widget.CardView"
replace_text "android.support.v7.widget.Toolbar" "androidx.appcompat.widget.Toolbar"
replace_text "android.support.v7.widget.AppCompatImageView" "androidx.appcompat.widget.AppCompatImageView"
replace_text "android.support.v7.widget.LinearLayoutManager" "androidx.recyclerview.widget.LinearLayoutManager"
replace_text "android.support.v7.widget.GridLayoutManager" "androidx.recyclerview.widget.GridLayoutManager"
replace_text "android.support.v7.widget.StaggeredGridLayoutManager" "androidx.recyclerview.widget.StaggeredGridLayoutManager"

# Core/Utils
replace_text "android.support.annotation.NonNull" "androidx.annotation.NonNull"
replace_text "android.support.annotation.Nullable" "androidx.annotation.Nullable"
replace_text "android.support.v4.content.ContextCompat" "androidx.core.content.ContextCompat"
replace_text "android.support.v4.content.FileProvider" "androidx.core.content.FileProvider"
replace_text "android.support.v4.view.ViewPager" "androidx.viewpager.widget.ViewPager"
replace_text "android.support.v4.view.PagerAdapter" "androidx.viewpager.widget.PagerAdapter"
replace_text "android.support.v4.view.GravityCompat" "androidx.core.view.GravityCompat"
replace_text "android.support.v4.widget.DrawerLayout" "androidx.drawerlayout.widget.DrawerLayout"
replace_text "android.support.v4.util.LruCache" "androidx.collection.LruCache"

# Design
replace_text "android.support.design.widget.FloatingActionButton" "com.google.android.material.floatingactionbutton.FloatingActionButton"
replace_text "android.support.design.widget.Snackbar" "com.google.android.material.snackbar.Snackbar"
replace_text "android.support.design.widget.TabLayout" "com.google.android.material.tabs.TabLayout"
replace_text "android.support.design.widget.NavigationView" "com.google.android.material.navigation.NavigationView"
replace_text "android.support.design.widget.CoordinatorLayout" "androidx.coordinatorlayout.widget.CoordinatorLayout"
replace_text "android.support.design.widget.AppBarLayout" "com.google.android.material.appbar.AppBarLayout"
replace_text "android.support.design.widget.CollapsingToolbarLayout" "com.google.android.material.appbar.CollapsingToolbarLayout"

echo "Migration complete."
