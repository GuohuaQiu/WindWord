
package qiu.tool.windword;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.os.Handler;
import android.util.Log;

public class ColinsLoader extends NetworkLoader {

    // ===========================================================
    // Fields
    // ===========================================================

    public ColinsLoader() {
        // TODO Auto-generated constructor stub
    }

    public boolean isInItemOld() {
        if (mTags[3] == TAG_P) {
            if (mTotalLevel == 4) {
                return true;
            } else if (mTotalLevel > 4) {
                /* only strong and i is acceptable. */
                if (mTags[4] == TAG_I || mTags[4] == TAG_SPAN || mTags[4] == TAG_B || mTags[4] == TAG_STRONG) {
                    return true;
                }
            }
            return false;
        }

        if (mTags[3] == TAG_OL) {
            return true;
        }
        if (mTags[3] == TAG_DIV) {
            if (mTotalLevel == 4) {
                return true;
            }
        }
        return false;
    }
    public boolean isInItem() {
        if(mTotalLevel<4){
            return false;
        }
        if(mTags[0] != TAG_DIV ||mTags[1] != TAG_DIV ||mTags[2] != TAG_DIV){
            return false;
        }
        if(mTags[3] == TAG_P){
            if(mTags[4] == TAG_I) return true;
            return false;
        }
        if(mTags[3] == TAG_OL){
            if(mTags[4] == TAG_DIV || mTags[4] == TAG_LI) return true;
            return false;
        }
        return false;
    }

    /* TODO change level manage to parent class qiu simon */
}
