package com.sourcey.materiallogindemo;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import java.util.LinkedHashMap;

public class GoingActivity extends AppCompatActivity  implements
        AdapterView.OnItemSelectedListener{

        private FusedLocationProviderClient mFusedLocationClient; // Object used to receive location updates

    EditText from,to,time,via,phone;
    private LocationRequest locationRequest; // Object that defines important parameters regarding location request.
    private String emailId;
    Button submit;

    String[] typeofper = { "Driver", "Individual"};
    public String pesonType="Driver";
    private DatabaseReference databaseGoingTo;
    private FirebaseAuth firebaseAuth;
    private String fromf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_going);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(GoingActivity.this);

        databaseGoingTo = FirebaseDatabase.getInstance().getReference("GoingTo");

        SharedPreferences prefs = getSharedPreferences("getid", MODE_PRIVATE);
         emailId = prefs.getString("id", null);

        from = (EditText) findViewById(R.id.goingfrom);
        to = (EditText) findViewById(R.id.goingto);
        time = (EditText) findViewById(R.id.time);
        via = (EditText) findViewById(R.id.myroute);
        phone = (EditText) findViewById(R.id.phone);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 second delay between each request
        locationRequest.setFastestInterval(5000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(1); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests

        sendUpdatedLocationMessage();


        Spinner spin = (Spinner) findViewById(R.id.typeofperson);
        spin.setOnItemSelectedListener(this);


        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,typeofper);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(aa);

        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save to fire base data base
                addArtist();

            }
        });
    }

    private void addArtist() {

        /*
         * This method is saving a new artist to the
         * Firebase Realtime Database
         * */

            //getting the values to save
             fromf = from.getText().toString().trim();
            String tof = to.getText().toString();
            String timef = time.getText().toString();
            String viaf = via.getText().toString();
            String phonef = phone.getText().toString();

            //checking if the value is provided
            if (!TextUtils.isEmpty(tof)) {

                //getting a unique id using push().getKey() method
                //it will create a unique id and we will use it as the Primary Key for our Artist
                String id = databaseGoingTo.push().getKey();

                //creating an Artist Object
                GoingModel artist = new GoingModel(id,emailId, fromf, tof,timef,viaf,pesonType,phonef);

                //Saving the Artist
                databaseGoingTo.child(id).setValue(artist);

                //setting edittext to blank again
                to.setText("");
                from.setText("");
                time.setText("");
                via.setText("");
                phone.setText("");

                //displaying a success toast
                Toast.makeText(this, "Going Ride is added", Toast.LENGTH_LONG).show();
                addMapPing();

            } else {
                //if the value is not given displaying a toast
                Toast.makeText(this, "Please enter the Place", Toast.LENGTH_LONG).show();
            }



        }

    private void addMapPing() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference pointing to demo node
        rootRef = rootRef.child("MapPing").push();

        rootRef.setValue("//"+fromf);

        Toast.makeText(this, "Add to map", Toast.LENGTH_LONG).show();


    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(),typeofper[position] , Toast.LENGTH_LONG).show();
        pesonType=typeofper[position];
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    /*
        This method gets user's current location and publishes message to channel.
     */
    private void sendUpdatedLocationMessage() {
        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
            try{


                    final Location location = locationResult.getLastLocation();
                    LinkedHashMap<String, String> message = getNewLocationMessage(location.getLatitude(), location.getLongitude());


                        MainActivity.pubnub.publish()
                                .message(message)
                                .channel(Constants.PUBNUB_CHANNEL_NAME)
                                .async(new PNCallback<PNPublishResult>() {
                                    @Override
                                    public void onResponse(PNPublishResult result, PNStatus status) {

                                        double s = location.getLatitude();
                                        double ss = location.getLongitude();
                                        Toast.makeText(GoingActivity.this, "location" + s + "   " + ss + "  " + emailId, Toast.LENGTH_LONG).show();
                                        // handle publish result, status always present, result if successful
                                        // status.isError() to see if error happened
                                        if (!status.isError()) {
                                            System.out.println("pub timetoken: " + result.getTimetoken());
                                        }
                                        System.out.println("pub status code: " + status.getStatusCode());
                                    }
                                });
                    }catch (Exception e){
}
                }
                 }, Looper.myLooper());

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    /*
        Helper method that takes in latitude and longitude as parameter and returns a LinkedHashMap representing this data.
        This LinkedHashMap will be the message published by driver.
     */
    private LinkedHashMap<String, String> getNewLocationMessage(double lat, double lng) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("lat", String.valueOf(lat));
        map.put("lng", String.valueOf(lng));
        return map;
    }
}