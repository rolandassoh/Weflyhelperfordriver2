package com.wedevgroup.weflyhelperfordriver.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wedevgroup.weflyhelperfordriver.model.Culture;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.model.Point;
import com.wedevgroup.weflyhelperfordriver.presenter.BaseTempActivity;
import com.wedevgroup.weflyhelperfordriver.presenter.DataBasePresenter;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 02/04/2018.
 */

public class RecoveryTask extends AsyncTask<Void, Integer, Boolean> {
    private BaseTempActivity act;
    private String TAG = getClass().getSimpleName();
    private final CopyOnWriteArrayList<Parcelle> list = new CopyOnWriteArrayList<>();
    private String response = "";
    private String url = "";
    private int userId;
    private OnRecoveryCompleteListener listener;
    private AppController appController;
    private DataBasePresenter dbPresenter;

    public RecoveryTask(@NonNull final BaseTempActivity activity, int userId){
        this.act = activity;
        this.userId = userId;
        AppController.addTask(this);
        appController = AppController.getInstance();
        dbPresenter = DataBasePresenter.getInstance();
    }
    @Override
    protected void onPreExecute() {
        url = Constants.RECOVERY_LEFT_URL + userId + Constants.SEPARATOR + Constants.RECOVERY_RIGHT_URL;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            GetParcellesNetworkUtilities util = new GetParcellesNetworkUtilities();
            response = util.getResponseFromHttpUrl(url);
            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                if(response.trim().equals(Constants.RESPONSE_EMPTY) || response.trim().equals(Constants.RESPONSE_EMPTY_OTHER)){
                    // List Empty
                    // not allow
                    return false;
                }


                // CLEAN DB
                if (dbPresenter != null){
                    dbPresenter.resetDB(act);
                    DataBasePresenter.init(act);

                    // FILL DB

                    JSONArray jArray = null;
                    jArray = new JSONArray(response);

                    CopyOnWriteArrayList<Parcelle> mList = new CopyOnWriteArrayList<>();

                    if (appController != null)
                        mList.addAll(appController.ParcelsJSONArrToList(jArray, false));

                    for (Parcelle pm: mList){
                        // Save in SQL
                        if (dbPresenter.addParcelle( pm, act, false)){
                            dbPresenter.close();
                            list.add(pm);
                        }
                    }

                    if (list.size() > 0)
                        return true;



                }
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

    public void setOnRecoveryCompleteListener(@NonNull OnRecoveryCompleteListener listener) {
        this.listener = listener;

    }

    public static interface OnRecoveryCompleteListener {
        void onRecoveryError();
        void onRecoverySucces(@NonNull CopyOnWriteArrayList<Parcelle> parcelles);
    }

    private void notifyOnParcelleLoadingCompleteListener(boolean isOK, @NonNull CopyOnWriteArrayList<Parcelle> list) {
        if (listener != null){
            try {
                if (isOK){
                    if (appController != null)
                        appController.resetNotificationDate();
                    listener.onRecoverySucces(list);
                }else{
                    listener.onRecoveryError();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
