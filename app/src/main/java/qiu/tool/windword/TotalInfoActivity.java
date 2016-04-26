
package qiu.tool.windword;

import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;
import qiu.tool.windword.WordLibAdapter.CountReportInfo;


import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

public class TotalInfoActivity extends Activity {

//    CountReportInfo countHashtable;

//    String[] mDateArray = null;
    private TextView mTextFinished;
    private TextView mTextSpellFinished;

    WordLibAdapter mDb = WordLibAdapter.getInstance(this);
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.total_info);

        int finished = mDb.getFinishedCount();
        int ongoing = mDb.getTotalCount();
        int total = finished + ongoing;
        int confused = mDb.getConfusedWordCount();
        int failed = mDb.getFailedCount();

        mTextFinished = (TextView) findViewById(R.id.text_finished);
        mTextFinished.setText("Finished:" + finished + "\n Total:" + total
                + "\n Coufused:" + confused + "\n failed:" + failed );
        mTextSpellFinished = (TextView) findViewById(R.id.text_spell_finished);
        mTextSpellFinished.setText("Spell Finished:"+mDb.getSpellFinishedCount());

    } //end onCreate


     public static void openActivity(Context context) {
         Intent myIntent = new Intent(context, TotalInfoActivity.class);

         context.startActivity(myIntent);
     }
}