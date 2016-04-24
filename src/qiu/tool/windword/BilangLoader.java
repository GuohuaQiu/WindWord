package qiu.tool.windword;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;



import android.util.Log;

public class BilangLoader extends NetworkLoader {
    // ===========================================================
    // Fields
    // ===========================================================

    public BilangLoader() {
        // TODO Auto-generated constructor stub
    }

    public boolean isInItem() {
        if (mTags[1] == TAG_LI) {
            if (mTotalLevel == 2) {
                return true;
            } else if (mTotalLevel == 3) {
                /* only strong and i is acceptable. */
                if (mTags[2] == TAG_DIV || mTags[2] == TAG_B) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
