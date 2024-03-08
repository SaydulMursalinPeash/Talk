package com.example.talk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private ArrayList<Message> messages;
    private String sender_img,receiver_img;
    private Context context;

    public MessageAdapter(ArrayList<Message> messages, String sender_img, String receiver_img, Context context) {
        this.messages = messages;
        this.sender_img = sender_img;
        this.receiver_img = receiver_img;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.message_holder_cart,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        holder.text.setText(messages.get(position).getContent());
        ConstraintLayout c= holder.constraintLayout;
        if(messages.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            Glide.with(context).load(sender_img).error(R.drawable.account_icon).placeholder(R.drawable.account_icon).into(holder.image);
            ConstraintSet cs=new ConstraintSet();
            cs.clone(c);
            cs.clear(R.id.cardView2,ConstraintSet.LEFT);
            cs.clear(R.id.message_text,ConstraintSet.LEFT);
            cs.connect(R.id.cardView2,ConstraintSet.RIGHT,R.id.cc_layout,ConstraintSet.RIGHT,0);
            cs.connect(R.id.message_text,ConstraintSet.RIGHT,R.id.cardView2,ConstraintSet.LEFT,0);

            cs.applyTo(c);
        }else{
            Glide.with(context).load(receiver_img).error(R.drawable.account_icon).placeholder(R.drawable.account_icon).into(holder.image);
            ConstraintSet cs=new ConstraintSet();
            cs.clone(c);
            cs.clear(R.id.cardView2,ConstraintSet.RIGHT);
            cs.clear(R.id.message_text,ConstraintSet.RIGHT);
            cs.connect(R.id.cardView2,ConstraintSet.LEFT,R.id.cc_layout,ConstraintSet.LEFT,0);
            cs.connect(R.id.message_text,ConstraintSet.LEFT,R.id.cardView2,ConstraintSet.RIGHT,0);

            cs.applyTo(c);

        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        TextView text;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout=itemView.findViewById(R.id.cc_layout);
            text=itemView.findViewById(R.id.message_text);
            image=itemView.findViewById(R.id.small_profile_image);
        }
    }
}
