package com.wedevgroup.weflyhelperfordriver.model;

import java.io.Serializable;

/**
 * Created by admin on 02/04/2018.
 */

public class Company implements Serializable{
    private static final long serialVersionUID = 10L;
    private int parcelleId;
    private int entrepriseId ;
    private String name;
    private String email;
    private String address;
    private String tel;
    private String ref;
    private String webSite;
    private String internalNote;
    private String password;
    private int parcelCount;
    private String imageUrl;

    public Company(int entrepriseId, String name, String email, String tel, String ref, String internalNote, String address) {
        this.entrepriseId = entrepriseId;
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.ref = ref;
        this.address = address;
        this.internalNote = internalNote;
    }

    public Company() {

    }

    public int getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(int entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public String getName() {
        if (name == null)
            name = "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        if(email == null)
            email = "";
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        if(tel == null)
            tel = "";
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getRef() {
        if(ref == null)
            ref = "";
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getInternalNote() {
        if (internalNote ==  null)
            internalNote = "";
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }

    public String getWebSite() {
        if(webSite == null)
            webSite = "";
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPassword() {
        if (password ==  null)
            password = "";
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        if (address == null)
            address= "";
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getParcelCount() {
        return parcelCount;
    }

    public void setParcelCount(int parcelCount) {
        this.parcelCount = parcelCount;
    }

    public String getImageUrl() {
        if (imageUrl == null)
            imageUrl = "";
        return imageUrl;
    }

    public int getParcelleId() {
        return parcelleId;
    }

    public void setParcelleId(int parcelleId) {
        this.parcelleId = parcelleId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
