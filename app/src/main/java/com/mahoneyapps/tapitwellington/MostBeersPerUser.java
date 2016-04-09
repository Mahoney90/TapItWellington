package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brendan on 4/8/2016.
 */
public class MostBeersPerUser extends Fragment {


    RecyclerView mRecyclerView;
    public static AdapterView.OnClickListener mClickListener;
    Context mContext;
    public List<String> mBeerList = new ArrayList<>();
    public List<Integer> mCountList = new ArrayList<>();
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    BeerHandler mBeerHandler;
    static String mUserName;

    public static MostBeersPerUser instance(Bundle b){
        MostBeersPerUser userBeers = new MostBeersPerUser();
        userBeers.setArguments(b);
        mUserName = String.valueOf(b);
        int start = (mUserName.indexOf("=")) + 1;
        int end = mUserName.indexOf("}");
        mUserName = mUserName.substring(start, end);

        Log.d("mbpu user name", mUserName);

        return userBeers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.pub_list_fragment, container, false);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        TextView toolbar = (TextView) activity.findViewById(R.id.toolbar_title);
        toolbar.setText(mUserName + "'s Most Popular Beers");
//        activity.getSupportActionBar().setTitle("Recent Beers");

        if (container != null) {
            container.removeAllViews();
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MostBeersAdapter(mBeerList, mCountList);
        mRecyclerView.setAdapter(mAdapter);

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
        beersToAdd = mBeerHandler.getBeerLeadersByUser(mUserName);

        mBeerList.clear();

        for (String beer : beersToAdd){
            beerName.setText(beer);
            mBeerList.add(beer);
        }

        mAdapter.notifyDataSetChanged();
    }

    private class MostBeersAdapter extends RecyclerView.Adapter<MostBeersAdapter.MostBeersHolder> {

        public MostBeersAdapter(List<String> beerList, List<Integer> countList) {
            mBeerList = beerList;
            mCountList = countList;
        }

        @Override
        public MostBeersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("on create view holder", "okay");
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.leaderboard_row, parent, false);
            view.setOnClickListener(mClickListener);

            MostBeersHolder mostBeersHolder = new MostBeersHolder(view, mContext);
            return mostBeersHolder;
        }

        @Override
        public void onBindViewHolder(MostBeersHolder holder, int position) {
            holder.beerName.setText(String.valueOf(mBeerList.get(position)));

            String beer = String.valueOf(mBeerList.get(position));
            BeerHandler beerHandler = new BeerHandler(getActivity());
            int beerCount = beerHandler.getTotalCount(beer);
            holder.count.setText(String.valueOf(beerCount));
            holder.position.setText(String.valueOf(position + 1) + ".");

            if (position % 2 == 0){
                holder.relativeLayout.setBackgroundResource(R.drawable.shape_dark);
            }
        }

        @Override
        public int getItemCount() {
            Log.d("size", String.valueOf(mBeerList.size()));
            return mBeerList.size();
        }

        public class MostBeersHolder extends RecyclerView.ViewHolder{

            TextView beerName;
            TextView count;
            TextView position;
            View relativeLayout;
            Context mContext;

            public MostBeersHolder(View itemView, Context context) {
                super(itemView);
                mContext = context;
                beerName = (TextView) itemView.findViewById(R.id.leaderboard_beer_name);
                count = (TextView) itemView.findViewById(R.id.leaderboard_count);
                position = (TextView) itemView.findViewById(R.id.leaderboard_position);
                relativeLayout = (View) itemView.findViewById(R.id.relative_layout_leaderboard);
            }
        }

    }
}
