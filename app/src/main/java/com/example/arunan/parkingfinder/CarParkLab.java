package com.example.arunan.parkingfinder;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arunan on 10/25/16.
 */

public class CarParkLab {
    private static CarParkLab sCarParkLab;
    private static final String urlAddress = "http://localhost:8000";

    private static final String CAR_PARK_LAB_CONNECTION_TAG = "CAR_PARK_LAB_CONNECTION";

    private static List<CarPark> sCarParkList;

    public static CarParkLab get(Context context){
        if (sCarParkLab == null){
            sCarParkLab = new CarParkLab(context);
        }
        return sCarParkLab;
    }

    private CarParkLab(Context context){
        //sCarParkList = fetchItems();
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead=in.read(buffer))>0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }

    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<CarPark> fetchItems(String lon, String lat){

        ArrayList<CarPark> list = new ArrayList<>();
        try{
            String urlAdd = Uri.parse(urlAddress)
                    .buildUpon()
                    .appendQueryParameter("lon", lon)
                    .appendQueryParameter("lat", lat)
                    .build()
                    .toString();
            Log.i(CAR_PARK_LAB_CONNECTION_TAG, urlAdd);
            String jsonString = getUrlString(urlAdd);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(list, jsonBody);

        }catch (JSONException je){
            Log.e(CAR_PARK_LAB_CONNECTION_TAG, "Failed to parse json", je);
        }
        catch (IOException ioe){
            Log.e(CAR_PARK_LAB_CONNECTION_TAG, "Failed to fetch items", ioe);
        }

        return list;
    }

    public void parseItems(List<CarPark> carParkArray, JSONObject jsonBody)
        throws IOException, JSONException{
        JSONArray carParkJsonArray = jsonBody.getJSONArray("carpark");

        for (int i = 0; i < carParkJsonArray.length(); i++){
            JSONObject carparkJSONObject = carParkJsonArray.getJSONObject(i);

            CarPark carpark = new CarPark(
                    carparkJSONObject.getString("name"),
                    carparkJSONObject.getString("address"),
                    carparkJSONObject.getDouble("rating")
            );

            carParkArray.add(carpark);


        }

    }

    private class FetchItemsTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            //fetchItems(lon, lat);
            return null;
        }
    }
}


