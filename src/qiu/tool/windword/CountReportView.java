
package qiu.tool.windword;

import qiu.tool.windword.WordLibAdapter.CountReportInfo;
import qiu.tool.windword.WordLibAdapter.LevelInfo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

//TODO: only draw the items in the screen.
//      if press in the title only move one dim.
public class CountReportView extends View implements OnGestureListener {
    private GestureDetector mGestureDetector;

    private final int SCROLL_V = 1;

    private final int SCROLL_H = 2;

    private final int SCROLL_BOTH = 4;

    private final int SCROLL_NONE = 0;

    private final static int MODE_DRAG = 0;

    private final static int MODE_SELECT = 1;

    private int mMode = MODE_DRAG;

    int scroll_start_row = 0;

    int scroll_start_col = 0;

    int scroll_start_row_last = 0;

    int scroll_start_col_last = 0;

    int touch_start_row = 0;

    int touch_start_col = 0;

    float start_x = 0;

    float start_y = 0;

    private int view_w;

    private int view_h;

    private int max_row;

    private int max_col;

    private Paint fillPaint = new Paint();

    Rect selectRect = new Rect();

    CountReportInfo mCountHashtable = null;

    Paint mPaint = null;

    Paint mHotPaint = null;

    Paint mRedPaint = null;

    Paint mBluePaint = null;

    Paint mDatePaint = null;
    Paint mCountPaint = null;

    int mCount = 0;

    final int OFF_Y = 10;

    final int lineH = 50;

    final int dateW = 180;

    final int dataW = 55;

    int levelCount = 0;

    final int W = 15 * dataW + dateW;

    String[] mDateArray = null;
    String[] mMonthArray = null;

    Paint selectPaint = new Paint();

    Paint normalPaint = new Paint();
    Paint monthPaint = new Paint();

    final static int SELECT_STATE_IDLE = -1;
    final static int SELECT_STATE_BEGIN = 0;
    final static int SELECT_STATE_GOING = 1;
    final static int SELECT_STATE_DONE = 2;

    private int selectState = SELECT_STATE_IDLE;

    Context context = null;

    public CountReportView(Context context) {
        super(context);
        this.context = context;
        init();
        // TODO Auto-generated constructor stub
    }

    public CountReportView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }

    private void init() {
        fillPaint.setColor(0xff008833);
        mPaint = new Paint();
        // 设置绘制的颜色是红色
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(40);
        mPaint.setColor(0xffffff00);

        mHotPaint = new Paint();
        // 设置绘制的颜色是红色
        mHotPaint.setAntiAlias(true);
        mHotPaint.setTextSize(40);
        mHotPaint.setColor(0xffff8800);

        mRedPaint = new Paint();
        // 设置绘制的颜色是红色
        mRedPaint.setAntiAlias(true);
        mRedPaint.setTextSize(40);
        mRedPaint.setColor(Color.RED);
        mBluePaint = new Paint();
        // 设置绘制的颜色是红色
        mBluePaint.setAntiAlias(true);
        mBluePaint.setTextSize(40);
        mBluePaint.setColor(0xff0055ff);

        selectPaint.setColor(Color.BLACK);
        selectPaint.setStyle(Paint.Style.STROKE);
        selectPaint.setStrokeWidth(8);
        normalPaint.setColor(Color.BLUE);
        normalPaint.setStyle(Paint.Style.STROKE);
        normalPaint.setStrokeWidth(8);

        mDatePaint = new Paint();
        // 设置绘制的颜色是红色
        mDatePaint.setAntiAlias(true);
        mDatePaint.setTextSize(40);
        mDatePaint.setColor(0xff88ff33);

        mCountPaint = new Paint();
        mCountPaint.setAntiAlias(true);
        mCountPaint.setTextSize(45);
        mCountPaint.setColor(0xffffff33);

        monthPaint = new Paint();
        monthPaint.setAntiAlias(true);
        monthPaint.setTextSize(55);
        monthPaint.setColor(0x8800ff63);

        mGestureDetector = new GestureDetector((OnGestureListener)this);
    }

    public void setHashTable(CountReportInfo hashTable) {
        mCountHashtable = hashTable;

        mCount = mCountHashtable.countHashtable.size();
        Set<String> count = mCountHashtable.countHashtable.keySet();
        mDateArray = new String[mCount];
        mDateArray = (String[])count.toArray(mDateArray);
        Arrays.sort(mDateArray);

        int month_number = 0;
        String lastMonth = "";

        for (int i = 0; i < mCount; i++) {
            LevelInfo info = mCountHashtable.countHashtable.get(mDateArray[i]);
            info.calTotal();

            if(!mDateArray[i].substring(0,5).equals(lastMonth)){
                month_number++;
                lastMonth = mDateArray[i].substring(0,5);
            }
        }
        levelCount = hashTable.maxLevel + 1;


        //2014-07-22
        //calculate the month number.


        int month_count = 0;

        mMonthArray = new String[mCount];
        String currentMonth = mDateArray[0].substring(0, 5);
        for (int i = 0; i < mCount; i++) {
            LevelInfo info = mCountHashtable.countHashtable.get(mDateArray[i]);

            if (mDateArray[i].substring(0, 5).equals(currentMonth)) {
                month_count += info.mTotalCount;
            } else {
                mMonthArray[i-1] = currentMonth + ":" + month_count;
                month_count = info.mTotalCount;
                currentMonth = mDateArray[i].substring(0, 5);
            }

        }
        mMonthArray[mCount-1] = currentMonth + ":" + month_count;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        view_w = getMeasuredWidth();
        view_h = getMeasuredHeight();
        Util.log("w h:" + view_w + " " + view_h);
        max_col = (view_w - dateW) / dataW;
        max_row = (view_h - OFF_Y) / lineH;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Util.log("Touch event");

        if (mMode == MODE_SELECT) {

            if (event.getAction() == event.ACTION_UP) {
                if (selectState == SELECT_STATE_GOING) {
                    selectState = SELECT_STATE_DONE;
                    invalidate();
                    selectData();
                }

            }
        }

        mGestureDetector.onTouchEvent(event);
        // event.describeContents();
        return true;
        // TODO Auto-generated method stub
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // this is temp solution for show mode.

        if (mMode == MODE_SELECT) {
            mPaint.setColor(0xffffff00);
        } else {
            mPaint.setColor(0xff2288ff);
        }

        float bottomEdge = view_h;
        if (mCount > max_row) {
            if ((scroll_start_row + max_row) == mCount) {
                bottomEdge = max_row * lineH + OFF_Y;
            }
        } else {
            bottomEdge = mCount * lineH + OFF_Y;
        }

        float rightEdge = view_w;
        if (levelCount > max_col) {
            if ((scroll_start_col + max_col) == levelCount) {
                rightEdge = max_col * dataW + dateW;
            }
        } else {
            rightEdge = levelCount * dataW + dateW;
        }

        canvas.drawLine(0, OFF_Y, rightEdge, OFF_Y, mPaint);

        // draw columns
        float column_x = dateW;
        for (int i = scroll_start_col; i < levelCount; i++) {
            mRedPaint.setColor(Util.getIndexColor(i));
//            if (i == 5 || i == 10) {
                canvas.drawLine(column_x, OFF_Y, column_x, bottomEdge, mRedPaint);
//            } else {
//                canvas.drawLine(column_x, OFF_Y, column_x, bottomEdge, mPaint);
//            }
            column_x += dataW;
        }
        canvas.drawLine(column_x, OFF_Y, column_x, bottomEdge, mBluePaint);

        //2014-07-22
        //for calculate the month number.

        String currentMonth = mDateArray[0].substring(4,5);

        int topY = OFF_Y;
        int bomY = lineH + OFF_Y;

        int month_index = 0;


        Hashtable<String, LevelInfo> table = mCountHashtable.countHashtable;
        for (int i = scroll_start_row; i < mCount; i++) {
            canvas.drawText(mDateArray[i], 0, bomY, mDatePaint);
            canvas.drawLine(0, bomY, rightEdge, bomY, mPaint);
            int count = 0;
            LevelInfo info = table.get(mDateArray[i]);
            int averageLevel = info.getAverageLevel();
            column_x = dateW;
            for (int j = scroll_start_col; j < levelCount; j++) {
                count = info.countInfo[j];
                if (j == averageLevel) {
                    canvas.drawRect(column_x + 1, topY + 1, column_x + dataW - 1, bomY - 1,
                            fillPaint);
                }
                if (count > 0) {
                    canvas.drawText("" + count, column_x, bomY, mCountPaint);
                }

                column_x += dataW;
            }
            //show month number.
            if (mMonthArray[i]!=null) {
                canvas.drawText(mMonthArray[i], dateW, bomY,
                        monthPaint);
            }

            topY += lineH;
            bomY += lineH;
        }
        if (mMode == MODE_SELECT) {
            if (selectState == SELECT_STATE_GOING) {
                canvas.drawRect(selectRect, selectPaint);

            } else if (selectState == SELECT_STATE_DONE) {
                canvas.drawRect(selectRect, normalPaint);

            }
        }
        super.onDraw(canvas);
    }

    private void scrollTo(MotionEvent ev1, MotionEvent ev2) {
        float cx = ev1.getX() - ev2.getX();
        float cy = ev1.getY() - ev2.getY();

        //offset_x: actual moved grids.
        float offset_x = dataW / 2 + cx;
        offset_x /= dataW;
        float offset_y = lineH / 2 + cy;
        offset_y /= lineH;
        //touch_start_row: last drag operation moved grids.
        scroll_start_row = touch_start_row + (int)offset_y;
        scroll_start_col = touch_start_col + (int)offset_x;

        if ((scroll_start_row + max_row) > mCount) {
            scroll_start_row = mCount - max_row;
        }
        if (scroll_start_row < 0) {
            scroll_start_row = 0;
        }
        if ((scroll_start_col + max_col) > levelCount) {
            scroll_start_col = levelCount - max_col;
        }
        if (scroll_start_col < 0) {
            scroll_start_col = 0;
        }

        Util.log("fling x:" + cx + "  y:" + cy + " offset " + (int)offset_x);

        if (scroll_start_col != scroll_start_col_last || scroll_start_row != scroll_start_row_last) {
            scroll_start_col_last = scroll_start_col;
            scroll_start_row_last = scroll_start_row;
            invalidate();
        }

    }


    public boolean onDown(MotionEvent ev) {

        if (mMode == MODE_DRAG) {
            touch_start_col = scroll_start_col;
            touch_start_row = scroll_start_row;
        } else {
            selectState = SELECT_STATE_BEGIN;
            invalidate();
        }
        return true;
    }

    public boolean onFling(MotionEvent ev1, MotionEvent ev2, float vx, float vy) {
        float cx = ev1.getX() - ev2.getX();
        float cy = ev1.getY() - ev2.getY();
        Util.log("fling x:" + cx + "  y:" + cy + "  vx:" + vx + " vy:" + vy);

        return false;
    }

    // here switch the mode between DRAG and SELECT.
    public void onLongPress(MotionEvent arg0) {
        if (mMode == MODE_DRAG) {
            mMode = MODE_SELECT;
            selectState = SELECT_STATE_IDLE;
        } else {
            mMode = MODE_DRAG;
        }
        invalidate();
    }

    public boolean onScroll(MotionEvent ev1, MotionEvent ev2, float vx, float vy) {
        float cx = ev1.getX() - ev2.getX();
        float cy = ev1.getY() - ev2.getY();
        Util.log("scroll x:" + cx + "  y:" + cy + "  vx:" + vx + " vy:" + vy);
        if (mMode == MODE_DRAG) {
            scrollTo(ev1, ev2);
        } else {
            selectRect.right = Math.max((int)ev1.getX(), (int)ev2.getX());
            selectRect.left = Math.min((int)ev1.getX(), (int)ev2.getX());
            selectRect.bottom = Math.max((int)ev1.getY(), (int)ev2.getY());
            selectRect.top = Math.min((int)ev1.getY(), (int)ev2.getY());
            selectState = SELECT_STATE_GOING;
            invalidate();
        }

        return true;
    }

    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    public boolean onSingleTapUp(MotionEvent ev) {
        float cx = ev.getX();
        float cy = ev.getY();

        Util.log("onSingleTapUp x:" + cx + "  y:" + cy);

        // TODO Auto-generated method stub
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */

        /*
         * Shortcut the most recurring case: the user is in the dragging state
         * and he is moving his finger. We want to intercept this motion.
         */
    }
    private int getIndex(int start, int w,int v){
        return (int)(v - start)/w;
    }

    private void selectData(){
        int leftLevel = getIndex(dateW,dataW,selectRect.left);
        int rightLevel = getIndex(dateW,dataW,selectRect.right);
        if(rightLevel <0){
            return;
        }
        if(leftLevel<0){
            leftLevel = 0;
        }
        leftLevel += scroll_start_col;
        rightLevel += scroll_start_col;


        int topDate = getIndex(OFF_Y,lineH,selectRect.top);
        int bottomDate = getIndex(OFF_Y,lineH,selectRect.bottom);

        if(bottomDate<0){
            return;
        }
        if(topDate<0){
            topDate = 0;
        }

        topDate += scroll_start_row;
        bottomDate += scroll_start_row;

        long dateStart = Util.getTimeBegin(mDateArray[topDate]);
        long dateEnd = Util.getTimeEnd(mDateArray[bottomDate]);

        WordLibAdapter lib = WordLibAdapter.getInstance(context);
        Cursor cursor = lib.getWordList(leftLevel, rightLevel, dateStart, dateEnd);

        RefreshListPage.openWordListPage(context, cursor);
    }
}
