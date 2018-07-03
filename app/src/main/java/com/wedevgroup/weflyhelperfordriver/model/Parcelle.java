package com.wedevgroup.weflyhelperfordriver.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wedevgroup.weflyhelperfordriver.R;
import com.wedevgroup.weflyhelperfordriver.utils.CacheImage;
import com.wedevgroup.weflyhelperfordriver.utils.Constants;
import com.wedevgroup.weflyhelperfordriver.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by admin on 27/03/2018.
 */

public class Parcelle implements Serializable {
    private static final long serialVersionUID = 10L;
    private int parcelleId;
    private int regionId;
    private double perimetre;
    private double surface;
    private String dateSoumission;
    private String dateCreated;
    private String nomGuide;
    private String emailGuide;
    private String telGuide;
    private String couche;
    private String region;
    private String zone;
    private String distance;
    private boolean isDelete;
    private boolean isNew;
    private int idOnServer;
    private String imageUrl;
    private ArrayList<Point> pointsList = new ArrayList<Point>();
    private ArrayList<Culture> cultureList = new ArrayList<Culture>();
    private final String TAG = getClass().getSimpleName();
    private Company company;
    private String dateSurvol;
    private boolean isFlyDone;
    private int flYear, flMonth, flDay;


    public Parcelle(int id, String nomGuide, String emailGuide, String telGuide, double perimetre, double surface, String dateCreated, String dateSoumission, String couche, String region, String zone, @NonNull ArrayList<Point> list, @NonNull ArrayList<Culture> cultureList){

        this.parcelleId = id;
        this.perimetre = perimetre;
        this.surface = surface;
        this.dateSoumission = dateSoumission;
        this.dateCreated = dateCreated;
        this.nomGuide = nomGuide;
        this.emailGuide = emailGuide;
        this.telGuide = telGuide;
        this.couche = couche;
        this.region = region;
        this.zone = zone;
        this.pointsList.addAll(list);
        this.cultureList.addAll(cultureList);
    }

    public Parcelle(){

    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    public double getPerimetre() {
        return perimetre;
    }

    public void setPerimetre(double perimetre) {
        this.perimetre = perimetre;
    }

    public double getSurface() {
        return surface;
    }

    public void setSurface(double surface) {
        this.surface = surface;
    }


    public String getDateSoumission() {
        if(dateSoumission == null)
            dateSoumission = "";
        return dateSoumission;
    }

    public void setDateSoumission(String dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public String getDateCreated() {
        if(dateCreated == null)
            dateCreated = "";
        return dateCreated;
    }

    public @NonNull String getDateCreatedFormatted(){
        String date = "";

        try {
            Date pDate = Utils.getDateFromStringCopy(getDateCreated());
            date = Utils.convertDate(pDate.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;

    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ArrayList<Point> getPointsList() {
        for (Point dm : pointsList){
            dm.setParcelleId(getParcelleId());
        }
        return pointsList;
    }

    public String getNameGuide() {
        if (nomGuide == null)
            nomGuide = "";
        return nomGuide;
    }

    public void setNameGuide(String nomGuide) {
        this.nomGuide = nomGuide;
    }

    public String getEmailGuide() {
        if (emailGuide == null)
            emailGuide = "";
        return emailGuide;
    }

    public void setEmailGuide(String emailGuide) {
        this.emailGuide = emailGuide;
    }

    public String getTelGuide() {
        if (telGuide == null)
            telGuide = "";
        return telGuide;
    }

    public void setTelGuide(String telGuide) {
        this.telGuide = telGuide;
    }

    public void setPointsList(@NonNull ArrayList<Point> pointsList) {
        this.pointsList.clear();
        this.pointsList.addAll(pointsList);
    }

    public int getParcelleId() {
        return parcelleId;
    }

    public void setParcelleId(int parcelleId) {
        this.parcelleId = parcelleId;
    }


    public String getCouche() {
        if (couche == null)
            couche = "";
        return couche;
    }

    public void setCouche(String couche) {
        this.couche = couche;
    }

    public String getRegion() {
        if (region == null)
            region = "";
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public String getZone() {
        if (zone ==  null)
            zone = "";
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public ArrayList<Culture> getCultureList() {
        return cultureList;
    }

    public void setCultureList(@NonNull ArrayList<Culture> cultureList) {
        this.cultureList.clear();
        this.cultureList.addAll(cultureList);
    }

    public @NonNull String parcelleToStringWithServId(int index){
        if (getPointsList().size() >  0 && getCultureList().size() > 0){
            ArrayList<Point> pointList = new ArrayList<Point>();
            ArrayList<Culture> cultures = new ArrayList<Culture>();
            pointList.addAll(getPointsList());
            cultures.addAll(getCultureList());


            String parcelStr =

                            "%polygone"+ index +"%"+":" +
                            "[ " + "%{°poly°"+ " : "     +

                            "{" +   "°perimetre°"+      ":"  +  "°"+ getPerimetre()         + "°"+ ","+
                            "°surface°"+                ":"  +  "°"+ getSurface()           + "°"+ ","+
                            "°dateCreated°"+            ":"  +  "°"+ getDateCreated()       + "°"+ ","+
                            "°nomGuide°"+               ":"  +  "°"+ getNameGuide()          + "°"+ ","+
                            "°mid°"+                    ":"  +  "°"+ getParcelleId()        + "°"+ ","+
                            "°id°"+                     ":"  +  "°"+ getIdOnServer()        + "°"+ ","+
                            "°emailGuide°"+             ":"  +  "°"+ getEmailGuide()        + "°"+ ","+
                            "°telGuide°"+               ":"  +  "°"+ getTelGuide()          + "°"+ ","+
                            "°couche°"+                 ":"  +  "°"+ getCouche()            + "°"+ ","+
                            "°region°"+                 ":"  +  "°"+ getRegionId()                    + "°"+ ","+
                            "°isDelete°"+               ":"  +  "°"+ getIsDeleteAsString()            + "°"+ ","+ "".replace('t','T' ).replace('f','F' )+
                            "°zone°"+                   ":"  +  "°"+ getZone()              + "°"+ ","+
                            "°entrepriseId°"+           ":"  +  "°"+  "1"     + "°" +"}"+ "," +
                            "°points°"+ ": [";
            String pointFormated = "";

            if (getPointsList().size() > 1){
                for (int i = 0; i <= (pointList.size() -2); i++){
                    pointFormated += "{"+
                            "°la&i&ude°"+                ":"  + "°"+ pointList.get(i).getLatitude()        + "°"+ ","+
                            "°longi&ude°"+               ":"  + "°"+ pointList.get(i).getLongitude()       + "°"+ ","+
                            "°rang°"+                    ":"  + "°"+ pointList.get(i).getRang()            + "°"+ ","+
                            "°isRe;erence°"+             ":"  + "°"+ pointList.get(i).isReference()        + "°"+ ","+
                            "°isCen&er°"+                ":"  + "°"+ pointList.get(i).isCenter()           + "°"+ "}, ";
                }
            }

            // for last point
            pointFormated += "{"+
                    "°la&i&ude°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).getLatitude()        + "°"+ ","+
                    "°longi&ude°"+               ":"  + "°"+ pointList.get(pointList.size() -1 ).getLongitude()       + "°"+ ","+
                    "°rang°"+                    ":"  + "°"+ pointList.get(pointList.size() -1 ).getRang()            + "°"+ ","+
                    "°isRe;erence°"+             ":"  + "°"+ pointList.get(pointList.size() -1 ).isReference()        + "°"+ ","+
                    "°isCen&er°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).isCenter()           + "°"+ "} ]" + "," ;

            // convert  true TO True and false to False
            pointFormated = pointFormated.replace('t','T' );
            pointFormated = pointFormated.replace('f','F' );
            pointFormated = pointFormated.replaceAll("&","t" );
            pointFormated = pointFormated.replaceAll(";","f" );

            parcelStr += pointFormated;




            // add cultures
            parcelStr += "°culture°"+ ": [";

            if (cultures.size() > 1){
                for (int i = 0; i <= (cultures.size() -2); i++){
                    parcelStr += "{"+
                            "°id°"+                     ":"  +  "°"+ cultures.get(i).getCultureId()                             + "°" + "}, ";
                }
            }

            // for last culture
            parcelStr += "{"+
                    "°id°"+                      ":"  +   "°"+ cultures.get(cultures.size() -1 ).getCultureId()          + "°" + "} ]";

            parcelStr +=    "}%"
                    +"]"

            ;// close json object

            parcelStr = parcelStr.replace('%','"');

            return parcelStr.replaceAll("°", "'");
        }else{
            String s = "";
            return s;
        }


    }

    public @NonNull String parcelleToString(int index){
        if (getPointsList().size() >  0 && getCultureList().size() > 0){
            ArrayList<Point> pointList = new ArrayList<Point>();
            ArrayList<Culture> cultures = new ArrayList<Culture>();
            pointList.addAll(getPointsList());
            cultures.addAll(getCultureList());


            String parcelStr =

                    "%polygone"+ index +"%"+":" +
                            "[ " + "%{°poly°"+ " : "     +

                            "{" +   "°perimetre°"+      ":"  +  "°"+ getPerimetre()         + "°"+ ","+
                            "°surface°"+                ":"  +  "°"+ getSurface()           + "°"+ ","+
                            "°dateCreated°"+            ":"  +  "°"+ getDateCreated()       + "°"+ ","+
                            "°nomGuide°"+               ":"  +  "°"+ getNameGuide()          + "°"+ ","+
                            "°mid°"+                    ":"  +  "°"+ getParcelleId()        + "°"+ ","+
                            "°emailGuide°"+             ":"  +  "°"+ getEmailGuide()        + "°"+ ","+
                            "°telGuide°"+               ":"  +  "°"+ getTelGuide()          + "°"+ ","+
                            "°couche°"+                 ":"  +  "°"+ getCouche()            + "°"+ ","+
                            "°region°"+                 ":"  +  "°"+ getRegionId()                    + "°"+ ","+
                            "°isDelete°"+               ":"  +  "°"+ getIsDeleteAsString()            + "°"+ ","+ "".replace('t','T' ).replace('f','F' )+
                            "°zone°"+                   ":"  +  "°"+ getZone()              + "°"+ ","+
                            "°entrepriseId°"+           ":"  +  "°"+  "1"     + "°" +"}"+ "," +
                            "°points°"+ ": [";
            String pointFormated = "";

            if (getPointsList().size() > 1){
                for (int i = 0; i <= (pointList.size() -2); i++){
                    pointFormated += "{"+
                            "°la&i&ude°"+                ":"  + "°"+ pointList.get(i).getLatitude()        + "°"+ ","+
                            "°longi&ude°"+               ":"  + "°"+ pointList.get(i).getLongitude()       + "°"+ ","+
                            "°rang°"+                    ":"  + "°"+ pointList.get(i).getRang()            + "°"+ ","+
                            "°isRe;erence°"+             ":"  + "°"+ pointList.get(i).isReference()        + "°"+ ","+
                            "°isCen&er°"+                ":"  + "°"+ pointList.get(i).isCenter()           + "°"+ "}, ";
                }
            }

            // for last point
            pointFormated += "{"+
                    "°la&i&ude°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).getLatitude()        + "°"+ ","+
                    "°longi&ude°"+               ":"  + "°"+ pointList.get(pointList.size() -1 ).getLongitude()       + "°"+ ","+
                    "°rang°"+                    ":"  + "°"+ pointList.get(pointList.size() -1 ).getRang()            + "°"+ ","+
                    "°isRe;erence°"+             ":"  + "°"+ pointList.get(pointList.size() -1 ).isReference()        + "°"+ ","+
                    "°isCen&er°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).isCenter()           + "°"+ "} ]" + "," ;

            // convert  true TO True and false to False
            pointFormated = pointFormated.replace('t','T' );
            pointFormated = pointFormated.replace('f','F' );
            pointFormated = pointFormated.replaceAll("&","t" );
            pointFormated = pointFormated.replaceAll(";","f" );

            parcelStr += pointFormated;




            // add cultures
            parcelStr += "°culture°"+ ": [";

            if (cultures.size() > 1){
                for (int i = 0; i <= (cultures.size() -2); i++){
                    parcelStr += "{"+
                            "°id°"+                     ":"  +  "°"+ cultures.get(i).getCultureId()                             + "°" + "}, ";
                }
            }

            // for last culture
            parcelStr += "{"+
                    "°id°"+                      ":"  +   "°"+ cultures.get(cultures.size() -1 ).getCultureId()          + "°" + "} ]";

            parcelStr +=    "}%"
                    +"]"

            ;// close json object

            parcelStr = parcelStr.replace('%','"');

            return parcelStr.replaceAll("°", "'");
        }else{
            String s = "";
            return s;
        }


    }

    public @Nullable JSONObject parcelleToJSONObjAsPostItem(){

        if (getPointsList().size() >  0 && getCultureList().size() > 0){
            ArrayList<Point> pointList = new ArrayList<Point>();
            ArrayList<Culture> cultures = new ArrayList<Culture>();
            pointList.addAll(getPointsList());
            cultures.addAll(getCultureList());


            String parcelStr =
                    "{" +
                            "%polygone"+"0"+ "%"+ ":" +
                            "[ " + "%{°poly°"+ " : "     +

                            "{" +   "°perimetre°"+              ":"  +  "°"+ getPerimetre()         + "°"+ ","+
                            "°surface°"+                ":"  +  "°"+ getSurface()           + "°"+ ","+
                            "°dateCreated°"+            ":"  +  "°"+ getDateCreated()       + "°"+ ","+
                            "°nomGuide°"+               ":"  +  "°"+ getNameGuide()          + "°"+ ","+
                            "°mid°"+                    ":"  +  "°"+ getParcelleId()        + "°"+ ","+
                            "°emailGuide°"+             ":"  +  "°"+ getEmailGuide()        + "°"+ ","+
                            "°telGuide°"+               ":"  +  "°"+ getTelGuide()          + "°"+ ","+
                            "°couche°"+                 ":"  +  "°"+ getCouche()            + "°"+ ","+
                            "°region°"+                 ":"  +  "°"+ getRegionId()                    + "°"+ ","+
                            "°isDelete°"+               ":"  +  "°"+ getIsDeleteAsString()            + "°"+ ","+ "".replace('t','T' ).replace('f','F' )+
                            "°zone°"+                   ":"  +  "°"+ getZone()              + "°"+ ","+
                            "°entrepriseId°"+           ":"  +  "°"+  "1"     + "°" +"}"+ "," +
                            "°points°"+ ": [";
            String pointFormated = "";

            if (getPointsList().size() > 1){
                for (int i = 0; i <= (pointList.size() -2); i++){
                    pointFormated += "{"+
                            "°la&i&ude°"+                ":"  + "°"+ pointList.get(i).getLatitude()        + "°"+ ","+
                            "°longi&ude°"+               ":"  + "°"+ pointList.get(i).getLongitude()       + "°"+ ","+
                            "°rang°"+                    ":"  + "°"+ pointList.get(i).getRang()            + "°"+ ","+
                            "°isRe;erence°"+             ":"  + "°"+ pointList.get(i).isReference()        + "°"+ ","+
                            "°isCen&er°"+                ":"  + "°"+ pointList.get(i).isCenter()           + "°"+ "}, ";
                }
            }

            // for last point
            pointFormated += "{"+
                    "°la&i&ude°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).getLatitude()        + "°"+ ","+
                    "°longi&ude°"+               ":"  + "°"+ pointList.get(pointList.size() -1 ).getLongitude()       + "°"+ ","+
                    "°rang°"+                    ":"  + "°"+ pointList.get(pointList.size() -1 ).getRang()            + "°"+ ","+
                    "°isRe;erence°"+             ":"  + "°"+ pointList.get(pointList.size() -1 ).isReference()        + "°"+ ","+
                    "°isCen&er°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).isCenter()           + "°"+ "} ]" + "," ;

            // convert  true TO True and false to False
            pointFormated = pointFormated.replace('t','T' );
            pointFormated = pointFormated.replace('f','F' );
            pointFormated = pointFormated.replaceAll("&","t" );
            pointFormated = pointFormated.replaceAll(";","f" );

            parcelStr += pointFormated;




            // add cultures
            parcelStr += "°culture°"+ ": [";

            if (cultures.size() > 1){
                for (int i = 0; i <= (cultures.size() -2); i++){
                    parcelStr += "{"+
                            "°id°"+                     ":"  +  "°"+ cultures.get(i).getCultureId()                             + "°" + "}, ";
                }
            }

            // for last culture
            parcelStr += "{"+
                    "°id°"+                      ":"  +   "°"+ cultures.get(cultures.size() -1 ).getCultureId()          + "°" + "} ]";

            parcelStr +=    "}%"
                    +"]" +

                    "}" ;// close json object

            parcelStr =  parcelStr.replace('%','"');
            String strFinal = parcelStr.replaceAll("°", "'");

            try {
                JSONObject resul = new JSONObject(strFinal);
                return  resul;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }else
            return null;

    }

    public @Nullable JSONObject toJsonAddSchedule() throws Exception{
        JSONObject obj = new JSONObject();
        //obj.put("isflown", false);
        obj.put("flydate", getDateSurvol());

        return obj;

    }

    public @Nullable JSONObject toJsonCancelSchedule() throws Exception{
        JSONObject obj = new JSONObject();
        obj.put("flydate", "Inconnu");

        return obj;

    }

    public @Nullable JSONObject toJsonIsDone() throws Exception{
        JSONObject obj = new JSONObject();
        //obj.put("isflown", false);
        obj.put("isflown", "True");
        return obj;

    }


    public @Nullable Bitmap getImageAsBitmap(final Context ctx){
        Bitmap bp = null;

        try {
            CacheImage cImage = new CacheImage(ctx);
            bp = cImage.load(String.valueOf(getParcelleId()));

            return bp;

            //return Utils.getResizedBitmap(bp, 1280, 960);
        }catch (Exception e){
            // load default image
            bp = Utils.drawableToBitmap(ctx, R.drawable.img_default_parcel);
        }

        return bp;
    }

    public @Nullable Bitmap getDefaultImage(final Context ctx) throws  Exception{
        Bitmap bp = null;

        bp = Utils.drawableToBitmap(ctx, R.drawable.img_default_parcel);

        return bp;
    }

    public @Nullable File getImageAsFile(final Context ctx) throws Exception{

        CacheImage cImage = new CacheImage(ctx);
        File f = cImage.getAsFile(String.valueOf(getParcelleId()));
        return f;
    }


    public String getDistance() {
        if (distance == null)
            distance = "";
        return distance;
    }

    public @Nullable Calendar getDateSurvolAsCalendar()throws Exception {
        return Utils.getDateFromString(getDateSurvol());

    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public int getIsDeleteAsInt(){
        int bool = (isDelete)? 1 : 0;
        return  bool;
    }



    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getIsNewAsInt(){
        int bool = (isNew)? 1 : 0;
        return  bool;
    }
    public void setDelete(int delete) {
        isDelete = delete == 1;
    }

    public void setNew(int aNew) {
        isNew = aNew == 1;
    }

    public String getIsDeleteAsString(){
        String res = "";
        if (isDelete)
            res = "True";
        else
            res = "False";
        return res;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(int idOnServer) {
        this.idOnServer = idOnServer;
    }

    public Company getCompany() {
        if (company == null)
            company = new Company();
        return company;
    }

    public String getImageUrl() {
        if (imageUrl == null)
            imageUrl = "";
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCompany(Company company) {
        this.company = company;
    }


    public String getDateSurvol() {
        if (dateSurvol == null)
            dateSurvol = "";
        return dateSurvol;
    }

    public void setDateSurvol(@NonNull String dateSurvol) {
        this.dateSurvol = dateSurvol;
    }

    public boolean isFlyDone() {
        return isFlyDone;
    }

    public void setFlyDone(boolean flyDone) {
        isFlyDone = flyDone;
    }

    public int getIsFlDoneAsInt(){
        int bool = (isFlyDone)? 1 : 0;
        return  bool;
    }
    public void setFlyDone(int flyDone) {
        isFlyDone = flyDone == 1;
    }

    public int getFlYear() {
        return flYear;
    }

    public void setFlYear(int flYear) {
        this.flYear = flYear;
    }

    public int getFlMonth() {
        return flMonth;
    }

    public void setFlMonth(int flMonth) {
        this.flMonth = flMonth;
    }

    public int getFlDay() {
        return flDay;
    }

    public void setFlDay(int flDay) {
        this.flDay = flDay;
    }
}
