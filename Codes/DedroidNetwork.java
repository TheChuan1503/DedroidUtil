package tc.dedroid.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class DedroidNetwork {

    public static interface HttpCallback {
        void onResponse(String responseString, int httpCode, LocalDateTime requestEndTime);
        void onFailure(Exception e);
    }

    public static void get(final String urlStr, final HttpCallback callback) {
        new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(urlStr);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000); // 设置连接超时时间为5秒
                        connection.setReadTimeout(5000); // 设置读取超时时间为5秒

                        // 记录请求开始时间
                        LocalDateTime requestStartTime = LocalDateTime.now();

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

                            // 记录请求结束时间
                            LocalDateTime requestEndTime = LocalDateTime.now();

                            // 调用回调函数传递结果
                            callback.onResponse(responseBuilder.toString(), httpCode, requestEndTime);
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}
