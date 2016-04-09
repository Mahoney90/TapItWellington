package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Brendan on 4/1/2016.
 */
public class Leaderboard extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaderboard, container, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        if (container != null) {
            container.removeAllViews();
        }

        String[] leaderboardOptions = new String[]{"Most Popular Beers", "Highest Rated Beers", "Your Most Consumed Beers",
                                        "Your Highest Rated Beers"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                        android.R.layout.simple_spinner_dropdown_item, leaderboardOptions);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("position selected", String.valueOf(position));
                switch (position) {
                    case 0:
                        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                        ft.add(R.id.frame_for_spinner_leaderboard, new MostBeersFragment()).commit();
                        break;
                    case 1:
                        FragmentTransaction ft1 = getActivity().getFragmentManager().beginTransaction();
                        ft1.add(R.id.frame_for_spinner_leaderboard, new HighestRatingFragment()).commit();
                        break;
                    case 2:
                        FragmentTransaction ft2 = getActivity().getFragmentManager().beginTransaction();
                        ft2.add(R.id.frame_for_spinner_leaderboard, new MostBeersPerUser()).commit();
                        break;
                    case 3:
                        FragmentTransaction ft3 = getActivity().getFragmentManager().beginTransaction();
                        ft3.add(R.id.frame_for_spinner_leaderboard, new HighestRatingPerUser()).commit();
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

}
