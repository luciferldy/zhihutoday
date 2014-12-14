package com.example.zhihupocket;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ListViewForScrollViewInMainXML extends ListView{

	public ListViewForScrollViewInMainXML(Context arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public ListViewForScrollViewInMainXML(Context arg0, AttributeSet arg1){
		super(arg0, arg1);
	}
	
	public ListViewForScrollViewInMainXML(Context arg0, AttributeSet arg1, int arg2){
		super(arg0, arg1, arg2);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}