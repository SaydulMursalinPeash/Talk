package com.example.talk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private ArrayList<User> users;
    private Context context;
    onUserClickListener on_user_click;

    public UsersAdapter(ArrayList<User> users, Context context, onUserClickListener on_user_click) {
        this.users = users;
        this.context = context;
        this.on_user_click = on_user_click;
    }

    interface onUserClickListener{
        void onUserClicked(int pos);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_holder_cart,parent,false);
        Log.d("adapter","called");
        return new ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.friends_name.setText(users.get(position).getUserName());
        Glide.with(context).load(users.get(position).getProfile_picture()).error(R.drawable.account_icon).placeholder(R.drawable.account_icon).into(holder.friends_photo);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView friends_name;
        ImageView friends_photo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    on_user_click.onUserClicked(getAdapterPosition());
                }
            });
            friends_name=itemView.findViewById(R.id.friend_name);
            friends_photo=itemView.findViewById(R.id.friend_photo);

        }
    }
}
