package sergey.knyazev.dataparser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.R.attr.name;

public class UpdateService extends Service {

    final String LOG_TAG = "Service log:";
    int i = 0;
    Timer timer;
    TimerTask tTask;
    long interval = 2000;
    String time;
    InfoUpdater infoUpdate;
    int mNewMessages = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "service");
        timer = new Timer();
        infoUpdate = new InfoUpdater(mNewMessages, getApplicationContext());
        tTask = new TimerTask() {
            public void run() {
                Log.i("timer task", "started");
                if (!infoUpdate.isAlive()) {
                    infoUpdate.start();
                    infoUpdate.join();
                }
            }
        };

        timer.schedule(tTask, interval, interval);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        interval = 0;
        timer.cancel();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        time = intent.getStringExtra("time");
        return START_REDELIVER_INTENT;
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
