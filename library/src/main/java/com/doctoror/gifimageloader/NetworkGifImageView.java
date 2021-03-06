/*
 * Copyright (C) 2011 The Android Open Source Project
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import pl.droidsonroids.gif.GifImageView;

/**
 * Similar to {@link NetworkImageView} but extends {@link GifImageView} and works with {@link GifImageLoader}
 */
public class NetworkGifImageView extends GifImageView {

    /** The url of the network image to load */
    private String mImageUrl;

    /**
     * Resource ID of the image to be used as a placeholder until the network image is loaded.
     */
    private int mDefaultImageId;

    /**
     * Resource ID of the image to be used if the network response fails.
     */
    private int mErrorImageId;

    /** Local copy of the ImageLoader. */
    private GifImageLoader mImageLoader;

    /** Current ImageContainer. (either in-flight or finished) */
    private GifImageLoader.ImageContainer mImageContainer;

    public NetworkGifImageView(Context context) {
        super(context);
    }

    public NetworkGifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkGifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NetworkGifImageView(final Context context, final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Sets URL of the image that should be loaded into this view. Note that calling this will
     * immediately either set the cached image (if available) or the default image specified by
     * {@link NetworkGifImageView#setDefaultImageResId(int)} on the view.
     *
     * NOTE: If applicable, {@link NetworkGifImageView#setDefaultImageResId(int)}
     * and
     * {@link NetworkGifImageView#setErrorImageResId(int)} should be called prior
     * to
     * calling
     * this function.
     *
     * @param url   The url of the image that should be loaded into this ImageView.
     * @param imageLoader ImageLoader that will be used to make the request.
     */
    public void setImageUrl(@Nullable final String url,
            @Nullable final GifImageLoader imageLoader) {
        mImageUrl = url;
        mImageLoader = imageLoader;
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
    }

    /**
     * Sets the default image resource ID to be used for this view until the attempt to load it
     * completes.
     */
    public void setDefaultImageResId(@DrawableRes final int defaultImage) {
        mDefaultImageId = defaultImage;
    }

    /**
     * Sets the error image resource ID to be used for this view in the event that the image
     * requested fails to load.
     */
    public void setErrorImageResId(@DrawableRes final int errorImage) {
        mErrorImageId = errorImage;
    }

    /**
     * Loads the image for the view if it isn't already loaded.
     */
    void loadImageIfNecessary(final boolean isInLayoutPass) {
        // if the URL to be loaded in this view is empty, cancel any old requests and clear the
        // currently loaded image.
        if (isNullOrEmpty(mImageUrl)) {
            if (mImageContainer != null) {
                mImageContainer.cancelRequest();
                mImageContainer = null;
            }
            setDefaultImageOrNull();
            return;
        }

        final int width = getWidth();
        final int height = getHeight();

        boolean wrapWidth = false, wrapHeight = false;
        if (getLayoutParams() != null) {
            wrapWidth = getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT;
            wrapHeight = getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        // if the view's bounds aren't known yet, and this is not a wrap-content/wrap-content
        // view, hold off on loading the image.
        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }

        // Calculate the max image width / height to use while ignoring WRAP_CONTENT dimens.
        int maxWidth = wrapWidth ? 0 : width;
        int maxHeight = wrapHeight ? 0 : height;

        // if there was an old request in this view, check if it needs to be canceled.
        if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
            if (mImageContainer.getRequestUrl().equals(mImageUrl)) {
                // if the request is from the same URL, return.
                return;
            } else {
                // if there is a pre-existing request, cancel it if it's fetching a different URL.
                mImageContainer.cancelRequest();
                if (!mImageLoader.isCached(mImageUrl, maxWidth, maxHeight)) {
                    setDefaultImageOrNull();
                }
            }
        }

        // The pre-existing content of this view didn't match the current URL. Load the new image
        // from the network.
        mImageContainer = mImageLoader.get(mImageUrl,
                new GifImageLoader.ImageListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        if (mErrorImageId != 0) {
                            setImageResource(mErrorImageId);
                        }
                    }

                    @Override
                    public void onResponse(final GifImageLoader.ImageContainer response,
                            final boolean isImmediate) {
                        // If this was an immediate response that was delivered inside of a layout
                        // pass do not set the image immediately as it will trigger a requestLayout
                        // inside of a layout. Instead, defer setting the image by posting back to
                        // the main thread.
                        if (isImmediate && isInLayoutPass) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    onResponse(response, false);
                                }
                            });
                            return;
                        }

                        onLoadFinished(response.getImage(), response.getRequestUrl());
                    }
                }, maxWidth, maxHeight);
    }

    private void setDefaultImageOrNull() {
        if (mDefaultImageId != 0) {
            setImageResource(mDefaultImageId);
        } else {
            setImageDrawable(null);
        }
    }

    @Override
    protected void onLayout(final boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mImageContainer != null) {
            // If the view was bound to an image request, cancel it and clear
            // out the image from the view.
            mImageContainer.cancelRequest();
            setImageDrawable(null);
            // also clear out the container so we can reload the image if necessary.
            mImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    private void onLoadFinished(@Nullable final Drawable data,
            @NonNull final String imageUrl) {
        if (imageUrl.equals(mImageUrl)) {
            if (data == null) {
                if (mErrorImageId != 0) {
                    setImageResource(mErrorImageId);
                } else {
                    setImageDrawable(null);
                }
            } else {
                setImageDrawable(data);
            }
        }
    }

    private boolean isNullOrEmpty(@Nullable final String string) {
        return string == null || string.length() == 0;
    }
}
