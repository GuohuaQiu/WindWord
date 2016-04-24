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



public class WordListAdapter extends BaseAdapter {


    private LayoutInflater mInflater = null;

    private Cursor  mCursor = null;


    /**
     * Constructor
     *
     * @param context The context where the View associated with this WordListAdapter is running
     */
    public WordListAdapter(Context context) {

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
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public String getItem(int position) {
        if(mCursor != null)
        {
            mCursor.moveToPosition(position);
            return mCursor.getString(mCursor.getColumnIndex(WordLibAdapter.COL_WORD));
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
        return createViewFromResource(position, convertView, parent, R.layout.top10item);
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
            TextView v = (TextView)view.findViewById(R.id.word);
            v.setText(mCursor.getString(mCursor.getColumnIndex(WordLibAdapter.COL_WORD)));
            v = (TextView)view.findViewById(R.id.score);
//            v.setText("" + mCursor.getInt(mCursor.getColumnIndex(WordLibAdapter.COL_SCORE)));
            v.setText("" + mCursor.getInt(1));
            v = (TextView)view.findViewById(R.id.index);
            v.setText("" + position);
        }
    }
}
