package com.mahoneyapps.tapitwellington;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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


        // if there is no saved bundle, add FragmentOne XML to the activity launch frame
        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().add(R.id.frame, new MyBeerHistory()).commit();
        }


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                TAB_POSITION = tab.getPosition();
                // Open Fragment depending upon which tab in the tab layout is selected
                switch (tab.getPosition()) {
                    case 0:
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.frame, new MyBeerHistory()).commit();
                        break;
                    case 1:
                        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                        ft1.replace(R.id.frame, new PubListFragment()).addToBackStack(null).commit();
                        break;
                    case 2:
                        FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                        ft2.replace(R.id.frame, new BeerMap()).addToBackStack(null).commit();
                        break;
                    case 3:
                        FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                        ft3.replace(R.id.frame, new Leaderboard()).addToBackStack(null).commit();
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

        // Pop the backstack if there are entries on the backstack
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent openAbout = new Intent(MainActivity.this, About.class);
            startActivity(openAbout);
            return true;
        }
//        if (id == R.id.sign_out){
//            FacebookSdk.sdkInitialize(getApplicationContext());
//            LoginManager.getInstance().logOut();
//            mSharedPreferences = getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = mSharedPreferences.edit();
//            editor.clear();
//            editor.apply();
//            Intent intent = new Intent(this, SignInChooser.class);
//            startActivity(intent);
//            finish();
//        }
//        if (id == R.id.sign_out_google){
//            GoogleSignIn google = new GoogleSignIn();
//            google.signOut();
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
