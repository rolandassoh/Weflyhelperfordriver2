package com.wedevgroup.weflyhelperfordriver.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wedevgroup.weflyhelperfordriver.model.Company;
import com.wedevgroup.weflyhelperfordriver.model.Culture;
import com.wedevgroup.weflyhelperfordriver.model.Parcelle;
import com.wedevgroup.weflyhelperfordriver.model.Point;
import com.wedevgroup.weflyhelperfordriver.model.Region;
import com.wedevgroup.weflyhelperfordriver.utils.CacheImage;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.Save;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 27/03/2018.
 */

public class DataBasePresenter extends SQLiteOpenHelper {

    private final CopyOnWriteArrayList<Parcelle> parcelleList = new CopyOnWriteArrayList<>();
    private final ArrayList<Point> pointList = new ArrayList<>();
    private final CopyOnWriteArrayList<Company> companyList = new CopyOnWriteArrayList<>();
    private final ArrayList<Culture> cultureList = new ArrayList<>();
    private final String TAG = getClass().getSimpleName();

    private static DataBasePresenter instance;

    public static void init(@NonNull Context ctx){
        if (null==instance) {
            instance = new DataBasePresenter(ctx);
        }
    }

    public static @NonNull DataBasePresenter getInstance(){
        return instance;
    }



    public DataBasePresenter(final Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            //create table PARCELLE
            String CREATE_TABLE_PARCELLE = "CREATE TABLE " + Constants.TABLE_PARCELLE + "("
                    + Constants.TABLE_PARCELLE_KEY_ID                   + " INTEGER PRIMARY KEY, "
                    + Constants.TABLE_PARCELLE_ZONE_NAME                + " TEXT, "
                    + Constants.TABLE_PARCELLE_DISTANCE_NAME            + " TEXT, "
                    + Constants.TABLE_PARCELLE_COUCHE_NAME              + " TEXT, "
                    + Constants.TABLE_PARCELLE_REGION_NAME              + " TEXT, "
                    + Constants.TABLE_PARCELLE_ENTREPRISE_ID_NAME       + " INT, "
                    + Constants.TABLE_PARCELLE_IS_DELETE_NAME           + " INT, "
                    + Constants.TABLE_PARCELLE_REGION_ID_NAME           + " INT, "
                    + Constants.TABLE_PARCELLE_IS_NEW_NAME              + " INT, "
                    + Constants.TABLE_PARCELLE_ID_ON_SERVER_NAME        + " INT, "
                    + Constants.TABLE_PARCELLE_IS_FLY_DONE_NAME         + " INT, "
                    + Constants.TABLE_PARCELLE_DATE_SURVOL_YEAR_NAME    + " INT, "
                    + Constants.TABLE_PARCELLE_DATE_SURVOL_MONTH_NAME   + " INT, "
                    + Constants.TABLE_PARCELLE_DATE_SURVOL_DAY_NAME     + " INT, "
                    + Constants.TABLE_PARCELLE_PERIMETRE_NAME           + " LONG, "
                    + Constants.TABLE_PARCELLE_SURFACE_NAME             + " LONG, "
                    + Constants.TABLE_PARCELLE_GUIDE_NOM_NAME           + " TEXT, "
                    + Constants.TABLE_PARCELLE_GUIDE_EMAIL_NAME         + " TEXT, "
                    + Constants.TABLE_PARCELLE_GUIDE_TEL_NAME           + " TEXT, "
                    + Constants.TABLE_PARCELLE_IMAGE_URL                + " TEXT, "
                    + Constants.TABLE_PARCELLE_DATE_SURVOL_NAME         + " TEXT, "
                    + Constants.TABLE_PARCELLE_DATE_SOUMISSION_NAME     + " TEXT, "
                    + Constants.TABLE_PARCELLE_DATE_CREATED_NAME        + " TEXT);";


            //create table points
            String CREATE_TABLE_POINT = "CREATE TABLE " + Constants.TABLE_POINT + "("
                    + Constants.TABLE_POINT_KEY                 + " INTEGER PRIMARY KEY, "
                    + Constants.TABLE_POINT_LATITUDE_NAME       + " LONG, "
                    + Constants.TABLE_POINT_LONGITUDE_NAME      + " LONG, "
                    + Constants.TABLE_POINT_RANG_NAME           + " INT, "
                    + Constants.TABLE_POINT_IS_CENTER_NAME      + " INT, "
                    + Constants.TABLE_POINT_PARCELLE_ID_NAME    + " INT, "
                    + Constants.TABLE_POINT_IS_REFERNCE_NAME    + " INT);";



            //create table culture
            String CREATE_TABLE_CULTURE = "CREATE TABLE " + Constants.TABLE_CULTURE + "("
                    + Constants.TABLE_CULTURE_KEY                 + " INTEGER PRIMARY KEY, "
                    + Constants.TABLE_CULTURE_ID_NAME             + " INT, "
                    + Constants.TABLE_CULTURE_TYPE_ID_NAME        + " INT, "
                    + Constants.TABLE_CULTURE_PARCELLE_ID_NAME    + " INT, "
                    + Constants.TABLE_CULTURE_NAME                + " TEXT, "
                    + Constants.TABLE_CULTURE_TYPE_NAME           + " TEXT);";


            //create table entreprise
            String CREATE_TABLE_ENTREPRISE = "CREATE TABLE " + Constants.TABLE_ENTREPRISE + "("
                    + Constants.TABLE_ENTREPRISE                            + " INTEGER PRIMARY KEY, "
                    + Constants.TABLE_ENTREPRISE_KEY                        + " INT, "
                    + Constants.TABLE_ENTREPRISE_PARCELLE_ID_NAME           + " INT, "
                    + Constants.TABLE_ENTREPRISE_NAME_NAME                  + " TEXT, "
                    + Constants.TABLE_ENTREPRISE_PHONE_NAME                 + " TEXT, "
                    + Constants.TABLE_ENTREPRISE_EMAIL_NAME                 + " TEXT, "
                    + Constants.TABLE_ENTREPRISE_ADDRESS_NAME               + " TEXT, "
                    + Constants.TABLE_ENTREPRISE_REFERENCE_NAME             + " TEXT, "
                    + Constants.TABLE_ENTREPRISE_WEB_SITE_NAME              + " TEXT);";



            db.execSQL(CREATE_TABLE_PARCELLE);
            db.execSQL(CREATE_TABLE_POINT);
            db.execSQL(CREATE_TABLE_CULTURE);
            db.execSQL(CREATE_TABLE_ENTREPRISE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_PARCELLE);
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_POINT);
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CULTURE);
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_ENTREPRISE);

            //create a new one
            onCreate(db);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //Get total items saved
    public int getParcelleTotalItems() {
        int totalItems = 0;
        try{
            String query = "SELECT * FROM " + Constants.TABLE_PARCELLE;
            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.rawQuery(query, null);

            totalItems = cursor.getCount();

            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return totalItems;


    }

    //Get total items saved
    public int getPointTotalItems() {

        int totalItems = 0;
        try {
            String query = "SELECT * FROM " + Constants.TABLE_POINT;
            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.rawQuery(query, null);

            totalItems = cursor.getCount();

            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return totalItems;


    }

    //Get total
    public int getCultureTotalItems() {

        int totalItems = 0;
        try {
            String query = "SELECT * FROM " + Constants.TABLE_CULTURE;
            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.rawQuery(query, null);

            totalItems = cursor.getCount();

            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return totalItems;


    }


    //Get total
    public int getCompanyTotalItems() {

        int totalItems = 0;
        try {
            String query = "SELECT * FROM " + Constants.TABLE_ENTREPRISE;
            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.rawQuery(query, null);

            totalItems = cursor.getCount();

            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return totalItems;


    }



    //delete parcelle item
    public boolean deleteParcelle(@NonNull final Parcelle parcelle, final Context ctx) {
        int id = parcelle.getParcelleId();
        try{
            // was never sent
            if (parcelle.isNew() && parcelle.getDateSoumission().trim().equals("")){
                deleteParcelle(parcelle.getParcelleId(), ctx);
            }else{
                // send One time
                parcelle.setDateSoumission("");
                parcelle.setNew(false);
                parcelle.setDelete(true);
            }
            return  updateParcelle(parcelle);
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }

    }

    //delete parcelle item
    public boolean deleteParcelle(int parcelId, final  Context ctx) {
        try{
            if (ctx != null){
                // save Image
                CacheImage cImage = new CacheImage(ctx);
                cImage.deleteFile(String.valueOf(parcelId));
            }
            SQLiteDatabase dba = this.getWritableDatabase();
            dba.delete(Constants.TABLE_PARCELLE, Constants.TABLE_PARCELLE_KEY_ID + " = ?",
                    new String[]{String.valueOf(parcelId)});

            dba.close();

            deletePoints(parcelId);

            deleteCultures(parcelId);

            deleteCompagny(parcelId);


            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }

    }


    //delete point item
    private void deletePoints(int id) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();
            dba.delete(Constants.TABLE_POINT, Constants.TABLE_POINT_PARCELLE_ID_NAME + " = ?",
                    new String[]{String.valueOf(id)});

            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    //delete point item
    private void deleteCompagny(int id) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();
            dba.delete(Constants.TABLE_ENTREPRISE, Constants.TABLE_ENTREPRISE_PARCELLE_ID_NAME + " = ?",
                    new String[]{String.valueOf(id)});

            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    //delete culture item
    private void deleteCultures(int id) {
        try {
            SQLiteDatabase dba = this.getWritableDatabase();
            dba.delete(Constants.TABLE_CULTURE, Constants.TABLE_CULTURE_PARCELLE_ID_NAME + " = ?",
                    new String[]{String.valueOf(id)});
            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    //add content to db - add parcelle
    public boolean addParcelle(@NonNull Parcelle parcelle, final  Context ctx, @NonNull Bitmap image, boolean fromTemp) {
        boolean isSaved = false;
        parcelle.setDateSoumission("");
        parcelle.setNew(true);
        try {

            if (ctx != null){
                // save Image
                CacheImage cImage = new CacheImage(ctx);
                if (fromTemp)
                    isSaved = cImage.saveFromTemp(String.valueOf(getAnId()));
                else
                    isSaved = cImage.save(String.valueOf(getAnId()), image );
            }

            if (isSaved){
                int id = getAnId();
                SQLiteDatabase dba = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Constants.TABLE_PARCELLE_KEY_ID, id );
                values.put(Constants.TABLE_PARCELLE_ZONE_NAME, parcelle.getZone());
                values.put(Constants.TABLE_PARCELLE_DISTANCE_NAME, parcelle.getDistance());
                values.put(Constants.TABLE_PARCELLE_COUCHE_NAME, parcelle.getCouche());
                values.put(Constants.TABLE_PARCELLE_REGION_NAME, parcelle.getRegion());
                values.put(Constants.TABLE_PARCELLE_REGION_ID_NAME, parcelle.getRegionId());
                values.put(Constants.TABLE_PARCELLE_ENTREPRISE_ID_NAME, parcelle.getCompany().getEntrepriseId());
                values.put(Constants.TABLE_PARCELLE_PERIMETRE_NAME, parcelle.getPerimetre());
                values.put(Constants.TABLE_PARCELLE_SURFACE_NAME, parcelle.getSurface());
                values.put(Constants.TABLE_PARCELLE_DATE_SOUMISSION_NAME, parcelle.getDateSoumission());
                String date = parcelle.getDateCreated();
                if(date.trim().equals(""))
                    values.put(Constants.TABLE_PARCELLE_DATE_CREATED_NAME, Utils.getCurrentDate());
                else
                    values.put(Constants.TABLE_PARCELLE_DATE_CREATED_NAME, date);
                values.put(Constants.TABLE_PARCELLE_GUIDE_NOM_NAME, parcelle.getNameGuide());
                values.put(Constants.TABLE_PARCELLE_GUIDE_EMAIL_NAME, parcelle.getEmailGuide());
                values.put(Constants.TABLE_PARCELLE_GUIDE_TEL_NAME, parcelle.getTelGuide());
                values.put(Constants.TABLE_PARCELLE_IMAGE_URL, parcelle.getImageUrl());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_NAME, parcelle.getDateSurvol());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_YEAR_NAME, parcelle.getFlYear());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_MONTH_NAME, parcelle.getFlMonth());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_DAY_NAME, parcelle.getFlDay());
                values.put(Constants.TABLE_PARCELLE_IS_DELETE_NAME, parcelle.getIsDeleteAsInt());
                values.put(Constants.TABLE_PARCELLE_IS_NEW_NAME, parcelle.getIsNewAsInt());
                values.put(Constants.TABLE_PARCELLE_ID_ON_SERVER_NAME, parcelle.getIdOnServer());
                values.put(Constants.TABLE_PARCELLE_IS_FLY_DONE_NAME, parcelle.getIsFlDoneAsInt());

                dba.insert(Constants.TABLE_PARCELLE, null, values);
                dba.close();

                // save Points
                for (Point dm: parcelle.getPointsList()){
                    addPoint(dm, id);
                }

                // save culture
                for (Culture dm: parcelle.getCultureList()){
                    addCulture(dm, id);
                }
                // save company
                addCompany(parcelle.getCompany(), id);

                return true;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //add content to db - add parcelle
    public boolean addParcelle(@NonNull Parcelle parcelle, final  Context ctx, boolean fromTemp) {
        boolean isSaved = false;
        parcelle.setDateSoumission("");
        parcelle.setNew(true);

        try {

            if (ctx != null){
                // save Image
                CacheImage cImage = new CacheImage(ctx);
                if (fromTemp)
                    isSaved = cImage.saveFromTemp(String.valueOf(getAnId()));
                else{
                    // use default image
                    isSaved = true;
                }
            }

            if (isSaved){
                int id = getAnId();
                SQLiteDatabase dba = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Constants.TABLE_PARCELLE_KEY_ID, id );
                values.put(Constants.TABLE_PARCELLE_ZONE_NAME, parcelle.getZone());
                values.put(Constants.TABLE_PARCELLE_DISTANCE_NAME, parcelle.getDistance());
                values.put(Constants.TABLE_PARCELLE_COUCHE_NAME, parcelle.getCouche());
                values.put(Constants.TABLE_PARCELLE_REGION_NAME, parcelle.getRegion());
                values.put(Constants.TABLE_PARCELLE_REGION_ID_NAME, parcelle.getRegionId());
                values.put(Constants.TABLE_PARCELLE_ENTREPRISE_ID_NAME, parcelle.getCompany().getEntrepriseId());
                values.put(Constants.TABLE_PARCELLE_PERIMETRE_NAME, parcelle.getPerimetre());
                values.put(Constants.TABLE_PARCELLE_SURFACE_NAME, parcelle.getSurface());
                values.put(Constants.TABLE_PARCELLE_DATE_SOUMISSION_NAME, parcelle.getDateSoumission());
                String date = parcelle.getDateCreated();
                if(date.trim().equals(""))
                    values.put(Constants.TABLE_PARCELLE_DATE_CREATED_NAME, Utils.getCurrentDate());
                else
                    values.put(Constants.TABLE_PARCELLE_DATE_CREATED_NAME, date);
                values.put(Constants.TABLE_PARCELLE_GUIDE_NOM_NAME, parcelle.getNameGuide());
                values.put(Constants.TABLE_PARCELLE_GUIDE_EMAIL_NAME, parcelle.getEmailGuide());
                values.put(Constants.TABLE_PARCELLE_GUIDE_TEL_NAME, parcelle.getTelGuide());
                values.put(Constants.TABLE_PARCELLE_IMAGE_URL, parcelle.getImageUrl());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_NAME, parcelle.getDateSurvol());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_YEAR_NAME, parcelle.getFlYear());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_MONTH_NAME, parcelle.getFlMonth());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_DAY_NAME, parcelle.getFlDay());
                values.put(Constants.TABLE_PARCELLE_IS_DELETE_NAME, parcelle.getIsDeleteAsInt());
                values.put(Constants.TABLE_PARCELLE_IS_NEW_NAME, parcelle.getIsNewAsInt());
                values.put(Constants.TABLE_PARCELLE_ID_ON_SERVER_NAME, parcelle.getIdOnServer());
                values.put(Constants.TABLE_PARCELLE_IS_FLY_DONE_NAME, parcelle.getIsFlDoneAsInt());

                dba.insert(Constants.TABLE_PARCELLE, null, values);
                dba.close();

                // save Points
                for (Point dm: parcelle.getPointsList()){
                    addPoint(dm, id);
                }

                // save culture
                for (Culture dm: parcelle.getCultureList()){
                    addCulture(dm, id);
                }
                // save company
                addCompany(parcelle.getCompany(), id);





                return true;
            }

        }catch (Exception e){
            e.printStackTrace();

        }
        return false;
    }


    //add content to db - add point
    private void addPoint(@NonNull Point point, int id) {
        try{
            SQLiteDatabase dba = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Constants.TABLE_POINT_PARCELLE_ID_NAME, id);
            values.put(Constants.TABLE_POINT_RANG_NAME, point.getRang());
            values.put(Constants.TABLE_POINT_LATITUDE_NAME, point.getLatitude());
            values.put(Constants.TABLE_POINT_LONGITUDE_NAME, point.getLongitude());
            values.put(Constants.TABLE_POINT_IS_REFERNCE_NAME, point.getIsReferenceAsInt());
            values.put(Constants.TABLE_POINT_IS_CENTER_NAME, point.getIsCenterAsInt());


            dba.insert(Constants.TABLE_POINT, null, values);


            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //add content to db - add Culture
    private void addCulture(@NonNull Culture culture, int id) {
        try{
            SQLiteDatabase dba = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(Constants.TABLE_CULTURE_PARCELLE_ID_NAME, id);
            values.put(Constants.TABLE_CULTURE_ID_NAME, culture.getCultureId());
            values.put(Constants.TABLE_CULTURE_TYPE_ID_NAME, culture.getTypeCultureId());
            values.put(Constants.TABLE_CULTURE_NAME, culture.getName());
            values.put(Constants.TABLE_CULTURE_TYPE_NAME, culture.getTypeCulture());
            dba.insert(Constants.TABLE_CULTURE, null, values);


            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //add content to db - add Culture
    private void addCompany(@NonNull Company com, int id) {
        try{
            SQLiteDatabase dba = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(Constants.TABLE_ENTREPRISE_PARCELLE_ID_NAME, id);
            values.put(Constants.TABLE_ENTREPRISE_NAME_NAME, com.getName());
            values.put(Constants.TABLE_ENTREPRISE_PHONE_NAME, com.getTel());
            values.put(Constants.TABLE_ENTREPRISE_EMAIL_NAME, com.getEmail());
            values.put(Constants.TABLE_ENTREPRISE_ADDRESS_NAME, com.getAddress());
            values.put(Constants.TABLE_ENTREPRISE_REFERENCE_NAME, com.getRef());
            values.put(Constants.TABLE_ENTREPRISE_WEB_SITE_NAME, com.getWebSite());


            dba.insert(Constants.TABLE_ENTREPRISE, null, values);


            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public boolean updateParcelle(@NonNull Parcelle parcelle) {
        try {
            // create and edit without send
            if (!parcelle.getDateSoumission().trim().equals("")){
                parcelle.setNew(false);
            }
            parcelle.setDateSoumission("");

            SQLiteDatabase dba = this.getWritableDatabase();
            String updateId = Integer.toString(parcelle.getParcelleId());
            ContentValues values = new ContentValues();

            values.put(Constants.TABLE_PARCELLE_ZONE_NAME, parcelle.getZone());
            values.put(Constants.TABLE_PARCELLE_DISTANCE_NAME, parcelle.getDistance());
            values.put(Constants.TABLE_PARCELLE_COUCHE_NAME, parcelle.getCouche());
            values.put(Constants.TABLE_PARCELLE_REGION_NAME, parcelle.getRegion());
            values.put(Constants.TABLE_PARCELLE_REGION_ID_NAME, parcelle.getRegionId());
            values.put(Constants.TABLE_PARCELLE_ENTREPRISE_ID_NAME, parcelle.getCompany().getEntrepriseId());
            values.put(Constants.TABLE_PARCELLE_PERIMETRE_NAME, parcelle.getPerimetre());
            values.put(Constants.TABLE_PARCELLE_SURFACE_NAME, parcelle.getSurface());
            values.put(Constants.TABLE_PARCELLE_DATE_SOUMISSION_NAME, parcelle.getDateSoumission());
            String date = parcelle.getDateCreated();
            if(date.trim().equals(""))
                values.put(Constants.TABLE_PARCELLE_DATE_CREATED_NAME, Utils.getCurrentDate());
            else
                values.put(Constants.TABLE_PARCELLE_DATE_CREATED_NAME, date);
            values.put(Constants.TABLE_PARCELLE_GUIDE_NOM_NAME, parcelle.getNameGuide());
            values.put(Constants.TABLE_PARCELLE_GUIDE_EMAIL_NAME, parcelle.getEmailGuide());
            values.put(Constants.TABLE_PARCELLE_GUIDE_TEL_NAME, parcelle.getTelGuide());
            values.put(Constants.TABLE_PARCELLE_IMAGE_URL, parcelle.getImageUrl());
            values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_NAME, parcelle.getDateSurvol());
            values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_YEAR_NAME, parcelle.getFlYear());
            values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_MONTH_NAME, parcelle.getFlMonth());
            values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_DAY_NAME, parcelle.getFlDay());
            values.put(Constants.TABLE_PARCELLE_IS_DELETE_NAME, parcelle.getIsDeleteAsInt());
            values.put(Constants.TABLE_PARCELLE_IS_NEW_NAME, parcelle.getIsNewAsInt());
            values.put(Constants.TABLE_PARCELLE_ID_ON_SERVER_NAME, parcelle.getIdOnServer());
            values.put(Constants.TABLE_PARCELLE_IS_FLY_DONE_NAME, parcelle.getIsFlDoneAsInt());

            dba.update(Constants.TABLE_PARCELLE, values, Constants.TABLE_PARCELLE_KEY_ID + "=?", new String[]{updateId});
            dba.close();

            deletePoints(parcelle.getParcelleId());

            deleteCultures(parcelle.getParcelleId());

            //delete company
            deleteCompagny(parcelle.getParcelleId());

            // save Points
            for (Point dm: parcelle.getPointsList()){
                addPoint(dm, parcelle.getParcelleId());
            }

            // save culture
            for (Culture dm: parcelle.getCultureList()){
                addCulture(dm, parcelle.getParcelleId());

            }

            // save company
            addCompany(parcelle.getCompany(), parcelle.getParcelleId());


            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateParcelle(@NonNull Parcelle parcelle, final Context ctx, boolean fromTemp) {
        try {
            boolean isSaved = false;
            // create and edit without send
            if (!parcelle.getDateSoumission().trim().equals("")){
                parcelle.setNew(false);
            }
            parcelle.setDateSoumission("");




            if (ctx != null){
                // save Image
                CacheImage cImage = new CacheImage(ctx);
                if (fromTemp)
                    isSaved = cImage.saveFromTemp(String.valueOf(parcelle.getParcelleId()));
            }

            if (isSaved){
                SQLiteDatabase dba = this.getWritableDatabase();
                String updateId = Integer.toString(parcelle.getParcelleId());
                ContentValues values = new ContentValues();

                values.put(Constants.TABLE_PARCELLE_ZONE_NAME, parcelle.getZone());
                values.put(Constants.TABLE_PARCELLE_DISTANCE_NAME, parcelle.getDistance());
                values.put(Constants.TABLE_PARCELLE_COUCHE_NAME, parcelle.getCouche());
                values.put(Constants.TABLE_PARCELLE_REGION_NAME, parcelle.getRegion());
                values.put(Constants.TABLE_PARCELLE_REGION_ID_NAME, parcelle.getRegionId());
                values.put(Constants.TABLE_PARCELLE_ENTREPRISE_ID_NAME, parcelle.getCompany().getEntrepriseId());
                values.put(Constants.TABLE_PARCELLE_PERIMETRE_NAME, parcelle.getPerimetre());
                values.put(Constants.TABLE_PARCELLE_SURFACE_NAME, parcelle.getSurface());
                values.put(Constants.TABLE_PARCELLE_DATE_SOUMISSION_NAME, parcelle.getDateSoumission());
                String date = parcelle.getDateCreated();
                if(date.trim().equals(""))
                    values.put(Constants.TABLE_PARCELLE_DATE_CREATED_NAME, Utils.getCurrentDate());
                else
                    values.put(Constants.TABLE_PARCELLE_DATE_CREATED_NAME, date);
                values.put(Constants.TABLE_PARCELLE_GUIDE_NOM_NAME, parcelle.getNameGuide());
                values.put(Constants.TABLE_PARCELLE_GUIDE_EMAIL_NAME, parcelle.getEmailGuide());
                values.put(Constants.TABLE_PARCELLE_GUIDE_TEL_NAME, parcelle.getTelGuide());
                values.put(Constants.TABLE_PARCELLE_IMAGE_URL, parcelle.getImageUrl());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_NAME, parcelle.getDateSurvol());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_YEAR_NAME, parcelle.getFlYear());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_MONTH_NAME, parcelle.getFlMonth());
                values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_DAY_NAME, parcelle.getFlDay());
                values.put(Constants.TABLE_PARCELLE_IS_DELETE_NAME, parcelle.getIsDeleteAsInt());
                values.put(Constants.TABLE_PARCELLE_IS_NEW_NAME, parcelle.getIsNewAsInt());
                values.put(Constants.TABLE_PARCELLE_ID_ON_SERVER_NAME, parcelle.getIdOnServer());
                values.put(Constants.TABLE_PARCELLE_IS_FLY_DONE_NAME, parcelle.getIsFlDoneAsInt());

                dba.update(Constants.TABLE_PARCELLE, values, Constants.TABLE_PARCELLE_KEY_ID + "=?", new String[]{updateId});
                dba.close();

                deletePoints(parcelle.getParcelleId());

                deleteCultures(parcelle.getParcelleId());

                deleteCompagny(parcelle.getParcelleId());

                // save Points
                for (Point dm: parcelle.getPointsList()){
                    addPoint(dm, parcelle.getParcelleId());
                }

                // save culture
                for (Culture dm: parcelle.getCultureList()){
                    addCulture(dm, parcelle.getParcelleId());

                }
                // save company
                addCompany(parcelle.getCompany(), parcelle.getParcelleId());
                return true;
            }

        } catch (Exception e){
            e.printStackTrace();

        }
        return false;
    }

    public boolean updateParcelleFromApi(@NonNull Parcelle parcelle) {
        try {
            parcelle.setNew(false);

            SQLiteDatabase dba = this.getWritableDatabase();
            String updateId = Integer.toString(parcelle.getParcelleId());
            ContentValues values = new ContentValues();

            values.put(Constants.TABLE_PARCELLE_ZONE_NAME, parcelle.getZone());
            values.put(Constants.TABLE_PARCELLE_DISTANCE_NAME, parcelle.getDistance());
            values.put(Constants.TABLE_PARCELLE_COUCHE_NAME, parcelle.getCouche());
            values.put(Constants.TABLE_PARCELLE_REGION_NAME, parcelle.getRegion());
            values.put(Constants.TABLE_PARCELLE_REGION_ID_NAME, parcelle.getRegionId());
            values.put(Constants.TABLE_PARCELLE_ENTREPRISE_ID_NAME, parcelle.getCompany().getEntrepriseId());
            values.put(Constants.TABLE_PARCELLE_PERIMETRE_NAME, parcelle.getPerimetre());
            values.put(Constants.TABLE_PARCELLE_SURFACE_NAME, parcelle.getSurface());
            values.put(Constants.TABLE_PARCELLE_DATE_SOUMISSION_NAME, parcelle.getDateSoumission());
            String date = parcelle.getDateCreated();
            if(date.trim().equals(""))
                values.put(Constants.TABLE_PARCELLE_DATE_CREATED_NAME, Utils.getCurrentDate());
            else
                values.put(Constants.TABLE_PARCELLE_DATE_CREATED_NAME, date);
            values.put(Constants.TABLE_PARCELLE_GUIDE_NOM_NAME, parcelle.getNameGuide());
            values.put(Constants.TABLE_PARCELLE_GUIDE_EMAIL_NAME, parcelle.getEmailGuide());
            values.put(Constants.TABLE_PARCELLE_GUIDE_TEL_NAME, parcelle.getTelGuide());
            values.put(Constants.TABLE_PARCELLE_IMAGE_URL, parcelle.getImageUrl());
            values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_NAME, parcelle.getDateSurvol());
            values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_YEAR_NAME, parcelle.getFlYear());
            values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_MONTH_NAME, parcelle.getFlMonth());
            values.put(Constants.TABLE_PARCELLE_DATE_SURVOL_DAY_NAME, parcelle.getFlDay());
            values.put(Constants.TABLE_PARCELLE_IS_DELETE_NAME, parcelle.getIsDeleteAsInt());
            values.put(Constants.TABLE_PARCELLE_IS_NEW_NAME, parcelle.getIsNewAsInt());
            values.put(Constants.TABLE_PARCELLE_ID_ON_SERVER_NAME, parcelle.getIdOnServer());
            values.put(Constants.TABLE_PARCELLE_IS_FLY_DONE_NAME, parcelle.getIsFlDoneAsInt());

            dba.update(Constants.TABLE_PARCELLE, values, Constants.TABLE_PARCELLE_KEY_ID + "=?", new String[]{updateId});
            dba.close();

            deletePoints(parcelle.getParcelleId());

            deleteCultures(parcelle.getParcelleId());

            deleteCompagny(parcelle.getParcelleId());

            // save Points
            for (Point dm: parcelle.getPointsList()){
                addPoint(dm, parcelle.getParcelleId());
            }

            // save culture
            for (Culture dm: parcelle.getCultureList()){
                addCulture(dm, parcelle.getParcelleId());

            }
            // save company
            addCompany(parcelle.getCompany(), parcelle.getParcelleId());

            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void updatePoint(@NonNull Point point){
        try {
            SQLiteDatabase dba = this.getWritableDatabase();
            String updateId = Integer.toString(point.getPointId());
            ContentValues values = new ContentValues();


            values.put(Constants.TABLE_POINT_PARCELLE_ID_NAME, point.getParcelleId());
            values.put(Constants.TABLE_POINT_RANG_NAME, point.getRang());
            values.put(Constants.TABLE_POINT_LATITUDE_NAME, point.getLatitude());
            values.put(Constants.TABLE_POINT_LONGITUDE_NAME, point.getLongitude());
            values.put(Constants.TABLE_POINT_IS_REFERNCE_NAME, point.getIsReferenceAsInt());
            values.put(Constants.TABLE_POINT_IS_CENTER_NAME, point.getIsCenterAsInt());

            dba.update(Constants.TABLE_POINT, values, Constants.TABLE_POINT_KEY + "=?", new String[]{updateId});
            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void updateCompany(@NonNull Company com){
        try {
            SQLiteDatabase dba = this.getWritableDatabase();
            String updateId = Integer.toString(com.getEntrepriseId());
            ContentValues values = new ContentValues();


            values.put(Constants.TABLE_ENTREPRISE_PARCELLE_ID_NAME, com.getParcelleId());
            values.put(Constants.TABLE_ENTREPRISE_NAME_NAME, com.getName());
            values.put(Constants.TABLE_ENTREPRISE_PHONE_NAME, com.getName());
            values.put(Constants.TABLE_ENTREPRISE_EMAIL_NAME, com.getEmail());
            values.put(Constants.TABLE_ENTREPRISE_ADDRESS_NAME, com.getAddress());
            values.put(Constants.TABLE_ENTREPRISE_REFERENCE_NAME, com.getRef());
            values.put(Constants.TABLE_ENTREPRISE_WEB_SITE_NAME, com.getWebSite());

            dba.update(Constants.TABLE_ENTREPRISE, values, Constants.TABLE_ENTREPRISE_KEY + "=?", new String[]{updateId});
            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateCulture(@NonNull Culture culture){
        try {
            SQLiteDatabase dba = this.getWritableDatabase();
            String updateId = Integer.toString(culture.getCultureId());
            ContentValues values = new ContentValues();

            values.put(Constants.TABLE_CULTURE_PARCELLE_ID_NAME, culture.getParcelleId());
            values.put(Constants.TABLE_CULTURE_ID_NAME, culture.getCultureId());
            values.put(Constants.TABLE_CULTURE_TYPE_ID_NAME, culture.getTypeCultureId());
            values.put(Constants.TABLE_CULTURE_NAME, culture.getName());
            values.put(Constants.TABLE_CULTURE_TYPE_NAME, culture.getTypeCulture());

            dba.update(Constants.TABLE_CULTURE, values, Constants.TABLE_CULTURE_KEY + "=?", new String[]{updateId});
            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //Get all parcelle
    public @NonNull CopyOnWriteArrayList<Parcelle> getParcelles(){
        // clear old
        onInit();
        //load point
        getPoints();
        // load culture
        getCultures();
        // load company
        getCompanies();


        try{
            SQLiteDatabase dba = this.getReadableDatabase();

            Cursor cursor = dba.query(Constants.TABLE_PARCELLE,
                    new String[]{
                            Constants.TABLE_PARCELLE_KEY_ID,
                            Constants.TABLE_PARCELLE_ZONE_NAME,
                            Constants.TABLE_PARCELLE_DISTANCE_NAME,
                            Constants.TABLE_PARCELLE_COUCHE_NAME,
                            Constants.TABLE_PARCELLE_REGION_NAME,
                            Constants.TABLE_PARCELLE_REGION_ID_NAME,
                            Constants.TABLE_PARCELLE_ENTREPRISE_ID_NAME,
                            Constants.TABLE_PARCELLE_PERIMETRE_NAME,
                            Constants.TABLE_PARCELLE_SURFACE_NAME,
                            Constants.TABLE_PARCELLE_DATE_SOUMISSION_NAME,
                            Constants.TABLE_PARCELLE_DATE_CREATED_NAME,
                            Constants.TABLE_PARCELLE_GUIDE_NOM_NAME,
                            Constants.TABLE_PARCELLE_IS_DELETE_NAME,
                            Constants.TABLE_PARCELLE_IS_NEW_NAME,
                            Constants.TABLE_PARCELLE_ID_ON_SERVER_NAME,
                            Constants.TABLE_PARCELLE_IS_FLY_DONE_NAME,
                            Constants.TABLE_PARCELLE_GUIDE_EMAIL_NAME,
                            Constants.TABLE_PARCELLE_IMAGE_URL,
                            Constants.TABLE_PARCELLE_DATE_SURVOL_NAME,
                            Constants.TABLE_PARCELLE_DATE_SURVOL_YEAR_NAME,
                            Constants.TABLE_PARCELLE_DATE_SURVOL_MONTH_NAME,
                            Constants.TABLE_PARCELLE_DATE_SURVOL_DAY_NAME,
                            Constants.TABLE_PARCELLE_GUIDE_TEL_NAME}, null, null, null, null, Constants.TABLE_PARCELLE_KEY_ID + " DESC ");

            if (cursor.moveToFirst()) {
                do {

                    final Parcelle parcelle = new Parcelle();
                    parcelle.setParcelleId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_PARCELLE_KEY_ID)));
                    parcelle.setZone(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_ZONE_NAME)));
                    parcelle.setDistance(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_DISTANCE_NAME)));
                    parcelle.setCouche(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_COUCHE_NAME)));
                    parcelle.setRegion(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_REGION_NAME)));
                    parcelle.setRegionId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_PARCELLE_REGION_ID_NAME)));
                    parcelle.getCompany().setEntrepriseId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_PARCELLE_ENTREPRISE_ID_NAME)));
                    parcelle.setDelete(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_PARCELLE_IS_DELETE_NAME)));
                    parcelle.setNew(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_PARCELLE_IS_NEW_NAME)));
                    parcelle.setIdOnServer(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_PARCELLE_ID_ON_SERVER_NAME)));
                    parcelle.setFlyDone(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_PARCELLE_IS_FLY_DONE_NAME)));
                    parcelle.setPerimetre(cursor.getLong(cursor.getColumnIndex(Constants.TABLE_PARCELLE_PERIMETRE_NAME)));
                    parcelle.setSurface(cursor.getLong(cursor.getColumnIndex(Constants.TABLE_PARCELLE_SURFACE_NAME)));

                    parcelle.setDateSoumission(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_DATE_SOUMISSION_NAME)));
                    parcelle.setDateCreated(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_DATE_CREATED_NAME)));
                    parcelle.setNameGuide(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_GUIDE_NOM_NAME)));
                    parcelle.setEmailGuide(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_GUIDE_EMAIL_NAME)));
                    parcelle.setTelGuide(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_GUIDE_TEL_NAME)));
                    parcelle.setImageUrl(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_IMAGE_URL)));
                    parcelle.setDateSurvol(cursor.getString(cursor.getColumnIndex(Constants.TABLE_PARCELLE_DATE_SURVOL_NAME)));
                    parcelle.setFlYear(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_PARCELLE_DATE_SURVOL_YEAR_NAME)));
                    parcelle.setFlMonth(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_PARCELLE_DATE_SURVOL_MONTH_NAME)));
                    parcelle.setFlDay(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_PARCELLE_DATE_SURVOL_DAY_NAME)));

                    // Polygon
                    parcelle.setPointsList(getPointsById(parcelle.getParcelleId()));
                    // cultures
                    parcelle.setCultureList(getCultureById(parcelle.getParcelleId()));
                    // company
                    parcelle.setCompany(getCompanyById(parcelle.getParcelleId()));

                    parcelleList.add(parcelle);

                }while (cursor.moveToNext());

            }

            cursor.close();
            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return parcelleList;

    }

    private @NonNull ArrayList<Point> getPointsById(int parcelleCustomId) {
        ArrayList<Point> list = new ArrayList<Point>();
        if (pointList.size() > 0){
            for(Point dm :  pointList){
                if (dm.getParcelleId() == parcelleCustomId)
                    list.add(dm);
            }
        }

        return list;
    }

    private @NonNull Company getCompanyById(int parcelleId) {
        Company company = null;
        if (companyList.size() > 0){
            for(Company dm :  companyList){
                if (dm.getParcelleId() == parcelleId)
                    company = dm;
            }
        }

        if (company == null)
            company = new Company();

        return company;
    }

    private @NonNull ArrayList<Culture> getCultureById(int parcelleCustomId) {
        ArrayList<Culture> list = new ArrayList<Culture>();
        if (cultureList.size() > 0){
            for(Culture dm :  cultureList){
                if (dm.getParcelleId() == parcelleCustomId)
                    list.add(dm);
            }
        }

        return list;
    }


    //Get all points
    public ArrayList<Point> getPoints(){

        pointList.clear();
        try {


            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.query(Constants.TABLE_POINT,
                    new String[]{
                            Constants.TABLE_POINT_KEY,
                            Constants.TABLE_POINT_PARCELLE_ID_NAME,
                            Constants.TABLE_POINT_RANG_NAME,
                            Constants.TABLE_POINT_LATITUDE_NAME,
                            Constants.TABLE_POINT_LONGITUDE_NAME,
                            Constants.TABLE_POINT_IS_REFERNCE_NAME,
                            Constants.TABLE_POINT_IS_CENTER_NAME}, null, null, null, null, null);


            //loop through...
            if (cursor.moveToFirst()) {
                do {

                    Point pt = new Point();

                    pt.setLatitude(cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_POINT_LATITUDE_NAME )));
                    pt.setLongitude(cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_POINT_LONGITUDE_NAME)));
                    pt.setRang(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_POINT_RANG_NAME )));
                    pt.setCenter(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_POINT_IS_CENTER_NAME)));
                    pt.setReference(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_POINT_IS_REFERNCE_NAME)));
                    pt.setParcelleId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_POINT_PARCELLE_ID_NAME)));


                    pointList.add(pt);

                }while (cursor.moveToNext());


            }

            cursor.close();
            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return pointList;

    }

    //Get all points
    public CopyOnWriteArrayList<Company> getCompanies(){

        companyList.clear();
        try {


            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.query(Constants.TABLE_ENTREPRISE,
                    new String[]{
                            Constants.TABLE_ENTREPRISE_KEY,
                            Constants.TABLE_ENTREPRISE_PARCELLE_ID_NAME,
                            Constants.TABLE_ENTREPRISE_NAME_NAME,
                            Constants.TABLE_ENTREPRISE_PHONE_NAME,
                            Constants.TABLE_ENTREPRISE_EMAIL_NAME,
                            Constants.TABLE_ENTREPRISE_ADDRESS_NAME,
                            Constants.TABLE_ENTREPRISE_REFERENCE_NAME,
                            Constants.TABLE_ENTREPRISE_WEB_SITE_NAME,
                            }, null, null, null, null, null);


            //loop through...
            if (cursor.moveToFirst()) {
                do {

                    Company com = new Company();

                    com.setName(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ENTREPRISE_NAME_NAME )));
                    com.setTel(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ENTREPRISE_PHONE_NAME )));
                    com.setEmail(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ENTREPRISE_EMAIL_NAME )));
                    com.setAddress(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ENTREPRISE_ADDRESS_NAME )));
                    com.setRef(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ENTREPRISE_REFERENCE_NAME )));
                    com.setWebSite(cursor.getString(cursor.getColumnIndex(Constants.TABLE_ENTREPRISE_WEB_SITE_NAME )));

                    com.setParcelleId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_ENTREPRISE_PARCELLE_ID_NAME)));


                    companyList.add(com);

                }while (cursor.moveToNext());


            }

            cursor.close();
            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return companyList;

    }

    //Get all points
    public ArrayList<Culture> getCultures(){

        cultureList.clear();
        try {

            SQLiteDatabase dba = this.getReadableDatabase();
            Cursor cursor = dba.query(Constants.TABLE_CULTURE,
                    new String[]{
                            Constants.TABLE_CULTURE_KEY,
                            Constants.TABLE_CULTURE_PARCELLE_ID_NAME,
                            Constants.TABLE_CULTURE_ID_NAME,
                            Constants.TABLE_CULTURE_TYPE_ID_NAME,
                            Constants.TABLE_CULTURE_NAME,
                            Constants.TABLE_CULTURE_TYPE_NAME}, null, null, null, null, null);


            //loop through...
            if (cursor.moveToFirst()) {
                do {

                    Culture culture = new Culture();

                    culture.setParcelleId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_CULTURE_PARCELLE_ID_NAME )));
                    culture.setCultureId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_CULTURE_ID_NAME )));
                    culture.setTypeCultureId(cursor.getInt(cursor.getColumnIndex(Constants.TABLE_CULTURE_TYPE_ID_NAME )));
                    culture.setName(cursor.getString(cursor.getColumnIndex(Constants.TABLE_CULTURE_NAME)));
                    culture.setTypeCulture(cursor.getString(cursor.getColumnIndex(Constants.TABLE_CULTURE_TYPE_NAME )));


                    cultureList.add(culture);
                }while (cursor.moveToNext());


            }
            cursor.close();
            dba.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return cultureList;

    }


    public void onInit(){
        parcelleList.clear();
        pointList.clear();
        companyList.clear();
        cultureList.clear();
        // reload everything in all Array
    }

    public void onReload(){
        onInit();
    }


    public void saveCultureList(@NonNull Context ctx, @NonNull JSONArray jArray){
        try {
            Save.defaultSaveString(Constants.PREF_CULTURE_LIST, jArray.toString(), ctx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void saveRegionsList(@NonNull Context ctx, @NonNull JSONArray array){
        try {
            Save.defaultSaveString(Constants.PREF_REGION, array.toString(), ctx);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveOnlineDBVersion(@NonNull Context ctx, @NonNull String objectString){
        try {
            Save.defaultSaveString(Constants.PREF_DATABASE_VERSION_DATE, objectString, ctx);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void clearCultures(@NonNull Context ctx){
        try {
            Save.defaultSaveString(Constants.PREF_CULTURE_LIST,"", ctx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearRegions(@NonNull Context ctx){
        try {
            Save.defaultSaveString(Constants.PREF_REGION,"", ctx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearDbVersionDate(@NonNull Context ctx){
        try {
            Save.defaultSaveString(Constants.PREF_DATABASE_VERSION_DATE,"", ctx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public @NonNull ArrayList<Culture> getCultures(@NonNull Context ctx){
        ArrayList<Culture> list =  new ArrayList<Culture>();

        try {
            String res = Save.defaultLoadString(Constants.PREF_CULTURE_LIST, ctx);
            JSONArray array = new JSONArray(res);
            for (int i = 0; i < array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                Culture culture = new Culture();
                culture.setCultureId(obj.getInt("id"));
                culture.setName(obj.getString("itemName"));
                culture.setTypeCultureId(obj.getInt("type_de_culture"));
                list.add(culture);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return list;

    }

    public @NonNull ArrayList<Region> getRegions(@NonNull Context ctx){
        ArrayList<Region> list = new ArrayList<Region>();

        try {
            String res = Save.defaultLoadString(Constants.PREF_REGION, ctx);
            JSONArray array = new JSONArray(res);
            for (int i = 0; i < array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                Region region = new Region();
                region.setRegionId(obj.getInt("id"));
                region.setName(obj.getString("nom_region"));
                list.add(region);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public int getAnId(){
        int newId = 0;
        int great = 0;
        newId = getParcelleTotalItems();
        if (newId> 0){
            for (Parcelle dm: getParcelles()){
                if (dm.getParcelleId() > great)
                    great = dm.getParcelleId();
            }
        }

        if (great != 0)
            return great + 1;
        return  newId;
    }

    public  CopyOnWriteArrayList<Parcelle> synchroDatabase(@NonNull final JSONArray pArray, final Context ctx){
        onInit();
        getCultures();

        if (ctx != null){

            try {
                // get All Parcels
                for (int i = 0; i< pArray.length(); i++){
                    JSONObject obj = pArray.getJSONObject(i);

                    boolean isDeleted = false;
                    isDeleted = obj.getBoolean("isDelete");

                    if (!isDeleted){
                        Parcelle parc = new Parcelle();
                        parc.setIdOnServer(Integer.valueOf(obj.getString("id")));
//                      parc.setEntrepriseId(Integer.valueOf("YOU KEY"));
                        parc.setDateSoumission(obj.getString("date_soumission"));
                        parc.setDateCreated(obj.getString("date_creation"));
                        parc.setRegion(obj.getString("region"));
                        parc.setNameGuide(obj.getString("guide_name"));
                        parc.setEmailGuide(obj.getString("guide_email"));
                        parc.setTelGuide(obj.getString("guide_phone"));
                        parc.setSurface(obj.getDouble("superficie"));
                        parc.setPerimetre(obj.getDouble("perimetre"));
                        parc.setZone(obj.getString("zone"));
                        parc.setParcelleId(Integer.valueOf(obj.getString("id_android")));

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
                            for (Culture cDm: cultureList){
                                if (cDm.getName().toString().trim().equals(c.getName().toString().trim()))
                                    c.setCultureId(cDm.getCultureId());
                            }
                            cultures.add(c);
                        }

                        // Sav all Culture
                        if (cultures.size() > 0){
                            parc.setCultureList(cultures);
                        }

                        parcelleList.add(parc);
                    }

                }

                // Save all Parcelle
                if (parcelleList.size() > 0){
                    for (Parcelle dm : parcelleList)
                        addParcelle(dm, ctx, false);
                }
                // update RecyclerView

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return parcelleList;
    }


    public void resetDB(@NonNull final  Context ctx) throws Exception {
        // close All connection
        getInstance().close();
        ctx.deleteDatabase(Constants.DATABASE_NAME);
        // delete all Image
        CacheImage cache = new CacheImage(ctx);
        cache.cleanCache();


    }



}
