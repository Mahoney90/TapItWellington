package com.mahoneyapps.tapitwellington;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;

/**
 * Created by Brendan on 3/29/2016.
 */
public class SignInChooser extends AppCompatActivity {

    public static final String LOGIN_STATE = "Login State";
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.sign_in_chooser);

        mSharedPreferences = getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
        boolean isLoggedIn = mSharedPreferences.getBoolean("loggedin", false);
        String name = mSharedPreferences.getString("name", "duh");

        Log.d("is chooser Logged in?", String.valueOf(isLoggedIn));
        if (mSharedPreferences.getBoolean("loggedin", false) && !mSharedPreferences.getString("name", "duh").equals("duh")){
            Log.d("intent to main", "good");
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }

    public void signInToGoogle(View view){
        Log.d("signing in to google", "yes..");
        Intent openGoogleSignIn = new Intent(this, GoogleSignIn.class);
        startActivity(openGoogleSignIn);

    }

    public void signInToFacebook(View view){
        Intent openFacebookSignIn = new Intent(this, SignInActivity.class);
        startActivity(openFacebookSignIn);
    }
}
