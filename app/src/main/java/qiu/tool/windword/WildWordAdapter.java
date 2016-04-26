
package qiu.tool.windword;

import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.R.integer;
import android.R.raw;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A concrete BaseAdapter that is backed by an array of arbitrary objects. By
 * default this class expects that the provided resource id references a single
 * TextView. If you want to use a more complex layout, use the constructors that
 * also takes a field id. That field id should reference a TextView in the
 * larger layout resource.
 *
 * <p>
 * However the TextView is referenced, it will be filled with the toString() of
 * each object in the array. You can add lists or arrays of custom objects.
 * Override the toString() method of your objects to determine what text will be
 * displayed for the item in the list.
 *
 * <p>
 * To use something other than TextViews for the array display, for instance,
 * ImageViews, or to have some of data besides toString() results fill the
 * views, override {@link #getView(int, View, ViewGroup)} to return the type of
 * view you want.
 */
public class WildWordAdapter extends BaseAdapter implements Filterable {
    /**
     * Contains the list of objects that represent the data of this
     * WildWordAdapter. The content of this list is referred to as "the array"
     * in the documentation.
     */
    private final static String LOG_TAG = "WordWind";
    private int mMode = SearchWordPage.SEARCH_MODE_ENGLISH;

    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is
     * also used by the filter (see {@link #getFilter()} to make a synchronized
     * copy of the original array of data.
     */
    private final Object mLock = new Object();

    /**
     * Indicates whether or not {@link #notifyDataSetChanged()} must be called
     * whenever {@link #mObjects} is modified.
     */
    private boolean mNotifyOnChange = true;

    private Context mContext;
    private ArrayFilter mFilter;

    private LayoutInflater mInflater;
    private WordLibAdapter mLib;
    private Cursor mCursor;

    /**
     * Constructor
     *
     * @param context The current context.
     * @param textViewResourceId The resource ID for a layout file containing a
     *            TextView to use when instantiating views.
     */
    public WildWordAdapter(Context context, WordLibAdapter lib,int mode) {
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLib = lib;
        mMode = mode;
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    /**
     * Control whether methods that change the list ({@link #add},
     * {@link #insert}, {@link #remove}, {@link #clear}) automatically call
     * {@link #notifyDataSetChanged}. If set to false, caller must manually call
     * notifyDataSetChanged() to have the changes reflected in the attached
     * view.
     *
     * The default is true, and calling notifyDataSetChanged() resets the flag
     * to true.
     *
     * @param notifyOnChange if true, modifications to the list will
     *            automatically call {@link #notifyDataSetChanged}
     */
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    /**
     * Returns the context associated with this array adapter. The context is
     * used to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * {@inheritDoc}
     */
    public int getCount() {
        /* return 20;*/

        if(mCursor == null) return 0;
        return mCursor.getCount();
    }
    /**
     * {@inheritDoc}
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(LOG_TAG, "getView positon is "+position);
        return createViewFromResource(position, convertView, parent);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent) {
        View view;
        TextView text_word = null;
        TextView text_chinese = null;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.word_list_item, parent, false);
        } else {
            view = convertView;
        }

        try {
            text_word = (TextView)view.findViewById(R.id.tvWordItem);
            text_chinese = (TextView)view.findViewById(R.id.tvChineseItem);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "WildWordAdapter requires the resource ID to be a TextView", e);
        }
/*
        text_word.setText("Only for test" + position);
//        Log.i(LOG_TAG, mCursor.getString(1));
        text_chinese.setText("Yes ,you are great.");*/
//        Util.log("createViewFromResource");

        mCursor.moveToPosition(position);
        text_word.setText(mCursor.getString(0));
            Log.i(LOG_TAG, mCursor.getString(1));
            text_chinese.setText(mCursor.getString(1));
        return view;
    }



    /**
     * {@inheritDoc}
     */
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    public void doSearch(String key){
        getFilter().filter(key);
    }

    /**
     * <p>
     * An array filter constrains the content of the array adapter with a
     * prefix. Each item that does not start with the supplied prefix is removed
     * from the list.
     * </p>
     */
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {

            FilterResults results = new FilterResults();
            Log.i(LOG_TAG,"Filter is " + prefix.toString());

            if (mMode == SearchWordPage.SEARCH_MODE_ENGLISH) {
                mCursor = mLib.getWordContain(prefix.toString());
            } else {
                mCursor = mLib.getWordContainChinese(prefix.toString());
            }
            if(mCursor == null){
                results.values = null;
                results.count = 0;
            }

            int count = mCursor.getCount();
            Log.i(LOG_TAG, "Word count is "+count);
            if(count > 0){
                ArrayList<String> list = new ArrayList<String>();
                mCursor.moveToFirst();
                while(!mCursor.isAfterLast()){
                    list.add(mCursor.getString(0));
                    mCursor.moveToNext();
                }
                results.values = list;
                results.count = list.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // noinspection unchecked
            if (results.count > 0) {
                Util.log("publishResults.notifyDataSetChanged");
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    public String getItem(int position) {
        if(mCursor!= null){
            mCursor.moveToPosition(position);
            return mCursor.getString(0);
        }
        return null;
    }
    public void setMode(int mode){
        mMode = mode;
    }
}
