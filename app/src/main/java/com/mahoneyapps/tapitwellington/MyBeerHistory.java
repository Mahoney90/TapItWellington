package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    public static MyBeerHistory newHistory(Bundle b){
        MyBeerHistory mbh = new MyBeerHistory();
        mbh.setArguments(b);
        mUsername = String.valueOf(b);
        int start = (mUsername.indexOf("=")) + 1;
        int end = mUsername.indexOf("}");
        mUsername = mUsername.substring(start, end);
        Log.d("the user name mbh", mUsername);

        return mbh;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("on attach", "attaching");
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
        Log.d("on create view", "on create");
        View view = inflater.inflate(R.layout.beer_history_layout, container, false);
        view.setClipToOutline(true);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        TextView toolbar = (TextView)activity.findViewById(R.id.toolbar_title);
        toolbar.setText("My History");
//        activity.getSupportActionBar().setTitle("Recent Beers");

        if (container != null) {
            container.removeAllViews();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HistoryAdapter(mBeerList, mRatingList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(Color.rgb(255, 255, 255)).size(90).build());

//        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(GoogleSignIn.class);
//        mUsername = mSharedPreferences.getString("account name", "duh");
//        Log.d("the user name in MBH", mUsername);

        getBeerName();

        return view;
    }

    private void getBeerName() {
        Log.d("get beer name", "getting name and ratings");

        TextView tvName;
        TextView tvBrewery;
//        mBeerList = new ArrayList<>();
//        mRatingList = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.beer_history_layout, null, false);
        tvName = (TextView) view.findViewById(R.id.beer_name_for_history);
        tvBrewery = (TextView) view.findViewById(R.id.brewery);
        mBeerHandler = new BeerHandler(getActivity());
//        for (HashMap<String, Integer> line : beerHandler.getUserHistory("Debbie")){
//            Log.d("hashmap while", String.valueOf(line));
//        }
        List<String> beersToAdd = new ArrayList<>();
        Log.d("user beer history", beersToAdd.toString());
        if (mBeerHandler.getUserBeerHistory(mUsername) == null ){
            Log.d("less than 1", "returned null");
            Toast.makeText(mContext, "DRINK MORE!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("1 or more","didn't return null");
            beersToAdd = mBeerHandler.getUserBeerHistory(mUsername);

        }

        mBeerList.clear();
        mRatingList.clear();

        //        for (int i=0; i < 10; i++){
        for (String beer : beersToAdd) {
//            Log.d("get key", String.valueOf(entry.getKey()));
//            String key = String.valueOf(entry.getKey());
            tvName.setText(beer);
            mBeerList.add(beer);
            Log.d("beer list size", String.valueOf(mBeerList.size()));
            mBrewery = mBeerHandler.getBrewery(beer);
            Log.d("brewery", mBrewery);

        }
        LinkedHashSet<String> userSet = new LinkedHashSet<>();
        userSet.addAll(mBeerList);
        mBeerList.clear();
        Log.d("beer list size2", String.valueOf(mBeerList.size()));
        mBeerList.addAll(userSet);
        Collections.reverse(mBeerList);
        Log.d("beer list size3", String.valueOf(mBeerList.size()));
        Log.d("size of linked set", String.valueOf(userSet.size()));
        mAdapter.notifyDataSetChanged();

    }

    private void getBrewery() {
        BeerCatalogue catalogue = new BeerCatalogue();
        catalogue.getBrewery();
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.BeerHistoryHolder>{

        public HistoryAdapter(List<String> beerList, List<Integer> ratingList){
            Log.d("history adapter", "adapter");
            mBeerList = beerList;
            mRatingList = ratingList;
        }


        @Override
        public BeerHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("on create view holder", "okay");
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.my_beer_history, parent, false);
            view.setOnClickListener(mClickListener);

            BeerHistoryHolder beerHistoryHolder = new BeerHistoryHolder(view, mContext);
            return beerHistoryHolder;
        }

        @Override
        public void onBindViewHolder(BeerHistoryHolder holder, int position) {
            Log.d("on bind view holder", "okay");
            holder.tvName.setText(String.valueOf(mBeerList.get(position)));
            String beer = String.valueOf(mBeerList.get(position));
            mBeerHandler = new BeerHandler(getActivity());
            String rating = mBeerHandler.getUserAvgRating(beer, mUsername);
            holder.tvRating.setText(rating);
            holder.tvBrewery.setText(mBrewery);

            if (position % 2 == 0){
                holder.relativeLayout.setBackgroundResource(R.drawable.shape_dark);
            }

            double ratingForCheck = Double.parseDouble(rating);
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
            Log.d("size", String.valueOf(mBeerList.size()));
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
                Log.d("beer history holder", "holder");
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
        Log.d("on resume", "resume");
        super.onResume();
        mBeerList = new ArrayList<>();
        mRatingList = new ArrayList<>();

        getBeerName();
    }
//
//    public void makeToast(){
////            LayoutInflater inflater = LayoutInflater.from(mContext);
////
////            View view = inflater.inflate(R.layout.my_beer_history, null);
////            TextView textview = (TextView) view.findViewById(R.id.beer_name);
////            textview.setText("Drink!!!");
//
//        Log.d("calling make toast", "toast");
//        Toast.makeText(mContext, "Start drinking " + mUsername + "!", Toast.LENGTH_LONG).show();
//    }

}


