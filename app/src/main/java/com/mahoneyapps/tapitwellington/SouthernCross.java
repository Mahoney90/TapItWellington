package com.mahoneyapps.tapitwellington;

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
 * Created by Brendan on 4/4/2016.
 */
public class SouthernCross extends Fragment {

    ListView mListView;
    String mBrewery = "Southern Cross";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_list_view, container, false);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        TextView toolbar = (TextView)activity.findViewById(R.id.toolbar_title);
        toolbar.setText("Southern Cross Tap List");

        mListView = (ListView) view.findViewById(R.id.list_view);

        if (container != null) {
            container.removeAllViews();
        }

        new SouthernCrossTask(getActivity()).execute();

        return view;
    }

    private class SouthernCrossTask extends AsyncTask<Void, Void, ArrayList<String>> {
        Context mContext;
//        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//        View v = inflater.inflate(R.layout.beer_list_view, null);


        String url = "http://www.thecross.co.nz/food-drink/drinks#tap-beers";

        public SouthernCrossTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> arr_beerName = new ArrayList<>();

            String beerName = "";
            String newBeerName = "";
            String trimmedBeerName = "";

            try {
                Document doc = Jsoup.connect(url).get();
                Elements element = doc.select("#tap-beers-tab div.menu-item > h4");
                Elements elementSpan = doc.select("#tap-beers-tab div.menu-item > h4 span");
                Log.d("SC elements", String.valueOf(element));
                Log.d("SC span elements", String.valueOf(elementSpan));
                for (Element item : element){
                    Log.d("starting", "woo");
                    beerName = item.text();
                    Log.d("beer name", beerName);

                    int indexOfDollarSign = beerName.indexOf("$");
                    if (indexOfDollarSign > 0){
                        trimmedBeerName = beerName.substring(0, indexOfDollarSign);
                    }



                    arr_beerName.add(trimmedBeerName);
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


                    ft.replace(R.id.frame, sbv).addToBackStack("add sbv").commit();

//                    UntappdAPICall apiCall = new UntappdAPICall();
//                    apiCall.searchForBeer(spotInList);

                }
            });

        }
    }
}


