package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brendan on 4/1/2016.
 */
public class HighestRatingFragment extends Fragment {

    RecyclerView mRecyclerView;
    public static AdapterView.OnClickListener mClickListener;
    Context mContext;
    public List<String> mBeerList = new ArrayList<>();
    public List<Integer> mRatingList = new ArrayList<>();
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    BeerHandler mBeerHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.pub_list_fragment, container, false);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        TextView toolbar = (TextView) activity.findViewById(R.id.toolbar_title);
//         Set toolbar text
        toolbar.setText("Highest Rated");

        // prevents views from overlaying one another
        if (container != null) {
            container.removeAllViews();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Pass Beer List and Rating List to adapter
        mAdapter = new MostBeersAdapter(mBeerList, mRatingList);
        mRecyclerView.setAdapter(mAdapter);

        // Adds an item divider for the Recycler View
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(Color.rgb(153, 50, 204)).margin(40).build());

        getBeerName();

        return view;
    }

    private void getBeerName() {
        TextView beerName;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.leaderboard_row, null, false);
        beerName = (TextView) view.findViewById(R.id.leaderboard_beer_name);

        mBeerHandler = new BeerHandler(getActivity());
        List<String> beersToAdd = new ArrayList<>();
        // Get ArrayList of Beers sorted by highest rating
        beersToAdd = mBeerHandler.getBeerLeadersByRating();

        if (beersToAdd == null){
            Toast.makeText(getActivity(), "Leaderboard is blank!", Toast.LENGTH_SHORT).show();
        } else {

            // clear the BeerList, previously empty
            mBeerList.clear();

            // For each beer in the ArrayList, set the TextView equal to the beer and then add it to the recently cleared ArrayList
            for (String beer : beersToAdd) {
                if (beer.length() > 25){
                    beer = beer.substring(0, beer.length());
                }
                beerName.setText(beer);
                mBeerList.add(beer);
                // limit list to 10 entries
                if (mBeerList.size() >= 10){
                    return;
                }
            }

            // Notify our adapter to update with our newly added beers
            mAdapter.notifyDataSetChanged();
        }
    }

    private class MostBeersAdapter extends RecyclerView.Adapter<MostBeersAdapter.MostBeersHolder> {

        public MostBeersAdapter(List<String> beerList, List<Integer> ratingList) {
            mBeerList = beerList;
            mRatingList = ratingList;
        }

        @Override
        public MostBeersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.leaderboard_row, parent, false);
            view.setOnClickListener(mClickListener);

            MostBeersHolder mostBeersHolder = new MostBeersHolder(view, mContext);
            return mostBeersHolder;
        }

        @Override
        public void onBindViewHolder(MostBeersHolder holder, int position) {
            // Set text view equal to the beer name
            holder.beerName.setText(String.valueOf(mBeerList.get(position)));

            String beer = String.valueOf(mBeerList.get(position));
            BeerHandler beerHandler = new BeerHandler(getActivity());

            // Pass the beer name as an argument and return its overall average rating
            String beerRating = beerHandler.getTotalAvgRating(beer);

            // Set the text view equal to the beer rating
            holder.count.setText(String.valueOf(beerRating));

            // Setting the values of the list (i.e. 1. 2. 3.)
            holder.position.setText(String.valueOf(position + 1) + ".");

            if (position % 2 == 0){
                holder.relativeLayout.setBackgroundResource(R.drawable.shape_dark);
            }
        }

        @Override
        public int getItemCount() {
            // Return size of the beer list
            return mBeerList.size();
        }

        public class MostBeersHolder extends RecyclerView.ViewHolder{

            TextView beerName;
            TextView count;
            TextView position;
            Context mContext;
            View relativeLayout;

            public MostBeersHolder(View itemView, Context context) {
                super(itemView);
                mContext = context;
                // Initialize some text views in our Leaderboard Row (number in list, beer name, rating)
                beerName = (TextView) itemView.findViewById(R.id.leaderboard_beer_name);
                count = (TextView) itemView.findViewById(R.id.leaderboard_count);
                position = (TextView) itemView.findViewById(R.id.leaderboard_position);
                relativeLayout = (View) itemView.findViewById(R.id.relative_layout_leaderboard);
            }
        }

    }
}
