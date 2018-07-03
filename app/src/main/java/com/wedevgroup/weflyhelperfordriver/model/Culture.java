package com.wedevgroup.weflyhelperfordriver.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;


import java.io.Serializable;

/**
 * Created by admin on 20/03/2018.
 *  from : MINISTÈRE
 DE L’AGRICULTURE DE L’AGROALIMENTAIRE ET DE LA FORÊT FRANCAIS
 * Dossier PAC Campagne 2015
 * lien: https://www1.telepac.agriculture.gouv.fr/telepac/pdf/tas/2015/Dossier-PAC-2015_notice_cultures-precisions.pdf
 */

public class Culture implements Serializable{
    private static final long serialVersionUID = 10L;
    private int cultureId ;
    private int typeCultureId;
    private int parcelleId;
    private String name;
    private String code;
    private String typeCulture;


    public Culture( String name, String code){
        this.name =  name;
        this.code = code;
    }

    public Culture( int cultureId, String name, String code){
        this.name =  name;
        this.code = code;
    }

    public Culture() {

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getCultureId() {
        return cultureId;
    }

    public void setCultureId(int cultureId) {
        this.cultureId = cultureId;
    }

    public String getName() {
        if ( name == null){
            name = "";
        }
        return name;
    }

    public int getTypeCultureId() {
        return typeCultureId;
    }

    public void setTypeCultureId(int typeCultureId) {
        this.typeCultureId = typeCultureId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        if (code == null){
            code = "";
        }
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }




    public int getParcelleId() {
        return parcelleId;
    }

    public void setParcelleId(int parcelleId) {
        this.parcelleId = parcelleId;
    }

    public String getTypeCulture() {
        if (typeCulture == null)
            typeCulture = "";
        return typeCulture;
    }

    public void setTypeCulture(String typeCulture) {
        this.typeCulture = typeCulture;
    }
}
