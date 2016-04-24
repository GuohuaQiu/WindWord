package qiu.tool.windword;

import android.R.integer;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;




class DownWordContentTask extends AsyncTask<Integer, Integer, Void> {

    private String mWord;
    private Cursor mCursor;
    private final int mType;
    private Handler mHandler;


    public final static int MSG_DOWN_LOAD_BEGIN = 4;
    public final static int MSG_DOWN_LOAD_END = 5;

    public final static int TYPE_SINGLE = 1;
    public final static int TYPE_LIST = 2;

    public final static int CONTENT_ENG_DESCRIPTION = 0;
    public final static int CONTENT_COLINS_DICTION = 1;
    public final static int CONTENT_BILING_SAMPLE = 2;
    public final static int CONTENT_VOICE = 3;
    public final static int CONTENT_ALL = 100;




    public DownWordContentTask(Cursor csr, Handler handler) {
        mCursor = csr;
        mType = TYPE_LIST;//english interpretation.
        mHandler = handler;
    }

    public DownWordContentTask(String word, Handler handler) {
        mWord = word;
        mType = TYPE_SINGLE;
        mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Integer... params) {
        Util.log("down " + mWord + " starting... type is " + mType);
        if(mType == TYPE_SINGLE){
            downWord(mWord);
            return null;
        }else if(mType == TYPE_LIST){
            boolean b = mCursor.moveToFirst();
            if(!b){
                return null;
            }
            while(!mCursor.isAfterLast()){
                String wd = mCursor.getString(0);
                downWord(wd);
                mCursor.moveToNext();
            }
            return null;
        }
        return null;
    }

    private void downWord(String wd){
        Util.downEnglishInter(wd, CONTENT_ENG_DESCRIPTION, mHandler);
        Util.downEnglishInter(wd, CONTENT_COLINS_DICTION, mHandler);
        Util.downEnglishInter(wd, CONTENT_BILING_SAMPLE, mHandler);
        Util.downVoice(wd, mHandler);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.d("MainScreen", "onProgressUpdate: " + values[0]);
        // mProgress.incrementProgressBy(values[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
    }


}
