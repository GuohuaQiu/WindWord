
package qiu.tool.windword;





import qiu.tool.windword.R;
import qiu.tool.windword.R.drawable;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;
import qiu.tool.windword.R.menu;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

import android.widget.Button;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;


import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.io.IOException;
import java.net.ContentHandler;


public class WordPage extends Activity implements View.OnClickListener {

    public static final int VIEW_MODE_LEARN = 1;
    public static final int VIEW_MODE_REFRESH = 2;
    public static final String LAUNCH_MODE = "MODE";
    public static final String LAUNCH_WORD = "WORD";
    public static final String LAUNCH_LEVEL = "LEVEL";

    public static final int VOICE_SATE_NO_FILE = 0;

    public static final int VOICE_SATE_DOWNING_FILE = 1;

    public static final int VOICE_SATE_PLAYING = 2;

    public static final int VOICE_SATE_HAS_FILE = 3;

    private int mVoiceState = VOICE_SATE_NO_FILE;
    MyDefinedMenu mPop = null;
    private int[] icons={R.drawable.ic_gprs_left_focus,R.drawable.ic_gprs_left_focus,R.drawable.ic_gprs_left_focus,R.drawable.ic_gprs_left_focus};
    private String[] items={"退出","复习","上一课","下一课"};

    int mMode;

    Word mWord = null;

    TextView wordView;

    TextView soundView;

    TextView interpretView;

    TextView bookView;

//    TextView numberView;

    TextView mDeliView;

    /** View holding left background image */
//    private View mFolderBgLeft;

//    TextView countView;
//    View     panel_learn_button;
    View     panel_refresh_button;
    View panel_bottom;

    TextView mSampleView;
    View mDowloadView = null;

    View mBtnShowDescription = null;
    View mViewDescription = null;
    View mBtnPass = null;
    View mBtnFail = null;
    View mBtnWordNext = null;
    View mBtnWordPrev = null;
  //deleted on 30 July 2014
//    Button mBtnMore = null;

    TextView mColinsView = null;
    TextView mBilingView = null;

    LampView mLampView = null;

    FrameLayout mEngInterView = null;

    View mDowloadingView = null;

    TextView mEngInterText = null;

    Button mToDownloadEng = null;

    WordList mRefreshList = null;
    WordList mWordList = null;

    boolean todayMode = false;

    String mBook = null;

    int mCount = 0;

    //this id is 0 based.
    int mCurrentId = 0;

    // hard words.
    int mLevel = Util.LOAD_HARD_ALL_LEVEL;
    int mOkCount = 0;

    private boolean showDescription = true;

    private Typeface mSoundFont = null;

    Cursor mCursor = null;

    WordLibAdapter mLib = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            Log.i("FAILED INTENT", "HAHA");
            return;
        }
        mSoundFont = Typeface.createFromAsset(getAssets(), "SEGOEUI.TTF");
        Bundle bunde = intent.getExtras();
        mBook = bunde.getString(WordPage.LAUNCH_WORD);
        mLevel = bunde.getInt(WordPage.LAUNCH_LEVEL);
        Util.log("Span is "+mLevel+" in wordpage ");
        mMode = bunde.getInt(WordPage.LAUNCH_MODE);

        setContentView(R.layout.wordshow);

        wordView = (TextView)findViewById(R.id.word_textview);
        soundView = (TextView)findViewById(R.id.phonetic);
//        mFolderBgLeft = findViewById(R.id.folder_bg_left);

        soundView.setTypeface(mSoundFont);

        soundView.setOnClickListener(this);

        interpretView = (TextView)findViewById(R.id.interpret);

//        countView = (TextView)findViewById(R.id.label_group_count);
//        numberView = (TextView)findViewById(R.id.label_number);
        //        bookView = (TextView)findViewById(R.id.label_book);
        //        sectionView = (TextView)findViewById(R.id.label_group);
        mLampView = (LampView)findViewById(R.id.myview);
        mSampleView = (TextView)findViewById(R.id.sample);
        mEngInterView = (FrameLayout)findViewById(R.id.item_eng_interpret);
        mDowloadingView = findViewById(R.id.downing_eng);
        mDowloadView = findViewById(R.id.down_eng);

        mEngInterText = (TextView)findViewById(R.id.text_eng_interpret);
        mEngInterText.setOnClickListener(this);

        mToDownloadEng = (Button)findViewById(R.id.btn_down_eng);
        mToDownloadEng.setOnClickListener(this);

//        panel_learn_button=findViewById(R.id.learn_buttons);
        panel_refresh_button=findViewById(R.id.refresh_buttons);
        panel_bottom = findViewById(R.id.panel_control);

        mBtnShowDescription = findViewById(R.id.btn_show_description);
        mBtnShowDescription.setOnClickListener(this);

        mViewDescription = findViewById(R.id.discription_content);

        mBtnPass = findViewById(R.id.remember_ok);
        mBtnPass.setOnClickListener(this);
        mBtnFail = findViewById(R.id.remember_fail);
        mBtnFail.setOnClickListener(this);

//        mBtnWordPrev = findViewById(R.id.prev_word);
//        mBtnWordPrev.setOnClickListener(this);

//        mBtnWordNext = findViewById(R.id.next_word);
//        mBtnWordNext.setOnClickListener(this);

        mColinsView = (TextView)findViewById(R.id.text_colins);
        mBilingView = (TextView)findViewById(R.id.text_biling);


//deleted on 30 July 2014
//        mBtnMore = (Button)findViewById(R.id.button_more);// this will change to
//        mBtnMore.setVisibility(View.VISIBLE);
//        mBtnMore.setOnClickListener(this);

        mDeliView = (TextView)findViewById(R.id.section_interpretion);

        mLib = WordLibAdapter.getInstance(this);
        todayMode = false;
        if(mMode == VIEW_MODE_LEARN){
                setWord(mBook);
        } else {
            todayMode = true;
//deleted on 30 July 2014
//            mBtnMore.setVisibility(View.GONE);

            setWordForToday(mLevel);
        }
        initMode();
    }

    private void log(String info){
        Log.i("WordWind",info);
    }

    public void setWordForToday(int level) {
    	Cursor cursor;
        if (level == Util.LOAD_HARD_ALL_LEVEL) {
            cursor = mLib.getTodayRefreshList();
        } else if (level == Util.LOAD_RECENT_ALL_LEVEL) {
            cursor = mLib.getTodayRefreshList();
        } else {
            cursor = mLib.getTodayRefreshList(level);
        }
        mWordList = new WordList(cursor);
        if(cursor.getCount() == 0){
            return;
        }

        mWord = mWordList.get(0);
        mCount = mWordList.size();
        showDescription = false;
        mCurrentId = 0;
        if(mMode == VIEW_MODE_REFRESH){
            mLampView.reset();
        }
        ShowWord();
    }


    public void initMode() {

        if (mMode == VIEW_MODE_REFRESH) {
            mBtnShowDescription.setVisibility(View.VISIBLE);
            mViewDescription.setVisibility(View.INVISIBLE);
            mLampView.setCount(mCount);
            mLampView.setVisibility(View.VISIBLE);
            mLampView.setIndex(0);
//            panel_learn_button.setVisibility(View.INVISIBLE);
            panel_refresh_button.setVisibility(View.VISIBLE);

            firstFailWord();
        } else {// learn mode
            mBtnShowDescription.setVisibility(View.INVISIBLE);
            mViewDescription.setVisibility(View.VISIBLE);

            //will remove learn mode so the learn mode only take effect in ONE WORD.

                panel_bottom.setVisibility(View.GONE);
              //deleted on 30 July 2014
                //mBtnMore.setVisibility(View.GONE);

            mLampView.setVisibility(View.INVISIBLE);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.prev_word: {
//                prevWord();
//                break;
//            }
//            case R.id.next_word: {
//                nextWord();
//                break;
//            }
            case R.id.btn_show_description: {
                showDescription = true;
                showDescription();
                break;
            }
            case R.id.remember_ok: {
                setWordOk();
                break;
            }
            case R.id.remember_fail: {
                setWordFail();
                break;
            }
            case R.id.btn_down_eng: {
                if (mWord.getInterpretionEng() == null) {
                    Message msg = handler.obtainMessage();
                    msg.what = DownWordContentTask.MSG_DOWN_LOAD_BEGIN;
                    msg.arg1 = 0;
                    handler.sendMessage(msg);
                }
                break;
            }
            case R.id.phonetic: {
                if (Util.isRawFileExist(mWord.getWord(), DownWordContentTask.CONTENT_VOICE)) {
                    playVoice();
                } else {
                    if(Util.getAppPath() != null){
                        downVoice();
                    }
                }
                break;
            }
        }
    }
    private void playVoice(){
        Util.playVoice(this,mWord.getWord(),new OnCompletionListener() {

            public void onCompletion(MediaPlayer arg0) {
                Util.log("voice end.");
                updateVoice();
            }
        });
        soundView.setTextColor(0xFF33FFFF);
    }

    public static void openOneWord(Context context, String word) {
        openStudyPage(context, word);
    }


    public static void openStudyPage(Context context, String word) {
        Intent myIntent = new Intent(context, WordPage.class);

        Bundle bundle = new Bundle();

        bundle.putInt(WordPage.LAUNCH_MODE, WordPage.VIEW_MODE_LEARN);
        bundle.putString(WordPage.LAUNCH_WORD, word);
        myIntent.putExtras(bundle);
        context.startActivity(myIntent);
    }

    public static void openRefreshTodayWordPage(Context context) {
        Intent myIntent = new Intent(context, WordPage.class);

        Bundle bundle = new Bundle();

        bundle.putInt(WordPage.LAUNCH_MODE, WordPage.VIEW_MODE_REFRESH);
        bundle.putInt(WordPage.LAUNCH_LEVEL, Util.LOAD_RECENT_ALL_LEVEL);
        myIntent.putExtras(bundle);
        context.startActivity(myIntent);
    }

    public static void openRefreshTodayWordPage(Context context, int level) {
        Intent myIntent = new Intent(context, WordPage.class);

        Bundle bundle = new Bundle();

        bundle.putInt(WordPage.LAUNCH_MODE, WordPage.VIEW_MODE_REFRESH);
        bundle.putInt(WordPage.LAUNCH_LEVEL, level);
        myIntent.putExtras(bundle);
        context.startActivity(myIntent);
    }

    public static void openRefreshHardWordPage(Context context) {
        Intent myIntent = new Intent(context, WordPage.class);

        Bundle bundle = new Bundle();

        bundle.putInt(WordPage.LAUNCH_MODE, WordPage.VIEW_MODE_REFRESH);
        bundle.putInt(WordPage.LAUNCH_LEVEL, Util.LOAD_HARD_ALL_LEVEL);
        myIntent.putExtras(bundle);
        context.startActivity(myIntent);
    }

    public static void openRefreshPage(Context context, String book, int level) {
        Intent myIntent = new Intent(context, WordPage.class);

        Bundle bundle = new Bundle();

        bundle.putInt(WordPage.LAUNCH_MODE, WordPage.VIEW_MODE_REFRESH);
        bundle.putString(WordPage.LAUNCH_WORD, book);
        bundle.putInt(WordPage.LAUNCH_LEVEL, level);
        myIntent.putExtras(bundle);
        context.startActivity(myIntent);
    }


    public void finishRefresh() {
        if(todayMode){
            finishTodayRefresh();
        } else {
        }
    }

    public void setWordOk() {
        mWord.setOK(true);
        mLampView.setPass(mCurrentId,true);
        if (mWord.todayScore == 0 ) {
            if(!mLib.removeWord(mWord.getWord(), WordLibAdapter.DEL_REASON_COMPLETE)){
                mLib.updateWordHistory(mWord.getWord(), 0, true);
            }
        } else {
            mLib.updateWordHistory(mWord.getWord(), mWord.todayScore, true);
            mLib.addSpellWord(mWord.getWord(), 1);
        }
        mWord.setOK(true);
//        mWordList.remove(mWord);
        mCurrentId--;
        mOkCount++;
        nextFailWord();
    }

    public void setWordFail() {
        issueWord(-1);
        mWord.setOK(false);
        nextFailWord();
    }

    public void finishTodayRefresh() {
        for (Word wd : mWordList) {
            if (wd.todayScore < 0 && !wd.isOK()) {
//                mLib.faildOneTime(wd.getWord());
//                mLib.issueTodayWord(wd.getWord(), wd.todayScore, false);
                mLib.updateWordHistory(wd.getWord(), wd.todayScore, false);
            }
        }
    }

    public void issueWord(int score) {
        if (mWord != null) {
            if(todayMode){
                mWord.todayScore += score;
            }
//            mWord.setScore(score);
        } else {
            Log.e("Issue Error", "No word to issue!");
        }
    }

    public void updateVoice(){
        if (mWord == null) {
            Util.log("mWord == null will not update the voice color.");
            return;
        }
        soundView.setText(mWord.getPhonetic());
        if(Util.isRawFileExist(mWord.getWord(), DownWordContentTask.CONTENT_VOICE)){
            soundView.setTextColor(0xFF0088FF);
        }else{
            soundView.setTextColor(0xFF0000FF);
        }
    }

    public void ShowWord() {
        if (mWord == null) {
            return;
        }
        /* Set the result to be displayed in our GUI. */
        updateVoice();

        wordView.setText(mWord.getWord());
//        numberView.setText("No." + (mCurrentId+1));
        showDescription();
        mDeliView.setText("L " + mWord.mLevel);
    }

    public void firstFailWord() {
        mWord = mWordList.GetNextNotOKWord(0);
        if (mWord == null) {
            Log.i("Current Word ", "null in firstFailWord");
            return;
        } else {
            mCurrentId = mWordList.indexOf(mWord);
            showDescription = false;
            Log.i("Current Word in firstFailWord", mWord.getWord());
            ShowWord();
            setRefreshTitle();
        }
    }

    public void setRefreshTitle() {
        int noOkCount = mWordList.size();

        setTitle("Total:" + mCount + "OK:" + mOkCount + " R:"
                + noOkCount);
    }


    public void nextFailWord() {

        mWord = mWordList.GetNextNotOKWord(mCurrentId + 1);
        if (mWord != null) {
            mCurrentId = mWordList.indexOf(mWord);
            showDescription = false;
            Log.i("Current Word in next from 0", mWord.getWord());
            ShowWord();
            setRefreshTitle();

            Util.log("Set as " + mCurrentId);

            mLampView.setIndex(mCurrentId);
        } else {
            if (mMode == VIEW_MODE_REFRESH) {
                finishRefresh();
            }
            finish();
        }
    }

    /*this function only works in LEARN mode.*/
/*    public void nextWord() {
        Word thisword = null;
        thisword = mWordList.getNextWord(mCurrentId);
        if (thisword != null) {
            mCurrentId++;
            mWord = thisword;
            showDescription = false;
            ShowWord();
        }
    }*/

    /*this function only works in LEARN mode.*/
/*    public void prevWord() {
        Word thisword = mWordList.getPrevWord(mCurrentId);

        if (thisword != null) {
            mCurrentId--;
            mWord = thisword;
            showDescription = false;
            ShowWord();
        }
    }*/

    public void showInterpertionEng() {
        boolean bvisible = (mMode == VIEW_MODE_LEARN) || showDescription;
        if (bvisible) {
            if (mWord.getInterpretionEng() == null) {
                mDowloadView.setVisibility(View.VISIBLE);
                mToDownloadEng.setVisibility(View.VISIBLE);
                mEngInterText.setVisibility(View.GONE);
                mDowloadingView.setVisibility(View.INVISIBLE);
            } else {
                mDowloadView.setVisibility(View.INVISIBLE);
                mEngInterText.setText(getEngInterpretTextChars());
                mEngInterText.setVisibility(View.VISIBLE);
            }
            mEngInterView.setVisibility(View.VISIBLE);
        } else {
            mEngInterView.setVisibility(View.GONE);
        }
    }
    /*mark the samples in blue.*/
    SpannableStringBuilder getEngInterpretTextChars(){
        String str = mWord.getInterpretionEng();
        int last_index = -1;
        boolean search_left = true;
        int left_pos = 0;
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        while(true){
            last_index = str.indexOf('\"', last_index);
            if(last_index == -1){
                break;
            }
            if(search_left){
                left_pos = last_index++;
                search_left = false;
            } else {
                search_left = true;
                last_index++;
                style.setSpan(new ForegroundColorSpan(0xff0088ff), left_pos, last_index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return style;
    }

    public void showInterpretion() {
        if (mMode == VIEW_MODE_LEARN) {
            interpretView.setText(mWord.getInterpretion());

            mSampleView.setText(mWord.getSample());
        } else {
            if (showDescription) {
                interpretView.setText(mWord.getInterpretion());
                mSampleView.setText(mWord.getSample());
            } else {
                interpretView.setText("");
                mSampleView.setText("");
            }
        }
    }
    public void setWord(String word){
        updateNewWord(word);
    }

    public void updateNewWord(String word) {
        mWordList = new WordList(mLib.getWord(word));

        if(mWordList.size() == 0){
            Toast.makeText(this, "No word to show.", 4000);
            finish();
            return ;
        }

        mWord = mWordList.get(0);
        mCount = 1;
        showDescription = false;
        mCurrentId = 0;
        if(mMode == VIEW_MODE_REFRESH){
            mLampView.reset();
        }
        ShowWord();
    }

    //2014-07-16
    //sometimes after the last one mWord is null ,so we should judge before use it.
    private boolean isCurrentWord(String word){
        if(mWord == null){
            return false;
        }
        if(mWord.getWord().equals(word)){
            return true;
        }
        return false;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DownWordContentTask.MSG_DOWN_LOAD_BEGIN:
                    if(msg.arg1 == DownWordContentTask.CONTENT_ENG_DESCRIPTION){
                        //show downing.
                        mEngInterView.setVisibility(View.VISIBLE);
                        mEngInterText.setVisibility(View.GONE);
                        mDowloadingView.setVisibility(View.VISIBLE);
                        mToDownloadEng.setVisibility(View.INVISIBLE);
                    }
                    if(Util.getAppPath() == null){
                        Toast.makeText(WordPage.this, "No disk is available.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new DownloadTask(mWord.getWord(),msg.arg1).execute();
                    break;
                case DownWordContentTask.MSG_DOWN_LOAD_END:
                    DownContent content = (DownContent)msg.obj;
                    if(content != null && msg.arg2 == Util.ERROR_NO){
                        mWordList.updateContent(content,msg.arg1);
                        if(msg.arg1 == DownWordContentTask.CONTENT_ENG_DESCRIPTION){
                            mLib.insertInterEng(content.mWord,content.mDestripionEng);
                            if(isCurrentWord(content.mWord)){
                                showInterpertionEng();
                            }
                        } else if(msg.arg1 == DownWordContentTask.CONTENT_COLINS_DICTION){
                            mLib.insertColins(content.mWord,content.mDestripionEng);
                            if(isCurrentWord(content.mWord)){
                                showColins();
                            }
                        } else if(msg.arg1 == DownWordContentTask.CONTENT_BILING_SAMPLE){
                            mLib.insertBiling(content.mWord,content.mDestripionEng);
                            if(isCurrentWord(content.mWord)){
                                showBiling();
                            }
                        } else if(msg.arg1 == DownWordContentTask.CONTENT_VOICE){
                            if(isCurrentWord(content.mWord)){
                                updateVoice();
                            }
                        }
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
    static class DownContent{
        String mWord;
        String mDestripionEng;
        public DownContent(String strWord,String destritionE){
            mWord = strWord;
            mDestripionEng = destritionE;
        }
    }
    private class DownloadTask extends AsyncTask<Integer, Integer, Void> {

        private final String mWord;
        private final int mType;

        public DownloadTask(String word) {
            mWord = word;
            mType = 0;//english interpretation.
        }

        public DownloadTask(String word, int type) {
            mWord = word;
            mType = type;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Integer... params) {
            log("down " + mWord + " starting... type is " + mType);
            if(mType == DownWordContentTask.CONTENT_ALL){
                Util.downEnglishInter(mWord, DownWordContentTask.CONTENT_ENG_DESCRIPTION, handler);
                Util.downEnglishInter(mWord, DownWordContentTask.CONTENT_COLINS_DICTION, handler);
                Util.downEnglishInter(mWord, DownWordContentTask.CONTENT_BILING_SAMPLE, handler);
                Util.downVoice(mWord, handler);
                return null;
            }
            if (mType != DownWordContentTask.CONTENT_VOICE) {
                Util.downEnglishInter(mWord, mType, handler);
                return null;
            } else {
                Util.downVoice(mWord, handler);
                return null;
            }
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
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Action 1"){function1(item.getItemId());}
        else if(item.getTitle()=="Action 2"){function2(item.getItemId());}
        else {return false;}
        return true;
    }

    public void function1(int id){
        Toast.makeText(this, "function 1 called", Toast.LENGTH_SHORT).show();
    }
    public void function2(int id){
        Toast.makeText(this, "function 2 called", Toast.LENGTH_SHORT).show();
    }

    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Action 1");
        menu.add(0, v.getId(), 0, "Action 2");
    }

    void showDescription(){
        if(mMode == VIEW_MODE_REFRESH){
            if(!showDescription){
                mBtnShowDescription.setVisibility(View.VISIBLE);
                mViewDescription.setVisibility(View.INVISIBLE);
                panel_refresh_button.setVisibility(View.INVISIBLE);
                return;
            }else{
                panel_refresh_button.setVisibility(View.VISIBLE);
            }
        }
        mBtnShowDescription.setVisibility(View.INVISIBLE);
        mViewDescription.setVisibility(View.VISIBLE);
        showInterpretion();
        showInterpertionEng();
        showSample();
        showBiling();
        showColins();
    }
    private void showSample(){

    }
    private void showBiling(){
        boolean bvisible = (mMode == VIEW_MODE_LEARN) || showDescription;
        if (bvisible && mWord.getBiling() != null) {
            mBilingView.setText(mWord.getBiling());
            mBilingView.setVisibility(View.VISIBLE);
        } else {
            mBilingView.setVisibility(View.INVISIBLE);
        }
    }
    private void showColins(){
        boolean bvisible = (mMode == VIEW_MODE_LEARN) || showDescription;
        if (bvisible && mWord.getColins() != null) {
            mColinsView.setText(mWord.getColins());
            mColinsView.setVisibility(View.VISIBLE);
        } else {
            mColinsView.setVisibility(View.INVISIBLE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.word_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_down_colins:
                downColins();
                return true;
            case R.id.menu_down_biling:
                downBiling();
                return true;
            case R.id.menu_down_eng_des:
                downEngDescription();
                return true;
            case R.id.menu_history:
                RefreshListPage.openHistoryPage(this, mWord.getWord());
                return true;
            case R.id.menu_add_to_confused_word:
                int refId = mLib.addConfusedWord(mWord.getWord());
                ConfusedWordActivity.openActivity(this, refId);
                return true;
            case R.id.menu_manage_confused_word:
                refId = mLib.addConfusedWord(mWord.getWord());
                ConfusedWordManageActivity.setCompareId(this, refId);
                return true;
            case R.id.menu_delete_temp_file:
                Util.delColinsFiles(mWord.getWord());
                return true;
            case R.id.menu_down_content:
                downContent();
                return true;
            case R.id.menu_down_wdlist_content:
                downList();
                return true;
            default:
                return false;
        }
    }

    void downContent(){
        downColins();
        downBiling();
        downEngDescription();
        downVoice();
    }

    void downColins(){
        Message msg = handler.obtainMessage();
        msg.what = DownWordContentTask.MSG_DOWN_LOAD_BEGIN;
        msg.arg1 = DownWordContentTask.CONTENT_COLINS_DICTION;
        handler.sendMessage(msg);
    }
    void downBiling(){
        Message msg = handler.obtainMessage();
        msg.what = DownWordContentTask.MSG_DOWN_LOAD_BEGIN;
        msg.arg1 = DownWordContentTask.CONTENT_BILING_SAMPLE;
        handler.sendMessage(msg);
    }
    void downEngDescription(){
        Message msg = handler.obtainMessage();
        msg.what = DownWordContentTask.MSG_DOWN_LOAD_BEGIN;
        msg.arg1 = DownWordContentTask.CONTENT_ENG_DESCRIPTION;
        handler.sendMessage(msg);
    }

    void downVoice(){
        soundView.setTextColor(0xFFEE00FF);

        Message msg = handler.obtainMessage();
        msg.what = DownWordContentTask.MSG_DOWN_LOAD_BEGIN;
        msg.arg1 = DownWordContentTask.CONTENT_VOICE;
        handler.sendMessage(msg);
    }
    void downList(){
        Cursor csr = mLib.getNewWordList();
        new DownWordContentTask(csr,handler).execute();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /* no need to do anything.
        log("onConfigurationChanged + " + newConfig.orientation);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.switcher_land);
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.switcher);
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
        loadResource();
        */
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(todayMode){
            finishTodayRefresh();
        }
    }

}
