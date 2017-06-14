package sergey.knyazev.dataparser;

/**
 * Created by Sergey on 14.06.2017.
 */

public class DataObject {

    private String mTitle;
    private String mLink;
    private String mDate;

    DataObject (String title, String link, String date){
        mTitle = title;
        mLink = link;
        mDate = date;
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
        mDate = date;
    }

}