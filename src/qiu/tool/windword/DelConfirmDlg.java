package qiu.tool.windword;

import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class DelConfirmDlg extends AlertDialog.Builder{

    private AlertDialog dialog = null;
    Context context = null;

    private EditText mWordText = null;
    private CheckBox mConfirmBox = null;
    public DelConfirmDlg(Context arg0) {
        super(arg0);
        context = arg0;

        LayoutInflater inflater = LayoutInflater.from(arg0);
        final View view = inflater.inflate(R.layout.del_confirm, null);

        mWordText = (EditText) view.findViewById(R.id.edit_word);
        mConfirmBox = (CheckBox)view.findViewById(R.id.confirm_checkbox);


        setCancelable(false);

        setTitle("Delete word");
        setView(view);

        setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });


        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (mWordText != null) {
                    Util.log("aaaaaaaa");
                    if (mConfirmBox.isChecked()) {
                        deleteWord(mWordText.getText().toString());
                    }else{
                        Util.log("not checked.");
                    }
                }
            }
        });
    }

    public void deleteWord(String word) {
        WordLibAdapter adapter = WordLibAdapter.getInstance(context);
        if(word == null || word.length() == 0){

        }else{
            Cursor cursor = adapter.getWord(word);
            if (cursor != null && cursor.getCount() == 1) {
                Util.log("no this word in library!");
                adapter.removeWord(word,WordLibAdapter.DEL_REASON_REPEAT);
            } else {

            }
        }
        Util.delFiles(word);
    }

    public AlertDialog show() {
        dialog = create();
        dialog.show();
        return dialog;
    }
    public void dismiss(){
        dialog.dismiss();
    }
}
