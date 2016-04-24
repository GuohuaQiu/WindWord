package qiu.tool.windword;


import qiu.tool.windword.R;


import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;



public class WordView extends Activity implements View.OnClickListener{

    /*sub folder to store the catetory png files*/
	private String mPathName = null;
	/*current borwser folder, it is a sub folder after user select a category
	 * but it is app root folder when app launched*/
	private String mCurrentPath = Environment.getExternalStorageDirectory().getPath();

	final private File mRootPathFile = new File(Environment.getExternalStorageDirectory() + "//babyword//");
	/*file list in current sub folder*/
	private String[] mFilePaths = null;
	/*sub folder list for selection*/
	private String[] mFolderNames = null;

	private TextView textWord = null;
	private ImageView imgview = null;
	private Button btnPrev = null;
    private Button btnNext = null;
    private Button btnRename = null;
    private EditText editRename = null;
	private static final int DIALOG_LOAD_FILE = 1000;

	private int mIndex = -1;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageword);
		editRename =  (EditText)findViewById(R.id.edit_rename);
		imgview =  (ImageView)findViewById(R.id.img_word);
		btnPrev =  (Button)findViewById(R.id.btn_prev);
        btnNext =  (Button)findViewById(R.id.btn_next);
        btnRename =  (Button)findViewById(R.id.btn_rename);
		textWord =  (TextView)findViewById(R.id.text_word);
		btnPrev.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnRename.setOnClickListener(this);
        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);

	}
	public void nextWord() {
        if(mFilePaths == null || mFilePaths.length == 0) {
            return;
        }
		mIndex++;
		if(mIndex >= mFilePaths.length) {
			mIndex = 0;
		}
		showImage(mIndex);
	}
	public void prevWord() {
        if(mFilePaths == null || mFilePaths.length == 0) {
            return;
        }
		mIndex--;
		if(mIndex < 0) {
			mIndex = mFilePaths.length - 1;
		}
		showImage(mIndex);
	}

	public void showImage(int index) {
		if(mFilePaths == null || mFilePaths.length == 0) {
			return;
		}
		if(mIndex == -1) {
			return;
		}
		String filepath = mCurrentPath + mFilePaths[index];
		File file = new File(filepath);
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Log.i("WordView", "file is not exist " + filepath);
			e.printStackTrace();
			return;
		}
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream (in);

		} catch (Exception e) {
			Log.i("WordView", "decode exception" + filepath);
			e.printStackTrace();
			return;
		}
		if(bitmap != null)
		{
			imgview.setImageBitmap(bitmap);

			String strWord = getNameOnly(mFilePaths[index]);
			textWord.setText(strWord);
			editRename.setText(strWord);
		}
	}

	public String getNameOnly(String filename) {
	    int n = filename.lastIndexOf('.');
	    return filename.substring(0,n);
	}

	public String getNameExt(String filename) {
        int n = filename.lastIndexOf('.');
        return filename.substring(n, filename.length());
	}

    public boolean renameSDFile(int index, String newFileName) {

        String newfile = newFileName + getNameExt(mFilePaths[index]);
        File oleFile = new File(mCurrentPath + mFilePaths[index]);
        File newFile = new File(mCurrentPath + newfile);
        boolean b = oleFile.renameTo(newFile);
        if(b) {
            mFilePaths[index] = newfile;
        }
        return b;
    }

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_prev: {
			prevWord();
			break;
		}
        case R.id.btn_next: {
            nextWord();
            break;
        }
        case R.id.btn_rename: {
            renameWord();
            break;
        }
		}
	}

	private void renameWord() {
	    String strNew = editRename.getText().toString();
	    if(strNew.length() == 0){
	        return;
	    }
	    if(strNew.equalsIgnoreCase(textWord.getText().toString())) {
	        return;
	    }
	    renameSDFile(mIndex,strNew);
	}



	void loadFolderList() {
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		};
		File folders[] = mRootPathFile.listFiles(filter);
		mFolderNames = new String[folders.length];
		int i = 0;
		for(File file : folders) {
			mFolderNames[i] = file.getName();
			Log.i("WordView", "folder " + i + " " + mFolderNames[i]);
			i++;
		}
	}

	public void resetPath() {
		mCurrentPath = Environment.getExternalStorageDirectory() + "//babyword//"
				+ mPathName + "//";

		File f = new File(mCurrentPath);
		mFilePaths = f.list();
		if(mFilePaths != null && mFilePaths.length > 0) {
			mIndex = 0;
			showImage(mIndex);
	        if( mFilePaths.length > 1) {
            btnPrev.setEnabled(true);
            btnNext.setEnabled(true);
	        }
		} else {
            btnPrev.setEnabled(false);
            btnNext.setEnabled(false);
			mIndex = -1;
		}
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		Builder builder = new Builder(this);

		switch (id) {
		case DIALOG_LOAD_FILE:
			builder.setTitle("选择分类：");
			loadFolderList();
			if (mFolderNames == null) {
				Log.e("LoadFile", "Showing file picker before loading the file list");
				dialog = builder.create();
				return dialog;
			}
			builder.setItems(mFolderNames, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mPathName = mFolderNames[which];
					resetPath();
				}
			});
			break;
		}
		dialog = builder.show();
		return dialog;
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.word_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.choose_path:
			showDialog(DIALOG_LOAD_FILE);
			return true;
		default:
			return false;
		}
	}

}

