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
        mId = id;
        mDate = new Date();
    }

    //Definimos los get de la clase
    public UUID getId(){return mId;}
    public String getTitle(){return mTitle;}
    public Date getDate() {return mDate;}
    public boolean isSolved(){return mSolved;}
    public String getSuspect(){return mSuspect;}
    public String getPhotoFilename(){return "IMG_"+getId().toString()+".jpg";}

    //Definimos los set de la clase
    public void setTitle(String title) {mTitle = title;}
    public void setDate(Date date) {mDate = date;}
    public void setSolved(boolean solved) {mSolved = solved;}
    public void setSuspect(String suspect){ mSuspect = suspect;}

}
