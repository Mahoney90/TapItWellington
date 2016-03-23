package com.mahoneyapps.tapitwellington;

/**
 * Created by Brendan on 3/23/2016.
 */

public class BeerCatalogue {

    int m_ID;
    String mName;
    int mRating;
    String mUserName;

    public BeerCatalogue(int id, String name, int rating, String userName){
        this.m_ID = id;
        this.mName = name;
        this.mRating = rating;
        this.mUserName = userName;
    }

    public BeerCatalogue(String name, int rating, String userName){
        this.mName = name;
        this.mRating = rating;
        this.mUserName = userName;
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
}

