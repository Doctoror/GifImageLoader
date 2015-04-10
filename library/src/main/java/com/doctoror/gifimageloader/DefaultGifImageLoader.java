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

import com.android.volley.toolbox.Volley;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;

import pl.droidsonroids.gif.GifDrawable;

/**
 * The default {@link GifImageLoader} as a singleton with LruCache
 */
public final class DefaultGifImageLoader extends GifImageLoader {

    private static final Object LOCK = new Object();

    private static DefaultGifImageLoader sInstance;

    /**
     * Returns an instance of {@link DefaultGifImageLoader}.
     * If cacheSize differs from existing instance's cache size new instance is returned.
     *
     * @param cacheSize maxSize for the LruCache in bytes
     * @return The {@link DefaultGifImageLoader} which has {@link LruCache} with maxSize equal to
     * cacheSize
     */
    @NonNull
    public static DefaultGifImageLoader getInstance(@NonNull final Context context,
            final int cacheSize) {
        if (sInstance == null || sInstance.mCacheSize != cacheSize) {
            synchronized (LOCK) {
                if (sInstance == null || sInstance.mCacheSize != cacheSize) {
                    sInstance = new DefaultGifImageLoader(context.getApplicationContext(),
                            cacheSize);
                }
            }
        }
        return sInstance;
    }

    private final int mCacheSize;

    private DefaultGifImageLoader(@NonNull final Context context, final int cacheSize) {
        super(Volley.newRequestQueue(context), ImageLruCache.getInstance(cacheSize));
        mCacheSize = cacheSize;
    }

    private static final class ImageLruCache extends LruCache<String, Drawable> implements
            ImageCache {

        private static ImageLruCache sInstance;

        /**
         * Returns an instance of {@link ImageLruCache}.
         * If cacheSize differs from existing instance's cache size new instance is returned
         *
         * @param cacheSize maxSize for the LruCache in bytes
         * @return The {@link ImageLruCache} which maxSize is equal to cacheSize
         */
        public static synchronized ImageLruCache getInstance(final int cacheSize) {
            if (sInstance == null || sInstance.maxSize() != cacheSize) {
                sInstance = new ImageLruCache(cacheSize);
            }
            return sInstance;
        }

        private ImageLruCache(final int maxSize) {
            super(maxSize);
        }

        @Override
        public Drawable getImage(final String url) {
            return get(url);
        }

        @Override
        public void putImage(final String url, final Drawable image) {
            put(url, image);
        }

        @SuppressLint("NewApi")
        @Override
        protected int sizeOf(final String key, final Drawable value) {
            if (value instanceof BitmapDrawable) {
                final Bitmap bitmap = ((BitmapDrawable) value).getBitmap();
                if (bitmap != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                        return bitmap.getByteCount();
                    } else {
                        return bitmap.getRowBytes() * bitmap.getHeight();
                    }
                }
            }
            if (value instanceof GifDrawable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return (int) ((GifDrawable) value).getAllocationByteCount();
                }
                final long length = ((GifDrawable) value).getInputSourceByteCount();
                if (length != -1) {
                    return (int) length;
                }
                return (((GifDrawable) value).getFrameByteCount() * ((GifDrawable) value)
                        .getNumberOfFrames());
            }
            return super.sizeOf(key, value);
        }
    }
}
