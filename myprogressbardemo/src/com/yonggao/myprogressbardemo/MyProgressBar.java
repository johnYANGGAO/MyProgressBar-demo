package com.yonggao.myprogressbardemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author YongGao 此类也有其他种写法，但基本上类似。就这几个方法
 * 
 */
public class MyProgressBar extends View {

	static String TAG = "MyProgress";
	private Context mContext;
	// 总共我们需要绘制四个图，所以需要四个画笔
	private Paint mCompletePaint;
	private Paint mUncompletePaint;
	private Paint mTextPaint;
	private Paint mFillPaint;
	private float mTextSize;
	private int mCompleteColor;
	private int mUncompleteColor;
	private int mTextColor;
	private int mFillColor;
	private int mProgress;
	private int mMaxProgress;
	private float Raidus;
	private float mTotalRaidus;
	private float mCX;
	private float mCY;
	private boolean isDrawText;

	private final static int DEFAULT_COMPLETE_PAINT_COLOR = Color
			.parseColor("#78909C");
	private final static int DEFAULT_UNCOMPLETE_PAINT_COLOR = Color
			.parseColor("#CFD8DC");
	private final static int DEFAULT_TEXT_COLOR = Color.parseColor("#0091EA");
	private final static int DEFAULT_FILL_COLOR = Color.parseColor("#FAFAFA");
	private final static float MAX_SIZE = 160;
	private final static float MIN_SIZE = 80;

	public MyProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// 获取自定义的属性值
		getAttrs(attrs);
		initializePainters();
	}

	public void getAttrs(AttributeSet attrs) {
		TypedArray attributes = mContext.getTheme().obtainStyledAttributes(
				attrs, R.styleable.myProgressBar, 0, 0);
		mCompleteColor = attributes.getColor(
				R.styleable.myProgressBar_complete_color,
				DEFAULT_COMPLETE_PAINT_COLOR);
		mUncompleteColor = attributes.getColor(
				R.styleable.myProgressBar_uncomplete_color,
				DEFAULT_UNCOMPLETE_PAINT_COLOR);
		mTextColor = attributes.getColor(R.styleable.myProgressBar_text_color,
				DEFAULT_TEXT_COLOR);
		Raidus = attributes.getDimension(R.styleable.myProgressBar_fill_radius,
				0);
		mTextSize = attributes.getDimension(
				R.styleable.myProgressBar_text_size, sptopx(12));
		mFillColor = attributes.getInt(R.styleable.myProgressBar_fill_color,
				DEFAULT_FILL_COLOR);
		setMaxProgress(attributes.getInt(R.styleable.myProgressBar_maxprogress,
				100));
		setProgress(attributes.getInt(R.styleable.myProgressBar_progress, 0));
		isDrawText = attributes.getInt(
				R.styleable.myProgressBar_text_visibility, 1) == 1;
		// 对象回收到池里
		attributes.recycle();

	}

	/**
	 * 初始化绘制内圆，外圆，弧（进度），text画笔
	 * 
	 * */
	private void initializePainters() {
		mCompletePaint = new Paint();
		mCompletePaint.setColor(mCompleteColor);
		mCompletePaint.setAntiAlias(true);
		mCompletePaint.setStyle(Paint.Style.STROKE);
		mCompletePaint.setStrokeWidth(18);
		mCompletePaint.setStrokeCap(Paint.Cap.ROUND);

		mUncompletePaint = new Paint();
		mUncompletePaint.setColor(mUncompleteColor);
		mUncompletePaint.setAntiAlias(true);
		// mUncompletePaint.setStrokeCap(Paint.Cap.ROUND);

		mFillPaint = new Paint();
		mFillPaint.setColor(mFillColor);
		mFillPaint.setAntiAlias(true);

		mTextPaint = new Paint();
		mTextPaint.setColor(mTextColor);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setAntiAlias(true);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		int left, right, top, bottom;
		if ((w > 0) && (h > 0) && ((w != oldw) || (h != oldh))) {
			left = getPaddingLeft();
			right = getPaddingRight();
			top = getPaddingTop();
			bottom = getPaddingBottom();
			oval.set(left, top, w - right, w - bottom);
		}
	}

	/** 绘制区域 */
	RectF oval = new RectF();

	/**
	 * 计算当前view的宽高
	 * 
	 * */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int minSize = Math.min(widthSize, heightSize);
		int resultSize = 0;
		if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST
				|| MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			resultSize = (int) dptopx(MIN_SIZE);
		} else {
			if (minSize >= dptopx(MAX_SIZE)) {
				resultSize = (int) dptopx(MAX_SIZE);
			} else if (minSize <= dptopx(MIN_SIZE)) {
				resultSize = (int) dptopx(MIN_SIZE);
			} else {
				resultSize = minSize;
			}
		}
		setMeasuredDimension(resultSize, resultSize);
	}

	private void calcuateBeforeDraw() {
		mCX = getMeasuredWidth() / 2;
		mCY = getMeasuredHeight() / 2;
		// 设置好内圆和外圆的半径
		mTotalRaidus = getMeasuredWidth() / 2 - 6;
		if (Raidus <= 0 || Raidus >= mTotalRaidus) {
			Raidus = mTotalRaidus * 0.9f - 7;

		}
		// 保证text不超出圆的范围
		while (mTextPaint.measureText("100%") >= Raidus * 2) {
			mTextPaint.setTextSize(mTextPaint.getTextSize() - 3);
		}
	}

	/**
	 * 开始绘制
	 * 
	 * */

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mProgress > mMaxProgress) {
			Log.w(TAG, "progress 超出范围  ");
			mProgress = mMaxProgress;
		}
		calcuateBeforeDraw();
		drawUnCompleteCircle(canvas);
		drawCompleteCircle(canvas);
		drawFillCircleBlock(canvas);
		if (isDrawText)
			drawCenterText(canvas);

	}

	/**
	 * 绘制圆相关
	 * 
	 * */
	private void drawUnCompleteCircle(Canvas canvas) {
		canvas.drawCircle(mCX, mCY, mTotalRaidus, mUncompletePaint);
	}

	/**
	 * 方法介绍，绘制进度弧 public void drawArc(RectF oval, float startAngle, float
	 * sweepAngle, boolean useCenter, Paint paint) oval :指定圆弧的外轮廓矩形区域。
	 * startAngle: 圆弧起始角度，单位为度。 sweepAngle: 圆弧扫过的角度，顺时针方向，单位为度。 useCenter:
	 * 如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。 paint: 绘制圆弧的画板属性，如颜色，是否填充等。
	 */
	private void drawCompleteCircle(Canvas canvas) {
		// RectF oval = new RectF(0, 0, getWidth(), getHeight());
		float sweepAngle = 360 * (getProgress() / (getMaxProgress() * 1.0f));
		canvas.drawArc(oval, -90, -sweepAngle, false, mCompletePaint);
	}

	/**
	 * 绘制内圆
	 * 
	 * */
	private void drawFillCircleBlock(Canvas canvas) {
		canvas.drawCircle(mCX, mCY, Raidus, mFillPaint);
	}

	/**
	 * 绘制进度text相关
	 * 
	 * */
	private void drawCenterText(Canvas canvas) {
		String text = (int) (100 * ((getProgress() / (1.0f * getMaxProgress()))))
				+ "%";
		Rect textBound = new Rect();
		mTextPaint.getTextBounds(text, 0, text.length(), textBound);
		float textwidth = mTextPaint.measureText(text);
		float textheight = textBound.height();
		canvas.drawText(text, mCX - (textwidth / 2.0f), mCY
				+ (textheight / 2.0f), mTextPaint);
	}

	/**
	 * 对外开放方法
	 * 
	 * */
	public void setMaxProgress(int maxProgress) {
		this.mMaxProgress = Math.abs(maxProgress);
	}

	public int getMaxProgress() {
		return mMaxProgress;
	}

	/**
	 * 注意每一次set要调用invalidate 方法 重走ondraw方法
	 * 
	 * */
	public void setProgress(int progress) {
		this.mProgress = progress;
		if (mProgress > mMaxProgress || mProgress < 0) {
			return;
		}
		invalidate();
	}

	public int getProgress() {
		return mProgress;
	}

	public void setCompleteColor(int color) {
		mCompleteColor = color;
		invalidate();
	}

	public void setUnCompleteColor(int color) {
		mUncompleteColor = color;
		invalidate();
	}

	public void setTextColor(int color) {
		mTextColor = color;
		invalidate();
	}

	/**
	 * px转dp ，dp转px相关
	 * 
	 * */
	private float dptopx(float dp) {
		float scale = getResources().getDisplayMetrics().density;
		return scale * dp + 0.5f;
	}

	private float sptopx(float sp) {
		float scale = getResources().getDisplayMetrics().scaledDensity;
		return scale * sp;
	}
}
