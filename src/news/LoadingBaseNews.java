package news;

import java.util.ArrayList;
import java.util.HashMap;

public interface LoadingBaseNews {
	public void initView();
	public void runView(ArrayList<HashMap<String, Object>> stories_group, final ArrayList<HashMap<String, Object>> topstories_group);
}
