package com.freehui.wechat2.baseclass;

import android.view.KeyEvent;

import com.example.wechat2.R;

public class SlideActivity extends BaseActivity {

	
	/*
	 * 关闭当前 Activity 并开启滑动动画
	 * */
	public void close(){
		
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	/*
	 * 默认的退出是 close，可以在子类重写他换成 finish。
	 * */
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
			close();
		
		return true;
	}		
}
