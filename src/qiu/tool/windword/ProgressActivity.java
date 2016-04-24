
package qiu.tool.windword;

import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

public class ProgressActivity extends Activity {
    // private static final int PROGRESS = 0x1;

    // private ProgressBar mProgress;
    // private int mProgressStatus = 0;

    WordLibAdapter mDb = WordLibAdapter.getInstance(this);

    
    private ScrollitemView mItemView = null;

    private long beginTime = 0;

    private void log(String info) {
        Util.log("ProgressActivity: " + info);
    }

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Intent intent = getIntent();
        Uri uri = (Uri)intent.getData();
        String path = uri.getPath();
        log("Open with this path:" + path);

        setContentView(R.layout.dialoglayout);
        mItemView = (ScrollitemView) findViewById(R.id.items_view);
        beginTime = Calendar.getInstance().getTimeInMillis();
        new ImportTask(path).execute();
    }


    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            log("mUIHandler received msg:" + msg.what);
            switch (msg.what) {
                case MainScreen.MSG_WORD_UPDATED:
                    String newWord = (String)msg.obj;
                    mItemView.AddString(newWord, true);
                    log("Update: " + newWord);
                    break;
                case MainScreen.MSG_WORD_IMPORT_COMPLETED:
                    mItemView.AddString(showResult(), true);
                    //to test the performance, delete it temporarily.
                    //finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    //this is a test purpose function to calculate the time used for whole process.
    private String showResult(){
        long endTime = Calendar.getInstance().getTimeInMillis();
        long timeElapsed = endTime - beginTime;
        return Util.getFormatTimeElapsed(timeElapsed);
    }

    private class ImportTask extends AsyncTask<Integer, Integer, Void> {

        private final String mFilePath;

        public ImportTask(String path) {
            mFilePath = path;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Integer... params) {
            DatabaseBackuper.importData(mDb, mUIHandler, mFilePath);
            return null;
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
    public static void openActivity(Context context, String filePath) {
        Intent myIntent = new Intent(context, ProgressActivity.class);
        Uri uri = Uri.fromFile(new File(filePath));
        Util.log("Open progress activity by " + uri.toString());
        myIntent.setData(uri);
        context.startActivity(myIntent);
    }

}
