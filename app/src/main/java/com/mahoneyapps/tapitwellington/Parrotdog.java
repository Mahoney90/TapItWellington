package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Brendan on 3/29/2016.
 */
public class Parrotdog extends Fragment {
    ListView mListView;
    String mBrewery = "Parrotdog";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_list_view, container, false);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        TextView toolbar = (TextView)activity.findViewById(R.id.toolbar_title);
        // change toolbar title
        toolbar.setText("Parrotdog");

        mListView = (ListView) view.findViewById(R.id.list_view);

        // prevents views from overlaying one another
        if (container != null) {
            container.removeAllViews();
        }

        // Start Async task
        new ParrotDogTask(getActivity()).execute();

        return view;
    }


    private class ParrotDogTask extends AsyncTask<Void, Void, ArrayList<String>> {
        Context mContext;
        ProgressDialog progress = new ProgressDialog(getActivity());

        // Tap List URL to connect to in background
        String url1 = "http://parrotdog.co.nz/cellardoor/";
        String beerName1 = "";
        ArrayList<String> arr_beerName1 = new ArrayList<String>();

        public ParrotDogTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(getActivity());
            progress.setCancelable(false);
            progress.setMessage("Getting tap list..");
            progress.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {
                Document doc1 = Jsoup.connect(url1).get();
                Elements element1 = doc1.select(".beer-circle p");

                // get text of beer name and add beer to beer array list
                for (Element parrot : element1){
                    beerName1 = parrot.text();
                    arr_beerName1.add(beerName1);
                }
            } catch(IOException e){
                e.printStackTrace();
            }

            return arr_beerName1;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            progress.dismiss();

            ArrayAdapter adapter = new ArrayAdapter<String>(mContext, R.layout.pub_item, R.id.pub_text_view, result);

            // Animation adapter to swing in from left
            SwingLeftInAnimationAdapter swingAdapter = new SwingLeftInAnimationAdapter(adapter);
            swingAdapter.setAbsListView(mListView);
            mListView.setAdapter(swingAdapter);


            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get position of item clicked, pass that as beer name
                    String spotInList = mListView.getItemAtPosition(position).toString();

                    // Add bundle to Selected Beer View fragment, passing the beer name and brewery/pub name
                    FragmentTransaction ft = ((FragmentActivity) mContext).getFragmentManager().beginTransaction();
                    Bundle args = new Bundle();
                    args.putString("beer", spotInList);
                    args.putString("brewery", mBrewery);
                    SelectedBeerView sbv = new SelectedBeerView();
                    sbv.setArguments(args);

                    // Add transaction to backstack for proper back navigation
                    ft.replace(R.id.frame, sbv).addToBackStack("add sbv").commit();

                }
            });


        }
    }

}