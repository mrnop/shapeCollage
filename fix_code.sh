#!/bin/bash
SRC="shapeCollage/src/main/java"

# Function to replace text recursively
replace_text() {
    local search="$1"
    local replace="$2"
    echo "Replacing $search with $replace..."
    find "$SRC" -name "*.java" -print0 | xargs -0 sed -i '' "s/$search/$replace/g"
}

# Imports
replace_text "android.support.v4.view.MenuItemCompat" "androidx.core.view.MenuItemCompat"
replace_text "android.support.v7.widget.ShareActionProvider" "androidx.appcompat.widget.ShareActionProvider"
replace_text "android.support.v7.view.ActionMode" "androidx.appcompat.view.ActionMode"
replace_text "android.support.v4.widget.NestedScrollView" "androidx.core.widget.NestedScrollView"
replace_text "android.support.v4.widget.SimpleCursorAdapter" "androidx.cursoradapter.widget.SimpleCursorAdapter"
replace_text "android.support.v7.app.AppCompatDelegate" "androidx.appcompat.app.AppCompatDelegate"
replace_text "com.crashlytics.android.Crashlytics" ""
replace_text "io.fabric.sdk.android.Fabric" ""

# Picasso: with(context) -> get()
# We use perl for regex replacement to handle parenthesis content
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/Picasso\.with\([^)]+\)/Picasso.get()/g'

# Fabric Removal
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/Fabric\.getLogger\(\).*//g'

# Fix R.string.saving_image usage if causing issues? No, leave it.

echo "Code fix script complete."
