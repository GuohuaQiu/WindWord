package qiu.tool.windword;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.util.Log;



public class EngInterLoader extends NetworkLoader {
    private boolean in_inter = false;
    private int mIndex;


    @Override
    public boolean isInItem() {
        if(mTotalLevel == 0){
            return false;
        }
        if (mTags[mTotalLevel-1] == TAG_LI ||mTags[mTotalLevel-1] == TAG_STRONG||mTags[mTotalLevel-1] == TAG_EM ) {
            return true;
        }
        return false;
    }
    /**
     * Gets be called on opening tags like: <tag> Can provide attribute(s), when
     * xml was like: <tag attribute="attributeValue">
     */
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        super.startElement(namespaceURI, localName, qName, atts);
        try {
            if(mTags[mTotalLevel-1]==TAG_OL){
                in_inter = true;
                mIndex = 1;
            } else if(mTags[mTotalLevel-1]==TAG_LI){
                if (in_inter) {
                    mResult += mIndex + ". ";
                    mIndex++;
                }
            }


        } catch (NullPointerException e) {
            Log.i("Sorry","startElement error.");
            // TODO: handle exception
        }
    }

    /**
     * Gets be called on closing tags like: </tag>
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        super.endElement(namespaceURI, localName, qName);
        Log.i("eng endElement", "lelel is "+mTotalLevel);
        if(mTags[mTotalLevel]==TAG_OL){
            in_inter = false;
        }
        Log.i("Eng Sub class","endElement");
    }


}
