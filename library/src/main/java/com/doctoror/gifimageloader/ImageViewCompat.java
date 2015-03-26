/*
 * Copyright (C) 2015 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doctoror.gifimageloader;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * Compatibility methods for ImageView
 */
public final class ImageViewCompat {

    /**
     * Backward compatible {@link ImageView#getCropToPadding()}
     *
     * @param view ImageView to call {@link ImageView#getCropToPadding()}
     * @return {@link ImageView#getCropToPadding()} of the target {@link ImageView} if API level >=
     * {@link Build.VERSION_CODES#JELLY_BEAN}, false for lower API's
     */
    @SuppressLint("NewApi")
    public static boolean getCropToPadding(@NonNull final ImageView view) {
        //noinspection SimplifiableIfStatement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return view.getCropToPadding();
        } else {
            return false;
        }
    }

    /**
     * Backward compatible {@link ImageView#getAdjustViewBounds()}
     *
     * @param view ImageView to call {@link ImageView#getAdjustViewBounds()}
     * @return {@link ImageView#getAdjustViewBounds()} of the target {@link ImageView} if API level
     * >=
     * {@link Build.VERSION_CODES#JELLY_BEAN}, false for lower API's
     */
    @SuppressLint("NewApi")
    public static boolean getAdjustViewBounds(@NonNull final ImageView view) {
        //noinspection SimplifiableIfStatement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return view.getAdjustViewBounds();
        } else {
            return false;
        }
    }

    /**
     * Backward compatible {@link ImageView#getMaxWidth()}
     *
     * @param view ImageView to call {@link ImageView#getMaxWidth()}
     * @return {@link ImageView#getMaxWidth()} of the target {@link ImageView} if API level >=
     * {@link Build.VERSION_CODES#JELLY_BEAN}. The value retrieved by reflection for lower API
     * levels. If reflection fails, Integer.MAX_VALUE returned
     */
    @SuppressLint("NewApi")
    public static int getMaxWidth(@NonNull final ImageView view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return view.getMaxWidth();
        }

        try {
            final Field maxWidthField = ImageView.class.getDeclaredField("mMaxWidth");
            maxWidthField.setAccessible(true);
            return (Integer) maxWidthField.get(view);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Backward compatible {@link ImageView#getMaxHeight()}
     *
     * @param view ImageView to call {@link ImageView#getMaxHeight()}
     * @return {@link ImageView#getMaxHeight()} of the target {@link ImageView} if API level >=
     * {@link Build.VERSION_CODES#JELLY_BEAN}. The value retrieved by reflection for lower API
     * levels. If reflection fails, Integer.MAX_VALUE returned
     */
    @SuppressLint("NewApi")
    public static int getMaxHeight(@NonNull final ImageView view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return view.getMaxHeight();
        }

        try {
            final Field maxHeightField = ImageView.class.getDeclaredField("mMaxHeight");
            maxHeightField.setAccessible(true);
            return (Integer) maxHeightField.get(view);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return Integer.MAX_VALUE;
        }
    }
}
