package com.mahoneyapps.tapitwellington;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.List;

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
    public static Context appContext;
    String usernameEdit;
    String passwordEdit;
    String firstNameEdit;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.sign_in_activity);
        appContext = this;

        TextView appName = (TextView) findViewById(R.id.app_name);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Arizonia-Regular.ttf");
        appName.setTypeface(typeface);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mSharedPreferences = getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);

        // retrieve login status (true = logged in, false = not logged in)
        boolean isLoggedIn = mSharedPreferences.getBoolean("loggedin", false);

        // retrieve name of user
        String theUserName = mSharedPreferences.getString("name", "Name:");

        Log.d("is Logged in?", String.valueOf(isLoggedIn));
        Log.d("user name transfer", theUserName);

        if (mSharedPreferences.getBoolean("loggedin", false)){

            // if user is already logged in, open Main Activity and pass user name as a bundle
            Log.d("intent to main", "good");
            Intent i = new Intent(GoogleSignIn.this, MainActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("user name", theUserName);
//            i.putExtras(bundle);
            mSharedPreferences = this.getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("name", theUserName);
            editor.putBoolean("loggedin", true);
            editor.apply();
            startActivity(i);
        }

        mStatusTextView = (TextView) findViewById(R.id.status);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.start_button).setOnClickListener(this);

        // initialize views in case user opts to Sign Up without Google
        final EditText usernameEditText = (EditText) findViewById(R.id.username);
        final EditText passwordEditText = (EditText) findViewById(R.id.password);
        final EditText firstNameEditText = (EditText) findViewById(R.id.first_name);
        Button submitBtn = (Button) findViewById(R.id.submit);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEdit = usernameEditText.getText().toString();
                Log.d("submit name", usernameEdit);
                passwordEdit = passwordEditText.getText().toString();
                Log.d("submit pword", passwordEdit);
                firstNameEdit = firstNameEditText.getText().toString();
                Log.d("submit first", firstNameEdit);
                BeerHandler beerHandler = new BeerHandler(appContext);
                boolean notTaken = false;
                List<String> usernames = beerHandler.checkUsername(usernameEdit);
                if (usernames != null) {
                    for (String username : usernames) {
                        if (!username.equals(usernameEdit)) {
                            notTaken = true;
                        }

                    }
                } else {
                    notTaken = true;
                }

                if (usernameEdit.length() < 4){
                    usernameEditText.setError("Please try a Username longer than 3 letters!");
                } else if (passwordEdit.length() < 6){
                    passwordEditText.setError("Please try a Password with at least 6 characters!");
                } else if (firstNameEdit.length() < 2){
                    firstNameEditText.setError("Please try a First Name of at least 2 letters!");
                } else if (!notTaken) {
                    usernameEditText.setError("Please try a different Username! This one is taken");
                } else {
                    Log.d("manual login", "woo");
                    manualLogin();
                }


            }
        });

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

    private void manualLogin() {
        mSharedPreferences = this.getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("loggedin", true);
        editor.apply();
        Intent i = new Intent(GoogleSignIn.this, MainActivity.class);
        Log.d("user name edit", usernameEdit);
        setPrefs("name", usernameEdit, this);
        startActivity(i);
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

            setPrefs("name", mDisplayName, this);
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

    public static void setPrefs(String name, String mDisplayName, Context context) {
        Log.d("Set prefs", "cool");
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString(name, mDisplayName);
        editor.commit();
    }

    public static String getPrefs(String name, Context context){
        Log.d("get prefs", "cool");
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return myPrefs.getString(name, "default");
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
