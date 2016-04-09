package com.mahoneyapps.tapitwellington;

/**
 * Created by Brendan on 3/23/2016.
 */

public class BeerCatalogue {

    int m_ID;
    String mName;
    int mRating;
    String mUserName;
    String mBrewery;

    public BeerCatalogue(int id, String name, int rating, String userName, String brewery){
        this.m_ID = id;
        this.mName = name;
        this.mRating = rating;
        this.mUserName = userName;
        this.mBrewery = brewery;
    }

    public BeerCatalogue(String name, int rating, String userName, String brewery){
        this.mName = name;
        this.mRating = rating;
        this.mUserName = userName;
        this.mBrewery = brewery;
    }

    public BeerCatalogue(){

    }

    public int getID(){
        return m_ID;
    }

    public void setID(int id){
        this.m_ID = id;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public int getRating(){
        return mRating;
    }

    public void setRating(int rating){
        mRating = rating;
    }

    public String getUserName(){
        return mUserName;
    }

    public void setUserName(String userName){
        mUserName = userName;
    }

    public String getBrewery(){
        return mBrewery;
    }
    public void setBrewery(String brewery){
        mBrewery = brewery;
    }
}

