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
        // Set toolbar text
        toolbar.setText("Little Beer Quarter");

        mListView = (ListView) view.findViewById(R.id.list_view);

        // prevents views from overlaying one another
        if (container != null) {
            container.removeAllViews();
        }

        // Start Async task
        new LBQTask(getActivity()).execute();

        return view;
    }

    private class LBQTask extends AsyncTask<Void, Void, ArrayList<String>> {
        Context mContext;

        // URL to connect to
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
                    /* Determine if the 2nd letter of the first word is Uppercase. HTML is sloppy and no way
                    to pinpoint just Beer name (will return type, brewery, etc). Pattern noticed that all beers
                    have first word capitalized. Check that beer length is greater than one to avoid NPE, then
                    check for capitalization.

                    Then return text and add to ArrayList
                     */

                    //if beer name isn't null
                    if (beerName.length() > 1){
                        Log.d("length greater than 1", beerName);
                        secondLetter = beerName.charAt(1);

                        // and if the second letter is upper case
                        if (Character.isUpperCase(secondLetter)){
                            Log.d("upper case", beerName);
                            int spaceIndex = beerName.indexOf(" ");
                            String firstWord = beerName.substring(0, spaceIndex);
                            String restOfWord = beerName.substring(spaceIndex);
                            Log.d("first word", firstWord);
                            Log.d("rest of word", restOfWord);

                            // for each character in the first word (which will be uppercase),
                            // for the second characters and beyond, replace upper case letter with lower case
                            for (int i=1; i < firstWord.length(); i++){

                                char oldChar = beerName.charAt(i);
                                Log.d("old char", String.valueOf(oldChar));
                                char newChar = Character.toLowerCase(oldChar);
                                Log.d("new char", String.valueOf(newChar));
                                firstWord = firstWord.replace(oldChar, newChar);
                                Log.d("new first word", firstWord);
                                beerName = firstWord + restOfWord;
                                String firstLetter = String.valueOf(beerName.charAt(0));
                                String newFirstLetter = beerName.valueOf(Character.toUpperCase(firstLetter.charAt(0)));
                                beerName = beerName.replaceFirst(firstLetter, newFirstLetter);

                            }

                            // add this newly formatted beer name to the array list
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
            // Animation adapter to swing in from left
            SwingLeftInAnimationAdapter swingAdapter = new SwingLeftInAnimationAdapter(adapter);
            swingAdapter.setAbsListView(mListView);
            mListView.setAdapter(swingAdapter);


            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get position of item clicked, pass that as beer name
                    String spotInList = mListView.getItemAtPosition(position).toString();
                    Log.d("listview click test", spotInList);

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

