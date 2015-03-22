package com.jacob.measure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by jacob-wj on 2015/3/22.
 */
public class MeasureDashboardView extends View{
    private int mRadiusDashBoard;

    private int mRadiusIndicate;

    private int mRadiusRuler;

    private int mLayoutSize;

    private int mCenterX;

    private int mCenterY;

    private int mTextNumberSize = spToPx(15) ;

    private int LOW_COLOR = Color.parseColor("#FF6FB1FF");
    private int NORMAL_COLOR = Color.parseColor("#FF20FF80");
    private int HIGH_COLOR= Color.parseColor("#FFFF9868");

    private Paint mPaintDashboard = new Paint();
    private Paint mPaintText = new Paint();
    private Paint mPaintNumber = new Paint();
    private Paint mPaintIndicate = new Paint();

    private int mStrokeWidth = 45;

    private Path mTextPath = new Path();

    private RectF mRectDashboard;
    private RectF mRectIndicate;

    private Rect mRectText ;
    private Rect mRectTitle;
    private Rect mRectNumber;

    private String mTextIndicate = "舒适温度";
    private String mTextTitle= "温湿度计";

    private int mCountNumber = 8;
    private int mCountChild =  mCountNumber*5;

    private double mAngleNumber= Math.PI/mCountNumber;
    private double mAngleChild= Math.PI/mCountChild;

    public static final int MIN_VALUE= 20;
    public static final int MAX_VALUE = 100;

    private int[] mNumber = new int[]{20,30,40,50,60,70,80,90,100};

    private int result =45;
    private double startAngle = 0;

    public MeasureDashboardView(Context context) {
        this(context,null);
    }

    public MeasureDashboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeasureDashboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDashboardPaint();

        initTextPaint();

        initNumberPaint();

        initIndicatePaint();


        //默认半径给150dp
        mRadiusDashBoard = dpToPx(150);
        mRadiusIndicate = mRadiusDashBoard-mStrokeWidth/2-dpToPx(3);
        mRadiusRuler= mRadiusIndicate-dpToPx(7);

        mLayoutSize = mRadiusDashBoard *2+mStrokeWidth*2;
        mCenterX = mLayoutSize/2;
        mCenterY = mLayoutSize/2;

        mRectDashboard = new RectF(mStrokeWidth,mStrokeWidth,mLayoutSize-mStrokeWidth,mLayoutSize-mStrokeWidth);
        mRectIndicate =  new RectF(mCenterX-mRadiusIndicate,mCenterX-mRadiusIndicate,
                mCenterX+mRadiusIndicate,mLayoutSize-mStrokeWidth);

        mRectText = new Rect();
        mPaintText.getTextBounds(mTextIndicate,0, mTextIndicate.length(),mRectText);
        mTextPath.addArc(mRectDashboard,240,60);

        mRectTitle = new Rect();
        mPaintText.getTextBounds(mTextTitle,0, mTextTitle.length(),mRectTitle);

        mRectNumber = new Rect();

        startAngle = getAngleFromResult(result);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mLayoutSize,mLayoutSize/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);

        //绘制低温范围的颜色带
        mPaintDashboard.setColor(LOW_COLOR);
        canvas.drawArc(mRectDashboard,180,59,false,mPaintDashboard);

        //绘制舒适温度范围的颜色带
        mPaintDashboard.setColor(NORMAL_COLOR);
        canvas.drawArc(mRectDashboard,240,59,false,mPaintDashboard);

        //绘制高温范围的颜色带
        mPaintDashboard.setColor(HIGH_COLOR);
        canvas.drawArc(mRectDashboard, 300, 60, false, mPaintDashboard);

        //绘制舒适范围颜色带的文字
        canvas.drawTextOnPath(mTextIndicate,mTextPath,mRadiusDashBoard/2-mRectText.width()/2,mRectText.height()/2,mPaintText);

        //绘制title
        canvas.drawText(mTextTitle,mCenterX-mRectTitle.width()/2,mCenterY/2+mRectTitle.height(),mPaintText);

        //绘制刻度盘的弧形
        canvas.drawArc(mRectIndicate,180,180,false,mPaintNumber);

        //绘制刻度盘上的刻度
        for (int i = 0; i <= mCountNumber; i++) {
            double angle = i*mAngleNumber;
            Point point = getRulerPoint(angle);
            //绘制大刻度
            canvas.drawLine((float)(mCenterX+(mRadiusIndicate-2)*Math.cos(Math.PI+i*mAngleNumber)),
                    (float)(mCenterY+(mRadiusIndicate-2)*Math.sin(Math.PI+i*mAngleNumber)),
                    point.x,
                    point.y,
                    mPaintNumber);

            //绘制圆盘上的数字
            String number = String.valueOf(mNumber[i]);
            mPaintNumber.getTextBounds(number,0,number.length(),mRectNumber);

              if(i>mCountNumber/2){
                canvas.drawText(number,point.x-mRectNumber.width()-dpToPx(10),point.y+mRectNumber.height(),mPaintText);
            }else{
                canvas.drawText(number,point.x,point.y+mRectNumber.height(),mPaintText);
            }

        }

        //绘制小的子刻度
        for (int i = 0; i <= mCountChild; i++) {
            canvas.drawCircle((float)(mCenterX+(mRadiusIndicate-2)*Math.cos(Math.PI+i*mAngleChild)),
                    (float)(mCenterY+(mRadiusIndicate-2)*Math.sin(Math.PI+i*mAngleChild)),
                    dpToPx(1),mPaintNumber);
        }

        //绘制中心点的圆
        canvas.drawCircle(mCenterX,mCenterX,dpToPx(10),mPaintIndicate);

        //绘制指针
        Point point = getRulerPoint(startAngle);
        canvas.drawLine(mCenterX,mCenterY,point.x,point.y,mPaintIndicate);
    }




    private void initIndicatePaint() {
        mPaintIndicate.setAntiAlias(true);
        mPaintIndicate.setDither(true);
        mPaintIndicate.setStrokeJoin(Paint.Join.ROUND);
        mPaintIndicate.setStrokeCap(Paint.Cap.ROUND);
        mPaintIndicate.setStrokeWidth(7);
        mPaintIndicate.setStyle(Paint.Style.FILL);
        mPaintIndicate.setColor(Color.parseColor("#FFA7EEFF"));
    }

    private void initNumberPaint() {
        mPaintNumber.setAntiAlias(true);
        mPaintNumber.setDither(true);
        mPaintNumber.setStrokeWidth(3);
        mPaintNumber.setStyle(Paint.Style.STROKE);
        mPaintNumber.setColor(Color.WHITE);
    }

    private void initTextPaint() {
        mPaintText.setAntiAlias(true);
        mPaintText.setDither(true);
        mPaintText.setTextSize(mTextNumberSize);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setStyle(Paint.Style.STROKE);
    }

    private void initDashboardPaint() {
        mPaintDashboard.setAntiAlias(true);
        mPaintDashboard.setDither(true);
        mPaintDashboard.setStrokeWidth(mStrokeWidth);
        mPaintDashboard.setStyle(Paint.Style.STROKE);
    }



    private int dpToPx(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,getResources().getDisplayMetrics());
    }

    private int spToPx(int sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp,getResources().getDisplayMetrics());
    }

    /**
     * 通过角度得到对应的坐标值
     */
    private Point getRulerPoint(double angle){
        Point point = new Point();
        point.x = (int) (mCenterX+mRadiusRuler*Math.cos(Math.PI + angle));
        point.y = (int) (mCenterY+mRadiusRuler*Math.sin(Math.PI + angle));
        return point;
    }

    /**
     * 通过数值得到角度位置
     */
    private double getAngleFromResult(int result){
        return Math.PI*(result-MIN_VALUE)/(MAX_VALUE-MIN_VALUE);
    }

}
