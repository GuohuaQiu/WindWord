package qiu.tool.windword;

import android.database.Cursor;
import android.util.Log;


public class Word {
    private String word = null;
    private String phonetic = null;
    private String interpretion = null;
    private String interpertion_eng = null;
    private String mSample = null;
    private String mColins = null;
    private String mBiling = null;
//    private int mScore = 0;
    public int todayScore = 0;
    public int mLevel = 0;

    private boolean mIsOK = false;

    public Word(Cursor cursor){
        setWord(cursor.getString(cursor.getColumnIndex(WordLibAdapter.COL_WORD)));
        setInterpretion(cursor.getString(cursor.getColumnIndex(WordLibAdapter.COL_INTERPRETION)));
        if (cursor.getColumnIndex(WordLibAdapter.COL_PHONETIC) != -1) {
            setPhonetic(cursor.getString(cursor.getColumnIndex(WordLibAdapter.COL_PHONETIC)));
            setInterpretionEng(cursor
                    .getString(cursor.getColumnIndex(WordLibAdapter.COL_INTER_ENG)));
            setColins(cursor.getString(cursor.getColumnIndex(WordLibAdapter.COL_COLINS_DICT)));
            setBiling(cursor.getString(cursor.getColumnIndex(WordLibAdapter.COL_BILING_SAMPLE)));
            mLevel = cursor.getInt(cursor.getColumnIndex(WordLibAdapter.COL_WORD_SPAN));
        }
    }


    //load sample from database. 2011-12-12
    public void loadSample(WordLibAdapter db){
         mSample = db.getSample(word);
    }

    public Word(){
//        mScore = 10;
    }


    public String getWord() {
        return word;
    }

//    public int getScore(){
//        return mScore;
//    }
//    public void setScore(int score){
//        mScore = score;
//    }
    public String getInterpretion() {
        return interpretion;
    }

    public String getInterpretionEng() {
        return interpertion_eng;
    }

    public String getSample() {
        return mSample;
    }

    public boolean isOK(){
        return mIsOK;
    }

    public void setOK(boolean ok){
        mIsOK = ok;
    }

    public void setWord(String extractedString) {
        if(word != null)        {
            word += extractedString;
        } else {
            word = extractedString;
        }
    }

    public void setInterpretionEng(String extractedString) {
        if(extractedString == null || extractedString.equals("null")){
            return;
        }
        interpertion_eng = extractedString;

    }


    public void setInterpretion(String extractedString) {
        if(interpretion != null)        {
            interpretion += extractedString;
        } else {
            interpretion = extractedString;
        }
    }

    public void setSample(String sample){
        mSample = sample;
    }

    public void appendSample(String sample){
        if(mSample == null){
            mSample = sample;
        }else{
            mSample += sample;
        }
    }

    public String getPhonetic() {
        return phonetic;
    }
    public void setPhonetic(String extractedString) {
        if(phonetic != null)        {
            phonetic += extractedString;
        } else {
            phonetic = extractedString;
        }
    }

    public String toString(){
        return "Word = " + this.word
                + "phonetic = [" + this.phonetic + "]";
    }
    public void clear(){
        word = null;
        phonetic = null;
        interpretion = null;
    }
    public String getColins(){
        return mColins;
    }
    public String getBiling(){
        return mBiling;
    }
    public void setColins(String colins){
        mColins = colins;
    }
    public void setBiling(String biling){
        mBiling = biling;
    }
}