package com.sourcey.materiallogindemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AcceptanceNotify extends AppCompatActivity {


    DatabaseReference dref;
    ListView listview;
    ArrayList<String> list=new ArrayList<>();
    String userEmail;
    private DatabaseReference rootRef,mDatabase;
    private String item;
    private String myFriend;
    private String checknot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptance_notify);


        listview=(ListView)findViewById(R.id.listview);
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list);
        listview.setAdapter(adapter);




        SharedPreferences sharedPreferences2=getSharedPreferences("getid", Context.MODE_PRIVATE);
        userEmail =sharedPreferences2.getString("id","vichuavk@gmail.com");
        
        checknot = userEmail.replaceAll("[^a-zA-Z0-9]", "");





        dref= FirebaseDatabase.getInstance().getReference("Notification-"+checknot);
      //
        //  dref.child("notify");


        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                // list.add(dataSnapshot.getValue(String.class));
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String value= dataSnapshot.child("notify").getValue(String.class);
                    list.add(value);
                  //  list.add(myChildValues);

                }

                adapter.notifyDataSetChanged();


            }




            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                list.remove(dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                item = ((TextView)view).getText().toString();



                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(AcceptanceNotify.this);

                alertDialogBuilder.setTitle("ADD TO RIDE");

                alertDialogBuilder
                        .setMessage("Add "+item+" with ride")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {


                                myFriend=userEmail.replaceAll("[^a-zA-Z0-9]", "");
                                //Toast.makeText(getBaseContext(), myFriend, Toast.LENGTH_LONG).show();

                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Addwithridedriver"+myFriend);
                                ref.orderByChild("value").equalTo(item).addValueEventListener(new ValueEventListener(){
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot){
                                        if(dataSnapshot.exists()) {
                                            //username exist
                                            Toast.makeText(AcceptanceNotify.this, " Alredy Exist", Toast.LENGTH_LONG).show();
                                        }else {
                                            Toast.makeText(AcceptanceNotify.this, "Add To cab", Toast.LENGTH_LONG).show();
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
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                dialog.cancel();
                            }
                        }).setNeutralButton("Location", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        dref= FirebaseDatabase.getInstance().getReference("Notification-"+checknot);
                        dref.child("notify");


                        dref.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                // list.add(dataSnapshot.getValue(String.class));
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    String value= dataSnapshot.child("location").getValue(String.class);
                                    Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(AcceptanceNotify.this,LocationOfFriends.class);
                                    intent.putExtra("locationkey", value);
                                    startActivity(intent);

                                }

                                adapter.notifyDataSetChanged();


                            }




                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            }
                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                list.remove(dataSnapshot.getValue(String.class));
                                adapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });




                    }
                })

                ;


                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }


        });



    }

    private void addAsMyFriend() {

        rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference pointing to demo node
        mDatabase = rootRef.child("Addwithridedriver"+myFriend).push();

        mDatabase.child("value").setValue(item);

        Toast.makeText(this, " Added Come with us", Toast.LENGTH_LONG).show();

    }


}