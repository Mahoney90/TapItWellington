//package com.mahoneyapps.tapitwellington;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//
//import com.facebook.FacebookSdk;
//
///**
// * Created by Brendan on 3/29/2016.
// */
//public class SignInChooser extends AppCompatActivity {
//
//    public static final String LOGIN_STATE = "Login State";
//    SharedPreferences mSharedPreferences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Necessary to initialize Facebook SDK right away
//        FacebookSdk.sdkInitialize(this.getApplicationContext());
//        setContentView(R.layout.sign_in_chooser);
//
//        // gets login state, either true or false based on whether or not the user has previously logged in
//        mSharedPreferences = getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
//        boolean isLoggedIn = mSharedPreferences.getBoolean("loggedin", false);
////        String name = mSharedPreferences.getString("name", "duh");
//
//        // if user is logged in, go right to main activity
//
//        if (mSharedPreferences.getBoolean("loggedin", false)){
//
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
//        }
//
//    }
//
//    // Intent to open Google sign in, registered via user click on Google Login button
//    public void signInToGoogle(View view){
//
//        Intent openGoogleSignIn = new Intent(this, GoogleSignIn.class);
//        startActivity(openGoogleSignIn);
//
//    }
//
//    // Intent to open Facebook sign in, registered via user click on Facebook Login button
//    public void signInToFacebook(View view){
//        Intent openFacebookSignIn = new Intent(this, SignInActivity.class);
//        startActivity(openFacebookSignIn);
//    }
//}
