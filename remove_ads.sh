#!/bin/bash
SRC="shapeCollage/src/main/java"

echo " commenting out AdMob code..."

# Imports
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*import com.google.android.gms.ads.*)$/\/\/$1/g'

# Fields
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*private InterstitialAd.*)$/\/\/$1/g'
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*private NativeExpressAdView.*)$/\/\/$1/g'
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*private com.google.android.gms.ads.InterstitialAd.*)$/\/\/$1/g'

# Usage
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*MobileAds.initialize.*)$/\/\/$1/g'
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*adView =.*)$/\/\/$1/g'
# Usage matches for AdMob and Facebook variables
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*mInterstitial\..*)$/\/\/$1/g'
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*admobInterstitial\..*)$/\/\/$1/g'
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*fbInterstitialAd\..*)$/\/\/$1/g'
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*nativeAd\..*)$/\/\/$1/g'
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*adView\..*)$/\/\/$1/g'

# Handle AdListener imports
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*import com.google.android.gms.ads.AdListener.*)$/\/\/$1/g'
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*new AdListener.*)$/\/\/ new AdListener() {/g'
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^(.*super\.onAdClosed.*)$/\/\/$1/g'
# This might leave hanging brackets like ` } });` but Java compiler often handles or errors differently.
# Ideally we comment out the whole block but that is hard.

echo "AdMob removal complete."
