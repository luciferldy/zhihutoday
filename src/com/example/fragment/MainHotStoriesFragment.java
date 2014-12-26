package com.example.fragment;

import com.example.zhihupocket.R;

import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

public class MainHotStoriesFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private int position;

	public static MainHotStoriesFragment newInstance(int position) {
		MainHotStoriesFragment f = new MainHotStoriesFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		FrameLayout fl = (FrameLayout)getResources().getLayout(R.id.rl_contain_topstories);
		ImageView pic = (ImageView)fl.getChildAt(0);
		TextView txt = (TextView)fl.getChildAt(1);
		
		return fl;
	}

}
