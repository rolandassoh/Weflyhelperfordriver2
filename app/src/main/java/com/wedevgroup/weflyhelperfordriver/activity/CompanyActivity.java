package com.wedevgroup.weflyhelperfordriver.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.victor.loading.newton.NewtonCradleLoading;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.adapter.CompanyAdapter;
import com.wedevgroup.weflyhelperfordriver.model.Company;
import com.wedevgroup.weflyhelperfordriver.presenter.DBActivity;
import com.wedevgroup.weflyhelperfordriver.task.CompanyGetAllTask;
import com.wedevgroup.weflyhelperfordriver.task.ScheduleAddTask;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.NetworkWatcher;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;

import java.util.concurrent.CopyOnWriteArrayList;

public class CompanyActivity extends DBActivity implements CompanyGetAllTask.OnCompanyLoadingCompleteListener {
    private int selected ;
    private Utils utils;
    private RelativeLayout rLayout;
    private Toolbar mToolbar;
    private final String TAG = getClass().getSimpleName();
    private SearchView searchView;

    protected LinearLayout liNoNetwork;
    protected LinearLayout liNoCompany, liServerError, liLoading;
    protected NewtonCradleLoading loadingBar;
    private CopyOnWriteArrayList<Company> companyList = new CopyOnWriteArrayList<>(), searchedArray;
    private CompanyAdapter compAdapter;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        iniViews();
        AppController.addToDestroyList(this);

        utils = new Utils();
        watcher = new NetworkWatcher(this,rLayout );
        watcher.setOnInternetListener(this);

        selected = 1;
        watcher.isNetworkAvailable();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {


        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }


    protected void iniViews() {

        mToolbar             = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }


        loadingBar                      = (NewtonCradleLoading) findViewById(R.id.loadingBar);


        liNoNetwork                     =           (LinearLayout) findViewById(R.id.liNetworkError);
        rLayout                         =           (RelativeLayout) findViewById(R.id.rLayout);
        liNoCompany                     =           (LinearLayout) findViewById(R.id.liNoCompagny);
        liServerError                   =           (LinearLayout) findViewById(R.id.liServerError);
        liLoading                       =           (LinearLayout) findViewById(R.id.liLoading);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.company_menu, menu);

        final MenuItem item = menu.findItem(R.id.search_contact);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_ALWAYS);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setQueryHint(getString(R.string.search_default));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                String searchText = newText.trim();
                searchedArray = new CopyOnWriteArrayList<>();

                for (Company dm : companyList) {
                    if (dm.getName().toLowerCase()
                            .contains(searchText.toLowerCase())) {
                        searchedArray.add(dm);
                    }
                }
                if (searchText.isEmpty()) {
//                        show contacts
                    recyclerView.setAdapter(compAdapter);
                } else {
//                        display what user want
                    recyclerView.setAdapter(new CompanyAdapter(CompanyActivity.this, searchedArray, rLayout));

                }

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onLoadError() {
        onDisplayUI(false,false,true, false, companyList, false, false, false);

    }

    @Override
    public void onSucces(@NonNull CopyOnWriteArrayList<Company> companies) {
        companyList.clear();
        companyList.addAll(companies);

        if (companyList.size() > 0){
            restoreList();
        }else{
            onDisplayUI(true, false, false,false,companyList, false, false,false);
        }
    }

    private void restoreList() {
        if (companyList.size()> 0 && rLayout != null){
            updateList(companyList);
            onDisplayUI(true, false, false, false, companyList, false, false, false);
        }
    }

    private void updateList(@NonNull CopyOnWriteArrayList<Company> companyList) {
        compAdapter  =  new CompanyAdapter(this, companyList,rLayout);
        recyclerView.setAdapter(compAdapter);
    }

    protected void onDisplayUI(boolean canDisplayUI, boolean isNetworkError, boolean isServerError, boolean isLoading, @NonNull final CopyOnWriteArrayList<Company> list, boolean isSending, boolean isScheduleDone, boolean shouldRunSchedule){

        if (rLayout.getVisibility() != View.GONE)
            rLayout.setVisibility(View.GONE);
        if (liLoading.getVisibility() != View.GONE)
            liLoading.setVisibility(View.GONE);
        if (liNoCompany.getVisibility() != View.GONE)
            liNoCompany.setVisibility(View.GONE);
        if (liServerError.getVisibility() != View.GONE)
            liServerError.setVisibility(View.GONE);
        if (liNoNetwork.getVisibility() != View.GONE)
            liNoNetwork.setVisibility(View.GONE);
        loadingBar.stop();




        if (canDisplayUI){
            if (list.size()> 0){
                rLayout.setVisibility(View.VISIBLE);
            }else
                liNoCompany.setVisibility(View.VISIBLE);
            return;
        }

        if (isLoading){
            liLoading.setVisibility(View.VISIBLE);
            loadingBar.start();
            return;
        }else {
            loadingBar.stop();
        }

        if (isNetworkError){
            liNoNetwork.setVisibility(View.VISIBLE);
            return;
        }

        if (isServerError){
            liServerError.setVisibility(View.VISIBLE);
            return;
        }

        if (isSending){
            liLoading.setVisibility(View.VISIBLE);
            loadingBar.start();
            Utils.showToast(this, R.string.schedule_performing, rLayout);
            return;
        }else{
            loadingBar.stop();
        }

        if (shouldRunSchedule){
            if (isScheduleDone){
                findViewById(R.id.menu_schedule).setClickable(true);
                rLayout.setVisibility(View.VISIBLE);
                Utils.showToast(this, R.string.schedule_ok, rLayout);
                return;
            }else {
                findViewById(R.id.menu_schedule).setClickable(true);
                rLayout.setVisibility(View.VISIBLE);
                Utils.showToast(this, R.string.schedule_failed, rLayout);
                return;
            }
        }




    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Constants.STATE_COMPANIES_LIST, companyList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            companyList.clear();
            companyList.addAll((CopyOnWriteArrayList<Company>) savedInstanceState.getSerializable(Constants.STATE_COMPANIES_LIST));
            if (companyList.size()> 0){
                restoreList();
            }else{
                resfreshList();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onConnected() {
        super.onConnected();
        switch (selected){
            case 1:
                loadCompanies();
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
        onDisplayUI(false, true,false, false, companyList, false, false, false);
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


    protected void loadCompanies() {
        onDisplayUI(false, false, false, true, new CopyOnWriteArrayList<Company>(), false, false, false);
        CompanyGetAllTask task = new CompanyGetAllTask(this);
        task.setCompanyLoadingCompleteListener(this);
        if (appController != null)
            appController.launchTask(task);
    }

}
