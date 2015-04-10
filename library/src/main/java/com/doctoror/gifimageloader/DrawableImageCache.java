package com.doctoror.gifimageloader;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.util.LruCache;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Default {@link LruCache} and {@link GifImageLoader.ImageCache} implementation
 */
public class DrawableImageCache extends LruCache<String, Drawable> implements
        GifImageLoader.ImageCache {

    public DrawableImageCache(final int maxSize) {
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
        } else if (value instanceof GifDrawable) {
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