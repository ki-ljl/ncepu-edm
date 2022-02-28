package com.example.NCEPU.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.NCEPU.R;

public class MajorSortProgressView extends View {
    /**
     * 背景的圆
     */
    private Paint mPaintOut;
    /**
     * 当前的圆
     */
    private Paint mPaintCurrent;
    /**
     * 字体
     */
    private Paint mPaintTextTop;
    private Paint mPaintTextBottom;

    /**
     * 自定义属性
     */
    private float mTextSizeTop;
    private float mPaintWidth;
    private int mPaintColor = getResources().getColor(R.color.paint_current);
    private int mTextColorTop = Color.BLACK;
    private int mTextColorBottom = Color.BLACK;
    private float mTextSizeBottom;
    /**
     * 开始的角度
     * 直角坐标系
     * 左边  180
     * 上面  270
     * 右边  0
     * 下边  90
     */
    private int startAngle = 135;
    /**
     * 要画的圆弧的度数
     * 圆 ：360
     */
    private int sweepAngle = 270;
    /**
     * 满绩
     */
    private double totalGPA = 5.0;
    /**
     * 当前绩点
     */
    private double currentGPA;

    private int majorRank;


    private OnLoadingCompleteListenter onLoadingCompleteListenter;

    public MajorSortProgressView(Context context) {
        this(context, null);
    }

    public MajorSortProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MajorSortProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 获取自定的属性
         */
        TypedArray typedArray = getContext()
                .obtainStyledAttributes(attrs, R.styleable.circle_progress_view);
        mPaintWidth = typedArray
                .getDimension(R.styleable.circle_progress_view_progress_paint_width,
                        dip2px(context, 10));
        mTextSizeTop = typedArray
                .getDimension(R.styleable.circle_progress_view_progress_text_size_top,
                        dip2px(context, 18));
        mPaintColor = typedArray.getColor(R.styleable.circle_progress_view_progress_paint_color,
                mPaintColor);
        mTextColorTop = typedArray.getColor(R.styleable.circle_progress_view_progress_text_color_top,
                mTextColorTop);
        mTextSizeBottom = typedArray
                .getDimension(R.styleable.circle_progress_view_progress_text_size_bottom,
                        dip2px(context, 18));
        mTextColorBottom = typedArray
                .getColor(R.styleable.circle_progress_view_progress_text_color_bottom,
                        mTextColorTop);
        typedArray.recycle();//释放

        mPaintOut = new Paint();
        mPaintOut.setAntiAlias(true);
        mPaintOut.setColor(getResources().getColor(R.color.paint_out));
        mPaintOut.setStrokeWidth(mPaintWidth);
        /**
         * 画笔样式
         *
         */
        mPaintOut.setStyle(Paint.Style.STROKE);
        /**
         * 笔刷的样式
         * Paint.Cap.ROUND 圆形
         * Paint.Cap.SQUARE 方型
         */
        mPaintOut.setStrokeCap(Paint.Cap.ROUND);

        mPaintCurrent = new Paint();
        mPaintCurrent.setAntiAlias(true);
        mPaintCurrent.setColor(mPaintColor);
        mPaintCurrent.setStrokeWidth(mPaintWidth);
        mPaintCurrent.setStyle(Paint.Style.STROKE);
        mPaintCurrent.setStrokeCap(Paint.Cap.ROUND);

        mPaintTextTop = new Paint();
        mPaintTextTop.setAntiAlias(true);
        mPaintTextTop.setColor(mTextColorTop);
        mPaintTextTop.setStyle(Paint.Style.STROKE);
        mPaintTextTop.setTextSize(mTextSizeTop);

        mPaintTextBottom = new Paint();
        mPaintTextBottom.setAntiAlias(true);
        mPaintTextBottom.setColor(mTextColorBottom);
        mPaintTextBottom.setStyle(Paint.Style.STROKE);
        mPaintTextBottom.setTextSize(mTextSizeBottom);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //宽度
        int width = MeasureSpec.getSize(widthMeasureSpec);
        //高度
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    public void setMajorRank(int majorRank) {
        this.majorRank = majorRank;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * mPaintWidth  圆弧的宽度
         *
         * RectF就相当于一个画布，画布有上下左右四个顶点，
         * 宽度为 right - left
         * 高度为 bottom - top
         *
         */
        RectF rectF = new RectF(mPaintWidth / 2,
                mPaintWidth / 2,
                getWidth() - mPaintWidth / 2,
                getHeight() - mPaintWidth / 2);

        canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaintOut);

        float currentAngle = (float) (currentGPA * sweepAngle / totalGPA);
        canvas.drawArc(rectF, startAngle, currentAngle, false, mPaintCurrent);

        String text = "专业排名:" + majorRank;
        String text1 = currentGPA + "/5.00";
        String text2 = "GPA";

        /**
         * 半径
         */
        float radius = (getWidth() - mPaintWidth) / 2;
        /**
         * 圆心和弦的距离
         */
        float dis = (float) Math.sqrt((radius * radius) / 2);

        /**
         * 绘制顶部文字
         */
        //测量文字的宽度
        float textWidth1 = mPaintTextTop.measureText(text, 0, text.length());
        //测量文字的高度
        float textHeight1 = (float) getTxtHeight(mPaintTextTop);
        float textHeight2 = (float) getTxtHeight(mPaintTextBottom);
        /**
         * 基线x的坐标即为：
         * view宽度的一半减去文字宽度的一半
         */
        float dx1 = getWidth() / 2 - textWidth1 / 2;
        /**
         * 基线y的坐标为：
         * view高度的一半减去文字高度的一半
         */
        float dy1 = getHeight() / 2 - textHeight1 / 2 + dis - textHeight2;
        /**
         * 绘制底部文字
         */
        float textWidth2 = mPaintTextBottom.measureText(text1, 0, text1.length());
        float dx2 = getWidth() / 2 - textWidth2 / 2;
        float dy2 = getHeight() / 2 - textHeight2 / 2 + dis;

        canvas.drawText(text, dx1, dy1, mPaintTextTop);
        canvas.drawText(text1, dx2, dy2, mPaintTextBottom);


        /**
         * 完成
         */
        if (getOnLoadingCompleteListenter() != null && currentGPA == totalGPA) {
            getOnLoadingCompleteListenter().onComplete();
        }

    }


    public double getmCurrent() {
        return currentGPA;
    }

    /**
     * 设置当前进度并且重新绘制界面
     *
     * @param mCurrent
     */
    public void setmCurrent(double mCurrent) {
        this.currentGPA = mCurrent;
        //重新绘制的方法
        invalidate();
    }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public double getTotalScore() {
        return totalGPA;
    }

    public void setTotalScore(double totalScore) {
        this.totalGPA = totalScore;
    }


    /**
     * 获取文字的高度
     *
     * @param mPaint
     * @return
     */
    public double getTxtHeight(Paint mPaint) {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        return Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    interface OnLoadingCompleteListenter {
        void onComplete();
    }

    public OnLoadingCompleteListenter getOnLoadingCompleteListenter() {
        return onLoadingCompleteListenter;
    }

    public void setOnLoadingCompleteListenter(OnLoadingCompleteListenter onLoadingCompleteListenter) {
        this.onLoadingCompleteListenter = onLoadingCompleteListenter;
    }
}
