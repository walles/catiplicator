/*
 *
 *  * Copyright 2018, Johan Walles <johan.walles@gmail.com>
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.gmail.walles.johan.catiplicator;

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
        // Get the scale
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
