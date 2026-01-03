#!/bin/bash
SRC="shapeCollage/src/main/java"

echo "Cleaning up leftover Ad fragments..."

# Fix orphan .build(); from shapeCollage/HomeActivity
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^\s*\.build\(\);/\/\/ .build();/g'

# Fix AdRequest.DEVICE_ID_EMULATOR).build(); pattern
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/AdRequest\.DEVICE_ID_EMULATOR\)\.build\(\);/\/\/ AdRequest.DEVICE_ID_EMULATOR).build();/g'

# Just in case: .addTestDevice(...).build() remaining line parts
find "$SRC" -name "*.java" -print0 | xargs -0 perl -pi -e 's/^\s*\.addTestDevice\(.*\)\.build\(\);/\/\/ .addTestDevice/g'

echo "Cleanup complete."
