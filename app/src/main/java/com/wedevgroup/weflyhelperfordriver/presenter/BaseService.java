package com.wedevgroup.weflyhelperfordriver.presenter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.PermissionUtil;
import com.wedevgroup.weflyhelperfordriver.utils.Save;

/**
 * Created by admin on 04/04/2018.
 */

public class BaseService extends Service {
    public static Double uLatitude = Constants.DOUBLE_NULL;
    public static Double uLongitude = Constants.DOUBLE_NULL;
    protected static boolean isAllPermissionGranted = false;
    protected PermissionUtil pUtil;
    protected final String TAG = getClass().getSimpleName();
    String token = "";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pUtil = new PermissionUtil(this);
        //check permission
        isAllPermissionGranted = pUtil.isAllPermissionsGranded();
    }

    public boolean isTokenValide(){
        try {
            token = Save.defaultLoadString(Constants.PREF_TOKEN, getBaseContext());
            if(!token.equals("")){
                JWT jwt = new JWT(token);
                boolean isExpired = jwt.isExpired(0);
                return !isExpired;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }



    public String getToken(){
        try {
            token = Save.defaultLoadString(Constants.PREF_TOKEN, getBaseContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
}
