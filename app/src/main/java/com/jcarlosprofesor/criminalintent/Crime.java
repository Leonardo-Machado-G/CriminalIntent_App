package com.jcarlosprofesor.criminalintent;
import java.util.Date;
import java.util.UUID;

//Declaro la clase
public class Crime {

    //Defino los atributos de la clase
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;

    //Constructor sobrecargado para el cursor
    public Crime(){ this (UUID.randomUUID()); }
    public Crime(UUID id){
        this.mId = id;
        this.mDate = new Date();
    }

    //Definimos los get de la clase
    public UUID getId(){return this.mId;}
    public String getTitle(){return this.mTitle;}
    public Date getDate() {return this.mDate;}
    public boolean isSolved(){return this.mSolved;}
    public String getSuspect(){return this.mSuspect;}
    public String getPhotoFilename(){return "IMG_"+getId().toString()+".jpg";}

    //Definimos los set de la clase
    public void setTitle(String title) {this.mTitle = title;}
    public void setDate(Date date) {this.mDate = date;}
    public void setSolved(boolean solved) {this.mSolved = solved;}
    public void setSuspect(String suspect){this.mSuspect = suspect;}

}
