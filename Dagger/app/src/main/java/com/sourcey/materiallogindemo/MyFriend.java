package com.sourcey.materiallogindemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

class MyFriend extends AppCompatActivity {

    DatabaseReference dref;
    ListView listview;
    ArrayList<String> list=new ArrayList<>();
    String userEmail;
    private DatabaseReference rootRef,mDatabase;
    private String item;
    private String myFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfriend);
        listview=(ListView)findViewById(R.id.listview);
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list);
        listview.setAdapter(adapter);

        Button button=(Button)findViewById(R.id.myfriend) ;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),FriendList.class);
                startActivity(intent);

            }
        });


        SharedPreferences sharedPreferences2=getSharedPreferences("getid", Context.MODE_PRIVATE);
        userEmail =sharedPreferences2.getString("id","vichuavk@gmail.com");
        myFriend=userEmail.replaceAll("[^a-zA-Z0-9]", "");

      //  Toast.makeText(this, ""+myFriend, Toast.LENGTH_LONG).show();


        dref= FirebaseDatabase.getInstance().getReference(myFriend);
        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // list.add(dataSnapshot.getValue(String.class));
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String myChildValues = snapshot.getValue(String.class);
                    list.add(myChildValues);

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



            }


        });



    }

    private void addAsMyFriend() {

        rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference pointing to demo node
        mDatabase = rootRef.child(myFriend).push();

        mDatabase.child("value").setValue(item);

        Toast.makeText(this, "Artist added", Toast.LENGTH_LONG).show();

    }


}