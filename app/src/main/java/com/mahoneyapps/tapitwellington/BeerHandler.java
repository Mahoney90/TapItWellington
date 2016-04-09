package com.mahoneyapps.tapitwellington;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Brendan on 3/23/2016.
 */
public class BeerHandler {

    private SQLiteBeerHelper dbHelper;

    public BeerHandler(Context context){
        dbHelper = new SQLiteBeerHelper(context);
    }

    public int addRating(BeerCatalogue beerCatalogue){
        SQLiteDatabase ourDB = dbHelper.getWritableDatabase();

        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(dbHelper.BEER_NAME, beerCatalogue.getName());
        valuesToStore.put(dbHelper.BEER_RATING, beerCatalogue.getRating());
        valuesToStore.put(dbHelper.USER_NAME, beerCatalogue.getUserName());
        valuesToStore.put(dbHelper.BREWERY, beerCatalogue.getBrewery());

        int insertId = (int) ourDB.insert(dbHelper.TABLE_BEERS, null, valuesToStore);
        ourDB.close();
        return insertId;

    }

    BeerCatalogue getRating(int id){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();

        Cursor cursor = ourDB.query(dbHelper.TABLE_BEERS, new String[] {dbHelper.KEY_ID, dbHelper.BEER_NAME,
                        dbHelper.BEER_RATING, dbHelper.USER_NAME}, dbHelper.KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
            BeerCatalogue catalogue = new BeerCatalogue(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                    Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4));

            return catalogue;
        }
        else {
            return null;
        }
    }

    public List<String> getUserBeerHistory(String userName){
        List<String> userHistory = new ArrayList<>();

        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.rawQuery("SELECT name FROM beers WHERE user_name = ?", new String[]{userName});
        int columnIndex = cursor.getColumnIndex("name");
        Log.d("column index 1", "name");
        cursor.moveToFirst();

        if (cursor.getCount() < 1){
            Log.d("cursor less than 1", String.valueOf(cursor.getCount()));
            return null;

        }
        String beerName = cursor.getString(columnIndex);
        Log.d("column index 1", beerName);

        userHistory.add(beerName);

        while (cursor.moveToNext()){
            beerName = cursor.getString(columnIndex);
            Log.d("beer name while loop", beerName);
            userHistory.add(beerName);
        }
        return userHistory;

    }

    public Multimap<String, Integer> getUserHistory(String userName){
        Multimap<String, Integer> userHistory = ArrayListMultimap.create();
//        String beerName;
//        int beerRating;

        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.rawQuery("SELECT name FROM beers WHERE user_name = ?", new String[]{userName});
        int columnIndex = cursor.getColumnIndex("name");
        Log.d("column index 1", "name");
        cursor.moveToFirst();

        String beerName = cursor.getString(columnIndex);
        Log.d("column index 1", beerName);

        Cursor cursor2 = ourDB.rawQuery("SELECT rating FROM beers WHERE user_name = ?", new String[]{userName});
        int otherColumnIndex = cursor2.getColumnIndex("rating");
        Log.d("column index 2", "rating");
        cursor2.moveToFirst();

        int rating = cursor2.getInt(otherColumnIndex);
        Log.d("column index 2", String.valueOf(rating));

        userHistory.put(beerName, rating);
        Set set = userHistory.asMap().entrySet();
        while (cursor.moveToNext() && cursor2.moveToNext()){
            beerName = cursor.getString(columnIndex);
            Log.d("beer name while loop", beerName);
            rating = cursor2.getInt(otherColumnIndex);
            Log.d("beer rating while loop", String.valueOf(rating));
            userHistory.put(beerName, rating);
        }
        return userHistory;
    }

    public List<BeerCatalogue> getAllRatings(){
        SQLiteDatabase ourDB = dbHelper.getWritableDatabase();
        List<BeerCatalogue> beerCatalogueList = new ArrayList<BeerCatalogue>();
        String selectQuery = "SELECT * FROM " + dbHelper.TABLE_BEERS;

        Cursor cursor = ourDB.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                int rating = Integer.parseInt(cursor.getString(2));
                String userName = cursor.getString(3);
                String brewery = cursor.getString(4);

                BeerCatalogue catalogue = new BeerCatalogue(id, name, rating, userName, brewery);

                beerCatalogueList.add(catalogue);
            } while (cursor.moveToNext());
        }
        return beerCatalogueList;
    }


    public void deleteRating(BeerCatalogue catalogue){
        SQLiteDatabase ourDB = dbHelper.getWritableDatabase();
        ourDB.delete(dbHelper.TABLE_BEERS, dbHelper.KEY_ID + " = ?", new String[]{String.valueOf(catalogue.getID()) });

        ourDB.close();
    }

    public int updateRating(BeerCatalogue catalogue){
        SQLiteDatabase ourDB = dbHelper.getWritableDatabase();

        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(dbHelper.BEER_NAME, catalogue.getName());
        valuesToStore.put(dbHelper.BEER_RATING, catalogue.getRating());
        valuesToStore.put(dbHelper.USER_NAME, catalogue.getUserName());

        return ourDB.update(dbHelper.TABLE_BEERS, valuesToStore, dbHelper.KEY_ID + " = ?",
                new String[] {String.valueOf(catalogue.getID())});
    }

    public int getTotalCount(String beerName){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.query(dbHelper.TABLE_BEERS, new String[] {dbHelper.KEY_ID, dbHelper.BEER_NAME,
                        dbHelper.BEER_RATING, dbHelper.USER_NAME}, dbHelper.BEER_NAME + "=?", new String[]{beerName},
                null, null, null, null);
        int numOfTimes = cursor.getCount();
        return numOfTimes;
    }

    public int getUserCount(String beerName, String userName){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.rawQuery("SELECT name FROM beers WHERE name = ? AND user_name = ?",
                new String[]{beerName, userName});
        int numOfTimes = cursor.getCount();
        return numOfTimes;
    }

    public String getTotalAvgRating(String beerName){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
//        Cursor cursor = ourDB.query(dbHelper.TABLE_BEERS, new String[]{dbHelper.KEY_ID, dbHelper.BEER_NAME,
//                dbHelper.BEER_RATING, dbHelper.USER_NAME}, dbHelper.BEER_NAME + "=?", new String[]{beerName}, null, null, null, null);

        Cursor cursor1 = ourDB.rawQuery("SELECT Sum(rating) FROM beers WHERE name = ?", new String[]{beerName});

        int columnIndex = cursor1.getColumnIndex("Sum(rating)");
        cursor1.moveToFirst();
        int totalRatingPoints = cursor1.getInt(columnIndex);
        Log.d("ratingpoints first", String.valueOf(totalRatingPoints));
        double ratingPointsDouble = Double.parseDouble(String.valueOf(totalRatingPoints));
        Log.d("ratingpoints", String.valueOf(ratingPointsDouble));

        cursor1.close();

        Cursor cursor2 = ourDB.rawQuery("SELECT name FROM beers WHERE name = ?", new String[]{beerName});
        int totalTimesRated = cursor2.getCount();

        double ratingTimesDouble = Double.parseDouble(String.valueOf(totalTimesRated));
        Log.d("ratingtimes", String.valueOf(ratingTimesDouble));


        double averageRating = ratingPointsDouble/ratingTimesDouble;
        String averageRatingRounded = String.format("%.1f", averageRating);

        return averageRatingRounded;

    }

    public String getUserAvgRating(String beerName, String userName){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();

        Cursor cursor = ourDB.rawQuery("SELECT Sum(rating) FROM beers WHERE name = ? AND user_name = ?",
                new String[]{beerName, userName});
        cursor.moveToFirst();
        int totalRatingPoints = cursor.getInt(0);
        Log.d("user avg rating", String.valueOf(totalRatingPoints));
        double ratingPointsDouble = Double.parseDouble(String.valueOf(totalRatingPoints));


        Cursor cursor2 = ourDB.rawQuery("SELECT name FROM beers WHERE name = ? AND user_name = ?",
                new String[]{beerName, userName});
        int totalTimesRated = cursor2.getCount();
        Log.d("user avg rating", String.valueOf(totalTimesRated));
        double ratingTimesDouble = Double.parseDouble(String.valueOf(totalTimesRated));

        double averageRating = ratingPointsDouble/ratingTimesDouble;
        String averageRatingRounded = String.format("%.1f", averageRating);

        return averageRatingRounded;

    }

    public List<String> getBeerLeaders(){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.rawQuery("SELECT DISTINCT name FROM beers", null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex("name");
        String beerName = cursor.getString(columnIndex);

        List<String> beerList = new ArrayList<>();
        beerList.add(beerName);
        while (cursor.moveToNext()){
            Log.d("beer name", beerName);
            beerName = cursor.getString(columnIndex);
            beerList.add(beerName);
        }
        Log.d("cursor", String.valueOf(cursor));

        return beerList;

    }


    public List<String> getBeerLeadersInOrder(){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.rawQuery("SELECT DISTINCT name, COUNT(id) FROM beers GROUP BY name " +
                                        "ORDER BY COUNT(id) DESC", null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex("name");
        String beerName = cursor.getString(columnIndex);

        List<String> beerList = new ArrayList<>();
        beerList.add(beerName);
        while (cursor.moveToNext()){
            Log.d("beer order name order", beerName);
            beerName = cursor.getString(columnIndex);
            beerList.add(beerName);
        }
        Log.d("cursor order", String.valueOf(cursor));

        return beerList;

    }


    public List<String> getBeerLeadersByUser(String userName){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.rawQuery("SELECT DISTINCT name, COUNT(id) FROM beers WHERE user_name = ? " +
                                        "GROUP BY name ORDER BY COUNT(id) DESC", new String[]{userName});
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex("name");
        String beerName = cursor.getString(columnIndex);

        List<String> beerList = new ArrayList<>();
        beerList.add(beerName);
        while (cursor.moveToNext()){
            Log.d("beer order name order", beerName);
            beerName = cursor.getString(columnIndex);
            beerList.add(beerName);
        }
        Log.d("cursor order", String.valueOf(cursor));

        return beerList;

    }


    public List<String> getBeerLeadersByRating(){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.rawQuery("SELECT DISTINCT name, AVG(rating) FROM beers GROUP BY name ORDER BY AVG(rating) DESC", null);

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex("name");
        String beerName = cursor.getString(columnIndex);

        List<String> beerList = new ArrayList<>();
        beerList.add(beerName);
        while (cursor.moveToNext()){
            Log.d("beer order name rating", beerName);
            beerName = cursor.getString(columnIndex);
            beerList.add(beerName);
        }
        Log.d("cursor order rating", String.valueOf(cursor));

        return beerList;

    }



    public List<String> getBeerRatingsByUser(String userName){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.rawQuery("SELECT DISTINCT name, AVG(rating) FROM beers WHERE user_name = ? " +
                                        "GROUP BY name ORDER BY AVG(rating) DESC", new String[]{userName});

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex("name");
        String beerName = cursor.getString(columnIndex);

        List<String> beerList = new ArrayList<>();
        beerList.add(beerName);
        while (cursor.moveToNext()){
            Log.d("beer order name rating", beerName);
            beerName = cursor.getString(columnIndex);
            beerList.add(beerName);
        }
        Log.d("cursor order rating", String.valueOf(cursor));

        return beerList;

    }

    public String getBrewery(String beerName){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.rawQuery("SELECT brewery FROM beers WHERE name = ?",
                new String[]{beerName});
        cursor.moveToFirst();
        int breweryColumn = cursor.getColumnIndex("brewery");
        Log.d("brewery column", String.valueOf(breweryColumn));
        String brewery = cursor.getString(breweryColumn);
        Log.d("brewery", brewery);

        return brewery;
    }
}


