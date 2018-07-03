package com.wedevgroup.weflyhelperfordriver.task;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wedevgroup.weflyhelperfordriver.presenter.BaseTempActivity;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.Save;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;



/**
 * Created by admin on 02/04/2018.
 */

public  class LoginTask extends AsyncTask<Void, Integer, Boolean> {
    private String response = "";
    private OnLoginListener listener;
    private View v;
    private JSONObject obj;
    private String uName, uPword;
    private BaseTempActivity act;
    private String token = "";
    private boolean isServerError;

    private String TAG = getClass().getSimpleName();

    public LoginTask(@NonNull String uName, @NonNull String pWord, @NonNull final BaseTempActivity act){
        this.uName = uName;
        this.uPword = pWord;
        this.act = act;
        obj= new JSONObject();
    }
    @Override
    protected void onPreExecute() {
        AppController.addTask(this);
        try {
            obj.put("username", uName);
            obj.put("password", uPword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        LoginNetworkUtilities util = new LoginNetworkUtilities();
        try {
            response = util.getResponseFromHttpUrl(obj, Constants.LOGIN_URL );
            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().equals(Constants.RESPONSE_EMPTY) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                // post update
                if(response.trim().contains(Constants.RESPONSE_EMPTY_INPUT)){
                    return false;
                }
                return true;
            }else
                isServerError = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        if(isCancelled())
            return;
        try {
            if (isOk){
                Log.v(Constants.APP_NAME , TAG + "isToken is OK "+ token );
                token = new JSONObject(response)
                        .getString("token");
                Log.v(Constants.APP_NAME , TAG + "isTokenValide val "+ token );
                Save.defaultSaveString(Constants.PREF_TOKEN, token, act);
                Save.defaultSaveString(Constants.PREF_USER_NAME, uName, act);
                Save.defaultSaveString(Constants.PREF_USER_PASSWORD, uPword, act);
                // Dont clear DB if is save user
                String lastUsName = Save.defaultLoadString(Constants.PREF_USER_LAST_NAME,act);
                String lastUsPwd  = Save.defaultLoadString(Constants.PREF_USER_LAST_PASSWORD,act);

                if (!lastUsName.toString().trim().contentEquals(uName.toString().trim()) &&
                        !lastUsPwd.toString().trim().contentEquals(uPword.toString().trim())){
                    Save.defaultSaveString(Constants.PREF_USER_LAST_NAME, uName, act);
                    Save.defaultSaveString(Constants.PREF_USER_LAST_PASSWORD, uPword, act);
                    // Not same
                    Save.defaultSaveBoolean(Constants.PREF_IS_SAME_USER, true, act);
                }else{
                    //Same User
                    Save.defaultSaveBoolean(Constants.PREF_IS_SAME_USER, true, act);
                }


            }else{
                Log.v(Constants.APP_NAME , TAG + "isToken is NOT OK "+ token );
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.v(Constants.APP_NAME , TAG + "isToken is NOT OK printStackTrace "+ token );
        }
        notifyOnLoginListener(isOk);
    }

    public void setOnLoginListener(@NonNull OnLoginListener listener, @NonNull View view) {
        this.listener = listener;
        this.v = view;

    }


    public static interface OnLoginListener {
        void onLoginError(@NonNull View view);
        void onLoginServerError();
        void onLoginSucces();
    }

    private void notifyOnLoginListener(boolean isDone) {
        if (listener != null  && v != null){
            try {
                if (isServerError)
                    listener.onLoginServerError();
                else{
                    if (isDone)
                        listener.onLoginSucces();
                    else
                        listener.onLoginError(v);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    public final class LoginNetworkUtilities {
        public String getResponseFromHttpUrl(@NonNull JSONObject jsonParam, @Nullable String url) throws IOException {

            HttpClient httpclient ;
            HttpPost httppost = new HttpPost(url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);
            Log.v(Constants.APP_NAME, TAG + "  doInBackGround url " +url  + " Obj " + jsonParam.toString());

            try {
                httppost.setEntity(new StringEntity(jsonParam.toString(), "UTF-8"));
                httppost.setHeader("Content-type", "application/json");
                response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                Log.v(Constants.APP_NAME, TAG + "  doInBackGround parcelJson.toString response ");
                return builder.toString();
            }catch (UnsupportedEncodingException e) {
                Log.v(Constants.APP_NAME, TAG + "   doInBackGround UnsupportedEncodingException" );
                e.printStackTrace();
            }catch (IOException e) {
                Log.v(Constants.APP_NAME, TAG + "   doInBackGround IOException" );
                e.printStackTrace();
            }
            return Constants.SERVER_ERROR;
        }
    }

}
