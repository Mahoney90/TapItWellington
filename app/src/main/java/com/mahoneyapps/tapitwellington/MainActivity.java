package com.mahoneyapps.tapitwellington;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static android.support.v4.app.FragmentManager fragmentManager;
    public static Context mContext;
    Menu mMenu;
    GoogleApiClient mGoogleApiClient;
    SharedPreferences mSharedPreferences;
    private static final String LOGIN_STATE = "Login State";
    private int TAB_POSITION;
    private String mUserName;

    String name = "Brendan";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set Toolbar for App
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();

        // add TabLayout and two tabs with titles
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_list));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_map));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_leaderboard));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//        mUserName = getIntent().getExtras().getString("user name");
//        Log.d("the user name MA", mUserName);

//        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        editor.putString("the user name", mUserName);
//        Log.d("put string user name", mUserName);
//        editor.apply();

//        final FragmentTwo frag2 = new FragmentTwo();
//        Bundle bundle = new Bundle();
//        bundle.putString("text", name);
//        frag2.setArguments(bundle);

//
//        mSessionManager = new SessionManager(this);
//        if (!mSessionManager.isLoggedIn()){
//            Log.d("Opening session manager", "okay");
//            Intent openLogin = new Intent(this, SignInActivity.class);
//            startActivity(openLogin);
//        }


        // if there is no saved bundle, add FragmentOne XML to the activity launch frame
        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().add(R.id.frame, new MyBeerHistory()).commit();
        }


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                TAB_POSITION = tab.getPosition();
                switch (tab.getPosition()) {
                    case 0:
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.frame, new MyBeerHistory()).commit();
                        break;
                    case 1:
                        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                        ft1.replace(R.id.frame, new PubListFragment()).commit();
                        break;
                    // if the 2nd tab is selected, open the Map

                    case 2:
                        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                        ft2.replace(R.id.frame, new BeerMap()).commit();
                        break;

                    case 3:
                        FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                        ft3.replace(R.id.frame, new Leaderboard()).commit();
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        Log.d("start onBackPressed: " + getFragmentManager().getBackStackEntryCount(), "woo");
        if (getFragmentManager().getBackStackEntryCount() != 0){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.home){
            getFragmentManager().popBackStack();

            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.sign_out){
            FacebookSdk.sdkInitialize(getApplicationContext());
            LoginManager.getInstance().logOut();
            mSharedPreferences = getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, SignInChooser.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.sign_out_google){
            GoogleSignIn google = new GoogleSignIn();
            google.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
