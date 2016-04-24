
package qiu.tool.windword;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class DatabaseBackuper {

    private SQLiteDatabase _db;

    private Exporter _exporter;

    private Context mContext;

    public DatabaseBackuper(SQLiteDatabase db, Context context) {
        _db = db;
        mContext = context;

        try {
            // create a file on the sdcard to export the
            // database contents to
            String file = Util.getDataFile();
            if (file != null) {
                File myFile = new File(file);
                myFile.createNewFile();

                FileOutputStream fOut = new FileOutputStream(myFile);
                BufferedOutputStream bos = new BufferedOutputStream(fOut);

                _exporter = new Exporter(bos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportData(String[] tables) {
        log("Exporting Data");

        try {
            _exporter.startDbExport(_db.getPath());

            // get the tables out of the given sqlite database
            String sql = "SELECT * FROM sqlite_master";

            Cursor cur = _db.rawQuery(sql, new String[0]);
            Log.d("db", "show tables, cur size " + cur.getCount());
            cur.moveToFirst();

            String tableName;
            while (cur.getPosition() < cur.getCount()) {
                tableName = cur.getString(cur.getColumnIndex("name"));
                log("table name " + tableName);
                for(String tN:tables){
                    if(tableName.equals(tN)){
                        exportTable(tableName);
                    }
                }
                cur.moveToNext();
            }
            _exporter.endDbExport();
            _exporter.close();
            MediaScannerConnection.scanFile(mContext, new String[] {
                Util.getDataFile().toString()
            }, null, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportTable(String tableName) throws IOException {
        _exporter.startTable(tableName);
        log("TABLE....." + tableName);

        // get everything from the table
        String sql = "select * from " + tableName;
        Cursor cur = _db.rawQuery(sql, new String[0]);
        int numcols = cur.getColumnCount();

        log("Start exporting table " + tableName);

        // // logging
        // for( int idx = 0; idx < numcols; idx++ )
        // {
        // log( "column " + cur.getColumnName(idx) );
        // }

        cur.moveToFirst();
        // int i = 0;

        // move through the table, creating rows
        // and adding each column with name and value
        // to the row
        while (cur.getPosition() < cur.getCount()) {
            _exporter.startRow();
            String name;
            String val;
            for (int idx = 0; idx < numcols; idx++) {
                name = cur.getColumnName(idx);
                val = cur.getString(idx);
                log("col '" + name + "' -- val '" + val + "'");

                _exporter.addColumn(name, val);
            }

            _exporter.endRow();
            cur.moveToNext();
        }

        cur.close();

        _exporter.endTable();
    }

    private void log(String msg) {
        Log.d("DatabaseBackuper", msg);
    }

    class Exporter {
        private static final String CLOSING_WITH_TICK = "'>\r\n";

        private static final String START_DB = "<export-database name='";

        private static final String END_DB = "</export-database>\r\n";

        private static final String START_TABLE = "<table name='";

        private static final String END_TABLE = "</table>\r\n";

        private static final String START_ROW = "<row>\r\n";

        private static final String END_ROW = "</row>\r\n";

        private static final String START_COL = "<column name='";

        private static final String END_COL = "</column>\r\n";

        private BufferedOutputStream _bos;

        public Exporter() {
        }

        public Exporter(BufferedOutputStream bos) {
            _bos = bos;
        }

        public void close() throws IOException {
            if (_bos != null) {
                _bos.close();
            }
        }

        public void startDbExport(String dbName) throws IOException {
            String stg = START_DB + dbName + CLOSING_WITH_TICK;
            _bos.write(stg.getBytes());
        }

        public void endDbExport() throws IOException {
            _bos.write(END_DB.getBytes());
        }

        public void startTable(String tableName) throws IOException {
            String stg = START_TABLE + tableName + CLOSING_WITH_TICK;
            _bos.write(stg.getBytes());
        }

        public void endTable() throws IOException {
            _bos.write(END_TABLE.getBytes());
        }

        public void startRow() throws IOException {
            _bos.write(START_ROW.getBytes());
        }

        public void endRow() throws IOException {
            _bos.write(END_ROW.getBytes());
        }

        public void addColumn(String name, String val) throws IOException {
            String stg = START_COL + name + CLOSING_WITH_TICK + val + END_COL;
            _bos.write(stg.getBytes());
        }
    }

    public static void importData(WordLibAdapter dbAdapter, Handler handler, String path) {
        try {
            Util.log("Load this file:"+path);
            /* Get a SAXParser from the SAXPArserFactory. */
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            /* Get the XMLReader of the SAXParser we created. */
            XMLReader xr = sp.getXMLReader();
            /* Create a new ContentHandler and apply it to the XML-Reader */
            Importer importer = new Importer(dbAdapter, handler);

            xr.setContentHandler(importer);
            /* Parse the xml-data from our URL. */
            String file;
            if (path != null) {
                file = path;
            } else {
                file = Util.getDataFile();
            }
            if (file != null) {
                xr.parse(new InputSource(new FileInputStream(file)));
            }
            /* Parsing has finished. */
        } catch (SAXParseException e) {
            /* Display any Error to the GUI. */
            Util.log("load error for parse:"+ e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Importer extends DefaultHandler {
        private final Handler mUIHandler;

        WordLibAdapter mWordLibAdapter;

        String mColName;

        ContentValues mRowValues;

        String mCurrentValue;

        String mTableName;

        public Importer(WordLibAdapter dbAdapter, Handler handler) {
            mWordLibAdapter = dbAdapter;
            mUIHandler = handler;

        }

        @Override
        public void startDocument() throws SAXException {
            mWordLibAdapter.beginImportData();
        }

        @Override
        public void endDocument() throws SAXException {
            if (mUIHandler != null) {
                Message msg = mUIHandler.obtainMessage(MainScreen.MSG_WORD_IMPORT_COMPLETED);
                mUIHandler.sendMessage(msg);
                if(mWordLibAdapter != null){
                    mWordLibAdapter.endImportData();
                }
                Util.log("mUIHandler send msg to ProgressActivity:" + msg.what);
            }

        }

        /**
         * Gets be called on opening tags like: <tag> Can provide attribute(s),
         * when xml was like: <tag attribute="attributeValue">
         */
        @Override
        public void startElement(String namespaceURI, String localName, String qName,
                Attributes atts) throws SAXException {
            try {
                if (localName.equals("row")) {
                    startRow();
                } else if (localName.equals("column")) {
                    mColName = atts.getValue("name");
                    mCurrentValue = "";
                } else if (localName.equals("table")) {
                    mTableName = atts.getValue("name");
                }
                //Util.log("startElement " + namespaceURI + " local " +
                //localName + " qName " + qName);
                //Util.log("name = " + atts.getValue("name"));

            } catch (NullPointerException ex) {
                Util.log("startElement " + ex.toString());
            }
        }

        /**
         * Gets be called on closing tags like: </tag>
         */
        @Override
        public void endElement(String namespaceURI, String localName, String qName)
                throws SAXException {
            try {
                if (localName.equals("row")) {
                    endRow();
                } else if (localName.equals("column")) {
                    setRowValue();
                }
                // Util.log("Farther class" + "endElement" + "$ " + namespaceURI
                // + "$ " + localName
                // + "$" + qName);
            } catch (NullPointerException e) {
                Util.log("endElement NullPointerException", e);
                // TODO: handle exception
            }

            // Log.i("wind element", "endElement " + namespaceURI + " local "
            // + localName + " qName " + qName);
        }

        /**
         * Gets be called on the following structure: <tag>characters</tag>
         */
        @Override
        public void characters(char ch[], int start, int length) {
            try {

                String strAppend = new String(ch, start, length);
                // Util.log(strAppend);

                if (length > 0) {
                    mCurrentValue += strAppend;
                }
            } catch (NullPointerException e) {
                Util.log("characters null pointer");
                // TODO: handle exception
            }
        }

        public void startRow() {
            mRowValues = new ContentValues();
        }

        public void endRow() {
            mWordLibAdapter.updateValues(mTableName, mRowValues);
            if (mUIHandler != null) {
                Message msg = mUIHandler.obtainMessage(MainScreen.MSG_WORD_UPDATED);
                msg.obj = mRowValues.get(WordLibAdapter.COL_WORD);
                mUIHandler.sendMessage(msg);
                Util.log("mUIHandler send msg to ProgressActivity:" + msg.what);
            }
            mRowValues = null;
        }

        public void setRowValue() {
            if (mColName == null) {
                Util.log("Column name is null , Error happen");
                return;
            }/*
              * byte[] a = mCurrentValue.getBytes(); String aa = ""; for(int i =
              * 0;i < 3;i++){ aa += String.format("<%2x>", a[i]); }
              * Util.log("HEX :" + aa);
              */
            if (mCurrentValue.startsWith("\n")) {

                mCurrentValue = mCurrentValue.replaceFirst("\n", "");
            }
            if (mCurrentValue.equals("null")) {

            } else {
                mRowValues.put(mColName, mCurrentValue);
            }

            mColName = null;
        }
    }
}
