package com.example.user.r;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

//params, progress, result
public class GetNearbyRestaurants extends AsyncTask<Object, String, JSONArray> {

    private GoogleMap mMap;
    private JSONArray googlePlacesData;
    private double lng;
    private double lat;
    private MapsActivity mapsActivity;

    private static String LOCATION_API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=1500&type=restaurant&key=AIzaSyB9FgWx0cRxb77LvOFUZDqWeqdFQyv7oHc";

    public GetNearbyRestaurants(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    @Override
    protected JSONArray doInBackground(Object... objects) {

        mMap = (GoogleMap) objects[0];
        lng = Math.abs((Double) objects[2]);
        lat = Math.abs((Double) objects[1]);

        LOCATION_API = String.format(LOCATION_API, lng, lat);

        try {
            googlePlacesData = readURL();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(JSONArray googlePlacesData) {
        HashMap<Marker, Object> markers = new HashMap<Marker, Object>();
        try {
            for (int i = 0; i < googlePlacesData.length(); i++) {
                JSONObject jsonObject = googlePlacesData.getJSONObject(i);
                JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
                String name = jsonObject.getString("name");
                LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));

                Marker restoran = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name));

                boolean open_now = false;
                String rating = "";
                String adresa = "";
                String slike = "";


                JSONArray ar = jsonObject.names();
                for(int j = 0; j < ar.length(); j++) {
                    if(ar.get(j).equals("opening_hours")) {
                        if(jsonObject.getJSONObject("opening_hours") != null){
                            open_now = jsonObject.getJSONObject("opening_hours").getBoolean("open_now");
                        }
                    }
                    if(ar.get(j).equals("rating")) {
                        if(jsonObject.getString("rating") != null && !jsonObject.getString("rating").isEmpty()) {
                            rating = jsonObject.getString("rating");
                        }
                    }
                    if(ar.get(j).equals("vicinity")) {
                        if(jsonObject.getString("vicinity") != null && !jsonObject.getString("vicinity").isEmpty()) {
                            adresa = jsonObject.getString("vicinity");
                        }
                    }

                    if(ar.get(j).equals("photos")) {
                        if(jsonObject.getJSONArray("photos") != null) {
                            if(jsonObject.getJSONArray("photos").getJSONObject(0) != null) {
                                if(jsonObject.getJSONArray("photos").getJSONObject(0).getJSONArray("html_attributions") == null) {
                                    continue;
                                }
                                slike = jsonObject.getJSONArray("photos").getJSONObject(0).getJSONArray("html_attributions").getString(0);
                            }
                        }
                    }
                }


                Object markerData[] = new Object[4];
                markerData[0] = open_now;
                markerData[1] = rating;
                markerData[2] = adresa;
                markerData[3] = slike;

                markers.put(restoran, markerData);
            }
            mapsActivity.asyncResult(markers);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private JSONArray readURL() throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            /*URL url = new URL(LOCATION_API);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));*/
            StringBuffer sb = new StringBuffer();

            /*String line = "";
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }*/
            sb.append("{\n" +
                    "   \"html_attributions\" : [],\n" +
                    "   \"next_page_token\" : \"CrQCIQEAAAxfbz5h9oLsTZXnnmwVg_LXfcx8NKwwc_lMhtLofJhLPdfsTNKhmCQ4yq3Kq_f9-RXOkPT1xRnMZdEVxlyc-hph4HRKprTf4_2GIeMpLiLowX91h7RJTvJlb9kcoOdv7BL-FgQuGhCCJQBNfATh03LNQr5H_8N3-tKudV5Nq2PgrpknehDPMDGptELYdN2LcaQiCAidlkrynpv1qJ8LMbJhTYvvjitKZkCzILbQaoW_qtpZGOc0Y5HEMnc-21lrMpfSaFovIGDY3KDOinN9VRGWqZvHX0k99Vv3LgJJgF6cc1xzTJIeJaCsGvodKTnOF_TxBUmX0pbFzsWjXUYspi767W5v6PtiEolsyr6_bgiOy6oKUHPFabx7FAMgdNzHKUkJMX8hX-_yaWh31-TCqx0SENOZc3IqKPvsTymS_8w5mkwaFKUH3fxFVFzUb3TBFosUD7sLhVcv\",\n" +
                    "   \"results\" : [\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.25867,\n" +
                    "               \"lng\" : 19.8456059\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.26003213029151,\n" +
                    "                  \"lng\" : 19.8469702802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.25733416970851,\n" +
                    "                  \"lng\" : 19.8442723197085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                    "         \"id\" : \"b5cf0fd608f225b794cc463916d2f6491d149ab7\",\n" +
                    "         \"name\" : \"Hotel and restaurant Fountain\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 2988,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/118139974883354386587/photos\\\"\\u003eХотел Ресторан Фонтана\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAA5OsiwGsFxN6w7l6naiO_bUHUtVTGjJ42oIe8nqdPgTht49IFAJDPfNXZlvN05G9_4ES02-i68fykEF8y79dcjEEVt3_lnJFgFibpJqSaUE0szAGIY38c01sIZsVtfUdzEhDrZjvFoCUITCSHmdtagXMPGhRpel4InNTAwTs9WEEYyTZZ3eAWfQ\",\n" +
                    "               \"width\" : 5312\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJocmNkmcQW0cRbWiL3n-lrxs\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R5W+F6 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R5W+F6\"\n" +
                    "         },\n" +
                    "         \"rating\" : 4.5,\n" +
                    "         \"reference\" : \"ChIJocmNkmcQW0cRbWiL3n-lrxs\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"lodging\", \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 995,\n" +
                    "         \"vicinity\" : \"Nikole Pašića 27, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.249106,\n" +
                    "               \"lng\" : 19.8393305\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2504466802915,\n" +
                    "                  \"lng\" : 19.8407021302915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2477487197085,\n" +
                    "                  \"lng\" : 19.8380041697085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"05f5b7696d18a5ca8ed677848f402b9e26398210\",\n" +
                    "         \"name\" : \"Ananda\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 930,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/107178536083726999056/photos\\\"\\u003eAnanda\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAhu_1NBZz52vXuzrZcjc8WpWsgfgF9dMi15ZayGJyyPR_brpo1Imh7gDtvbQTL99iRFrjLRkM-ujcaSlegjWnD9-8v9MrigkHOe-cdCV31SSBEphE74HCPq2XXLJtB0rGEhBXMefqTRYoeffp5kKR6ZiDGhT3kAsVZitNfsw9COx6ScLAjHF-yA\",\n" +
                    "               \"width\" : 1240\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJucw0tmoQW0cRhxD0Z1nof5g\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"6RXQ+JP Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX6RXQ+JP\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4.6,\n" +
                    "         \"reference\" : \"ChIJucw0tmoQW0cRhxD0Z1nof5g\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 403,\n" +
                    "         \"vicinity\" : \"Petra Drapšina 51, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.2607103,\n" +
                    "               \"lng\" : 19.8333861\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2621449302915,\n" +
                    "                  \"lng\" : 19.8346880802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2594469697085,\n" +
                    "                  \"lng\" : 19.8319901197085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/bar-71.png\",\n" +
                    "         \"id\" : \"018ef82060cadcd275634dd818e6c00c8a14bc33\",\n" +
                    "         \"name\" : \"Sketch\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 697,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/118319028945577646621/photos\\\"\\u003eSketch\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAoFaQPG0pG8Yf4H1-lVVB_NWi5OSS5gWfkAkUGDMlzeUl49Es8SBxi6hMVxJTL0FKdik2YK2PM6MjEZny0u2-oebC6kvvqNX8H3pQ8fC9QrzTwscWBa3Jbj2WLAQuuq1XEhApyeAvPSd8wzMTkqdbLW2LGhTCtYwTpJmf3uEF-us5gnVL4SLUAg\",\n" +
                    "               \"width\" : 1045\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJaUeeHVAQW0cRjfMU6paNjYI\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R6M+79 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R6M+79\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4.5,\n" +
                    "         \"reference\" : \"ChIJaUeeHVAQW0cRjfMU6paNjYI\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [\n" +
                    "            \"bar\",\n" +
                    "            \"cafe\",\n" +
                    "            \"restaurant\",\n" +
                    "            \"food\",\n" +
                    "            \"point_of_interest\",\n" +
                    "            \"establishment\"\n" +
                    "         ],\n" +
                    "         \"user_ratings_total\" : 649,\n" +
                    "         \"vicinity\" : \"Bulevar kralja Petra I 15, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.262356,\n" +
                    "               \"lng\" : 19.8437322\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2637482302915,\n" +
                    "                  \"lng\" : 19.8450826802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2610502697085,\n" +
                    "                  \"lng\" : 19.8423847197085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"f77fbcd3f6184bd5f6dd5c9b0b4abefabe8899e7\",\n" +
                    "         \"name\" : \"Jos Ovu Noc\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 4605,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/107715660151200218743/photos\\\"\\u003eZoran Skaljac\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAtzUjic9NetMry2qQVuu0V65kJQUB_Yz0mbQSJYhC1TiA0-BGgjaGOE1qfaaDNpBuCefPouJvQ9AW0t8GgFb3zz0TG5Ctzfp185M7Q3Dlr6ewanjgAu5y29spei0gYGcDEhD74Ya0VYF7Rpv8lO8KqzeAGhSRXRSc3rp3bCDM5vxqRfOjUXRHbg\",\n" +
                    "               \"width\" : 7360\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJ_9XQPvsQW0cR5MyPahFHCQg\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R6V+WF Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R6V+WF\"\n" +
                    "         },\n" +
                    "         \"rating\" : 2.4,\n" +
                    "         \"reference\" : \"ChIJ_9XQPvsQW0cR5MyPahFHCQg\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 5,\n" +
                    "         \"vicinity\" : \"18/A TEMERINSKA, 21000, Novi Sad, Južno-bački okrug\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.2576435,\n" +
                    "               \"lng\" : 19.8333907\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2590312302915,\n" +
                    "                  \"lng\" : 19.8348794802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2563332697085,\n" +
                    "                  \"lng\" : 19.83218151970849\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"2b79d17e3105203b0ec33a76b93e6caf6f5e744e\",\n" +
                    "         \"name\" : \"De Gusto\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 4160,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101476505478615980904/photos\\\"\\u003ePantelija Bozic\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAA6v28hUVMNnGtOj76Rb8Ul4R8VGytgS4_nRzCR_q2DEskUKzSGB6EMV3GdzF-MFyfIQTDf5mpRGamm_Vph17NvhKZqeO5afdDzX2k2Xfzauj68hVJ44sX9uPZdgN_P7SAEhDwtYHuuSN7O_5ilHdlLh2SGhTaVhjt-s-cSvPvpGuFmHr97WhX_Q\",\n" +
                    "               \"width\" : 3120\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJ41PD_EQQW0cR2gRXK9lEfUE\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R5M+39 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R5M+39\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4.5,\n" +
                    "         \"reference\" : \"ChIJ41PD_EQQW0cR2gRXK9lEfUE\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 781,\n" +
                    "         \"vicinity\" : \"Bulеvar oslobođenja 48, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.2546834,\n" +
                    "               \"lng\" : 19.8460869\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2560045802915,\n" +
                    "                  \"lng\" : 19.8478228302915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2533066197085,\n" +
                    "                  \"lng\" : 19.8451248697085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"6166af8a24efd674ac05b90c494cbfb0668dd9b0\",\n" +
                    "         \"name\" : \"Pizzeria Caribic\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 3456,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/112225380326556194836/photos\\\"\\u003eDamir Kurti 85-to god u vezi i lepak za budale\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAApafIyS0D1SfTRDCnUzZ4BruOuJWop231Fyk6wlY1dbKjnnLwVs4Y7cOlErCu12j53DelwX6716_eAH01ENlG8gCvuDYz9q3L66idkAi_xIOUHXdUS9Vu4VU8wzINOnlYEhB37iYVIogVk25UU_NGv3UsGhT3Vd_w5avT-TNO4Chl6Y-pq6vOkg\",\n" +
                    "               \"width\" : 4608\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJc2fm4WgQW0cRu6e-k1iDNPk\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R3W+VC Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R3W+VC\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4,\n" +
                    "         \"reference\" : \"ChIJc2fm4WgQW0cRu6e-k1iDNPk\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [\n" +
                    "            \"meal_takeaway\",\n" +
                    "            \"restaurant\",\n" +
                    "            \"food\",\n" +
                    "            \"point_of_interest\",\n" +
                    "            \"store\",\n" +
                    "            \"establishment\"\n" +
                    "         ],\n" +
                    "         \"user_ratings_total\" : 1037,\n" +
                    "         \"vicinity\" : \"prizemlje i prvi sprat, Modene, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.26510890000001,\n" +
                    "               \"lng\" : 19.8367468\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2664023302915,\n" +
                    "                  \"lng\" : 19.8380131802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2637043697085,\n" +
                    "                  \"lng\" : 19.8353152197085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"0047daca3c34653d9cedfbed703604a174a9e308\",\n" +
                    "         \"name\" : \"Gostiona \\\"Miša\\\"\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 2268,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/108292204621116246772/photos\\\"\\u003eIvan Vesić KESA\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAAYirjkvH6OwS1I_GNftziXxqQhzVC6mD6xTvNaITcK3T--7bWZqfueg9ROKZ0FgJ4NFZ0pRXElNirICh-YlDQZIfnn3EyjfCC6ou3Jnu36jwlFrnMHSt7khrTQOzFbqBEhBUVlNwjcer04Nz5SIP6btmGhQvshcXtVTM4G_TXgZTpgVsoiINpA\",\n" +
                    "               \"width\" : 4032\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJxTuEv1kQW0cRsSM_3u4m-ss\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R8P+2M Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R8P+2M\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 1,\n" +
                    "         \"rating\" : 4.4,\n" +
                    "         \"reference\" : \"ChIJxTuEv1kQW0cRsSM_3u4m-ss\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 141,\n" +
                    "         \"vicinity\" : \"Kisačka br. 56, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.256547,\n" +
                    "               \"lng\" : 19.845605\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2581363302915,\n" +
                    "                  \"lng\" : 19.8470711302915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2554383697085,\n" +
                    "                  \"lng\" : 19.8443731697085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"b672cfc1a8f7d12976ec4861701d05cc18149ef2\",\n" +
                    "         \"name\" : \"Црна Маца\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 3096,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/116951350271341730919/photos\\\"\\u003eUroš Bajić\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAA8IEjS8DPBnZ1hWeicG3vmbnmVSB1s5Rcp3rNlfC58ZqwWRCwaO7GcbHMqVCAAFXZZidAMZl1kYMlV0Lc_GmkhkPOM7-BF6JKm73OSUDyozAzdhxTzXjSdNmgz2xe_KieEhDyaQ603wpjHSjnqie63fD7GhRbBlDClmhurs8KSIw3jU_vgeQ8rQ\",\n" +
                    "               \"width\" : 4128\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJ19lZb2gQW0cR-6l-5K3xM_c\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R4W+J6 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R4W+J6\"\n" +
                    "         },\n" +
                    "         \"rating\" : 4.3,\n" +
                    "         \"reference\" : \"ChIJ19lZb2gQW0cR-6l-5K3xM_c\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 107,\n" +
                    "         \"vicinity\" : \"Mite Ružića, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.25437300000001,\n" +
                    "               \"lng\" : 19.84112\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2557293802915,\n" +
                    "                  \"lng\" : 19.8423926802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2530314197085,\n" +
                    "                  \"lng\" : 19.8396947197085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"52c388f0a194c3f80b33c89e28eb342cb0e79124\",\n" +
                    "         \"name\" : \"Ресторан \\\"Зак\\\"\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 540,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/100158250133097438756/photos\\\"\\u003eРесторан &quot;Зак&quot;\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAVDwlhPLxHqzPvvK-c9PxMSMvbhJQwok6GAUdf2UjiAo6fMVLm8qTo1Id-8lyK8_GRN7YUbnE2aTyvP20hZsY3_xfG0QlZtDL7e6z3T6-ZSJoYdsoC0n3JWZLGFirPV8bEhBQyk7XK2lqC6tvMm1nEBTbGhS3rOtC8CdlaqCSDuEroE0rwfHpHQ\",\n" +
                    "               \"width\" : 960\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJcSH3c0IQW0cRoe8lLswrjdo\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R3R+PC Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R3R+PC\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 3,\n" +
                    "         \"rating\" : 4.6,\n" +
                    "         \"reference\" : \"ChIJcSH3c0IQW0cRoe8lLswrjdo\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 225,\n" +
                    "         \"vicinity\" : \"Šafarikova 6, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.2590458,\n" +
                    "               \"lng\" : 19.8245696\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.26044263029149,\n" +
                    "                  \"lng\" : 19.82590703029151\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2577446697085,\n" +
                    "                  \"lng\" : 19.8232090697085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"df246368a770dabacbf4c320db1dc29ba37c83f1\",\n" +
                    "         \"name\" : \"Bubi Grill\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 1512,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/109304192987312555642/photos\\\"\\u003eBubi Grill\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAA09mEI74h8GTPn5Bqn8ZiRMkLBrUeRX-1XE2M_pkVzEuhgUKEwACRIdnJ4rF7aDJn_1WLkAl-xUtpabeHrvHqARh-m_8nDgiOFiZKUlZ6J9dC11SJrYeupjTBKAzTaNq6EhBxoWXrfrxISELKezE6RgYFGhTQAiEx8ZG1UlWFlbZzVkWFS92vOQ\",\n" +
                    "               \"width\" : 2048\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJK4GtZE4QW0cRN2CXRy-m53Q\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R5F+JR Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R5F+JR\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 1,\n" +
                    "         \"rating\" : 4.1,\n" +
                    "         \"reference\" : \"ChIJK4GtZE4QW0cRN2CXRy-m53Q\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"bar\", \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 247,\n" +
                    "         \"vicinity\" : \"Bulevar kralja Petra I 95, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.26502,\n" +
                    "               \"lng\" : 19.831606\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2663133802915,\n" +
                    "                  \"lng\" : 19.83300253029151\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.26361541970851,\n" +
                    "                  \"lng\" : 19.8303045697085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"087182f8fcdac8c7dfc8bf986ad971034eef9150\",\n" +
                    "         \"name\" : \"McDonald’s\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 4032,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/107942400854800377617/photos\\\"\\u003eDarko Kovač\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAApRLCK4z2jyp_tUC6r-kGgEEJ2oWA5lVUalCHpKtctt4smnWh8cxgcrWMVsbcFRzjkZnrWt1AWtM9FoRy-tgRuSw3VD8qSarpK4NIWRbhdMm5hRqD_lmMMSYMp-vKKaJ0EhBxYQ4ysmJLbYo0Kp5_ads9GhQklPq4qFkR5TANaGPjJsDBgKPLoQ\",\n" +
                    "               \"width\" : 1960\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJW1TWK1cQW0cREg-6FGy3lYQ\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R8J+2J Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R8J+2J\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4.3,\n" +
                    "         \"reference\" : \"ChIJW1TWK1cQW0cREg-6FGy3lYQ\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 1679,\n" +
                    "         \"vicinity\" : \"Bulevar Jaše Tomića, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.26182119999999,\n" +
                    "               \"lng\" : 19.830855\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.26313958029149,\n" +
                    "                  \"lng\" : 19.8320916302915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.26044161970849,\n" +
                    "                  \"lng\" : 19.82939366970849\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"39fad1ee1790716cfa6cc8a1f731386fdf27b445\",\n" +
                    "         \"name\" : \"Minuta\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 800,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/100310847637267525465/photos\\\"\\u003eLazar Gugleta\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAgWpR6Sdwl8bUxQ11h5qc0VopeDiHpMQr_GJL-6-HNpyEhmAetKvcnM2QiSJ4qjbLA70JiOvO0uTurkvq9h7OJO-e1pKG095lZpP-f-EoG1WsEuCpx1K77qon91cvj36DEhAOkfIwHjxfwrDVt3mzfQm9GhTAOM8NigSeMZrGiy-QUhdosd9MwA\",\n" +
                    "               \"width\" : 1200\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJTzDy81AQW0cR3FiEusNwUhk\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R6J+P8 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R6J+P8\"\n" +
                    "         },\n" +
                    "         \"rating\" : 3.8,\n" +
                    "         \"reference\" : \"ChIJTzDy81AQW0cR3FiEusNwUhk\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 124,\n" +
                    "         \"vicinity\" : \"Bulevar oslobođenja 12-16, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.2730873,\n" +
                    "               \"lng\" : 19.8338235\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2744443302915,\n" +
                    "                  \"lng\" : 19.8352520802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2717463697085,\n" +
                    "                  \"lng\" : 19.8325541197085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"2c32f7748f19da3de1d4dcb9f565d217f6520bfc\",\n" +
                    "         \"name\" : \"Carpaccio Food & Beer Bar\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"place_id\" : \"ChIJH-d0S_kQW0cRIp2HyfYdLxw\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7RFM+6G Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7RFM+6G\"\n" +
                    "         },\n" +
                    "         \"rating\" : 4.5,\n" +
                    "         \"reference\" : \"ChIJH-d0S_kQW0cRIp2HyfYdLxw\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 4,\n" +
                    "         \"vicinity\" : \"Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.2611076,\n" +
                    "               \"lng\" : 19.832925\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2623603302915,\n" +
                    "                  \"lng\" : 19.8343285802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2596623697085,\n" +
                    "                  \"lng\" : 19.8316306197085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"273c111174b34ccb3425cd8d973fb6f223d1ff6b\",\n" +
                    "         \"name\" : \"Чика Перо\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 312,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/102288380851686349312/photos\\\"\\u003eČika Pero\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAADpAFbU8yZ0cD4H1NbluuLwhM4w3XvgU_Xq51_jxlchMeL1KqALRtTtmJWYCP1XAAcgRkMcb7osW1OpK2hQTDL-zg3UtaoqNVsgm27Gf1N2MxfKArOXlTjGjuZIX80hFLEhB5lw4r3y20BmcUndXXVQLcGhT5UsUMs7ixkNQrXd2L9HglH8KBAQ\",\n" +
                    "               \"width\" : 820\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJ-X_8mVoQW0cR9amE8GOoGsk\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R6M+C5 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R6M+C5\"\n" +
                    "         },\n" +
                    "         \"rating\" : 4,\n" +
                    "         \"reference\" : \"ChIJ-X_8mVoQW0cR9amE8GOoGsk\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 6,\n" +
                    "         \"vicinity\" : \"Bulevar kralja Petra I 22, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.2599545,\n" +
                    "               \"lng\" : 19.832917\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2612673802915,\n" +
                    "                  \"lng\" : 19.8341346802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2585694197085,\n" +
                    "                  \"lng\" : 19.83143671970849\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"51271caa99703aa9a18bf7508395adb1f6a18a98\",\n" +
                    "         \"name\" : \"Restaurant & Pizzeria Savoca\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 1080,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/105532447345117569236/photos\\\"\\u003eDeny\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAUGHcOHgaUeYXAMEiSDBnO0VVYIe72JmqxrfnfJBuDyAcaY3-zyUGqz3SNjhPHXDnzklBtcEhXnSbJHRgEpL7oW1_CBn5KbpObAiVTI4A0brNDnYfOW0v9WZsFlUQkO67EhAPwqKSxrsa7lK5AsKBjVTlGhSfPMw5ZH0066soBts3hPJa1sS3MA\",\n" +
                    "               \"width\" : 1920\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJ9aM2dFAQW0cRkIJV3aBCxFU\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R5M+X5 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R5M+X5\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4.6,\n" +
                    "         \"reference\" : \"ChIJ9aM2dFAQW0cRkIJV3aBCxFU\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 1297,\n" +
                    "         \"vicinity\" : \"Bulevar oslobođenja 41, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.25963,\n" +
                    "               \"lng\" : 19.82906\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2610208302915,\n" +
                    "                  \"lng\" : 19.8303856802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2583228697085,\n" +
                    "                  \"lng\" : 19.8276877197085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"8c1f7d4d60505746f1d393d95a4625535ff90f1b\",\n" +
                    "         \"name\" : \"Panter\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 600,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/109429208398427362034/photos\\\"\\u003ePanter\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAMg2bWS2BXn_utWzrII0ka1OlqlMp_o0WVmJWixNUg42rDhuUagJPSLzdeeD4UZ6LyKiLVNjM7VxOAqyEZwQ2ga7wmB8qQ3FahKP9XznPba6jJ67pE5Ld8pHfmyDXwXpTEhBVKLBfF-qyLq7hseW0AdjxGhRMB9sHViu9x8x7L57HU-orK41VUA\",\n" +
                    "               \"width\" : 960\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJQzbdwk8QW0cRNFP9_q8tcCM\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R5H+VJ Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R5H+VJ\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4.5,\n" +
                    "         \"reference\" : \"ChIJQzbdwk8QW0cRNFP9_q8tcCM\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [\n" +
                    "            \"restaurant\",\n" +
                    "            \"cafe\",\n" +
                    "            \"food\",\n" +
                    "            \"point_of_interest\",\n" +
                    "            \"store\",\n" +
                    "            \"establishment\"\n" +
                    "         ],\n" +
                    "         \"user_ratings_total\" : 1476,\n" +
                    "         \"vicinity\" : \"Bulevar kralja Petra I 29, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.2591027,\n" +
                    "               \"lng\" : 19.8356314\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.26051538029149,\n" +
                    "                  \"lng\" : 19.8370084302915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2578174197085,\n" +
                    "                  \"lng\" : 19.83431046970849\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"daafd350d1fd10184bd02a561ea094ef710d5513\",\n" +
                    "         \"name\" : \"Pizzeria Ciao 1\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 713,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/105226050666708655584/photos\\\"\\u003ePizzeria Ciao 1\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAOAOZmzC0EmDWhJDTqBnE9JHtehThaU-0Ivfln_ugu6VRrhawjS1aWh6HJfRtQ1ph41oCUvyNSx6oxvBLfO71YaLvHBo-J8f-woEST8zferQSqMuRZIKtQZ3VzhX6uwT_EhCsigFVj5cz0tcHhIcAY-tgGhRMeNkkzx0GVgWi2FfIOntFkE7dJQ\",\n" +
                    "               \"width\" : 960\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJqyUmh2gQW0cRPcAqDdhBjkI\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R5P+J7 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R5P+J7\"\n" +
                    "         },\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4.4,\n" +
                    "         \"reference\" : \"ChIJqyUmh2gQW0cRPcAqDdhBjkI\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 2918,\n" +
                    "         \"vicinity\" : \"Braće Jovandić 1, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.2580502,\n" +
                    "               \"lng\" : 19.8354375\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2594141302915,\n" +
                    "                  \"lng\" : 19.8367202302915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2567161697085,\n" +
                    "                  \"lng\" : 19.83402226970849\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"f2e67d9d4d0d92cadd8d47337ab5bbabda594d31\",\n" +
                    "         \"name\" : \"Pekara NS Zrnce\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : false\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 530,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/106457818931257250875/photos\\\"\\u003ePekara NS Zrnce\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAbe0o6VPUyUhZeB5fjVYIYrRaSltze1uDaBMSDOle-AkMRUiw5PC0uklCKxWV_QTcAyKjaS_uZGEuGTf6debtJ961Cq02zy-VnaFqTNf2957gKUQrMJCsT25cA2lIZjtSEhBTqE_IzsVEHSfTZe7B0Xi7GhRhvseo00ReVeXEXaBJgRsHHAmQQQ\",\n" +
                    "               \"width\" : 530\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJrUhOvkQQW0cR5CWtZ6w94UU\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R5P+65 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R5P+65\"\n" +
                    "         },\n" +
                    "         \"rating\" : 4.7,\n" +
                    "         \"reference\" : \"ChIJrUhOvkQQW0cR5CWtZ6w94UU\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [\n" +
                    "            \"bakery\",\n" +
                    "            \"restaurant\",\n" +
                    "            \"food\",\n" +
                    "            \"point_of_interest\",\n" +
                    "            \"store\",\n" +
                    "            \"establishment\"\n" +
                    "         ],\n" +
                    "         \"user_ratings_total\" : 3,\n" +
                    "         \"vicinity\" : \"Dimitrija Avramovića 13, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.254844,\n" +
                    "               \"lng\" : 19.833028\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.2562465302915,\n" +
                    "                  \"lng\" : 19.8343660302915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2535485697085,\n" +
                    "                  \"lng\" : 19.8316680697085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/shopping-71.png\",\n" +
                    "         \"id\" : \"31cd454d0879cf541d85904432914946aa8a9627\",\n" +
                    "         \"name\" : \"PALAČINKARNICA BAŠ UKUSNO\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 700,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/108911493350268871754/photos\\\"\\u003ePALAČINKARNICA BAŠ UKUSNO\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAxVDds2DiPtoaSeFQ-bD10c_nFsDDTqQgwvtOxWOswaFhu6_TbFMb1SAUWYSoUNPFlShQpkcqs3VsodVjNcXk7nlM1qJPW3zEEA6Z2vOZM--GyIq40qRMg2bdtpp_YWnpEhCqwAptb2uP5rAaWLV48C2SGhRTWORnWGG0KRWXK5fc9B7Yzcq9hQ\",\n" +
                    "               \"width\" : 960\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJoeMdr0YQW0cRA4LUGxw-iTE\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R3M+W6 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R3M+W6\"\n" +
                    "         },\n" +
                    "         \"rating\" : 4.5,\n" +
                    "         \"reference\" : \"ChIJoeMdr0YQW0cRA4LUGxw-iTE\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"store\", \"establishment\" ],\n" +
                    "         \"user_ratings_total\" : 16,\n" +
                    "         \"vicinity\" : \"Novosadskog sajma 19, Novi Sad\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : 45.25945890000001,\n" +
                    "               \"lng\" : 19.8428877\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : 45.26084068029149,\n" +
                    "                  \"lng\" : 19.8440917802915\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : 45.2581427197085,\n" +
                    "                  \"lng\" : 19.8413938197085\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\n" +
                    "         \"id\" : \"69b1e43f1c046fa0755027e6a3905f3d332f936a\",\n" +
                    "         \"name\" : \"Big Pizza - Dostava Novi Sad\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 960,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/105716297349940053917/photos\\\"\\u003eBig Pizza - Dostava Novi Sad\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CmRaAAAAhjdAixDIf1K51oINFpeIk1Wx875HCRv6geoklg_KSewnwDJdTS1QlY7ko9Bjiku2vWLbf-T23VpqiNtPu51n_yANOhfrawSGtblZDQFrUixB-Q2eWQ16a5XB1bNmy5WHEhDg1qQ_FOotNEeKajNqsWhKGhRbnhqgRN5HRkiQVtfCqvLpDNKuIQ\",\n" +
                    "               \"width\" : 1280\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJNXn7OV0QW0cR8GeCrPq9PTI\",\n" +
                    "         \"plus_code\" : {\n" +
                    "            \"compound_code\" : \"7R5V+Q5 Novi Sad, Serbia\",\n" +
                    "            \"global_code\" : \"8FQX7R5V+Q5\"\n" +
                    "         },\n" +
                    "         \"rating\" : 3.6,\n" +
                    "         \"reference\" : \"ChIJNXn7OV0QW0cR8GeCrPq9PTI\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [\n" +
                    "            \"meal_delivery\",\n" +
                    "            \"meal_takeaway\",\n" +
                    "            \"restaurant\",\n" +
                    "            \"food\",\n" +
                    "            \"point_of_interest\",\n" +
                    "            \"establishment\"\n" +
                    "         ],\n" +
                    "         \"user_ratings_total\" : 122,\n" +
                    "         \"vicinity\" : \"Jovana Subotića 18, Novi Sad\"\n" +
                    "      }\n" +
                    "   ],\n" +
                    "   \"status\" : \"OK\"\n" +
                    "}\n");
            JSONObject jsonObject = new JSONObject(sb.toString());
//            if (jsonObject.has("next_page_token")) {
//                nextPageToken = jsonObject.getString("next_page_token");
//            } else {
//                nextPageToken = "";
//            }
//            bufferedReader.close();*/
            return jsonObject.getJSONArray("results");

        } /*catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } */catch (JSONException e) {
            e.printStackTrace();
        } finally {
//            inputStream.close();
//            urlConnection.disconnect();
        }
        return null;
    }
}
