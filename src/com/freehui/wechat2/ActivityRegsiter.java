package com.freehui.wechat2;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.NoTieleActivity;
import com.freehui.wechat2.util.ByteUtils;
import com.freehui.wechat2.util.LoginRegisterOnFocusChangeListener;
import com.freehui.wechat2.windows.WechatAlert;

@SuppressLint("NewApi")
public class ActivityRegsiter extends NoTieleActivity implements
		OnClickListener {

	RelativeLayout camera;
	ImageView cameraimage;
	LinearLayout username;
	LinearLayout phonenumber;
	LinearLayout password;
	EditText username_value;
	EditText phonenumber_value;
	EditText password_value;
	Button register_button;
	ByteArrayOutputStream userImage;
	boolean is_key = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_register);
		setBack(findViewById(R.id.register_back));

		cameraimage = (ImageView) findViewById(R.id.cameraimage);
		username = (LinearLayout) findViewById(R.id.register_username);
		phonenumber = (LinearLayout) findViewById(R.id.register_phonenumber);
		password = (LinearLayout) findViewById(R.id.register_password);
		username_value = (EditText) findViewById(R.id.register_username_value);
		phonenumber_value = (EditText) findViewById(R.id.register_phonenumber_value);
		password_value = (EditText) findViewById(R.id.register_password_value);
		register_button = (Button) findViewById(R.id.register_button);
		username_value
				.setOnFocusChangeListener(new LoginRegisterOnFocusChangeListener(
						username));
		password_value
				.setOnFocusChangeListener(new LoginRegisterOnFocusChangeListener(
						password));
		phonenumber_value
				.setOnFocusChangeListener(new LoginRegisterOnFocusChangeListener(
						phonenumber));
		phonenumber_value.addTextChangedListener(new MyTextWatcher());
		password_value.addTextChangedListener(new MyTextWatcher());
		username_value.addTextChangedListener(new MyTextWatcher());
		camera = (RelativeLayout) findViewById(R.id.camera);
		camera.setOnClickListener(this);
		register_button.setOnClickListener(this);
 
	}

	private Socket socket = null;
	private OutputStream out = null;

	class RegisterAsyncTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub

			int ret = WechatSocket.getInstance().register(
					username_value.getText().toString(),
					phonenumber_value.getText().toString(),
					password_value.getText().toString(),
					userImage == null ? null : userImage.toByteArray());
			
			
			if (ret == 2) {
				finish();
				bootActivity(ActivityLogin.class);
			} else
				publishProgress(ret);

			return null;
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
				
			int ret = (Integer) values[0];
			
			closewaitwindow();
			if (ret == 0)
				new WechatAlert(ActivityRegsiter.this).setValue("无法连接到服务器。")
					.setOnConfirm(null).show();
			else 
				new WechatAlert(ActivityRegsiter.this).setValue("该账户已注册。")
					.setOnConfirm(null).show();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		if (arg0.equals(camera)) {

			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, 1);
		} else if (arg0.equals(register_button)) {

			if (is_key == false)
				return;

			pupopwaiting("正在注册...");
			new RegisterAsyncTask().execute(new Object());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (requestCode == 1) {
			// 开始裁剪图片
			if (data == null)
				return;
			Uri uri = data.getData();
			// Android系统自带的一个图片剪裁页面
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			// aspectX aspectY 是宽高的比例
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			// outputX outputY 是裁剪图片宽高
			intent.putExtra("outputX", 400);
			intent.putExtra("outputY", 400);
			intent.putExtra("return-data", true);
			// 打开裁剪页面
			startActivityForResult(intent, 2);
		} else if (requestCode == 2) {
			// 图片裁剪完毕
			if (data == null)
				return;
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				
				//压缩显示图片
				userImage = compressBitmap(photo);
				cameraimage.setImageBitmap(ByteUtils.Bytes2Bimap(userImage.toByteArray())); // 把图片显示在ImageView控件上
			}
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

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

			if (!TextUtils.isEmpty(username_value.getText())
					&& !TextUtils.isEmpty(phonenumber_value.getText())
					&& !TextUtils.isEmpty(password_value.getText())) {

				if (is_key == true)
					return;

				is_key = true;
				register_button
						.setBackgroundResource(R.drawable.register_login_button_selector);
			} else {

				if (is_key == false)
					return;

				is_key = false;
				register_button.setBackgroundColor(getResources().getColor(
						R.color.registerlogin_default));
			}
		}
	}
}
