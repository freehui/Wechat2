package com.freehui.wechat2;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.NoTieleActivity;

public class MainActivity extends NoTieleActivity implements OnClickListener{
	
	Button registerbtn;
	Button loginbtn;
	Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		if (WechatSocket.islogin == true){
			//已登录
			
			startActivity(new Intent(this,ActivityMain.class));
			finish();
			return;
		}			
		
		setContentView(R.layout.activity_main);
			
		registerbtn = ((Button)(findViewById(R.id.register)));
		registerbtn.setOnClickListener((android.view.View.OnClickListener) this);
		loginbtn = ((Button)(findViewById(R.id.login)));
		loginbtn.setOnClickListener((android.view.View.OnClickListener) this);
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				SharedPreferences sp = getSharedPreferences("user",Activity.MODE_PRIVATE);
				String phone = sp.getString("phone", null);
				String password = sp.getString("password", null);
				if (phone != null && password != null){
					//用户登录过，那么尝试登录
					
					WechatSocket sock = WechatSocket.getInstance();
					sock.setPhone(phone);
					sock.setPassword(password);
					if (sock.login(MainActivity.this) == 2){
						//登录成功
						
						startActivity(new Intent(MainActivity.this,ActivityMain.class));
						finish();
						return;
					}
				}
				
				WechatSocket.getInstance().disconnected();
				
				//登录失败或之前未登录过，那么显示注册和登录按钮
				registerbtn.setVisibility(View.VISIBLE);
				loginbtn.setVisibility(View.VISIBLE);
				ObjectAnimator anim = ObjectAnimator.ofFloat(registerbtn,"alpha",0,1);
				ObjectAnimator animloginbtn = ObjectAnimator.ofFloat(loginbtn,"alpha",0,1);
				anim.setDuration(500);//持续时间 
				animloginbtn.setDuration(500);//持续时间 
				anim.start();
				animloginbtn.start();
			}
		}, 1300);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		if (arg0.equals(registerbtn)){
	
			bootActivity(ActivityRegsiter.class);
		}else if (arg0.equals(loginbtn)){
			
			bootActivity(ActivityLogin.class);
		}		
	} 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		getMenuInflater().inflate(R.menu.main, menu); 
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
			finish();
		
		return true;
	}		
}
