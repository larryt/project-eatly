package com.eatlink.eatly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

//food.json
public class EatlyDataBase {
    private final String TAG = "DB";
    private final int CONN_TIMEOUT = 10000;
    private final int DATA_TIMEOUT = 10000;
    private final String url = "http://webstore.test.c4mi.com/food.json";
    // private JSONObject m_jObj = null;
    private JSONArray m_jarray = null;
    private static EatlyDataBase s_instance = null;
    private static final String JSON_STORE = "store";
    private static final String JSON_QUALITY = "quality";
    private final String dbfilepath = android.os.Environment.getExternalStorageDirectory().toString() + "/eatly/.db";

    private ConcurrentHashMap<String, String> m_restaurantMap = new ConcurrentHashMap<String, String>();
    private List<ConcurrentHashMap<String, String>> m_list = null;

    static EatlyDataBase getInstance() {
        if (s_instance == null)
            s_instance = new EatlyDataBase();

        return s_instance;
    }

    public EatlyDataBase() {

    }

    public void genDB() {
        // if (m_jObj == null)
        // m_jObj = readDBfromFile();
        if (m_jarray == null)
            m_jarray = readDBfromServer();
        if (m_jarray != null) {
            parseJSON(m_jarray);
        }
    }

    // private JSONObject readDBfromFile() {
    // try {
    //
    // File dbfile = new File(dbfilepath);
    //
    // if (!dbfile.exists()) {
    // return null;
    // }
    //
    // FileInputStream fIn = new FileInputStream(dbfile);
    // InputStreamReader isr = new InputStreamReader(fIn);
    // BufferedReader buffreader = new BufferedReader(isr);
    // String json = "";
    // String readString = buffreader.readLine();
    // while (readString != null) {
    // json = json + readString;
    // readString = buffreader.readLine();
    // }
    //
    // isr.close();
    // printDebug("readDBfromFile " + json);
    //
    // return new JSONObject(json);
    //
    // } catch (FileNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // return null;
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // return null;
    // } catch (JSONException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // return null;
    // }
    //
    // }

    // private void writeDBtofile(String json_string) {
    // try {
    //
    // File dbfile = new File(dbfilepath);
    //
    // if (!dbfile.exists()) {
    // dbfile.createNewFile();
    // }
    //
    // FileOutputStream fos = new FileOutputStream(dbfile);
    // fos.write(json_string.getBytes());
    // fos.flush();
    // fos.close();
    // } catch (FileNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // }

    private JSONArray readDBfromServer() {
        // JSONObject tmp_jObj = null;
        JSONArray tmp_jarray = null;
        String json = null;
        InputStream is = null;

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpParams httpParameters = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(httpParameters, CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParameters, DATA_TIMEOUT);
            httpclient.setParams(httpParameters);

            HttpResponse response = httpclient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                printDebug("status code=" + statusCode);
                return null;
            }

            HttpEntity entity = response.getEntity();

            is = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            // writeDBtofile(json);
            // json = json.substring(1, json.length() - 1);
            printDebug("readDBfromServer " + json);

            try {
                tmp_jarray = new JSONArray(json);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // try {
            // tmp_jObj = new JSONObject(json);
            // } catch (JSONException e) {
            // printDebug("Error parsing data " + e.toString());
            // }
            // return tmp_jObj;
            return tmp_jarray;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private void parseJSON(JSONArray jarray) {
        try {
            printDebug("jarray.length() " + jarray.length());

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject job = jarray.getJSONObject(i);
                String no = job.getString(Integer.toString(i));
                String store = job.getString(JSON_STORE);
                String quality = job.getString(JSON_QUALITY);
                printDebug("job[" + no + "] store " + store);
                printDebug("job[" + no + "] quality " + quality);
                m_restaurantMap.put(Integer.toString(i), store);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public int getDBSize() {
        return m_restaurantMap.size();
    }

    public String pickOnefromDB(String s) {
        return m_restaurantMap.get(s);
    }

    private void printDebug(String s) {
        Log.d(TAG, s);
    }
}
