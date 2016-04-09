package com.mahoneyapps.tapitwellington;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Created by Brendan on 3/29/2016.
 */
public class Malthouse extends Fragment {

    ListView mListView;
    String mBrewery = "Malthouse";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_list_view, container, false);


        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        TextView toolbar = (TextView)activity.findViewById(R.id.toolbar_title);
        toolbar.setText("Malthouse Tap List");

        mListView = (ListView) view.findViewById(R.id.list_view);

        if (container != null) {
            container.removeAllViews();
        }

        new MalthouseTask(getActivity()).execute();

        return view;

    }

    private class MalthouseTask extends AsyncTask<Void, Void, ArrayList<String>> {
        Context mContext;
        String url = "http://themalthouse.co.nz/on-tap";

        public MalthouseTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> arrayListOfBeers = new ArrayList<>();
            String beerName = "";

            try {
                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("td");
                for (Element malthouseBeer : elements) {
                    beerName = malthouseBeer.text();
                    Log.d("malthouse beer name", beerName);
                    int indexOfComma = beerName.indexOf(",");
                    String finalBeerName = beerName.substring(0, indexOfComma);
                    Log.d("malthouse beer name", finalBeerName);
                    arrayListOfBeers.add(finalBeerName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return arrayListOfBeers;
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

                    FragmentManager fm = getFragmentManager();
                    for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++){
                        Log.i("Fragment found", fm.getBackStackEntryAt(entry).toString());
                    }
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
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