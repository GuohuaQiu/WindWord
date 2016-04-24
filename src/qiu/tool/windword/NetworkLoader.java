
package qiu.tool.windword;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class NetworkLoader extends DefaultHandler {
    public final static int TAG_OTHER = 0;
    public final static int TAG_DIV = 1;
    public final static int TAG_P = 2;
    public final static int TAG_STRONG = 3;
    public final static int TAG_SPAN = 4;
    public final static int TAG_B = 5;
    public final static int TAG_OL = 6;
    public final static int TAG_LI = 7;
    public final static int TAG_I = 8;
    public final static int TAG_A = 9;
    public final static int TAG_EM = 10;

    //to build a multi level tags.
    public int mTags[] = new int[16];
    public int mTotalLevel = 0;
    protected boolean mNeedReturn = false;



    // ===========================================================
    // Fields
    // ===========================================================

    protected boolean in_item = false;

    protected String mResult = "";


    public NetworkLoader() {

    }

    public static int getTagId(String strTag){
        if(strTag.equals("div")){
            return TAG_DIV;
         }
        if(strTag.equals("p")){
            return TAG_P;
         }
        if(strTag.equals("strong")){
            return TAG_STRONG;
         }
        if(strTag.equals("span")){
            return TAG_SPAN;
         }
        if(strTag.equals("b")){
            return TAG_B;
         }
        if(strTag.equals("ol")){
            return TAG_OL;
         }
        if(strTag.equals("a")){
            return TAG_A;
         }
        if(strTag.equals("li")){
            return TAG_LI;
         }
        if(strTag.equals("i")){
            return TAG_I;
         }
        if(strTag.equals("em")){
            return TAG_EM;
         }
        return TAG_OTHER;

    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public String getParsedData() {
        return mResult;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }
    public abstract boolean isInItem();

    /**
     * Gets be called on opening tags like: <tag> Can provide attribute(s), when
     * xml was like: <tag attribute="attributeValue">
     */
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        try {
            mTags[mTotalLevel] = getTagId(localName);
            mTotalLevel++;
            in_item = isInItem();

            String strTag = "";
            for (int i = 0; i < mTotalLevel; i++) {
                strTag += "|" + mTags[i];
            }
//            Util.log("tags:"+ strTag);

 //           Util.log( "startElement " + namespaceURI + " local " + localName + " qName "
//                    + qName);

        } catch (NullPointerException e) {
            Log.i("Father","startElement null pointer");
            // TODO: handle exception
        }
    }

    /**
     * Gets be called on closing tags like: </tag>
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            mTotalLevel--;
            in_item = isInItem();
            if (mNeedReturn) {
                if (mTags[mTotalLevel] == TAG_SPAN || mTags[mTotalLevel] == TAG_P
                        || mTags[mTotalLevel] == TAG_DIV || mTags[mTotalLevel] == TAG_LI) {
                    mResult += "\r\n";
                    mNeedReturn = false;
                    Log.e("add return", "for "+ mTags[mTotalLevel]);
                }
            }
            Log.i("Farther class","endElement");
        } catch (NullPointerException e) {
            Log.i("Father", "endElement NullPointerException");
            // TODO: handle exception
        }


        Util.log("endElement " + namespaceURI + " local "
         + localName + " qName " + qName);
    }

    /**
     * Gets be called on the following structure: <tag>characters</tag>
     */
    @Override
    public void characters(char ch[], int start, int length) {
        try {
            String strApend = new String(ch, start, length);

            if (length > 0 && !strApend.equals("\n")&& !strApend.equals("/")&& !strApend.equals(" ")) {
                if (in_item) {
                    mResult += strApend;
                    mNeedReturn = !strApend.substring(length - 1).equals("\n");

                    Log.v("Good", "Need return is " + mNeedReturn);
                    Log.e("add element", strApend);
                } else {
                    Log.i("no element", strApend);
                }
            }
        } catch (NullPointerException e) {
            Log.i("Father","characters null pointer");
            // TODO: handle exception
        }
    }
}
