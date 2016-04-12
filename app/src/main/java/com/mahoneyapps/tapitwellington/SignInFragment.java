package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Brendan on 3/26/2016.
 */
public class SignInFragment extends Fragment {

    private TextView mTextDetails;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    SharedPreferences mSharedPreferences;
    public static final String LOGIN_STATE = "Login State";
    public static Context facebookContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookContext = getActivity();

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                updateUI();
            }
        };

        // start tracking for changes in access token or profile/user info
        mAccessTokenTracker.startTracking();
        mProfileTracker.startTracking();

    }

    private void updateWithToken(AccessToken newAccessToken) {
    }

    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
        Log.d("update UI", "yup");

        Profile profile = Profile.getCurrentProfile();
        if (profile == null){
            Log.d("Profile", "null");
        }
        if (enableButtons && profile != null){
            Log.d("Access Token", AccessToken.getCurrentAccessToken().toString());
            Log.d("update UI name", profile.getName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facebook_sign_in_activity, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);
        mTextDetails = (TextView) view.findViewById(R.id.welcome_message);

    }


    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d("success", "should work");
            AccessToken accessToken = loginResult.getAccessToken();
            Log.e("access token", accessToken.toString());
            Profile profile = Profile.getCurrentProfile();

            mSharedPreferences = getActivity().getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("name", profile.getName());
            editor.putBoolean("loggedin", false);
            editor.apply();

            displayWelcomeMessage(profile);

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d("on resume", "resuming");
        Profile profile = Profile.getCurrentProfile();
        if (profile == null) {
            Log.d("profile is null", "null");
            updateUI();
        }
        mSharedPreferences = getActivity().getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (profile != null) {
            Log.d("profile not null", "not null");
            editor.putString("name", profile.getName());
        }
        editor.putBoolean("loggedin", true);
        editor.apply();

        displayWelcomeMessage(profile);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

        // active monitoring process, so need to stop it so that it's not always running
        mAccessTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void displayWelcomeMessage (Profile profile){
        if (profile != null){
            Log.d("profile name", profile.getName());
            String name = profile.getName();
            Toast.makeText(getActivity(), "Welcome " + name, Toast.LENGTH_SHORT).show();
            mTextDetails.setText("Hi " + profile.getName());
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.replace(R.id.frame, new PubListFragment()).commit();
            Log.d("get here", "did get here");
            Intent i = new Intent(getActivity(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent ready = new Intent(getActivity(), GoogleSignIn.class);
            ready.putExtra("name", name);
            startActivity(ready);

            getActivity().finish();

        }
    }
}
