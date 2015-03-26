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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * {@link ImageView} that supports animated gif
 *
 * Some ideas are based on com.abhi.gif.lib.AnimatedGifImageView which can be found at
 * https://code.google.com/p/giflib/
 */
public class GifImageView extends ImageView {

    private BitmapDrawable mNullBitmapDrawable;

    private Matrix mDrawMatrix;
    private Matrix mMatrix = new Matrix();

    // Avoid allocations...
    private RectF mTempSrc = new RectF();
    private RectF mTempDst = new RectF();

    private boolean mAnimatedGifImage;

    private Movie mMovie;
    private long mMovieStart;

    private final Rect mMovieBounds = new Rect();

    public GifImageView(final Context context) {
        super(context);
        init(context);
    }

    public GifImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GifImageView(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GifImageView(final Context context, final AttributeSet attrs, final int defStyleAttr,
            final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context) {
        mNullBitmapDrawable = new BitmapDrawable(context.getResources(), (Bitmap) null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setAnimatedGif(final byte[] byteArray) {
        try {
            setAnimatedGif(Movie.decodeByteArray(byteArray, 0, byteArray.length));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAnimatedGif(final Movie movie) {
        super.setImageDrawable(mNullBitmapDrawable);
        mAnimatedGifImage = true;
        mMovie = movie;
        configureBounds();
        requestLayout();
        invalidate();
    }

    @Override
    public void setImageResource(final int resId) {
        mAnimatedGifImage = false;
        super.setImageResource(resId);
    }

    @Override
    public void setImageURI(final Uri uri) {
        mAnimatedGifImage = false;
        super.setImageURI(uri);
    }

    @Override
    public void setImageDrawable(@Nullable final Drawable drawable) {
        mAnimatedGifImage = false;
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(@Nullable final Bitmap bm) {
        mAnimatedGifImage = false;
        super.setImageBitmap(bm);
    }

    @Override
    protected boolean setFrame(final int l, final int t, final int r, final int b) {
        final boolean result = super.setFrame(l, t, r, b);
        configureBounds();
        return result;
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //configureBounds();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        if (mMovie != null) {
            int w = mMovie.width();
            int h = mMovie.height();

            // Desired aspect ratio of the view's contents (not including padding)
            float desiredAspect = 0.0f;

            // We are allowed to change the view's width
            boolean resizeWidth = false;

            // We are allowed to change the view's height
            boolean resizeHeight = false;

            final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

            if (w <= 0) {
                w = 1;
            }
            if (h <= 0) {
                h = 1;
            }

            // We are supposed to adjust view bounds to match the aspect
            // ratio of our drawable. See if that is possible.
            if (ImageViewCompat.getAdjustViewBounds(this)) {
                resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
                resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;

                desiredAspect = (float) w / (float) h;
            }

            int pleft = getPaddingLeft();
            int pright = getPaddingLeft();
            int ptop = getPaddingTop();
            int pbottom = getPaddingBottom();

            int widthSize;
            int heightSize;

            if (resizeWidth || resizeHeight) {
            /* If we get here, it means we want to resize to match the
                drawables aspect ratio, and we have the freedom to change at
                least one dimension.
            */

                // Get the max possible width given our constraints
                widthSize = resolveAdjustedSize(w + pleft + pright, ImageViewCompat.getMaxWidth(
                        this), widthMeasureSpec);

                // Get the max possible height given our constraints
                heightSize = resolveAdjustedSize(h + ptop + pbottom, ImageViewCompat.getMaxHeight(
                        this), heightMeasureSpec);

                if (desiredAspect != 0.0f) {
                    // See what our actual aspect ratio is
                    float actualAspect = (float) (widthSize - pleft - pright) /
                            (heightSize - ptop - pbottom);

                    if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {

                        boolean done = false;

                        // Try adjusting width to be proportional to height
                        if (resizeWidth) {
                            int newWidth = (int) (desiredAspect * (heightSize - ptop - pbottom)) +
                                    pleft + pright;

                            // Allow the width to outgrow its original estimate if height is fixed.
                            if (!resizeHeight && !ImageViewCompat.getAdjustViewBounds(this)) {
                                widthSize = resolveAdjustedSize(newWidth,
                                        ImageViewCompat.getMaxWidth(
                                                this),
                                        widthMeasureSpec);
                            }

                            if (newWidth <= widthSize) {
                                widthSize = newWidth;
                                done = true;
                            }
                        }

                        // Try adjusting height to be proportional to width
                        if (!done && resizeHeight) {
                            int newHeight = (int) ((widthSize - pleft - pright) / desiredAspect) +
                                    ptop + pbottom;

                            // Allow the height to outgrow its original estimate if width is fixed.
                            if (!resizeWidth && !ImageViewCompat.getAdjustViewBounds(this)) {
                                heightSize = resolveAdjustedSize(newHeight,
                                        ImageViewCompat.getMaxHeight(
                                                this),
                                        heightMeasureSpec);
                            }

                            if (newHeight <= heightSize) {
                                heightSize = newHeight;
                            }
                        }
                    }
                }
            } else {
            /* We are either don't want to preserve the drawables aspect ratio,
               or we are not allowed to change view dimensions. Just measure in
               the normal way.
            */
                w += pleft + pright;
                h += ptop + pbottom;

                w = Math.max(w, getSuggestedMinimumWidth());
                h = Math.max(h, getSuggestedMinimumHeight());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
                    heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
                } else {
                    widthSize = resolveSize(w, widthMeasureSpec);
                    heightSize = resolveSize(h, heightMeasureSpec);
                }
            }

            setMeasuredDimension(widthSize, heightSize);

        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private int resolveAdjustedSize(final int desiredSize,
            final int maxSize,
            final int measureSpec) {
        int result = desiredSize;
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /* Parent says we can be as big as we want. Just don't be larger
                   than max size imposed on ourselves.
                */
                result = Math.min(desiredSize, maxSize);
                break;
            case MeasureSpec.AT_MOST:
                // Parent says we can be as big as we want, up to specSize.
                // Don't be larger than specSize, and don't be larger than
                // the max size imposed on ourselves.
                result = Math.min(Math.min(desiredSize, specSize), maxSize);
                break;
            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

    private void configureBounds() {
        if (mMovie == null) {
            return;
        }

        int dwidth = mMovie.width();
        int dheight = mMovie.height();

        int vwidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int vheight = getHeight() - getPaddingTop() - getPaddingBottom();

        boolean fits = (dwidth < 0 || vwidth == dwidth) &&
                (dheight < 0 || vheight == dheight);

        if (dwidth <= 0 || dheight <= 0 || ScaleType.FIT_XY == getScaleType()) {
            /* If the drawable has no intrinsic size, or we're told to
                scaletofit, then we just fill our entire view.
            */
            mMovieBounds.set(0, 0, vwidth, vheight);
            mDrawMatrix = null;
        } else {
            // We need to do the scaling ourself, so have the drawable
            // use its native size.
            mMovieBounds.set(0, 0, dwidth, dheight);

            if (ScaleType.MATRIX == getScaleType()) {
                // Use the specified matrix as-is.
                if (mMatrix.isIdentity()) {
                    mDrawMatrix = null;
                } else {
                    mDrawMatrix = mMatrix;
                }
            } else if (fits) {
                // The bitmap fits exactly, no transform needed.
                mDrawMatrix = null;
            } else if (ScaleType.CENTER == getScaleType()) {
                // Center bitmap in view, no scaling.
//                mDrawMatrix = mMatrix;
//                mDrawMatrix.setScale(1f, 1f);
//                mDrawMatrix.postTranslate((int) ((vwidth - dwidth) * 0.5f + 0.5f),
//                        (int) ((vheight - dheight) * 0.5f + 0.5f));

                mDrawMatrix = mMatrix;
                final float scale = 1.0f;

                final float dx = (int) ((vwidth - dwidth * scale) * 0.5f + 0.5f);
                final float dy = (int) ((vheight - dheight * scale) * 0.5f + 0.5f);

                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate(dx, dy);

            } else if (ScaleType.CENTER_CROP == getScaleType()) {
                mDrawMatrix = mMatrix;

                float scale;
                float dx = 0, dy = 0;

                if (dwidth * vheight > vwidth * dheight) {
                    scale = (float) vheight / (float) dheight;
                    dx = (vwidth - dwidth * scale) * 0.5f;
                } else {
                    scale = (float) vwidth / (float) dwidth;
                    dy = (vheight - dheight * scale) * 0.5f;
                }

                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
            } else if (ScaleType.CENTER_INSIDE == getScaleType()) {
                mDrawMatrix = mMatrix;
                float scale;
                float dx;
                float dy;

                if (dwidth <= vwidth && dheight <= vheight) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float) vwidth / (float) dwidth,
                            (float) vheight / (float) dheight);
                }

                dx = (int) ((vwidth - dwidth * scale) * 0.5f + 0.5f);
                dy = (int) ((vheight - dheight * scale) * 0.5f + 0.5f);

                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate(dx, dy);
            } else {
                // Generate the required transform.
                mTempSrc.set(0, 0, dwidth, dheight);
                mTempDst.set(0, 0, vwidth, vheight);

                mDrawMatrix = mMatrix;
                mDrawMatrix
                        .setRectToRect(mTempSrc, mTempDst, scaleTypeToScaleToFit(getScaleType()));
            }
        }
    }

    private static final Matrix.ScaleToFit[] sS2FArray = {
            Matrix.ScaleToFit.FILL,
            Matrix.ScaleToFit.START,
            Matrix.ScaleToFit.CENTER,
            Matrix.ScaleToFit.END
    };

    private static Matrix.ScaleToFit scaleTypeToScaleToFit(@NonNull final ScaleType st) {
        // ScaleToFit enum to their corresponding Matrix.ScaleToFit values
        return sS2FArray[scaleTypeNativeInt(st) - 1];
    }

    private static int scaleTypeNativeInt(@NonNull final ScaleType scaleType) {
        try {
            final Field nativeIntField = ScaleType.class.getDeclaredField("nativeInt");
            nativeIntField.setAccessible(true);
            return (Integer) nativeIntField.get(scaleType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            switch (scaleType) {
                case MATRIX:
                    return 0;

                case FIT_XY:
                    return 1;

                case FIT_START:
                    return 2;

                case FIT_CENTER:
                    return 3;

                case FIT_END:
                    return 4;

                case CENTER:
                    return 5;

                case CENTER_CROP:
                    return 6;

                case CENTER_INSIDE:
                    return 7;

                default:
                    return 0;
            }
        }
    }

    @Override
    protected void onDraw(@NonNull final Canvas c) {
        super.onDraw(c);
        if (mAnimatedGifImage) {
            if (mMovie != null) {
                if (mMovie.width() == 0 || mMovie.height() == 0) {
                    return; // nothing to draw (empty bounds)
                }

                final long now = android.os.SystemClock.uptimeMillis();
                if (mMovieStart == 0) { // first time
                    mMovieStart = now;
                }
                int dur = mMovie.duration();
                if (dur == 0) {
                    dur = 1000;
                }
                int relTime = (int) ((now - mMovieStart) % dur);
                mMovie.setTime(relTime);

                if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
                    final int saveCount = c.getSaveCount();
                    c.save();

                    c.scale((float) c.getWidth() / (float) mMovie.width(),
                            (float) c.getHeight() / (float) mMovie.height());

                    mMovie.draw(c, mMovieBounds.left, mMovieBounds.top);
                    c.restoreToCount(saveCount);
                } else {
                    final int saveCount = c.getSaveCount();
                    c.save();

                    if (ImageViewCompat.getCropToPadding(this)) {
                        final int scrollX = getScrollX();
                        final int scrollY = getScrollY();
                        c.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                                scrollX + getRight() - getLeft() - getPaddingRight(),
                                scrollY + getBottom() - getTop() - getPaddingBottom());
                    }

                    c.translate(getPaddingLeft(), getPaddingTop());

                    if (mDrawMatrix != null) {
                        c.concat(mDrawMatrix);
                    }

                    mMovie.draw(c, mMovieBounds.left, mMovieBounds.top);
                    c.restoreToCount(saveCount);
                }
                invalidate();
            }
        }
    }
}
