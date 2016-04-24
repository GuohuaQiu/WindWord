package qiu.tool.windword.testclock;

import qiu.tool.windword.ConfusedWordActivity;
import qiu.tool.windword.QiuClock;
import qiu.tool.windword.R;
import qiu.tool.windword.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;



public class TestActivity extends Activity {
    QiuClock mClock;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clocker);
        mClock = (QiuClock)findViewById(R.id.clock);
    }
    public static void openActivity(Context context) {
        Intent myIntent = new Intent(context, TestActivity.class);

        Util.log("open confused refid is " );

        context.startActivity(myIntent);
    }

}
