package com.example.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.zhihupocket.R;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	private ArrayList<HashMap<String, Object>> top_stories = new ArrayList<HashMap<String,Object>>();

	public static MainHotStoriesFragment newInstance(int position, ArrayList<HashMap<String, Object>> top_stories) {
		MainHotStoriesFragment f = new MainHotStoriesFragment(top_stories);
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}
	
	public MainHotStoriesFragment(ArrayList<HashMap<String, Object>> top_stories) {
		// TODO Auto-generated constructor stub
		this.top_stories = top_stories;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.vf_show_item, null);
		ImageView pic = (ImageView)fl.getChildAt(0);
		TextView txt = (TextView)fl.getChildAt(1);
		
		Uri uri = (Uri)top_stories.get(position).get("imguri");
		System.out.println(uri.getPath());
		Drawable drawble = Drawable.createFromPath(uri.getPath());
		
		pic.setImageDrawable(drawble);
		txt.setText(top_stories.get(position).get("title").toString());
		return fl;
	}

}
