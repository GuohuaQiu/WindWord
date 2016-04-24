package qiu.tool.windword;

import java.util.List;


import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;



public class MyDefinedMenu extends PopupWindow {

	private LinearLayout layout;	//总的布局

	private GridView gv_body;		//选项视图
	private BodyAdatper bodyAdapter;	//选项适配器

	private Context context;			//上下文
	public int currentState;			//对话框状态：0--显示中、1--已消失、2--失去焦点



	public MyDefinedMenu(Context context,
			String[] item_names, int[] item_images,
			OnItemClickListener itemClickEvent) {

		super(context);
		this.context = context;
		currentState = 1;

		//布局框架
		layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//选项视图
		bodyAdapter = new BodyAdatper(context, item_names, item_images);

		gv_body = new GridView(context);
		gv_body.setNumColumns(1);	//每行显示1个选项
		gv_body.setBackgroundColor(Color.TRANSPARENT);
		gv_body.setAdapter(bodyAdapter);	//设置适配器


		//设置选项点击事件
		gv_body.setOnItemClickListener(itemClickEvent);

		//添加标题栏和选项

		layout.addView(gv_body);

		// 添加菜单视图
		this.setContentView(layout);
		this.setWidth( 100);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应
//		this.setBackgroundDrawable(R.drawable.home_hint_dialog_bg);
	}
}
