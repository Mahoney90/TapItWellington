//package com.mahoneyapps.tapitwellington;
//
//import android.app.FragmentTransaction;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//
///**
// * Created by Brendan on 3/26/2016.
// */
//public class SignInActivity extends AppCompatActivity {
//
//    public static final String LOGIN_STATE = "Login State";
//    SharedPreferences mSharedPreferences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.sign_in_frame);
////
////        mSharedPreferences = getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
////
////        // retrieve login status (true = logged in, false = not logged in)
////        boolean isLoggedIn = mSharedPreferences.getBoolean("loggedin", false);
////
////        // retrieve name of user
////        String theUserName = mSharedPreferences.getString("name", "Name:");
////
////        Log.d("is Logged in?", String.valueOf(isLoggedIn));
////        Log.d("user name transfer", theUserName);
////
////        if (mSharedPreferences.getBoolean("loggedin", false)){
////
////            // if user is already logged in, open Main Activity and pass user name as a bundle
////            Log.d("intent to main", "good");
////            Intent i = new Intent(SignInActivity.this, MainActivity.class);
////            Bundle bundle = new Bundle();
////            bundle.putString("user name", theUserName);
////            i.putExtras(bundle);
////            startActivity(i);
////        } else {
//            // if not logged in, open SignInFragment to give user Login options
//
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.add(R.id.frame_for_sign_in, new SignInFragment()).commit();
////        }
//
//    }
//
//}
