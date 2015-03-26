package com.doctoror.gifimageloader.sample;

public interface AspectRatioWidget {

    void setAspect(float aspect);

    void setAspectType(int aspectType);

    void superOnMeasure(int widthMeasureSpec, int heightMeasureSpec);

    void requestLayout();

    int getMeasuredWidth();

    int getMeasuredHeight();
}
