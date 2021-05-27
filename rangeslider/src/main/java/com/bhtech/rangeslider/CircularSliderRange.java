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
    private static final int THUMB_SIZE_NOT_DEFINED = -1;
    // TODO the start and end of the available range can be added as xml view attributes eventually.
    public static final int START_RANGE_FOR_START_THUMB = 150;
    public static final int END_RANGE_FOR_END_THUMB = 30;

    // TODO Thumbs can be refactored into objects, for much cleaner code.
    private int startThumbX;
    private int startThumbY;
    private int endThumbX;
    private int endThumbY;
    private int startThumbSize;
    private int endThumbSize;
    private int startThumbColor;
    private int endThumbColor;
    private double startThumbAngle;
    private double endThumbAngle;
    private boolean isStartThumbSelected;
    private boolean isEndThumbSelected;
    private Drawable startThumbImage;
    private Drawable endThumbImage;
    private int thumbStrokeColor;

    private int circleCenterX;
    private int circleCenterY;
    private int circleRadius;
    private int borderColor;
    private int borderThickness;
    private int arcDashSize;
    private int arcColor;
    private LineCap lineCap;
    private int padding;

    private Paint paint = new Paint();
    private Paint linePaint = new Paint();
    private RectF arcRectF = new RectF();
    private Rect arcRect = new Rect();
    private OnSliderRangeMovedListener rangeMovedListener;

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
        startThumbSize = a.getDimensionPixelSize(R.styleable.CircularSliderRange_start_thumb_size, THUMB_SIZE_NOT_DEFINED);
        endThumbSize = a.getDimensionPixelSize(R.styleable.CircularSliderRange_end_thumb_size, THUMB_SIZE_NOT_DEFINED);
        startThumbColor = a.getColor(R.styleable.CircularSliderRange_start_thumb_color, Color.GRAY);
        thumbStrokeColor = a.getColor(R.styleable.CircularSliderRange_thumb_stroke_color, -1);
        endThumbColor = a.getColor(R.styleable.CircularSliderRange_end_thumb_color, Color.GRAY);
        borderThickness = a.getDimensionPixelSize(R.styleable.CircularSliderRange_border_thickness, 20);
        arcDashSize = a.getDimensionPixelSize(R.styleable.CircularSliderRange_arc_dash_size, 60);
        arcColor = a.getColor(R.styleable.CircularSliderRange_arc_color, 0);
        borderColor = a.getColor(R.styleable.CircularSliderRange_border_color, Color.RED);
        startThumbImage = a.getDrawable(R.styleable.CircularSliderRange_start_thumb_image);
        endThumbImage = a.getDrawable(R.styleable.CircularSliderRange_end_thumb_image);
        lineCap = LineCap.fromId(a.getInt(R.styleable.CircularSliderRange_line_cap, 0));

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
        this.padding = padding;
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
        startThumbSize = thumbSize;
    }

    public void setEndThumbSize(int thumbSize) {
        if (thumbSize == THUMB_SIZE_NOT_DEFINED)
            return;
        endThumbSize = thumbSize;
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
        circleCenterX = largestCenteredSquareRight / 2 + (w - largestCenteredSquareRight) / 2;
        circleCenterY = largestCenteredSquareBottom / 2 + (h - largestCenteredSquareBottom) / 2;
        circleRadius = smallerDim / 2 - borderThickness / 2 - padding;

        // works well for now, should we call something else here?
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // outer circle (ring)
        paint.setColor(borderColor);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(borderThickness);
        paint.setAntiAlias(true);
        canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, paint);

        // find thumb start position
        startThumbX = (int) (circleCenterX + circleRadius * Math.cos(startThumbAngle));
        startThumbY = (int) (circleCenterY - circleRadius * Math.sin(startThumbAngle));

        //find thumb end position
        endThumbX = (int) (circleCenterX + circleRadius * Math.cos(endThumbAngle));
        endThumbY = (int) (circleCenterY - circleRadius * Math.sin(endThumbAngle));

        // draw the arc between the thumbs
        linePaint.setColor(arcColor == 0 ? Color.RED : arcColor);
        linePaint.setStyle(Style.STROKE);
        linePaint.setStrokeWidth(arcDashSize);
        linePaint.setAntiAlias(true);
        linePaint.setTextSize(50);
        linePaint.setStrokeCap(lineCap.getPaintCap());

        arcRect.set(circleCenterX - circleRadius, circleCenterY + circleRadius, circleCenterX + circleRadius, circleCenterY - circleRadius);
        arcRectF.set(arcRect);
        arcRectF.sort();

        final float drawStart = toDrawingAngle(startThumbAngle);
        final float drawEnd = toDrawingAngle(endThumbAngle);

        canvas.drawArc(arcRectF, drawStart, (MAX_CIRCLE_DEGREES + drawEnd - drawStart) % MAX_CIRCLE_DEGREES, false, linePaint);
        int mThumbSize = startThumbSize;
        if (startThumbImage != null) {
            // draw png
            startThumbImage.setBounds(startThumbX - mThumbSize / 2, startThumbY - mThumbSize / 2, startThumbX + mThumbSize / 2, startThumbY + mThumbSize / 2);
            startThumbImage.draw(canvas);
        } else {
            // draw start thumb circle
            paint.setColor(startThumbColor);
            paint.setStyle(Style.FILL);
            canvas.drawCircle(startThumbX, startThumbY, mThumbSize / 2f, paint);

            // draw start thumb outline
            if (thumbStrokeColor != -1) {
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(DEFAULT_THUMB_STROKE_WIDTH);
                paint.setColor(thumbStrokeColor);
                canvas.drawCircle(startThumbX, startThumbY, mThumbSize / 2f, paint);
            }
        }

        mThumbSize = endThumbSize;
        if (endThumbImage != null) {
            // draw png
            endThumbImage.setBounds(endThumbX - mThumbSize / 2, endThumbY - mThumbSize / 2, endThumbX + mThumbSize / 2, endThumbY + mThumbSize / 2);
            endThumbImage.draw(canvas);
        } else {
            // draw end thumb circle
            paint.setStyle(Style.FILL);
            paint.setColor(endThumbColor);
            canvas.drawCircle(endThumbX, endThumbY, mThumbSize / 2f, paint);

            // draw end thumb outline
            if (thumbStrokeColor != -1) {
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(DEFAULT_THUMB_STROKE_WIDTH);
                paint.setColor(thumbStrokeColor);
                canvas.drawCircle(endThumbX, endThumbY, mThumbSize / 2f, paint);
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
        linePaint.setStyle(Style.FILL);
        canvas.drawText(String.format(Locale.US, "%.1f", drawStart), startThumbX - startOffsetFromAngle, startThumbY - 40, linePaint);
        canvas.drawText(String.format(Locale.US, "%.1f", drawEnd), endThumbX - endOffsetFromAngle, endThumbY - 40, linePaint);
    }

    /**
     * Invoked when slider starts moving or is currently moving. This method calculates and sets position and angle of the thumb.
     *
     * @param touchX Where is the touch identifier now on X axis
     * @param touchY Where is the touch identifier now on Y axis
     */
    private void updateSliderState(int touchX, int touchY, Thumb thumb) {
        int distanceX = touchX - circleCenterX;
        int distanceY = circleCenterY - touchY;
        double c = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        double angle = Math.acos(distanceX / c);
        if (distanceY < 0)
            angle = -angle;

        // If the user attempts to move the thumb from the bottom to the other side,
        // this will cancel the MotionEvent.ACTION_MOVE to stop them from looping the thumb around.
        float drawingAngle = toDrawingAngle(angle);
        if (drawingAngle > 80 && drawingAngle < 100) {
            isStartThumbSelected = false;
            isEndThumbSelected = false;
        }

        if (thumb == Thumb.START) {
            if (isNumberInOverflowedRange(drawingAngle, START_RANGE_FOR_START_THUMB, toDrawingAngle(endThumbAngle), MAX_CIRCLE_DEGREES)) {
                startThumbAngle = angle;
                rangeMovedListener.onStartSliderMoved(toDrawingAngle(angle));
            }
        } else if (thumb == Thumb.END) {
            if (isNumberInOverflowedRange(drawingAngle, toDrawingAngle(startThumbAngle), END_RANGE_FOR_END_THUMB, MAX_CIRCLE_DEGREES)) {
                endThumbAngle = angle;
                rangeMovedListener.onEndSliderMoved(toDrawingAngle(angle));
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
        rangeMovedListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // start moving the thumb (this is the first touch)
                int x = (int) ev.getX();
                int y = (int) ev.getY();

                int mThumbSize = startThumbSize;
                boolean isThumbStartPressed = x < startThumbX + mThumbSize
                        && x > startThumbX - mThumbSize
                        && y < startThumbY + mThumbSize
                        && y > startThumbY - mThumbSize;

                mThumbSize = endThumbSize;
                boolean isThumbEndPressed = x < endThumbX + mThumbSize
                        && x > endThumbX - mThumbSize
                        && y < endThumbY + mThumbSize
                        && y > endThumbY - mThumbSize;

                if (isThumbStartPressed) {
                    isStartThumbSelected = true;
                    updateSliderState(x, y, Thumb.START);
                } else if (isThumbEndPressed) {
                    isEndThumbSelected = true;
                    updateSliderState(x, y, Thumb.END);
                }

                if (rangeMovedListener != null) {
                    if (isStartThumbSelected)
                        rangeMovedListener.onStartSliderEvent(ThumbEvent.THUMB_PRESSED);
                    if (isEndThumbSelected)
                        rangeMovedListener.onEndSliderEvent(ThumbEvent.THUMB_PRESSED);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // still moving the thumb (this is not the first touch)
                if (isStartThumbSelected) {
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    updateSliderState(x, y, Thumb.START);
                } else if (isEndThumbSelected) {
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    updateSliderState(x, y, Thumb.END);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (rangeMovedListener != null) {
                    if (isStartThumbSelected)
                        rangeMovedListener.onStartSliderEvent(ThumbEvent.THUMB_RELEASED);
                    if (isEndThumbSelected)
                        rangeMovedListener.onEndSliderEvent(ThumbEvent.THUMB_RELEASED);
                }

                // finished moving (this is the last touch)
                isStartThumbSelected = false;
                isEndThumbSelected = false;
                break;
            }
        }

        invalidate();
        return true;
    }

}
