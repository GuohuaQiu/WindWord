package qiu.tool.windword;


import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class WeightedWordListAdapter extends BaseAdapter {


    private LayoutInflater mInflater = null;

    private Cursor  mCursor = null;
    private int index_wd = 0;
    private int index_score = 1;
    private int index_level = 3;

    /**
     * Constructor
     *
     * @param context The context where the View associated with this WordListAdapter is running
     */
    public WeightedWordListAdapter(Context context) {

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
        index_wd = cursor.getColumnIndex(WordLibAdapter.COL_WORD);
        index_score =cursor.getColumnIndex(WordLibAdapter.COL_SCORE);
        index_level =cursor.getColumnIndex(WordLibAdapter.COL_WORD_SPAN);
        Util.log("Index " + " " + index_wd  + " " + index_score  + " " + index_level );
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public String getItem(int position) {
        if(mCursor != null)
        {
            mCursor.moveToPosition(position);
            return mCursor.getString(index_wd);
        }
        return null;
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
        return createViewFromResource(position, convertView, parent, R.layout.hard_word_item);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
//            Log.i("convertView","is null");
        } else {
            v = convertView;
//            Log.i("convertView","is not null");
        }

        bindView(position, v);

        return v;
    }


    private void bindView(int position, View view) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            TextView v = (TextView)view.findViewById(R.id.order_text);
            v.setText("" + position);

            v = (TextView)view.findViewById(R.id.wd_text);
            v.setText(mCursor.getString(index_wd));

            v = (TextView)view.findViewById(R.id.times_text);
            v.setText("" + mCursor.getLong(index_score));
            v = (TextView)view.findViewById(R.id.span_text);
            v.setText("" + mCursor.getLong(index_level));
        }
    }
}
