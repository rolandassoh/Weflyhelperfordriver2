package com.wedevgroup.weflyhelperfordriver.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.haibin.calendarviewproject.colorful.MainActivity;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.presenter.BaseTempActivity;
import com.wedevgroup.weflyhelperfordriver.receiver.startMyCalendarJobService;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.PermissionUtil;
import com.wedevgroup.weflyhelperfordriver.utils.Save;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;
import com.wedevgroup.weflyhelperfordriver.utils.design.KenBurnsView;

import java.util.concurrent.TimeUnit;

public class SplashScreensActivity extends BaseTempActivity {
	
	private KenBurnsView mKenBurns;
	private ImageView mLogo;
	private final String TAG = getClass().getSimpleName();
    private PermissionUtil pUtil;
    private static boolean isAllPermissionGranted = false;
    private static boolean showPermissionDialog;
    private static boolean isFirstTime = true;
    private Utils utils;
    private TextView driverText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
		setContentView(R.layout.activity_splash_screen);
		
		mKenBurns       = (KenBurnsView) findViewById(R.id.ken_burns_images);
		mLogo           = (ImageView)    findViewById(R.id.logo);
		driverText      = (TextView)     findViewById(R.id.driverText);
        utils = new Utils();
        // Listen Network change
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            scheduleJob();
        }

		mKenBurns.setImageResource(R.drawable.img_splash_screen);
		animation2();


		pUtil = new PermissionUtil(this);
		//check permission
		isAllPermissionGranted = pUtil.isAllPermissionsGranded();
		if(!isAllPermissionGranted){
			if(isFirstTime){
				showPermissionDialog = false;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							onRequestAllPermissions();
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				}, TimeUnit.SECONDS.toMillis(1));
				isFirstTime = false;
			}
		}else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    iniSetting();
                    launchActivity();
                }
            }, 4000);
        }

	}



    private void animation2() {
		mLogo.setAlpha(1.0F);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
		mLogo.startAnimation(anim);
	}


    public void onRequestAllPermissions(){
        //request Permission
        pUtil.requestAllPermissions();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(showPermissionDialog)
            pUtil.requestAllPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_APP_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    showPermissionDialog = false;
                    isAllPermissionGranted = true;
                    iniSetting();
                    launchActivity();


                } else {
                    showPermissionDialog = true;
                    // Display on Next launch
                    isFirstTime = true;
                    try{
                        pUtil.onAllPermissionError(pUtil.getPermissionsDenied());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void launchActivity() {
        if (appController != null && utils != null){
            if(appController.isTokenValide()){
                startActivity(new Intent(this, MainActivity.class));
            }else{
                startActivity(new Intent(this, LoginActivity.class));
            }
            utils.onOpenWithAnimation(this);
            finish();
        }
    }

    private void iniSetting() {
        if (!Save.defaultLoadBoolean(Constants.PREF_IS_FIRST_LAUNCH, SplashScreensActivity.this)){
            // First launch
            // remenber
            Save.defaultSaveString(Constants.LAST_NOTIFICATION_DATE, "", SplashScreensActivity.this);


            Save.defaultSaveBoolean(Constants.PREF_IS_FIRST_LAUNCH,true, SplashScreensActivity.this);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scheduleJob() {
        try {
            JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, startMyCalendarJobService.class))
                    .setRequiresCharging(false)
                    .setMinimumLatency(1000)
                    .setOverrideDeadline(3000)
                    .setPersisted(true)
                    .build();

            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(myJob);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
