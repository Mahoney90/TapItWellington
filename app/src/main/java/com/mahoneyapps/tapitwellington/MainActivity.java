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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static android.support.v4.app.FragmentManager fragmentManager;
    public static Context mContext;
    Menu mMenu;
    GoogleApiClient mGoogleApiClient;
    SessionManager mSessionManager;
    SharedPreferences mSharedPreferences;
    private static final String LOGIN_STATE = "Login State";

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
        tabLayout.addTab(tabLayout.newTab().setText("List"));
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

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
            getFragmentManager().beginTransaction().add(R.id.frame, new PubListFragment()).commit();
        }


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add(R.id.frame, new PubListFragment()).commit();
                        break;
                    // if the 2nd tab is selected, open the Map

                    // don't need to handle 'case 0' because the Pub List fragment is
                    // instantiated already when savedInstanceState is null
                    case 1:
                        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                        ft1.add(R.id.frame, new BeerMap()).commit();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.sign_out){
            mSharedPreferences = getSharedPreferences(LOGIN_STATE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
