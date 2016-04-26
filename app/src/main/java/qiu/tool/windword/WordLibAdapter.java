
package qiu.tool.windword;

import android.R.integer;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectableChannel;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;

public class WordLibAdapter {
    public static final int ADAPTER_RET_OK = 0;

    public static final int ADAPTER_RET_EMPTY = 1;

    private static final String TAG = "WordLibAdapter";

    private static final String DATABASE_NAME = "wordlib";

    public static final String COL_WORD = "word";

    public static final String COL_PHONETIC = "phonetic";

    public static final String COL_INTERPRETION = "interpretion";

    public static final String COL_INTER_ENG = "eng_interpretion";

    // public static final String COL_BOOK = "book";

    // public static final String COL_SECTION = "section";

    public static final String COL_SCORE = "score";

    public static final String COL_WORD_LAST_DATE = "WordLastDay";

    public static final String COL_WORD_NEXT_DATE = "WordNextDay";

    public static final String COL_WORD_SPAN = "WordSpanDays";

    public static final String COL_DATE = "LastDay";

    public static final String COL_NEXT_DATE = "NextDay";

    public static final String COL_SPAN = "SpanDays";

    public static final String COL_SAMPLE1 = "sample1";

    public static final String COL_SAMPLE2 = "sample2";

    public static final String COL_SAMPLE3 = "sample3";

    public static final String COL_COLINS_DICT = "colins_dict";

    public static final String COL_BILING_SAMPLE = "biling_sample";

    public static final String COL_DONE = "done";

    public static final String COL_CHANGE = "change";

    public static final String COL_LEVEL = "level";

    public static final String COL_ACTION_DATE = "action_date";

    public static final String COL_SPELL_VALUE = "spell_value";

    public static final String COL_SPELL_DAY = "spell_day";

    public static int sample1Index;

    public static int sample2Index;

    public static int sample3Index;

    public static final int DEL_REASON_COMPLETE = 0;

    public static final int DEL_REASON_REPEAT = 1;

    public static final String COL_ID = "_id";

    public static final String COL_SENTENCE = "sentences";

    public static final String COL_REFERENCE = "ref_id";

    public static final String COL_REASON = "del_reason";

    public static final String COL_SPELL_TIMES = "sepll_times";

    // public static final String DATABASE_TABLE_WORDS = "words";
    public static final String DATABASE_TABLE_WORD = "word_table";

    public static final String DATABASE_TABLE_SCHEDULES = "schedule_table";

    public static final String DATABASE_TABLE_SENTENCES = "sentence_table";

    public static final String DATABASE_TABLE_HISTORY = "history_table";

    public static final String DATABASE_TABLE_CONFUSED = "confused_table";

    public static final String DATABASE_TABLE_DELETED = "deleted_table";

    public static final String DATABASE_TABLE_SPELL = "spell_table";

    private static final int SPAN_OK = 16;

    //Works to do when add a new table :
    //1. add the table name to backupTables;
    //2. add updateTable object to handle import.

    public static final String[] backupTables = new String[] {
            DATABASE_TABLE_WORD, DATABASE_TABLE_HISTORY, DATABASE_TABLE_CONFUSED,
            DATABASE_TABLE_DELETED, DATABASE_TABLE_SPELL
    };

    // right is 1

    private static final int DATABASE_VERSION = 14;

    private static final String DATABASE_WORD_CREATE = "create table " + DATABASE_TABLE_WORD + " ("
            + COL_WORD + " text primary key, " + COL_PHONETIC + " text, " + COL_INTERPRETION
            + " text not null, " + COL_INTER_ENG + " text default null, " + COL_SCORE
            + " integer default 10, " + COL_SAMPLE1 + " integer default null, " + COL_SAMPLE2
            + " integer default null, " + COL_SAMPLE3 + " integer default null, " + COL_COLINS_DICT
            + "  text default null, " + COL_BILING_SAMPLE + "  text default null,"
            + COL_WORD_LAST_DATE + " date DEFAULT 0, " + COL_WORD_NEXT_DATE + " date DEFAULT 0, "
            + COL_WORD_SPAN + " integer not null default -1," + COL_DONE
            + " integer not null default 0 , " + COL_REFERENCE + " integer not null default -1);";

    private static final String DATABASE_SENTENCE_CREATE = "create table "
            + DATABASE_TABLE_SENTENCES + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_SENTENCE + " text);";

    private static final String DATABASE_HISTORY_CREATE = "create table " + DATABASE_TABLE_HISTORY
            + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_WORD + " text,"
            + COL_CHANGE + " integer not null," + COL_LEVEL + " integer not null,"
            + COL_ACTION_DATE + " date not null DEFAULT CURRENT_TIMESTAMP);";

    private static final String DATABASE_CONFUSED_CREATE = "create table "
            + DATABASE_TABLE_CONFUSED + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_WORD + " text," + COL_REFERENCE + " integer not null default -1,"
            + COL_ACTION_DATE + " date not null DEFAULT CURRENT_TIMESTAMP," + COL_SPELL_VALUE
            + " INTEGER NOT NULL DEFAULT 0);";

    private static final String DATABASE_DELETED_CREATE = "create table " + DATABASE_TABLE_DELETED
            + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_WORD + " text,"
            + COL_REASON + " integer not null default " + DEL_REASON_REPEAT + ");";

    private static final String DATABASE_SPELL_CREATE = "create table " + DATABASE_TABLE_SPELL
            + " (" + COL_WORD + " text," + COL_SPELL_TIMES + " integer not null ," + COL_SPELL_DAY
            + "  integer not null default 0);";



    private final Context context;

    private DatabaseHelper DBHelper;

    private SQLiteDatabase db;

    private void log(String info) {
        Util.log("WordLib: " + info);
    }

    private WordLibAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
        Log.i("TEST", "DBHelper : " + DBHelper);
    }

    // Singleton instance
    private static WordLibAdapter INSTANCE = null;

    /**
     * get singleton instance of CallManager
     *
     * @return CallManager
     */
    public static WordLibAdapter getInstance(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = new WordLibAdapter(ctx);
            INSTANCE.open();
        }
        Log.i("TEST", "INSTANCE IS " + INSTANCE);
        return INSTANCE;
    }

    // must be a negative value.
    private static int getSpanSecond(int index) {
        int secs = 3600 * 24;
        int c = index;
        do {
            c++;
            secs /= 2;
        } while (c < 0);
        return secs;
    }

    private static int getSpanDays(int index) {
        if (index < 0) {
            return 0;
        } else if (index > 12) {
            return getSpanDays(index - 1) + (index - 10);
        }

        switch (index) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 4;
            case 3:
                return 7;
            case 4:
                return 15;
            default:
                return 20 + index * 2;
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_WORD_CREATE);
            db.execSQL(DATABASE_SENTENCE_CREATE);
            db.execSQL(DATABASE_HISTORY_CREATE);
            db.execSQL(DATABASE_CONFUSED_CREATE);
            db.execSQL(DATABASE_DELETED_CREATE);
            db.execSQL(DATABASE_SPELL_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            if (oldVersion < 2) {
                String ALTER_TBL = "ALTER TABLE words ADD COLUMN " + COL_COLINS_DICT
                        + "  text default null;";
                db.execSQL(ALTER_TBL);
                ALTER_TBL = "ALTER TABLE words ADD COLUMN " + COL_BILING_SAMPLE
                        + "  text default null;";
                db.execSQL(ALTER_TBL);
            } else if (oldVersion < 3) {
                String ALTER_TBL = "ALTER TABLE words ADD COLUMN " + COL_WORD_LAST_DATE
                        + " date DEFAULT 0; ";
                db.execSQL(ALTER_TBL);
                ALTER_TBL = "ALTER TABLE words ADD COLUMN " + COL_WORD_NEXT_DATE
                        + "  date DEFAULT 0;";
                db.execSQL(ALTER_TBL);
                ALTER_TBL = "ALTER TABLE words ADD COLUMN " + COL_WORD_SPAN
                        + "  integer not null default -1;";
                db.execSQL(ALTER_TBL);
            } else if (oldVersion < 4) {
                String ALTER_TBL = "ALTER TABLE words ADD COLUMN " + COL_DONE
                        + "  integer not null default 0;";
                db.execSQL(ALTER_TBL);
            } else if (oldVersion < 5) {
                db.execSQL(DATABASE_CONFUSED_CREATE);

                String ALTER_TBL = "ALTER TABLE words ADD COLUMN " + COL_REFERENCE
                        + "  integer not null default -1;";
                db.execSQL(ALTER_TBL);
            }
            if (oldVersion < 6) {
                String ALTER_TBL = "DROP TABLE IF EXISTS " + DATABASE_TABLE_SCHEDULES;
                db.execSQL(ALTER_TBL);
            }
            if (oldVersion < 7) {// modify the table words to word, create new
                                 // tabel word and delete old table words.
                db.execSQL(DATABASE_WORD_CREATE);
            }
            if (oldVersion < 8) {// modify the table words to word, create new
                                 // tabel word and delete old table words.
                db.execSQL(DATABASE_DELETED_CREATE);
            }
            if (oldVersion < 10) {
                String ALTER_TBL = "ALTER TABLE " + DATABASE_TABLE_CONFUSED + " ADD COLUMN "
                        + COL_ACTION_DATE + " date not null DEFAULT 1402538678000" + ";";
                db.execSQL(ALTER_TBL);
                ALTER_TBL = "ALTER TABLE " + DATABASE_TABLE_CONFUSED + " ADD COLUMN "
                        + COL_SPELL_VALUE + " INTEGER NOT NULL DEFAULT 0" + ";";
                db.execSQL(ALTER_TBL);
            }
            if (oldVersion < 11) {// 2014-10-08
                db.execSQL(DATABASE_SPELL_CREATE);
            }

            if(oldVersion <14) {//2015-04-04
                String ALTER_TBL = "ALTER TABLE "+DATABASE_TABLE_SPELL+" ADD COLUMN " + COL_SPELL_DAY
                        + "  integer not null default 0;";
                db.execSQL(ALTER_TBL);
            }
        }
    }

    // when one time fail ,update the score.
    public void faildOneTime(String wd) {
        final String strWhere = COL_WORD + "= \'" + wd + "\' ";
        Cursor cursor = db.query(DATABASE_TABLE_WORD, null, strWhere, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        } else {
            log("Error on issueTodayWord no word " + wd + " is found.");
            return;
        }
        int current_span = cursor.getInt(cursor.getColumnIndex(COL_WORD_SPAN));
        log(wd + " Current is " + current_span + " and score is -1 ...");
        current_span--;

        // if (current_span < 0) {
        // current_span = 0;
        // }
        ContentValues values = new ContentValues();

        values.put(COL_WORD_SPAN, current_span);
        db.update(DATABASE_TABLE_WORD, values, strWhere, null);
    }

    private void updateScore(String word) {
        long[] ratio = new long[] {
                10, 9, 8, 7, 6, 5, 4, 3, 2, 1
        };
        final String strWhere = COL_WORD + "= \'" + word + "\' ";
        Cursor cursor = db.query(DATABASE_TABLE_HISTORY, null, strWhere, null, null, null,
                COL_ACTION_DATE + " DESC");
        boolean b = cursor.moveToFirst();
        if (!b) {
            return;
        }

        long score = 0;
        int i = 0;
        while (!cursor.isAfterLast() && i < 10) {
            long change = cursor.getLong(cursor.getColumnIndex(COL_CHANGE));
//            log("change:" + change + "before score :" + score + " ratio:" + ratio[i]);
            if (change <= 0) {
                score += (change) * ratio[i];
            }
//            log("after score :" + score);
            i++;
            cursor.moveToNext();
        }
        issueWord(word, (int)score);
    }

    public boolean isRememberedWord(String word) {
        final String strWhere = COL_WORD + "= \'" + word + "\' ";
        Cursor cursor = db.query(DATABASE_TABLE_WORD, null, strWhere, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        } else {
            log("Error for no word:" + word);
            return false;
        }
        int current_span = cursor.getInt(cursor.getColumnIndex(COL_WORD_SPAN));
        if (current_span < SPAN_OK)
            return false;
        int score = cursor.getInt(cursor.getColumnIndex(WordLibAdapter.COL_SCORE));
        if (score < 0) {
            return false;
        }
        Cursor csr = getConfusedWordList(word);
        if (csr != null) {
            csr.moveToFirst();
            while (!csr.isAfterLast()) {
                int span = csr.getInt(0);
                if (span < SPAN_OK)
                    return false;
                csr.moveToNext();
            }
        }
        return true;
    }

    public void testDisAllOKWord() {
        final String strWhere = COL_SCORE + "=0 and " + COL_WORD_SPAN + ">13";
        Cursor cursor = db.query(DATABASE_TABLE_WORD, null, strWhere, null, null, null, null);
        log("Total OK word:" + cursor.getCount());
        if (cursor.moveToFirst()) {
            // log("Total OK word:"+cursor.getCount());
            int i = 0;
            while (!cursor.isAfterLast()) {
                log("" + i++ + "\t:" + cursor.getString(cursor.getColumnIndex(COL_WORD)));
                cursor.moveToNext();
            }
        }

    }

    // score is 0 represent remember this word.
    // other cases:
    // if isOk is true. -1, -2, -3 represent after 1,2,3 times failed , remember
    // it.
    // if isOK is false -1, -2, -3 represent after 1,2,3 times failed , but not
    // remember it.
    // the orginal name is issueTodayWord
    public void updateWordHistory(String word, int score, boolean isOk) {
        Util.log("updateWordHistory("+word+","+score+","+isOk+").");

        int newSpan;
        int newChange;
        boolean hasHistory = false;

        final String strWord = COL_WORD + "= \'" + word + "\' ";

        Cursor cursor = db.query(DATABASE_TABLE_WORD, null, strWord, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        } else {
            log("Error on issueTodayWord no word " + word + " is found.");
            return;
        }

        int current_span = cursor.getInt(cursor.getColumnIndex(COL_WORD_SPAN));

        Util.log("current_span:"+current_span);

        Cursor cursorHistory = null;
        if (word != null) {
            cursorHistory = getLatestHistory(word);
        }
        int lastSpan = -1000;
        int lastChange = -1000;

        long expectedTime = -1;
        long thisId = 0;

        if (cursorHistory != null && cursorHistory.getCount() > 0) {
            hasHistory = true;
            cursorHistory.moveToFirst();
            lastChange = cursorHistory.getInt(2);
            lastSpan = cursorHistory.getInt(3);
            Util.log("lastSpan:"+lastSpan);

            long thisTime = cursorHistory.getLong(4);
            thisId = cursorHistory.getLong(0);
            expectedTime = getNextTime(lastSpan, thisTime);
            if (current_span != lastSpan) {
                log("Error the span in word table(" + current_span + ") and history table ("
                        + lastSpan + ") is not same!");
            }
            // update as usual.this is the first time
        } else {
            lastSpan = current_span;
        }

        ContentValues historyValues = new ContentValues();
        Calendar currentTime = Calendar.getInstance();
        historyValues.put(COL_ACTION_DATE, currentTime.getTimeInMillis());

        if (!hasHistory || expectedTime <= currentTime.getTimeInMillis()) {
            historyValues.put(COL_WORD, word);
            if (isOk) {
                if (score == 0) {
                    newSpan = lastSpan + 1;
                    newChange = 1;
                    Util.log("u newSpan:(lastSpan + 1)"+newSpan);
                    Util.log("u newChange:(1)"+newChange);
                } else if (score < 0) {
                    newSpan = lastSpan + score;
                    newChange = score;
                    Util.log("v newSpan:(lastSpan + score)"+newSpan);
                    Util.log("v newChange:(score)"+newChange);
                } else {
                    throw new IllegalStateException("score is not valid :" + score);
                }
            } else {
                if (score < 0) {
                    newSpan = lastSpan + score - 1;
                    newChange = score - 1;
                    Util.log("x newSpan:(lastSpan + score + 1)"+newSpan);
                    Util.log("x newChange:(score - 1)"+newChange);

                } else {
                    throw new IllegalStateException("score is not valid when in a failed state :"
                            + score);
                }
            }
            historyValues.put(COL_LEVEL, newSpan);
            historyValues.put(COL_CHANGE, newChange);
            Util.log("s to history newSpan:"+newSpan);
            Util.log("s to history newChange:"+newChange);
            db.insert(DATABASE_TABLE_HISTORY, null, historyValues);
        } else {// has a abnormal history
            // just update it for
            // there are
            // temporarily
            // record.
            if (isOk) {
                newSpan = lastSpan + score + 1;
                newChange = lastChange + score + 1;
                Util.log("y newSpan:(lastSpan + score + 1)"+newSpan);
                Util.log("y newChange:(newChange + score + 1)"+newChange);
            } else {
                newSpan = lastSpan + score;
                newChange = lastChange + score;
                Util.log("z newSpan:(lastSpan + score )"+newSpan);
                Util.log("z newChange:(newChange + score )"+newChange);
            }
            historyValues.put(COL_LEVEL, newSpan);
            Util.log("s to history newSpan:"+newSpan);

            historyValues.put(COL_CHANGE, newChange);
            db.update(DATABASE_TABLE_HISTORY, historyValues, COL_ID + "= \'" + thisId + "\' ", null);
        }

        ContentValues values = new ContentValues();
        Calendar nextTime = Calendar.getInstance();

        if (newSpan < 0) {
            int secs = getSpanSecond(newSpan);
            // update last day is today
            // update next day is today+days
            log(word + " get time from " + current_span + " and SECs is " + secs);
            nextTime.add(Calendar.SECOND, secs);
        } else {
            int days = getSpanDays(newSpan);
            // update last day is today
            // update next day is today+days
            log(word + " get time from " + newSpan + " and days is " + days);
            nextTime.add(Calendar.DAY_OF_YEAR, days);
        }

        // no matter ok or not we should put the last time in.
        if (cursor.getLong(cursor.getColumnIndex(COL_WORD_LAST_DATE)) == 0) {
            // the first time we remember the word.
            values.put(COL_WORD_LAST_DATE, currentTime.getTimeInMillis());
        }

        if (isOk) {
            values.put(COL_WORD_NEXT_DATE, nextTime.getTimeInMillis());
            // if it is a
        } else {
            values.put(COL_WORD_NEXT_DATE, currentTime.getTimeInMillis());
        }
        Util.log("to word newSpan:"+newSpan);

        values.put(COL_WORD_SPAN, newSpan);
        db.update(DATABASE_TABLE_WORD, values, strWord, null);

        updateScore(word);

    }

    // if done the day is updated. otherwise just span is updated.
    public void issueTodayWord(String wd, final int score, final boolean isOk) {

        final String strWhere = COL_WORD + "= \'" + wd + "\' ";
        Cursor cursor = db.query(DATABASE_TABLE_WORD, null, strWhere, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        } else {
            log("Error on issueTodayWord no word " + wd + " is found.");
            return;
        }
        int current_span = cursor.getInt(cursor.getColumnIndex(COL_WORD_SPAN));
        log("issueTodayWord:" + wd + " base " + current_span + " and score is " + score
                + " success " + isOk);

        current_span += score;
        // 0 represent one time fail and ok then.
        if (score < 1) {
            current_span--;
        }

        ContentValues historyValues = new ContentValues();
        ContentValues values = new ContentValues();
        Calendar nextTime = Calendar.getInstance();
        Calendar thisTime = Calendar.getInstance();

        if (current_span < 0) {
            int secs = getSpanSecond(current_span);
            // update last day is today
            // update next day is today+days
            log(wd + " get time from " + current_span + " and SECs is " + secs);
            nextTime.add(Calendar.SECOND, secs);
        } else {
            int days = getSpanDays(current_span);
            // update last day is today
            // update next day is today+days
            log(wd + " get time from " + current_span + " and days is " + days);
            nextTime.add(Calendar.DAY_OF_YEAR, days);
        }
        if (isOk) {
            if (cursor.getLong(cursor.getColumnIndex(COL_WORD_LAST_DATE)) == 0) {
                values.put(COL_WORD_LAST_DATE, thisTime.getTimeInMillis());
            }
            values.put(COL_WORD_NEXT_DATE, nextTime.getTimeInMillis());
        } else {
            // values.put(COL_WORD_LAST_DATE, thisTime.getTimeInMillis());
            values.put(COL_WORD_NEXT_DATE, thisTime.getTimeInMillis());
        }

        values.put(COL_WORD_SPAN, current_span);
        db.update(DATABASE_TABLE_WORD, values, strWhere, null);

        historyValues.put(COL_WORD, wd);
        historyValues.put(COL_LEVEL, current_span);
        if (isOk) {
            historyValues.put(COL_CHANGE, score);
        } else {
            historyValues.put(COL_CHANGE, score - 1);
        }
        historyValues.put(COL_ACTION_DATE, thisTime.getTimeInMillis());
        db.insert(DATABASE_TABLE_HISTORY, null, historyValues);

        log(wd + ":history score :" + score + " span:" + current_span);
        updateScore(wd);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public WordLibAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
        db = null;
        INSTANCE = null;
    }

    public Cursor getTodayRefreshList() {
        Calendar now = Calendar.getInstance();
        long today = now.getTimeInMillis();
        String strWhere = COL_WORD_NEXT_DATE + " < " + today;
        if (db == null) {
            Log.i("TEST", "instance is in gettodaylist " + INSTANCE);
            Toast.makeText(context, "No db", 3000).show();
            return null;
        }
        return db.query(DATABASE_TABLE_WORD, null, strWhere, null, null, null, COL_WORD_NEXT_DATE
                + " DESC", " 0, 20");
    }

    public Cursor getTodayRefreshList(int level) {
        Calendar now = Calendar.getInstance();
        long today = now.getTimeInMillis();
        String strWhere = COL_WORD_NEXT_DATE + " < " + today + " AND " + COL_WORD_SPAN + " IS "
                + level;
        if (db == null) {
            Log.i("TEST", "instance is in gettodaylist " + INSTANCE);
            Toast.makeText(context, "No db", 3000).show();
            return null;
        }
        return db.query(DATABASE_TABLE_WORD, null, strWhere, null, null, null, COL_WORD_NEXT_DATE
                + " DESC", " 0, 50");
    }

    public int getTodayRefreshCount() {
        Cursor cursor = getTodayRefreshList();
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    public boolean isExist(String word) {
        Cursor cursor = db.query(DATABASE_TABLE_WORD, null, COL_WORD + "= \'" + word + "\'", null,
                null, null, null);
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public long insertWord(Word word) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_PHONETIC, word.getPhonetic());
        initialValues.put(COL_INTERPRETION, word.getInterpretion());
        try {
            String thisword = word.getWord();
            // 2014-07-09
            // remove the blank in the tail.
            while (thisword.endsWith(" ")) {
                thisword = thisword.substring(0, thisword.length() - 1);
            }

            if (isExist(thisword)) {
                db.update(DATABASE_TABLE_WORD, initialValues, COL_WORD + "= \'" + thisword + "\'",
                        null);
                return -2;

            } else if (isFinished(thisword)) {
                return -3;

            } else {
                initialValues.put(COL_WORD, thisword);
                db.insert(DATABASE_TABLE_WORD, null, initialValues);
                return 0;
            }
        } catch (SQLException e) {
            Log.e("insert failed", e.getMessage());
            return -1;
        }

    }

    public boolean deleteWord(String word) {
        log(word + " is to be deleted!");

        return db.delete(DATABASE_TABLE_WORD, COL_WORD + " = " + "\'" + word + "\'", null) > 0;
    }

    private boolean issueWord(String word, int value) {
        try {
            // update t set count = count + 1 where ..
            String strSQL = "UPDATE " + DATABASE_TABLE_WORD + " SET " + COL_SCORE + "=" + value
                    + " WHERE " + COL_WORD + " = " + "\'" + word + "\'";

            db.execSQL(strSQL);
            // update history.
            Util.log("Score for " + word + " " + value);

            return true;

        } catch (SQLException e) {
            Log.e("issue failed", e.getMessage());
            return false;
        }
    }

    public Cursor getTopWords(int count) {

        return db.query(DATABASE_TABLE_WORD, null, null, null, null, null, COL_SCORE + " ASC",
                " 0, " + count);//
    }

    public Cursor getBelow10Words() {
        return db.query(DATABASE_TABLE_WORD, null, null, null, null, null, COL_SCORE + " DESC",
                " 0, 20");//
    }

    public Cursor getWord(String word) {

        String strWhere = COL_WORD + "=" + "\'" + word + "\'";
        Cursor cursor;

        cursor = db.query(DATABASE_TABLE_WORD, new String[] {
                COL_WORD, COL_PHONETIC, COL_INTERPRETION, COL_INTER_ENG, COL_BILING_SAMPLE,
                COL_COLINS_DICT, COL_WORD_SPAN
        }, strWhere, null, null, null, null);
        return cursor;
    }

    // get the id of a sentence
    // return -1 if it is not in the database.
    // 2011-12-13
    private long getSentenceId(String sentence) {
        String strSel = " " + COL_SENTENCE + " = \'" + sentence + "\'";
        Cursor cursor = db.query(DATABASE_TABLE_SENTENCES, null, strSel, null, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getLong(cursor.getColumnIndex(COL_ID));
    }

    public boolean insertSentence(String sentence) {
        if (sentence == null)
            return false;
        // insert to sentence table.
        // get sentence index.

        long sentenceIndex = getSentenceId(sentence);
        if (sentenceIndex != -1) {
            return false;
        }
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_SENTENCE, sentence);
        try {
            sentenceIndex = db.insert(DATABASE_TABLE_SENTENCES, null, initialValues);
        } catch (SQLException e) {
            Log.i("insert Sentence", e.toString());
            return false;
        }
        StringTokenizer st = new StringTokenizer(sentence, " ,.");
        while (st.hasMoreTokens()) {
            String strWord = st.nextToken();
            matchSentence(sentenceIndex, strWord);
            Log.i("match word", strWord);
        }

        return false;
    }

    // if the strword is in the word list , add the sentenceIndex to the word
    // record.
    // so the word has a sentence sample linked to it.
    private int matchSentence(long sentenceIndex, String strword) {

        Cursor cursor = db.query(DATABASE_TABLE_WORD, null, COL_WORD + "= \'" + strword + "\'",
                null, null, null, null);
        sample1Index = cursor.getColumnIndex(WordLibAdapter.COL_SAMPLE1);
        sample2Index = cursor.getColumnIndex(WordLibAdapter.COL_SAMPLE2);
        sample3Index = cursor.getColumnIndex(WordLibAdapter.COL_SAMPLE3);
        if (cursor == null || cursor.getCount() == 0) {
            return -1;
        }
        String strSQL = null;
        try {
            cursor.moveToFirst();
            if (cursor.isNull(sample1Index)) {
                strSQL = "UPDATE " + DATABASE_TABLE_WORD + " SET " + COL_SAMPLE1 + " = "
                        + sentenceIndex;
            } else if (cursor.isNull(sample2Index)) {
                strSQL = "UPDATE " + DATABASE_TABLE_WORD + " SET " + COL_SAMPLE2 + " = "
                        + sentenceIndex;
            } else if (cursor.isNull(sample3Index)) {
                strSQL = "UPDATE " + DATABASE_TABLE_WORD + " SET " + COL_SAMPLE3 + " = "
                        + sentenceIndex;
            }

            if (strSQL == null) {
                return -1;
            }
            String condition = " WHERE " + COL_WORD + " = " + "\'" + strword + "\'";
            strSQL += condition;
            db.execSQL(strSQL);
            return 1;
        } catch (SQLException e) {
            Log.e("match word", e.getMessage());
            return -1;
        }
    }

    public String getBilangSample(String word) {
        Cursor cursor = getWordCursor(word);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        int col_idx = cursor.getColumnIndex(COL_BILING_SAMPLE);
        if (col_idx == -1) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(col_idx);
    }

    public String getSample(String word) {
        Cursor cursor = getWordCursor(word);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        sample1Index = cursor.getColumnIndex(WordLibAdapter.COL_SAMPLE1);
        sample2Index = cursor.getColumnIndex(WordLibAdapter.COL_SAMPLE2);
        sample3Index = cursor.getColumnIndex(WordLibAdapter.COL_SAMPLE3);
        String sample = "";

        long samples[] = new long[3];
        samples[0] = -1;
        samples[1] = -1;
        samples[2] = -1;

        int count = 0;

        try {
            cursor.moveToFirst();

            if (!cursor.isNull(sample1Index)) {
                samples[0] = cursor.getLong(sample1Index);
                count++;
                if (!cursor.isNull(sample2Index)) {
                    samples[1] = cursor.getLong(sample2Index);
                    count++;
                    if (!cursor.isNull(sample3Index)) {
                        samples[2] = cursor.getLong(sample3Index);
                        count++;
                    }
                }
            }
            Cursor sentenceCursor = getSentenceForWord(samples, count);
            sentenceCursor.moveToFirst();
            int col_sentence = sentenceCursor.getColumnIndex(COL_SENTENCE);
            while (!sentenceCursor.isAfterLast()) {
                sample += sentenceCursor.getString(col_sentence);
                sample += "\n";
                sentenceCursor.moveToNext();
            }
            return sample;
        } catch (SQLException e) {
            Log.e("get sample", e.getMessage());
            return null;
        }

    }

    // below functions operation one word.

    private Cursor getWordCursor(String word) {
        return db.query(DATABASE_TABLE_WORD, null, COL_WORD + "= \'" + word + "\'", null, null,
                null, null);
    }

    private Cursor getSentenceForWord(long[] indexs, int length) {
        // ID IN (id1, id2, ..., idn)

        String strSel = " " + COL_ID + " IN (";
        for (int i = 0; i < length; i++) {
            strSel += indexs[i];
            if (i < (length - 1)) {
                strSel += ",";
            }
        }
        strSel += ")";
        return db.query(DATABASE_TABLE_SENTENCES, null, strSel, null, null, null, null);
    }

    public boolean insertInterEng(String word, String interE) {
        String strSQL;
        if (word == null || interE == null) {
            return false;
        }

        interE = interE.replace("\'", "\'\'");

        strSQL = "UPDATE " + DATABASE_TABLE_WORD + " SET " + COL_INTER_ENG + " = " + "'" + interE
                + "'" + " WHERE " + COL_WORD + " = " + "\'" + word + "\'";
        db.execSQL(strSQL);
        return true;

    }

    public boolean insertColins(String word, String colins) {
        String strSQL;
        if (word == null || colins == null) {
            return false;
        }

        colins = colins.replace("\'", "\'\'");

        strSQL = "UPDATE " + DATABASE_TABLE_WORD + " SET " + COL_COLINS_DICT + " = " + "'" + colins
                + "'" + " WHERE " + COL_WORD + " = " + "\'" + word + "\'";
        db.execSQL(strSQL);
        return true;
    }

    public boolean insertBiling(String word, String biling) {
        String strSQL;
        if (word == null || biling == null) {
            return false;
        }

        biling = biling.replace("\'", "\'\'");

        strSQL = "UPDATE " + DATABASE_TABLE_WORD + " SET " + COL_BILING_SAMPLE + " = " + "'"
                + biling + "'" + " WHERE " + COL_WORD + " = " + "\'" + word + "\'";
        db.execSQL(strSQL);
        return true;
    }

    public String getDbPath() {
        String packageName = "qiu.tool.wordwind";
        String currentDBPath = "//data//data//" + packageName + "//databases//";
        return currentDBPath;
    }

    public void backupDb() {
        try {
            String packageName = "qiu.tool.windword";
            String ext_path = Util.getAppPath();

            File data = Environment.getDataDirectory();

            if (ext_path != null && Util.isWritable(ext_path)) {
                String currentDBPath = "//data//" + packageName + "//databases//" + DATABASE_NAME;
                String backupDBPath = DATABASE_NAME + ".db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(ext_path, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, backupDB.toString(), Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(context, "SD card cant write.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public Cursor getWordContain(String part) {
        String where = COL_WORD + " LIKE \'%" + part + "%\'";

        return db.query(DATABASE_TABLE_WORD, new String[] {
                COL_WORD, COL_INTERPRETION
        }, where, null, null, null, COL_WORD, " 0, 100");//
    }

    public Cursor getWordContainChinese(String part) {
        String where = COL_INTERPRETION + " LIKE \'%" + part + "%\'";

        return db.query(DATABASE_TABLE_WORD, new String[] {
                COL_WORD, COL_INTERPRETION
        }, where, null, null, null, COL_WORD, " 0, 100");//
    }

    public Cursor getStaticInfo() {
        String cols[] = new String[] {
                COL_WORD_SPAN, "count(*)", "max(" + COL_WORD_NEXT_DATE + ")",
                "min(" + COL_WORD_NEXT_DATE + ")"
        };
        return db.query(DATABASE_TABLE_WORD, cols, null, null, COL_WORD_SPAN, null, null, null);
    }

    public void backupDatabase() {
        DatabaseBackuper backuper = new DatabaseBackuper(db, context);
        backuper.exportData(backupTables);
    }

    public void restoreDatabase() {
        DatabaseBackuper.importData(this, null, null);
    }

    private UpdateTable mUpdateTableWord = null;

    private UpdateTable mUpdateTableHistory = null;

    private UpdateTable mUpdateTableConfused = null;
    private UpdateTable mUpdateTableDeleted = null;
    private UpdateTable mUpdateTableSpell = null;

    public void updateValues(String table, ContentValues values) {
        if (table.equals(DATABASE_TABLE_WORD)) {
            /*
             * if(mUpdateTableWord == null){ mUpdateTableWord = new
             * UpdateTable(DATABASE_TABLE_WORDS, new String[] { COL_WORD,
             * COL_PHONETIC, COL_INTERPRETION, COL_INTER_ENG, COL_SCORE,
             * COL_COLINS_DICT, COL_BILING_SAMPLE, COL_WORD_LAST_DATE,
             * COL_WORD_NEXT_DATE, COL_WORD_SPAN, COL_DONE, COL_REFERENCE }); }
             * mUpdateTableWord.addItem(values);
             */
            updateWord(values);
        } else if (table.equals(DATABASE_TABLE_HISTORY)) {

            if (mUpdateTableHistory == null) {
                mUpdateTableHistory = new UpdateTable(DATABASE_TABLE_HISTORY, new String[] {
                        COL_ID, COL_WORD, COL_CHANGE, COL_LEVEL, COL_ACTION_DATE
                });
            }
            mUpdateTableHistory.addItem(values);/**/

            // updateHistory(values);
        } else if (table.equals(DATABASE_TABLE_CONFUSED)) {
            if (mUpdateTableConfused == null) {
                mUpdateTableConfused = new UpdateTable(DATABASE_TABLE_CONFUSED, new String[] {
                        COL_ID, COL_WORD, COL_REFERENCE,COL_ACTION_DATE,COL_SPELL_VALUE
                });
            }
            mUpdateTableConfused.addItem(values);
            // updateConfusedWord(values);
        } else if (table.equals(DATABASE_TABLE_DELETED)) {
            if (mUpdateTableDeleted == null) {
                mUpdateTableDeleted = new UpdateTable(DATABASE_TABLE_DELETED, new String[] {
                        COL_ID , COL_WORD ,COL_REASON
                });
            }
            mUpdateTableDeleted.addItem(values);
        } else if (table.equals(DATABASE_TABLE_SPELL)) {
            if (mUpdateTableSpell == null) {
                mUpdateTableSpell = new UpdateTable(DATABASE_TABLE_SPELL, new String[] {
                        COL_WORD ,COL_SPELL_TIMES , COL_SPELL_DAY
                });
            }
            mUpdateTableSpell.addItem(values);
       }


    }

    public void updateConfusedWord(ContentValues values) {
        // if (-1 != getConfusedWordRefId(values.getAsString(COL_WORD))) {
        // db.update(DATABASE_TABLE_CONFUSED, values,
        // COL_WORD + "= \'" + values.getAsString(COL_WORD) + "\' ", null);
        // } else {
        db.insert(DATABASE_TABLE_CONFUSED, null, values);
        // }
    }

    public void updateWord(ContentValues values) {
        String word = (String)values.get(COL_WORD);
        if (word == null) {
            Util.log("updateValues cant get a word.");
            return;
        }
        Util.log("Word:" + word + " begin^^^^^^^^^^^^^^^^^^^^^^^^^@^^^^^^^^^^^^^^^^");
        if (isExist(word)) {
            final String strWhere = COL_WORD + "= \'" + word + "\' ";
            int n = db.update(DATABASE_TABLE_WORD, values, strWhere, null);
            Util.log("updateValues " + word + " is updated " + n);
        } else {
            long n = db.insert(DATABASE_TABLE_WORD, null, values);
            Util.log("updateValues " + word + "is inserted " + n);
        }
    }

    public void endImportData() {
        if (mUpdateTableHistory != null) {
            mUpdateTableHistory.endUpdate();
        }
        // writeHistoryStashRecords();
        if (mUpdateTableConfused != null) {
            mUpdateTableConfused.endUpdate();
        }
        if (mUpdateTableDeleted != null) {
            mUpdateTableDeleted.endUpdate();
        }
        if (mUpdateTableSpell != null) {
            mUpdateTableSpell.endUpdate();
        }
    }

    public void beginImportData() {
    }

    class UpdateTable {
        private int countForOneTime = 50;

        private String insertSQL;

        private int mInsertCount = 0;

        private String mColumns[] = null;

        private String insertHead;

        public UpdateTable(String table, String cols[]) {
            mColumns = cols;

            insertHead = "INSERT INTO " + table + "(";
            for (int i = 0; i < mColumns.length; i++) {
                insertHead += mColumns[i];
                if (i < (mColumns.length - 1)) {
                    insertHead += ",";
                } else {
                    insertHead += ") VALUES ";
                }
            }
            Util.log("Head:" + insertHead);
        }

        public void beginUpdate() {
            insertSQL = "";
            mInsertCount = 0;
        }

        public void endUpdate() {
            execSql();
        }

        public void addItem(ContentValues values) {
            if (mInsertCount == 0) {
                insertSQL = "" + insertHead;
            }
            mInsertCount++;
            String oneItem = "";

            if (mInsertCount > 1) {
                oneItem += ",";
            }
            oneItem += "(";
            for (int i = 0; i < mColumns.length; i++) {
                oneItem += "\'" + values.getAsString(mColumns[i]) + "\'";
                if (i < (mColumns.length - 1)) {
                    oneItem += ",";
                } else {
                    oneItem += ")";
                }
            }

            insertSQL += oneItem;
            if (mInsertCount == countForOneTime) {
                execSql();
            }
        }

        private void execSql() {
            if (mInsertCount > 0) {
                insertSQL += ";";
                Util.log("Exec:" + insertSQL);
                db.execSQL(insertSQL);
                mInsertCount = 0;
            }
        }
    }

    private boolean isHistoryExist(ContentValues values) {

        Cursor cursor = db.query(DATABASE_TABLE_HISTORY, null,
                COL_WORD + "= \'" + values.getAsString(COL_WORD) + "\' and " + COL_ACTION_DATE
                        + " is " + values.getAsString(COL_ACTION_DATE), null, null, null, null);
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;

    }

    public Cursor getLatestHistory(String word) {
        Cursor cursor = db.query(DATABASE_TABLE_HISTORY, null, COL_WORD + "= \'" + word + "\' ",
                null, null, null, COL_ACTION_DATE + " DESC", "0,1");
        return cursor;
    }

    public Cursor getHistory(String word) {

        Cursor cursor = db.query(DATABASE_TABLE_HISTORY, null, COL_WORD + "= \'" + word + "\' ",
                null, null, null, COL_ACTION_DATE);
        // if (cursor.getCount() > 0) {
        return cursor;
        // }
        // return null;

    }

    public Cursor getRecentFailedWord(int count) {

        Cursor cursor = db.query(DATABASE_TABLE_HISTORY, new String[] {
                COL_WORD, "MAX(" + COL_ACTION_DATE + ")"
        }, COL_CHANGE + "<= 0", null, COL_WORD, null, "MAX(" + COL_ACTION_DATE + ") DESC");
        if (cursor.getCount() > 0) {
            return cursor;
        }
        return null;
    }

    public int getConfusedWordRefId(String word) {
        Cursor cusor = db.query(DATABASE_TABLE_CONFUSED, null, COL_WORD + "= \'" + word + "\' ",
                null, null, null, null);
        if (cusor == null || cusor.getCount() == 0) {
            return -1;
        }
        cusor.moveToFirst();
        return cusor.getInt(cusor.getColumnIndex(COL_REFERENCE));
    }

    public int addConfusedWord(String word) {
        int refId = getConfusedWordRefId(word);
        if (-1 != refId) {
            return refId;
        }
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_WORD, word);
        Cursor cusor = db.query(DATABASE_TABLE_CONFUSED, null, null, null, null, null, null);
        int count = getNextConfusedRefId();
        initialValues.put(COL_REFERENCE, count);
        db.insert(DATABASE_TABLE_CONFUSED, null, initialValues);
        return count;
    }

    public void updateConfusedWord(String word, int index) {
        if (-1 != getConfusedWordRefId(word)) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_WORD, word);
            initialValues.put(COL_REFERENCE, index);

            db.update(DATABASE_TABLE_CONFUSED, initialValues, COL_WORD + "= \'" + word + "\' ",
                    null);
        }
    }

    public Cursor getConfusedWordList() {
        String cmd = "SELECT ";
        cmd += DATABASE_TABLE_CONFUSED + ".*," + DATABASE_TABLE_WORD + "." + COL_INTERPRETION;
        cmd += " FROM " + DATABASE_TABLE_CONFUSED;
        cmd += " INNER JOIN " + DATABASE_TABLE_WORD;
        cmd += " ON " + DATABASE_TABLE_CONFUSED + "." + COL_WORD + "=" + DATABASE_TABLE_WORD + "."
                + COL_WORD;
        cmd += " ORDER BY " + COL_REFERENCE;
        Cursor csr = db.rawQuery(cmd, null);
        // int count = csr.getColumnCount();
        // for (int i = 0; i < count; i++) {
        // String name = csr.getColumnName(i);
        // Util.log("col" + i + ":" + name);

        // }
        return csr;
    }

    public Cursor getConfusedWordList(long refId) {
        String cmd = "SELECT ";
        cmd += DATABASE_TABLE_CONFUSED + ".*," + DATABASE_TABLE_WORD + "." + COL_INTERPRETION;
        cmd += " FROM " + DATABASE_TABLE_CONFUSED;
        cmd += " INNER JOIN " + DATABASE_TABLE_WORD;
        cmd += " ON " + DATABASE_TABLE_CONFUSED + "." + COL_WORD + "=" + DATABASE_TABLE_WORD + "."
                + COL_WORD + " AND " + DATABASE_TABLE_CONFUSED + "." + COL_REFERENCE + "=" + refId;
        cmd += " ORDER BY " + COL_REFERENCE;

        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);
        int count = csr.getColumnCount();
        for (int i = 0; i < count; i++) {
            String name = csr.getColumnName(i);
            Util.log("col" + i + ":" + name);
        }
        return csr;

    }

    public Cursor getConfusedWordList(String word) {
        int refId = getConfusedWordRefId(word);
        if (refId == -1) {
            return null;
        }
        // return getConfusedWordList(refId);

        String cmd = "SELECT ";
        cmd += DATABASE_TABLE_WORD + "." + COL_WORD_SPAN + "," + DATABASE_TABLE_CONFUSED + ".*";
        cmd += " FROM " + DATABASE_TABLE_CONFUSED;
        cmd += " INNER JOIN " + DATABASE_TABLE_WORD;
        cmd += " ON " + DATABASE_TABLE_CONFUSED + "." + COL_WORD + "=" + DATABASE_TABLE_WORD + "."
                + COL_WORD + " AND " + DATABASE_TABLE_CONFUSED + "." + COL_REFERENCE + "=" + refId;

        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);
        int count = csr.getColumnCount();
        for (int i = 0; i < count; i++) {
            String name = csr.getColumnName(i);
            Util.log("col" + i + ":" + name);
        }
        csr.moveToFirst();
        while (!csr.isAfterLast()) {
            int span = csr.getInt(0);
            String wd = csr.getString(2);
            Util.log(wd + " SPAN IS " + span);
            csr.moveToNext();
        }
        return csr;

    }

    public void mergeConfusedGroup(int refIdA, int refIdB) {
        Util.log("merge " + refIdA + "= =" + refIdB);
        int bigId = refIdA > refIdB ? refIdA : refIdB;
        int smallId = refIdA > refIdB ? refIdB : refIdA;
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_REFERENCE, smallId);

        db.update(DATABASE_TABLE_CONFUSED, initialValues, COL_REFERENCE + "= " + bigId + " ", null);
    }

    public Cursor getAllTime() {
        return db.query(DATABASE_TABLE_WORD, new String[] {
                COL_WORD_SPAN, COL_WORD_NEXT_DATE
        }, null, null, null, null, COL_WORD_SPAN + "," + COL_WORD_NEXT_DATE, null);

    }

    public int getSpanCount() {
        String cmd = "SELECT COUNT(" + COL_WORD_SPAN + ") FROM " + DATABASE_TABLE_WORD;
        if (db == null) {
            return 0;
        }
        Cursor csr = db.rawQuery(cmd, null);
        if (csr == null) {
            return 0;
        }
        csr.moveToFirst();
        return csr.getInt(0);
    }

    public long getLongestTime() {
        String cmd = "SELECT MAX(" + COL_WORD_NEXT_DATE + ") FROM " + DATABASE_TABLE_WORD;
        if (db == null) {
            return 0;
        }
        Cursor csr = db.rawQuery(cmd, null);
        if (csr == null) {
            return 0;
        }
        csr.moveToFirst();
        return csr.getLong(0);
    }

    public Cursor getNewWordList() {
        return db.query(DATABASE_TABLE_WORD, new String[] {
            COL_WORD
        }, COL_WORD_SPAN + "<=0", null, null, null, null);//
    }

    public Cursor getWordsRefreshCount() {

        String a_word = "a." + COL_WORD;
        String a_level = "a." + COL_WORD_SPAN;
        String b_word = "b." + COL_WORD;
        String counts = "count(" + b_word + ")";
        String counts_as_cnts = counts + " as cnts ";

        String hard_level = counts + "*100/(" + a_level + "+2) as total ";
        String cmd = "select " + a_word + "," + hard_level + "," + counts_as_cnts + "," + a_level
                + " from " + DATABASE_TABLE_WORD + " as a," + DATABASE_TABLE_HISTORY
                + " as b where " + a_word + "=" + b_word + " and " + a_level + ">0 group by "
                + a_word + " order by total desc limit 50 offset 0;";
        Cursor cursor = db.rawQuery(cmd, null);
        /*
         * print log cursor.moveToFirst(); while(!cursor.isAfterLast()){ String
         * wd = cursor.getString(0); long hard = cursor.getLong(1); long count =
         * cursor.getLong(2); long level = cursor.getLong(3); Util.log("zee:\t"+
         * wd + "\t"+hard + "\t"+ count+"\t"+level); cursor.moveToNext(); }
         */
        return cursor;
    }

    // get the word has action recently.
    public Cursor getActionedWord(int count) {

        Cursor cursor = db.query(DATABASE_TABLE_HISTORY, new String[] {
                COL_WORD, "MAX(" + COL_ACTION_DATE + ")"
        }, null, null, COL_WORD, null, "MAX(" + COL_ACTION_DATE + ") DESC", "0," + count);
        if (cursor.getCount() == 0) {
            Util.log("No word has been viewed!");
            return null;
        }
        return cursor;
    }

    // input is the cursor contains the word .
    public void checkError(Cursor cursor) {
        cursor.moveToFirst();

        int i = 0;
        mErrorcount = 0;
        Util.log("Total history count:" + getHistoryCount());
        while (!cursor.isAfterLast()) {

            String word = cursor.getString(0);
            checkError(word);
            cursor.moveToNext();
            i++;
            // log(""+ i + ": " + word);
        }
        if (mErrorcount > 0) {
            log("Deleted count:" + mErrorcount);
        } else {
            log("Deleted count |||:" + mErrorcount);
        }

    }

    int mErrorcount = 0;

    public void checkError(String word) {
        Cursor cursorHistory = null;
        if (word != null) {
            cursorHistory = getHistory(word);
        }
        if (cursorHistory == null) {
            return;
        }
        cursorHistory.moveToLast();
        int lastSpan = -1000;
        int lastChange = -1000;
        long lastTime = 0;
        int thisSpan = -1000;
        int thisChange = -1000;
        long thisTime;
        long expectedTime;
        long lastId = -1000;
        long thisId;

        while (!cursorHistory.isBeforeFirst()) {
            if (lastSpan == -1000) {
                lastChange = cursorHistory.getInt(2);
                lastSpan = cursorHistory.getInt(3);
                lastTime = cursorHistory.getLong(4);
                lastId = cursorHistory.getLong(0);
            } else {
                thisChange = cursorHistory.getInt(2);
                thisSpan = cursorHistory.getInt(3);
                thisTime = cursorHistory.getLong(4);
                thisId = cursorHistory.getLong(0);

                expectedTime = getNextTime(thisSpan, thisTime);
                if (thisSpan == lastSpan && lastChange == thisChange) {
                    // log(" Span Error for <<"
                    // + word + ">> ---------------");
                    // log("P " + lastChange + " " + lastSpan + " "+
                    // Util.getFormatDateTime(lastTime)
                    // );
                    // log("T " + thisChange + " " + thisSpan + " "
                    // + Util.getFormatDateTime(thisTime));
                    // delHistoryRecord(lastId);
                    // mErrorcount++;
                } else if (lastTime < expectedTime && thisChange < 0) {
                    // if (word.equals("shrink")) {
                    Util.log(word + ": based(" + thisChange + ")" + thisSpan + " in "
                            + Util.getFormatDateTime(thisTime) + "  expect "
                            + Util.getFormatDateTime(expectedTime) + "Actual (" + lastChange + ")"
                            + lastSpan + " " + Util.getFormatDateTime(lastTime));

                    ContentValues values = new ContentValues();
                    values.put(COL_CHANGE, thisChange + lastChange);

                    db.update(DATABASE_TABLE_HISTORY, values, COL_ID + "=" + lastId, null);
                    db.delete(DATABASE_TABLE_HISTORY, COL_ID + "=" + thisId, null);

                    mErrorcount++;
                    if (mErrorcount == 20)
                        break;
                    // }
                }
                lastChange = thisChange;
                lastSpan = thisSpan;
                lastTime = thisTime;
                lastId = thisId;
            }

            cursorHistory.moveToPrevious();
        }
        cursorHistory.close();
        cursorHistory = null;

    }

    // to do use this function in normal updating work.
    private long getNextTime(int current_span, long this_time) {
        Calendar nextTime = Calendar.getInstance();

        nextTime.setTimeInMillis(this_time);

        if (current_span < 0) {
            int secs = getSpanSecond(current_span);
            nextTime.add(Calendar.SECOND, secs);
        } else {
            int days = getSpanDays(current_span);
            nextTime.add(Calendar.DAY_OF_YEAR, days);
        }
        // log("Next time:"+ current_span +" "+
        // Util.getFormatDateTime(nextTime.getTimeInMillis()));
        return nextTime.getTimeInMillis();
    }

    public static void backupFile(Context txt) {
        WordLibAdapter ins = getInstance(txt);
        ins.backupDb();
    }

    public static void test(Context txt) {
        // Cursor csr = ins.getWordList(0);
        // Util.log("Number of level(0):"+ csr.getCount());

        // ins.correctValue01();
        // ins.test_getStaticInfo();
        // ins.test_updateFirstInfo();
        // ins.findSameWords();
        // Cursor csr = ins.getActionedWord(4000);
        // ins.checkError(csr);
        WordLibAdapter ins = getInstance(txt);
        //ins.deleteJustImportedWord();
        // ins.findBlankWord();
        // ins.correctBlankEndWord();
        // Util.log("--------------------after---------------------");
        // ins.findBlankWord();
    }

    public void deleteJustImportedWord() {

        Cursor c = getWordList(-4, -1, Util.getTimeBegin("14-10-09"), Util.getTimeEnd("14-10-09"));
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String s = c.getString(c.getColumnIndex(COL_WORD));
                Util.log("Word to remove:" + s);
                removeWord(s,DEL_REASON_REPEAT);
                c.moveToNext();
            }
        }

    }

    /*
     * private void updateScores(Cursor csr){ csr.moveToFirst(); while
     * (!csr.isAfterLast()){ String word = csr.getString(0); updateScore(word);
     * csr.moveToNext(); } }
     */
    public boolean removeWord(String word, int reason) {
        if (reason == DEL_REASON_COMPLETE) {
            if (!isRememberedWord(word)) {
                return false;
            }
        }
        boolean b;
        Util.log("The word:" + word + "is to be deleted from database!");
        b = deleteWord(word);
        if (b) {
            log("Delete " + word + "from main table success!");
        } else {
            log("Delete " + word + "from main table failed!");
        }
        b = deleteHistory(word);
        if (b) {
            log("Delete " + word + "from history table success!");
        } else {
            log("Delete " + word + "from history table failed!");
        }
        b = deleteConfused(word);
        if (b) {
            log("Delete " + word + "from confused table success!");
        } else {
            log("Delete " + word + "from confused table failed!");
        }
        b = deleteSpell(word);
        if (b) {
            log("Delete " + word + "from confused table success!");
        } else {
            log("Delete " + word + "from confused table failed!");
        }
        addDelHistory(word, reason);
        return true;
    }

    private boolean deleteHistory(String word) {
        final String strWhere = COL_WORD + "= \'" + word + "\' ";

        return db.delete(DATABASE_TABLE_HISTORY, strWhere, null) > 0;
    }

    private boolean deleteConfused(String word) {
        final String strWhere = COL_WORD + "= \'" + word + "\' ";
        return db.delete(DATABASE_TABLE_CONFUSED, strWhere, null) > 0;
    }

    private boolean deleteSpell(String word) {
        final String strWhere = COL_WORD + "= \'" + word + "\' ";
        return db.delete(DATABASE_TABLE_SPELL, strWhere, null) > 0;
    }

    private void addDelHistory(String word, int reason) {
        ContentValues values = new ContentValues();
        values.put(COL_WORD, word);
        values.put(COL_REASON, reason);
        db.insert(DATABASE_TABLE_DELETED, null, values);
    }

    private boolean delHistoryRecord(long id) {
        final String strWhere = COL_ID + "=" + id;
        return db.delete(DATABASE_TABLE_HISTORY, strWhere, null) > 0;
    }

    private int getHistoryCount() {
        Cursor cr = db.query(DATABASE_TABLE_HISTORY, null, null, null, null, null, null);
        if (cr.moveToFirst()) {
            return cr.getCount();
        }
        return 0;
    }

    private Cursor getAllWord() {
        return db.query(DATABASE_TABLE_WORD, new String[] {
            COL_WORD
        }, null, null, null, null, null);//
    }

    private int findSameWords() {
        Cursor cursor = getAllWord();
        cursor.moveToFirst();

        ArrayList<String> a = new ArrayList<String>();

        while (!cursor.isAfterLast()) {
            a.add(cursor.getString(0));
            cursor.moveToNext();
        }
        String[] p = (String[])a.toArray();
        int c = 0;
        for (int i = 0; i < p.length - 2; i++) {
            for (int j = i + 1; i < p.length - 1; i++) {
                if (p[i].compareToIgnoreCase(p[j]) == 0) {
                    log("Same:" + p[i] + "\t" + p[j]);
                    c++;
                }
            }
        }
        log("Total Same word:" + c);
        return c;
    }

    // This is a temporary function to replace the LAST TIME as the FIRST time.
    // HAVE DONE. 2014-01-17

    private void updateFirstInfo(String word) {
        String strWhere = COL_WORD + "=" + "\'" + word + "\'";
        Cursor cursor = db.query(DATABASE_TABLE_HISTORY, new String[] {
                COL_WORD, COL_ACTION_DATE
        }, strWhere, null, null, null, COL_ACTION_DATE + " ASC", "0,1");

        if (cursor.moveToFirst()) {
            long datetime = cursor.getLong(1);
            // ==========================
            ContentValues values = new ContentValues();
            values.put(COL_WORD_LAST_DATE, datetime);
            db.update(DATABASE_TABLE_WORD, values, strWhere, null);
            log("Word:" + word + "\t " + Util.getFormatDateTime(datetime));
            // ======================
        }
        cursor.close();
    }

    /*
     * Hashtable<String, String> hashTable = new Hashtable<String, String>();
     * for(String s: arrayOfString){ String[] array = s.split(";"); String sKey
     * ="", sValue=""; if(array.length > 1){ sKey = array[0]; sValue = array[1];
     * hashTable.put(sKey, sValue); } }
     *
     * Enumeration<String> enumer = hashTable.keys(); while
     * (enumer.hasMoreElements()) { String keyFromTable = (String)
     * enumer.nextElement(); // get Returns the value to which the specified key
     * is mapped, // or null if this map contains no mapping for the key
     * System.out.println(keyFromTable + " = " + hashTable.get(keyFromTable)); }
     */

    class LevelInfo {
        int mAverage = -1;

        // 2014-07-22
        int mTotalCount = 0;

        int maxLevel = getMaxLevel();
        int countInfo[] = new int[maxLevel+1];

        public LevelInfo(int level) {
            for (int i = 0; i <= maxLevel; i++) {
                countInfo[i] = 0;
            }
            add(level);
        }

        void set(int level, int count) {
            if (level < 0 || level> maxLevel) {
                return;
            }
            countInfo[level] = count;
        }

        void add(int level) {
            if (level < 0 || level> maxLevel) {
                return;
            }
            countInfo[level]++;
        }

        String getInfo() {
            String s = "";
            for (int i = 0; i <= maxLevel; i++) {
                s += countInfo[i] + ";";
            }
            return s;
        }

        public int getCountInfo(int level) {
            if (level < 0 || level> maxLevel) {
                return -1;
            }
            return countInfo[level];
        }

        public void calAverage() {
            String strLog = "";
            int total = 0;
            int number = 0;
            for (int i = 0; i <= maxLevel; i++) {
                strLog += "" + countInfo[i] + ";";
                number += countInfo[i];
                total += countInfo[i] * i;
            }
            if (number > 0) {
                mAverage = total / number;
                strLog += ">;" + mAverage;
            }
            Util.log(strLog);

        }

        public int getAverageLevel() {
            if (mAverage == -1) {
                calAverage();
            }
            return mAverage;
        }

        public void calTotal() {
            mTotalCount = 0;
            for (int i = 0; i <= maxLevel; i++) {
                mTotalCount += countInfo[i];
            }
        }
    }

    class CountReportInfo {
        int maxLevel;

        Hashtable<String, LevelInfo> countHashtable;
    }

    private void test_updateFirstInfo() {
        Cursor cursor = db.query(DATABASE_TABLE_WORD, new String[] {
                COL_WORD, COL_WORD_LAST_DATE, COL_WORD_SPAN
        }, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            updateFirstInfo(cursor.getString(0));
            cursor.moveToNext();
        }
    }

    public CountReportInfo getStaticTableInfo() {
        Cursor cursor = db.query(DATABASE_TABLE_WORD, new String[] {
                COL_WORD, COL_WORD_LAST_DATE, COL_WORD_SPAN
        }, null, null, null, null, null, null);

        Hashtable<String, LevelInfo> hashTable = new Hashtable<String, LevelInfo>();

        cursor.moveToFirst();
        int maxLevel = 0;
        while (!cursor.isAfterLast()) {
            long time = cursor.getLong(1);
            int level = cursor.getInt(2);
            if (level > maxLevel) {
                maxLevel = level;
            }
            String key = Util.getShortFormatDate(time);

            if (hashTable.containsKey(key)) {
                LevelInfo info = hashTable.get(key);
                info.add(level);
            } else {
                hashTable.put(key, new LevelInfo(level));
            }
            cursor.moveToNext();
        }
        CountReportInfo info = new CountReportInfo();
        info.maxLevel = maxLevel;
        Util.log("Max level is " + maxLevel);
        info.countHashtable = hashTable;
        // return hashTable;
        return info;

    }

    private void test_getStaticInfo() {
        Cursor cursor = db.query(DATABASE_TABLE_WORD, new String[] {
                COL_WORD, COL_WORD_LAST_DATE, COL_WORD_SPAN
        }, null, null, null, null, null, null);

        Hashtable<String, LevelInfo> hashTable = new Hashtable<String, LevelInfo>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long time = cursor.getLong(1);
            int level = cursor.getInt(2);
            String key = Util.getFormatDate(time);

            if (hashTable.containsKey(key)) {
                LevelInfo info = hashTable.get(key);
                info.add(level);
            } else {
                hashTable.put(key, new LevelInfo(level));
            }
            cursor.moveToNext();
        }

        Enumeration<String> enumer = hashTable.keys();
        while (enumer.hasMoreElements()) {
            String keyFromTable = (String)enumer.nextElement();
            // get Returns the value to which the specified key is mapped,
            // or null if this map contains no mapping for the key
            log(keyFromTable + ":" + hashTable.get(keyFromTable).getInfo());
        }
    }

    public int getRecordCount(String table) {
        String sql = "select count(*) from " + table;
        Cursor csr = db.rawQuery(sql, null);
        if (csr != null) {
            if (csr.moveToFirst()) {
                return csr.getInt(0);
            }
        }
        return 0;
    }

    public int getRecordCount(String table, String where) {
        String sql = "select count(*) from " + table + " where " + where;
        Cursor csr = db.rawQuery(sql, null);
        if (csr != null) {
            if (csr.moveToFirst()) {
                return csr.getInt(0);
            }
        }
        return 0;
    }

    public void correctHistoryValue() {
        ContentValues values = new ContentValues();

        values.put(COL_CHANGE, -5);
        db.update(DATABASE_TABLE_HISTORY, values, COL_CHANGE + "= \'" + -4 + "\' ", null);
        values.put(COL_CHANGE, -4);
        db.update(DATABASE_TABLE_HISTORY, values, COL_CHANGE + "= \'" + -3 + "\' ", null);
        values.put(COL_CHANGE, -3);
        db.update(DATABASE_TABLE_HISTORY, values, COL_CHANGE + "= \'" + -2 + "\' ", null);
        values.put(COL_CHANGE, -2);
        db.update(DATABASE_TABLE_HISTORY, values, COL_CHANGE + "= \'" + -1 + "\' ", null);
        values.put(COL_CHANGE, -1);
        db.update(DATABASE_TABLE_HISTORY, values, COL_CHANGE + "= \'" + 0 + "\' ", null);
    }

    // 2014-3-5
    // there are some word has wrong data for the new data is replaced by
    // importing the backup data
    // those data has below attribute:
    // 1. History: change is 2 or 0.
    //
    // method:
    // 1.get the history which change value is 2 or 0
    // 2.subtract 1 from the change value. and update history level.
    // 3.update word level accroding to the new value and the due date.
    private void correctValue() {
        Cursor cursor = db.query(DATABASE_TABLE_HISTORY, null, COL_CHANGE + " IN (0,2)", null,
                null, null, null);
        long id = -1;
        int change, level;
        final int idx_id = cursor.getColumnIndex(COL_ID);
        final int idx_change = cursor.getColumnIndex(COL_CHANGE);
        final int idx_level = cursor.getColumnIndex(COL_LEVEL);
        final int idx_wd = cursor.getColumnIndex(COL_WORD);
        final int idx_time = cursor.getColumnIndex(COL_ACTION_DATE);
        long time;

        ContentValues historyValues = new ContentValues();
        ContentValues wdValues = new ContentValues();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String wd = cursor.getString(idx_wd);
                change = cursor.getInt(idx_change);
                level = cursor.getInt(idx_level);
                time = cursor.getLong(idx_time);

                Util.log("CORRECT:" + cursor.getString(cursor.getColumnIndex(COL_WORD))
                        + " CHANGE AND LEVEL:" + change + " " + level);
                id = cursor.getLong(idx_id);
                change -= 1;
                level -= 1;

                historyValues.put(COL_CHANGE, change);
                historyValues.put(COL_LEVEL, level);

                wdValues.put(COL_WORD_SPAN, level);
                time = getNextTime(level, time);

                wdValues.put(COL_WORD_NEXT_DATE, time);

                Util.log("Next time:" + Util.getFormatDateTime(time));

                db.update(DATABASE_TABLE_HISTORY, historyValues, COL_ID + "=" + id, null);
                db.update(DATABASE_TABLE_WORD, wdValues, COL_WORD + "=\'" + wd + "\'", null);

                cursor.moveToNext();
            }
        }
    }

    // 2014-03-07
    // There is a bug which made the word level to -1000 after first time
    // remember.
    // Function:
    // 1. Delete all history records the level great than -100
    // 2. Update word table set the level as -1 and the last date as 0.
    private void correctValue01() {
        String strWhere = COL_LEVEL + "<-100";
        String strWhereWd = COL_WORD_SPAN + "<-100";

        // update the word table

        ContentValues wdValues = new ContentValues();

        wdValues.put(COL_WORD_SPAN, -1);
        wdValues.put(COL_WORD_LAST_DATE, 0);
        wdValues.put(COL_WORD_NEXT_DATE, 0);
        db.update(DATABASE_TABLE_WORD, wdValues, strWhereWd, null);

        // delete the history items
        int count = getRecordCount(DATABASE_TABLE_HISTORY);
        int count_to_del = getRecordCount(DATABASE_TABLE_HISTORY, strWhere);
        Util.log("Total:" + count + " to del:" + count_to_del + " after del:"
                + (count - count_to_del));
        db.delete(DATABASE_TABLE_HISTORY, strWhere, null);
        count = getRecordCount(DATABASE_TABLE_HISTORY);
        count_to_del = getRecordCount(DATABASE_TABLE_HISTORY, strWhere);
        Util.log("After Total:" + count + " to del:" + count_to_del + " after del:"
                + (count - count_to_del));

    }

    private void correctValues(String word) {
        Cursor cursor = getHistory(word);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cursor.moveToNext();
            }
        }

    }

    public boolean isBlank() {
        Cursor cursor = db.query(DATABASE_TABLE_WORD, null, null, null, null, null, null);
        if (cursor == null) {
            return true;
        }
        if (cursor.getCount() > 0) {
            return false;
        }
        return true;
    }

    public Cursor getTooOldWord() {
        Util.log("getTooOldWord");
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(2013, Calendar.JUNE, 1, 0, 0, 0);

        Util.log("2013-5-1:" + currentTime.getTimeInMillis());
        String strWhere = COL_WORD_SPAN + "<5 AND " + COL_WORD_LAST_DATE + "<"
                + currentTime.getTimeInMillis();
        Cursor cursor = db.query(DATABASE_TABLE_WORD, null, strWhere, null, null, null,
                COL_WORD_LAST_DATE, null);
        if (cursor.getCount() == 0) {
            Util.log("No word has been viewed!");
            return null;
        }
        return cursor;

    }

    // 2014-04-28
    public Cursor getWordList(int startLevel, int endLevel, long startDate, long endDate) {

        final String strWhere = COL_WORD_SPAN + ">=" + startLevel + " and " + COL_WORD_SPAN + "<="
                + endLevel + " and " + COL_WORD_LAST_DATE + ">=" + startDate + " and "
                + COL_WORD_LAST_DATE + "<=" + endDate;

        Util.log(strWhere);

        // test begin
        Cursor csr = db.query(DATABASE_TABLE_WORD, null, strWhere, null, null, null, null);
        dumpWordCursor(csr);
        // test end

        return db.query(DATABASE_TABLE_WORD, null, strWhere, null, null, null, null);

    }

    // 2014-04-28
    private void dumpWordCursor(Cursor cursor) {

        if (!cursor.moveToFirst()) {
            Util.log("no one is load.");
        }
        int word_index = cursor.getColumnIndex(COL_WORD);
        int first_index = cursor.getColumnIndex(COL_WORD_LAST_DATE);
        int level_index = cursor.getColumnIndex(COL_WORD_SPAN);

        while (!cursor.isAfterLast()) {
            String wd = cursor.getString(word_index);
            long date = cursor.getLong(first_index);
            int level = cursor.getInt(level_index);

            Util.log("zee:\t" + wd + "\t" + Util.getFormatDate(date) + "\t" + level);
            cursor.moveToNext();
        }

    }

    // 2014-06-06
    public Cursor getWordList(int level) {
        return db.query(DATABASE_TABLE_WORD, new String[] {
            COL_WORD
        }, COL_WORD_SPAN + "=" + level, null, null, null, null);
    }

    public boolean removeWord(String word) {
        boolean b;
        Util.log("The word:" + word + "is to be deleted from database!");
        b = deleteWord(word);
        if (b) {
            log("Delete " + word + "from main table success!");
        } else {
            log("Delete " + word + "from main table failed!");
        }
        b = deleteHistory(word);
        if (b) {
            log("Delete " + word + "from history table success!");
        } else {
            log("Delete " + word + "from history table failed!");
        }
        b = deleteConfused(word);
        if (b) {
            log("Delete " + word + "from confused table success!");
        } else {
            log("Delete " + word + "from confused table failed!");
        }
        return true;
    }

    public boolean isFinished(String word) {
        Cursor cursor = db.query(DATABASE_TABLE_DELETED, null, COL_WORD + "= \'" + word + "\'",
                null, null, null, null);
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public int getFinishedCount() {
        Cursor csr = db.query(DATABASE_TABLE_DELETED, new String[] {
            COL_WORD
        }, COL_REASON + "=" + WordLibAdapter.DEL_REASON_COMPLETE, null, null, null, null);
//        csr.moveToFirst();
//        while (!csr.isAfterLast()) {
//            Util.log("Total:" + csr.getString(0));
//            csr.moveToNext();
//        }
        return csr.getCount();
    }

    public int getTotalCount(){
        String cmd = "SELECT";

        cmd += " COUNT(*) AS word_count";
        cmd += " FROM " + DATABASE_TABLE_WORD;

        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);
        csr.moveToLast();
        int count = csr.getInt(0);
        return count;
    }

    public int getConfusedWordCount(){
        String cmd = "SELECT";

        cmd += " COUNT(*) AS word_count";
        cmd += " FROM " + DATABASE_TABLE_CONFUSED;

        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);
        csr.moveToLast();
        int count = csr.getInt(0);
        return count;
    }


    public int getFailedCount(){
        String cmd = "SELECT";

        cmd += " COUNT(*) AS word_count";
        cmd += " FROM " + DATABASE_TABLE_SPELL;

        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);
        csr.moveToLast();
        int count = csr.getInt(0);
        return count;
    }

    // 2014-06-27
    public int getSpellFinishedCount() {
        String cmd = "SELECT";

        cmd += " COUNT(*) AS spell_count," + COL_SPELL_VALUE;
        cmd += " FROM " + DATABASE_TABLE_CONFUSED;
        cmd += " GROUP BY " + COL_SPELL_VALUE;
        cmd += " ORDER BY " + COL_SPELL_VALUE;
        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);
        if(csr.getCount()==0)
            return 0;
        csr.moveToLast();
        int count = csr.getInt(0);
        /*
         * JUST FOR TEST boolean b = csr.moveToFirst(); if (!b) { return 0 ; }
         *
         * while (!csr.isAfterLast() ) { Util.log("Count is "+
         * csr.getInt(0)+" FOR " + csr.getInt(1)); csr.moveToNext(); }
         */
        return count;
    }

    public void backupDatabaseFile() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//{package name}//databases//{database name}";
                String backupDBPath = "{database name}";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }

    public Cursor getSpellList() {
        String cmd = "SELECT ";
        cmd += DATABASE_TABLE_WORD + "." + COL_WORD + "," + DATABASE_TABLE_WORD + "."
                + COL_INTERPRETION;
        cmd += " FROM " + DATABASE_TABLE_WORD;
        cmd += " INNER JOIN " + DATABASE_TABLE_CONFUSED;
        cmd += " ON " + DATABASE_TABLE_CONFUSED + "." + COL_WORD + "=" + DATABASE_TABLE_WORD + "."
                + COL_WORD + " ORDER BY " + DATABASE_TABLE_CONFUSED + "." + COL_SPELL_VALUE
                + " LIMIT 0,20";

        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);
        return csr;
    }

    // ////////////
    public Cursor getRecentFailedWordListForSpell(int count) {
        String cmd = "SELECT ";
        cmd += DATABASE_TABLE_WORD + "." + COL_WORD + "," + DATABASE_TABLE_WORD + "."
                + COL_INTERPRETION + ",MAX(" + DATABASE_TABLE_HISTORY + "." + COL_ACTION_DATE
                + ") AS time";
        cmd += " FROM " + DATABASE_TABLE_WORD;
        cmd += " INNER JOIN " + DATABASE_TABLE_HISTORY;
        cmd += " ON " + DATABASE_TABLE_HISTORY + "." + COL_WORD + "=" + DATABASE_TABLE_WORD + "."
                + COL_WORD;
        cmd += " WHERE " + DATABASE_TABLE_HISTORY + "." + COL_CHANGE + "<= 0";
        cmd += " GROUP BY " + DATABASE_TABLE_HISTORY + "." + COL_WORD;
        cmd += " ORDER BY time DESC LIMIT 0,30;";

        Util.log("SEE BELOW");
        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);

        if (csr.getCount() > 0) {
            return csr;
        }
        return null;
    }

    // /////////////

    // 2014-06-17
    public void saveSpellResult(String wd, int score) {
        String strWhere = COL_WORD + "= \'" + wd + "\'";
        Cursor cursor = db.query(DATABASE_TABLE_CONFUSED, null, strWhere, null, null, null, null);
        int scoreIndex = cursor.getColumnIndex(COL_SPELL_VALUE);
        Util.log(COL_SPELL_VALUE + " index is " + scoreIndex);
        if (scoreIndex == -1) {
            return;
        }
        boolean b = cursor.moveToFirst();
        if (!b) {
            int count = cursor.getCount();
            Util.log("fail to move to first!" + count);
            return;
        }
        int value = cursor.getInt(scoreIndex);
        value += score;
        ContentValues values = new ContentValues();

        values.put(COL_SPELL_VALUE, value);
        db.update(DATABASE_TABLE_CONFUSED, values, strWhere, null);
    }

    // 2014-06-27
    public void findDateError() {
        String cmd = "SELECT ";
        cmd += "MIN(" + DATABASE_TABLE_HISTORY + "." + COL_ACTION_DATE + "),";
        cmd += DATABASE_TABLE_WORD + "." + COL_WORD + "," + DATABASE_TABLE_WORD + "."
                + COL_WORD_LAST_DATE;
        cmd += " FROM " + DATABASE_TABLE_HISTORY;
        cmd += " INNER JOIN " + DATABASE_TABLE_WORD;
        cmd += " WHERE " + DATABASE_TABLE_HISTORY + "." + COL_WORD + "=" + DATABASE_TABLE_WORD
                + "." + COL_WORD;
        cmd += " GROUP BY " + DATABASE_TABLE_HISTORY + "." + COL_WORD;
        cmd += ";";

        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);
        boolean b = csr.moveToFirst();
        if (!b) {
            return;
        }
        Util.log("Count is " + csr.getCount());

        csr.moveToFirst();
        int n = 0;
        while (!csr.isAfterLast()) {
            long date0 = csr.getLong(0);
            long date2 = csr.getLong(2);
            if (date0 != date2) {
                n++;
                Util.log("Error:" + csr.getString(1) + "\t" + Util.getFormatDateTime(date0) + "\t^"
                        + Util.getFormatDateTime(date2));
                updateFirstInfo(csr.getString(1));
            }

            csr.moveToNext();
        }
        Util.log("There are error " + n);

    }

    // 2014-07-07
    public void findBlankWord() {
        Cursor csr = db.query(DATABASE_TABLE_WORD, null, null, null, null, null, null);
        boolean b = csr.moveToFirst();
        if (!b) {
            return;
        }
        Util.log("Count is " + csr.getCount());
        int col = csr.getColumnIndex(COL_WORD);
        int col_span = csr.getColumnIndex(COL_WORD_SPAN);

        csr.moveToFirst();
        int n = 0;
        while (!csr.isAfterLast()) {
            String wd = csr.getString(col);
            int level = csr.getInt(col_span);
            if (wd.endsWith(" ")) {

                n++;
                Util.log("Error:" + wd + " " + wd.replace(" ", "*") + " Level:" + level);
                String rightWd = wd.substring(0, wd.length() - 1);
                Cursor cs = getWord(rightWd);
                if (cs.getCount() == 1) {
                    Log.e("WINDWORD", "Error exist:" + rightWd);
                }
            }

            csr.moveToNext();
        }
        Util.log("There are error " + n);
    }

    // 2014-07-07
    // if the word has already exist. delete it directly.
    // otherwise just delete the blank.
    public void correctBlankEndWord() {
        Cursor csr = db.query(DATABASE_TABLE_WORD, null, null, null, null, null, null);
        boolean b = csr.moveToFirst();
        if (!b) {
            return;
        }
        Util.log("Count is " + csr.getCount());
        int col = csr.getColumnIndex(COL_WORD);
        int col_span = csr.getColumnIndex(COL_WORD_SPAN);

        csr.moveToFirst();
        int n = 0;
        while (!csr.isAfterLast()) {
            String wd = csr.getString(col);
            String rightWd = wd.substring(0, wd.length() - 1);
            int level = csr.getInt(col_span);
            if (wd.endsWith(" ")) {
                Cursor cs = getWord(rightWd);
                if (cs.getCount() == 1) {
                    Log.e("WINDWORD", "Error exist:" + rightWd);
                    removeWord(wd, WordLibAdapter.DEL_REASON_REPEAT);
                } else {
                    String strSQL = "UPDATE " + DATABASE_TABLE_CONFUSED + " SET " + COL_WORD + "="
                            + "'" + rightWd + "'" + " WHERE " + COL_WORD + "=" + "'" + wd + "'";

                    db.execSQL(strSQL);
                    strSQL = "UPDATE " + DATABASE_TABLE_WORD + " SET " + COL_WORD + "=" + "'"
                            + rightWd + "'" + " WHERE " + COL_WORD + "=" + "'" + wd + "'";

                    db.execSQL(strSQL);
                    strSQL = "UPDATE " + DATABASE_TABLE_HISTORY + " SET " + COL_WORD + "=" + "'"
                            + rightWd + "'" + " WHERE " + COL_WORD + "=" + "'" + wd + "'";

                    db.execSQL(strSQL);
                }

                n++;
                Util.log("Error:" + wd + " " + wd.replace(" ", "*") + " Level:" + level);
            }

            csr.moveToNext();
        }
        Util.log("There are error " + n);
    }

    public int getSpellValue(String word) {
        String strWhere = COL_WORD + "= \'" + word + "\'";
        Cursor cursor = db.query(DATABASE_TABLE_SPELL, null, strWhere, null, null, null, null);
        if (0 == cursor.getCount()) {
            return -1;
        }
        int valueIndex = cursor.getColumnIndex(COL_SPELL_TIMES);
        cursor.moveToFirst();
        return cursor.getInt(valueIndex);
    }

    /*2015-08-28 return value: if the word be deleted in the spell library.*/

    public boolean addSpellWord(String word, int value) {
        if (value == 0) {
            return false;
        }
        int oldValue = getSpellValue(word);
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_WORD, word);
        if (oldValue == -1) {
            initialValues.put(COL_SPELL_TIMES, value);
            db.insert(DATABASE_TABLE_SPELL, null, initialValues);

        } else {
            value += oldValue;
            String strWhere = COL_WORD + "= \'" + word + "\'";

            if (0 == value) {
                db.delete(DATABASE_TABLE_SPELL, strWhere, null);
                return true;
            } else {
                initialValues.put(COL_SPELL_TIMES, value);
                initialValues.put(COL_SPELL_DAY, Util.getToday());
                db.update(DATABASE_TABLE_SPELL, initialValues, strWhere, null);

            }
        }
        return false;
    }

    public void testSpellTable() {
        int v = getSpellValue("test");
        addSpellWord("test", -v);
        v = getSpellValue("test");
        if (v != -1) {
            Util.log("Test Fail in case 0 : The Value is " + v);
        } else {
            Util.log("Test Success in case 0 : The Value is " + v);
        }
        addSpellWord("test", 0);
        v = getSpellValue("test");
        if (v != -1) {
            Util.log("Test Fail in case 1 : The Value is " + v);
        } else {
            Util.log("Test Success in case 1 : The Value is " + v);
        }

        addSpellWord("test", 1);
        v = getSpellValue("test");
        if (v != 1) {
            Util.log("Test Fail in case 2 : The Value is " + v);
        } else {
            Util.log("Test Success in case 2 : The Value is " + v);
        }

        addSpellWord("test", 2);
        v = getSpellValue("test");
        if (v != 3) {
            Util.log("Test Fail in case 3 : The Value is " + v);
        } else {
            Util.log("Test Success in case 3 : The Value is " + v);
        }
        addSpellWord("test", -3);
        v = getSpellValue("test");
        if (v != -1) {
            Util.log("Test Fail in case 4 : The Value is " + v);
        } else {
            Util.log("Test Success in case 4 : The Value is " + v);
        }
    }

    // 2014-10-08
    public Cursor getRecentFailedWordListForSpell_V2() {

        //2015-4-4
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DATE);
        //end 2015-4-4

        String cmd = "SELECT ";
        cmd += DATABASE_TABLE_WORD + "." + COL_WORD + "," + DATABASE_TABLE_WORD + "."
                + COL_INTERPRETION + ",MAX(" + DATABASE_TABLE_SPELL + "." + COL_SPELL_TIMES
                + ") AS times";
        cmd += " FROM " + DATABASE_TABLE_WORD;
        cmd += " INNER JOIN " + DATABASE_TABLE_SPELL;
        cmd += " ON " + DATABASE_TABLE_SPELL + "." + COL_WORD + "=" + DATABASE_TABLE_WORD + "."
                + COL_WORD;
        //2015-4-4
        cmd += " AND " + COL_SPELL_DAY + " IS NOT " + today;
        //end 2015-4-4
        cmd += " GROUP BY " + DATABASE_TABLE_SPELL + "." + COL_WORD;
        cmd += " ORDER BY times DESC LIMIT 0,200;";

        Util.log("SEE BELOW");
        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);

        if (csr.getCount() > 0) {
            return csr;
        }
        return null;
    }

    public int getMaxLevel(){
        String sqlExec = "SELECT MAX("+COL_WORD_SPAN+") FROM "+ DATABASE_TABLE_WORD;
        Cursor csr = db.rawQuery(sqlExec, null);
        csr.moveToFirst();
        return csr.getInt(0);
    }

    public int getNextConfusedRefId(){
        String cmd = "SELECT ";
        cmd += "MAX(" + COL_REFERENCE + ")";
        cmd += " FROM " + DATABASE_TABLE_CONFUSED;

        Util.log("SEE BELOW");
        Util.log(cmd);
        Cursor csr = db.rawQuery(cmd, null);
        if(csr!=null){
            csr.moveToFirst();
            return csr.getInt(0) + 1;
        }
        return 1;
    }
}
