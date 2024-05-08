package tc.dedroid.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;



public class DedroidNetwork {

    public static interface HttpCallback {
        void onResponse(String responseString, int httpCode);
        void onFailure(Exception e);
    }
    public static interface HttpByteCallback {
        void onResponse(byte[] data, int httpCode);
        void onFailure(Exception e);
    }

    public static void get(final String urlStr, final HttpCallback callback,final int timeout) {
        new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(urlStr);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(timeout);
                        connection.setReadTimeout(timeout);
                        connection.connect();
                        int httpCode = connection.getResponseCode();

                        if (httpCode >= 200 && httpCode < 300) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder responseBuilder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                responseBuilder.append(line).append("\n");
                            }
                            reader.close();
                            callback.onResponse(responseBuilder.toString(), httpCode);
                        } else {
                            callback.onFailure(new Exception("HTTP Response Code: " + httpCode));
                        }
                    } catch (Exception e) {
                        callback.onFailure(e);
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            }).start();
    }
    public static void get(final String urlStr, final HttpByteCallback callback, final int timeout) {
        new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(urlStr);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(timeout);
                        connection.setReadTimeout(timeout);
                        connection.connect();
                        int httpCode = connection.getResponseCode();

                        if (httpCode >= 200 && httpCode < 300) {
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            InputStream inputStream = connection.getInputStream();
                            byte[] buffer = new byte[1024];
                            int length;

                            while ((length = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, length);
                            }

                            outputStream.flush();
                            byte[] responseBytes = outputStream.toByteArray();
                            outputStream.close();
                            inputStream.close();

                            callback.onResponse(responseBytes, httpCode);
                        } else {
                            callback.onFailure(new Exception("HTTP Response Code: " + httpCode));
                        }
                    } catch (Exception e) {
                        callback.onFailure(e);
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            }).start();
    }
    
    public static void get(String url,HttpCallback callback){
        get(url,callback,10000);
    }
    public static void get(String url,HttpByteCallback callback){
        get(url,callback,10000);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}
