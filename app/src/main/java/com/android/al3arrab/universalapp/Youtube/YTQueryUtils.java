package com.android.al3arrab.universalapp.Youtube;

import android.text.TextUtils;
import android.util.Log;

import com.android.al3arrab.universalapp.R;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class YTQueryUtils {

    private static final String LOG_TAG = YTQueryUtils.class.getSimpleName();

    private YTQueryUtils() {
    }

    public static List<YTVideo> fetchYTData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<YTVideo> videos = extractFeatureFromJson(jsonResponse);

        return videos;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the video JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<YTVideo> extractFeatureFromJson(String YTJSON) {
        if (TextUtils.isEmpty(YTJSON)) {
            return null;
        }

        List<YTVideo> videos = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(YTJSON);

            if (!baseJsonResponse.isNull("items")){
                JSONArray VideosArray = baseJsonResponse.getJSONArray("items");

                for (int i = 0; i < VideosArray.length(); i++) {

                    JSONObject currentVideo = VideosArray.getJSONObject(i);
                    JSONObject ytID = currentVideo.getJSONObject("id");
                    JSONObject ytSnippet = currentVideo.getJSONObject("snippet");

                    if (!ytID.isNull("videoId") && !ytSnippet.isNull("title")){

                        String image;
                        JSONObject imageResource = ytSnippet.getJSONObject("thumbnails");
                        if (!imageResource.isNull("medium")) {
                            JSONObject myImg = imageResource.getJSONObject("medium");
                            image = myImg.getString("url");
                        }else {
                            image = String.valueOf(R.drawable.ic_no_image);
                        }

                        String myID = ytID.getString("videoId");
                        String title = ytSnippet.getString("title");

                        YTVideo video = new YTVideo(myID, title, image);

                        videos.add(video);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the video JSON results", e);
        }

        return videos;
    }

}