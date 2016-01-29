package com.freehui.wechat2;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.NoTieleActivity;
import com.freehui.wechat2.windows.WechatAlert;

public class ActivityAddfriend extends NoTieleActivity{
	
	EditText search_value;
	ViewGroup top;
	View windowview;
	TextView textvalue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_addfriend);
		setBack(findViewById(R.id.register_back));
		
		search_value = (EditText)findViewById(R.id.search_value);
		top = (ViewGroup)findViewById(R.id.top);
		windowview = getLayoutInflater().inflate(R.layout.addfriendwindow, null);
		textvalue = (TextView)windowview.findViewById(R.id.textvalue);
		
		windowview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				handle_search();
			}
		});
		
		search_value.addTextChangedListener(new TextWatcher(){
			
			boolean dismiss = false;
			PopupWindow window;
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
				if (TextUtils.isEmpty(search_value.getText())){
					
					if (window.isShowing()) /* 若已打开提示框，则关闭他 */
						window.dismiss();
					
				}else {
					
					if (window == null){
						/* 若提示框未创建，则创建并显示他 */
						
						/* 最后一个参数表示这个 pupopwindow 不会获取焦点。否则他会夺取 EditText 的焦点 */
						window = 
						new PopupWindow(windowview,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
						window.setTouchable(true);
						window.setBackgroundDrawable(new BitmapDrawable());
						window.showAsDropDown(top);
						window.setOnDismissListener(new OnDismissListener() {
							/* 设置 pupopwindow 的关闭监听，在他关闭时设置 dismiss 为 false */
							public void onDismiss() {
								// TODO Auto-generated method stub
								
								dismiss = false;
							}
						});
					}else if (!window.isShowing()){
						/* 提示框已创建，但被关闭了，那么就显示他 */
						
						window.showAsDropDown(top);
					}
					
					textvalue.setText("搜索:" + search_value.getText());
				}
			}
		});
		
		search_value.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				
				if (arg1 == KeyEvent.KEYCODE_ENTER && arg2.ACTION_UP == arg2.getAction()){


					handle_search();
				}
				return false;
			}
		});
	}
	
	Handler handler = new Handler(){
			
		public void handleMessage(android.os.Message msg) {
			
			if (msg.obj == null){
				
				new WechatAlert(ActivityAddfriend.this)
					.setValue("用户不存在")
					.setOnConfirm(null)
					.show();
				return;
			}			
			
			JSONObject obj;
			String name = "null";
			
			try {
				obj = new JSONObject((String)msg.obj);
				name = (String) obj.get("name");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Intent intent = new Intent(ActivityAddfriend.this,ActivityUser.class);
			intent.putExtra("name",name);
			intent.putExtra("phone",search_value.getText().toString());
			
			bootActivity(intent);						
		};
	};
	
	void handle_search(){
		
		String phone = search_value.getText().toString();
		WechatSocket.getInstance().getMesage(phone,handler);
	}	
}
