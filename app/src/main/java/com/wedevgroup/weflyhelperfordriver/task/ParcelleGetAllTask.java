package com.wedevgroup.weflyhelperfordriver.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;


import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.presenter.ListActivity;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 02/04/2018.
 */

public class ParcelleGetAllTask extends AsyncTask<Void, Integer, Boolean> {
    private ListActivity act;
    private String TAG = getClass().getSimpleName();
    private final CopyOnWriteArrayList<Parcelle> list = new CopyOnWriteArrayList<>();
    private String response = "";
    private String url = "";
    private int enterId;
    private OnParcelleLoadingCompleteListener listener;
    private AppController appController;

    public ParcelleGetAllTask(@NonNull final ListActivity activity, int enterId){
        this.act = activity;
        this.enterId = enterId;
        AppController.addTask(this);
        appController = AppController.getInstance();
    }
    @Override
    protected void onPreExecute() {
        url = Constants.PARCELLES_DOWNLOAD_LEFT_URL + enterId + Constants.SEPARATOR + Constants.PARCELLES_DOWNLOAD_RIGHT_URL;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            GetParcellesNetworkUtilities util = new GetParcellesNetworkUtilities();
            response = util.getResponseFromHttpUrl(url);
            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                if(response.trim().equals(Constants.RESPONSE_EMPTY) || response.trim().equals(Constants.RESPONSE_EMPTY_OTHER)){
                    // List Empty
                    return true;
                }

                JSONArray jArray = null;
                jArray = new JSONArray(response);

                if (appController != null)
                    list.addAll(appController.ParcelsJSONArrToList(jArray, true));


                if (list.size() > 0)
                    return true;

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        if(isCancelled())
            return;
        try {
            notifyOnParcelleLoadingCompleteListener(isOk, list);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public final class GetParcellesNetworkUtilities {
        public String getResponseFromHttpUrl(final String base_url) throws IOException {

            HttpClient httpclient ;
            HttpGet httpget = new HttpGet(base_url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);

            try {
                httpget.setHeader("Content-type", "application/json;charset=UTF-8");
                httpget.setHeader("Accept-Type","application/json");
                if (appController != null)
                    httpget.setHeader("Authorization",Constants.TOKEN_HEADER_NAME + appController.getToken());
                response = httpclient.execute(httpget);
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

    public void setOnParcelleLoadingCompleteListener(@NonNull OnParcelleLoadingCompleteListener listener) {
        this.listener = listener;

    }

    public static interface OnParcelleLoadingCompleteListener {
        void onLoadError();
        void onSucces(@NonNull CopyOnWriteArrayList<Parcelle> parcelles);
    }

    private void notifyOnParcelleLoadingCompleteListener(boolean isOK, @NonNull CopyOnWriteArrayList<Parcelle> list) {
        if (listener != null){
            try {
                if (isOK){
                    listener.onSucces(list);
                }else{
                    listener.onLoadError();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
