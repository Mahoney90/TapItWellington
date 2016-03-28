package com.mahoneyapps.tapitwellington;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Brendan on 3/28/2016.
 */
public class SessionManager {

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;
    Context mContext;
    private static final String PREF_NAME = "LoggedInPrefs";
    int PRIVATE_MODE = 0;
    private static final String LOGGED_IN = "LoggedIn";
    public static final String NAME = "name";

    public SessionManager(Context context){
        mContext = context;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPreferences.edit();
    }

    public void createLoginSession(String name){
        mEditor.putBoolean(LOGGED_IN, true);
        mEditor.putString(NAME, name);
        mEditor.commit();
    }

    public void checkLogin(){
        if (!this.isLoggedIn()){
            Intent openLoginIntent = new Intent(mContext, SignInActivity.class);
            mContext.startActivity(openLoginIntent);
        }
    }

    public boolean isLoggedIn(){
        return mPreferences.getBoolean(LOGGED_IN, false);
    }

    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();

        Intent startNewLogin = new Intent(mContext, SignInActivity.class);
        mContext.startActivity(startNewLogin);

    }
}
