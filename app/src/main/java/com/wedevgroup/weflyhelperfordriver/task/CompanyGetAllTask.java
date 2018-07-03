package com.wedevgroup.weflyhelperfordriver.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wedevgroup.weflyhelperfordriver.model.Company;
import com.wedevgroup.weflyhelperfordriver.presenter.BaseTempActivity;
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

public class CompanyGetAllTask extends AsyncTask<Void, Integer, Boolean> {
    private BaseTempActivity act;
    private String TAG = getClass().getSimpleName();
    private final CopyOnWriteArrayList<Company> list = new CopyOnWriteArrayList<>();
    private String response = "";
    private OnCompanyLoadingCompleteListener listener;
    private AppController appController;

    public CompanyGetAllTask(@NonNull final BaseTempActivity activity){
        this.act = activity;
        AppController.addTask(this);
        appController = AppController.getInstance();
    }
    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            GetCompanyNetworkUtilities util = new GetCompanyNetworkUtilities();
            response = util.getResponseFromHttpUrl(Constants.COMPANIES_DOWNLOAD_URL);

            // TEst
            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                if(response.trim().equals(Constants.RESPONSE_EMPTY) || response.trim().equals(Constants.RESPONSE_EMPTY_OTHER)){
                    // List Empty
                    return true;
                }

                JSONArray jArray = null;
                jArray = new JSONArray(response);

                if (appController!= null)
                    list.addAll(appController.companiesJSONArrToList(jArray));

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
            notifyOnCompanyLoadingCompleteListener(isOk, list);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public final class GetCompanyNetworkUtilities {
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

    public void setCompanyLoadingCompleteListener(@NonNull OnCompanyLoadingCompleteListener listener) {
        this.listener = listener;

    }

    public static interface OnCompanyLoadingCompleteListener {
        void onLoadError();
        void onSucces(@NonNull CopyOnWriteArrayList<Company> companies);
    }

    private void notifyOnCompanyLoadingCompleteListener(boolean isOK, @NonNull CopyOnWriteArrayList<Company> list) {
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
