package cn.life3t.life3t.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by wuguohao on 15-7-2.
 */
public class IrregularityImageView extends ImageView
{
    private int width = -1;
    private int height = -1;
    private Bitmap bitmap;

    public IrregularityImageView(Context context) {
        super(context);
    }

    public IrregularityImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IrregularityImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent( event);
        }

        int x = (int)event.getX();
        int y = (int)event.getY();
        if(width == -1 || height == -1) {
//            Drawable drawable = ((StateListDrawable)getBackground()).getCurrent();
            Drawable drawable = getDrawable();
            bitmap = ((BitmapDrawable)drawable).getBitmap();
            width = getWidth();
            height = getHeight();
        }

        if(null == bitmap || x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }

        int pixel = bitmap.getPixel( x, y);

        if(Color.TRANSPARENT == pixel) {
            return false;
        }

        return super.onTouchEvent( event);
    }
}
