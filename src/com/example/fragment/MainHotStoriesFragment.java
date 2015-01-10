package com.example.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.zhihupocket.R;
import com.example.zhihupocket.StoryContent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainHotStoriesFragment extends Fragment implements OnClickListener{

	private static final String ARG_POSITION = "position";

	private int position;
	private ArrayList<HashMap<String, Object>> top_stories = new ArrayList<HashMap<String,Object>>();
	private static DisplayImageOptions options;
	
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
	
	// 初始化配置
	public static boolean initDisplayImageOptions(){
		try {
			options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.resetViewBeforeLoading(true)
			.cacheInMemory(false)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		RelativeLayout pager_item_container = (RelativeLayout)inflater.inflate(R.layout.vf_show_item, null);
		pager_item_container.setOnClickListener(this);
		ImageView pic = (ImageView)pager_item_container.getChildAt(0);
		TextView txt = (TextView)pager_item_container.getChildAt(1);
		txt.setText(top_stories.get(position).get("title").toString());
		final ProgressBar spinner = (ProgressBar) pager_item_container.getChildAt(2);

		ImageLoader.getInstance().displayImage(top_stories.get(position).get("image").toString(),
				pic, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				spinner.setVisibility(View.VISIBLE);
			}
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
					case IO_ERROR:
						message = "Input/Output error";
						break;
					case DECODING_ERROR:
						message = "Image can't be decoded";
						break;
					case NETWORK_DENIED:
						message = "Downloads are denied";
						break;
					case OUT_OF_MEMORY:
						message = "Out Of Memory error";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
				}
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

				spinner.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				spinner.setVisibility(View.GONE);
			}
		});
		return pager_item_container;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), StoryContent.class);
		intent.putExtra("stories_group", top_stories);
		intent.putExtra("story_order", position);
		startActivity(intent);
	}

}
