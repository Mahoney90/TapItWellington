package com.mahoneyapps.tapitwellington;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by Brendan on 3/28/2016.
 */
public class GoogleSignIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    public GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;
    private String mDisplayName;
    public static final String LOGIN_STATE = "Login State";
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        mStatusTextView = (TextView) findViewById(R.id.status);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.start_button).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

    }

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> pendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()){
            Log.d("yay", "pendingresult is done");
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = pendingResult.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.d("handle status", String.valueOf(statusCode));
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result){
        Log.d("handle sign in", "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()){
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            Log.d("account", String.valueOf(account));
            Log.d("account name", account.getDisplayName());
            mDisplayName = account.getDisplayName();

            mSharedPreferences = this.getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("name", account.getDisplayName());
            editor.putBoolean("loggedin", true);
            editor.apply();

            if (account != null) {
                mStatusTextView.setText(getString(R.string.signed_in, account.getDisplayName()));
                mStatusTextView.setText("Hi " + mDisplayName);
            }
            updateUI(true);

        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.start_button:
                moveToApp();
                break;
        }
    }

    // [start signIn]
    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 9001);
    }


    // [start signOut]
    public void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                updateUI(false);
            }
        });
    }

    public void revokeAccess(){
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                updateUI(false);
            }
        });
    }

    public void moveToApp(){
        Intent intent = new Intent(GoogleSignIn.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("DEBUG", "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog(){
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    private void updateUI(boolean signedIn){
        if (signedIn){
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);

            Intent intent = new Intent(GoogleSignIn.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", mDisplayName);
            intent.putExtras(bundle);
            Log.d("user name test bundle", String.valueOf(bundle));
            MyBeerHistory.newHistory(bundle);
            SelectedBeerView.newInstance(bundle);
            MostBeersPerUser.instance(bundle);
            HighestRatingPerUser.newInstance1(bundle);

//            startActivity(intent);
//
//            finish();
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    public static SelectedBeerView newInstance(String mDisplayName) {
        Bundle bundle = new Bundle();
        SelectedBeerView sbv = new SelectedBeerView();
        bundle.putString("username", mDisplayName);
        Log.d("bundle", String.valueOf(bundle));
        sbv.setArguments(bundle);

        return sbv;
    }

    public static MyBeerHistory newHistory(String name){
        Bundle bundle = new Bundle();
        MyBeerHistory mbh = new MyBeerHistory();
        bundle.putString("username", name);
        mbh.setArguments(bundle);

        return mbh;
    }

    public static MostBeersPerUser instance(String name){
        Bundle bundle = new Bundle();
        MostBeersPerUser userBeers = new MostBeersPerUser();
        bundle.putString("username", name);
        userBeers.setArguments(bundle);

        return userBeers;
    }

    public static HighestRatingPerUser instance1(String name){
        Bundle bundle = new Bundle();
        HighestRatingPerUser userRatings = new HighestRatingPerUser();
        bundle.putString("username", name);
        userRatings.setArguments(bundle);

        return userRatings;
    }

}
