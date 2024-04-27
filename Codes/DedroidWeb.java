package tc.dedroid.util;

import android.content.Intent;
import android.content.Context;
import android.text.TextUtils;
import android.content.DialogInterface;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import android.widget.EditText;
import android.app.Activity;
import android.net.Uri;
import android.app.Dialog;
import android.webkit.WebViewClient;
import android.os.Handler;
import android.os.Looper;
import java.time.LocalDateTime;
import android.view.View;

public class DedroidWeb {
    static public class WebPage {
        private String defaultUrl;
        private String url;
        private WebView webView;
        private Activity _activity;
        public WebPage(Activity act, String url) {
            webPage(act, url);
        }
        public WebPage(Activity act, String url, int layoutResId) {
            if (DedroidNetwork.isNetworkAvailable(act)) {
                webPage(act, url);
            } else {
                act.setContentView(layoutResId);
            }
        }
        public WebPage(Activity act, String url, View view) {
            if (DedroidNetwork.isNetworkAvailable(act)) {
                webPage(act, url);
            } else {
                act.setContentView(view);
            }
        }
        private void webPage(Activity act, String url) {
            _activity = act;
            defaultUrl = Dedroid.strApi(_activity, url);
            this.url = defaultUrl;
            this.webView = new WebView(act);
            webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            DedroidWeb.JsBridge jsb=new DedroidWeb.JsBridge(act);
            jsb.setWebView(webView);
            webView.addJavascriptInterface(jsb, "Dedroid");
            webView.loadUrl(defaultUrl);
            act.setContentView(webView);
        }
        public WebView getWebView() {
            return this.webView;
        }
        public void setWebView(WebView v) {
            this.webView = v;
            _activity.setContentView(webView);
        }
        public void goBack() {
            webView.goBack();
        }
    }
    static public class JsBridge {
        private static DialogInterface dialog;
        private static Context _context;
        private static Activity _activity;
        private static WebView webView;
        public void setDialog(Dialog dia) {
            dialog = dia;
        }
        public void setActivity(Activity act) {
            _activity = act;
        }
        public void setWebView(WebView wv) {
            webView = wv;
        }
        public JsBridge(Context ctx) {
            _context = ctx;
        }
        @JavascriptInterface
        public void toast(String msg) {
            DedroidToast.toast(_context, msg);
        }
        @JavascriptInterface
        public void alert(String msg, String title) {
            DedroidDialog.alert(_context, msg, title, false);
        }
        @JavascriptInterface
        public void alert(String msg) {
            DedroidDialog.alert(_context, msg, false);
        }
        @JavascriptInterface
        public void prompt(final int symbo, final String title, final String content, final String callback, final String defaultText) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                    @Override
                    public void run() {
                        DedroidDialog.prompt(_context, title, content, false, new DedroidDialog.PromptCallback(){

                                @Override
                                public void onClick(EditText et) {
                                    webView.loadUrl("javascript:" + callback + "(" + symbo + ",true,\"" + et.getText().toString() + "\")");
                                }


                            }, new DedroidDialog.PromptCallback(){

                                @Override
                                public void onClick(EditText et) {
                                    webView.loadUrl("javascript:" + callback + "(" + symbo + ",false,\"" + et.getText().toString() + "\")");
                                }


                            }, defaultText);
                    }
                });

        }
        @JavascriptInterface
        public void get(final int symbo, final String url, final String callback) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                    @Override
                    public void run() {

                        DedroidNetwork.get(url, new DedroidNetwork.HttpCallback(){

                                @Override
                                public void onResponse(String responseString, int httpCode, LocalDateTime requestEndTime) {
                                    DedroidToast.toast(_context, responseString); //webView.loadUrl("javascript:"+callback+"("+symbo+",true,\""+DedroidCharacter.stringToUnicode(responseString)+"\","+httpCode+")");
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    DedroidToast.toast(_context, "失败");//webView.loadUrl("javascript:"+callback+"("+symbo+",false)");

                                }


                            });

                    }});
        }
        @JavascriptInterface
        public void prompt(int symbo, String title, final String callback) {
            prompt(symbo, title, null, callback, "");
        }
        @JavascriptInterface
        public void prompt(int symbo, String title, final String callback, String defaultText) {
            prompt(symbo, title, null, callback, defaultText);
        }
        @JavascriptInterface
        public void networkDialog(String url) {
            DedroidDialog.networkDialog(_context, _activity, url);
        }
        @JavascriptInterface
        public boolean putString(String name, String key, String value) {
            return DedroidConfig.putString(_context, name, key, value);
        }
        @JavascriptInterface
        public String getString(String name, String key) {
            return DedroidConfig.getString(_context, name, key);
        }
        @JavascriptInterface
        public void closeDialog() {
            dialog.cancel();
        }
        @JavascriptInterface
        public void jumpUrl(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            _context.startActivity(intent);
        }
        @JavascriptInterface
        public boolean isAppInstalled(String pkg) {
            return Dedroid.isAppInstalled(_context, pkg);
        }
        @JavascriptInterface
        public void launchApp(String pkg) {
            Dedroid.launchApp(_context, pkg);
        }
        @JavascriptInterface
        public void startActivity(String name) throws ClassNotFoundException {
            Intent intent = new Intent(_context, Class.forName(name));
            _context.startActivity(intent);
        }
        @JavascriptInterface
        public void loadUrl(final String url) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(url);
                    }});

        }
        @JavascriptInterface
        public void goBack() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.goBack();
                    }});
        }
        @JavascriptInterface
        public String strApi(String str) {
            return Dedroid.strApi(_context,str);
        }
        @JavascriptInterface
        public void finish() {
            _activity.finish();
        }
        @JavascriptInterface
        public void finish(boolean i) {
            if(i){
                _activity.finishAndRemoveTask();
            }
            else{
                finish();
            }
        }
    }

}
