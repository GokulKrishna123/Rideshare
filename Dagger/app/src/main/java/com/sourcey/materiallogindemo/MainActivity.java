package com.sourcey.materiallogindemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;
import static android.Manifest.permission.ANSWER_PHONE_CALLS;
import static android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.LOCATION_HARDWARE;
import static android.Manifest.permission.PROCESS_OUTGOING_CALLS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {

    private FirebaseAuth firebaseAuth;
    ArrayList<String> list=new ArrayList<>();

    private static final int RequestPermissionCode =1 ;
    private WebView web;
    private String loadpage;
    private ProgressDialog progressDialog;
    Button amgoing,joinwith,frndlist,exit;
    public static PubNub pubnub;
    private DatabaseReference databaseGoingTo;

    private DatabaseReference mDatabase;
    DatabaseReference rootRef, demoRef;
    public String emailfrnd;
    private DatabaseReference  postRef;
    private DatabaseReference dref;
    private String myChildValues;


    //  https://maps.googleapis.com/maps/api/directions/json?origin=trivandrum&destination=kollam&travelmode=driving&waypoints=kottayam&key=AIzaSyDB-k0v53m7nOvSO4IUv5MwdTHU87bN688
   // https://maps.googleapis.com/maps/api/directions/json?origin=trivandrum&destination=kollam&key=AIzaSyDB-k0v53m7nOvSO4IUv5MwdTHU87bN688
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amgoing=(Button)findViewById(R.id.amgoing);
        joinwith=(Button)findViewById(R.id.joinwith);
        frndlist=(Button)findViewById(R.id.frndlist);
        exit=(Button)findViewById(R.id.exit);





        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
// ...Irrelevant code for customizing the buttons and title
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.text_inpu_password, null);
                dialogBuilder.setView(dialogView);

                final EditText input = (EditText) dialogView.findViewById(R.id.input);
                android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
                dialogBuilder.setPositiveButton("OK ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        String klm=input.getText().toString();
                        int klmint= Integer.parseInt(klm);
                        if (TextUtils.isEmpty(klm)) {
                            input.setError("Please enter Kilometer");
                            input.requestFocus();

                        }else {

                            String Rupees= String.valueOf(klmint*20);
                            Toast.makeText(MainActivity.this,"FARE IS = "+Rupees,Toast.LENGTH_LONG).show();

                          //  dialog.dismiss();

                        }

                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialogBuilder.show();





                //finish();
            }
        });

        amgoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,GoingActivity.class);
                startActivity(intent);

            }
        });
        joinwith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Joinwith.class);
                startActivity(intent);

            }
        });
        frndlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(MainActivity.this,MyFriend.class);
                startActivity(intent);

            }
        });
        //databaseGoingTo = FirebaseDatabase.getInstance().getReference("GoingTo");

        mDatabase = FirebaseDatabase.getInstance().getReference("FriendList");

        EnableRuntimePermission();


        SharedPreferences sharedPreferences=getSharedPreferences("locpoint", Context.MODE_PRIVATE);

        String maptype =sharedPreferences.getString("maptype","0");




        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        final FirebaseUser user = firebaseAuth.getCurrentUser();
       String name=  user.getDisplayName();
       String phone=  user.getPhoneNumber();
       emailfrnd=user.getEmail();

        Toast.makeText(MainActivity.this,"You Are login as  "+user.getEmail(),Toast.LENGTH_LONG).show();




        SharedPreferences sharedpreferences = getSharedPreferences("getid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("id", user.getEmail());
        editor.commit();





        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(15000);
                        runOnUiThread
                                (new Runnable() {
                                    @Override
                                    public void run() {

                                        try {

                                            String checknot=emailfrnd.replaceAll("[^a-zA-Z0-9]", "");

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notification-"+checknot);
                                          //  ref.orderByChild("notify").equalTo(emailfrnd).addValueEventListener(new ValueEventListener() {

                                                ref.orderByChild("notify").addValueEventListener(new ValueEventListener() {

                                                    @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        //username exist
                                                     //   Toast.makeText(MainActivity.this, "Friend"+dataSnapshot, Toast.LENGTH_LONG).show();
                                                        checkTheNotification();

                                                    } else {

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } catch (Exception e) {
                                        }
                                    }
                                });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();




        rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference pointing to demo node
        mDatabase = rootRef.child("FriendMainList").push();


        if(user.getEmail()!=null) {
           // addinFriendList();

            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("FriendMainList");
            ref.orderByChild("value").equalTo(emailfrnd).addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    if(dataSnapshot.exists()) {
                        //username exist
                       // Toast.makeText(MainActivity.this, "exist", Toast.LENGTH_LONG).show();
                    }else {
                       // Toast.makeText(MainActivity.this, "Not Exist", Toast.LENGTH_LONG).show();
                        addinFriendList();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });
        }


        if (maptype.equals("1")){

            SharedPreferences sharedPreferences2=getSharedPreferences("locpoint", Context.MODE_PRIVATE);
            String orgin =sharedPreferences2.getString("from","trivandrum");
            String destination =sharedPreferences2.getString("to","trivandrum");
            String viaplace =sharedPreferences2.getString("via","trivandrum");
            loadpage="https://www.google.com/maps/dir/?api=1&origin="+orgin+"&destination="+destination+"&travelmode=driving&waypoints="+viaplace;

            LoadAnother();

        }else {


            dref= FirebaseDatabase.getInstance().getReference("MapPing");
            dref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // list.add(dataSnapshot.getValue(String.class));
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        myChildValues = snapshot.getValue(String.class);

                        list.add(myChildValues);
                        Toast.makeText(MainActivity.this, "My Places"+list, Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                   /* list.remove(dataSnapshot.getValue(String.class));
                    adapter.notifyDataSetChanged();*/
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });




            //loadpage="https://www.google.com/maps/dir/?api=1&origin="+orgin+"&destination="+destination+"&travelmode=driving&waypoints="+viaplace;

            //loadpage="https://www.google.com/maps/dir/Thiruvananthapuram,+Kerala//Kollam,+Kerala//Alappuzha,+Kerala//nemam//Attingal,+Kerala//pathanamthitta//@9.0106788,76.0772578,9z/data=!4m33!4m32!1m5!1m1!1s0x3b05bbb805bbcd47:0x15439fab5c5c81cb!2m2!1d76.9366376!2d8.5241391!1m0!1m5!1m1!1s0x3b05fc5bdda9c621:0x8bf03195267372f7!2m2!1d76.6141396!2d8.8932118!1m0!1m5!1m1!1s0x3b0884f1aa296b61:0xb84764552c41f85a!2m2!1d76.3388484!2d9.4980667!1m0!1m0!1m0!1m5!1m1!1s0x3b05e9f654143cbf:0x20d213704165f74c!2m2!1d76.817881!2d8.6950338!1m0!1m0!1m0";
            LoadAnother();

        }



    }

    private void checkTheNotification() {

        //Toast.makeText(MainActivity.this, "My Places"+list, Toast.LENGTH_LONG).show();


        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.goride)
                        .setContentTitle("Hai "+emailfrnd)
                        .setSound(soundUri)
                        .setOnlyAlertOnce(true)
                        .setContentText("some people "+"wands to travel with you");

        Intent notificationIntent = new Intent(MainActivity.this, AcceptanceNotify.class);
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());


    }

    private void addinFriendList() {

       mDatabase.child("value").setValue(emailfrnd);



        //displaying a success toast
       // Toast.makeText(this, "Artist added", Toast.LENGTH_LONG).show();

    }


    private void LoadAnother() {





        SharedPreferences sharedPreferences=getSharedPreferences("iteam", Context.MODE_PRIVATE);
        //loadpage =sharedPreferences.getString("nameurl","http://maps.google.com/maps");
/*

        SharedPreferences sharedPreferences2=getSharedPreferences("locpoint", Context.MODE_PRIVATE);
       String orgin =sharedPreferences2.getString("from","trivandrum");
       String destination =sharedPreferences2.getString("to","trivandrum");
       String viaplace =sharedPreferences2.getString("via","trivandrum");



        loadpage="https://www.google.com/maps/dir/?api=1&origin="+orgin+"&destination="+destination+"&travelmode=driving&waypoints="+viaplace;

*/

                web = (WebView) findViewById(R.id.webview01);
        //  progressBar = (ProgressBar) findViewById(R.id.progressBar);

        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(loadpage);
        web.canGoBack();
        web.canGoBackOrForward(2);

        progressDialog = new ProgressDialog(MainActivity.this);
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


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
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



    private void initPubnub() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(Constants.PUBNUB_PUBLISH_KEY);
        pnConfiguration.setSecure(true);
        pubnub = new PubNub(pnConfiguration);
    }




    private void EnableRuntimePermission() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        NOTIFICATION_SERVICE,ACCESS_NOTIFICATION_POLICY,BIND_NOTIFICATION_LISTENER_SERVICE,CALL_PHONE,SEND_SMS,PROCESS_OUTGOING_CALLS,ANSWER_PHONE_CALLS,
                        INTERNET,LOCATION_HARDWARE,LOCATION_SERVICE,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,ACCESS_COARSE_LOCATION,ACCESS_FINE_LOCATION,RECORD_AUDIO

                }, RequestPermissionCode);


    }


    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            // progressBar.setVisibility(View.INVISIBLE);
            super.onPageStarted(view, url, favicon);
        }


    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        firebaseAuth.signOut();
                        //closing activity
                        //finish();
                        //starting login activity

                        SharedPreferences sharedPreferences = getSharedPreferences("locpoint", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("maptype", "0");
                        editor.apply();


                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        // finish();
                    }
                }).setNegativeButton("No", null).show();


    }

}






