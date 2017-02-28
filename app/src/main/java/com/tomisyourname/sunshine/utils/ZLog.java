package com.tomisyourname.sunshine.utils;

import android.util.Log;

import com.tomisyourname.sunshine.BuildConfig;

/**
 * Created by zain on 28/02/2017.
 */

public class ZLog {

  public static void v(String tag, String msg) {
    if(BuildConfig.DEBUG) Log.v(tag, msg);
  }

  public static void i(String tag, String msg) {
    if(BuildConfig.DEBUG) Log.i(tag, msg);
  }

  public static void w(String tag, String msg) {
    if(BuildConfig.DEBUG) Log.w(tag, msg);
  }

  public static void e(String tag, String msg) {
    if(BuildConfig.DEBUG) Log.e(tag, msg);
  }
}
