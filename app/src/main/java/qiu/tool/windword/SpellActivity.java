
package qiu.tool.windword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class SpellActivity extends Activity implements OnClickListener {

    private Button btnInput_a = null;

    private Button btnInput_b = null;

    private Button btnInput_c = null;

    private Button btnInput_d = null;

    private Button btnInput_e = null;

    private Button btnInput_f = null;

    private Button btnInput_g = null;

    private Button btnInput_h = null;

    private Button btnInput_i = null;

    private Button btnInput_j = null;

    private Button btnInput_k = null;

    private Button btnInput_l = null;

    private Button btnInput_m = null;

    private Button btnInput_n = null;

    private Button btnInput_o = null;

    private Button btnInput_p = null;

    private Button btnInput_q = null;

    private Button btnInput_r = null;

    private Button btnInput_s = null;

    private Button btnInput_t = null;

    private Button btnInput_u = null;

    private Button btnInput_v = null;

    private Button btnInput_w = null;

    private Button btnInput_x = null;

    private Button btnInput_y = null;

    private Button btnInput_z = null;

    private Button btnInput_space = null;

    private Button btnInput_sub = null;

    private Button btnInput_del = null;

    private Button btnSubmit = null;

    private Button btnNext = null;

    private Button btnVoice = null;

    private TextView text_word = null;

    private TextView text_description = null;

    private TextView text_actual_word = null;

    private String actualWord = "";

    private String displayWord = "";

    private WordList mWordList = null;

    private WordLibAdapter dbAdapter = null;

    private int mCurrentIndex = 0;

    private int state;

    private static final int STATE_TESTING = 0;

    private static final int STATE_REVIEW = 1;

    private String currentWord;

    private Typeface mSpellFont = null;

    private View panelView;

    private LampView mLampView = null;

    public static int forConfused = 1;

    private int round = 0;

    private View viewResult = null;
    private View viewWork = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spell);
        Intent intent = getIntent();
        if (intent == null) {
            Log.i("FAILED INTENT", "HAHA");
            return;
        }
        text_word = (TextView)findViewById(R.id.text_word);
        text_actual_word = (TextView)findViewById(R.id.text_actual_word);
        text_description = (TextView)findViewById(R.id.text_description);

        mSpellFont = Typeface.createFromAsset(getAssets(), "Cousine-Bold.ttf");
        text_word.setTypeface(mSpellFont);
        text_actual_word.setTypeface(mSpellFont);
        btnSubmit = (Button)findViewById(R.id.btn_submit);
        btnNext = (Button)findViewById(R.id.btn_next);
        btnVoice = (Button)findViewById(R.id.btn_voice);
        btnInput_del = (Button)findViewById(R.id.btn_del);

        panelView = findViewById(R.id.panel);

        initInput();
        init();
        loadWord();
        mLampView.setCount(mWordList.size());
        // testFilter();

        viewResult = findViewById(R.id.frame_complete_list);
        viewWork = findViewById(R.id.frame_spell_view);



    }

    private void loadWord() {
        if (dbAdapter == null) {
            dbAdapter = WordLibAdapter.getInstance(this);
        }
        if (forConfused == 1) {
            Cursor cursor = dbAdapter.getSpellList();
            mWordList = new WordList(cursor);
        } else {
            Cursor cursor = dbAdapter.getRecentFailedWordListForSpell_V2();
            if (cursor != null) {
                Util.log("Total:" + cursor.getCount());
            }
            int count = cursor.getCount();
            if (count > 30) {
                mWordList = new WordList(cursor, 30);
            } else {
                mWordList = new WordList(cursor);
            }
        }
        mCurrentIndex = 0;
        round = 0;
        showWord();
    }

    private void initInput() {
        btnInput_a = (Button)panelView.findViewById(R.id.char_a);
        btnInput_b = (Button)panelView.findViewById(R.id.char_b);
        btnInput_c = (Button)panelView.findViewById(R.id.char_c);
        btnInput_d = (Button)panelView.findViewById(R.id.char_d);
        btnInput_e = (Button)panelView.findViewById(R.id.char_e);
        btnInput_f = (Button)panelView.findViewById(R.id.char_f);
        btnInput_g = (Button)panelView.findViewById(R.id.char_g);
        btnInput_h = (Button)panelView.findViewById(R.id.char_h);
        btnInput_i = (Button)panelView.findViewById(R.id.char_i);
        btnInput_j = (Button)panelView.findViewById(R.id.char_j);
        btnInput_k = (Button)panelView.findViewById(R.id.char_k);
        btnInput_l = (Button)panelView.findViewById(R.id.char_l);
        btnInput_m = (Button)panelView.findViewById(R.id.char_m);
        btnInput_n = (Button)panelView.findViewById(R.id.char_n);
        btnInput_o = (Button)panelView.findViewById(R.id.char_o);
        btnInput_p = (Button)panelView.findViewById(R.id.char_p);
        btnInput_q = (Button)panelView.findViewById(R.id.char_q);
        btnInput_r = (Button)panelView.findViewById(R.id.char_r);
        btnInput_s = (Button)panelView.findViewById(R.id.char_s);
        btnInput_t = (Button)panelView.findViewById(R.id.char_t);
        btnInput_u = (Button)panelView.findViewById(R.id.char_u);
        btnInput_v = (Button)panelView.findViewById(R.id.char_v);
        btnInput_w = (Button)panelView.findViewById(R.id.char_w);
        btnInput_x = (Button)panelView.findViewById(R.id.char_x);
        btnInput_y = (Button)panelView.findViewById(R.id.char_y);
        btnInput_z = (Button)panelView.findViewById(R.id.char_z);
        btnInput_space = (Button)panelView.findViewById(R.id.char_space);
        btnInput_sub = (Button)panelView.findViewById(R.id.char_sub);
        btnInput_a.setOnClickListener((OnClickListener)this);
        btnInput_b.setOnClickListener((OnClickListener)this);
        btnInput_c.setOnClickListener((OnClickListener)this);
        btnInput_d.setOnClickListener((OnClickListener)this);
        btnInput_e.setOnClickListener((OnClickListener)this);
        btnInput_f.setOnClickListener((OnClickListener)this);
        btnInput_g.setOnClickListener((OnClickListener)this);
        btnInput_h.setOnClickListener((OnClickListener)this);
        btnInput_i.setOnClickListener((OnClickListener)this);
        btnInput_j.setOnClickListener((OnClickListener)this);
        btnInput_k.setOnClickListener((OnClickListener)this);
        btnInput_l.setOnClickListener((OnClickListener)this);
        btnInput_m.setOnClickListener((OnClickListener)this);
        btnInput_n.setOnClickListener((OnClickListener)this);
        btnInput_o.setOnClickListener((OnClickListener)this);
        btnInput_p.setOnClickListener((OnClickListener)this);
        btnInput_q.setOnClickListener((OnClickListener)this);
        btnInput_r.setOnClickListener((OnClickListener)this);
        btnInput_s.setOnClickListener((OnClickListener)this);
        btnInput_t.setOnClickListener((OnClickListener)this);
        btnInput_u.setOnClickListener((OnClickListener)this);
        btnInput_v.setOnClickListener((OnClickListener)this);
        btnInput_w.setOnClickListener((OnClickListener)this);
        btnInput_x.setOnClickListener((OnClickListener)this);
        btnInput_y.setOnClickListener((OnClickListener)this);
        btnInput_z.setOnClickListener((OnClickListener)this);
        btnInput_space.setOnClickListener((OnClickListener)this);
        btnInput_sub.setOnClickListener((OnClickListener)this);
        btnInput_del.setOnClickListener((OnClickListener)this);
        btnSubmit.setOnClickListener((OnClickListener)this);
        btnNext.setOnClickListener((OnClickListener)this);
        btnVoice.setOnClickListener((OnClickListener)this);
    }

    public void showWord() {
        state = STATE_TESTING;
        currentWord = mWordList.get(mCurrentIndex).getWord();
        text_word.setText("");
        text_word.setTextColor(0xff1166ff);
        text_actual_word.setVisibility(View.GONE);
        text_description.setText(filterDescription());
        displayWord = "";
        btnSubmit.setEnabled(true);
        btnNext.setEnabled(false);
        panelView.setVisibility(View.VISIBLE);

    }

    public String filterDescription() {
        String description = mWordList.get(mCurrentIndex).getInterpretion();
        return filterWord(description, currentWord);
    }

    public static String filterWord(String target, String sub) {
        String end = target.toLowerCase();
        end = end.replace(sub, "");
        return end;
    }

    public void testFilter() {
        String a = "qiu Wangke";
        String b = filterWord(a, "wang");

        Util.log(a + " become " + b + ".");
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.char_a:
                append('a');
                break;
            case R.id.char_b:
                append('b');
                break;
            case R.id.char_c:
                append('c');
                break;
            case R.id.char_d:
                append('d');
                break;
            case R.id.char_e:
                append('e');
                break;
            case R.id.char_f:
                append('f');
                break;
            case R.id.char_g:
                append('g');
                break;
            case R.id.char_h:
                append('h');
                break;
            case R.id.char_i:
                append('i');
                break;
            case R.id.char_j:
                append('j');
                break;
            case R.id.char_k:
                append('k');
                break;
            case R.id.char_l:
                append('l');
                break;
            case R.id.char_m:
                append('m');
                break;
            case R.id.char_n:
                append('n');
                break;
            case R.id.char_o:
                append('o');
                break;
            case R.id.char_p:
                append('p');
                break;
            case R.id.char_q:
                append('q');
                break;
            case R.id.char_r:
                append('r');
                break;
            case R.id.char_s:
                append('s');
                break;
            case R.id.char_t:
                append('t');
                break;
            case R.id.char_u:
                append('u');
                break;
            case R.id.char_v:
                append('v');
                break;
            case R.id.char_w:
                append('w');
                break;
            case R.id.char_x:
                append('x');
                break;
            case R.id.char_y:
                append('y');
                break;
            case R.id.char_z:
                append('z');
                break;
            case R.id.char_space:
                append(' ');
                break;
            case R.id.char_sub:
                append('-');
                break;
            case R.id.btn_del:
                del_char();
                break;
            case R.id.btn_submit:
                submit();
                break;
            case R.id.btn_next:
                nextWord();
                break;
            case R.id.btn_voice:
                Util.playVoice(this, currentWord, null);
                break;
        }

    }

    // 2014-06-11
    private void append(char a) {
        if (state == STATE_TESTING) {
            displayWord += a;
            text_word.setText(displayWord);
            adjustTextSize();
        }
    }

    // 2014-06-19
    private void del_char() {
        if (state == STATE_TESTING) {
            if (displayWord.length() > 0) {
                displayWord = displayWord.substring(0, displayWord.length() - 1);
                text_word.setText(displayWord);
                adjustTextSize();
            }
        }
    }

    private void nextWord() {
        int index = mWordList.GetNextNotOKWordIndex(mCurrentIndex+1);
        if(index == -1){
            return;
        }
        if(index <= mCurrentIndex)
        {
            round++;
        }
        mCurrentIndex = index;

        mLampView.setOngoing(mCurrentIndex);
        showWord();
    }

    private ArrayList<String> completeList = new ArrayList<String>();

    private void done(int score) {
        Word wd = mWordList.get(mCurrentIndex);
        wd.todayScore += score;
        if (forConfused == 1) {
            if (round == 0) {
                if (score > 0) {
                    dbAdapter.saveSpellResult(currentWord, 1);
                } else {
                    dbAdapter.saveSpellResult(currentWord, -1);
                }
            }
        } else {
            if (round == 0) {
                if (score > 0) {
                    boolean b = dbAdapter.addSpellWord(wd.getWord(), -1);
                    if(b){
                        completeList.add(wd.getWord());
                    }
                }
            } else if (round % 2 == 1) {
                dbAdapter.addSpellWord(wd.getWord(), 1);
            }
        }
        if (wd.todayScore > 0) {
            wd.setOK(true);
            mLampView.setPass(mCurrentIndex, true);
            if (mWordList.GetNotOkCount()==0) {
                btnSubmit.setEnabled(false);
                btnNext.setEnabled(false);
                btnNext.setText("End");

                viewResult.setVisibility(View.VISIBLE);
                viewWork.setVisibility(View.INVISIBLE);
                String a = "";
                for (String str:completeList) {
                    a += "\n";
                    a += str;
                }
                ((TextView)viewResult).setText(a);
                //simon
            }
        }
    }

    private void submit() {
        state = STATE_REVIEW;

        if (displayWord.equalsIgnoreCase(currentWord)) {
            text_word.setTextColor(0xff00ff00);
            done(1);
        } else {
            text_actual_word.setVisibility(View.VISIBLE);
            text_actual_word.setText(currentWord);
            String strWord = getFormatString();
            text_word.setText(Html.fromHtml(strWord));
            // text_word.setTextColor(0xffff0000);
            done(-1);
        }
        adjustTextSize();

        btnSubmit.setEnabled(false);
        btnNext.setEnabled(true);
        panelView.setVisibility(View.GONE);

    }

    private String getFormatString() {

        final int real_len = currentWord.length();
        final int input_len = displayWord.length();

        if (input_len < 1) {
            return "";
        }
        String wd = currentWord.toLowerCase();

        String fmtString = "";

        final String wrongColor = "ff1111";
        final String okColor = "11ff44";

        int start = 0;
        boolean same = (displayWord.charAt(0) == wd.charAt(0));

        Util.log("first result:" + same + "real_len = " + real_len + " input " + displayWord
                + " actual " + wd);

        for (int i = 1; i < input_len; i++) {

            if (i >= real_len) {
                Util.log("i is " + i + " inner end result:" + same + " start " + start);
                if (same) {
                    fmtString += appendColorText(okColor, start, i);
                    fmtString += appendColorText(wrongColor, i, input_len);
                } else {
                    fmtString += appendColorText(wrongColor, start, input_len);
                }
                start = input_len;
                break;
            }
            if (displayWord.charAt(i) == wd.charAt(i)) {
                if (!same) {
                    Util.log("change in " + i + " become same");
                    fmtString += appendColorText(wrongColor, start, i);
                    start = i;
                    same = true;
                }
            } else {
                if (same) {
                    Util.log("change in " + i + " become different");
                    fmtString += appendColorText(okColor, start, i);
                    start = i;
                    same = false;
                }
            }
        }
        if (start < input_len) {
            Util.log("end result:" + same + " start " + start);
            fmtString += appendColorText(same ? okColor : wrongColor, start, input_len);
        }
        if (input_len < real_len) {
            fmtString += appendColorText(wrongColor, input_len, real_len);
        }
        return fmtString;

        // StringtextStr1 = "<font color=\"#ffff00\">如果有一天，</font><br>";

    }

    private String appendColorText(String color, int start, int end) {
        if (end < start) {
            return "";
        }

        String strFormat = "<font color=\"#";
        strFormat += color;
        strFormat += "\">";
        if (end > displayWord.length()) {
            for (int i = start; i < end; i++) {
                strFormat += "_";
            }
        } else {
            strFormat += displayWord.substring(start, end);
        }
        strFormat += "</font>";

        Util.log(strFormat);
        return strFormat;
    }

    private void adjustTextSize() {
        int len = displayWord.length();
        //Util.log("len a is " + len);
        float size;
        if (state == STATE_REVIEW && len < currentWord.length()) {
            size = getSize(currentWord);
            // len = currentWord.length();
            //Util.log("len b is " + len);
        } else {
            size = getSize(displayWord);
        }
        // size = getSize(len);
        text_word.setTextSize(size);
//        Util.log("set size a is " + size);
        if (state == STATE_REVIEW) {
//            Util.log("set size b is " + size);
            text_actual_word.setTextSize(size);
        }
    }

    private int maxWidth = 360;

    private void init() {
        mLampView = (LampView)findViewById(R.id.light_view);
        mLampView.reset();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        Util.log("left is " + text_word.getLeft() + " right is " + text_word.getRight());
        Util.log("density is " + metric.density + " sd is " + metric.scaledDensity);
        maxWidth = (int)(metric.widthPixels / metric.scaledDensity);
        maxWidth -= text_word.getLeft();
        maxWidth -= text_word.getPaddingRight();
        maxWidth -= 40;
        Util.log(metric.toString());

        Util.log(" density is " + metric.density + " total sp is " + maxWidth);

    }

    private float getSize(String disString) {
        float trySize = 50;
        Paint testPaint = new Paint();
        testPaint.setTypeface(mSpellFont);

        testPaint.setTextSize(trySize);
        // TextView a;

        while ((trySize > 5) && (testPaint.measureText(disString) > maxWidth)) {

            trySize -= 1;
            if (trySize <= 5) {
                trySize = 5;
                break;
            }
            testPaint.setTextSize(trySize);
        }
        return trySize;
    }

    /*
     * 2014-06-20 Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void refitText() {
        int textWidth = 350;
        if (textWidth > 0) {
            int availableWidth = textWidth - text_word.getPaddingLeft()
                    - text_word.getPaddingRight();
            float trySize = 50;

            Paint testPaint = new Paint();
            testPaint.set(text_word.getPaint());

            testPaint.setTextSize(trySize);
            while ((trySize > 5) && (testPaint.measureText(displayWord) > availableWidth)) {
                trySize -= 1;
                if (trySize <= 5) {
                    trySize = 5;
                    break;
                }
                testPaint.setTextSize(trySize);
            }

            text_word.setTextSize(trySize);
        }
    }

    public static void open(Context context) {
        Intent myIntent = new Intent(context, SpellActivity.class);
        SpellActivity.forConfused = 1;
        context.startActivity(myIntent);
    }

    public static void openForFailed(Context context) {
        Intent myIntent = new Intent(context, SpellActivity.class);
        SpellActivity.forConfused = 0;
        Util.log("We not for confused.");
        context.startActivity(myIntent);
    }
}
