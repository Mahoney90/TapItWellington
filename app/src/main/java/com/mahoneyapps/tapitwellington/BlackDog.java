package com.mahoneyapps.tapitwellington;

/**
 * Created by Brendan on 4/4/2016.
 */

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
 * Created by Brendan on 3/23/2016.
 */

public class BlackDog extends Fragment {

    ListView mListView;
    String mBrewery = "Black Dog";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_list_view, container, false);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        TextView toolbar = (TextView)activity.findViewById(R.id.toolbar_title);
        toolbar.setText("Black Dog Tap List");

        mListView = (ListView) view.findViewById(R.id.list_view);

        if (container != null) {
            container.removeAllViews();
        }

        new BlackDogTask(getActivity()).execute();

        return view;
    }

    private class BlackDogTask extends AsyncTask<Void, Void, ArrayList<String>> {
        Context mContext;
//        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//        View v = inflater.inflate(R.layout.beer_list_view, null);


        String url = "http://blackdogbrewery.co.nz/the-beers/";

        public BlackDogTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> arr_beerName = new ArrayList<>();

            String beerName = "";

            try {
                Document doc = Jsoup.connect(url).get();
                Elements element = doc.select("div.maxithumbs  div.description > span.title");
                for (Element item : element){
                    beerName = item.text();

                    arr_beerName.add(beerName);
                }

            } catch(IOException e){
                e.printStackTrace();
            }

            return arr_beerName;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            ArrayAdapter adapter = new ArrayAdapter<String>(mContext, R.layout.pub_item, R.id.pub_text_view, result);
            SwingLeftInAnimationAdapter swingAdapter = new SwingLeftInAnimationAdapter(adapter);
            swingAdapter.setAbsListView(mListView);
            mListView.setAdapter(swingAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String spotInList = mListView.getItemAtPosition(position).toString();
                    Log.d("listview click test", spotInList);

                    FragmentTransaction ft = ((FragmentActivity)mContext).getFragmentManager().beginTransaction();
                    Bundle args = new Bundle();
                    args.putString("beer", spotInList);
                    args.putString("brewery", mBrewery);
                    SelectedBeerView sbv = new SelectedBeerView();
                    sbv.setArguments(args);


                    Log.d("clicked Black Dog", String.valueOf(R.id.frame));
                    Log.d("clicked BD ft", String.valueOf(ft));
                    Log.d("click on BD", String.valueOf(sbv));
                    ft.replace(R.id.frame, sbv).addToBackStack("add sbv").commit();

//                    UntappdAPICall apiCall = new UntappdAPICall();
//                    apiCall.searchForBeer(spotInList);

                }
            });

        }
    }
}


