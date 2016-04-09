package com.mahoneyapps.tapitwellington;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    String url1 = "http://parrotdog.co.nz/cellardoor/";
    String mBrewery = "Parrotdog";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.beer_list_view, container, false);

        mListView = (ListView) view.findViewById(R.id.list_view);

        if (container != null) {
            container.removeAllViews();
        }

        new ParrotDogTask(getActivity()).execute();

        return view;
    }


    private class ParrotDogTask extends AsyncTask<Void, Void, ArrayList<String>> {
        Context mContext;
        String beerName1 = "";
        ArrayList<String> arr_beerName1 = new ArrayList<String>();

        public ParrotDogTask(Context context){
            mContext = context;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {

                Document doc1 = Jsoup.connect(url1).get();
                Elements element1 = doc1.select(".beer-circle p");
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

            ArrayAdapter adapter = new ArrayAdapter<String>(mContext, R.layout.pub_item, R.id.pub_text_view, result);
            SwingLeftInAnimationAdapter swingAdapter = new SwingLeftInAnimationAdapter(adapter);
            swingAdapter.setAbsListView(mListView);
            mListView.setAdapter(swingAdapter);


            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String spotInList = mListView.getItemAtPosition(position).toString();
                    Log.d("listview click test", spotInList);

                    FragmentTransaction ft = ((FragmentActivity) mContext).getFragmentManager().beginTransaction();
                    Bundle args = new Bundle();
                    args.putString("beer", spotInList);
                    args.putString("brewery", mBrewery);
                    SelectedBeerView sbv = new SelectedBeerView();
                    sbv.setArguments(args);


                    ft.replace(R.id.frame, sbv).addToBackStack("add sbv").commit();

                }
            });


        }
    }

}