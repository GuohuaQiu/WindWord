package qiu.tool.windword;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BodyAdatper extends BaseAdapter {
    private String[] item_names;
    private int[] item_images;
//	private List<String> item_names;
//	private List<Integer> item_images;
	private Context context;


	public BodyAdatper(Context context, String[] item_names,
			int[] item_images) {
		this.context = context;
		this.item_names = item_names;
		this.item_images = item_images;
	}


	public int getCount() {
	    Log.i("getCount",""+item_images.length);
		// TODO Auto-generated method stub
		return item_images.length;
	}


	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}



	public View getView(int position, View convertView, ViewGroup parent) {
	    Log.i("getView", ""+ position);
		//总布局
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER);
		//选项名称
		TextView tv_item = new TextView(context);
		tv_item.setGravity(Gravity.CENTER);
		tv_item.setLayoutParams(new GridView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv_item.setText(item_names[position]);
		//选项图表
		ImageView img_item = new ImageView(context);
		img_item.setLayoutParams(new LayoutParams(100, 100));
		img_item.setImageResource(item_images[position]);
		//添加选项图标和名字
		layout.addView(img_item);
		layout.addView(tv_item);

		return layout;
	}

}
