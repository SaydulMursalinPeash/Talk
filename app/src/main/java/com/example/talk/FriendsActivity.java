package com.example.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FriendsActivity extends AppCompatActivity {
    private ImageView profile;
    private RecyclerView r_view;
    private ArrayList<User> users;
    private UsersAdapter.onUserClickListener on_user_click;
    private ProgressBar progress_bar;
    private UsersAdapter users_adapter;
    private SwipeRefreshLayout sl;
    private String my_image_url,my_name,my_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_friends);
        profile=(ImageView) findViewById(R.id.profile_button);
        progress_bar=(ProgressBar) findViewById(R.id.progressBar);
        r_view=(RecyclerView) findViewById(R.id.recycler_view);
        users=new ArrayList<>();
        sl=(SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        Glide.with(FriendsActivity.this).load(my_image_url).error(R.drawable.account_icon).placeholder(R.drawable.account_icon).into(profile);
        sl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
                sl.setRefreshing(false);
            }
        });
        on_user_click=new UsersAdapter.onUserClickListener() {
            @Override
            public void onUserClicked(int pos) {
                startActivity(new Intent(FriendsActivity.this,MessageActivity.class).putExtra(
                        "chat_user_name",users.get(pos).getUserName()
                        ).putExtra("chat_user_email",users.get(pos).getEmail())
                        .putExtra("chat_user_photo",users.get(pos).getProfile_picture())
                        .putExtra("my_photo",my_image_url)
                );
                //Toast.makeText(FriendsActivity.this, "Tapped on user.."+users.get(pos).getUserName(), Toast.LENGTH_SHORT).show();
            }
        };
        //Showing user list in recycler view
        getUsers();

        //Setting click event to Profile button
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendsActivity.this,Profile.class).putExtra("my_photo",my_image_url).putExtra("name",my_name).putExtra("email",my_email));
            }
        });
    }


    //Function to get User from firebase database
    private void getUsers(){
        users.clear();
        //Toast.makeText(getApplicationContext(),"Get User Called",Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap:snapshot.getChildren()){
                    users.add(snap.getValue(User.class));



                }
                for(User u:users){
                    if(u.getEmail().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())){
                        my_image_url=u.getProfile_picture();
                        my_name=u.getUserName();
                        my_email=u.getEmail();
                        users.remove(u);
                        break;
                    }
                }
                users_adapter=new UsersAdapter(users,FriendsActivity.this,on_user_click);
                r_view.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
                r_view.setAdapter(users_adapter);
                progress_bar.setVisibility(View.GONE);
                r_view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}