package com.appsinventiv.toolsbazzar.Activities;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.appsinventiv.toolsbazzar.Adapters.ChatAdapter;
import com.appsinventiv.toolsbazzar.Interface.NotificationObserver;
import com.appsinventiv.toolsbazzar.Models.AdminModel;
import com.appsinventiv.toolsbazzar.Models.ChatModel;
import com.appsinventiv.toolsbazzar.R;
import com.appsinventiv.toolsbazzar.Utils.NotificationAsync;
import com.appsinventiv.toolsbazzar.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WholesaleLiveChat extends AppCompatActivity implements NotificationObserver {

    DatabaseReference mDatabase;
    EditText message;
    ImageView send;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ChatAdapter adapter;
    ArrayList<ChatModel> chatModelArrayList = new ArrayList<>();
    int soundId;
    SoundPool sp;
    String adminFcmKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(WholesaleLiveChat.this, R.raw.tick_sound, 1);




    }

    @Override
    protected void onResume() {
        super.onResume();
        getAdminDetails();
        getMessagesFromServer();
        sendMessageToServer();
        readAllMessages();
    }

    private void getAdminDetails() {
        mDatabase.child("Admin").child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    AdminModel model = dataSnapshot.getValue(AdminModel.class);
                    if (model != null) {
                        WholesaleLiveChat.this.setTitle(model.getId());
                        adminFcmKey = model.getFcmKey();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readAllMessages() {
        mDatabase.child("Chats/WholesaleChats").child(SharedPrefs.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel chatModel = snapshot.getValue(ChatModel.class);
                        if (chatModel != null) {
                            if (!chatModel.getUsername().equals(SharedPrefs.getUsername())) {
                                mDatabase.child("Chats/WholesaleChats").child(SharedPrefs.getUsername()).child(chatModel.getId()).child("status").setValue("read");
                            }
                        }
                    }
                    SharedPrefs.setNewMsg("0");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMessagesFromServer() {
        recyclerView = findViewById(R.id.chats);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(WholesaleLiveChat.this, chatModelArrayList);
        recyclerView.setAdapter(adapter);

        mDatabase.child("Chats/WholesaleChats").child(SharedPrefs.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    chatModelArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = snapshot.getValue(ChatModel.class);
                        if (model != null) {
                            chatModelArrayList.add(model);
                            recyclerView.scrollToPosition(chatModelArrayList.size() - 1);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    recyclerView.scrollToPosition(chatModelArrayList.size() - 1);
                }

            }
        });

    }

    private void sendMessageToServer() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (message.getText().length() == 0) {
                    message.setError("Can't send empty message");
                } else {
                    final String msg = message.getText().toString();
                    message.setText(null);
                    final String key = mDatabase.push().getKey();
                    mDatabase.child("Chats/WholesaleChats").child(SharedPrefs.getUsername()).child(key)
                            .setValue(new ChatModel(key, msg, SharedPrefs.getUsername()
                                    , System.currentTimeMillis(), "sending",SharedPrefs.getUsername(),

                                    SharedPrefs.getName())).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            sp.play(soundId, 1, 1, 0, 0, 1);
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(chatModelArrayList.size() - 1);

                            mDatabase.child("Chats/WholesaleChats").child(SharedPrefs.getUsername()).child(key).child("status").setValue("sent");


                            NotificationAsync notificationAsync = new NotificationAsync(WholesaleLiveChat.this);
                            String NotificationTitle = "New message from " + SharedPrefs.getUsername();
                            String NotificationMessage = "Message: " + msg;
                            notificationAsync.execute("ali", adminFcmKey, NotificationTitle, NotificationMessage, "WholesaleChat", key);

                        }
                    });
                }
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSuccess(String chatId) {
        mDatabase.child("Chats/WholesaleChats").child(SharedPrefs.getUsername()).child(chatId).child("status").setValue("delivered");
    }

    @Override
    public void onFailure() {

    }
}
