package com.sourcey.materiallogindemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

;

public class Joinwith extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private DatabaseReference databaseGoingTo;
    ListView listViewArtists;
    FirebaseAuth firebaseAuth;

    // private DatabaseReference mDatabase;


    // javascrip  appid  KAErls2IFLbh9YT0AUHg   appcode  dBMQxMfDpRex_ULMpiMZSw

    //a list to store all the artist from firebase database
    //List<GoingModel> artists;


    private Location location;
    private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;


    private List<GoingModel> artists = new ArrayList<>();
    //   private OkHttpHandler okHttpHandler;
    OkHttpClient client = new OkHttpClient();
    private String userEmail;
    private String myFriend;
    private DatabaseReference rootRef, mDatabase;
    private String item;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinwith);
        listViewArtists = (ListView) findViewById(R.id.listViewArtists);

        SharedPreferences sharedPreferences2 = getSharedPreferences("getid", Context.MODE_PRIVATE);
        userEmail = sharedPreferences2.getString("id", "vichuavk@gmail.com");

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(Joinwith.this);


        databaseGoingTo = FirebaseDatabase.getInstance().getReference("GoingTo");


        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                final GoingModel artist = artists.get(i);


                SharedPreferences sharedPreferences = getSharedPreferences("locpoint", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("from", artist.getPersonFrom());
                editor.putString("to", artist.getPersonTo());
                editor.putString("via", artist.getPersonVia());
                editor.putString("cost", "0");
                editor.putString("maptype", "1");
                editor.apply();


                if (artist.getPersonType().equals("Driver")) {

                    item = artist.getPersonName();
                    // Toast.makeText(Joinwith.this, "This Person is a driver so you need to pay", Toast.LENGTH_LONG).show();

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Joinwith.this);

                    alertDialogBuilder.setTitle("DRIVER");

                    alertDialogBuilder
                            .setMessage("Dear  " + userEmail + ".\n Mr. " + artist.getPersonName() + "  \nThis Person is a Driver so you need to pay for the ride")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                    sendNotificationTodriver();


                                    Intent mainClass = new Intent(Joinwith.this, MainActivity.class);
                                    startActivity(mainClass);
                                    finish();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();


                } else {

                    item = artist.getPersonName();

                    myFriend = userEmail.replaceAll("[^a-zA-Z0-9]", "");
                    //Toast.makeText(getBaseContext(), myFriend, Toast.LENGTH_LONG).show();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(myFriend);
                    ref.orderByChild("value").equalTo(item).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                //username exist
                                Toast.makeText(Joinwith.this, "Friend", Toast.LENGTH_LONG).show();

                                Intent mainClass = new Intent(Joinwith.this, MainActivity.class);
                                startActivity(mainClass);
                                finish();

                            } else {
                                Toast.makeText(Joinwith.this, "This Person is not your friend", Toast.LENGTH_LONG).show();

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Joinwith.this);

                                alertDialogBuilder.setTitle("ADD AS FRIEND");

                                alertDialogBuilder
                                        .setMessage("Add " + item + " as your friend")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {


                                                myFriend = userEmail.replaceAll("[^a-zA-Z0-9]", "");
                                                //Toast.makeText(getBaseContext(), myFriend, Toast.LENGTH_LONG).show();

                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(myFriend);
                                                ref.orderByChild("value").equalTo(item).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            //username exist
                                                            Toast.makeText(Joinwith.this, "Friend Alredy Exist", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(Joinwith.this, "Add Friend", Toast.LENGTH_LONG).show();
                                                            addAsMyFriend();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                // FriendList.this.finish();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                dialog.cancel();
                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();


                                //addAsMyFriend();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                // startActivity(intent);
            }
        });

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
    }







    private void sendNotificationTodriver() {


        String notname=item.replaceAll("[^a-zA-Z0-9]", "");
        rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference pointing to demo node
        mDatabase = rootRef.child("Notification-"+notname).push();
        mDatabase.child("notify").setValue(userEmail);
        mDatabase.child("location").setValue(location.getLatitude()+","+location.getLongitude());

        Toast.makeText(this, "Notification send please wait for acceptance", Toast.LENGTH_LONG).show();



    }

    private void addAsMyFriend() {
        rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference pointing to demo node
        mDatabase = rootRef.child(myFriend).push();

        mDatabase.child("value").setValue(item);

        Toast.makeText(this, "Artist added", Toast.LENGTH_LONG).show();


    }


    void run() throws IOException {

        SharedPreferences sharedPreferences2=getSharedPreferences("locpoint", Context.MODE_PRIVATE);
        String orgin =sharedPreferences2.getString("from","trivandrum");
        String desti =sharedPreferences2.getString("to","trivandrum");
        String via =sharedPreferences2.getString("via","trivandrum");

         client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/directions/json?origin="+orgin+"&destination="+desti+"&travelmode=driving&waypoints="+via+"&key=AIzaSyDB-k0v53m7nOvSO4IUv5MwdTHU87bN688")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                Joinwith.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);

                            JSONArray status = jsonObject.getJSONArray("routes");

                            // Log.i("status", status);

                            Toast.makeText(getApplicationContext(),""+status,Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"full error",Toast.LENGTH_LONG).show();
                        }




                       // Toast.makeText(getApplicationContext(),""+myResponse,Toast.LENGTH_LONG).show();
                       // txtString.setText(myResponse);
                    }


                });



            }
        });
    }




    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        //attaching value event listener
        databaseGoingTo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
              //  artists.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    GoingModel artistr = postSnapshot.getValue(GoingModel.class);
                    //adding artist to the list
                    artists.add(artistr);
                }

                //creating adapter
                GoingList artistAdapter = new GoingList(Joinwith.this, artists);
                //attaching adapter to the listview
                listViewArtists.setAdapter(artistAdapter);




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            //locationTv.setText("You need to install Google Play Services to use the App properly");
            Toast.makeText(getApplicationContext(),"You need to install Google Play Services to use the App properly",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());

            Toast.makeText(getApplicationContext(),"Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(),Toast.LENGTH_LONG ).show();

        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            Toast.makeText(getApplicationContext(),"Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(),Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(Joinwith.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }
}
