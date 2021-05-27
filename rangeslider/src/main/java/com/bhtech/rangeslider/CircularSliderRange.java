package com.bhtech.rangeslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Locale;

public class CircularSliderRange extends View {

    /**
     * Listener interface used to detect when slider moves around.
     */
    public interface OnSliderRangeMovedListener {

        /**
         * This method is invoked when start thumb is moved, providing position of the start slider thumb.
         *
         * @param pos Value between 0 and 1 representing the current angle.<br>
         *            {@code pos = (Angle - StartingAngle) / (2 * Pi)}
         */
        void onStartSliderMoved(double pos);

        /**
         * This method is invoked when end thumb is moved, providing position of the end slider thumb.
         *
         * @param pos Value between 0 and 1 representing the current angle.<br>
         *            {@code pos = (Angle - StartingAngle) / (2 * Pi)}
         */
        void onEndSliderMoved(double pos);

        /**
         * This method is invoked when start slider is pressed/released.
         *
         * @param event Event represent state of the slider, it can be in two states: Pressed or Released.
         */
        void onStartSliderEvent(ThumbEvent event);

        /**
         * This method is invoked when end slider is pressed/released.
         *
         * @param event Event represent state of the slider, it can be in two states: Pressed or Released.
         */
        void onEndSliderEvent(ThumbEvent event);
    }

    public static final int MAX_CIRCLE_DEGREES = 360;
    public static final int DEFAULT_THUMB_STROKE_WIDTH = 2;
    // TODO the start and end of the available range can be added as view attributes eventually.
    public static final int START_RANGE_FOR_START_THUMB = 150;
    public static final int END_RANGE_FOR_END_THUMB = 30;
    private static final int THUMB_SIZE_NOT_DEFINED = -1;

    private int mThumbStartX;
    private int mThumbStartY;

    private int mThumbEndX;
    private int mThumbEndY;

    private int mCircleCenterX;
    private int mCircleCenterY;
    private int mCircleRadius;

    private Drawable mStartThumbImage;
    private Drawable mEndThumbImage;
    private int mPadding;
    private int mStartThumbSize;
    private int mEndThumbSize;
    private int mStartThumbColor;
    private int mEndThumbColor;
    private int mThumbStrokeColor;
    private int mBorderColor;
    private int mBorderThickness;
    private int mArcDashSize;
    private int mArcColor;
    private LineCap mLineCap;
    private double startThumbAngle;
    private double endThumbAngle;
    private boolean mIsThumbSelected = false;
    private boolean mIsThumbEndSelected = false;

    private Paint mPaint = new Paint();
    private Paint mLinePaint = new Paint();
    private RectF arcRectF = new RectF();
    private Rect arcRect = new Rect();
    private OnSliderRangeMovedListener mListener;

    private enum Thumb {
        START, END
    }

    public enum LineCap {
        BUTT(0),
        ROUND(1),
        SQUARE(2);

        int id;

        LineCap(int id) {
            this.id = id;
        }

        static LineCap fromId(int id) {
            for (LineCap lc : values()) {
                if (lc.id == id) return lc;
            }
            throw new IllegalArgumentException();
        }

        public Paint.Cap getPaintCap() {
            switch (this) {
                case BUTT:
                default:
                    return Paint.Cap.BUTT;
                case ROUND:
                    return Paint.Cap.ROUND;
                case SQUARE:
                    return Paint.Cap.SQUARE;
            }
        }
    }

    public CircularSliderRange(Context context) {
        this(context, null);
    }

    public CircularSliderRange(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularSliderRange(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    // common initializer method
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularSliderRange, defStyleAttr, 0);

        // read all available attributes
        float startAngle = a.getFloat(R.styleable.CircularSliderRange_start_angle, 90);
        float endAngle = a.getFloat(R.styleable.CircularSliderRange_end_angle, 60);
        int thumbSize = a.getDimensionPixelSize(R.styleable.CircularSliderRange_thumb_size, 50);
        mStartThumbSize = a.getDimensionPixelSize(R.styleable.CircularSliderRange_start_thumb_size, THUMB_SIZE_NOT_DEFINED);
        mEndThumbSize = a.getDimensionPixelSize(R.styleable.CircularSliderRange_end_thumb_size, THUMB_SIZE_NOT_DEFINED);
        mStartThumbColor = a.getColor(R.styleable.CircularSliderRange_start_thumb_color, Color.GRAY);
        mThumbStrokeColor = a.getColor(R.styleable.CircularSliderRange_thumb_stroke_color, -1);
        mEndThumbColor = a.getColor(R.styleable.CircularSliderRange_end_thumb_color, Color.GRAY);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.CircularSliderRange_border_thickness, 20);
        mArcDashSize = a.getDimensionPixelSize(R.styleable.CircularSliderRange_arc_dash_size, 60);
        mArcColor = a.getColor(R.styleable.CircularSliderRange_arc_color, 0);
        mBorderColor = a.getColor(R.styleable.CircularSliderRange_border_color, Color.RED);
        mStartThumbImage = a.getDrawable(R.styleable.CircularSliderRange_start_thumb_image);
        mEndThumbImage = a.getDrawable(R.styleable.CircularSliderRange_end_thumb_image);
        mLineCap = LineCap.fromId(a.getInt(R.styleable.CircularSliderRange_line_cap, 0));

        setStartAngle(startAngle);
        setEndAngle(endAngle);
        setThumbSize(thumbSize);

        // assign padding - check for version because of RTL layout compatibility
        int padding;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int all = getPaddingLeft() + getPaddingRight() + getPaddingBottom() + getPaddingTop() + getPaddingEnd() + getPaddingStart();
            padding = all / 6;
        } else {
            padding = (getPaddingLeft() + getPaddingRight() + getPaddingBottom() + getPaddingTop()) / 4;
        }
        mPadding = padding;
        a.recycle();
    }

    public void setStartAngle(double startAngle) {
        startThumbAngle = fromDrawingAngle(startAngle);
    }

    public void setEndAngle(double angle) {
        endThumbAngle = fromDrawingAngle(angle);
    }

    public void setThumbSize(int thumbSize) {
        setStartThumbSize(thumbSize);
        setEndThumbSize(thumbSize);
    }

    public void setStartThumbSize(int thumbSize) {
        if (thumbSize == THUMB_SIZE_NOT_DEFINED)
            return;
        mStartThumbSize = thumbSize;
    }

    public void setEndThumbSize(int thumbSize) {
        if (thumbSize == THUMB_SIZE_NOT_DEFINED)
            return;
        mEndThumbSize = thumbSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // use smaller dimension for calculations (depends on parent size)
        int smallerDim = Math.min(w, h);

        // find circle's rectangle points
        int largestCenteredSquareLeft = (w - smallerDim) / 2;
        int largestCenteredSquareTop = (h - smallerDim) / 2;
        int largestCenteredSquareRight = largestCenteredSquareLeft + smallerDim;
        int largestCenteredSquareBottom = largestCenteredSquareTop + smallerDim;

        // save circle coordinates and radius in fields
        mCircleCenterX = largestCenteredSquareRight / 2 + (w - largestCenteredSquareRight) / 2;
        mCircleCenterY = largestCenteredSquareBottom / 2 + (h - largestCenteredSquareBottom) / 2;
        mCircleRadius = smallerDim / 2 - mBorderThickness / 2 - mPadding;

        // works well for now, should we call something else here?
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // outer circle (ring)
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(mBorderThickness);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mCircleRadius, mPaint);

        // find thumb start position
        mThumbStartX = (int) (mCircleCenterX + mCircleRadius * Math.cos(startThumbAngle));
        mThumbStartY = (int) (mCircleCenterY - mCircleRadius * Math.sin(startThumbAngle));

        //find thumb end position
        mThumbEndX = (int) (mCircleCenterX + mCircleRadius * Math.cos(endThumbAngle));
        mThumbEndY = (int) (mCircleCenterY - mCircleRadius * Math.sin(endThumbAngle));

        // draw the arc between the thumbs
        mLinePaint.setColor(mArcColor == 0 ? Color.RED : mArcColor);
        mLinePaint.setStyle(Style.STROKE);
        mLinePaint.setStrokeWidth(mArcDashSize);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setTextSize(50);
        mLinePaint.setStrokeCap(mLineCap.getPaintCap());

        arcRect.set(mCircleCenterX - mCircleRadius, mCircleCenterY + mCircleRadius, mCircleCenterX + mCircleRadius, mCircleCenterY - mCircleRadius);
        arcRectF.set(arcRect);
        arcRectF.sort();

        final float drawStart = toDrawingAngle(startThumbAngle);
        final float drawEnd = toDrawingAngle(endThumbAngle);

        canvas.drawArc(arcRectF, drawStart, (MAX_CIRCLE_DEGREES + drawEnd - drawStart) % MAX_CIRCLE_DEGREES, false, mLinePaint);
        int mThumbSize = mStartThumbSize;
        if (mStartThumbImage != null) {
            // draw png
            mStartThumbImage.setBounds(mThumbStartX - mThumbSize / 2, mThumbStartY - mThumbSize / 2, mThumbStartX + mThumbSize / 2, mThumbStartY + mThumbSize / 2);
            mStartThumbImage.draw(canvas);
        } else {
            // draw start thumb circle
            mPaint.setColor(mStartThumbColor);
            mPaint.setStyle(Style.FILL);
            canvas.drawCircle(mThumbStartX, mThumbStartY, mThumbSize / 2f, mPaint);

            // draw start thumb outline
            if (mThumbStrokeColor != -1) {
                mPaint.setStyle(Style.STROKE);
                mPaint.setStrokeWidth(DEFAULT_THUMB_STROKE_WIDTH);
                mPaint.setColor(mThumbStrokeColor);
                canvas.drawCircle(mThumbStartX, mThumbStartY, mThumbSize / 2f, mPaint);
            }
        }

        mThumbSize = mEndThumbSize;
        if (mEndThumbImage != null) {
            // draw png
            mEndThumbImage.setBounds(mThumbEndX - mThumbSize / 2, mThumbEndY - mThumbSize / 2, mThumbEndX + mThumbSize / 2, mThumbEndY + mThumbSize / 2);
            mEndThumbImage.draw(canvas);
        } else {
            // draw end thumb circle
            mPaint.setStyle(Style.FILL);
            mPaint.setColor(mEndThumbColor);
            canvas.drawCircle(mThumbEndX, mThumbEndY, mThumbSize / 2f, mPaint);

            // draw end thumb outline
            if (mThumbStrokeColor != -1) {
                mPaint.setStyle(Style.STROKE);
                mPaint.setStrokeWidth(DEFAULT_THUMB_STROKE_WIDTH);
                mPaint.setColor(mThumbStrokeColor);
                canvas.drawCircle(mThumbEndX, mThumbEndY, mThumbSize / 2f, mPaint);
            }
        }

        // TODO organize, make constants
        float angleRange = MAX_CIRCLE_DEGREES - START_RANGE_FOR_START_THUMB;

        float minTextOffset = MAX_CIRCLE_DEGREES;
        float maxTextOffset = 150;
        float textOffsetRange = maxTextOffset - minTextOffset;

        float startOffsetFromAngle = drawStart * textOffsetRange / angleRange + minTextOffset;
        float endOffsetFromAngle = drawEnd * textOffsetRange / angleRange + minTextOffset;

        // TODO previously used as helper text, used for debugging.
        //  Eventually it should display the chosen start and end values.
        mLinePaint.setStyle(Style.FILL);
        canvas.drawText(String.format(Locale.US, "%.1f", drawStart), mThumbStartX - startOffsetFromAngle, mThumbStartY - 40, mLinePaint);
        canvas.drawText(String.format(Locale.US, "%.1f", drawEnd), mThumbEndX - endOffsetFromAngle, mThumbEndY - 40, mLinePaint);
    }

    /**
     * Invoked when slider starts moving or is currently moving. This method calculates and sets position and angle of the thumb.
     *
     * @param touchX Where is the touch identifier now on X axis
     * @param touchY Where is the touch identifier now on Y axis
     */
    private void updateSliderState(int touchX, int touchY, Thumb thumb) {
        int distanceX = touchX - mCircleCenterX;
        int distanceY = mCircleCenterY - touchY;
        double c = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        double angle = Math.acos(distanceX / c);
        if (distanceY < 0)
            angle = -angle;

        // If the user attempts to move the thumb from the bottom to the other side,
        // this will cancel the MotionEvent.ACTION_MOVE to stop them from looping the thumb around.
        float drawingAngle = toDrawingAngle(angle);
        if (drawingAngle > 80 && drawingAngle < 100) {
            mIsThumbSelected = false;
            mIsThumbEndSelected = false;
        }

        if (thumb == Thumb.START) {
            if (isNumberInOverflowedRange(drawingAngle, START_RANGE_FOR_START_THUMB, toDrawingAngle(endThumbAngle), MAX_CIRCLE_DEGREES)) {
                startThumbAngle = angle;
                mListener.onStartSliderMoved(toDrawingAngle(angle));
            }
        } else if (thumb == Thumb.END) {
            if (isNumberInOverflowedRange(drawingAngle, toDrawingAngle(startThumbAngle), END_RANGE_FOR_END_THUMB, MAX_CIRCLE_DEGREES)) {
                endThumbAngle = angle;
                mListener.onEndSliderMoved(toDrawingAngle(angle));
            }
        }
    }

    private boolean isNumberInOverflowedRange(float number, float rangeStart, float rangeEnd, float maxNumber) {
        if (rangeEnd < rangeStart) {
            rangeEnd += maxNumber;
        }
        if (number < rangeStart) {
            number += maxNumber;
        }
        return number > rangeStart && number < rangeEnd;
    }

    private float toDrawingAngle(double angleInRadians) {
        double fixedAngle = Math.toDegrees(angleInRadians);
        if (angleInRadians > 0)
            fixedAngle = MAX_CIRCLE_DEGREES - fixedAngle;
        else
            fixedAngle = -fixedAngle;
        return (float) fixedAngle;
    }

    private double fromDrawingAngle(double angleInDegrees) {
        double radians = Math.toRadians(angleInDegrees);
        return -radians;
    }

    /**
     * Set slider range moved listener. Set {@link OnSliderRangeMovedListener} to {@code null} to remove it.
     *
     * @param listener Instance of the slider range moved listener, or null when removing it
     */
    public void setOnSliderRangeMovedListener(OnSliderRangeMovedListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // start moving the thumb (this is the first touch)
                int x = (int) ev.getX();
                int y = (int) ev.getY();

                int mThumbSize = mStartThumbSize;
                boolean isThumbStartPressed = x < mThumbStartX + mThumbSize
                        && x > mThumbStartX - mThumbSize
                        && y < mThumbStartY + mThumbSize
                        && y > mThumbStartY - mThumbSize;

                mThumbSize = mEndThumbSize;
                boolean isThumbEndPressed = x < mThumbEndX + mThumbSize
                        && x > mThumbEndX - mThumbSize
                        && y < mThumbEndY + mThumbSize
                        && y > mThumbEndY - mThumbSize;

                if (isThumbStartPressed) {
                    mIsThumbSelected = true;
                    updateSliderState(x, y, Thumb.START);
                } else if (isThumbEndPressed) {
                    mIsThumbEndSelected = true;
                    updateSliderState(x, y, Thumb.END);
                }

                if (mListener != null) {
                    if (mIsThumbSelected)
                        mListener.onStartSliderEvent(ThumbEvent.THUMB_PRESSED);
                    if (mIsThumbEndSelected)
                        mListener.onEndSliderEvent(ThumbEvent.THUMB_PRESSED);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // still moving the thumb (this is not the first touch)
                if (mIsThumbSelected) {
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    updateSliderState(x, y, Thumb.START);
                } else if (mIsThumbEndSelected) {
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    updateSliderState(x, y, Thumb.END);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (mListener != null) {
                    if (mIsThumbSelected)
                        mListener.onStartSliderEvent(ThumbEvent.THUMB_RELEASED);
                    if (mIsThumbEndSelected)
                        mListener.onEndSliderEvent(ThumbEvent.THUMB_RELEASED);
                }

                // finished moving (this is the last touch)
                mIsThumbSelected = false;
                mIsThumbEndSelected = false;
                break;
            }
        }

        invalidate();
        return true;
    }

}
