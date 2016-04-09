package com.mahoneyapps.tapitwellington;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
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
public class HopGarden extends Fragment {

    ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_list_view, container, false);


        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        TextView toolbar = (TextView)activity.findViewById(R.id.toolbar_title);
        toolbar.setText("Hop Garden Tap List");

        mListView = (ListView) view.findViewById(R.id.list_view);

        if (container != null) {
            container.removeAllViews();
        }

        new HopGardenTask(getActivity()).execute();

        return view;

    }

    private class HopGardenTask extends AsyncTask<Void, Void, ArrayList<String>> {
        Context mContext;
        String url = "http://www.thehopgarden.co.nz/the-taps.html";

        public HopGardenTask(Context context){
            mContext = context;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> arrayListOfBeers = new ArrayList<>();
            String beerName = "";

            try {
                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select(":contains(Renaissance)");
                Log.d("hop garden", String.valueOf(elements));
                for (Element HopGardenBeer : elements){
                    beerName = HopGardenBeer.text();
                    Log.d("hop garden text", beerName);
                    arrayListOfBeers.add(beerName);
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

                    FragmentTransaction ft = ((FragmentActivity) mContext).getFragmentManager().beginTransaction();
                    Bundle args = new Bundle();
                    args.putString("beer", spotInList);
                    SelectedBeerView sbv = new SelectedBeerView();
                    sbv.setArguments(args);

                    ft.replace(R.id.frame, sbv).addToBackStack("add sbv").commit();

                }
            });


        }
    }

}

