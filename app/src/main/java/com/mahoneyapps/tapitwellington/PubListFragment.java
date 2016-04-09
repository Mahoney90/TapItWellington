package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
 * Created by Brendan on 3/23/2016.
 */
public class PubListFragment extends Fragment {


    private RecyclerView mRecyclerList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public List<Pub> mPubsList;
    public static AdapterView.OnClickListener mItemClickListener;
    Context mContext;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get Activity context
        mContext = getActivity();

        // inflate our list XML for our list of Pubs
        View rootView = inflater.inflate(R.layout.pub_list_fragment, container, false);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        TextView toolbar = (TextView)activity.findViewById(R.id.toolbar_title);
        toolbar.setText("Pub List");

        if (container != null) {
            container.removeAllViews();
        }

        // initiate RecyclerList and make custom adapter to display list of pubs in recycler list
        mRecyclerList = (RecyclerView) rootView.findViewById(R.id.list_recycler_view);
        mRecyclerList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerList.setLayoutManager(mLayoutManager);

        mAdapter = new PubRecyclerViewAdapter(mPubsList);
        mRecyclerList.setAdapter(mAdapter);

        mRecyclerList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(Color.rgb(255, 255, 255)).size(90).build());

        getPubList();

        return rootView;
    }

    private void getPubList() {

        mPubsList = new ArrayList<>();

        String[] pubList = {"Garage Project", "Black Dog", "Rogue and Vaganbond", "The Malthouse",
                "Parrotdog", "Fork and Brewer", "Little Beer Quarter", "Southern Cross"};

        for (int index = 0; index < pubList.length ; index++){
            Pub p = new Pub(pubList[index]);
            mPubsList.add(index, p);
        }

        Log.v("Test", String.valueOf(mPubsList.size()));
        Log.v("Testing", String.valueOf(mPubsList));

        mAdapter.notifyDataSetChanged();
    }

    private class PubRecyclerViewAdapter extends RecyclerView.Adapter<PubRecyclerViewAdapter.PubHolder> {
        public PubRecyclerViewAdapter(List<Pub> pubsListYo) {
            mPubsList = pubsListYo;

        }

        @Override
        public PubHolder onCreateViewHolder(ViewGroup parent, int i) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.pub_item, parent, false);
            TextView pubTextView = (TextView) view.findViewById(R.id.pub_text_view);
            view.setOnClickListener(mItemClickListener);

            Log.d("context2", String.valueOf(mContext));
            PubHolder pubHolder = new PubHolder(view, mContext);
            return pubHolder;
        }

        @Override
        public void onBindViewHolder(PubHolder holder, int position) {
            holder.pubTextView.setText(mPubsList.get(position).getPubName());

            if (position % 2 == 0){
                holder.relativeLayout.setBackgroundResource(R.drawable.shape_dark);
            }
        }

        @Override
        public int getItemCount() {
            return mPubsList.size();
        }


        public class PubHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView pubTextView;
            Context mContext;
            View relativeLayout;

            public PubHolder(View itemView, Context context){
                super(itemView);
                mContext = context;
                Log.d("context3", String.valueOf(mContext));
                pubTextView = (TextView) itemView.findViewById(R.id.pub_text_view);
                pubTextView.setOnClickListener(this);
                relativeLayout = (View) itemView.findViewById(R.id.relative_layout_pub);

            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();

                Log.d("position", String.valueOf(position));

                openTapList(position);

            }

            private void openTapList(int position) {
                FragmentManager fm = getFragmentManager();
                for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++){
                    Log.i("Fragment found", fm.getBackStackEntryAt(entry).toString());
                }
                FragmentTransaction ft = ((FragmentActivity)mContext).getFragmentManager().beginTransaction();
                int viewIDToReplace = R.id.frame;
                switch (position){
                    case 0:
                        ft.replace(viewIDToReplace, new GarageProject()).addToBackStack("add gp").commit();
                        break;
                    case 1:
                        Log.d("clicked Black Dog", String.valueOf(viewIDToReplace));
                        Log.d("clicked BD ft", String.valueOf(ft));
                        Log.d("click on BD", String.valueOf(new BlackDog()));
                        ft.replace(viewIDToReplace, new BlackDog()).addToBackStack(null).commit();
                        break;
                    case 2:
                        ft.replace(viewIDToReplace, new RogueAndVagabond()).addToBackStack(null).commit();
                        break;
                    case 3:
                        ft.replace(viewIDToReplace, new Malthouse()).addToBackStack(null).commit();
                        break;
                    case 4:
                        ft.replace(viewIDToReplace, new Parrotdog()).addToBackStack(null).commit();
                        break;
                    case 5:
                        ft.replace(viewIDToReplace, new NewFork()).addToBackStack(null).commit();
                        break;
                    case 6:
                        ft.replace(viewIDToReplace, new LittleBeerQuarter()).addToBackStack(null).commit();
                        break;
                    case 7:
                        ft.replace(viewIDToReplace, new SouthernCross()).addToBackStack(null).commit();
                        break;
                    default:
                        break;
                }
            }

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mPubsList = new ArrayList<>();
        getPubList();
    }
}
