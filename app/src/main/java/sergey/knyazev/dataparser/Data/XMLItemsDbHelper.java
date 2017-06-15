package sergey.knyazev.dataparser.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sergey on 15.06.2017.
 */

public class XMLItemsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME =  "XMLItems.db";
    private static final int DATABASE_VERSION = 5;
    public XMLItemsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE " + XMLItemsContract.XMLEntry.TABLE_NAME + " ("
                + XMLItemsContract.XMLEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + XMLItemsContract.XMLEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + XMLItemsContract.XMLEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + XMLItemsContract.XMLEntry.COLUMN_LINK + " TEXT NOT NULL, "
                + XMLItemsContract.XMLEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL); ";

        db.execSQL(SQL_CREATE_GUESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + XMLItemsContract.XMLEntry.TABLE_NAME);
        onCreate(db);
    }
}
