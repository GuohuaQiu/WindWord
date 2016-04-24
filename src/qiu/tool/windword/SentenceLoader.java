package qiu.tool.windword;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SentenceLoader {
    public int buffer_size = 256;
    public String TAG = "SentenceLoader";
    WordLibAdapter mWordLib = null;

    public SentenceLoader(WordLibAdapter wordLib) {
        mWordLib = wordLib;
    }


    public boolean addSentence(String sentence){
        int offset = 0;
        boolean ok = true;
        while (ok) {
            int end = sentence.indexOf(0x20, offset);
            if (end == -1) {
                ok = false;
                end = sentence.length();
            }
            String strword = sentence.substring(offset, end);



            offset = end + 1;
            Log.i(TAG, "word="+strword);
        }


        return false;
    }

    public boolean loadFromFile(String filepath) {
        // 打开文件
        try {
            BufferedReader in = new BufferedReader(new FileReader(filepath));
            while(true){
             String str = in.readLine();
             if(str == null){
                 break;
             }
             mWordLib.insertSentence(str);
                  Log.i(TAG,str);
           }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
