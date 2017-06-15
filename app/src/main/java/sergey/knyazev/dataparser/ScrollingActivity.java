package sergey.knyazev.dataparser;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import sergey.knyazev.dataparser.Data.XMLItemsContract;
import sergey.knyazev.dataparser.Data.XMLItemsDbHelper;

public class ScrollingActivity extends AppCompatActivity {
    private MyRecyclerViewAdapter mAdapter;
    private ArrayList<DataObject> mData;
    private XMLItemsDbHelper mDbHelper;
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ScrollingActivity.this, "Загрузка...", Toast.LENGTH_SHORT).show();
                new ProgressTask().execute(getString(R.string.BASE_URL));
            }
        });

        InitItems();
        startService(new Intent(this, UpdateService.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitItems()
    {
        mDbHelper = new XMLItemsDbHelper(this);
        mData = GetDataFromDB();
        //Items view
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i("scrolled", String.valueOf(dy));
            /*
            int visibleItemCount = layoutManager.getChildCount();//смотрим сколько элементов на экране
            int totalItemCount = layoutManager.getItemCount();//сколько всего элементов
            int firstVisibleItems = layoutManager.findFirstVisibleItemPosition();//какая позиция первого элемента

            if (!isLoading) {//проверяем, грузим мы что-то или нет, эта переменная должна быть вне класса  OnScrollListener
                if ( (visibleItemCount+firstVisibleItems) >= totalItemCount) {
                    isLoading = true;//ставим флаг что мы попросили еще элемены
                    if(loadingListener != null){
                        loadingListener.loadMoreItems(totalItemCount);//тут я использовал калбэк который просто говорит наружу что нужно еще элементов и с какой позиции начинать загрузку
                    }
                }
            }
            */
            }
        };

        mRecyclerView.setOnScrollListener(scrollListener);
        mAdapter = new MyRecyclerViewAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

    }

    private ArrayList<DataObject> GetDataFromDB() {
        ArrayList<DataObject> result = new ArrayList<DataObject>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                XMLItemsContract.XMLEntry._ID,
                XMLItemsContract.XMLEntry.COLUMN_DATE,
                XMLItemsContract.XMLEntry.COLUMN_NAME,
                XMLItemsContract.XMLEntry.COLUMN_LINK};

        String orderBy =XMLItemsContract.XMLEntry.COLUMN_TIMESTAMP + " ASC";
        Cursor cursor = db.query(
                XMLItemsContract.XMLEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                orderBy);

        try {
            int idColumnIndex =             cursor.getColumnIndex(XMLItemsContract.XMLEntry._ID);
            int titleColumnIndex =           cursor.getColumnIndex(XMLItemsContract.XMLEntry.COLUMN_NAME);
            int linkColumnIndex =    cursor.getColumnIndex(XMLItemsContract.XMLEntry.COLUMN_LINK);
            int dateColumnIndex =           cursor.getColumnIndex(XMLItemsContract.XMLEntry.COLUMN_DATE);


            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);

                String currentTitle = cursor.getString(titleColumnIndex);
                String currentLink = cursor.getString(linkColumnIndex);
                String currentDate = cursor.getString(dateColumnIndex);
                Log.i("Info", currentTitle + " " + currentLink);
                DataObject obj = new DataObject(currentTitle, currentLink, currentDate);
                result.add(obj);
            }
        } finally {
            cursor.close();
        }
        return result;
    }


    public class ProgressTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

            String content = "";
            try {
                content = getContent(path[0]);
            } catch (IOException ex) {
                content = ex.getMessage();
            } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
            } catch (SAXException e1) {
                e1.printStackTrace();
            }

            return content;

        }

        @Override
        protected void onPostExecute(String content) {
            Toast.makeText(ScrollingActivity.this, "Данные загружены", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mAdapter.updateData(mData);

            //loadedText.setText(content);
            /*URL url = new URL("http://image10.bizrate-images.com/resize?sq=60&uid=2216744464");
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(bmp);
            */

        }

        private boolean InsertObj(String title, String link, String date) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            DataObject obj = new DataObject(title, link, date);
            values.put(XMLItemsContract.XMLEntry.COLUMN_NAME, title);
            values.put(XMLItemsContract.XMLEntry.COLUMN_LINK, link);
            values.put(XMLItemsContract.XMLEntry.COLUMN_DATE, date);
            values.put(XMLItemsContract.XMLEntry.COLUMN_TIMESTAMP, obj.getTimeStamp());


            Cursor cursor = db.rawQuery("select " + XMLItemsContract.XMLEntry._ID +" from " + XMLItemsContract.XMLEntry.TABLE_NAME + "  WHERE link = '" + link + "'", null);

            boolean result = false;
            if(!cursor.moveToFirst()) {
                long id = db.insert(XMLItemsContract.XMLEntry.TABLE_NAME, null, values);
                result = true;
            }
            return result;
        }

        public String getContent(String path) throws IOException, ParserConfigurationException, SAXException {
              ArrayList<DataObject> result = new ArrayList<DataObject>();
            try {
                URL url = new URL(path);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

                NodeList nodeList = doc.getElementsByTagName("item");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Log.i("Info_i", String.valueOf(i));
                    Node node = nodeList.item(i);
                    Element element = (Element) node;

                    NodeList titleList = element.getElementsByTagName("title");
                    Element titleElement = (Element) titleList.item(0);
                    titleList = titleElement.getChildNodes();
                    String title = ((Node) titleList.item(0)).getNodeValue();

                    NodeList dateList = element.getElementsByTagName("pubDate");
                    Element dateElement = (Element) dateList.item(0);
                    dateList = dateElement.getChildNodes();
                    String date = ((Node) dateList.item(0)).getNodeValue();

                    NodeList linkList = element.getElementsByTagName("link");
                    Element linkElement = (Element) linkList.item(0);
                    linkList = linkElement.getChildNodes();
                    String link = ((Node) linkList.item(0)).getNodeValue();

                    if(InsertObj(title, link, date))
                    {
                        notify = true;
                        DataObject obj = new DataObject(title, link, date);
                        mData.add(obj);
                    }

                }
            } catch (Exception e) {
                System.out.println("XML Pasing Excpetion = " + e);
            }

            return "";
        }
    }
    //from https://stackoverflow.com/questions/12065951/how-can-i-parse-xml-from-url-in-android
}
