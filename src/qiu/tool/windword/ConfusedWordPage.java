
package qiu.tool.windword;

import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView.OnItemClickListener;

import android.widget.BaseAdapter;
import android.widget.ListView;

public class ConfusedWordPage extends Activity implements OnItemClickListener {

    private ConfusedWordListAdapter mAdapter = null;

    WordLibAdapter mLib = null;

    ListView lv;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.refreshlist);

        lv = (ListView)findViewById(R.id.list_refresh);

        mLib = WordLibAdapter.getInstance(this);

        Intent intent = getIntent();
        if (intent == null) {
            Log.i("FAILED INTENT", "HAHA");
            return;
        }
        mAdapter = new ConfusedWordListAdapter(this);
        mAdapter.loadData();

        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ConfusedWordManageActivity.setCompareId(this,mAdapter.getRefId(position));
/*
        int nSelA = mAdapter.getSelA();
        Util.log("position: " + position);
        if (nSelA != -1) {
            ConfusedWordManageActivity.ref_id_A = mAdapter.getRefId(nSelA);
            ConfusedWordManageActivity.ref_id_B = mAdapter.getRefId(position);
            ConfusedWordManageActivity.openActivity(this);
//            mergeGroup(mAdapter.getRefId(nSelA), mAdapter.getRefId(position));

        } else {
            mAdapter.setSelA(position);
            // lv.invalidateChild((View)view.getParent(), null);
            lv.invalidateViews();
        }*/
    }

    public void mergeGroup(int idA, int idB) {
        mLib.mergeConfusedGroup(idA, idB);
        mAdapter.setSelA(-1);
        mAdapter.refresh();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    static public void openConfusedPage(Context txt) {
        Intent listIntent = new Intent(txt, ConfusedWordPage.class);

        txt.startActivity(listIntent);
    }
}
