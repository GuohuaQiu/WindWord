package qiu.tool.windword;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class WordLoader extends DefaultHandler{

    // ===========================================================
    // Fields
    // ===========================================================
    private final Context context;
    private boolean in_wordbook = false;
    private boolean in_item = false;
    private boolean in_trans = false;
    private boolean in_phonetic = false;
    private boolean in_word = false;
    private WordLibAdapter db = null;
    private int import_number = 0;

    private Word myWord = new Word();

    private Handler mUIHandler = null;

    public WordLoader(Context ctx, Handler handler) {
        this.context = ctx;
        mUIHandler = handler;
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public Word getParsedData() {
        return this.myWord;
    }


    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {
        this.myWord = new Word();
        db = WordLibAdapter.getInstance(context);
        import_number = 0;
    }

    @Override
    public void endDocument() throws SAXException {
        // Nothing to do
    }

    /** Gets be called on opening tags like:
     * <tag>
     * Can provide attribute(s), when xml was like:
     * <tag attribute="attributeValue">*/
    @Override
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) throws SAXException {
        if (localName.equals("wordbook")) {
            import_number = 0;
            this.in_wordbook = true;
        }else if (localName.equals("item")) {
            myWord.clear();
            this.in_item = true;
        }else if (localName.equals("trans")) {
            this.in_trans = true;
        }else if (localName.equals("phonetic")) {
            this.in_phonetic = true;
        }else if (localName.equals("word")) {
            this.in_word = true;
        }
    }

    /** Gets be called on closing tags like:
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (localName.equals("wordbook")) {
            this.in_wordbook = false;
        } else if (localName.equals("item")) {
            // add this word to library.
            Log.i("NUMBER", "number=" + import_number);
            Message msg = mUIHandler.obtainMessage();
            msg.obj = myWord.getWord();
            if (db.insertWord(myWord) == 0) {
                import_number++;
                msg.what = MainScreen.MSG_WORD_ADDED;

            } else {
                msg.what = MainScreen.MSG_WORD_UPDATED;
            }
            mUIHandler.sendMessage(msg);
            this.in_item = false;
        } else if (localName.equals("trans")) {
            this.in_trans = false;
        } else if (localName.equals("phonetic")) {
            this.in_phonetic = false;
        } else if (localName.equals("word")) {
            this.in_word = false;
        }
    }

    /** Gets be called on the following structure:
     * <tag>characters</tag> */
    @Override
    public void characters(char ch[], int start, int length) {
        if (this.in_trans) {
            myWord.setInterpretion(new String(ch, start, length));
        } else if (this.in_phonetic) {
            myWord.setPhonetic(new String(ch, start, length));
        } else if (this.in_word) {
            myWord.setWord(new String(ch, start, length));
        }
    }
}
