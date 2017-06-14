package sergey.knyazev.dataparser;

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

public class ScrollingActivity extends AppCompatActivity {
    private MyRecyclerViewAdapter mAdapter;
    private ArrayList<DataObject> mData;

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
                new ProgressTask().execute("https://lifehacker.ru/feed/");
            }
        });

        InitItems();
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
        mData = new ArrayList<DataObject>();

        mAdapter = new MyRecyclerViewAdapter(mData);

        //Items view
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

    }


    private class ProgressTask extends AsyncTask<String, Void, String> {
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

        private String getContent(String path) throws IOException, ParserConfigurationException, SAXException {
            /*BufferedReader reader=null;
            try {
                URL url = new URL(path);
                HttpsURLConnection c=(HttpsURLConnection)url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
                c.connect();
                reader= new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line = null;
                while ((line=reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                return(buf.toString());
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }*/
            ArrayList<DataObject> result = new ArrayList<DataObject>();
            try {
                URL url = new URL(path);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

                NodeList nodeList = doc.getElementsByTagName("item");

                ArrayList<DataObject> items = new ArrayList<DataObject>();
                Log.i("Info_count", String.valueOf(nodeList.getLength()));
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Log.i("Info_i", String.valueOf(i));
                    Node node = nodeList.item(i);
                    Element element = (Element) node;

                    NodeList titleList = element.getElementsByTagName("title");
                    Element titleElement = (Element) titleList.item(0);
                    titleList = titleElement.getChildNodes();
                    String title = ((Node) titleList.item(0)).getNodeValue();
                    Log.i("Info_title", title);

                    NodeList dateList = element.getElementsByTagName("pubDate");
                    Element dateElement = (Element) dateList.item(0);

                    dateList = dateElement.getChildNodes();
                    String date = ((Node) dateList.item(0)).getNodeValue();

                    DataObject obj = new DataObject(title, "", date);
                    mData.add(obj);
                }
            } catch (Exception e) {
                System.out.println("XML Pasing Excpetion = " + e);
            }

            return "";
        }
    }
    //from https://stackoverflow.com/questions/12065951/how-can-i-parse-xml-from-url-in-android
}
