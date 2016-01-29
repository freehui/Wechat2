package com.freehui.wechat2;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.NetworkActivity;
import com.freehui.wechat2.customview.ChangeColorIcon;
import com.freehui.wechat2.pages.Addresspage;
import com.freehui.wechat2.pages.Foundpage;
import com.freehui.wechat2.pages.Mainpage;
import com.freehui.wechat2.pages.Settingpage;

@SuppressLint("NewApi")
public class ActivityMain extends NetworkActivity implements OnClickListener {

	ViewPager viewpage;
	int currIndex = 0;// 当前页卡编号
	ChangeColorIcon images[];
	RelativeLayout re_weixin;
	RelativeLayout re_contact_list;
	RelativeLayout re_find;
	RelativeLayout re_profile;
	ArrayList<Fragment> fragmentlist = new ArrayList<Fragment>();
	Button iv_add;
	ViewGroup dialogview;
	PopupWindow dialog;
	PopupMenu submenu;
	boolean isshow = false;
	View addfriend;
	Bitmap iconImages[] = null;
	Intent intent;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
	
		SQLiteDatabase sql = WechatLocalSQLite.getDB();
				
		intent = getIntent();
		
		iv_add = (Button) findViewById(R.id.iv_add);
		iv_add.setOnClickListener(this);
		
		/* 载入 popupwindow 要显示的 view */
		dialogview = (ViewGroup) getLayoutInflater().inflate(
				R.layout.addpupopwindow, null);
		dialogview.setFocusable(true);
		dialogview.setFocusableInTouchMode(true);
		/* 设为点击 Menu 键后关闭，而他打开后会获取焦点，所以监听 Menu 键 */
		dialogview.setOnKeyListener(new OnKeyListener() {
		 
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (KeyEvent.ACTION_DOWN == event.getAction()
					&& keyCode == KeyEvent.KEYCODE_MENU) {
		 
					dialog.dismiss();
				}
				return false;
			}
		});		
		 
		images = new ChangeColorIcon[4];
		images[0] = (ChangeColorIcon) findViewById(R.id.ib_weixin);
		images[1] = (ChangeColorIcon) findViewById(R.id.ib_contact_list);
		images[2] = (ChangeColorIcon) findViewById(R.id.ib_find);
		images[3] = (ChangeColorIcon) findViewById(R.id.ib_profile);
		images[0].setIconAlpha(1);
		
		re_weixin = (RelativeLayout) findViewById(R.id.re_weixin);
		re_contact_list = (RelativeLayout) findViewById(R.id.re_contact_list);
		re_find = (RelativeLayout) findViewById(R.id.re_find);
		re_profile = (RelativeLayout) findViewById(R.id.re_profile);
		re_weixin.setOnClickListener(new MenuOnclick(0));
		re_contact_list.setOnClickListener(new MenuOnclick(1));
		re_find.setOnClickListener(new MenuOnclick(2));
		re_profile.setOnClickListener(new MenuOnclick(3));

		fragmentlist.add(new Mainpage());
		fragmentlist.add(new Addresspage());	
		fragmentlist.add(new Foundpage());
		fragmentlist.add(new Settingpage());

		viewpage = (ViewPager) findViewById(R.id.tabpager);
		viewpage.setAdapter(new Mainadapter(getSupportFragmentManager()));
		viewpage.setOnPageChangeListener(new WechatOnPageChangeListener());
		
		viewpage.setCurrentItem(0,false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.equals(iv_add)) {
			/* 如果 popupwindow 未创建，那么先创建 */
			if (dialog == null) {
			 
				dialog = new PopupWindow(dialogview,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				dialog.setFocusable(true);
				dialog.setOutsideTouchable(true);
				dialog.setBackgroundDrawable(new BitmapDrawable());
				
				dialogview.findViewById(R.id.addfriend).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
						dialog.dismiss();
						bootActivity(ActivityAddfriend.class);
					}
				});
			}
			/* 显示在按钮下面 */
			dialog.showAsDropDown(iv_add, -10, 0);
		}		
	}

	/*
	 * adapter
	 */
	class Mainadapter extends FragmentPagerAdapter {

		public Mainadapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub

			return (Fragment) (fragmentlist.get(arg0));
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragmentlist.size();
		}
	}

	class MenuOnclick implements OnClickListener {

		int index;

		MenuOnclick(int index) {

			this.index = index;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			if (index == currIndex)
				return;
			
			for (int i = 0;i < images.length;i++){
				
				if (i == index)
					images[index].setIconAlpha(1);
				else
					images[i].setIconAlpha(0);	
			}
			
			viewpage.setCurrentItem(index,false);
		}
	}

	class WechatOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// TODO Auto-generated method stub

			if (positionOffset > 0) {
				ChangeColorIcon left = images[position];
				ChangeColorIcon right = images[position + 1];
				
				left.setIconAlpha(1 - positionOffset);//变小
				right.setIconAlpha(positionOffset);
			}			
		}
		@Override
		public void onPageSelected(int index) {
			// TODO Auto-generated method stub

			currIndex = index;
		}
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK)
			finish();

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			onClick(iv_add);
		}

		return true;
	}
}
