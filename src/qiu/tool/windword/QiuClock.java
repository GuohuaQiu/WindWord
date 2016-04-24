/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qiu.tool.windword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.content.res.TypedArray;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews.RemoteView;

import java.util.TimeZone;

/**
 * This widget display an analogic clock with two hands for hours and minutes.
 *
 * @attr ref android.R.styleable#AnalogClock_dial
 * @attr ref android.R.styleable#AnalogClock_hand_hour
 * @attr ref android.R.styleable#AnalogClock_hand_minute
 */
@RemoteView
public class QiuClock extends View implements OnGestureListener {
    private GestureDetector mGestureDetector;

    private Time mCalendar;

    private Drawable mHourHand;

    private Drawable mMinuteHand;

    private Drawable mDial;

    private Drawable mMyBack;

    private int mDialWidth;

    private int mDialHeight;

    private boolean mAttached;

    private final Handler mHandler = new Handler();

    private float mMinutes;

    private float mHour;

    private boolean mChanged;

    private Context mContext;

    public QiuClock(Context context) {
        this(context, null);
    }

    public QiuClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        getWidth();

    }

    public QiuClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        Resources r = context.getResources();
        // TypedArray a =
        // context.obtainStyledAttributes(
        // attrs, com.android.internal.R.styleable.AnalogClock, defStyle, 0);
        mDial = r.getDrawable(R.drawable.dial);
        mHourHand = r.getDrawable(R.drawable.appwidget_clock_hour);
        mMinuteHand = r.getDrawable(R.drawable.appwidget_clock_minute);
        // mMyBack = r.getDrawable(R.drawable.dial);

        // mDial =
        // a.getDrawable(com.android.internal.R.styleable.AnalogClock_dial);
        // if (mDial == null) {
        // mDial = r.getDrawable(com.android.internal.R.drawable.clock_dial);
        // }

        // mHourHand =
        // a.getDrawable(com.android.internal.R.styleable.AnalogClock_hand_hour);
        // if (mHourHand == null) {
        // mHourHand =
        // r.getDrawable(com.android.internal.R.drawable.clock_hand_hour);
        // }

        // mMinuteHand =
        // a.getDrawable(com.android.internal.R.styleable.AnalogClock_hand_minute);
        // if (mMinuteHand == null) {
        // mMinuteHand =
        // r.getDrawable(com.android.internal.R.drawable.clock_hand_minute);
        // }

        mCalendar = new Time();

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
        mGestureDetector = new GestureDetector((OnGestureListener)this);

        paintMark.setStrokeWidth(10);
        paintMark.setColor(0xff000000);
        paintMark.setAntiAlias(true);

        paintMarkDigital.setStrokeWidth(10);
        paintMarkDigital.setColor(0xff334433);
        paintMarkDigital.setAntiAlias(true);
        paintMarkDigital.setTextSize(50);

        paintDigital.setStrokeWidth(10);
        paintDigital.setColor(0xff333333);
        paintDigital.setAntiAlias(true);
        paintDigital.setTextSize(130);

        // int h = mMyBack.getIntrinsicHeight();
        // int w = mMyBack.getIntrinsicWidth();
        // Util.log("Out: w h " + h + " " +w);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();

            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
        }

        // NOTE: It's safe to do these after registering the receiver since the
        // receiver always runs
        // in the main thread, therefore the receiver can't run before this
        // method returns.

        // The time zone may have changed while the receiver wasn't registered,
        // so update the Time
        mCalendar = new Time();

        // Make sure we update to the current time
        onTimeChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float)widthSize / (float)mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float)heightSize / (float)mDialHeight;
        }

        float scale = Math.min(hScale, vScale);
        scale = 2;

        int width = ((Activity)getContext()).getWindowManager().getDefaultDisplay().getWidth();
        int height = ((Activity)getContext()).getWindowManager().getDefaultDisplay().getHeight();

        setMeasuredDimension(width, height);
        center_x = Math.min(width, height) / 2;
        center_y = center_x;
        dias = center_x - 40;
        // / setMeasuredDimension(resolveSizeAndState((int) (mDialWidth *
        // scale), widthMeasureSpec, 0),
        // resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec,
        // 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    private int center_x = 0;

    private int center_y = 0;

    private int dias = 280;

    Paint paint = new Paint();

    Paint paintMark = new Paint();

    Paint paintMarkDigital = new Paint();

    Paint paintDigital = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        int availableWidth = 2 * center_x;

        int availableHeight = 2 * center_y;

        int x = center_x;
        int y = center_y;

        final Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        boolean scaled = false;

        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float)availableWidth / (float)w, (float)availableHeight
                    / (float)h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);

        canvas.save();
        canvas.rotate(mHour / 12.0f * 360.0f, x, y);
        /*
         * final Drawable hourHand = mHourHand; if (changed) { w =
         * hourHand.getIntrinsicWidth(); h = hourHand.getIntrinsicHeight();
         * hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h /
         * 2)); } hourHand.draw(canvas); canvas.restore();
         *
         * canvas.save(); canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);
         *
         * final Drawable minuteHand = mMinuteHand; if (changed) { w =
         * minuteHand.getIntrinsicWidth(); h = minuteHand.getIntrinsicHeight();
         * minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h /
         * 2)); } minuteHand.draw(canvas);
         */
        canvas.restore();

        if (scaled) {
            canvas.restore();
        }

        // actual start.
        int width = 50;
        // paint.setColor(0xff003388);
        // canvas.drawCircle(center_x, center_y, dias, paint);
        // paint.setColor(0xff0066aa);
        // canvas.drawCircle(center_x, center_y, dias-width, paint);

        paint.setColor(0xff00ffaa);
        paint.setTextSize(60);

        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);

        for (int i = 1; i < 13; i++) {
            canvas.save();
            canvas.rotate(30 * i, center_x, center_y);
            canvas.drawText("" + i, center_x - 20, width + 30, paintMarkDigital);
            canvas.drawLine(center_x, width + 70, center_x, width + 110, paintMark);
            canvas.restore();
        }

        canvas.drawLine(center_x, center_y, moved_x, moved_y, paint);
        canvas.drawText(movedTime, 400, 400, paintDigital);

        // mMyBack.draw(canvas);
    }

    private void onTimeChanged() {
        mCalendar.setToNow();

        int hour = mCalendar.hour;
        int minute = mCalendar.minute;
        int second = mCalendar.second;

        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        mChanged = true;

        updateContentDescription(mCalendar);
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
            }

            onTimeChanged();

            invalidate();
        }
    };

    private void updateContentDescription(Time time) {
        final int flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
        String contentDescription = DateUtils.formatDateTime(mContext, time.toMillis(false), flags);
        setContentDescription(contentDescription);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
         * Util.log("Touch event");
         *
         * if (mMode == MODE_SELECT) {
         *
         * if (event.getAction() == event.ACTION_UP) { if (selectState ==
         * SELECT_STATE_GOING) { selectState = SELECT_STATE_DONE; invalidate();
         * selectData(); }
         *
         * } }
         */

        mGestureDetector.onTouchEvent(event);
        // event.describeContents();
        return true;
        // TODO Auto-generated method stub
    }

    private String movedTime = "00:00";

    private String getMovedTime(float du) {
        int idu = (int)du;
        int h = idu / 30;
        int m = idu % 30 * 2;
        return String.format("%02d:%02d", h,m);
    }

    private void setTime(MotionEvent ev) {
        float du = getAngle(ev);
        Util.log("du:" + du);
        movedTime = getMovedTime(du);
    }

    private void scrollTo(MotionEvent ev1, MotionEvent ev2) {
        setTime(ev2);
        /*
         * float cx = ev1.getX() - ev2.getX(); float cy = ev1.getY() -
         * ev2.getY();
         *
         * //offset_x: actual moved grids. float offset_x = dataW / 2 + cx;
         * offset_x /= dataW; float offset_y = lineH / 2 + cy; offset_y /=
         * lineH; //touch_start_row: last drag operation moved grids.
         * scroll_start_row = touch_start_row + (int)offset_y; scroll_start_col
         * = touch_start_col + (int)offset_x;
         *
         * if ((scroll_start_row + max_row) > mCount) { scroll_start_row =
         * mCount - max_row; } if (scroll_start_row < 0) { scroll_start_row = 0;
         * } if ((scroll_start_col + max_col) > levelCount) { scroll_start_col =
         * levelCount - max_col; } if (scroll_start_col < 0) { scroll_start_col
         * = 0; }
         *
         * Util.log("fling x:" + cx + "  y:" + cy + " offset " + (int)offset_x);
         *
         * if (scroll_start_col != scroll_start_col_last || scroll_start_row !=
         * scroll_start_row_last) { scroll_start_col_last = scroll_start_col;
         * scroll_start_row_last = scroll_start_row; invalidate(); }
         */

    }

    public boolean onDown(MotionEvent ev) {
        double ang = getAngle(ev);
        /*
         * if (mMode == MODE_DRAG) { touch_start_col = scroll_start_col;
         * touch_start_row = scroll_start_row; } else { selectState =
         * SELECT_STATE_BEGIN; invalidate(); } return true;
         */
        return false;
    }

    public boolean onScroll(MotionEvent ev1, MotionEvent ev2, float vx, float vy) {
        scrollTo(ev1, ev2);
        // Util.log("move");
        /*
         * float cx = ev1.getX() - ev2.getX(); float cy = ev1.getY() -
         * ev2.getY(); Util.log("scroll x:" + cx + "  y:" + cy + "  vx:" + vx +
         * " vy:" + vy); if (mMode == MODE_DRAG) { scrollTo(ev1, ev2); } else {
         * selectRect.right = Math.max((int)ev1.getX(), (int)ev2.getX());
         * selectRect.left = Math.min((int)ev1.getX(), (int)ev2.getX());
         * selectRect.bottom = Math.max((int)ev1.getY(), (int)ev2.getY());
         * selectRect.top = Math.min((int)ev1.getY(), (int)ev2.getY());
         * selectState = SELECT_STATE_GOING; invalidate(); }
         *
         * return true;
         */
        return false;
    }

    public boolean onSingleTapUp(MotionEvent ev) {
        float cx = ev.getX();
        float cy = ev.getY();

        Util.log("onSingleTapUp x:" + cx + "  y:" + cy);
        setTime(ev);

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

    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    private Point getCenter() {
        return new Point(mDialWidth / 2, mDialHeight / 2);
    }

    private float moved_x = 0;

    private float moved_y = 0;

    private float getAngle(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        moved_x = x;
        moved_y = y;

        // Point center = getCenter();
        double xie = Math.sqrt((x - center_x) * (x - center_x) + (y - center_y) * (y - center_y));
        double du = Math.toDegrees(Math.asin((center_y - y) / xie));
        // du*= 160;
        // du/=Math.PI;

        invalidate();
        if (du > 0) {
            if (moved_x > center_x) {
                Util.log("A");
                return (float)(90.0 - du);

            } else {
                Util.log("b");
                return (float)(270.0 + du);
            }
        } else {
            if (moved_x > center_x) {
                Util.log("c");
                return (float)(90.0 - du);

            } else {
                Util.log("d");
                return (float)(270.0 + du);
            }
        }
    }
}
