package com.example.qqslidemenu;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SlideMenu extends FrameLayout {

	private ViewDragHelper viewDraghelper;
	private float dragRange;// 拖拽的范围
	private View menuView;// 侧边栏
	private View mainView;// 主菜单
	private int width;// 当前控件的的宽
	private FloatEvaluator floatEvaluator;// float计算器
	private IntEvaluator intEvaluator;// int计算器

	public SlideMenu(Context context) {
		super(context);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	enum DragState {
		Open, Close;
	}

	// 定义当前的状态
	private DragState mCurrentState=DragState.Close;

	private void init() {
		// 初始化ViewDraghelper
		viewDraghelper = ViewDragHelper.create(this, callback);
		floatEvaluator = new FloatEvaluator();
		intEvaluator = new IntEvaluator();
	}

	

	@Override
	protected void onFinishInflate() {
		if (getChildCount() != 2) {
			throw new IllegalArgumentException("SlideMenu only have 2 childre！");
		}
		// 获取View
		menuView = getChildAt(0);
		mainView = getChildAt(1);
	}

	// 这个方法在onMeasure方法之后执行，用于获取当前View或子View的宽高，省去了测量的方法
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = getMeasuredWidth();
		dragRange = width * 0.6f;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return viewDraghelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDraghelper.processTouchEvent(event);
		return true;
	}

	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

		// 是否捕获当前的子View，返回true就是捕获并解析，false就是不捕获
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == menuView || child == mainView;
		}

		// 获取当前子View的拖拽范围，但实际不起到限制作用，只是用于过程的显示
		@Override
		public int getViewHorizontalDragRange(View child) {
			return (int) dragRange;
		}

		// 控制child在水平方向的移动 left:
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == mainView) {
				if (left < 0)
					left = 0;// 限制menuView的左边界
				if (left > dragRange)
					left = (int) dragRange;// 限制menuView的右边界
			}
			return left;
		}

		// 拖拽的伴随移动
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			// 手动固定menuView,移动的时候本身不动，让mainView移动
			if (changedView == menuView) {
				// 固定menuView不动
				menuView.layout(0, 0, menuView.getMeasuredWidth(),
						menuView.getMeasuredHeight());
				// 让mainView移动，限制边界
				int newLeft = mainView.getLeft() + dx;
				if (newLeft < 0)
					newLeft = 0;
				if (newLeft > dragRange)
					newLeft = (int) dragRange;
				mainView.layout(newLeft, mainView.getTop() + dy, newLeft
						+ mainView.getMeasuredWidth(), mainView.getBottom()
						+ dy);
			}

			// 1. 计算移动的百分比
			float fraction = mainView.getLeft() / dragRange;
			// 2. 执行伴随动画
			executeAnim(fraction);
			// 3. 根据百分比更改状态，调用接口
			if (fraction == 0 && mCurrentState!=DragState.Close) {
				// 更改状态为关闭，并回调关闭的方法
				mCurrentState=DragState.Close;
				
				Log.i("tag", "state:"+mCurrentState);
				
				if (listener!=null)listener.onClose();
			}else if(fraction==1f&&mCurrentState!=DragState.Open){
				mCurrentState=DragState.Open;
				
				Log.i("tag", "state:"+mCurrentState);
				
				if(listener!=null)listener.onOpen();
			}
			// 将drag的fraction暴漏给外界
			if (listener != null) {
				
				Log.i("tag", "listener:"+listener);
				
				listener.onDrag(fraction);
			}
		}

		// 释放之后的移动
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (mainView.getLeft() < dragRange / 2) {
				// 关闭侧边栏,打开主菜单
				close();
			} else {
				// 开启侧边栏，关闭主菜单
				open();
			}
			// 处理用户的稍微滑动
			if (xvel > 200 && mCurrentState != DragState.Open) {
				open();
			} else if (xvel < -200 && mCurrentState != DragState.Close) {
				close();
			}
		}

	};

	public void computeScroll() {
		if (viewDraghelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		}
	}

	protected void executeAnim(float fraction) {
		// 缩小mainView
		ViewHelper.setScaleX(mainView,
				floatEvaluator.evaluate(fraction, 1f, 0.8f));
		ViewHelper.setScaleY(mainView,
				floatEvaluator.evaluate(fraction, 1f, 0.8f));
		// 移动menuView
		ViewHelper.setTranslationX(menuView, intEvaluator.evaluate(fraction,
				-menuView.getMeasuredWidth() / 2, 0));
		// 放大menuView
		ViewHelper.setScaleX(menuView,
				floatEvaluator.evaluate(fraction, 0.5f, 1f));
		ViewHelper.setScaleY(menuView,
				floatEvaluator.evaluate(fraction, 0.5f, 1f));
		// menuView透明度变化
		ViewHelper.setAlpha(menuView,
				floatEvaluator.evaluate(fraction, 0.3f, 1f));
		// 设置背景颜色渐变，从深色变成透明
		getBackground().setColorFilter(
				(Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,
						Color.TRANSPARENT), Mode.SRC_OVER);
	};

	/**
	 * 关闭主菜单的方法
	 */
	public void close() {
		viewDraghelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
		// 刷新界面
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}

	/**
	 * 打开主菜单的方法
	 */
	public void open() {
		viewDraghelper.smoothSlideViewTo(mainView, (int) dragRange,
				mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}

	private OnDragStateChangeListener listener;

	public void setOnDragStateChangeListener(OnDragStateChangeListener listener) {
		this.listener = listener;
	}

	public interface OnDragStateChangeListener {
		void onOpen();

		void onClose();

		// 正在拖拽的时候的方法，把拖拽的百分比暴露出去，便于做其他的操作
		void onDrag(float fraction);
	}

	public DragState getCurrentState() {
		return mCurrentState;
	}

}
