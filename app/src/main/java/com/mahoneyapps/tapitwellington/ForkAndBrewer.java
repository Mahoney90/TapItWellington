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

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by Brendan on 3/29/2016.
 */
public class ForkAndBrewer extends Fragment {

    ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_list_view, container, false);

        mListView = (ListView) view.findViewById(R.id.list_view);

        if (container != null) {
            container.removeAllViews();
        }

        new ForkBrewerTask(getActivity()).execute();

        return view;
    }

    private class ForkBrewerTask extends AsyncTask<Void, Void, ArrayList<String>> {
        Context mContext;
        String url = "http://forkandbrewer.co.nz/brew/brewpage#5";

        public ForkBrewerTask(Context context) {
            mContext = context;
        }

        public ArrayList<String> queryItem(BufferedReader br) throws ParserConfigurationException, IOException, SAXException {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc;
            doc = builder.parse(new InputStream() {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            });

            XPathFactory xPathFactory = XPathFactory.newInstance();

            XPath xPath = xPathFactory.newXPath();
            XPathExpression xPathExpression;
            String xPathPattern;

            ArrayList<String> beers = new ArrayList<>();


            xPathPattern = "//li" + "[@id='page-5']/div[position()>2]/span[text()]";
            Log.d("Tag", xPathPattern);


            //*[@id="page-5"]/div[3]/span
            //*[@id="page-5"]/div[4]/span
            try {
                // Compile the XPath expression
                xPathExpression = xPath.compile(xPathPattern);

                // Run the query and get a node set
                Object result = xPathExpression.evaluate(doc, XPathConstants.NODESET);

                // Cast the result to a DOM NodeList
                NodeList nodes = (NodeList) result;
                for (int i = 0; i < nodes.getLength(); i++) {
                    beers.add(nodes.item(i).getNodeValue());
                }
            } catch (XPathExpressionException xee) {
                Log.d("Tag", "used xpathPattern: " + xPathPattern);
            }


            return (beers);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            ArrayList<String> beers = new ArrayList<>();
            try {
                URL newUrl = new URL("http://forkandbrewer.co.nz/brew/brewpage#5");
                Log.d("open stream", "woo");
                BufferedReader reader = new BufferedReader(new InputStreamReader(newUrl.openStream()));
                while ((reader.readLine()) != null){
                    Log.d("read line", "woo");
                    beers = queryItem(reader);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            return beers;
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

