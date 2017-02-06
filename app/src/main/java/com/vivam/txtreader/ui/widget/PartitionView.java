package com.vivam.txtreader.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.vivam.txtreader.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PartitionView extends FrameLayout {

    public static final int AREA_LEFT = 0;
    public static final int AREA_CENTER = 1;
    public static final int AREA_RIGHT = 2;

    private static final int LINE_DASHED = 0;
    private static final int LINE_SOLID = 1;

    private static final int LINE_WIDTH = 1;
    private static final int DASH_WIDTH = 1;
    private static final int DASH_GAP = 1;

    @IntDef({LINE_DASHED, LINE_SOLID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LineType {}

    /**
     * The absolute width of the available display size in pixels.
     */
    private int mScreenWidth;
    /**
     * The absolute height of the available display size in pixels.
     */
    private int mScreenHeight;
    /**
     * The width of left area in pixels.
     */
    private int mLeftWidth;
    /**
     * The start point of the central area in pixels.
     */
    private int mCenterStartX;
    private int mCenterStartY;
    /**
     * The width of the central area in pixels.
     */
    private int mCenterWidth;
    /**
     * The height of the central area in pixels.
     */
    private int mCenterHeight;
    /**
     * The color of line.
     */
    private int mLineColor;
    /**
     * The type of line.
     */
    private int mLineType;

    private boolean mNeedDrawLines = false;

    private OnPartitionClickListener mOnPartitionClickListener;

    public PartitionView(Context context) {
        this(context, null);
    }

    public PartitionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PartitionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.PartitionView, defStyleAttr, 0);
        mLeftWidth = a.getDimensionPixelSize(R.styleable.PartitionView_leftWidth, mScreenWidth / 2);
        mCenterStartX = a.getDimensionPixelSize(R.styleable.PartitionView_centerStartX,
                mScreenWidth * 3 / 8 );
        mCenterStartY = a.getDimensionPixelSize(R.styleable.PartitionView_centerStartY,
                mScreenHeight / 4);
        mCenterWidth = a.getDimensionPixelSize(R.styleable.PartitionView_centerWidth,
                mScreenWidth / 4);
        mCenterHeight = a.getDimensionPixelSize(R.styleable.PartitionView_centerHeight,
                mScreenHeight / 2);
        mLineColor = a.getColor(R.styleable.PartitionView_lineColor,
                context.getResources().getColor(R.color.default_partition_line));
        mLineType = a.getInteger(R.styleable.PartitionView_lineType, LINE_DASHED);
        a.recycle();

        checkValues();
    }

    private void checkValues() {
        if (mCenterStartX < 0) {
            mCenterStartX = 0;
        } else if (mCenterStartX > mScreenWidth) {
            mCenterStartX = mScreenWidth;
        }

        if (mCenterStartY < 0) {
            mCenterStartY = 0;
        } else if (mCenterStartY > mScreenHeight) {
            mCenterStartY = mScreenHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*if (mNeedDrawLines) {
            Paint paint = new Paint();
            paint.setColor(mLineColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(LINE_WIDTH);
            if (mLineType == LINE_DASHED) {
                paint.setPathEffect(new DashPathEffect(new float[]{DASH_WIDTH, DASH_GAP}, 0));
            }

            if (hasCenterArea()) {
                Path top = new Path();
                top.moveTo(mLeftWidth, 0);
                top.quadTo(mLeftWidth, mCenterStartY / 2, mLeftWidth, mCenterStartY);
                canvas.drawPath(top, paint);

                Path bottom = new Path();
                bottom.moveTo(mLeftWidth, mCenterStartY + mCenterHeight);
                bottom.quadTo(mLeftWidth, (mScreenHeight + mCenterStartY + mCenterHeight) / 2,
                        mLeftWidth, mScreenHeight);
                canvas.drawPath(bottom, paint);

                canvas.drawRect(mCenterStartX, mCenterStartY, mCenterStartX + mCenterWidth,
                        mCenterStartY + mCenterHeight, paint);
            } else {
                if (mLeftWidth > 0 && mLeftWidth < mScreenWidth) {
                    Path path = new Path();
                    path.moveTo(mLeftWidth, 0);
                    path.quadTo(mLeftWidth, mScreenHeight / 2, mLeftWidth, mScreenHeight);
                    canvas.drawPath(path, paint);
                }
            }
        }*/
    }

    private float mTouchX = -1;
    private float mTouchY = -1;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            mTouchX = ev.getRawX();
            mTouchY = ev.getRawY();
            return false;
        }

        if (action == MotionEvent.ACTION_UP && mTouchX > -1 && mTouchY > -1) {
            int area = getTouchedArea(mTouchX, mTouchY);
            if (mOnPartitionClickListener != null) {
                mOnPartitionClickListener.onClick(area);
            }
            mTouchX = -1;
            mTouchY = -1;
            return false;
        }

        mTouchX = -1;
        mTouchY = -1;

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    private int getTouchedArea(float x, float y) {
        if (hasCenterArea()) {
            final int centerStopX = mCenterStartX + mCenterWidth;
            final int centerStopY = mCenterStartX + mCenterHeight;

            if (x > mCenterStartX && x <= centerStopX
                    && y > mCenterStartY && y <= centerStopY) {
                return AREA_CENTER;
            } else {
                return x > mLeftWidth ? AREA_RIGHT : AREA_LEFT;
            }
        } else {
            return x > mCenterWidth ? AREA_RIGHT : AREA_LEFT;
        }
    }

    private boolean hasCenterArea() {
        return mCenterWidth > 0 && mCenterHeight > 0;
    }

    public int getLeftWidth() {
        return mLeftWidth;
    }

    public void setLeftWidth(int width) {
        mLeftWidth = width;
        invalidate();
    }

    public Point getCenterStart() {
        return new Point(mCenterStartX, mCenterStartY);
    }

    public void setCenterStart(Point centerStart) {
        if (centerStart != null) {
            mCenterStartX = centerStart.x;
            mCenterStartY = centerStart.y;
        } else {
            mCenterStartX = 0;
            mCenterStartY = 0;
        }
        invalidate();
    }

    public int getCenterWidth() {
        return mCenterWidth;
    }

    public void setCenterWidth(int width) {
        mCenterWidth = width;
        invalidate();
    }

    public int getCenterHeight() {
        return mCenterHeight;
    }

    public void setCenterHEight(int height) {
        mCenterHeight = height;
        invalidate();
    }

    public void setLineColor(int color) {
        mLineColor = color;
        invalidate();
    }

    public int getLineType() {
        return mLineType;
    }

    public void setLineType(@LineType int type) {
        mLineType = type;
        invalidate();
    }

    public boolean isNeedDrawLines() {
        return mNeedDrawLines;
    }

    public void setNeedDrawLines(boolean needDrawLines) {
        mNeedDrawLines = needDrawLines;
        invalidate();
    }

    public void setOnPartitionClickListener(OnPartitionClickListener listener) {
        mOnPartitionClickListener = listener;
    }

    public interface OnPartitionClickListener {
        public void onClick(int area);
    }
}
