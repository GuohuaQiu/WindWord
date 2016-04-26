package qiu.tool.windword;



import qiu.tool.windword.WordPage.DownContent;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Random;

public class WordList extends ArrayList<Word>{

    /**
     *
     */
    private static final long serialVersionUID = 2985357266053705329L;


    public WordList(Cursor cursor) {
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                while(!cursor.isAfterLast()) {
                    Word wd = new Word(cursor);
//                    wd.setSample(lib.getSample(wd.getWord()));
                    add(wd);
                    cursor.moveToNext();
                }
            }
        }
    }
    public WordList(Cursor cursor, int count) {

        int total = cursor.getCount();

        boolean space[] = new boolean[total];
        Random rand = new Random();
//        int index = 30;
        for (int i = 0; i < count; i++) {
            int randInt;
            do {
                randInt = rand.nextInt(total);
                Util.log("Rand:" + randInt);
            } while (space[randInt]);
            Util.log("----------------------------------------------------");

            space[randInt] = true;
        }
        int ccc = 0;
        for (int j = 0; j < total; j++) {
            if (space[j]) {

                Util.log("Rand(" + ccc + ") in:" + j);
                ccc++;
            }
        }

        for (int i = 0; i < total; i++) {
            if(space[i]){
                cursor.moveToPosition(i);
                Word wd = new Word(cursor);
                add(wd);
            }
        }
    }

    public int GetNotOkCount(){
        int count = 0;

        for(Word wd : this)
        {
            if(!wd.isOK()){
                count++;
            }
        }
        return count;
    }

    /*This function is used in REFRESH mode.*/
    public Word GetNextNotOKWord(int start){
        int size = size();
        for(int i = start;i<size;i++)
        {
            Word wd = get(i);
            if(wd!=null)
            {
                if(!wd.isOK()){
                    return wd;
                }
            }
        }
        for(int i = 0; i< start;i++)
        {
            Word wd = get(i);
            if(wd!=null)
            {
                if(!wd.isOK()){
                    return wd;
                }
            }
        }
        return null;
    }
    /*This function is used in REFRESH mode.*/
    public int GetNextNotOKWordIndex(int start){
        int size = size();
        for(int i = start;i<size;i++)
        {
            Word wd = get(i);
            if(wd!=null && !wd.isOK())
            {
                return i;
            }
        }
        for(int i = 0; i< start;i++)
        {
            Word wd = get(i);
            if(wd!=null && !wd.isOK())
            {
                return i;
            }
        }
        return -1;
    }
    /*This function is used in LEARN mode.*/
    public Word getNextWord(int start){
        int size = size();
        if(size == 0){
            return null;
        }
        if(start>=(size - 1) || start < -1){
            return null;
        }

        return get(start + 1);
    }
    /*This function is used in LEARN mode.*/
    public Word getPrevWord(int start){
        int size = size();
        if(size == 0){
            return null;
        }
        if(start<=0 || start >= size){
            return null;
        }

        return get(start - 1);
    }

    public void updateContent(DownContent content,int type){
        for(Word wd : this)
        {
            if(wd.getWord().equals(content.mWord)){
                if(type == 0){
                    wd.setInterpretionEng(content.mDestripionEng);
                }else if(type == 1){
                    wd.setColins(content.mDestripionEng);
                }else if(type == 2){
                    wd.setBiling(content.mDestripionEng);
                }
                return;
            }
        }
    }
}
