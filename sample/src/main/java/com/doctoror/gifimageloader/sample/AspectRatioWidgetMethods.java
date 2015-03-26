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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;

/**
 * Adds support of preserving aspect ratio to widgets
 */
public final class AspectRatioWidgetMethods {

    /**
     * The widget's height will be matched to width
     */
    private static final int ASPECT_VERTICAL = 0;

    /**
     * The widget's width will be matched to height
     */
    private static final int ASPECT_HORIZONTAL = 1;

    private final AspectRatioWidget view;

    private float aspect;
    private int aspectType;

    public AspectRatioWidgetMethods(final AspectRatioWidget view) {
        this.view = view;
        this.aspect = -1;
    }

    public AspectRatioWidgetMethods(final AspectRatioWidget view, Context context,
            AttributeSet attrs) {
        this.view = view;

        if (attrs != null) {
            final TypedArray arr = context
                    .obtainStyledAttributes(attrs, R.styleable.AspectRatioView);
            if (arr != null) {
                this.aspect = arr.getFloat(R.styleable.AspectRatioView_customAspect, -1f);
                this.aspectType = arr.getInt(R.styleable.AspectRatioView_aspectType, 0);
                arr.recycle();
            }
        }
    }

    public void setAspect(float aspect) {
        if (aspect != this.aspect) {
            this.aspect = aspect;
            this.view.requestLayout();
        }
    }

    public void setAspectType(int aspectType) {
        if (aspectType != ASPECT_VERTICAL && aspectType != ASPECT_HORIZONTAL) {
            throw new IllegalArgumentException("Invalid aspect type provided");
        }
        if (this.aspectType != aspectType) {
            this.aspectType = aspectType;
            this.view.requestLayout();
        }
    }

    public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        this.view.superOnMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.aspect != -1f) {

            final int mw = this.view.getMeasuredWidth();
            final int mh = this.view.getMeasuredHeight();

            switch (this.aspectType) {
                case ASPECT_VERTICAL:
                    //this.setMeasuredDimension(mw, (int) (mw * this.aspect));

                    this.view.superOnMeasure(MeasureSpec.makeMeasureSpec(mw, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec((int) (mw * this.aspect),
                                    MeasureSpec.EXACTLY));
                    break;

                case ASPECT_HORIZONTAL:
                    //this.setMeasuredDimension((int) (mh * this.aspect), mh);
                    this.view.superOnMeasure(MeasureSpec
                            .makeMeasureSpec((int) (mh * this.aspect), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(mh, MeasureSpec.EXACTLY));
                    break;

                default:
                    throw new IllegalArgumentException("Invalid aspect type");
            }
        }
    }

}
