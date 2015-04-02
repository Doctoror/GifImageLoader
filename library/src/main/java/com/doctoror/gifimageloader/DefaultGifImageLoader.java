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
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;

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

    private static final class ImageLruCache extends LruCache<String, Image> implements
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
        public Image getImage(final String url) {
            return get(url);
        }

        @Override
        public void putImage(final String url, final Image image) {
            put(url, image);
        }

        @SuppressLint("NewApi")
        @Override
        protected int sizeOf(final String key, final Image value) {
            if (value.bitmap != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    return value.bitmap.getByteCount();
                } else {
                    return value.bitmap.getRowBytes() * value.bitmap.getHeight();
                }
            }
            if (value.movie != null) {
                // TODO Bad way to determine Movie size. Is there a better way?
                return (int) (value.movie.width() * value.movie.height() * Math
                        .max((double) value.movie.duration() / 1000d, 1d));
            }
            return super.sizeOf(key, value);
        }
    }
}
