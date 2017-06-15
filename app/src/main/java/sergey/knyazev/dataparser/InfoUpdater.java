package sergey.knyazev.dataparser;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.NotificationCompat;
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
    private Context mContext;
    XMLItemsDbHelper mDbHelper;//= new XMLItemsDbHelper(mContext);
    SQLiteDatabase sqldb;// = mDbHelper.getWritableDatabase();

    InfoUpdater(int newMessages, Context context) {
        mNewMessages = newMessages;
        mContext = context;
        start();

    }

    public void run() {
        Log.i("thread_run", "runned");
        mIsAlive = true;
        try {
            String strUrl = "https://lifehacker.ru/feed/";
            URL url = new URL(strUrl);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");

            Context context = mContext;
            mDbHelper = new XMLItemsDbHelper(context);
            sqldb = mDbHelper.getWritableDatabase();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;

                NodeList linkList = element.getElementsByTagName("link");
                Element linkElement = (Element) linkList.item(0);
                linkList = linkElement.getChildNodes();
                String link = ((Node) linkList.item(0)).getNodeValue();

                Cursor cursor = sqldb.rawQuery("select " + XMLItemsContract.XMLEntry._ID +" from " + XMLItemsContract.XMLEntry.TABLE_NAME + "  WHERE link = '" + link + "'", null);

                boolean result = false;
                if(!cursor.moveToFirst()) {
                    result = true;
                }

                if(result)
                {
                    mNewMessages = 1;
                    sendNotif("new message");
                    Log.i("Updated", "new message");
                }
                else
                {
                    sendNotif("havent messages");
                    Log.i("Updated", "havent messages");
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mIsAlive = false;
    }

    public void sendNotif(String message) {
        Log.i("thread", "notify");
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Новости")
                        .setContentText(message);
        Intent resultIntent = new Intent(mContext, ScrollingActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(ScrollingActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(i++, mBuilder.build());
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
        Log.i("thread", "start thread");
    }

    public int getNewMessagesCount()
    {
        return mNewMessages;
    }
}
