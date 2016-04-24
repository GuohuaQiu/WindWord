package qiu.tool.windword;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ScrollitemView extends View{

    private int X = 10;
    private int Y = 20;
    private int H = 32;
    private final int NUMBER = 5;
    private Paint mPaintA = null;
    private Paint mPaintB = null;

    private int totalNumber = 0;

    private String mText[] = new String[NUMBER];
    private boolean madd[] = new boolean[NUMBER];

    public ScrollitemView(Context context) {
        super(context);
        init();
        // TODO Auto-generated constructor stub
    }


    public ScrollitemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaintA = new Paint();
        mPaintB = new Paint();

        mPaintA.setColor(Color.GRAY);
        mPaintA.setTextSize(28);
        mPaintA.setAntiAlias(true);
        mPaintB.setColor(Color.GREEN);
        mPaintB.setTextSize(28);
        mPaintB.setAntiAlias(true);
        for (int i = 0; i < NUMBER; i++) {
            mText[i] = new String("");
            madd[i] = false;
        }
    }

    public void AddString(String item, boolean added){
        for(int i = 0;i< (NUMBER - 1);i++){
            mText[i] = mText[i+1];
            madd[i] = madd[i+1];
        }
        mText[NUMBER - 1] = item;
        madd[NUMBER - 1] = added;
        Log.i("ScrollitemView","new item "+ item );
        totalNumber++;
        invalidate();
    }
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
        setMeasuredDimension(150, H*(NUMBER + 1) + Y);
    }
    private void drawItem(Canvas canvas,int index){
        if(mText[index] != null){
            canvas.drawText(mText[index], X, Y + index * H, madd[index]? mPaintA : mPaintB);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(int i = 0;i< NUMBER;i++){
            drawItem(canvas,i);
        }
        canvas.drawText(""+totalNumber, X, Y + NUMBER * H, mPaintB);
    }

}
