package sergey.knyazev.dataparser;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import sergey.knyazev.dataparser.Data.XMLItemsContract;
import sergey.knyazev.dataparser.Data.XMLItemsDbHelper;

import static sergey.knyazev.dataparser.R.attr.title;

/**
 * Created by Sergey on 15.06.2017.
 */

public class InfoUpdater extends Activity implements Runnable {
    private Thread thread;
    private boolean mIsAlive = false;
    private int i = 0;
    private int mNewMessages = 0;

    InfoUpdater(int newMessages) {
        mNewMessages = newMessages;
        start();
    }

    public void run() {
        mIsAlive = true;
        try {
            String strUrl = "https://lifehacker.ru/feed/";
            URL url = new URL(strUrl);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;

                NodeList linkList = element.getElementsByTagName("link");
                Element linkElement = (Element) linkList.item(0);
                linkList = linkElement.getChildNodes();
                String link = ((Node) linkList.item(0)).getNodeValue();


                XMLItemsDbHelper mDbHelper = new XMLItemsDbHelper(this);
                SQLiteDatabase sqldb = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                Cursor cursor = sqldb.rawQuery("select " + XMLItemsContract.XMLEntry._ID +" from " + XMLItemsContract.XMLEntry.TABLE_NAME + "  WHERE link = '" + link + "'", null);

                boolean result = false;
                if(!cursor.moveToFirst()) {
                    result = true;
                }

                if(result)
                {
                    mNewMessages = 1;
                    Log.i("Updated", "new message");
                }
                else
                {
                    Log.i("Updated", "havent messages");
                }


            }
        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);
        }
        mIsAlive = false;
    }
    public void join()
    {
        try {
            if (thread.isAlive()) {
                thread.join();
            }
        }catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean isAlive()
    {
        return mIsAlive;
    }

    public void start()
    {
        thread = new Thread(this, String.valueOf(i++));
        mIsAlive = true;
        thread.start();
    }

    public int getNewMessagesCount()
    {
        return mNewMessages;
    }
}
