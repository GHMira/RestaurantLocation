package com.example.user.r;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectionTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... url) {
        String data = "";
        try{
            data = fetchPath(url[0]);
        }catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String direction) {
        try {
            JSONArray array = new JSONObject(direction).getJSONArray("result");
            for(int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchPath(String s) throws IOException {
        String path = "";
        InputStream is = null;
        HttpURLConnection conn = null;
        try{
            URL url = new URL(s);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null) {
                sb.append(line);
            }

            path = sb.toString();
            br.close();

        }catch(Exception e){
            e.printStackTrace();
        } finally {
            is.close();
            conn.disconnect();
        }
        return path;
    }
}
