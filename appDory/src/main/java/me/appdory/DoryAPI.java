package me.appdory;

import android.net.Uri;
import android.net.Uri.Builder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Docs are available at
 * https://docs.google.com/document/d/1F-gHUmw4C8kkbhmZMUGKxQel3Eu6AdA0RcoWXbCDv0Y
 */
public class DoryAPI {

    static final String CODE = "0000";

    static final Uri BASE_URI = new Builder().scheme("http")
            .authority("appdory.me").path("/api/main.php")
            .appendQueryParameter("key", CODE).build();

    public static final String SOCIAL_VK = "vk";
    public static final String FROM_MANUAL = "manual";
    public static final String FROM_VK = "vk";

    /**
     * Returns Dory UID
     *
     * @param id            ID of user in social network
     * @param socialNetwork one of: {@link #SOCIAL_VK}
     * @param firstName
     * @param lastName
     * @return String user ID
     * @throws IOException
     * @throws JSONException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static String getUid(String id, String socialNetwork,
                                String firstName, String lastName) throws IOException,
            JSONException {
        Builder builder = BASE_URI.buildUpon();

        builder.appendQueryParameter("mod", "get_uid");
        builder.appendQueryParameter("id", id);
        builder.appendQueryParameter("social", socialNetwork);
        builder.appendQueryParameter("first_name", firstName);
        builder.appendQueryParameter("last_name", lastName);

        URL url = new URL(builder.build().toString());

        String s = requestToString(url);

        JSONObject response = new JSONObject(s);

        int code = response.getInt("status");
        if (code != 200) {
            throw new IllegalStateException("Error " + code);
        }

        JSONObject message = response.getJSONObject("msg");

        return message.getString("uid");
    }

    public static List<String> getConcertIDsOf(String uid) throws IOException,
            JSONException {
        Builder builder = BASE_URI.buildUpon();

        builder.appendQueryParameter("mod", "concerts");
        builder.appendQueryParameter("uid", uid);

        URL url = new URL(builder.build().toString());

        String s = requestToString(url);

        JSONObject response = new JSONObject(s);

        int code = response.getInt("status");
        if (code == 103) {
            return new ArrayList<String>();
        }
        if (code != 200) {
            throw new IllegalStateException("Error " + code);
        }

        JSONObject message = response.getJSONObject("msg");

        JSONArray ans = message.getJSONArray("ids");

        ArrayList<String> ret = new ArrayList<String>();

        int length = ans.length();

        for (int i = 0; i < length; i++) {
            ret.add(ans.getString(i));
        }

        return ret;
    }

    public static JSONObject getConcertJson(String concertId)
            throws IOException, JSONException {
        Builder builder = BASE_URI.buildUpon();

        builder.appendQueryParameter("mod", "get_concert");
        builder.appendQueryParameter("cid", concertId);

        URL url = new URL(builder.build().toString());

        String s = requestToString(url);

        JSONObject response = new JSONObject(s);

        int code = response.getInt("status");
        if (code != 200) {
            throw new IllegalStateException("Error " + code);
        }

        JSONObject message = response.getJSONObject("msg");

        return message;

    }

    public static void postArtistInfo(String uid, String artist, String count,
                                      String from) throws IOException {
        Builder builder = BASE_URI.buildUpon();

        builder.appendQueryParameter("mod", "add_artist");

        URL url = new URL(builder.build().toString());

        String charset = "UTF-8";
        String params = "uid=" + URLEncoder.encode(uid, charset) + "&artist="
                + URLEncoder.encode(artist, charset) + "&col="
                + URLEncoder.encode(count, charset) + "&from="
                + URLEncoder.encode(from, charset);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setFixedLengthStreamingMode(params.getBytes().length);
        connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");

        PrintWriter out = new PrintWriter(connection.getOutputStream());
        out.print(params);
        out.close();

        // String response = "";
        //
        // Scanner in = new Scanner(connection.getInputStream());
        //
        // while (in.hasNextLine()) {
        // response += in.nextLine();
        // }
        //
        // in.close();

    }

    public static String requestToString(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return Utils.inputStreamToString(connection.getInputStream());

    }

    String[] getMyRegions(String uid) {
        System.err.println("getMyRegions NOT IMPLEMENTED YET!!!");
        return new String[]{"Москва", "Казань", "Елабуга", "Зеленодольск",
                "Санкт-Петербург"};
    }
}
