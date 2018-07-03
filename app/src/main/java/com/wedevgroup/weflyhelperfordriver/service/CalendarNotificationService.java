package com.wedevgroup.weflyhelperfordriver.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.haibin.calendarviewproject.colorful.MainActivity;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.activity.ParcelleDetailsActivity;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.presenter.DBService;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;
import com.wedevgroup.weflyhelperfordriver.utils.CacheImage;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.Save;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Obrina.KIMI on 1/16/2018.
 */

public class CalendarNotificationService extends DBService {
    public Thread thread;
    private int serviceId;
    private final  String TAG = getClass().getSimpleName();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // service On start
        serviceId = startId;
        thread = new Thread(new mThreadClass(serviceId, CalendarNotificationService.this));
        thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // service finished
        if(thread != null){
           onStopThread(this);
        }
        super.onDestroy();
    }

    final class mThreadClass implements Runnable{
        private int serviceId;
        private Context context;


        public mThreadClass(int serviceId, final Context ctx){
            this.serviceId = serviceId;
            this.context = ctx;
        }
        @Override
        public synchronized void run() {
            // Do staff

            try{
                Save.defaultSaveBoolean(Constants.THREAD_CALENDAR_NOTIF_IS_RUNNING, true, getBaseContext());
                // say i'm running

                Calendar cal = Calendar.getInstance();
                String currentDate = Utils.getStringDate(cal);
                String lstNotifDate = Save.defaultLoadString(Constants.LAST_NOTIFICATION_DATE, getBaseContext());


                if (!lstNotifDate.toString().contentEquals(currentDate.toString())){
                    if (isTokenValide()){
                        // TOKEN Valid
                        final CopyOnWriteArrayList<Parcelle> list = new CopyOnWriteArrayList<>();
                        final CopyOnWriteArrayList<Parcelle> notifList = new CopyOnWriteArrayList<>();

                        list.addAll(getDBManager().getParcelles());

                        if (list.size() > 0) {
                            for (Parcelle dm: list){
                                if (dm.getDateSurvol().contentEquals(currentDate))
                                    notifList.add(dm);
                            }

                            if (notifList.size() > 0){
                                // Check number
                                if (notifList.size() > 1){
                                    // Notify
                                    try {
                                        String msg = getBaseContext().getString(R.string.calendar_msg_left) + " " + notifList.size() + " " + getBaseContext().getString(R.string.calendar_msg_right);

                                        NotificationCompat.Builder builder =
                                                new NotificationCompat.Builder( getBaseContext())
                                                        .setSmallIcon(R.drawable.ic_notification_default)
                                                        .setContentTitle( getBaseContext().getString(R.string.notif_calendar_title))
                                                        .setContentText( msg)
                                                        .setAutoCancel(true);

                                        Intent targetIntent = new Intent( getBaseContext(), MainActivity.class);
                                        PendingIntent contentIntent = PendingIntent.getActivity( getBaseContext(), 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        builder.setContentIntent(contentIntent);
                                        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        nManager.notify(Constants.NOTIFICATION_SERVICE_AUTO_POST_ID, builder.build());


                                        rememberToday(currentDate);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }else {
                                    // for only one
                                    if (sendOneNotification(notifList.get(notifList.size() - 1)))
                                        rememberToday(currentDate);

                                }

                            }
                        }



                    }
                }


                // stop at the end
                stopSelf();



            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void rememberToday(@NonNull String currentDate) {
        try {
            Save.defaultSaveString(Constants.LAST_NOTIFICATION_DATE,currentDate, getBaseContext());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void onStopThread(final Context ctx){

        //Event bus unregister
        //en flag
        try{
            Save.defaultSaveBoolean(Constants.THREAD_CALENDAR_NOTIF_IS_RUNNING, false, ctx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean sendOneNotification(@NonNull Parcelle parc) throws Exception {

        Bitmap image = null;
        if (isNetworkAvailable()){
            image = Utils.resizeMapBitmap(
                    getBitmapfromUrl(parc.getImageUrl()),
                    1280, 960, this);
        }else{
            image = Utils.resizeMapBitmap(
                    Utils.drawableToBitmap(this,R.drawable.img_default_parcel),
                    1280, 960, this);
        }

        String title = getString(R.string.parcelle_num) + String.valueOf(parc.getParcelleId());
        Intent intent = new Intent(this, ParcelleDetailsActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));




        Bundle bd = new Bundle();
        bd.putSerializable("notif_parc", parc);
        intent.putExtras(bd);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.custom_notification);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_default);


        remoteViews.setImageViewBitmap(R.id.image_view_notif_item,image);
        remoteViews.setTextViewText(R.id.regionTView,parc.getRegion());
        remoteViews.setTextViewText(R.id.entrepriseTView,parc.getCompany().getName());

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(image)/*Notification icon image*/
                .setSmallIcon(R.drawable.ic_notification_default)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setCustomBigContentView(remoteViews)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            notificationManager.notify(Constants.NOTIFICATION_SERVICE_AUTO_POST_ID /* ID of notification */, notificationBuilder.build());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
