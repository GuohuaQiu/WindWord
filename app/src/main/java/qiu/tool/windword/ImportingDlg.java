package qiu.tool.windword;

import qiu.tool.windword.R;
import qiu.tool.windword.R.id;
import qiu.tool.windword.R.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

public class ImportingDlg extends AlertDialog.Builder{

    private AlertDialog dialog = null;

    private ScrollitemView mItemView = null;
    public ImportingDlg(Context arg0) {
        super(arg0);

        LayoutInflater inflater = LayoutInflater.from(arg0);
        final View view = inflater.inflate(R.layout.dialoglayout, null);

        mItemView = (ScrollitemView) view.findViewById(R.id.items_view);

        setCancelable(false);

        setTitle("正在导入...");
        setView(view);

        setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
    }

    public void newItem(String item,int type){
        mItemView.AddString(item , type == 0);


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
