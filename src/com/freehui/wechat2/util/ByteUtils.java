package com.freehui.wechat2.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ByteUtils {

	/* 数字转 byte */
	public static byte[] number_to_byte(int number) {

		byte[] i = new byte[4];
		i[0] = (byte) (number & 0xff);
		i[1] = (byte) (number >> 8);
		i[2] = (byte) (number >> 16);
		i[3] = (byte) (number >> 24);

		return i;
	}

	/* byte 转数字 */
	public static int byte_to_number(byte[] byte_1, int offset) {

		return (byte_1[offset + 0] & 0xff) | (byte_1[offset + 1] << 8 & 0xff00)
				| ((byte_1[offset + 2] << 24) >> 8)
				| (byte_1[offset + 3] << 24);
	}
	
	/* 取出 byte[] 中的一部分 */
	public static byte[] getbyte(byte[] byte1,int start,int size) {

		byte[] byte2 = new byte[size];
		System.arraycopy(byte1, start, byte2, start, size);
		return byte2;
	}
	
	/* 合并 byte */
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {

		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}
	
	/* Bitmap 转 byte[] */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	/* byte[] 转 Bitmap */
	public static Bitmap Bytes2Bimap(byte[] b) {
		
		if (b.length != 0) {
			
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}	
}
