
package qiu.tool.windword;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import qiu.tool.windword.WordPage.DownContent;

import android.R.bool;
import android.R.integer;
import android.R.xml;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Util {
    private final static String LOG_TAG = "WORD";

    public final static int ERROR_NO = 0;

    public final static int ERROR_TYPE = 1;

    public final static int ERROR_CONNECTION = 2;

    public final static int ERROR_FILE = 3;

    public final static int ERROR_EXCEPTOIN = 4;

    public final static String EXT_CARD = "/ext_card";

    public final static String DOWN_CONTENT_PATH = "content/";

    private static final int IO_BUFFER_SIZE = 4 * 1024;

    public static final int LOAD_RECENT_ALL_LEVEL = 1000;
    public static final int LOAD_HARD_ALL_LEVEL = 1001;
    private static MediaPlayer MPX = null;


    public static void log(String info) {
        Log.i(LOG_TAG, info);
    }

    public static void log(String info, Throwable t) {
        Log.i(LOG_TAG, info, t);
    }

    public static boolean isWritable(String path) {
        File f = new File(path);
        if (f.exists() && f.canWrite()) {
            return true;
        }
        if (!f.exists()) {
            log(path + " is not exist!");
        }
        if (!f.canWrite()) {
            log(path + " is not writable!");
        }
        return false;
    }

    public static String getAppPath() {
        String sysDisk = null;
        if (isWritable(EXT_CARD)) {
            Util.log("EXT_CARD is writable!");
            sysDisk = EXT_CARD;
        } else if (isWritable(Environment.getExternalStorageDirectory().getPath())) {
            sysDisk = Environment.getExternalStorageDirectory().getPath();
        }
        if (sysDisk == null) {
            log("Both ext_card and sdcard are not writable .");
            return null;
        }
        sysDisk += "/word_wind/";
        File f = new File(sysDisk);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                return null;
            }
        }
        return sysDisk;
    }

    public static String getContentPath() {
        String path = getAppPath();
        if (path == null) {
            return null;
        }
        path += DOWN_CONTENT_PATH;

        File f = new File(path);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                return null;
            }
        }
        return path;
    }

    public static String getContentPath(String word) {
        String path = getAppPath();
        if (path == null) {
            return null;
        }
        path += DOWN_CONTENT_PATH;
        char first_letter = word.charAt(0);
        path += first_letter;
        path += "/";

        File f = new File(path);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                return null;
            }
        }
        return path;
    }

    public static String getExternalPath() {
//        File f = new File("/ext_card");
//        if (f.exists()) {
//            return "/ext_card";
//        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String getXmlFileName(String word, int type) {
        String appPath = getContentPath(word);
        if (appPath == null) {
            return null;
        }
        if (type == 0) {
            return appPath + word + ".xml";
        }
        RuntimeException ee = new RuntimeException("test");
        ee.fillInStackTrace();
        Log.i("TEST","CALL",ee);
        return appPath + word + "_" + type + ".xml";
    }

    public static String getRawFileName(String word, int type) {
        String appPath = getContentPath(word);
        if (appPath == null) {
            return null;
        }
        if (type == 0) {
            return appPath + word + ".eng";
        }
        if (type == DownWordContentTask.CONTENT_VOICE) {
            return appPath + word + ".mp3";
        }
        return appPath + word + "_" + type + ".eng";
    }

    public static void delColinsFiles(String word){
        String f = getRawFileName(word, DownWordContentTask.CONTENT_COLINS_DICTION);
        File file = new File(f);
        boolean deleted = file.delete();
        log("Del file:"+deleted +" "+ f);
        f = getXmlFileName(word, DownWordContentTask.CONTENT_COLINS_DICTION);
        file = new File(f);
        log("Del file:"+deleted +" "+ f);
        deleted = file.delete();
    }
/*
    public static String getRawFileName(String word, int type) {
        String appPath = getContentPath();
        if (appPath == null) {
            return null;
        }
        if (type == 0) {
            return appPath + word + ".eng";
        }
        if (type == DownWordContentTask.CONTENT_VOICE) {
            return appPath + word + ".mp3";
        }
        return appPath + word + "_" + type + ".eng";
    }

*/
    public static void downVoice(String word, Handler uihandler) {
        String voice_file_name = getRawFileName(word, DownWordContentTask.CONTENT_VOICE);
        Log.i("Download task", "" + word + "'s VOICE is downloading. ");
        File file;
        file = new File(voice_file_name);
        int err = ERROR_NO;

        if (!file.exists()) {
            err = DownloadWord(word, DownWordContentTask.CONTENT_VOICE);
        }
        DownContent content = null;
        Message msg = uihandler.obtainMessage();

        msg.obj = new DownContent(word, null);
        msg.what = DownWordContentTask.MSG_DOWN_LOAD_END;
        msg.arg1 = DownWordContentTask.CONTENT_VOICE;
        msg.arg2 = err;

        uihandler.sendMessage(msg);
        Log.i("Download task", "" + word + " is downloaded. ");
    }

    public static boolean isRawFileExist(String word, int type) {
        String file_name = getRawFileName(word, type);
        if (file_name == null) {
            return false;
        }

        File file;
        file = new File(file_name);
        return file.exists();
    }

    public static void downEnglishInter(String word, int type, Handler uihandler) {
        String xml_file_name = getXmlFileName(word, type);
        log("Download task " + word + " is downloading. ");
        File file;
        file = new File(xml_file_name);
        int err = ERROR_NO;

        if (!file.exists() || type == DownWordContentTask.CONTENT_BILING_SAMPLE) {
            String raw_file_name = getRawFileName(word, type);
            if (raw_file_name == null) {
                return;
            }

            log("raw file is " + raw_file_name);
            File fileraw = new File(raw_file_name);
            log("The file size is " + fileraw.getTotalSpace());
            if (!fileraw.exists()) {
                log("file " + xml_file_name + " is not exist.");
                err = DownloadWord(word, type);
            }
            if (err == ERROR_NO) {
                log("file " + xml_file_name + " exist. about to handle file.");
                preHandleFile(word, type);
            }
        }
        DownContent content = null;
        Message msg = uihandler.obtainMessage();
        if (err == ERROR_NO) {
            String strE = LoadContentFromFile(word, type);

            if (strE == null) {
                if (type == DownWordContentTask.CONTENT_ENG_DESCRIPTION) {
                    content = new DownContent(word, "No english description available!");
                } else if (type == DownWordContentTask.CONTENT_COLINS_DICTION) {
                    content = new DownContent(word, "No colins description available!");
                } else {
                    content = new DownContent(word, "No biling description available!");
                }
            } else {
                content = new DownContent(word, strE);
            }
        }
        msg.obj = content;
        msg.what = DownWordContentTask.MSG_DOWN_LOAD_END;
        msg.arg1 = type;
        msg.arg2 = err;

        uihandler.sendMessage(msg);
        Log.i("Download task", "" + word + " is downloaded. ");
    }

    public static void downVoice(String word, int type, Handler uihandler) {
        String xml_file_name = getXmlFileName(word, type);
        log("Download task " + word + " is downloading. ");
        File file;
        file = new File(xml_file_name);
        int err = ERROR_NO;

        if (!file.exists() || type == DownWordContentTask.CONTENT_BILING_SAMPLE) {
            String raw_file_name = getRawFileName(word, type);
            if (raw_file_name == null) {
                return;
            }

            log("raw file is " + raw_file_name);
            File fileraw = new File(raw_file_name);
            log("The file size is " + fileraw.getTotalSpace());
            if (!fileraw.exists()) {
                log("file " + xml_file_name + " is not exist.");
                err = DownloadWord(word, type);
            }
            if (err == ERROR_NO) {
                log("file " + xml_file_name + " exist. about to handle file.");
                preHandleFile(word, type);
            }
        }
        DownContent content = null;
        Message msg = uihandler.obtainMessage();
        if (err == ERROR_NO) {
            String strE = LoadContentFromFile(word, type);

            if (strE == null) {
                if (type == DownWordContentTask.CONTENT_ENG_DESCRIPTION) {
                    content = new DownContent(word, "No english description available!");
                } else if (type == DownWordContentTask.CONTENT_COLINS_DICTION) {
                    content = new DownContent(word, "No colins description available!");
                } else {
                    content = new DownContent(word, "No biling description available!");
                }
            } else {
                content = new DownContent(word, strE);
            }
        }
        msg.obj = content;
        msg.what = DownWordContentTask.MSG_DOWN_LOAD_END;
        msg.arg1 = type;
        msg.arg2 = err;

        uihandler.sendMessage(msg);
        Log.i("Download task", "" + word + " is downloaded. ");
    }

    /* standard function to download something by the url. */
    private static InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try {
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            log("Begin connect....");
            httpConn.connect();
            log("Begin getresponse code...");
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                log("Begin getInputStream....");
                in = httpConn.getInputStream();
                log("End getInputStream....");

            }else{
                log("error :End get...." + response);
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return in;
    }

    private static void preHandleFile(String word, int type) {
        String xml_file_name = getXmlFileName(word, type);
        String raw_file_name = getRawFileName(word, type);

        File file = new File(raw_file_name);
        FileInputStream fi;
        try {
            fi = new FileInputStream(file);
            long length = file.length();
            byte[] buffer = new byte[(int)length];
            fi.read(buffer);
            fi.close();
            int len = polishFile(buffer);

            File file_xml = new File(xml_file_name);
            if (!file_xml.exists()) {
                file_xml.createNewFile();
            }

            FileOutputStream fo;
            fo = new FileOutputStream(file_xml, false);
            /* =content change to ="content" */
            int start = 0;
            int end = 0;
            boolean in_content = false;
            if (type == DownWordContentTask.CONTENT_BILING_SAMPLE) {
                fo.write("<test>".getBytes());
            }
            while (start != -1) {
                if (in_content) {
                    end = findRightQuotationErrorPos(buffer, start, len - 1);
                } else {
                    end = findLeftQuotationErrorPos(buffer, start, len - 1);
                }
                if (end == -1) {
                    if (in_content) {
                        Log.e("Polish file error", "no right qutation position!");

                    }
                    fo.write(buffer, start, len - start);
                    break;
                } else {
                    fo.write(buffer, start, end - start);
                    fo.write('\"');
                    in_content = !in_content;
                }
                start = end;
            }
            if (type == DownWordContentTask.CONTENT_BILING_SAMPLE) {
                fo.write("</test>".getBytes());
            }
            fo.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // http://dict.youdao.com/dp/dp?
    // block=collins
    // &q=step%20up
    // &keyfrom=mdict.3.0.0.android
    // &vendor=android_market
    // &imei=004402141755789
    // &version=android_3.0
    // &version=android_3.0


 //   http://dict.youdao.com/dp/dp?
    //block=collins&
    //q=credible&
    //keyfrom=mdict.3.6.android&vendor=anzhuo&imei=004402146485424&screen=720x1184&version=android_3.0&version=android_3.0 HTTP/1.1
    private static int DownloadWord(String word, int type) {
        InputStream in = null;

        String strUrl;
        if (type == DownWordContentTask.CONTENT_ENG_DESCRIPTION) {
            strUrl = "http://dict.youdao.com/dp/dp?" + "block=ee&q=";
        } else if (type == DownWordContentTask.CONTENT_COLINS_DICTION) {
            strUrl = "http://dict.youdao.com/dp/dp?" + "block=collins&q=";
        } else if (type == DownWordContentTask.CONTENT_BILING_SAMPLE) {
            strUrl = "http://dict.youdao.com/dp/dp?" + "block=morelj&q=";
        } else if (type == DownWordContentTask.CONTENT_VOICE) {
            strUrl = "http://dict.youdao.com/speech?audio=";
        } else {
            log("downword error type " + type);
            return ERROR_TYPE;
        }

        if (word.contains(" ")) {
            String strW;
            strW = "" + word;
            strW.replace(" ", "%20");
            strUrl += strW;
        } else {
            strUrl += word;
        }
        // mdict36 doesnt work in mobile, but it works in PC.
//        String mdict36 = "&keyfrom=mdict.3.6.android&vendor=anzhuo&imei=004402141753281&"
//                +"model=MT35t&screen=720x1184&"+
//            "version=android_3.0 HTTP/1.1";
        String mdict30 = "&keyfrom=mdict.3.0.0.android"
            + "&vendor=android_market&imei=004402141753281"
            + "&version=android_3.0&version=android_3.0";
        if (type == DownWordContentTask.CONTENT_COLINS_DICTION ) {
            strUrl += mdict30;
        }
        else if (type != DownWordContentTask.CONTENT_VOICE && type != DownWordContentTask.CONTENT_BILING_SAMPLE) {
            strUrl += mdict30;
        }
        log("The link is " + strUrl);
        try {
            in = OpenHttpConnection(strUrl);
            if (in == null) {
                log("downword error connection ");

                return ERROR_CONNECTION;
            }
            String strfile = getRawFileName(word, type);
            log("Raw file is :" + strfile);

            File file = new File(strfile);
            log("To create file:" + strfile);
            boolean b = file.createNewFile();
            if (!b) {
                log("Faild to create file " + strfile);
            }
            FileOutputStream fo;
            fo = new FileOutputStream(file, false);
            if (fo == null) {
                log("download file error output stream.");
                return ERROR_FILE;
            }
            copy(in, fo);
            fo.close();
            in.close();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return ERROR_EXCEPTOIN;
        }
        log("Down " + word + "successfully. the type is " + type);
        return ERROR_NO;
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    private Bitmap DownloadImage(String URL) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return bitmap;
    }

    private static int polishFile(byte[] content) {
        int len = 0;
        len = delSpace(content, content.length);
        len = delNbsp(content, len);
        len = delLink(content, len);
        len = delBr(content, len);

        return len;
    }

    private static int delNbsp(byte[] content, int len) {
        byte[] after = new byte[len];
        int pos = 0;
        for (int i = 0; i < len; i++) {
            if (i < (len - 6) && content[i] == '&' && content[i + 1] == 'n'
                    && content[i + 2] == 'b' && content[i + 3] == 's' && content[i + 4] == 'p'
                    && content[i + 5] == ';') {
                i += 5;
                continue;
            }
            after[pos] = content[i];
            pos++;
        }
        for (int i = 0; i < pos; i++) {
            content[i] = after[i];

        }
        return pos;
    }

    private static int delSpace(byte[] content, int len) {
        byte[] after = new byte[len];
        int pos = 0;
        for (int i = 0; i < len; i++) {
            if (content[i] == 0x9) {
                continue;
            }
            after[pos] = content[i];
            pos++;
        }
        for (int i = 0; i < pos; i++) {
            content[i] = after[i];

        }
        return pos;
    }

    public static String LoadContentFromFile(String word, int type) {
        String strfile = getXmlFileName(word, type);

        log("LoadContentFromFile(String " + word + ",int " + type + ");");
        NetworkLoader myExampleHandler;
        if (type == DownWordContentTask.CONTENT_ENG_DESCRIPTION) {
            myExampleHandler = new EngInterLoader();
        } else if (type == DownWordContentTask.CONTENT_COLINS_DICTION) {
            myExampleHandler = new ColinsLoader();

        } else if (type == DownWordContentTask.CONTENT_BILING_SAMPLE) {
            myExampleHandler = new BilangLoader();
        } else {
            return "Wrong type " + type;
        }

        try {
            /* Get a SAXParser from the SAXPArserFactory. */
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            /* Get the XMLReader of the SAXParser we created. */
            XMLReader xr = sp.getXMLReader();
            /* Create a new ContentHandler and apply it to the XML-Reader */

            xr.setContentHandler(myExampleHandler);
            /* Parse the xml-data from our URL. */
            xr.parse(new InputSource(new FileInputStream(strfile)));
            /* Parsing has finished. */
            return myExampleHandler.getParsedData();

        } catch (SAXParseException e) {
            /* Display any Error to the GUI. */
            Log.e("load error for parse", e.getMessage());
            return myExampleHandler.getParsedData();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("load error", e.getMessage());
            return null;
        }
    }

    public static void addQuotationMark(byte[] buffer) {
        int count = 0;
        ArrayList<integer> idxPos = new ArrayList<integer>(20);
        int oldLen = buffer.length;
        boolean in_content = false;
        for (int i = 0; i < (buffer.length - 1); i++) {
            if (in_content) {
                if (buffer[i] == '>' && buffer[i + 1] != ' ') {
                    // idxPos.add(i);
                    in_content = false;
                }
            } else {
                if (buffer[i] == '=' && buffer[i + 1] != '\"') {
                    i += 1;
                    // idxPos.add(i);
                    in_content = true;
                }
            }
        }
    }

    /* return -1 if no error. */
    public static int findLeftQuotationErrorPos(byte[] buffer, int from, int to) {
        boolean inQuotation = false;
        for (int i = from; i < (to - 1); i++) {
            // some link are in quotation scope but it has parameters starts
            // with '='.
            // so we need to skip the links.
            if (buffer[i] == '\"') {
                inQuotation = !inQuotation;
            }
            if (inQuotation) {
                continue;
            }

            if (buffer[i] == '=' && buffer[i + 1] != '\"') {
                if (buffer[i + 1] == '\'') {
                    i++;
                    continue;
                }
                return i + 1;
            }
        }
        return -1;
    }

    /* return -1 if no error. */
    public static int findRightQuotationErrorPos(byte[] buffer, int from, int to) {
        for (int i = from; i < to; i++) {
            if (buffer[i] == '>' || buffer[i] == ' ') {
                return i;
            }
        }
        return -1;
    }

    public static StringBuffer gettag(StringBuffer strTag) {
        int blankpos = strTag.indexOf(" ");
        if (blankpos == -1) {
            return strTag;
        }

        return strTag.delete(blankpos, strTag.length());
    }

    public static void addTag2List(ArrayList<StringBuffer> array, StringBuffer tag) {
        if (tag.charAt(0) == '/') {
            int count = array.size();
            if (count == 0) {
                Log.e("Tag error", "no left tag for " + tag.toString());
                return;
            }
            StringBuffer left = array.get(count - 1);
            String right = tag.substring(1);

            if (left.toString().equals(right)) {
                array.remove(count - 1);
            }
        } else {
            array.add(new StringBuffer(tag));
        }
        print_tags(array);
    }

    public static void print_tags(ArrayList<StringBuffer> array) {
        String strtag = "";

        for (int i = 0; i < array.size(); i++) {
            StringBuffer tag = array.get(i);
            strtag += "|" + tag.toString();
        }
        Log.i("tag list", strtag);
    }

    /* true is the file is ok ,other the file is corrupted */

    public static boolean testFile(String word, int type) {
        String xml_file_name = getXmlFileName(word, type);
        ArrayList<StringBuffer> array = new ArrayList<StringBuffer>(20);

        File file = new File(xml_file_name);
        FileInputStream fi;
        try {
            fi = new FileInputStream(file);
            long length = file.length();
            byte[] buffer = new byte[(int)length];
            fi.read(buffer);
            fi.close();
            int pos = 0;

            StringBuffer aBuffer = new StringBuffer(128);

            // String strtag = gettag(aBuffer);
            while (pos < length) {
                pos = findTag(buffer, pos, aBuffer);
                aBuffer = gettag(aBuffer);
                Log.i("Test Tag", aBuffer.toString());
                addTag2List(array, aBuffer);
            }
            if (array.size() > 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            /* Display any Error to the GUI. */
            Log.e("test error", e.getMessage());
            return false;
        }
    }

    public static int findTag(byte[] buffer, int from, StringBuffer strOut) {
        int length = buffer.length;
        int i;
        boolean findleft = true;
        int start = -1;
        strOut.delete(0, strOut.length());

        for (i = from; i < length; i++) {
            if (findleft) {
                if (buffer[i] == '<') {
                    findleft = false;
                    start = i;
                }
            } else {
                if (buffer[i] == '>') {
                    i++;
                    strOut.append(new String(buffer, start + 1, i - start - 2));
                    break;
                }
            }
        }
        return i;
    }

    private static String testStr = "We<a href=\"audioimg></a><span class=\"gray\">(33KB)</span>";

    /* 01234567890 12345678901234567890123456 78901 2345678901234567890 */
    /* 1 2 3 4 5 6 */
    public static void test_delLink() {
        byte[] test_content = testStr.getBytes();
        int len = delLink(test_content, testStr.length());
        Log.i("test", new String(test_content, len));
    }

    /* This function delete <a>...</a> */
    private static int delLink(byte[] content, int len) {
        byte[] after = new byte[len];
        boolean inlink = false;
        int pos = 0;
        for (int i = 0; i < len; i++) {
            if (inlink) {
                if (i < (len - 8) && content[i] == '<' && content[i + 1] == '/'
                        && content[i + 2] == 'a' && content[i + 3] == '>') {
                    i += 3;
                    inlink = false;
                }
                continue;
            }
            if (i < (len - 8) && content[i] == '<' && content[i + 1] == 'a'
                    && content[i + 2] == ' ') {
                inlink = true;
                i += 4;
                continue;
            }
            after[pos] = content[i];
            pos++;
        }
        for (int i = 0; i < pos; i++) {
            content[i] = after[i];
        }
        return pos;
    }

    /* This function delete <a>...</a> */
    private static int delBr(byte[] content, int len) {
        byte[] after = new byte[len];
        boolean inlink = false;
        int pos = 0;
        for (int i = 0; i < len; i++) {
            if (i < (len - 4) && content[i] == '<' && content[i + 1] == 'b'
                    && content[i + 2] == 'r' && content[i + 3] == '>') {
                i += 3;
            } else {
                after[pos] = content[i];
                pos++;
            }
        }

        for (int i = 0; i < pos; i++) {
            content[i] = after[i];
        }
        return pos;
    }

    static public String getFormatTimeElapsed(final long time){
        return String.format("%d:%d:%d", time/3600000,time/60000%60,time/1000%60);
    }

    static public String getFormatDate(final long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("yyyy-MM-dd");

        int day = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

//        Log.i("Time convert", "" + time + "->" + year + "-" + month + "-" + day);

        return format.format(calendar.getTime());
    }
    static public String getShortFormatDate(final long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("yy-MM-dd");

        int day = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

//        Log.i("Time convert", "" + time + "->" + year + "-" + month + "-" + day);

        return format.format(calendar.getTime());
    }

    static public String getFormatDateTime(final long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("yyyy-MM-dd HH:mm:ss");


        int day = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

//        Util.log("Time convert:" + time + "->" + format.format(calendar.getTime()));

        return format.format(calendar.getTime());
    }

    static public String getFormatDateTimeAutoHideYear(final long time) {

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curDay = calendar.get(Calendar.DATE);
        int curMonth = calendar.get(Calendar.MONTH);

        calendar.setTimeInMillis(time);

        int day = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
//        Util.log("Actual time " + time + "->" + year + "-" + month + "-" + day);
//        Util.log("Current time " + time + "->" + curYear + "-" + curMonth + "-" + curDay);


        SimpleDateFormat format = new SimpleDateFormat();

        if (curYear == year) {
            if (month == curMonth && day == curDay) {
                format.applyPattern("MM-dd HH:mm:ss");
            } else {
                format.applyPattern("MM-dd HH:mm");
            }
        } else {
            format.applyPattern("yyyy-MM-dd");
        }

        return format.format(calendar.getTime());
    }
    static public String getFormatDateTimeNoYear(final long time) {

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curDay = calendar.get(Calendar.DATE);
        int curMonth = calendar.get(Calendar.MONTH);

        calendar.setTimeInMillis(time);

        int day = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
 //       Util.log("Actual time " + time + "->" + year + "-" + month + "-" + day);
//        Util.log("Current time " + time + "->" + curYear + "-" + curMonth + "-" + curDay);


        SimpleDateFormat format = new SimpleDateFormat();

        if (curYear == year && month == curMonth && day == curDay) {
            format.applyPattern("HH:mm:ss");
        } else {
            format.applyPattern("MM-dd");
        }

//        Log.i("Time convert", "" + time + "->" + year + "-" + month + "-" + day);

        return format.format(calendar.getTime());
    }

    public static String getDataFile() {
        String filePath = Util.getAppPath();
        if (filePath != null) {
            return filePath + "word_database.wdwd";
        }
        return null;
    }

    public static boolean delFile(String word, int type) {
        String filepath = "";
        filepath = getRawFileName(word, type);
        File file = new File(filepath);
        if (file != null && file.exists()) {
            return file.delete();
        }
        filepath = getXmlFileName(word, type);
        file = new File(filepath);
        if (file != null && file.exists()) {
            return file.delete();
        }
        return true;
    }

    public static void delFiles(String word){
        delFile(word, DownWordContentTask.CONTENT_ENG_DESCRIPTION);
        delFile(word, DownWordContentTask.CONTENT_COLINS_DICTION);
        delFile(word, DownWordContentTask.CONTENT_BILING_SAMPLE);
        delFile(word, DownWordContentTask.CONTENT_VOICE);
    }

    public static long getTimeBegin(String dt){
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
        Date date = null;
        try {
            date = format.parse(dt);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.getTimeInMillis();
    }
    public static long getTimeEnd(String dt){
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
        Date date = null;
        try {
            date = format.parse(dt);
            date.setHours(23);
            date.setMinutes(59);
            date.setSeconds(59);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.getTimeInMillis();
    }
    public static int[] getColors(int maxSpan) {

        int[] colors = new int[maxSpan + 1];
        int j = 0;
        for (int i = 0; i <= maxSpan; i++) {
            if (j > 9) {
                j = 0;
            }
            colors[i] = baseColors[j];
            j++;
        }
        return colors;
    }
    public static int[] baseColors = new int[] {
                0xff44fa74, 0xff3366ff, Color.RED, 0xff00ffff,Color.GREEN, 0xffffff00,  0xffff00ff,
                0xff00ddaa, 0xffddaa00, 0xff8800dd
        };
    public static int getIndexColor(int index) {
        if(index<0){
            return 0;
        }
        return baseColors[index%10];
    }

    public static void playVoice(Context txt,String word, OnCompletionListener listener){

        if(word != null){
            Util.log("Play voice for " + word);
        }
        if(MPX == null){
            MPX = new MediaPlayer();
            if(listener != null){
                MPX.setOnCompletionListener(listener);
            }
        }


        try {
            MPX.reset();
            MPX.setDataSource(Util.getRawFileName(word, DownWordContentTask.CONTENT_VOICE));
            MPX.prepare();
        } catch (IllegalArgumentException e) {
            Toast.makeText(txt, e.toString(), Toast.LENGTH_LONG);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Toast.makeText(txt, e.toString(), Toast.LENGTH_LONG);
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(txt, e.toString(), Toast.LENGTH_LONG);
            e.printStackTrace();
        }
        MPX.start();
    }


    //2015-04-04
    public static int getToday(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }
}
