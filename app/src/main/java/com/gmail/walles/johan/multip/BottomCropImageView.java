package com.gmail.walles.johan.multip;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * ImageView to display bottom-crop scale of an image view.
 *
 * From: https://stackoverflow.com/a/19163543/473672
 */
public class BottomCropImageView extends android.support.v7.widget.AppCompatImageView {

    public BottomCropImageView(Context context, AttributeSet st) {
        super(context, st);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        if (getDrawable() == null)
            return super.setFrame(l, t, r, b);

        Matrix matrix = getImageMatrix();

        float scale;
        int viewWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int drawableWidth = getDrawable().getIntrinsicWidth();
        int drawableHeight = getDrawable().getIntrinsicHeight();
        //Get the scale
        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        //Define the rect to take image portion from
        RectF drawableRect = new RectF(0, drawableHeight - (viewHeight / scale), drawableWidth, drawableHeight);
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.FILL);

        setImageMatrix(matrix);

        return super.setFrame(l, t, r, b);
    }
}
