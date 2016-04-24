
package qiu.tool.windword;

import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;

public class ConfusedWordActivity extends Activity {

    private static final String REF_ID = "ref_id";

    WordLibAdapter mDb = WordLibAdapter.getInstance(this);

    private TextView text[] = new TextView[5];

    private Cursor mCursor = null;

    private long mRefId = -1;

    private void log(String info) {
        Util.log("ConfusedWordActivity: " + info);
    }

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Intent intent = getIntent();
        mRefId = intent.getLongExtra(REF_ID,-1);


        log("Open confused id:" + mRefId);

        mCursor = mDb.getConfusedWordList(mRefId);
        int refIndex = mCursor.getColumnIndex(WordLibAdapter.COL_REFERENCE);
        int wdIndex = mCursor.getColumnIndex(WordLibAdapter.COL_WORD);
        int desIndex = mCursor.getColumnIndex(WordLibAdapter.COL_INTERPRETION);

        setContentView(R.layout.confused_group_item);

        text[0] = (TextView)findViewById(R.id.word_01);
        text[1] = (TextView)findViewById(R.id.word_02);
        text[2] = (TextView)findViewById(R.id.word_03);
        text[3] = (TextView)findViewById(R.id.word_04);
        text[4] = (TextView)findViewById(R.id.word_05);
        if (mCursor != null) {
            mCursor.moveToFirst();
            int i = 0;
            while (!mCursor.isAfterLast()) {
                String content = mCursor.getString(wdIndex);
                content += " " + mCursor.getString(desIndex);
                text[i].setText(content);


                Util.log("Word  " + mCursor.getString(wdIndex));
                Util.log(" " + mCursor.getString(wdIndex));

                text[i].setVisibility(View.VISIBLE);

                mCursor.moveToNext();
                i++;
                if(i>4){
                    break;
                }
            }
        }
    }

    public static void openActivity(Context context, long refId) {
        Intent myIntent = new Intent(context, ConfusedWordActivity.class);
        myIntent.putExtra(REF_ID, refId);
        Util.log("open confused refid is "+ refId);

        context.startActivity(myIntent);
    }

}
