package com.wedevgroup.weflyhelperfordriver.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.haibin.calendarviewproject.colorful.MainActivity;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.model.Company;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.presenter.ListActivity;
import com.wedevgroup.weflyhelperfordriver.task.ParcelleGetAllTask;
import com.wedevgroup.weflyhelperfordriver.task.ScheduleAddTask;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.NetworkWatcher;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class AllParcelsActivity extends ListActivity
{
    private int selected ;
    private Utils utils;
    private Company company;
    private final String TAG = getClass().getSimpleName();
    private CopyOnWriteArrayList<Parcelle> parcelleList = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Parcelle> pSelected = new CopyOnWriteArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcels_list);


        iniViews();
        AppController.addToDestroyList(this);

        utils = new Utils();
        watcher = new NetworkWatcher(this,rLayout );
        watcher.setOnInternetListener(this);


        company = (Company) getIntent().getSerializableExtra("compObj");

        if (company == null)
            company = new Company();


        selected = 1;
        watcher.isNetworkAvailable();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem mReset = menu.findItem(R.id.menu_reset);
        mReset.setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_eraser)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));

        MenuItem mAll = menu.findItem(R.id.menu_all);
        mAll.setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_clipboard)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));

        MenuItem mSchedule = menu.findItem(R.id.menu_schedule);
        mSchedule.setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_calendar_alt)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));


        return true;
    }


    @Override
    public void onAddError(@NonNull View view, boolean many) {
        super.onAddError(view, many);
        onDisplayUI(false,false, false, false, parcelleList, false, false, true);
    }

    @Override
    public void onAddSucces(@NonNull CopyOnWriteArrayList<Parcelle> newList, @NonNull View view) {
        super.onAddSucces(newList, view);
        onDisplayUI(false,false, false, false, parcelleList, false, true, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()){
            case R.id.menu_reset:
                //CLEAN ALL
                updateSelected();
                if (pSelected.size() > 1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AllParcelsActivity.this);
                    builder.setTitle(getString(R.string.deselec_title));
                    builder.setMessage(getString(R.string.msg_deselect));
                    builder.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // remove all points
                                    mAdapter.deselectAll();
                                    pSelected.clear();
                                }
                            });
                    builder.setNegativeButton(getString(R.string.cancel_dialog),
                            null);
                    builder.show();
                }

                break;
            case R.id.menu_all:
                mAdapter.selectAll();
                updateSelected();

                break;
            case R.id.menu_schedule:
                updateSelected();
                if (pSelected.size() > 0){
                    onDisplayUI(false, false, false, false, parcelleList, true, false, false);

                    ScheduleAddTask task = new ScheduleAddTask(pSelected, this);
                    task.setOnScheduleAddListener(this, rLayout);
                    if (appController != null)
                        appController.launchTask(task);

                }else
                    Utils.showToast(this, R.string.please_sel_parcel, rLayout);
                break;

            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }

    private void updateSelected() {
        pSelected.clear();
        pSelected.addAll(mAdapter.getItemSelected());
    }

    private void lockSend(){
        findViewById(R.id.menu_schedule).setClickable(false);
    }

    private void allowSend(){
        findViewById(R.id.menu_schedule).setClickable(false);
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
            if (parcelleList.size()> 0){
                restoreList();
            }else{
                resfreshList();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public void onLoadError() {
       onDisplayUI(false,false,true, false, parcelleList, false, false, false);
    }

    @Override
    public void onSucces(@NonNull CopyOnWriteArrayList<Parcelle> parcelles) {
        parcelleList.clear();
        for (Parcelle pm: parcelles){
            // not delete
            if (!pm.isDelete())
                parcelleList.add(pm);
        }

        if (parcelleList.size() > 0){
            restoreList();
        }else{
            onDisplayUI(true, false, false,false,parcelles, false, false,false);
        }

    }


    @Override
    public void onConnected() {
        super.onConnected();
        switch (selected){
            case 1:
                loadParcels();
                break;
            case 2:
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
        onDisplayUI(false, true,false, false, parcelleList, false, false, false);
    }

    @Override
    public void onRetry() {
        super.onRetry();
        resfreshList();

    }

    @Override
    protected void onRestart() {
        resfreshList();
        super.onRestart();
    }

    private void resfreshList() {
        if(watcher != null){
            selected = 1;
            watcher.isNetworkAvailable();

        }
    }

    private void restoreList() {
        if (parcelleList.size()> 0 && rLayout != null){
            iniListeners(parcelleList, company);
            onDisplayUI(true, false, false, false, parcelleList, false, false, false);
        }
    }

    private void loadParcels() {
        onDisplayUI(false, false, false, true, new CopyOnWriteArrayList<Parcelle>(), false, false, false);
        ParcelleGetAllTask task = new ParcelleGetAllTask(this, company.getEntrepriseId());
        task.setOnParcelleLoadingCompleteListener(this);
        if (appController != null)
            appController.launchTask(task);
    }

    public void goToMainActivity(@NonNull final Activity act){
        Intent intent = new Intent(act, MainActivity.class);
        if (appController != null){
            appController.launchActivity(intent, act,true );

        }



    }
}