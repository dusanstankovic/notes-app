package dev.dstankovic.notesapp.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

public class LinedEditText extends AppCompatEditText {

    private Rect mRect;
    private Paint mPaint;

    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(0xFFFFD966);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int viewHeight = ((View) this.getParent()).getHeight();
        int editTextLineHeight = getLineHeight();
        int numberOfLines = viewHeight / editTextLineHeight;

        Rect rect = mRect;
        Paint paint = mPaint;

        int baseline = getLineBounds(0, rect);

        for (int i = 0; i < numberOfLines; i++) {
            canvas.drawLine(rect.left, baseline + 1, rect.right, baseline + 1, paint);
            baseline += editTextLineHeight;
        }
        super.onDraw(canvas);
    }
}
