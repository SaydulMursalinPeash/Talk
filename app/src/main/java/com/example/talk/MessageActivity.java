package com.example.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView chat_recycler;
    private EditText edit_message;
    private ProgressBar progress;
    private ArrayList<Message> message_list;
    private String user_name_of_roommate;
    private String room_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_message);
        chat_recycler=(RecyclerView) findViewById(R.id.chat_recycle);
        edit_message=(EditText) findViewById(R.id.edit_msg);
        TextView chat_user_name = (TextView) findViewById(R.id.chat_user_name);
        ImageView chat_user_photo = (ImageView) findViewById(R.id.chat_user_photo);
        progress=(ProgressBar) findViewById(R.id.chat_progress);
        ImageView send_button = (ImageView) findViewById(R.id.send_button);
        message_list=new ArrayList<>();
        user_name_of_roommate=getIntent().getStringExtra("chat_user_name");
        String chat_with_email = getIntent().getStringExtra("chat_user_email");
        String chat_with_photo = getIntent().getStringExtra("chat_user_photo");
        String my_photo_link = getIntent().getStringExtra("my_photo");
        chat_user_name.setText(user_name_of_roommate);

        MessageAdapter message_adapter = new MessageAdapter(message_list,
                getIntent().getStringExtra("my_photo"),
                getIntent().getStringExtra("chat_user_photo"),
                MessageActivity.this);

        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("chat_user_photo")).
                error(R.drawable.account_icon).
                placeholder(R.drawable.account_icon).
                into(chat_user_photo);
        chat_recycler.setLayoutManager(new LinearLayoutManager(this));
        chat_recycler.setAdapter(message_adapter);

        setupChatRoom();
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("messages/"+room_id).push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),getIntent().getStringExtra("chat_user_email"),edit_message.getText().toString()));
                edit_message.setText("");


            }
        });

    }
    private void setupChatRoom(){
        FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String my_username= Objects.requireNonNull(snapshot.getValue(User.class)).getUserName();
                if(user_name_of_roommate.compareTo(my_username)>0){
                    room_id=my_username+user_name_of_roommate;
                }else if(user_name_of_roommate.compareTo(my_username)==0){
                    room_id=my_username+user_name_of_roommate;

                }
                else{
                    room_id=user_name_of_roommate+my_username;
                }
                attachMessageListener(room_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void attachMessageListener(String id){
        FirebaseDatabase.getInstance().getReference("messages/"+id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                message_list.clear();
                for(DataSnapshot m:snapshot.getChildren()){
                    message_list.add(m.getValue(Message.class));
                }
                //message_adapter.notifyDataSetChanged();
                chat_recycler.scrollToPosition(message_list.size()-1);
                chat_recycler.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}