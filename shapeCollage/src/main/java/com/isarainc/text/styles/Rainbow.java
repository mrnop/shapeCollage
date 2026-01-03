package com.isarainc.text.styles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

/**
 * Created by mrnop on 8/20/2015 AD.
 */
public class Rainbow extends TextStyle {
    private Paint mTextPaint = new Paint();
    private int[] getRainbowColors() {
        return new int[] {Color.RED,
                Color.YELLOW,
                Color.GREEN,
                Color.BLUE,
                Color.MAGENTA
        };
    }
    @Override
    public int colorCount() {
        return 0;
    }

    @Override
    protected void prepare(Canvas canvas, String text) {
        int[] rainbow = getRainbowColors();
        mTextPaint.setTypeface(getTypeface());
        mTextPaint.setAntiAlias(true);


        mTextPaint.setTextSize(this.getSize());
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTextPaint.setStyle(Paint.Style.FILL);
        Shader shader = new LinearGradient(0, 0, 0, getBound().width(), rainbow,
                null, Shader.TileMode.MIRROR);

        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        shader.setLocalMatrix(matrix);

        mTextPaint.setShader(shader);
    }

    @Override
    protected void drawText(Canvas canvas, Path path, String text) {
        canvas.drawTextOnPath(text, path, 0, 1, mTextPaint);
    }

    @Override
    protected void drawText(Canvas canvas, int x, int y, String text) {
        canvas.drawText(text, x,y, mTextPaint);
    }
}
