package sergey.knyazev.dataparser.Data;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import sergey.knyazev.dataparser.DataObject;

/**
 * Created by Sergey on 15.06.2017.
 */

public class DataObjectComparator implements Comparator<DataObject> {
    public int compare(DataObject first, DataObject second) {
        if (first.getTimeStamp() > second.getTimeStamp()) {
            return 1;
        } else if (first.getTimeStamp() < second.getTimeStamp()) {
            return -1;
        }
        return 0;
    }
}
