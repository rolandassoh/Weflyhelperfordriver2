package com.wedevgroup.weflyhelperfordriver.receiver;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.wedevgroup.weflyhelperfordriver.service.CalendarNotificationService;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;

/**
 * Created by admin on 07/05/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class startMyCalendarJobService extends JobService {
    private final String TAG = getClass().getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
    }



    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        unregisterReceiver(broadcastReceiver);
        return true;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent mintent = new Intent(context, CalendarNotificationService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startService(mintent);
        }
    };


}