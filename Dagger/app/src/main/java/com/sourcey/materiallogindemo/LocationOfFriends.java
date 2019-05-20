package com.sourcey.materiallogindemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Timer;
import java.util.TimerTask;

public class LocationOfFriends extends AppCompatActivity {
String loadpage;
WebView web;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_of_friends);

        String destloc =getIntent().getStringExtra("locationkey");
        loadpage="https://www.google.com/maps/search/?api=1&query="+destloc;




  

        web = (WebView) findViewById(R.id.webview01);
        //  progressBar = (ProgressBar) findViewById(R.id.progressBar);

        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(loadpage);
        web.canGoBack();
        web.canGoBackOrForward(2);

        progressDialog = new ProgressDialog(LocationOfFriends.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        Timer RunSplash = new Timer();

        TimerTask ShowSplash =  new TimerTask() {
            @Override
            public void run() {


                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }
        };

        // St art the timer
        RunSplash.schedule(ShowSplash, 3000);


        web.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });




        web.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.contains("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else if (url.contains("mailto:")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;

                } else if (url.contains("whatsapp:")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;


                } else if (url.contains("share:")) {

                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    startActivity(Intent.createChooser(share, "Share link!"));


                    return true;

                }

                else {
                    view.loadUrl(url);
                    return true;
                }

            }

            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {




                try {
                    webView.stopLoading();
                } catch (Exception e) {
                }

                if (webView.canGoBack()) {
                    webView.goBack();
                }

                webView.loadUrl("about:blank");


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LocationOfFriends.this);
                alertDialogBuilder.setMessage("You are not connected to Internet");
                alertDialogBuilder.setPositiveButton("Connect",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);

                                // Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                            }
                        });

                alertDialogBuilder.setNegativeButton("Exit",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                super.onReceivedError(webView, errorCode, description, failingUrl);
            }

        });



    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            // progressBar.setVisibility(View.INVISIBLE);
            super.onPageStarted(view, url, favicon);
        }

    }
}

