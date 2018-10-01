package com.wandsworkstation.chat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Wand's Work Station on 10/20/2017.
 */

public class AppSatus {
    static Context context;

    private static AppSatus instance = new AppSatus();
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    boolean connected = false;

    public static AppSatus getInstance(Context ctx){
        context = ctx.getApplicationContext();
        return instance;
    }

    public  boolean isOnline(){
        try{
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;
        }catch (Exception e){

        }
        return connected;
    }

}
