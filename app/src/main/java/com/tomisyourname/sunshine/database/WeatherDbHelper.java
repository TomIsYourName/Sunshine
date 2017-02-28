package com.tomisyourname.sunshine.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zain on 28/02/2017.
 */

public class WeatherDbHelper extends SQLiteOpenHelper {


  public WeatherDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                         int version) {
    super(context, name, factory, version);
  }

  public WeatherDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                         int version, DatabaseErrorHandler errorHandler) {
    super(context, name, factory, version, errorHandler);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }

}
