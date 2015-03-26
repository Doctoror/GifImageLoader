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

package com.doctoror.gifimageloader.sample;

import com.doctoror.gifimageloader.NetworkGifImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

/**
 * {@link NetworkGifImageView} as {@link AspectRatioWidget}
 */
public class AspectRatioNetworkGifImageView extends NetworkGifImageView
        implements AspectRatioWidget {

    private final AspectRatioWidgetMethods methods;


    public AspectRatioNetworkGifImageView(Context context) {
        super(context);
        this.methods = new AspectRatioWidgetMethods(this);
    }

    public AspectRatioNetworkGifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.methods = new AspectRatioWidgetMethods(this, context, attrs);
    }

    public AspectRatioNetworkGifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.methods = new AspectRatioWidgetMethods(this, context, attrs);
    }

    @Override
    public void setAspect(final float aspect) {
        this.methods.setAspect(aspect);
    }

    @Override
    public void setAspectType(final int aspectType) {
        this.methods.setAspectType(aspectType);
    }

    @SuppressLint("WrongCall")
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        this.methods.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("WrongCall")
    @Override
    public void superOnMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}