package qiu.tool.windword;


import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;




public class WordHistoryAdapter extends BaseAdapter {


    private LayoutInflater mInflater = null;

    private Cursor  mCursor = null;


    /**
     * Constructor
     *
     * @param context The context where the View associated with this StaticInfoAdapter is running
     */
    public WordHistoryAdapter(Context context) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    /**
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        if(mCursor!=null){
            return mCursor.getCount();
        }
        return 0;
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;

        String str[] = mCursor.getColumnNames();
        int i = 0;
        for(String c : str){
            Log.e("Col"+ i, c);
            i++;
        }

    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public Integer getItem(int position) {
        if(mCursor != null)
        {
            boolean b = mCursor.moveToPosition(position);
            if(!b){
                return -1;
            }
            return mCursor.getInt(1);
        }
        return -1;
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, R.layout.static_item);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
//            Log.i("convertView",""+ position + "is null");
        } else {
            v = convertView;
//            Log.i("convertView",""+ position + "is not null");
        }

        bindView(position, v);

        return v;
    }


    private void bindView(int position, View view) {
        if (mCursor != null) {

            Log.i("bindview", "position:" + position + " view is " + view);
            mCursor.moveToPosition(position);
            TextView v = (TextView)view.findViewById(R.id.level_text);
            int span = mCursor.getInt(2);
            int count = mCursor.getInt(3);
            long maxdate = mCursor.getLong(4);
            v.setText(""+count);
            v = (TextView)view.findViewById(R.id.level_count_text);
            v.setText(""+span);

            v = (TextView)view.findViewById(R.id.level_timespan_text);
            v.setText(Util.getFormatDateTime(maxdate));

        }
    }

    public void displayDay(String col, TextView textview)
    {
       textview.setText(Util.getFormatDate(mCursor.getLong(mCursor.getColumnIndex(col))));
    }


}
