package com.haibin.calendarviewproject.colorful;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarviewproject.base.activity.BaseActivity;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.victor.loading.newton.NewtonCradleLoading;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.activity.AboutActivity;
import com.wedevgroup.weflyhelperfordriver.activity.CompanyActivity;
import com.wedevgroup.weflyhelperfordriver.activity.LoginActivity;
import com.wedevgroup.weflyhelperfordriver.activity.ParcelleDetailsActivity;
import com.wedevgroup.weflyhelperfordriver.activity.ProfileActivity;
import com.wedevgroup.weflyhelperfordriver.adapter.ParcelleImgViewAdapter;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.presenter.SettingPresenter;
import com.wedevgroup.weflyhelperfordriver.task.CheckUpdateTask;
import com.wedevgroup.weflyhelperfordriver.task.RecoveryTask;
import com.wedevgroup.weflyhelperfordriver.task.ScheduleCancelTask;
import com.wedevgroup.weflyhelperfordriver.task.ScheduleEditTask;
import com.wedevgroup.weflyhelperfordriver.task.ScheduleJobDoneTask;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.NetworkWatcher;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class MainActivity extends BaseActivity implements
        CalendarView.OnDateSelectedListener,
        CalendarView.OnYearChangeListener,
        CalendarView.OnMonthChangeListener,
        GuillotineListener,
        RecoveryTask.OnRecoveryCompleteListener,
        View.OnClickListener {

    TextView mTextMonthDay;


    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;
     RecyclerView recyclerView;
     RecyclerView.LayoutManager layoutManager;
     ParcelleImgViewAdapter adapter;


    FrameLayout root;
    Toolbar toolbar;
    View contentHamburger;
    View guillotineMenu;
    private ImageView imgProfile, imgHelp, imgAbout, imgLogout, imgRecovery;
    private Utils utils;
    private int selected;
    private boolean shouldUpdate;
    private LinearLayout liLoading;
    private boolean guiloIsOpen;
    GuillotineAnimation.GuillotineBuilder builder;
    View view;
    FrameLayout fLayout;
    private NewtonCradleLoading loadingBar;

    private RelativeLayout rLayout;
    private Parcelle pSelect = null;
    private LinearLayout liProfile, liHelp, liLogout, liRecovery, liNoSchedule, liBg, liAbout;

    private final String TAG = getClass().getSimpleName();
    private boolean isUpdateChecked;
    private SettingPresenter pLang;
    boolean doNothingLang = false;
    int newDay;
    StickySwitch swLang;


    private CopyOnWriteArrayList<Parcelle> parcelleList = new CopyOnWriteArrayList<>() , searchedArray ;

    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        utils = new Utils();


        rLayout             = (RelativeLayout) findViewById(R.id.rLayout);
        liLoading           = (LinearLayout)   findViewById(R.id.liLoading);
        loadingBar          = (NewtonCradleLoading) findViewById(R.id.loadingBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        pLang = new SettingPresenter(this);
        pLang.setOnSettingChangeListener(this);


        watcher = new NetworkWatcher(this,rLayout );
        watcher.setOnInternetListener(this);



        iniCalendar();
        iniHamburgerMenu();
        iniListener();

        loadParcels();


        //check update

        selected = 6;
        watcher.isNetworkAvailable();
    }

    private void checkUpdate() {
        //Check out new version
        if (!isUpdateChecked){
            CheckUpdateTask updateTask = new CheckUpdateTask();
            updateTask.setOnUpdateCheckCompleteListener(new CheckUpdateTask.OnUpdateCheckCompleteListener() {
                @Override
                public void onDownloadError(@NonNull String errorMsg) {

                }

                @Override
                public void onDownloadSucces(@NonNull JSONObject response) {
                    try {
                        int versionOnServ   = 0;
                        String msg          =  "";
                        versionOnServ       = response
                                .getInt("version");
                        msg       = response
                                .getString("msg_driver");
                        //check app Version
                        final String appId = getPackageName();
                        if(versionOnServ != 0 && versionOnServ > Constants.PREF_APP_VERSION){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(getString(R.string.version_title));
                            builder.setMessage(getString(R.string.version_msg )+ " :" + msg);
                            builder.setPositiveButton(getString(R.string.update),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // go to play Store
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse("market://details?id=" + appId
                                                        )));
                                            } catch (Exception anfe) {
                                                try {
                                                    startActivity(new Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse("http://play.google.com/store/apps/details?id=" +appId
                                                            )));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                            builder.setNegativeButton(getString(R.string.cancel),
                                    null);
                            builder.show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            updateTask.execute();
            isUpdateChecked = true;
        }
    }


    private void iniListener() {
        liProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // profile
                if(appController != null)
                    appController.launchActivity(new Intent(MainActivity.this, ProfileActivity.class), MainActivity.this, true);
            }
        });

        liLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                if (appController != null){
                    appController.cleanToken(MainActivity.this);
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    utils.animActivityOpen(MainActivity.this);
                }


            }
        });

        liHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // video help
                // show Help
                if (isAllPermissionGranted()){
                    // check if file existe
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), (getString(R.string.download_title) + Constants.FILE_EXTENSION));
                    if(file.exists()){
                        // file Not Exist
                        String msg = getString(R.string.help_msg_again_left) + " "  + getString(R.string.download_title) +" " + getString(R.string.help_msg_again_right);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(getString(R.string.help_title));
                        builder.setMessage(msg);
                        builder.setPositiveButton(getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        // Download Video
                                        String url = Constants.VIDEO_TUTO_URL;
                                        String fileExtention = Constants.FILE_EXTENSION;
                                        String fileName = getString(R.string.download_title);
                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                                        request.setDescription(getString(R.string.download_msg));
                                        request.setTitle(fileName);
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.allowScanningByMediaScanner();
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + fileExtention);
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                                DownloadManager.Request.NETWORK_MOBILE);
                                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        try {
                                            manager.enqueue(request);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        builder.setNegativeButton(getString(R.string.cancel_dialog),
                                null);
                        builder.show();

                    }else {
                        // file Not Exist
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(getString(R.string.help_title));
                        builder.setMessage(getString(R.string.help_msg));
                        builder.setPositiveButton(getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        // Download Video
                                        String url = Constants.VIDEO_TUTO_URL;
                                        String fileExtention = Constants.FILE_EXTENSION;
                                        String fileName = getString(R.string.download_title);
                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                                        request.setDescription(getString(R.string.download_msg));
                                        request.setTitle(fileName);
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.allowScanningByMediaScanner();
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + fileExtention);
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                                DownloadManager.Request.NETWORK_MOBILE);
                                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        try {
                                            manager.enqueue(request);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        builder.setNegativeButton(getString(R.string.cancel_dialog),
                                null);
                        builder.show();
                    }

                }
            }
        });

        liRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Rebuild DB
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.recov_title));
                builder.setMessage(getString(R.string.recov_msg));
                builder.setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // remove all points
                                selected = 5;
                                if (watcher != null)
                                    watcher.isNetworkAvailable();

                            }
                        });
                builder.setNegativeButton(getString(R.string.cancel_dialog),
                        null);
                builder.show();
            }
        });

        liAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(appController != null)
                    appController.launchActivity(new Intent(MainActivity.this, AboutActivity.class), MainActivity.this, true);
            }
        });


        // Set Selected Change Listener
        swLang = (StickySwitch) findViewById(R.id.langSwitch);
        swLang.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull final String text) {
                if (!doNothingLang){
                    try {
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (text.toString().trim().contentEquals(getString(R.string.french))){
                                        pLang.setLanguage(Constants.LANGUAGE_FRENCH);
                                    }else{
                                        pLang.setLanguage(Constants.LANGUAGE_ENGLISH);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        },610);
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }


            }
        });

        //ini lang
        doNothingLang = true;
        if (pLang.getLanguage().toString().trim().contentEquals(Constants.LANGUAGE_ENGLISH)){
            swLang.setDirection(StickySwitch.Direction.LEFT);
        }else {
            swLang.setDirection(StickySwitch.Direction.RIGHT);
        }

        doNothingLang = false;


    }

    private void iniListView(@NonNull final  CopyOnWriteArrayList<Parcelle> list) {
        adapter  =  new ParcelleImgViewAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    private void iniHamburgerMenu() {
        root                            = (FrameLayout) findViewById(R.id.root);
        contentHamburger                = (View)        findViewById(R.id.content_hamburger);

        guillotineMenu                 = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);
        toolbar                         =           (Toolbar)   findViewById(R.id.toolbar);

        imgProfile                      =           (ImageView) guillotineMenu.findViewById(R.id.profileImg);
        imgHelp                         =           (ImageView) guillotineMenu.findViewById(R.id.helpImg);
        imgAbout                        =           (ImageView) guillotineMenu.findViewById(R.id.aboutImg);
        imgLogout                       =           (ImageView) guillotineMenu.findViewById(R.id.logoutImg);
        imgRecovery                     =           (ImageView) guillotineMenu.findViewById(R.id.recoveryImg);

        liProfile                       =           (LinearLayout) guillotineMenu.findViewById(R.id.liProfile);
        liHelp                          =           (LinearLayout) guillotineMenu.findViewById(R.id.liHelp);
        liLogout                        =           (LinearLayout) guillotineMenu.findViewById(R.id.liLogout);
        liRecovery                      =           (LinearLayout) guillotineMenu.findViewById(R.id.liRecovery);
        liAbout                         =           (LinearLayout) guillotineMenu.findViewById(R.id.liAbout);
        liNoSchedule                    =           (LinearLayout) findViewById(R.id.liNoSchedule);
        liBg                            =           (LinearLayout) findViewById(R.id.liMainDontUse);
        view                            =           (View) findViewById(R.id.view);

        fLayout                         =           (FrameLayout) findViewById(R.id.root);


        imgProfile.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_user2)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));

        imgHelp.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_question_circle2)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));

        imgAbout.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_info_circle)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));

        imgLogout.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_sign_out_alt)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));

        imgRecovery.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_medkit)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));



        builder = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger);
        builder.setStartDelay(Constants.RIPPLE_DURATION);
        builder.setActionBarViewForAnimation(toolbar);
        builder.setClosedOnStart(true);
        builder.setGuillotineListener(this);
        builder.build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // AppController.clearAsynTask(); auto by super

        if (guiloIsOpen){
            //Close Guillotine MENU
            if (builder != null){
                findViewById(R.id.guillotine_hamburger).performClick();
            }else{
                closeApp();

            }
        }else
            closeApp();



    }

    private void closeApp() {
        removeAsynTask();
        AppController.clearDestroyList();
        this.finish();
        utils.onFinishWithAnimation(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Constants.STATE_PARCELS_LIST, parcelleList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            parcelleList.clear();
            parcelleList.addAll((ArrayList<Parcelle>) savedInstanceState.getSerializable(Constants.STATE_PARCELS_LIST));
            showThisMonthParc(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void iniCalendar() {
        mTextMonthDay = (TextView) findViewById(R.id.tv_month_day);
        mRelativeTool = (RelativeLayout) findViewById(R.id.rl_tool);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mTextCurrentDay = (TextView) findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarView.showYearSelectLayout(mYear);
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView.setOnDateSelectedListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mYear = mCalendarView.getCurYear();
        //mTextMonthDay.setText(mCalendarView.getCurMonth() + "getCurMonth()" + mCalendarView.getCurDay() + "getCurDay()");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        mCalendarView.setOnDateLongClickListener(new CalendarView.OnDateLongClickListener() {
            @Override
            public void onDateLongClick(Calendar calendar) {
                if (isBadDate(calendar)){
                    Utils.showToast(MainActivity.this, R.string.select_err, rLayout);
                }else {
                    shouldUpdate = false;
                    Intent intent = new Intent(MainActivity.this, CompanyActivity.class);
                    if(appController!= null)
                        appController.launchActivity(intent, MainActivity.this, true);

                }


            }
        });


    }

    private boolean isBadDate(@NonNull final Calendar calendar) {

        // Not Late
        java.util.Calendar day = java.util.Calendar.getInstance();
        day.set(calendar.getYear(), getMonth(calendar.getMonth()), calendar.getDay());
        java.util.Calendar today = java.util.Calendar.getInstance();
        long rNowTime = today.getTimeInMillis();
        long dayTime = day.getTimeInMillis();

        return (rNowTime > dayTime);

    }

    private boolean isFuturedDate(@NonNull final Calendar calendar) {


        // Not Late
        java.util.Calendar day = java.util.Calendar.getInstance();
        day.set(calendar.getYear(), getMonth(calendar.getMonth()), calendar.getDay());
        java.util.Calendar today = java.util.Calendar.getInstance();
        long rNowTime = today.getTimeInMillis();
        long dayTime = day.getTimeInMillis();

        return rNowTime > dayTime;

    }

    @Override
    protected void initData() {

        if (mCalendarView != null){
            showThisMonthParc(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay());
        }



    }

    public CopyOnWriteArrayList<Parcelle> getParcellesCust(){
        CopyOnWriteArrayList<Parcelle> list = new CopyOnWriteArrayList<>();
        list.addAll(getParcelles());
        return list;
    }

    protected Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    @Override
    public void onCancelSucces(@NonNull CopyOnWriteArrayList<Parcelle> newList, @NonNull View view) {
        super.onCancelSucces(newList, view);
        resetUI(pSelect); // remove parcel
        onDisplayUI(false);
        Utils.showToast(this, R.string.cancelled, rLayout);

    }

    @Override
    public void onJobDoneSucces(@NonNull CopyOnWriteArrayList<Parcelle> newList, @NonNull View view) {
        super.onJobDoneSucces(newList, view);
        resetUI(pSelect); // remove parcel
        onDisplayUI(false);
        Utils.showToast(this, R.string.job_done, rLayout);
    }

    @Override
    public void onEditSucces(@NonNull CopyOnWriteArrayList<Parcelle> newList, @NonNull View view) {
        super.onEditSucces(newList, view);
        shouldUpdate = false;
        resetUI(pSelect);
        onDisplayUI(false);
        Utils.showToast(this, R.string.updated, rLayout);
    }

    @Override
    public void onRecoverySucces(@NonNull CopyOnWriteArrayList<Parcelle> parcelles) {
        onDisplayUI(false);
        resetUI(new Parcelle());
        Utils.showToast(this, R.string.recov_done, rLayout);
    }


    @Override
    public void onJobDoneError(@NonNull View view, boolean many) {
        super.onJobDoneError(view, many);
        onDisplayUI(false);
        Utils.showToast(this, R.string.job_done_f, rLayout);
    }

    @Override
    public void onCancelError(@NonNull View view, boolean many) {
        super.onCancelError(view, many);
        onDisplayUI(false);
        Utils.showToast(this, R.string.cancelled_f, rLayout);
    }

    @Override
    public void onEditError(@NonNull View view, boolean many) {
        super.onEditError(view, many);
        shouldUpdate = false;
        onDisplayUI(false);
        Utils.showToast(this, R.string.updated_f, rLayout);
    }

    @Override
    public void onRecoveryError() {
        onDisplayUI(false);
        Utils.showToast(this, R.string.recov_err, rLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // RECYCLER VIEW On Clik Item
//            case R.id.ll_colorful:
//                MainActivity.show(this);
//                break;
//            case R.id.ll_index:
//                break;
//            case R.id.ll_tab:
//                break;
//            case R.id.ll_solar_system:
//                break;
//            case R.id.ll_custom:
//                CustomActivity.show(this);
//                break;
        }
    }




    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSelected(final Calendar calendar, boolean isClick) {

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(calendar.getYear(), getMonth(calendar.getMonth()), calendar.getDay());
        final String selectDate = Utils.getStringDate(cal);

        AppController.setFlyYear(calendar.getYear());
        AppController.setFlyMonth(calendar.getMonth());
        AppController.setFlyDay(calendar.getDay());
        AppController.setDateSelected(selectDate);



        mTextMonthDay.setText(selectDate);
        mYear = calendar.getYear();
        //mTextCurrentDay.setText(calendar.getDay());
        // Display selected parcelle

        // Refresh UI
        showThisMonthParc(calendar.getYear(), calendar.getMonth(), calendar.getDay());
        if (shouldUpdate && pSelect != null){
            if (pSelect.getDateSurvol().contentEquals(selectDate))
                Utils.showToast(this, R.string.please_change, rLayout);
            else {
                if (!isFuturedDate(calendar)){
                    String msg = getString(R.string.up_msg_left) + " "+ calendar.getDay() +" "+ getString(R.string.up_msg_right) + " " + pSelect.getParcelleId();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(getString(R.string.up_title));
                    builder.setMessage(msg);
                    builder.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // remove all points
                                    try {
                                        newDay = calendar.getDay();
                                        selected = 2;
                                        pSelect.setDateSurvol(selectDate);
                                        if (watcher != null)
                                            watcher.isNetworkAvailable();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }


                                }
                            });
                    builder.setNegativeButton(getString(R.string.cancel_dialog),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.show();
                }else
                    Utils.showToast(MainActivity.this, R.string.select_err, rLayout);

            }

        }
    }


    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }

    @Override
    public void onMonthChange(int year, int month) {
        showThisMonthParc(year, month, mCalendarView.getCurDay());
    }


    private void launchTask() {
        if (watcher != null)
            watcher.isNetworkAvailable();
    }


    @Override
    public void onConnected() {
        super.onConnected();
        switch (selected){
            case 1:
                //info
                if (pSelect != null){
                    Intent edIntent = new Intent(MainActivity.this, ParcelleDetailsActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("parcelObj", pSelect);
                    edIntent.putExtras(b);
                    appController.launchActivity(edIntent, MainActivity.this, true);
                }
                break;
            case 2:
                // update
                if (pSelect != null){
                    CopyOnWriteArrayList<Parcelle> list = new CopyOnWriteArrayList<>();
                    list.add(pSelect);
                    ScheduleEditTask task = new ScheduleEditTask(list, MainActivity.this);
                    task.setOnScheduleEditListener(this, rLayout);
                    if (appController != null){
                        onDisplayUI(true);
                        appController.launchTask(task);
                    }

                }
                break;
            case 3:
                // delete
                if (pSelect != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(getString(R.string.delete_title));
                    builder.setMessage(getString(R.string.msg_delete));
                    builder.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // remove all points
                                    try {
                                        if (pSelect != null){
                                            CopyOnWriteArrayList<Parcelle> list = new CopyOnWriteArrayList<>();
                                            list.add(pSelect);
                                            ScheduleCancelTask task = new ScheduleCancelTask(list, MainActivity.this);
                                            task.setOnParcelleSendListener(MainActivity.this, rLayout);
                                            if (appController != null){
                                                onDisplayUI(true);
                                                appController.launchTask(task);
                                            }
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }


                                }
                            });
                    builder.setNegativeButton(getString(R.string.cancel_dialog),
                            null);
                    builder.show();
                }

                break;
            case 4:
                // Say Done
                if (pSelect != null){
                    CopyOnWriteArrayList<Parcelle> list = new CopyOnWriteArrayList<>();
                    list.add(pSelect);
                    ScheduleJobDoneTask task = new ScheduleJobDoneTask(list, MainActivity.this);
                    task.setOnScheduleJobDoneListener(this, rLayout);
                    if (appController != null){
                        onDisplayUI(true);
                        appController.launchTask(task);
                    }

                }
                break;

            case 5:
                // Recovery Mode
                if (appController != null){
                    RecoveryTask task = new RecoveryTask(MainActivity.this, appController.getUserId());
                    task.setOnRecoveryCompleteListener(this);
                    if (appController != null){
                        onDisplayUI(true);
                        appController.launchTask(task);
                    }
                }
                break;

            case 6:
                // Version
                checkUpdate();
                break;
            default:
                break;
        }
        // reset
        selected = 0;

    }

    @Override
    public void onNotConnected() {
        super.onNotConnected();
        Utils.showToast(this, R.string.error_no_internet, rLayout);
    }

    private void onDisplayUI(boolean isLoading) {

        if (isLoading){
            if (guiloIsOpen){
                findViewById(R.id.guillotine_hamburger).performClick();

            }

            fLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bghome));

            rLayout.setVisibility(View.GONE);
            liLoading.setVisibility(View.VISIBLE);
            loadingBar.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            liBg.setVisibility(View.GONE);
            loadingBar.start();
        }else {

            fLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            view.setVisibility(View.VISIBLE);
            liBg.setVisibility(View.VISIBLE);
            liLoading.setVisibility(View.GONE);
            loadingBar.stop();
            rLayout.setVisibility(View.VISIBLE);
        }



    }

    private void matchFound(boolean isNotEmpty){
        if (isNotEmpty){
            recyclerView.setVisibility(View.VISIBLE);
            liNoSchedule.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.GONE);
            liNoSchedule.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRetry() {
        super.onRetry();
        if (watcher != null){
            watcher.isNetworkAvailable();
        }

    }

    @Override
    public void onGuillotineOpened() {
        guiloIsOpen = true;
    }

    @Override
    public void onGuillotineClosed() {
        guiloIsOpen = false;
    }

    @Override
    public void onLanguageChanged(@NonNull String newLang) {
        super.onLanguageChanged(newLang);
        recreate();
    }


    private void loadParcels(){
        parcelleList.clear();
        parcelleList.addAll(getParcellesCust());



    }

    private void showThisMonthParc(int year, int month, int day){
        if (parcelleList.size() > 0){
            // short by year
            try {
                CopyOnWriteArrayList<Parcelle> listSameYear = new CopyOnWriteArrayList<>();
                CopyOnWriteArrayList<Parcelle> listSameMonth = new CopyOnWriteArrayList<>();
                List<Calendar> schemes = new ArrayList<>();
                CopyOnWriteArrayList<Parcelle> listForScroll = new CopyOnWriteArrayList<>();
                for (Parcelle pYear : parcelleList){
                    if (pYear.getFlYear() == year)
                        listSameYear.add(pYear);

                }

                if (listSameYear.size() > 0){

                    for (Parcelle pMonth : listSameYear)
                        if (pMonth.getFlMonth() == month)
                            listSameMonth.add(pMonth);

                    if (listSameMonth.size() > 0){
                        // Update everyOne


                        // Calendar

                        for (Parcelle pDay: listSameMonth){
                            // Draw in Calendar
                            int currentDate = pDay.getFlDay();

                            String pId = getString(R.string.num)+pDay.getParcelleId();
                            schemes.add(getSchemeCalendar(year, month, currentDate, 0xFF40db25, pId));
                            // Update RecyclerView
                            if (pDay.getFlDay() == day){
                                listForScroll.add(pDay);
                            }



                        }
                        // Update calendar
                        mCalendarView.clearSchemeDate();
                        mCalendarView.setSchemeDate(schemes);

//        schemes.add(getSchemeCalendar(year, month, 3, 0xFF40db25, "schemes.add 1"));
//        schemes.add(getSchemeCalendar(year, month, 6, 0xFFe69138, "schemes.add 2"));
//        schemes.add(getSchemeCalendar(year, month, 9, 0xFFdf1356, "schemes.add 3"));
//        schemes.add(getSchemeCalendar(year, month, 13, 0xFFedc56d, "schemes.add 4"));
//        schemes.add(getSchemeCalendar(year, month, 14, 0xFFedc56d, "schemes.add 5"));
//        schemes.add(getSchemeCalendar(year, month, 15, 0xFFaacc44, "schemes.add 6"));
//        schemes.add(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "schemes.add 7"));
                        // update listView
                        if (listForScroll.size() > 0){
                            iniListView(listForScroll);
                            matchFound(true);
                        }
                        else{
                            matchFound(false);
                        }



                    }else{
                        matchFound(false);

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            matchFound(false);
        }

    }

    public void showDialog(@NonNull final Parcelle p){
        pSelect = p;
        Holder holder = new ViewHolder(R.layout.content);
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(holder)
                .setHeader(R.layout.header)
                .setCancelable(true)
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialogPlus, View v) {
                        switch (v.getId()){
                            case R.id.itLCancel:
                                // delete
                                selected = 3;
                                launchTask();
                                dialogPlus.dismiss();
                                break;
                            case R.id.itLinfo:
                                //info
                                selected = 1;
                                launchTask();
                                dialogPlus.dismiss();
                                break;

                            case R.id.itLiSchDone:
                                // job Done
                                selected = 4;
                                launchTask();
                                dialogPlus.dismiss();
                                break;

                            case R.id.itLUpdate:
                                // update
                                shouldUpdate = true;
                                //selected = 2; --> by Dialog
                                Utils.showToast(MainActivity.this, R.string.msg_update, rLayout);
                                dialogPlus.dismiss();
                                break;

                        }
                    }
                })
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override public void onItemClick(DialogPlus dialog, Object item, View v, int position) {

                    }
                })
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialogPlus) {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogPlus dialogPlus) {

                    }
                })
                .create();
//        Button btnNo = (Button) dialog.findViewById(R.id.noButton);
//        Button btnYes = (Button) dialog.findViewById(R.id.yesButton);
//        TextView msg = (TextView) dialog.findViewById(R.id.msg);
//        TextView title = (TextView) dialog.findViewById(R.id.title);
//        title.setText(getString(R.string.permission_alert_title));
//        msg.setText(getString(R.string.alert_msg_granted));
//        btnNo.setText(ctx.getString(R.string.no));
//        btnYes.setText(ctx.getString(R.string.yes));

        dialog.show();
    }


    private void resetUI(@NonNull final Parcelle p){
        loadParcels();
        if (mCalendarView != null){
            // remove color
            final Calendar calendar = new Calendar();
            calendar.setDay(p.getFlDay());
            calendar.setMonth(p.getFlMonth());
            calendar.setYear(p.getFlYear());

            mCalendarView.removeSchemeDate(calendar);
            showThisMonthParc(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay());

            onDateSelected(mCalendarView.getSelectedCalendar(),true);


        }

    }
    private int getMonth(int month){
        return  month - 1;

    }




}
