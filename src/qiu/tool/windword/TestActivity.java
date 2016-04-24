
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

public class TestActivity extends Activity {

    CountReportInfo countHashtable;

    String[] mDateArray = null;
    private CountReportView mForm;

    WordLibAdapter mDb = WordLibAdapter.getInstance(this);
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.count_form);

        countHashtable = mDb.getStaticTableInfo();
        mForm = (CountReportView) findViewById(R.id.form);;
        mForm.setHashTable(countHashtable);
    } //end onCreate


     public static void openActivity(Context context) {
         Intent myIntent = new Intent(context, TestActivity.class);

         context.startActivity(myIntent);
     }
}