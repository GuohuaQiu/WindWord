
package qiu.tool.windword;

import qiu.tool.windword.R;
import qiu.tool.windword.R.drawable;
import qiu.tool.windword.R.layout;

import android.R.id;
import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConfusedWordListAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;

    private Cursor mCursor = null;

    private int mId[];

    private int mCount = -1;

    private int selA = -1;

    private int selB = -1;

    private int refIndex = -1;

    private int wdIndex = -1;
    private int desIndex = -1;

    WordLibAdapter mLib = null;

    /**
     * Constructor
     *
     * @param context The context where the View associated with this
     *            ConfusedWordListAdapter is running
     */
    public ConfusedWordListAdapter(Context context) {

        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLib = WordLibAdapter.getInstance(context);
    }

    /**
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        if (mCount != -1) {
            Util.log("return OK Confused count is " + mCount);
            return mCount;
        }
        mCount = 0;
        if (mCursor != null) {
            int wdCount = mCursor.getCount();
            Util.log("Total count confused  " + wdCount);

            if (wdCount != 0) {
                mId = new int[wdCount];

                mCursor.moveToFirst();
                int thisId;
                mId[0] = 0;

                Util.log("first mId[" + 0 + "] = 0");
                mCount = 1;

                while (!mCursor.isAfterLast()) {
                    thisId = mCursor.getInt(refIndex);
                    Util.log("Content:" + thisId + "  " + mCursor.getString(wdIndex));
                    if (thisId != mId[mCount - 1]) {
                        mId[mCount] = thisId;
                        Util.log("mId[" + mCount + "] = " + thisId);
                        mCount++;
                    }
                    mCursor.moveToNext();
                }
            }
        }
        return mCount;
    }

    public void loadData() {
        mCursor = mLib.getConfusedWordList();
        refIndex = mCursor.getColumnIndex(WordLibAdapter.COL_REFERENCE);
        wdIndex = mCursor.getColumnIndex(WordLibAdapter.COL_WORD);
        desIndex = mCursor.getColumnIndex(WordLibAdapter.COL_INTERPRETION);
        notifyDataSetChanged();
    }

    public void refresh() {
        loadData();
        mCount = -1;
        notifyDataSetChanged();
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
        refIndex = mCursor.getColumnIndex(WordLibAdapter.COL_REFERENCE);
        wdIndex = mCursor.getColumnIndex(WordLibAdapter.COL_WORD);
        notifyDataSetChanged();
    }

    private void MoveCursorTo(int pos) {
        if (mCursor != null) {
            int refIndex = mCursor.getColumnIndex(WordLibAdapter.COL_REFERENCE);
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                int thisId = mCursor.getInt(refIndex);
                if (thisId == mId[pos]) {
                    return;
                }
                mCursor.moveToNext();
            }
        }
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public String getItem(int position) {
        if (mCursor != null) {
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

    static class ViewHolder {
        TextView text[] = new TextView[5];
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, R.layout.confused_group_item);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent,
            int resource) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(resource, null);
            holder = new ViewHolder();
            holder.text[0] = (TextView)convertView.findViewById(R.id.word_01);
            holder.text[1] = (TextView)convertView.findViewById(R.id.word_02);
            holder.text[2] = (TextView)convertView.findViewById(R.id.word_03);
            holder.text[3] = (TextView)convertView.findViewById(R.id.word_04);
            holder.text[4] = (TextView)convertView.findViewById(R.id.word_05);
            convertView.setTag(holder);
//            Log.i("convertView", "is null");
        } else {
            holder = (ViewHolder)convertView.getTag();
//            Log.i("convertView", "is not null");
        }
        if (position == selA) {
            convertView.setBackgroundResource(R.drawable.calculator_button_pressed);
        }else{
            convertView.setBackgroundResource(R.drawable.calculator_button);
        }

        bindView(position, holder);

        return convertView;
    }


    private void bindView(int position, ViewHolder holder) {
        if (mCursor != null) {
            MoveCursorTo(position);
            Util.log("---------------------------" + position + "------------------------");

            for (int i = 0; i < 5; i++) {
                if (!mCursor.isAfterLast() && mCursor.getInt(refIndex) == mId[position]) {
                    Util.log("Word  " + mCursor.getString(wdIndex));
                    String content = mCursor.getString(wdIndex);
                    content += " " + mCursor.getString(desIndex);
                    holder.text[i].setText( content );
                    Util.log(" " + mCursor.getString(wdIndex));
/*
                    if (position == selA) {
                        holder.text[i].setTextColor(0xFFFF0000);
                    } else {
                        holder.text[i].setTextColor(0xFF0000FF);
                    }*/
                    holder.text[i].setVisibility(View.VISIBLE);
                    mCursor.moveToNext();
                } else {
                    holder.text[i].setVisibility(View.GONE);
                }
            }

        }
        Util.log("---------------------------" + position + "------------------------");
    }

    public void setSelA(int n) {
        selA = n;
    }

    public int getSelA() {
        return selA;
    }

    public int getRefId(int pos) {
        return mId[pos];
    }

}
