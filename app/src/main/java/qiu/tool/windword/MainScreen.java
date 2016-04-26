
package qiu.tool.windword;


import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import qiu.tool.windword.R;
import qiu.tool.windword.R.drawable;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;
import qiu.tool.windword.R.menu;
import qiu.tool.windword.chart.SalesStackedBarChart;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Random;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainScreen extends Activity implements OnClickListener, OnTouchListener {

    public final static int MSG_WORD_UPDATED = 1;

    public final static int MSG_WORD_ADDED = 2;

    public final static int MSG_WORD_IMPORT_COMPLETED = 3;

    private ImportingDlg mProgress = null;

    // to select a file.
    private String[] mFileList;

    private File mPath = null;

    private String mChosenFile = null;

    private static final String FTYPE = ".xml";

    private static final int DIALOG_LOAD_FILE = 1000;
    private static final int DIALOG_DEL_WORD = 1001;

    WordLibAdapter mWordLib = null;

    private Cursor mTop20Cursor = null;

    int mIndexofTop = 0;

    GestureDetector mDetector;


    private TextView mRandomSampleView = null;

    private TextView mSampleTitleView = null;

    // private static final int QUICK_REFRESH_COUNT = 5;

    // private static final int QUICK_LEARN_COUNT = 5;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWordLib = WordLibAdapter.getInstance(this);

        setContentView(R.layout.main_show);
        mTop20Cursor = mWordLib.getTopWords(50);

        mPath = new File(Util.getExternalPath() + "//");
        Util.log("The external path is:" + Util.getExternalPath());
        Util.getAppPath();

        //add slide menu.
//        SlidingMenu menu = new SlidingMenu(this);
//        menu.setMode(SlidingMenu.LEFT);
    }

    private void getWidgets() {
        mRandomSampleView = (TextView)findViewById(R.id.random_sample);
        mSampleTitleView = (TextView)findViewById(R.id.word_title);

        mDetector = new GestureDetector(this, new LearnGestureListener());
        mRandomSampleView.setOnTouchListener(this);
        fillSample();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WORD_UPDATED:
                    if (mProgress != null) {
                        Log.i("MainScreen", "MSG_WORD_UPDATED" + (String)msg.obj);
                        mProgress.newItem((String)msg.obj, 0);
                    }
                    break;
                case MSG_WORD_ADDED:
                    if (mProgress != null) {
                        Log.i("MainScreen", "MSG_WORD_ADDED" + (String)msg.obj);
                        mProgress.newItem((String)msg.obj, 1);
                    }

                    break;
                case MSG_WORD_IMPORT_COMPLETED:

                    break;

            }
            super.handleMessage(msg);
        }
    };

    private void loadFileList() {
        try {
            mPath.mkdirs();
            Util.log("path is " + mPath.getAbsolutePath());
        } catch (SecurityException e) {
            Log.e("LoadFile", "unable to write on the sd card " + e.toString());
        }
        if (mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.contains(FTYPE);
                }
            };
            mFileList = mPath.list(filter);
        } else {
            mFileList = new String[0];
        }
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        Builder builder = new Builder(this);

        switch (id) {
            case DIALOG_LOAD_FILE:
                builder.setTitle("Choose word file");
                loadFileList();
                if (mFileList == null) {
                    Log.e("LoadFile", "Showing file picker before loading the file list");
                    dialog = builder.create();
                    return dialog;
                }
                builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mChosenFile = mFileList[which];
                        new ImportTask().execute();
                        // you can do stuff with the file here too
                    }
                });
                break;
            case DIALOG_DEL_WORD:
                DelConfirmDlg dlg = new DelConfirmDlg(this);
                return dlg.create();
        }
        dialog = builder.show();
        return dialog;
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        Exception e = new Exception("hello simon");
        e.printStackTrace();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.loadfile:
                showDialog(DIALOG_LOAD_FILE);
                return true;
            case R.id.spell_test:
                SpellActivity.open(this);
                return true;
            case R.id.static_graph:
                Intent intent = null;
                intent = new SalesStackedBarChart().execute(this);
                startActivity(intent);
                return true;
            case R.id.static_info:
                openStaticInfoPage();
                return true;
            case R.id.restore_db:
                if (mWordLib.isBlank()) {
                    ProgressActivity.openActivity(this, Util.getDataFile());
                }else{
                    Toast.makeText(this, "Can't import for database is not blank.", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.Below10:
                openWeighted50Page();
                return true;
            case R.id.hard50:
                openHard50Page();
                return true;
            case R.id.failed_list:
                openTop10Page();
                return true;
            case R.id.spell_hard_word:
                SpellActivity.openForFailed(this);

//                showDialog(DIALOG_DEL_WORD);
                return true;
            case R.id.backup_db:
                backupDatabase();
                return true;
            case R.id.menu_old_hard:
                openTooOldPage();
               // searchWord();
                return true;
            case R.id.today_refresh:
                refreshTodayWord();
                return true;
            case R.id.search_chinese:
                searchChineseWord();
                return true;
            case R.id.view_confused:
                ConfusedWordPage.openConfusedPage(this);
                return true;
            case R.id.menu_test:
                test();
 //               WordCountTableActivity.openActivity(this);
 //               WordLibAdapter.test(this);
 //               qiu.tool.windword.testclock.TestActivity.openActivity(this);
                return true;
            case R.id.menu_progress_table:
                TestActivity.openActivity(this);
                return true;
            case R.id.general_info:
                TotalInfoActivity.openActivity(this);
                return true;
            case R.id.backup_dbfile:
                WordLibAdapter.backupFile(this);
                return true;
            default:
                return false;
        }
    }

    public void test() {
        int total = 50;
        boolean space[] = new boolean[total];
        Random rand = new Random();
        int index = 30;
        for (int i = 0; i < index; i++) {
            int randInt;
            do {
                randInt = rand.nextInt(total);
                Util.log("Rand:" + randInt);
                randInt = randInt % total;
            } while (space[randInt]);
            Util.log("----------------------------------------------------");

            space[randInt] = true;
        }
        int ccc = 0;
        for (int i = 0; i < total; i++) {
            if (space[i]) {

                Util.log("Rand(" + ccc + ") in:" + i);
                ccc++;
            }
        }

    }

    public void refreshTodayWord() {
        int count = mWordLib.getTodayRefreshCount();
        if (count > 0) {
            WordPage.openRefreshTodayWordPage(this);
        } else {
            Toast.makeText(this, "No words to refresh.", Toast.LENGTH_LONG).show();
        }
    }


    public void backupDatabase() {
        mWordLib.backupDatabase();
    }

    public void openPopup() {

        Dialog dialog = new Dialog(MainScreen.this);
        dialog.setContentView(R.layout.importing);

        dialog.setTitle("This is my custom dialog box");

        dialog.setCancelable(true);
        TextView text = (TextView)dialog.findViewById(R.id.text_item);

        text.setText("This is actually my first hello popup window, in the future , I will do more such jobs!");

        ImageView img = (ImageView)dialog.findViewById(R.id.icon_item);

        img.setImageResource(R.drawable.icon);

        Button button = (Button)dialog.findViewById(R.id.btn_cancel);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        // now that the dialog is set up, it's time to show it
        dialog.show();
    }

    private class ImportTask extends AsyncTask<Integer, Integer, Void> {

        public ImportTask() {
        }

        @Override
        protected void onPreExecute() {
            mProgress = new ImportingDlg(MainScreen.this);
            mProgress.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {

            LoadWordsFromFile();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("MainScreen", "onProgressUpdate: " + values[0]);
            // mProgress.incrementProgressBy(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgress.dismiss();
        }

    }


    public void openTop10Page() {
        Intent listIntent = new Intent(MainScreen.this, RefreshListPage.class);

        Bundle bundle = new Bundle();

        bundle.putInt(RefreshListPage.LAUNCH_MODE, RefreshListPage.VIEW_MODE_TOP_TEN);

        listIntent.putExtras(bundle);
        startActivity(listIntent);
    }

    public void openStaticInfoPage() {
        Intent listIntent = new Intent(MainScreen.this, RefreshListPage.class);

        Bundle bundle = new Bundle();

        bundle.putInt(RefreshListPage.LAUNCH_MODE, RefreshListPage.VIEW_MODE_STATIC_INFO);

        listIntent.putExtras(bundle);
        startActivity(listIntent);
    }

    public void openBelow10Page() {
        Intent listIntent = new Intent(MainScreen.this, RefreshListPage.class);

        Bundle bundle = new Bundle();
        bundle.putInt(RefreshListPage.LAUNCH_MODE, RefreshListPage.VIEW_MODE_BELOW_TEN);
        listIntent.putExtras(bundle);
        startActivity(listIntent);
    }


    public void openHard50Page() {
        Intent listIntent = new Intent(MainScreen.this, RefreshListPage.class);
        Util.log("openHard50Page()");

        Bundle bundle = new Bundle();
        bundle.putInt(RefreshListPage.LAUNCH_MODE, RefreshListPage.VIEW_MODE_HARD_FIFTY);
        listIntent.putExtras(bundle);
        startActivity(listIntent);
    }

    public void openTooOldPage() {
        Intent listIntent = new Intent(MainScreen.this, RefreshListPage.class);
        Util.log("openTooOldPage()");

        Bundle bundle = new Bundle();
        bundle.putInt(RefreshListPage.LAUNCH_MODE, RefreshListPage.VIEW_MODE_TOO_OLD);
        listIntent.putExtras(bundle);
        startActivity(listIntent);
    }


    public void openWeighted50Page() {
        Intent listIntent = new Intent(MainScreen.this, RefreshListPage.class);
        Util.log("openWeighted50Page()");

        Bundle bundle = new Bundle();
        bundle.putInt(RefreshListPage.LAUNCH_MODE, RefreshListPage.VIEW_MODE_WEIGHTED_FIFTY);
        listIntent.putExtras(bundle);
        startActivity(listIntent);
    }
    public boolean LoadWordsFromFile() {
        try {
            // InputStream is =
            // this.getResources().openRawResource(R.raw.sample);
            /* Create a URL we want to load some xml-data from. */

            /* Get a SAXParser from the SAXPArserFactory. */
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            /* Get the XMLReader of the SAXParser we created. */
            XMLReader xr = sp.getXMLReader();
            /* Create a new ContentHandler and apply it to the XML-Reader */
            WordLoader myExampleHandler = new WordLoader(this, handler);
            xr.setContentHandler(myExampleHandler);

            /* Parse the xml-data from our URL. */
            // xr.parse(new InputSource(is));

            /* Parse the xml-data from our URL. */
            xr.parse(new InputSource(new FileInputStream(Util.getExternalPath()
                    + "//" + mChosenFile)));
            /* Parsing has finished. */

        } catch (Exception e) {
            /* Display any Error to the GUI. */
            Log.e("load error", e.getMessage());
        }
        /* Display the TextView. */

        return false;
    }

    public void onClick(View v) {
    }

    protected void onResume() {
        super.onResume();
        getWidgets();
    }
    protected void searchWord() {
        Intent imgIntent = new Intent(MainScreen.this, SearchWordPage.class);
        startActivity(imgIntent);
    }

    protected void searchChineseWord() {
        Intent imgIntent = new Intent(MainScreen.this, SearchWordPage.class);
        Bundle bundle = new Bundle();
        bundle.putInt(SearchWordPage.LAUNCH_MODE, SearchWordPage.SEARCH_MODE_CHINESE);
        imgIntent.putExtras(bundle);

        startActivity(imgIntent);
    }

    private void fillSample() {
    	if(mTop20Cursor.getCount() == 0) {
    		return;
    	}
        mTop20Cursor.moveToPosition(mIndexofTop);
        if (mTop20Cursor.isAfterLast()) {
            mIndexofTop = 1;
            mTop20Cursor.moveToPosition(0);
        } else {
            mIndexofTop++;
        }
        String sample = mTop20Cursor.getString(mTop20Cursor
                .getColumnIndex(WordLibAdapter.COL_BILING_SAMPLE));
        if (sample == null || sample.length() == 0) {
            sample = mTop20Cursor.getString(mTop20Cursor
                    .getColumnIndex(WordLibAdapter.COL_COLINS_DICT));
        }
        if (sample == null || sample.length() == 0) {
            sample = mTop20Cursor.getString(mTop20Cursor
                    .getColumnIndex(WordLibAdapter.COL_INTER_ENG));
        }
        if (sample == null || sample.length() == 0) {
            sample = mTop20Cursor.getString(mTop20Cursor
                    .getColumnIndex(WordLibAdapter.COL_INTERPRETION));
        }
        String word = mTop20Cursor.getString(mTop20Cursor.getColumnIndex(WordLibAdapter.COL_WORD));
        mSampleTitleView.setText(word);
        if (sample != null) {
            mRandomSampleView.setText(sample);
        }else{
            mRandomSampleView.setText("");
        }
    }

    float lastX = 0;
    float lastY = 0;

    public boolean onTouch(View v, MotionEvent event) {

        Util.log("action is " + event.getAction());

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            lastX = event.getX();
            lastY = event.getY();
            return false;
        } else if (action == MotionEvent.ACTION_UP) {
            float thisX = event.getX();
            float thisY = event.getY();
            if (Math.abs(lastX - thisX) > Math.abs(lastY - thisY)) {
                fillSample();
            }
        }
        return false;

    }

    class LearnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent ev) {

            Util.log("onSingleTapUp");

            return true;
        }

        @Override
        public void onShowPress(MotionEvent ev) {

            Util.log("onShowPress_LG");

        }

        @Override
        public void onLongPress(MotionEvent ev) {

            Util.log("onLong_LG");

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            Util.log("onScroll_LG " + distanceX +"}|{"+ distanceY);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent ev) {

//            Util.log("onDown_LG");

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            fillSample();

            Util.log("onFling_LG"+ velocityX +"}|{"+ velocityY);

            return true;
        }
    }

    public void showDelWindow(){
        DelConfirmDlg dlg = new DelConfirmDlg(this);
        dlg.show();

    }
}
