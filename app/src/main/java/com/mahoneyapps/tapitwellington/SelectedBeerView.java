package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.content.SharedPreferences;
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
    String mBrewery;
    TextView getRatingText;
    TextView getNumOfTimesHad;
    static String mUserName;
    TextView mTotalCheckIn;
    TextView mTotalRating;
    TextView mUserCheckIn;
    TextView mUserRating;
    SharedPreferences mSharedPreferences;

    public static SelectedBeerView newInstance(Bundle b){
        SelectedBeerView sbv = new SelectedBeerView();
        sbv.setArguments(b);
        mUserName = String.valueOf(b);
        int start = (mUserName.indexOf("=")) + 1;
        int end = mUserName.indexOf("}");
        mUserName = mUserName.substring(start, end);
        Log.d("the user name newinst", mUserName);

        return sbv;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.selected_beer_view, container, false);

        Log.d("testing username", "hmm");
        TextView tv = (TextView) v.findViewById(R.id.text_beer_selected);
        TextView tvBrewery = (TextView) v.findViewById(R.id.brewery);

        mBeerName = getArguments().getString("beer");
        tv.setText(mBeerName);

        mBrewery = getArguments().getString("brewery");
        Log.d("brewery", mBrewery);
//        tvBrewery.setText(mBrewery);


        Log.d("the user name in SBV", mUserName);

        mTotalCheckIn = (TextView) v.findViewById(R.id.number_total_check_in);
        mTotalRating = (TextView) v.findViewById(R.id.number_total_rating);
        mUserCheckIn = (TextView) v.findViewById(R.id.number_user_check_in);
        mUserRating = (TextView) v.findViewById(R.id.number_user_check_in_rating);


        Button button = (Button) v.findViewById(R.id.save_button);
//        Button button1 = (Button) v.findViewById(R.id.view_db);

//        getRatingText = (TextView) v.findViewById(R.id.show_rating_text);
//        getNumOfTimesHad = (TextView) v.findViewById(R.id.show_number_of_times_had);

        if (container != null) {
            container.removeAllViews();
        }

        RatingBar rateBar = (RatingBar) v.findViewById(R.id.rate_bar);

//        BeerHandler handler = new BeerHandler(getActivity());
//        BeerCatalogue catalogue = new BeerCatalogue(1, mBeerName, mRating, mUserName);
//        handler.addRating(catalogue);

        BeerCatalogue catalogue = new BeerCatalogue();

        showRating(catalogue);


        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d("new rating", String.valueOf(rating));
                mRating = Math.round(rating);
//                Toast.makeText(getActivity(), "You rated this beer " + rating + " stars", Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BeerHandler handler = new BeerHandler(getActivity());
                BeerCatalogue catalogue = new BeerCatalogue(1, mBeerName, mRating, mUserName, mBrewery);
                handler.addRating(catalogue);

                showRating(catalogue);
                Toast.makeText(getActivity(), "Thanks! Hope you enjoyed " + mBeerName, Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    private void showRating(BeerCatalogue catalogue) {
        int rating = catalogue.getRating();
//        getRatingText.setText("You have rated this beer " + rating + " stars! Thanks " + mUserName);

        BeerHandler handler = new BeerHandler(getActivity());
        int count = handler.getUserCount(mBeerName, mUserName);
        int totalCount = handler.getTotalCount(mBeerName);
        String totalAvgRating = handler.getTotalAvgRating(mBeerName);
        String userAvgRating = handler.getUserAvgRating(mBeerName, mUserName);
//        getNumOfTimesHad.setText("This beer has an average rating of " + totalAvgRating + "\n" +
//                "You have drank this beer " + count + " times \n" +
//                "Your average rating is " + userAvgRating);

        mTotalCheckIn.setText(" " + totalCount);
        if (Double.parseDouble(totalAvgRating) > 0.0){
            mTotalRating.setText(" " + totalAvgRating);
        } else {
            mTotalRating.setText("0");
        }

        mUserCheckIn.setText(" " + count);
        mUserRating.setText(" " + userAvgRating);
        if (Double.parseDouble(userAvgRating) > 0.0){
            mUserRating.setText(" " + userAvgRating);
        } else {
            mUserRating.setText("0");
        }

    }
}

