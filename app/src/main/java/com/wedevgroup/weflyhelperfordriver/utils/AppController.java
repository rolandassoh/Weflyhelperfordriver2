package com.wedevgroup.weflyhelperfordriver.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.activity.LoginActivity;
import com.wedevgroup.weflyhelperfordriver.model.Account;
import com.wedevgroup.weflyhelperfordriver.model.Company;
import com.wedevgroup.weflyhelperfordriver.model.Culture;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.model.Point;
import com.wedevgroup.weflyhelperfordriver.presenter.SettingPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Obrina.KIMI on 6/21/2017.
 */
public class AppController extends Application {
    public static final String TAG = AppController.class
        .getSimpleName();


    private static AppController mInstance;
    private static ArrayList<Activity> activitiesList = new ArrayList<>();
    private static ArrayList<AsyncTask<Void, Integer, Boolean>> tasksList = new ArrayList<>();
    private static Context AppContext;
    private static String dateSelected = "";
    private static int flyYear, flyMonth , flyDay;
    LocalizationApplicationDelegate localizationDelegate = new LocalizationApplicationDelegate(this);

    private String token = "";
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    public static Typeface canaroExtraBold;


    public static void addToDestroyList(Activity activity){
        try {
            activitiesList.add(activity);
        }catch (Exception e){
            e.printStackTrace();

        }
    }
    public static void addTask(@NonNull final AsyncTask<Void, Integer, Boolean> task){
        tasksList.add(task);
    }
    public static void clearDestroyList(){
        if (activitiesList != null && activitiesList.size() >0){
            for(Activity act: activitiesList){
                if(!act.isFinishing())
                    act.finish();
            }
            activitiesList.clear();
        }
    }
    public static void clearAsynTask(){
        if(tasksList != null && tasksList.size() > 0){
            for(AsyncTask<Void, Integer, Boolean> mTask: tasksList){
                if(!(null == mTask))
                {
                    mTask.cancel(true);
                }
            }
            tasksList.clear();
        }
    }
    public void setToken(String token) {
        this.token = token;
    }




    public int getUserId() {
        int id = 0;
        if(this.isTokenValide()){
            try {
                JWT jwt = new JWT(getToken());
                Claim claim = jwt.getClaim("user_id");
                id = claim.asInt();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return id;

    }



    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public String getToken(){
        try {
            token = Save.defaultLoadString(Constants.PREF_TOKEN, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }


    public void cleanToken(@NonNull final  Context ctx){
        try {
            Save.defaultSaveString(Constants.PREF_TOKEN, "", ctx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Context getAppContext() {
        return AppContext;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        AppContext = getApplicationContext();

        // Init icons
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new FontAwesome());
        // Hambuger Frame font
        canaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);
    }

    // Need to test
    public boolean isTokenValide(){
        try {
            token = Save.defaultLoadString(Constants.PREF_TOKEN, getApplicationContext());
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

    public void startLoginActivity(@NonNull final Context ctx){
        Intent intent = new Intent(ctx, LoginActivity.class);
        startActivity(intent);
    }

    public void startLoginActivity(){
        Intent intent = new Intent(getInstance().getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void launchActivity(@NonNull final  Intent intent, @NonNull final  Activity act, boolean withAnimation){
        if (isTokenValide()){
            Utils utils = new Utils();
            startActivity(intent);
            if (withAnimation){
                utils.animActivityOpen(act);
            }
        }else{
            startLoginActivity(act);
        }
    }

    public void launchTask(@NonNull final AsyncTask<Void, Integer, Boolean> task){
        if (isTokenValide()){
            task.execute();
        }else{
            startLoginActivity();
        }
    }



    public @NonNull
    CopyOnWriteArrayList<Company> companiesJSONArrToList(@NonNull JSONArray array){
        CopyOnWriteArrayList<Company> list = new CopyOnWriteArrayList<>();
        try {
            for (int i = 0; i< array.length(); i++){
                JSONObject obj = array.getJSONObject(i);

                Company com = new Company();
                com.setEntrepriseId(Integer.valueOf(obj.getString("id")));
                com.setName(obj.getString("nom_entreprise"));
                String img = obj.getString("entreprise_image");
                if (img.contentEquals("null"))
                    com.setImageUrl("");
                else
                    com.setImageUrl(img);
                com.setTel(obj.getString("telephone"));
                com.setEmail(obj.getString("email_entreprise"));
                com.setInternalNote(obj.getString("note_interne"));
                com.setRef(obj.getString("reference"));
                com.setWebSite(obj.getString("site_web"));
                com.setParcelCount(Integer.valueOf(obj.getString("nombre_parcelle")));

                String address = obj.getString("ville")
                        + " " + obj.getString("rue")
                        + " " + obj.getString("num_appart");
                com.setAddress(address);

                list.add(com);

            }
        } catch (Exception e){
            e.printStackTrace();
        }


        return list;

    }


    public @NonNull
    CopyOnWriteArrayList<Parcelle> ParcelsJSONArrToList(@NonNull JSONArray array, boolean isCompanyAdded){
        CopyOnWriteArrayList<Parcelle> list = new CopyOnWriteArrayList<>();
        try {
            // get All Parcels
            for (int i = 0; i< array.length(); i++){
                JSONObject obj = array.getJSONObject(i);

                boolean isDeleted = false;
                isDeleted = obj.getBoolean("isDelete");

                if (!isDeleted){
                    Parcelle parc = new Parcelle();
                    parc.setIdOnServer(Integer.valueOf(obj.getString("id")));
                    parc.setDateSoumission(obj.getString("date_soumission"));
                    parc.setDateCreated(obj.getString("date_creation"));
                    parc.setRegion(obj.getString("region"));
                    parc.setNameGuide(obj.getString("guide_name"));
                    parc.setEmailGuide(obj.getString("guide_email"));
                    parc.setTelGuide(obj.getString("guide_phone"));
                    parc.setSurface(obj.getDouble("superficie"));
                    parc.setPerimetre(obj.getDouble("perimetre"));
                    parc.setZone(obj.getString("zone"));
                    parc.setDateSurvol(obj.getString("flydate"));
                    parc.setCouche(obj.getString("couche"));
                    String imgUrl = obj.getString("polygon_image");
                    if (!imgUrl.contentEquals("")){
                        parc.setImageUrl(Constants.API_URL_NO_SEPARATOR + imgUrl);

                    }
                    parc.setParcelleId(Integer.valueOf(obj.getString("id_android")));


                    Calendar calendar = Utils.getDateFromStringJ(parc.getDateSurvol());
                    parc.setFlYear(calendar.get(Calendar.YEAR));
                    parc.setFlMonth(calendar.get(Calendar.MONTH) + 1);
                    parc.setFlDay(calendar.get(Calendar.DAY_OF_MONTH));

                    if (!isCompanyAdded){

                        // get Company
                        Company com = new Company();
                        com.setName(obj.getString("nom_entreprise"));
                        com.setEmail(obj.getString("email_entreprise"));
                        com.setTel(obj.getString("telephone"));
                        com.setWebSite(obj.getString("site_web"));
                        com.setRef(obj.getString("reference"));

                        String city = obj.getString("ville");
                        String street = obj.getString("rue");
                        String localN = obj.getString("num_appart");
                        String address = city + street + localN;

                        com.setAddress(address);
                        parc.setCompany(com);

                    }

                    // add Get All Points
                    final ArrayList<Point> points = new ArrayList<Point>();

                    JSONArray ptArray = obj.getJSONArray("polygon");
                    for (int j = 0; j < ptArray.length(); j++){
                        JSONObject pointObj  = ptArray.getJSONObject(j);
                        Point p = new Point();
                        p.setParcelleId(parc.getParcelleId());
                        p.setLatitude(pointObj.getDouble("lat"));
                        p.setLongitude(pointObj.getDouble("lng"));
                        p.setRang(pointObj.getInt("rang"));
                        p.setReference(pointObj.getBoolean("isflyPoint"));
                        p.setCenter(pointObj.getBoolean("isCentrePoint"));

                        points.add(p);
                    }
                    // add All Points
                    if (points.size() > 0)
                        parc.setPointsList(points);

                    // get All Cultures
                    final ArrayList<Culture> cultures = new ArrayList<>();
                    JSONArray culArray = obj.getJSONArray("itemName");

                    for (int k = 0; k < culArray.length() ; k ++){
                        Culture c = new Culture();
                        c.setName(culArray.getString(k));
                        cultures.add(c);
                    }


                    // Sav all Culture
                    if (cultures.size() > 0){
                        parc.setCultureList(cultures);
                    }


                    list.add(parc);
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }


        return list;

    }

    public Account accountStringToObj(@NonNull String response){
        Account ac = new Account();
        try {

            JSONObject obj = new JSONObject(response);

            ac.setId(obj.getInt("id"));
            ac.setUserName(obj.getString("username"));
            ac.setEmail(obj.getString("email"));
            ac.setLastName(obj.getString("last_name"));
            ac.setFirstName(obj.getString("first_name"));
            ac.setPassword(obj.getString("password"));

            JSONObject profileObj = obj.getJSONObject("profile");

            ac.getProfile().setIdOnServer(profileObj.getInt("id"));
            ac.getProfile().setPhone(profileObj.getString("numero"));
            ac.getProfile().setImage(profileObj.getString("profil_photo"));
            ac.getProfile().setEntreprise(profileObj.getString("entreprise"));
            ac.getProfile().setFonction(profileObj.getString("nom_fonction"));
            ac.getProfile().setNumCnps(profileObj.getString("numcnps"));
            ac.getProfile().setActor(profileObj.getBoolean("a_acteur"));
            ac.setCouche(obj.getJSONArray("acteurs")
                    .getJSONObject(0)
                    .getString("couche"));



        } catch (Exception e){
            e.printStackTrace();
        }


        return ac;

    }

    public static String getDateSelected() {
        if (dateSelected == null)
            dateSelected = "";
        return dateSelected;
    }

    public static void setDateSelected(String dateSelected) {
        AppController.dateSelected = dateSelected;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(localizationDelegate.attachBaseContext(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localizationDelegate.onConfigurationChanged(this);
    }

    @Override
    public Context getApplicationContext() {
        return localizationDelegate.getApplicationContext(super.getApplicationContext());
    }

    public boolean isFrenchDisplay(@NonNull final Activity act){
        SettingPresenter p = new SettingPresenter(act);

        return p.isLanguageFrench();
    }

    public void resetNotificationDate(){
        // remenber
        try {
            Save.defaultSaveString(Constants.LAST_NOTIFICATION_DATE, "", getApplicationContext());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int getFlyYear() {
        return flyYear;
    }

    public static void setFlyYear(int flyYear) {
        AppController.flyYear = flyYear;
    }

    public static int getFlyMonth() {
        return flyMonth;
    }

    public static void setFlyMonth(int flyMonth) {
        AppController.flyMonth = flyMonth;
    }

    public static int getFlyDay() {
        return flyDay;
    }

    public static void setFlyDay(int flyDay) {
        AppController.flyDay = flyDay;
    }
}