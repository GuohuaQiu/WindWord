package qiu.tool.windword;


import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;




public class StaticInfoAdapter extends BaseAdapter {


    private LayoutInflater mInflater = null;

    private Cursor  mCursor = null;

    private int nearset_pos = -1;


    /**
     * Constructor
     *
     * @param context The context where the View associated with this StaticInfoAdapter is running
     */
    public StaticInfoAdapter(Context context) {

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

        boolean b = mCursor.moveToFirst();
        if(!b){
            nearset_pos = -1;
        }
        long mintime = 0;
        long thistime = 0;


        int pos = 0;

        while(!mCursor.isAfterLast()){
            thistime = mCursor.getLong(3);
            if (pos == 0) {
                mintime = thistime;
                nearset_pos = pos;
            } else {
                if (thistime < mintime) {
                    mintime = thistime;
                    nearset_pos = pos;
                }
            }
            pos++;
            mCursor.moveToNext();
        }



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
        Util.log("qiu.tool.wordwind.StaticInfoAdapter.getItemId(int)");
        mCursor.moveToPosition(position);
//        TextView v = (TextView)view.findViewById(R.id.BookName);
        return (long)mCursor.getInt(0);
//        return position;
    }

    static class ViewHolder {
        public TextView text_level;
        public TextView text_count;
        public TextView text_span;
      }

    /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.static_item, parent, false);
            Util.log("ROW VIEW FOR pos " + position + " is " + rowView);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text_level = (TextView)rowView.findViewById(R.id.level_text);
            viewHolder.text_count = (TextView)rowView.findViewById(R.id.level_count_text);
            viewHolder.text_span = (TextView)rowView.findViewById(R.id.level_timespan_text);

            rowView.setTag(viewHolder);
        }
        Util.log("Execute ROW VIEW FOR pos " + position + " is " + rowView);

        bindView(position, (ViewHolder)rowView.getTag());
        return rowView;
        // return createViewFromResource(position, convertView, parent,
        // R.layout.static_item);
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
            mCursor.moveToPosition(position);
            TextView v = (TextView)view.findViewById(R.id.level_text);
            int span = mCursor.getInt(0);
            int count = mCursor.getInt(1);
            long mindate = mCursor.getLong(3);
            long maxdate = mCursor.getLong(2);
            v.setText(""+span);
            v = (TextView)view.findViewById(R.id.level_count_text);
            v.setText(""+count);

            v = (TextView)view.findViewById(R.id.level_timespan_text);
            v.setText(Util.getFormatDateTimeNoYear(mindate) + " "
                    + Util.getFormatDateTimeNoYear(maxdate));


            Calendar calendar = Calendar.getInstance();

            if(position == nearset_pos){
                Log.i("WindWord",""+ position + " " + span + Util.getFormatDateTimeNoYear(mindate) + " "
                        + Util.getFormatDateTimeNoYear(maxdate));
                v.setBackgroundColor(0xff113355);
            }else{
                Log.i("WindWord","No "+ position + " " + span + Util.getFormatDateTimeNoYear(mindate) + " "
                        + Util.getFormatDateTimeNoYear(maxdate));
                v.setBackgroundColor(0xff113355);
            }
            if(mindate<calendar.getTimeInMillis()){
                v.setTextColor(0xffff0000);
            }else{
                v.setTextColor(0xff22aaff);
            }
            v = null;
        }
    }

    private void bindView(int position, ViewHolder view) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            TextView v = view.text_level;
            int span = mCursor.getInt(0);
            int count = mCursor.getInt(1);
            long mindate = mCursor.getLong(3);
            long maxdate = mCursor.getLong(2);
            v.setText(""+span);

            v = view.text_count;
            v.setText(""+count);

            v = view.text_span;
            v.setText(Util.getFormatDateTimeNoYear(mindate) + " "
                    + Util.getFormatDateTimeNoYear(maxdate));
            Calendar calendar = Calendar.getInstance();
            if(position == nearset_pos){
                Util.log(""+ position + " " + span +" "+ Util.getFormatDateTimeNoYear(mindate) + " "
                        + Util.getFormatDateTimeNoYear(maxdate));
                v.setBackgroundColor(0xff113355);
            }else{
                Util.log("No "+ position + " " + span +" "+ Util.getFormatDateTimeNoYear(mindate) + " "
                        + Util.getFormatDateTimeNoYear(maxdate));

                v.setBackgroundColor(Color.TRANSPARENT);
            }
            if(mindate<calendar.getTimeInMillis()){
                v.setTextColor(0xffff0000);
            }else{
                v.setTextColor(0xff22aaff);
            }


        }
    }

    public void displayDay(String col, TextView textview)
    {
       textview.setText(Util.getFormatDate(mCursor.getLong(mCursor.getColumnIndex(col))));
    }

    public String getGeneralInfo(){
        int wdNumber = 0;
        int wdScore = 0;
        if(mCursor != null){
            boolean b = mCursor.moveToFirst();
            if(b){
                while(!mCursor.isAfterLast()){
                    int span = mCursor.getInt(0);
                    int count = mCursor.getInt(1);
                    wdNumber += count;
                    wdScore += span * count;

                    mCursor.moveToNext();
                }
            }
        }
        return "Word:" + wdNumber + " " + wdScore + " Avg:" + (float)wdScore
                / (float)wdNumber;
    }


}
