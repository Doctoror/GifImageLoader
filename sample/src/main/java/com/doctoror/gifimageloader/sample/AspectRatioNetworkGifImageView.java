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