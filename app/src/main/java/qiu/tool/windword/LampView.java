package qiu.tool.windword;

import qiu.tool.windword.R;
import qiu.tool.windword.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class LampView extends View  {
    Paint mPaint = null;
    RectF mRects[] = null;
    boolean mPass[] = null;
    int  mIndex = 0;
    int  mCount = 0;

    public LampView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        setCount(10);
    }

    public LampView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCount(10);
    }
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
        if (mCount <= 0) {
            setMeasuredDimension(23*30, 24);
        } else {
            setMeasuredDimension(mCount * 23, 24);
        }
    }
    public void setCount(int count){
        mCount = count;
        init();
    }
    private void init(){
        mPaint = new Paint();
        //设置绘制的颜色是红色
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);

        mRects = new RectF[mCount];
        mPass = new boolean[mCount];
        for(int i = 0;i < mCount;i++)
        {
            mRects[i] = new RectF();
            mRects[i].left = i* 20;
            mRects[i].right = mRects[i].left + 18;
            mRects[i].top = 0;
            mRects[i].bottom = 40;
        }
        setAll(false);
    }

    public void setAll(boolean pass){
        for(int i = 0;i < mCount;i++)
        {
            mPass[i] = pass;
        }

    }

    public void reset(){
        setAll(false);
        setIndex(0);
        invalidate();
    }

    public void setIndex(int index){
        mIndex = index;
        invalidate();
    }






    public void setPass(int index, boolean pass){


        mPass[index] = pass;
        invalidate();
    }

    public void setOngoing(int index) {

        mIndex = index;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bmpPass = BitmapFactory.decodeResource(getResources(), R.drawable.blue_icon);
        Bitmap bmpFail = BitmapFactory.decodeResource(getResources(), R.drawable.red_icon);
        Bitmap bmpTest = BitmapFactory.decodeResource(getResources(), R.drawable.testing);
        for (int i = 0; i < mCount; i++) {
            if (mIndex == i) {
                canvas.drawBitmap(bmpTest, i * 23, 0, null);
            } else {

                if (mPass[i]) {
                    canvas.drawBitmap(bmpPass, i * 23, 0, null);
                    // canvas.draw
                    // mPaint.setColor(Color.GREEN);
                } else {
                    canvas.drawBitmap(bmpFail, i * 23, 0, null);
                    // mPaint.setColor(Color.RED);
                }
            }
            // canvas.drawRoundRect(mRects[i], 9, 9, mPaint);
        }
    }

}
