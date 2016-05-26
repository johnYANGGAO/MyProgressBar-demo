package com.yonggao.myprogressbardemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * ���εĽ�������
 * 
 * @author zhou
 * 
 */
public class ProgressbarCust extends View {


	private float mProgress;
	private int mAnimaProgress = -1;

	
	private Paint mForPain;
	
	private Paint mBacPain;
	private Paint mTextPain;


	private int mForColor;
	
	private int mBacColor;
	/** ��ʼ�Ƕ� */
	private float mStartAngle;
	
	private float mSweepAngle;
	
	private float mFontSize;

	/** �������� */
	RectF oval = new RectF();
	/** ���ֵĿ�� */
	float textWidth;

	private boolean withText = false;
	private boolean inAnimation = false;

	public ProgressbarCust(Context context) {
		this(context, null);
	}

	public ProgressbarCust(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ProgressbarCust(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		obtionAttrs(context, attrs);
		init();
	}

	/** ��ȡ�Զ������ԣ���ɫ���Ƕ� */
	private void obtionAttrs(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CircleProgress);
		mForColor = a.getColor(R.styleable.CircleProgress_forColor,
				Color.rgb(10, 20, 220));
		mBacColor = a.getColor(R.styleable.CircleProgress_bacColor,
				Color.rgb(200, 200, 200));
		mStartAngle = a.getFloat(R.styleable.CircleProgress_startAngle, 160);
		mSweepAngle = a.getFloat(R.styleable.CircleProgress_sweepAngle,
				540 - 2 * mStartAngle);
		mFontSize = a.getDimension(R.styleable.CircleProgress_fontSize, 0);
		mProgress = a.getInt(R.styleable.CircleProgress_progresss, 0);
		setWithText(mFontSize != 0);
		a.recycle();
	}

	private Paint mUncompletePaint, mFillPaint;

	private void init() {
		mForPain = new Paint();
		mBacPain = new Paint();
		mTextPain = new Paint();
		// BlurMaskFilter filter = new BlurMaskFilter(30,
		// BlurMaskFilter.Blur.OUTER);
		float strokeWidth = getResources().getDimension(R.dimen.strokeWidth);
//		mForPain.setAntiAlias(true);
//		mForPain.setStyle(Paint.Style.STROKE);
//		mForPain.setColor(mForColor);
//		mForPain.setStrokeCap(Paint.Cap.ROUND);
//		mForPain.setStrokeWidth(strokeWidth);

		mUncompletePaint = new Paint();
		mUncompletePaint.setColor(mForColor);
		mUncompletePaint.setAntiAlias(true);

		mFillPaint = new Paint();
		mFillPaint.setColor(Color.WHITE);
		mFillPaint.setAntiAlias(true);

		mBacPain.setAntiAlias(true);
		mBacPain.setStyle(Paint.Style.STROKE);
		mBacPain.setColor(mBacColor);
		mBacPain.setStrokeCap(Paint.Cap.ROUND);
		mBacPain.setStrokeWidth(strokeWidth);
		// mBacPain.setMaskFilter(filter);
		// mBacPain.setShadowLayer(5, 2, 2, Color.GRAY);

		mTextPain.setAntiAlias(true);
		mTextPain.setColor(Color.BLACK);
		// mTextPain.setShadowLayer(5, 2, 2, Color.GRAY);
		mTextPain.setStrokeCap(Paint.Cap.ROUND);
		mTextPain.setTextSize(mFontSize);
		// mTextPain.setTypeface(Typeface.DEFAULT_BOLD);
		textWidth = mTextPain.measureText((int) mProgress + "%");

		Paint.FontMetrics fontMetrics = mTextPain.getFontMetrics();
		fontTotalHeight = fontMetrics.bottom + fontMetrics.top;
	}

	float fontTotalHeight;

	public int getProgress() {
		return (int) (mProgress + 0.5f);
	}

	public void setProgress(int mProgress) {
		setProgress(mProgress, false);
	}

	private void setProgress(float mProgress) {
		this.mProgress = mProgress;
		textWidth = mTextPain.measureText((int) mProgress + "%");
		invalidate();
	}

	public void setProgress(float progress, boolean withAnim) {
		if (withAnim) {
			if (inAnimation) {
				clearAnimation();
			}
			ProgressAnimation animation = new ProgressAnimation(progress);
			this.startAnimation(animation);
			inAnimation = true;
		} else if (progress >= 0) {
			this.mProgress = progress;
			textWidth = mTextPain.measureText(mProgress + "%");
			invalidate();
		}
	}

	/**
	 * �Ƿ���ʾ�м����� �ڲ�����Ϊ���������С������(CircleProgress_fontSize)ʱ��Ĭ�ϲ���ʾ(false).
	 * 
	 * @param withText
	 */
	public void setWithText(boolean withText) {
		this.withText = withText;
	}

	private float mTotalRaidus, Raidus;
	private float mCX;
	private float mCY;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		calcuateBeforeDraw();
		canvas.drawCircle(mCX, mCY, mTotalRaidus, mUncompletePaint);
		canvas.drawCircle(mCX, mCY, Raidus, mFillPaint);
		canvas.drawArc(oval, mStartAngle, mSweepAngle, false, mBacPain);
		canvas.drawArc(oval, mStartAngle, mSweepAngle * mProgress / 100, false,
				mForPain);
		if (withText)
			canvas.drawText((int) mProgress + "%",
					(getWidth() - textWidth) / 2,
					(getWidth() - fontTotalHeight) / 2, mTextPain);
	}

	private void calcuateBeforeDraw() {
		mCX = getMeasuredWidth() / 2;
		mCY = getMeasuredHeight() / 2;
		// ���ú���Բ����Բ�İ뾶
		mTotalRaidus = getMeasuredWidth() / 2-5;
		if (Raidus <= 0 || Raidus >= mTotalRaidus) {
			Raidus = mTotalRaidus * 0.9f;

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int mode = MeasureSpec.getMode(heightMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (mode == MeasureSpec.AT_MOST) {
			int width = getMeasuredWidth();
			final int measureSpec = MeasureSpec.makeMeasureSpec(width,
					MeasureSpec.EXACTLY);
			super.onMeasure(widthMeasureSpec, measureSpec);
		}
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

	public class ProgressAnimation extends Animation {

		float start, end;

		public ProgressAnimation(float end) {
			start = getProgress();
			this.end = end;
			// ʱ�����е��������ܶ�����
			// �������Ա�֤��ζ��������ٵ�.
			// ����ʱ���Ǹ����κ���,����Ч���ȽϾ���.
			final float interval = Math.abs(end - start);
			if (interval >= 1)
				setDuration((long) (200 * Math.sqrt(interval)));
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			setProgress((start + (end - start) * interpolatedTime));
			if (interpolatedTime == 1) {
				inAnimation = false;
			}
		}

		@Override
		public void start() {
			super.start();
			inAnimation = true;
		}

		@Override
		public void cancel() {
			super.cancel();
			inAnimation = false;
		}

		@Override
		public boolean willChangeBounds() {
			return false;
		}
	}
}
