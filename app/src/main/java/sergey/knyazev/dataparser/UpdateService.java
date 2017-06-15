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
    long interval = 5000;
    String time;
    InfoUpdater infoUpdate;
    int mNewMessages = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "service");
        timer = new Timer();
        infoUpdate = new InfoUpdater(mNewMessages);
        tTask = new TimerTask() {
            public void run() {
                if (!infoUpdate.isAlive()) {
                    infoUpdate.start();
                    infoUpdate.join();
                    if (infoUpdate.getNewMessagesCount() > 0) {
                        sendNotif();
                    }
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

    void sendNotif() {
        Log.i(LOG_TAG, "notify");
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Новости")
                        .setContentText("У вас 0 новых сообщений");
        Intent resultIntent = new Intent(this, ScrollingActivity.class);

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

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
