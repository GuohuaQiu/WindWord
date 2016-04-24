package qiu.tool.windword;



import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView.OnItemClickListener;

import android.widget.BaseAdapter;
import android.widget.ListView;

import java.io.Serializable;


public class RefreshListPage extends Activity implements OnItemClickListener{


    private BaseAdapter mAdapter = null;

    private TextView mNoItemView = null;

    public static final int VIEW_MODE_HISTORY = 1;
    public static final int VIEW_MODE_HARD_FIFTY = 2;
    public static final int VIEW_MODE_TOP_TEN = 3;
    public static final int VIEW_MODE_BELOW_TEN = 4;
    public static final int VIEW_MODE_STATIC_INFO = 5;
    public static final int VIEW_MODE_WEIGHTED_FIFTY = 6;
    public static final int VIEW_MODE_TOO_OLD = 7;
    public static final int VIEW_MODE_SELECTION = 8;



    public static final String LAUNCH_MODE = "MODE";
    public static final String LAUNCH_CURSOR = "DATA_CURSOR";

    private int mMode;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.refreshlist);



        mNoItemView = (TextView)findViewById(R.id.emptyText);
        ListView lv = (ListView)findViewById(R.id.list_refresh);

        WordLibAdapter lib = WordLibAdapter.getInstance(this);

        Intent intent = getIntent();
        if (intent == null) {
            Log.i("FAILED INTENT", "HAHA");
            return;
        }
        Bundle bunde = intent.getExtras();
        mMode = bunde.getInt(LAUNCH_MODE);



        if (mMode == VIEW_MODE_HISTORY) {

            Cursor cursor = lib.getHistory(bunde.getString(WordPage.LAUNCH_WORD));
            if(cursor == null) {//no history exist for this word.
                mNoItemView.setText("this word dont have history!");
                mNoItemView.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new WordHistoryAdapter(this);
                ((WordHistoryAdapter)mAdapter).setCursor(cursor);
                Log.i("Count", "number of history list " + cursor.getCount());

                lv.setAdapter(mAdapter);
                lv.setOnItemClickListener(this);
            }
        } else if (mMode == VIEW_MODE_TOP_TEN) {
            Cursor cursor = lib.getRecentFailedWord(20);
            if (cursor == null || cursor.getCount() == 0) {
                mNoItemView.setText("单词库为空！");
                mNoItemView.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new FailWordListAdapter(this);
                ((FailWordListAdapter)mAdapter).setCursor(cursor);
                Log.i("Count", "number of list " + cursor.getCount());

                lv.setAdapter(mAdapter);
                lv.setOnItemClickListener(this);
            }
        } else if (mMode == VIEW_MODE_STATIC_INFO) {
            Cursor cursor = lib.getStaticInfo();
            if (cursor.getCount() == 0) {
                mNoItemView.setText("单词库为空！");
                mNoItemView.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new StaticInfoAdapter(this);
                ((StaticInfoAdapter)mAdapter).setCursor(cursor);
                Log.i("Count", "number of list " + cursor.getCount());

                lv.setAdapter(mAdapter);
                lv.setOnItemClickListener(this);
                setTitle(((StaticInfoAdapter)mAdapter).getGeneralInfo());
            }
        } else if (mMode == VIEW_MODE_HARD_FIFTY) {
            Cursor cursor = lib.getWordsRefreshCount();
            if (cursor.getCount() == 0) {
                mNoItemView.setText("单词库为空！");
                mNoItemView.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new HardWordListAdapter(this);
                ((HardWordListAdapter)mAdapter).setCursor(cursor);
                Log.i("Count", "number of list " + cursor.getCount());

                lv.setAdapter(mAdapter);
                lv.setOnItemClickListener(this);
                setTitle("Hard word 50");
            }
        } else if (mMode == VIEW_MODE_WEIGHTED_FIFTY) {
            Cursor cursor = lib.getTopWords(50);
            if (cursor.getCount() == 0) {
                mNoItemView.setText("单词库为空！");
                mNoItemView.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new WeightedWordListAdapter(this);
                ((WeightedWordListAdapter)mAdapter).setCursor(cursor);
                Log.i("Count", "number of list " + cursor.getCount());

                lv.setAdapter(mAdapter);
                lv.setOnItemClickListener(this);
                setTitle("最近十次失败率-前50");
            }
        } else if (mMode == VIEW_MODE_TOO_OLD) {
            Cursor cursor = lib.getTooOldWord();
            if (cursor.getCount() == 0) {
                mNoItemView.setText("old单词库为空！");
                mNoItemView.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new WeightedWordListAdapter(this);
                ((WeightedWordListAdapter)mAdapter).setCursor(cursor);
                Log.i("Count", "number of list " + cursor.getCount());

                lv.setAdapter(mAdapter);
                lv.setOnItemClickListener(this);
                setTitle("TOO OLD");
            }
        } else if (mMode == VIEW_MODE_SELECTION) {
            Cursor cursor = mWordListCursor;//(Cursor)bunde.get(LAUNCH_CURSOR);;
            if (cursor.getCount() == 0) {
                mNoItemView.setText("old单词库为空！");
                mNoItemView.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new WeightedWordListAdapter(this);
                ((WeightedWordListAdapter)mAdapter).setCursor(cursor);
                Log.i("Count", "number of list " + cursor.getCount());

                lv.setAdapter(mAdapter);
                lv.setOnItemClickListener(this);
                setTitle("TOO OLD");
            }
        } else {
            Cursor cursor = lib.getWordsRefreshCount();
//            Cursor cursor = lib.getBelow10Words();
            if (cursor.getCount() == 0) {
                mNoItemView.setText("单词库为空！");
                mNoItemView.setVisibility(View.VISIBLE);
            } else {
                mAdapter = new WordListAdapter(this);
                ((WordListAdapter)mAdapter).setCursor(cursor);
                Log.i("Count", "number of list " + cursor.getCount());

                lv.setAdapter(mAdapter);
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }

    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }




    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        if(mMode == VIEW_MODE_TOP_TEN){
            String word = ((FailWordListAdapter)mAdapter).getItem(position);
            WordPage.openOneWord(this, word);
            return;
        }
        if(mMode == VIEW_MODE_HARD_FIFTY){
            String word = ((HardWordListAdapter)mAdapter).getItem(position);
            WordPage.openOneWord(this, word);
            return;
        }
        if(mMode == VIEW_MODE_WEIGHTED_FIFTY){
            String word = ((WeightedWordListAdapter)mAdapter).getItem(position);
            WordPage.openOneWord(this, word);
            return;
        }
        if(mMode == VIEW_MODE_TOO_OLD){
            String word = ((WeightedWordListAdapter)mAdapter).getItem(position);
            WordPage.openOneWord(this, word);
            return;
        }
         if(mMode == VIEW_MODE_SELECTION){
            String word = ((WeightedWordListAdapter)mAdapter).getItem(position);
            WordPage.openOneWord(this, word);
            return;
        }
       if(mMode == VIEW_MODE_STATIC_INFO){
            int span = (int)mAdapter.getItemId(position);
        	WordPage.openRefreshTodayWordPage(this, span);
        	Util.log("Open pos:"+position + " span is " + span);
        	return;
        }
    }
    static public void openHistoryPage(Context txt,String word) {
        Intent listIntent = new Intent(txt, RefreshListPage.class);

        Bundle bundle = new Bundle();

        bundle.putInt(RefreshListPage.LAUNCH_MODE, RefreshListPage.VIEW_MODE_HISTORY);
        bundle.putString(WordPage.LAUNCH_WORD, word);

        listIntent.putExtras(bundle);
        txt.startActivity(listIntent);
     }

    static private Cursor mWordListCursor = null;

    static public void openWordListPage(Context txt,Cursor cursor) {
        Intent listIntent = new Intent(txt, RefreshListPage.class);

        Bundle bundle = new Bundle();

        bundle.putInt(RefreshListPage.LAUNCH_MODE, RefreshListPage.VIEW_MODE_SELECTION);
        mWordListCursor = cursor;
 //       bundle.putSerializable(RefreshListPage.LAUNCH_CURSOR, (Serializable)cursor);


        listIntent.putExtras(bundle);
        txt.startActivity(listIntent);
     }


}
