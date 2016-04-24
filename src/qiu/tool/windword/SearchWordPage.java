package qiu.tool.windword;



import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

import android.widget.BaseAdapter;
import android.widget.ListView;


public class SearchWordPage extends Activity implements TextWatcher, OnItemClickListener,
        View.OnClickListener {

    private final static String LOG_TAG = "WordWind";
//    private BaseAdapter mAdapter = null;

    private TextView mNoItemView = null;

    public static final int SEARCH_MODE_ENGLISH = 1;
    public static final int SEARCH_MODE_CHINESE = 2;


    public static final String LAUNCH_MODE = "MODE";



    private int mMode;
    WordLibAdapter mLib;
    EditText mWordInputer = null;
    WildWordAdapter mAdapter = null;
    Button btnFilter = null;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_page);
        Intent intent = getIntent();
        if (intent == null) {
            Log.i(LOG_TAG, "SearchWordPage onCreate intent == null");
            return;
        }
        Bundle bunde = intent.getExtras();
        if (bunde == null) {
            mMode = SEARCH_MODE_ENGLISH;
        } else {
            mMode = bunde.getInt(LAUNCH_MODE);
        }

        mLib = WordLibAdapter.getInstance(this);
        mWordInputer = (EditText) findViewById(R.id.word_input);
//        mWordInputer.setThreshold(1);
        mWordInputer.addTextChangedListener(this);
//        TextView aTextView;
//        aTextView.addTextChangedListener(watcher)
//        WildWordAdapter adapter = new WildWordAdapter(this,mLib,mMode);
        mAdapter = new WildWordAdapter(this,mLib,mMode);
//        mWordInputer.setAdapter(adapter);
        btnFilter = (Button)findViewById(R.id.btn_view);
        btnFilter.setOnClickListener(this);

        updateButtonText();
        ListView lv = (ListView)findViewById(R.id.list_word);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



    }

    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }




    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        WordPage.openOneWord(this, mAdapter.getItem(position));
    }
    public void afterTextChanged(Editable s) {
        Log.i("zhangya", "textchage");
        String contentStr = s.toString();
        mAdapter.doSearch(contentStr);
        Util.log("changed:"+contentStr);



        if (contentStr == null || contentStr.length() <= 0)// 判断contentStr是否为空,判断字符串是否为空典型写法
        {
            Log.i(LOG_TAG, "NO string is filter.");

        } else {
        }
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    public void onClick(View view) {
        Log.i(LOG_TAG, "onClick "+ view.getId());
        switch (view.getId()) {
            case R.id.btn_view: {
                if(mMode == SEARCH_MODE_ENGLISH){
                    mMode = SEARCH_MODE_CHINESE;
                }else{
                    mMode = SEARCH_MODE_ENGLISH;
                }
                mAdapter.setMode(mMode);
                updateButtonText();

//                Log.i(LOG_TAG, "onClick btn_view " + mWordInputer.getText().toString());
//                WordPage.openOneWord(this, mWordInputer.getText().toString());

                break;
            }
        }
    }
    public void updateButtonText(){
        if(mMode == SEARCH_MODE_ENGLISH){
            btnFilter.setText("En");
        }else{
            btnFilter.setText("Ch");
        }

    }


}
