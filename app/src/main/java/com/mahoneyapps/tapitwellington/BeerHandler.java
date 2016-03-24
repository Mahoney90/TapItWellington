package com.mahoneyapps.tapitwellington;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
                    Integer.parseInt(cursor.getString(2)), cursor.getString(3));

            return catalogue;
        }
        else {
            return null;
        }
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

                BeerCatalogue catalogue = new BeerCatalogue(id, name, rating, userName);

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

    public int getCount(String beerName){
        SQLiteDatabase ourDB = dbHelper.getReadableDatabase();
        Cursor cursor = ourDB.query(dbHelper.TABLE_BEERS, new String[] {dbHelper.KEY_ID, dbHelper.BEER_NAME,
                        dbHelper.BEER_RATING, dbHelper.USER_NAME}, dbHelper.BEER_NAME + "=?", new String[]{beerName},
                null, null, null, null);
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
}


