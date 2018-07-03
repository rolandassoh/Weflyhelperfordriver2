package com.wedevgroup.weflyhelperfordriver.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.michaelflisar.dragselectrecyclerview.DragSelectionProcessor;
import com.victor.loading.newton.NewtonCradleLoading;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.dragSelection.DragParcelsAdapter;
import com.wedevgroup.weflyhelperfordriver.model.Company;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.task.ParcelleGetAllTask;
import com.wedevgroup.weflyhelperfordriver.task.ScheduleAddTask;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;

import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 27/03/2018.
 */

public class ListActivity extends DBActivity implements ParcelleGetAllTask.OnParcelleLoadingCompleteListener, ScheduleAddTask.OnScheduleAddListener {
    protected DragSelectionProcessor.Mode mMode = DragSelectionProcessor.Mode.Simple;
    private final String TAG = getClass().getSimpleName();

    protected Toolbar mToolbar;
    protected DragSelectTouchListener mDragSelectTouchListener;
    protected DragParcelsAdapter mAdapter;

    protected DragSelectionProcessor mDragSelectionProcessor;
    protected RelativeLayout rLayout;


    protected LinearLayout liNoNetwork;
    protected LinearLayout liNoParcel, liServerError, liLoading;
    protected NewtonCradleLoading loadingBar;
    protected RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    protected void iniListeners(@NonNull final CopyOnWriteArrayList<Parcelle> list, @NonNull Company company) {
        recyclerView = (RecyclerView) findViewById(R.id.rvData);
        GridLayoutManager glm = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(glm);
        mAdapter = new DragParcelsAdapter(this, list, rLayout,company );
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(new DragParcelsAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                mAdapter.toggleSelection(position);
            }

            @Override
            public boolean onItemLongClick(View view, int position)
            {
                // if one item is long pressed, we start the drag selection like following:
                // we just call this function and pass in the position of the first selected item
                // the selection processor does take care to update the positions selection mode correctly
                // and will correctly transform the touch events so that they can be directly applied to your adapter!!!
                mDragSelectTouchListener.startDragSelection(position);
                return true;
            }
        });

        // 2) Add the DragSelectListener
        mDragSelectionProcessor = new DragSelectionProcessor(new DragSelectionProcessor.ISelectionHandler() {
            @Override
            public HashSet<Integer> getSelection() {
                return mAdapter.getSelection();
            }

            @Override
            public boolean isSelected(int index) {
                return mAdapter.getSelection().contains(index);
            }

            @Override
            public void updateSelection(int start, int end, boolean isSelected, boolean calledFromOnStart) {
                mAdapter.selectRange(start, end, isSelected);
            }
        })
                .withMode(mMode);
        mDragSelectTouchListener = new DragSelectTouchListener()
                .withSelectListener(mDragSelectionProcessor);
        updateSelectionListener();
        recyclerView.addOnItemTouchListener(mDragSelectTouchListener);
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
        liNoParcel                      =           (LinearLayout) findViewById(R.id.liNoParcel);
        liServerError                   =           (LinearLayout) findViewById(R.id.liServerError);
        liLoading                       =           (LinearLayout) findViewById(R.id.liLoading);
    }

    // ---------------------
    // Selection Listener
    // ---------------------

    protected void updateSelectionListener()
    {
        mDragSelectionProcessor.withMode(mMode);
    }

    @Override
    public void onLoadError() {

    }

    @Override
    public void onSucces(@NonNull CopyOnWriteArrayList<Parcelle> parcelles) {

    }

    protected void onDisplayUI(boolean canDisplayUI, boolean isNetworkError, boolean isServerError, boolean isLoading, @NonNull final CopyOnWriteArrayList<Parcelle> list, boolean isSending, boolean isScheduleDone, boolean shouldRunSchedule){

        if (rLayout.getVisibility() != View.GONE)
            rLayout.setVisibility(View.GONE);
        if (liLoading.getVisibility() != View.GONE)
            liLoading.setVisibility(View.GONE);
        if (liNoParcel.getVisibility() != View.GONE)
            liNoParcel.setVisibility(View.GONE);
        if (liServerError.getVisibility() != View.GONE)
            liServerError.setVisibility(View.GONE);
        if (liNoNetwork.getVisibility() != View.GONE)
            liNoNetwork.setVisibility(View.GONE);
        loadingBar.stop();




        if (canDisplayUI){
            if (list.size()> 0){
                rLayout.setVisibility(View.VISIBLE);
            }else
                liNoParcel.setVisibility(View.VISIBLE);
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
    public void onAddError(@NonNull View view, boolean many) {

    }

    @Override
    public void onAddSucces(@NonNull CopyOnWriteArrayList<Parcelle> newList, @NonNull View view) {

    }
}
