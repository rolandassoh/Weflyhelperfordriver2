package com.wedevgroup.weflyhelperfordriver.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.presenter.DataBasePresenter;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 02/04/2018.
 */

public  class ScheduleCancelTask extends AsyncTask<Void, Integer, Boolean> {
    final CopyOnWriteArrayList<Parcelle> list = new CopyOnWriteArrayList<>();
    final CopyOnWriteArrayList<Parcelle> notifyList = new CopyOnWriteArrayList<>();
    private String response = "";
    private String url = "";
    private OnScheduleCancelListener listener;
    private View v;
    private final Context ctx;
    private String result = "";
    private AppController appController;
    private String TAG = getClass().getSimpleName();

    public ScheduleCancelTask(@NonNull final CopyOnWriteArrayList<Parcelle> list, @NonNull final Context ctx){
        this.list.addAll(list);
        this.ctx = ctx;
        AppController.addTask(this);
        appController = AppController.getInstance();
    }
    @Override
    protected void onPreExecute() {
        // update or Create

    }

    @Override
    protected Boolean doInBackground(Void... voids) {


        try {
            if (list.size() > 0){

                for (Parcelle pm: list){
                    //send and save
                    ParcellePutItemNetworkUtilities util = new ParcellePutItemNetworkUtilities();
                    url  = Constants.POST_PARCELLES_ITEM_URL  + pm.getIdOnServer() + Constants.SEPARATOR;
                    response = util.getResponseFromHttpUrl(pm.toJsonCancelSchedule().toString(), url);

                    if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().equals(Constants.RESPONSE_EMPTY) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                        if(response.trim().equals(Constants.RESPONSE_EMPTY) || response.trim().equals(Constants.RESPONSE_EMPTY_OTHER)){
                            // List Empty
                            return true;
                        }
                        JSONObject jOb = new JSONObject(response);
                        pm.setDateSurvol(jOb.getString("date"));


                        if (DataBasePresenter.getInstance().deleteParcelle(pm.getParcelleId(), ctx)){
                            DataBasePresenter.getInstance().close();
                            notifyList.add(pm);
                        }
                    }

                }

                if (notifyList.size() > 0)
                    return true;



            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;

    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        if(isCancelled())
            return;
        notifyOnScheduleCancelListener(isOk, notifyList);
    }

    public void setOnParcelleSendListener(@NonNull OnScheduleCancelListener listener, @NonNull View view) {
        this.listener = listener;
        this.v = view;
    }

    public static interface OnScheduleCancelListener {
        void onCancelError(@NonNull View view, boolean many);
        void onCancelSucces(@NonNull final CopyOnWriteArrayList<Parcelle> newList, @NonNull View view);
    }

    private void notifyOnScheduleCancelListener(boolean isDone, @NonNull final CopyOnWriteArrayList<Parcelle> mList) {
        if (listener != null  && v != null){
            try{
                if (isDone){
                    if (appController != null)
                        appController.resetNotificationDate();
                    listener.onCancelSucces(mList, v);
                }
                else
                    listener.onCancelError(v, false);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    public final class ParcellePutItemNetworkUtilities {
        public String getResponseFromHttpUrl(@NonNull String obj,@NonNull String url) throws IOException {

            HttpClient httpclient ;
            HttpPut httpPut = new HttpPut(url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);

            try {
                httpPut.setEntity(new StringEntity(obj, "UTF-8"));
                httpPut.setHeader("Content-type", "application/json");
                if (appController != null)
                    httpPut.setHeader("Authorization",Constants.TOKEN_HEADER_NAME + appController.getToken());
                response = httpclient.execute(httpPut);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                return builder.toString();
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
            return Constants.SERVER_ERROR;
        }
    }




}
