package com.freehui.wechat2;
 
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.NoTieleActivity;
import com.freehui.wechat2.util.LoginRegisterOnFocusChangeListener;
import com.freehui.wechat2.windows.WechatAlert;

public class ActivityLogin extends NoTieleActivity implements OnClickListener{
	
	LinearLayout phonenumber;
	LinearLayout password;
	EditText phonenumber_value;
	EditText password_value;
	Button login_button;
	boolean is_key = false;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);
		setBack(findViewById(R.id.register_back));
		
		phonenumber = (LinearLayout) findViewById(R.id.phonenumber);
		password = (LinearLayout) findViewById(R.id.password);
		phonenumber_value = (EditText) findViewById(R.id.phonenumber_value);
		password_value = (EditText) findViewById(R.id.password_value);
		login_button = (Button) findViewById(R.id.login_button);

		password_value.setOnFocusChangeListener(new LoginRegisterOnFocusChangeListener(password));
		phonenumber_value.setOnFocusChangeListener(new LoginRegisterOnFocusChangeListener(phonenumber));
		phonenumber_value.addTextChangedListener(new MyTextWatcher());
		password_value.addTextChangedListener(new MyTextWatcher());	
		
		login_button.setOnClickListener(this);
	}
	
	class LoginAsyncTask extends AsyncTask{

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			
			WechatSocket sock = WechatSocket.getInstance();
			
			sock.setPhone(phonenumber_value.getText().toString());
			sock.setPassword(password_value.getText().toString());
			
			int ret = sock.login(ActivityLogin.this);		
			
			if (ret == 2){
				/* 登陆成功 */
				
				Intent intent = new Intent(ActivityLogin.this,ActivityMain.class);

				// 保存用户名和密码
				SharedPreferences sp = getSharedPreferences("user",Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("phone",sock.getPhone());
				editor.putString("password", sock.getPassword());
				editor.apply();
				
				finishAll();
				startActivity(intent);
				
			}else{
				
				sock.disconnected();
				publishProgress(ret);
			}	
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
			int ret = (Integer) values[0];
			
			ActivityLogin.this.closewaitwindow();
			Log.w("TAG",""+ret);
			if (ret != 1){
				/* 密码错误或未注册 */

				new WechatAlert(ActivityLogin.this)
					.setValue("无法连接到服务器。")
					.setOnConfirm(null)
					.show();
			}
			
			if (ret == 1){
				/* 无法连接到服务器 */

				new WechatAlert(ActivityLogin.this)
				.setTitle("登录失败")
				.setValue("帐号或密码错误，请重新填写。")
				.setOnConfirm(null)
				.show();	
			}					
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.equals(login_button)){
			
			if (is_key == false) 
				/* 确认按钮被激活 */
				return;		
			
			pupopwaiting("正在登录...");
			
			//开始登陆
			new LoginAsyncTask().execute(new Object());
		} 
	}
	
	class MyTextWatcher implements TextWatcher {

		Button target;

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}
		
		/*
		 * 文本被修改时，查看是否全部都不为空。若是，则激活按钮。
		 * */
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(phonenumber_value.getText())
					&& !TextUtils.isEmpty(password_value.getText())) {
				
				if (is_key == true)
					return;
				
				is_key = true;
				login_button
						.setBackgroundResource(R.drawable.register_login_button_selector);
			} else {
				
				if (is_key == false)
					return;
				
				is_key = false;
				login_button.setBackgroundColor(getResources().getColor(
						R.color.registerlogin_default));
			}
		}
	}
}
