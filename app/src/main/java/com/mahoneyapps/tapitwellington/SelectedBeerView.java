package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Brendan on 3/23/2016.
 */
public class SelectedBeerView extends Fragment {
    String mBeerName;
    int mRating;
    TextView getRatingText;
    TextView getNumOfTimesHad;
    static String mUserName;

    public static SelectedBeerView newInstance(Bundle b){
        SelectedBeerView sbv = new SelectedBeerView();
        sbv.setArguments(b);
        mUserName = String.valueOf(b);
        int start = (mUserName.indexOf("=")) + 1;
        int end = mUserName.indexOf("}");
        mUserName = mUserName.substring(start, end);

        return sbv;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.selected_beer_view, container, false);

        Log.d("testing username", "hmm");
        TextView tv = (TextView) v.findViewById(R.id.text_beer_selected);

        mBeerName = getArguments().getString("beer");
        tv.setText(mBeerName);



        Button button = (Button) v.findViewById(R.id.save_button);
        Button button1 = (Button) v.findViewById(R.id.view_db);

        getRatingText = (TextView) v.findViewById(R.id.show_rating_text);
        getNumOfTimesHad = (TextView) v.findViewById(R.id.show_number_of_times_had);

        if (container != null) {
            container.removeAllViews();
        }

        RatingBar rateBar = (RatingBar) v.findViewById(R.id.rate_bar);


        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d("new rating", String.valueOf(rating));
                mRating = Math.round(rating);
                Toast.makeText(getActivity(), "You rated this beer " + rating + " stars", Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BeerHandler handler = new BeerHandler(getActivity());
                BeerCatalogue catalogue = new BeerCatalogue(1, mBeerName, mRating, "Debbie");
                handler.addRating(catalogue);

                showRating(catalogue);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AndroidDatabaseManager.class);
                startActivity(intent);
            }
        });

        return v;
    }

    private void showRating(BeerCatalogue catalogue) {
        int rating = catalogue.getRating();
        getRatingText.setText("You have rated this beer " + rating + " stars! Thanks " + mUserName);

        BeerHandler handler = new BeerHandler(getActivity());
        int count = handler.getCount(mBeerName);
        String totalAvgRating = handler.getTotalAvgRating(mBeerName);
        String userAvgRating = handler.getUserAvgRating(mBeerName, "Debbie");
        getNumOfTimesHad.setText("This beer has an average rating of " + totalAvgRating + "\n" +
                "You have drank this beer " + count + " times \n" +
                "Your average rating is " + userAvgRating);


    }
}

