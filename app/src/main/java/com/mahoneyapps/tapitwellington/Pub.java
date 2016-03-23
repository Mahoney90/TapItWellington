package com.mahoneyapps.tapitwellington;

/**
 * Created by Brendan on 3/23/2016.
 */
public class Pub {

    // getters and setters to add Pubs and get Pub names
    String mName;

    public Pub(String name){
        this.mName = name;
    }

    public void setPubName(String name){
        this.mName = name;
    }

    public String getPubName(){
        return mName;
    }
}
