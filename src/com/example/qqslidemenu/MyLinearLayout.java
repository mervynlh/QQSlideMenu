package com.example.qqslidemenu;


import com.example.qqslidemenu.SlideMenu.DragState;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 当slideMenu打开的时候，拦截并消费掉触摸事件
 * 
 * @author Administrator
 * 
 */
public class MyLinearLayout extends LinearLayout {
	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		Log.e("tag", "MyLinearLayout 初始化");
	}
	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.e("tag", "MyLinearLayout 初始化");
	}

	public MyLinearLayout(Context context) {
		super(context);
		Log.e("tag", "MyLinearLayout 初始化");
	}
	
	
	private SlideMenu slideMenu;
	public void setSlideMenu(SlideMenu slideMenu){
		this.slideMenu = slideMenu;
		Log.e("tag", "用户调用setSlideMenu");
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(slideMenu!=null && slideMenu.getCurrentState()==DragState.Open){
			//如果slideMenu打开则应该拦截并消费掉事件
			Log.e("tag", "MyLinearLayout拦截点击事件");
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(slideMenu!=null && slideMenu.getCurrentState()==DragState.Open){
			if(event.getAction()==MotionEvent.ACTION_UP){
				Log.e("tag", "MyLinearLayout 消费点击事件");
				//抬起则应该关闭slideMenu
				slideMenu.close();
				
				Log.e("tag", "slideMenu打开，并且鼠标抬起onTouchEvent");
			}
			
			//如果slideMenu打开则应该拦截并消费掉事件
			return true;
		}
		return super.onTouchEvent(event);
	}
}
