package com.freehui.wechat2.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.BaseFragment;

public class Foundpage extends BaseFragment{
	
	View view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		view = getActivity().getLayoutInflater().inflate(R.layout.tab_found, null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
	//		parent.removeAllViews();
		}
		return view;
	}
}
