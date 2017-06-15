package sergey.knyazev.dataparser.Data;

import android.provider.BaseColumns;

/**
 * Created by Sergey on 15.06.2017.
 */

public final class XMLItemsContract {
    private XMLItemsContract() {};

    public static final class XMLEntry implements BaseColumns {
        public final static String TABLE_NAME = "XMLItems";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "Title";
        public final static String COLUMN_LINK = "Link";
        public final static String COLUMN_DATE = "Date";
        public final static String COLUMN_TIMESTAMP = "TimeStamp";
    }
}
