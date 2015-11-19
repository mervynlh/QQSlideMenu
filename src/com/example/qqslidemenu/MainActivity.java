package com.example.qqslidemenu;

import java.util.Random;

import com.example.qqslidemenu.SlideMenu.OnDragStateChangeListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private ListView menuView;
	private ListView mainView;
	private ImageView ivHead;
	private SlideMenu slideMenu;
	private MyLinearLayout my_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	private void initData() {
		// 给侧边栏设置数据
		menuView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView view = (TextView) super.getView(position, convertView,
						parent);
				view.setTextColor(Color.WHITE);
				return view;
			}
		});

		// 给主界面设置数据
		mainView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Constant.NAMES));

		// 回调
		slideMenu.setOnDragStateChangeListener(new OnDragStateChangeListener() {

			@Override
			public void onOpen() {
				Log.i("tag", "onOpen");
				// 让侧边栏随机的平滑到一个位置
				menuView.smoothScrollToPosition(new Random().nextInt(menuView
						.getCount()));
			}

			@Override
			public void onDrag(float fraction) {
				Log.i("tag", "fraction:" + fraction);

				// 主界面的头像的透明度随移动的百分比变化,
				ViewHelper.setAlpha(ivHead, 1 - fraction);
			}

			@Override
			public void onClose() {
				Log.i("tag", "onClose");
				// 头像抖动
				ViewPropertyAnimator.animate(ivHead).translationXBy(15)
						.setInterpolator(new CycleInterpolator(4))
						.setDuration(500).start();

			}
		});
		
		my_layout.setSlideMenu(slideMenu);

	}

	private void initView() {
		menuView = (ListView) findViewById(R.id.menu_listview);
		mainView = (ListView) findViewById(R.id.main_listview);
		slideMenu = (SlideMenu) findViewById(R.id.slideMenu);
		ivHead = (ImageView) findViewById(R.id.iv_head);
		my_layout = (MyLinearLayout) findViewById(R.id.my_layout);
	}

}
