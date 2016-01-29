package com.freehui.wechat2.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.wechat2.R;

public class ChangeColorIcon extends View {

	String text;
	Bitmap image;
	Bitmap image2;
	int alpha1;//深色图
	int alpha2 = 255;//浅色图
	Paint paint;
	float height;
	float width;
	
	private int mColor = 0xFF45C01A;
	//透明度 0.0-1.0
	private float mAlpha = 0f;
	
	public ChangeColorIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		/* 得到要使用的图片 */
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.custom);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		
		image = ((BitmapDrawable)ta.getDrawable(R.styleable.custom_icon)).getBitmap();
		image2 = ((BitmapDrawable)ta.getDrawable(R.styleable.custom_icontwo)).getBitmap();
		height = ta.getDimension(R.styleable.custom_height, -1);
		width = ta.getDimension(R.styleable.custom_width, -1);
		Log.w("hei = " + height,"width = " + width);
		
		/* 回收 */
		ta.recycle();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int iwidth = (int)width;
		int iheight = (int)height;
		int startleft = getMeasuredWidth()/2-(iwidth/2);
		//父宽度/2是显示的中心，再减去图片的一半就是图片要显示的位置
		Rect rect = new Rect(startleft, 0, startleft+iwidth, iheight); 

		paint.setAlpha(alpha1);
		canvas.drawBitmap(image, null, rect,paint);
		paint.setAlpha(alpha2);
		canvas.drawBitmap(image2, null, rect,paint);
	}
	
	public void setIconAlpha(float alpha){

		int intalpha = (int) Math.ceil((255 * alpha));
		alpha1 = intalpha;
		alpha2 = 255 - intalpha;
		
		invalidate();
	}	
}
