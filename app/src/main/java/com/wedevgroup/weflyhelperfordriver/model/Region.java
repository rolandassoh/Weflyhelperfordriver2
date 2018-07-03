package com.wedevgroup.weflyhelperfordriver.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;


/**
 * Created by admin on 04/04/2018.
 */

public class Region {
    private static final long serialVersionUID = 10L;
    private int regionId;
    private String name;
    private String description;

    public Region(int regionId, String name, String description){
        this.regionId = regionId;
        this.name = name;
        this.description = description;
    }

    public Region(){

    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getName() {
        if (name == null)
            name = "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        if (description == null)
            description = "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
