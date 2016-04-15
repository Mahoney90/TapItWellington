package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by Brendan on 3/30/2016.
 */
public class MyBeerHistory extends Fragment {

    RecyclerView mRecyclerView;
    HistoryAdapter mHistoryAdapter;
    public static AdapterView.OnClickListener mClickListener;
    Context mContext;
    public List<String> mBeerList = new ArrayList<>();
    public List<Integer> mRatingList = new ArrayList<>();
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    BeerHandler mBeerHandler;
    String mCheckMarkBeer;
    String mBrewery;
    static String mUsername;
    SharedPreferences mSharedPreferences;


    // Bundle passed from Sign In to store user's name, pass to MyBeerHistory class
    public static MyBeerHistory newHistory(Bundle b){
        MyBeerHistory mbh = new MyBeerHistory();
        mbh.setArguments(b);
        mUsername = String.valueOf(b);
        int start = (mUsername.indexOf("=")) + 1;
        int end = mUsername.indexOf("}");
        mUsername = mUsername.substring(start, end);

        return mbh;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignIn newGSI = new GoogleSignIn();
        mUsername = newGSI.getPrefs("name", GoogleSignIn.appContext);

        View view = inflater.inflate(R.layout.beer_history_layout, container, false);
        view.setClipToOutline(true);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());

        TextView toolbar = (TextView)activity.findViewById(R.id.toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Risaltype.ttf");
        toolbar.setTypeface(typeface);
        // Set toolbar text
        toolbar.setText("My History");

        // prevents views from overlaying one another
        if (container != null) {
            container.removeAllViews();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Pass beer list and rating list into adapter
        mAdapter = new HistoryAdapter(mBeerList, mRatingList);
        mRecyclerView.setAdapter(mAdapter);

        // Add item divider with margin
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(Color.rgb(255, 255, 255)).size(90).build());

        getBeerName();

        return view;
    }

    private void getBeerName() {

        TextView tvName;
        TextView tvBrewery;

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.beer_history_layout, null, false);
        tvName = (TextView) view.findViewById(R.id.beer_name_for_history);
        tvBrewery = (TextView) view.findViewById(R.id.brewery);
        mBeerHandler = new BeerHandler(getActivity());

        List<String> beersToAdd = new ArrayList<>();

        if (mBeerHandler.getUserBeerHistory(mUsername) == null ){
            // user has not rated any beers

            Toast.makeText(mContext, "DRINK MORE!", Toast.LENGTH_LONG).show();
        } else {
            // user has rated beers

            // get the beer history for the user
            beersToAdd = mBeerHandler.getUserBeerHistory(mUsername);
        }

        // Clear Array Lists
        mBeerList.clear();
        mRatingList.clear();

        for (String beer : beersToAdd) {

            // add the beer to the Beer List array
            tvName.setText(beer);
            mBeerList.add(beer);

            // Pass the beer as an argument and return the brewery
            mBrewery = mBeerHandler.getBrewery(beer);
        }

        LinkedHashSet<String> userSet = new LinkedHashSet<>();
        userSet.addAll(mBeerList);

        // Clear beer list before adding Hashset
        mBeerList.clear();
        mBeerList.addAll(userSet);

        // Most recent check ins are at top
        Collections.reverse(mBeerList);

        // Notify adapter of data changes
        mAdapter.notifyDataSetChanged();

    }

    private void getBrewery() {
        BeerCatalogue catalogue = new BeerCatalogue();
        catalogue.getBrewery();
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.BeerHistoryHolder>{

        public HistoryAdapter(List<String> beerList, List<Integer> ratingList){

            mBeerList = beerList;
            mRatingList = ratingList;
        }


        @Override
        public BeerHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.my_beer_history, parent, false);
            view.setOnClickListener(mClickListener);

            BeerHistoryHolder beerHistoryHolder = new BeerHistoryHolder(view, mContext);
            return beerHistoryHolder;
        }

        @Override
        public void onBindViewHolder(BeerHistoryHolder holder, int position) {

            // Set text view equal to the beer name
            holder.tvName.setText(String.valueOf(mBeerList.get(position)));
            String beer = String.valueOf(mBeerList.get(position));
            mBeerHandler = new BeerHandler(getActivity());

            // Pass the beer name and user name as arguements to get the user's average rating for each beer
            String rating = mBeerHandler.getUserAvgRating(beer, mUsername);

            // Set text view to the recently acquired average rating
            holder.tvRating.setText(rating);

            // Set text view equal to the brewery/pub where the check-in occured
            holder.tvBrewery.setText(mBrewery);

            // Change background for even numbered list items
            if (position % 2 == 0){
                holder.relativeLayout.setBackgroundResource(R.drawable.shape_dark);
            }

            double ratingForCheck = Double.parseDouble(rating);
            // If rating is 3 or above, show a thumbs up, if it is 2 or below, show a thumbs down
            if (ratingForCheck >= 3){
                holder.thumbImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_thumb_up));
            }
            if (ratingForCheck > 2 && ratingForCheck < 3){
                holder.thumbImage.setVisibility(View.GONE);
            } else if (ratingForCheck <= 2){
                holder.thumbImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_thumb_down));
            }

        }

        @Override
        public int getItemCount() {

            // return size of beer list
            return mBeerList.size();
        }


        public class BeerHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tvName;
            TextView tvRating;
            TextView tvBrewery;
            Context mContext;
            View relativeLayout;
            ImageView thumbImage;

            public BeerHistoryHolder(View itemView, Context context){
                super(itemView);

                mContext = context;
                tvName = (TextView) itemView.findViewById(R.id.beer_name);
                tvName.setOnClickListener(this);
                tvRating = (TextView) itemView.findViewById(R.id.user_rating);
                tvBrewery = (TextView) itemView.findViewById(R.id.brewery);
                relativeLayout = (View) itemView.findViewById(R.id.relative_layout_beer_history);
                thumbImage = (ImageView) itemView.findViewById(R.id.thumb_image_beer_history);
            }

            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "hi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        mBeerList = new ArrayList<>();
        mRatingList = new ArrayList<>();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        getBeerName();
    }


}


