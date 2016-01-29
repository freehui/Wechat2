package com.freehui.wechat2;

import java.io.ByteArrayOutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.NoTieleActivity;
import com.freehui.wechat2.util.ByteUtils;

public class ActivityIMessage extends NoTieleActivity implements OnClickListener{
	
	ViewGroup personal_layout;
	ImageView photo_icon;
	TextView tv_nickname;
	TextView tv_username;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_imessge);
		setBack(findViewById(R.id.register_back));
		
		photo_icon = (ImageView)findViewById(R.id.photo_icon);
		tv_nickname = (TextView)findViewById(R.id.tv_nickname);
		tv_username = (TextView)findViewById(R.id.tv_username);		
		
		personal_layout = (ViewGroup)findViewById(R.id.personal_layout);
		personal_layout.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		photo_icon.setImageBitmap(WechatSocket.getInstance().image);
		tv_nickname.setText(WechatSocket.getInstance().username);
		tv_username.setText(WechatSocket.getInstance().phone);				
	}
	
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
			WechatSocket sock = WechatSocket.getInstance();
			
			if (extras != null) {
				
				Bitmap photo = extras.getParcelable("data");
				
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				
				// 压缩缩放图片
				buf = compressBitmap(photo);
				
				sock.image = ByteUtils.Bytes2Bimap(buf.toByteArray());
				
				// 把图片显示在ImageView控件上
				photo_icon.setImageBitmap(sock.image); // 把图片显示在ImageView控件上
				
				sock.updateImage(sock.getPhone(), buf.toByteArray());
			}
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.equals(personal_layout)){
			
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, 1);		
		}
	}
}
