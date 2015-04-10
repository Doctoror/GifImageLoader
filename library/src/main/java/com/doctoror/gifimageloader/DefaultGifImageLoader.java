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

import android.content.Context;
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
        super(Volley.newRequestQueue(context), SingletonDrawableImageCache.getInstance(cacheSize));
        mCacheSize = cacheSize;
    }

    private static final class SingletonDrawableImageCache extends DrawableImageCache {

        private static SingletonDrawableImageCache sInstance;

        /**
         * Returns an instance of {@link SingletonDrawableImageCache}.
         * If cacheSize differs from existing instance's cache size new instance is returned
         *
         * @param cacheSize maxSize for the LruCache in bytes
         * @return The {@link SingletonDrawableImageCache} which maxSize is equal to cacheSize
         */
        static synchronized SingletonDrawableImageCache getInstance(final int cacheSize) {
            if (sInstance == null || sInstance.maxSize() != cacheSize) {
                sInstance = new SingletonDrawableImageCache(cacheSize);
            }
            return sInstance;
        }

        private SingletonDrawableImageCache(final int maxSize) {
            super(maxSize);
        }
    }
}
