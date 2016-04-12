package com.mahoneyapps.tapitwellington;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Brendan on 4/10/2016.
 */
public class About extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView aboutCredits = (TextView) findViewById(R.id.about_credits);
        aboutCredits.setText("Thanks to icons8.com for the badass icons!");
    }
}
