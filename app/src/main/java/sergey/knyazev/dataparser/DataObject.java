package sergey.knyazev.dataparser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sergey on 14.06.2017.
 */

public class DataObject {

    private String mTitle;
    private String mLink;
    private String mDate;
    private long mTimeStamp;

    DataObject (String title, String link, String date){
        mTitle = title;
        mLink = link;
        setDate(date);

    }

    private String parseDate (String date){
        String result;

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.US);
        try {
            Date formatedDate = sdf.parse(date);
            mTimeStamp = formatedDate.getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatedDate);

            result = String.format("%1$td %1$tb %1$tY %1$tI:%1$tM %1$Tp", calendar);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";

        }
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getLink() {
        return mLink;
    }
    public void setLink(String link) {
        this.mLink = link;
    }

    public String getDate() {
        return mDate;
    }
    public void setDate(String date) {
        mDate = parseDate(date);
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

}