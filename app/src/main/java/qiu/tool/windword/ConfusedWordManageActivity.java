
package qiu.tool.windword;

import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.R.integer;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;

public class ConfusedWordManageActivity extends Activity {

    public static int ref_id_A=-1;
    public static int ref_id_B=-1;

    private static final String REF_ID = "ref_id";

    WordLibAdapter mDb = WordLibAdapter.getInstance(this);

    private ListView mListA = null;

    private ListView mListB = null;

    private ListView mListFree = null;

    private Button mBtnSave = null;

    private Cursor mCursor = null;

    private ConfusedGroupAdapter mAdapterA = null;

    private ConfusedGroupAdapter mAdapterB = null;

    private void log(String info) {
        Util.log("ConfusedWordManageActivity: " + info);
    }

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Intent intent = getIntent();

        // mCursor = mDb.getConfusedWordList(mRefId);
        setContentView(R.layout.confused_manage);
        mListA = (ListView)findViewById(R.id.list_a);
        mListB = (ListView)findViewById(R.id.list_b);
        mListFree = (ListView)findViewById(R.id.list_free);
        mBtnSave = (Button)findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_save: {
                        mAdapterA.mWordList.saveChanged();
                        mAdapterB.mWordList.saveChanged();
                        break;
                    }
                }
            }
        });

        mAdapterA = new ConfusedGroupAdapter(ref_id_A, ConfusedGroupAdapter.POS_LEFT, this);
        mListA.setAdapter(mAdapterA);
        mAdapterB = new ConfusedGroupAdapter(ref_id_B, ConfusedGroupAdapter.POS_RIGHT, this);
        mListB.setAdapter(mAdapterB);
    }

    public static void openActivity(Context context/*, long refId*/) {
        Intent myIntent = new Intent(context, ConfusedWordManageActivity.class);
//        myIntent.putExtra(REF_ID, refId);
//        Util.log("open confused refid is " + refId);

        context.startActivity(myIntent);
    }

    public static void setCompareId(Context context,int refId) {
        if(ref_id_A == -1){
            ref_id_A = refId;
        }else{
            ref_id_B = refId;
            openActivity(context);
        }
    }

    public static void clearCompareIds(){
        ref_id_A = -1;
        ref_id_B = -1;
    }

    public void moveLeftWord(String word) {
        WordGroupInfo infoA = mAdapterA.mWordList.get(word);
        mAdapterB.mWordList.add(infoA);
        mAdapterA.mWordList.remove(infoA);
        mAdapterA.notifyDataSetChanged();
        mAdapterB.notifyDataSetChanged();
    }

    public void moveRightWord(String word) {
        WordGroupInfo infoA = mAdapterB.mWordList.get(word);
        mAdapterA.mWordList.add(infoA);
        mAdapterB.mWordList.remove(infoA);
        mAdapterB.notifyDataSetChanged();
        mAdapterA.notifyDataSetChanged();
    }

    class WordGroupInfo {
        String mWord;

        int indexOld;

        public WordGroupInfo(String word) {
            mWord = word;
            indexOld = -1;
        }

        public WordGroupInfo(String word, int refId) {
            mWord = word;
            indexOld = refId;
        }
        // int indexNew;
    }

    class WordList extends ArrayList<WordGroupInfo> {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        int mRefId;

        public WordList(int refId) {
            mRefId = refId;
            Cursor cursor = mDb.getConfusedWordList(refId);
            int index_wd = cursor.getColumnIndex(WordLibAdapter.COL_WORD);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                add(new WordGroupInfo(cursor.getString(index_wd), refId));
                cursor.moveToNext();
            }
        }

        public WordGroupInfo get(String word) {
            for (WordGroupInfo info : this) {
                if (info.mWord.equals(word)) {
                    return info;
                }
            }
            return null;
        }

        public void saveChanged() {
            for (WordGroupInfo info : this) {
                if (info.indexOld != mRefId) {
                    mDb.updateConfusedWord(info.mWord, mRefId);
                }
            }
        }
    }

    class ConfusedGroupAdapter extends BaseAdapter {
        WordLibAdapter mLib = null;

        WordList mWordList = null;

        final static int POS_LEFT = 0;

        final static int POS_RIGHT = 1;

        final static int POS_NEW = 2;

        int mPos;//

        Context mContext = null;

        private LayoutInflater mInflater = null;

        public ConfusedGroupAdapter(int refId, int pos, Context txt) {
            mLib = WordLibAdapter.getInstance(txt);
            mWordList = new WordList(refId);
            mContext = txt;
            mPos = pos;

            mInflater = (LayoutInflater)txt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            if (mWordList == null)
                return 0;
            return mWordList.size();
        }

        public String getItem(int arg0) {
            if (mWordList == null)
                return null;
            return mWordList.get(arg0).mWord;
        }

        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return createViewFromResource(position, convertView, parent,
                    R.layout.confused_group_manage_item);
        }

        private View createViewFromResource(int position, View convertView, ViewGroup parent,
                int resource) {
            View v;
            if (convertView == null) {
                v = mInflater.inflate(resource, parent, false);
                Log.i("convertView", "is null");
            } else {
                v = convertView;
                Log.i("convertView", "is not null");
            }

            bindView(position, v);

            return v;
        }

        class OnClicker implements OnClickListener {
            String mWord;

            Button mMove;

            Button mDel;

            public OnClicker(String wd, Button btnDel, Button btnMove) {
                mWord = wd;
                mMove = btnMove;
                mDel = btnDel;
                // TODO Auto-generated constructor stub
            }

            public void onClick(View v) {
                if (v == mDel) {
                    Toast.makeText(mContext, mWord + " del", Toast.LENGTH_SHORT).show();
                } else if (v == mMove) {
                    if (mPos == POS_LEFT) {
                        moveLeftWord(mWord);
                    } else if (mPos == POS_RIGHT) {
                        moveRightWord(mWord);
                    }
                    Toast.makeText(mContext, mWord + " move", Toast.LENGTH_SHORT).show();
                }
            }

            public void setWord(String word) {
                mWord = word;
            }
        }

        private void bindView(int position, View view) {
            String item = getItem(position);
            final Button delBtn = (Button)view.findViewById(R.id.btn_delete);
            final Button moveBtn = (Button)view.findViewById(R.id.btn_move);

            if (item != null) {
                TextView v = (TextView)view.findViewById(R.id.word);
                v.setText(item);
                OnClicker listener = new OnClicker(item, delBtn, moveBtn);

                delBtn.setOnClickListener(listener);
                moveBtn.setOnClickListener(listener);
            }
        }


    }
    @Override
    public void onDestroy() {
        clearCompareIds();
        super.onDestroy();

    }

}
