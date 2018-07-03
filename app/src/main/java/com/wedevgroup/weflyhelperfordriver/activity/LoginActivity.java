package com.wedevgroup.weflyhelperfordriver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.haibin.calendarviewproject.colorful.MainActivity;
import com.victor.loading.newton.NewtonCradleLoading;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.presenter.DBActivity;
import com.wedevgroup.weflyhelperfordriver.task.LoginTask;
import com.wedevgroup.weflyhelperfordriver.utils.AppController;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.Save;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;
import com.wedevgroup.weflyhelperfordriver.utils.design.AnimeView;

public class LoginActivity extends DBActivity implements LoginTask.OnLoginListener{
    private EditText edName, edPassword;
    private String name = "";
    private String password = "";
    private AnimeView aView;
    private NewtonCradleLoading loadingBar;
    private LinearLayout liLogin, liMain, liMainSec, liLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iniView();
        AppController.addToDestroyList(this);


        iniListener();



        try {
            String name = Save.defaultLoadString(Constants.PREF_USER_NAME, this);
            String pwd = Save.defaultLoadString(Constants.PREF_USER_PASSWORD, this);

            if (name != null && pwd!= null){
                edName.setText(name);
                edPassword.setText(pwd);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void iniView() {

        liLogin             = (LinearLayout)         findViewById(R.id.liLogin);
        liMain              = (LinearLayout)         findViewById(R.id.liMain);
        liLoading           = (LinearLayout)         findViewById(R.id.liLoading);
        liMainSec           = (LinearLayout)         findViewById(R.id.liMainSec);

        loadingBar          = (NewtonCradleLoading) findViewById(R.id.loadingBar);

        edName              = (EditText)             findViewById(R.id.nameEdText);
        edPassword          = (EditText)             findViewById(R.id.passwordEdText);

    }

    private void iniListener() {
        liLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aView = new AnimeView(liLogin, new AnimeView.OnAnimationEndCallBack() {
                    @Override
                    public void onEnd(@NonNull View view) {
//                        startActivity(new Intent(LoginActivity.this, MainOldActivity.class));
//                        finish();
                        name            = edName.getText().toString().trim();
                        password        = edPassword.getText().toString().trim();

                        if (isInputValid()){
                            if (appController != null){
                                if (appController.isNetworkAvailable()){
                                    onDisplayUI( true, false, false, true);
                                    LoginTask task = new LoginTask(name, password, LoginActivity.this);
                                    task.setOnLoginListener(LoginActivity.this, liMain);
                                    task.execute();
                                }else
                                    Utils.showToast(LoginActivity.this, R.string.error_no_internet, liMain);

                            }
                        }

                    }
                });
                aView.startAnimation();
            }
        });

    }


    private void onDisplayUI(boolean canDisableBtn, boolean isLoginError, boolean isServerError, boolean isLoading) {
        if (liMain != null && liLogin != null){
            if (canDisableBtn){
                liLogin.setClickable(false);
            }else
                liLogin.setClickable(true);

            if (isLoginError)
                Utils.showToast(this, R.string.error_login, liMain);

            liMain.setVisibility(View.VISIBLE);

            if (isServerError){
                Utils.showToast(this, R.string.error_api, liMain);
            }

            if (isLoading){
                liMainSec.setVisibility(View.GONE);
                liLoading.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.VISIBLE);
                loadingBar.start();
            }else{
                liMainSec.setVisibility(View.VISIBLE);
                liLoading.setVisibility(View.GONE);
                loadingBar.stop();
            }




        }

    }

    private boolean isInputValid() {
        if (name.equals("")){
            Utils.showToast(this,R.string.name_empty , liLogin);
            return false;
        }

        if (password.equals("")){
            Utils.showToast(this,R.string.password_empty , liLogin);
            return false;
        }

        return true;

    }

    @Override
    public void onLoginError(@NonNull View view) {
        onDisplayUI( false, true, false, false);
    }

    @Override
    public void onLoginServerError() {
        onDisplayUI(false, false, true, false);
    }



    @Override
    public void onLoginSucces() {

        try {
            boolean isOldUser = Save.defaultLoadBoolean(Constants.PREF_IS_SAME_USER, this);
            if (!isOldUser)
                cleanDB();
        }catch (Exception e){
            e.printStackTrace();
        }

        onDisplayUI( false, false, false, false);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.STATE_USER_NAME,name );
        outState.putString(Constants.STATE_USER_NAME,password );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore old State
        String uName           = savedInstanceState.getString(Constants.STATE_USER_NAME);
        String uPassword       = savedInstanceState.getString(Constants.STATE_USER_PASSWORD);

        if (uName != null && uPassword != null && edPassword != null && edName != null ){
            edName.setText(uName);
            edPassword.setText(uPassword);
        }
    }
}
