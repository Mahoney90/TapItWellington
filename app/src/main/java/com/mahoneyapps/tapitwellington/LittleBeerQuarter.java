package com.mahoneyapps.tapitwellington;

import android.app.FragmentManager;
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
public class LittleBeerQuarter extends Fragment {

    ListView mListView;
    String mBrewery = "Little Beer Quarter";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_list_view, container, false);


        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        TextView toolbar = (TextView)activity.findViewById(R.id.toolbar_title);
        toolbar.setText("Little Beer Quarter Tap List");

        mListView = (ListView) view.findViewById(R.id.list_view);

        if (container != null) {
            container.removeAllViews();
        }

        new LBQTask(getActivity()).execute();

        return view;
    }

    private class LBQTask extends AsyncTask<Void, Void, ArrayList<String>> {
        Context mContext;
        String url = "http://littlebeerquarter.co.nz/now-pouring";

        public LBQTask(Context context){
            mContext = context;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> arrayListOfBeers = new ArrayList<>();
            String beerName = "";
            char secondLetter;

            try {
                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("body > div.texture > div > div > div.main-content " +
                                                "> div > div > div > div:gt(0)");
                for (Element LBQ : elements){
                    beerName = LBQ.text();
                    if (beerName.length() > 1){
                        Log.d("length greater than 1", beerName);
                        secondLetter = beerName.charAt(1);

                        if (Character.isUpperCase(secondLetter)){
                            Log.d("upper case", beerName);
                            arrayListOfBeers.add(beerName);
                        }

                    }

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

    private boolean testForUpperCase(String beerInfo){

        int indexOfSecondWord = beerInfo.indexOf(" ");
        Log.d("index", String.valueOf(indexOfSecondWord));

        if (indexOfSecondWord > 0){
        String beerTitle = beerInfo.substring(0, indexOfSecondWord);
            Log.d("testing index title", beerTitle);
        for (int i=0; i < beerTitle.length(); i++) {
            char character = beerTitle.charAt(i);
            char character2 = beerTitle.charAt(2);
            boolean test = Character.isUpperCase(character);
            if (Character.isUpperCase(character2)) {
                Log.d("testing index", beerTitle);
                return true;
            }
            return false;
        }
            return false;
        }
        return false;
    }

}

