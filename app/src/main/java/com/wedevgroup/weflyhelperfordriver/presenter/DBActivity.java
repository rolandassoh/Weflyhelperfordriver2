package com.wedevgroup.weflyhelperfordriver.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.haibin.calendarview.Calendar;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.task.ScheduleAddTask;
import com.wedevgroup.weflyhelperfordriver.task.ScheduleCancelTask;
import com.wedevgroup.weflyhelperfordriver.task.ScheduleEditTask;
import com.wedevgroup.weflyhelperfordriver.task.ScheduleJobDoneTask;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 27/03/2018.
 */

public class DBActivity extends BaseTempActivity implements ScheduleJobDoneTask.OnScheduleJobDoneListener, ScheduleAddTask.OnScheduleAddListener, ScheduleCancelTask.OnScheduleCancelListener,ScheduleEditTask.OnScheduleEditListener{

    private Parcelle oldParcel = null;
    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBasePresenter.init(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            DataBasePresenter.getInstance().close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected @NonNull ArrayList<Parcelle> getParcelles(){
        ArrayList<Parcelle> list = new ArrayList<Parcelle>();
        try{
            list.addAll(DataBasePresenter.getInstance().getParcelles());
            DataBasePresenter.getInstance().close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    protected void cleanDB(){
        try {
            DataBasePresenter.getInstance().resetDB(this);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onAddError(@NonNull View view, boolean many) {

    }

    @Override
    public void onJobDoneError(@NonNull View view, boolean many) {

    }



    @Override
    public void onCancelError(@NonNull View view, boolean many) {

    }

    @Override
    public void onCancelSucces(@NonNull CopyOnWriteArrayList<Parcelle> newList, @NonNull View view) {

    }

    @Override
    public void onJobDoneSucces(@NonNull CopyOnWriteArrayList<Parcelle> newList, @NonNull View view) {

    }

    @Override
    public void onAddSucces(@NonNull CopyOnWriteArrayList<Parcelle> newList, @NonNull View view) {

    }


    @Override
    public void onEditError(@NonNull View view, boolean many) {

    }

    @Override
    public void onEditSucces(@NonNull CopyOnWriteArrayList<Parcelle> newList, @NonNull View view) {

    }
}
