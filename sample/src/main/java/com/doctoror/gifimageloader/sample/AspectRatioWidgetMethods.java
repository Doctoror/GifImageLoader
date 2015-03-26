package com.doctoror.gifimageloader.sample;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;

public final class AspectRatioWidgetMethods {

    private static final int ASPECT_VERTICAL = 0;
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
