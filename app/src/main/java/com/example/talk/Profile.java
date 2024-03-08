package com.example.talk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Profile extends AppCompatActivity {
    private Button log_out,upload;
    private ImageView profile_image;
    private Uri imagePath;
    private TextView name;
    private TextView email;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null){
            imagePath=data.getData();
            try {
                getImageInView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getImageInView() throws IOException {
        Bitmap bit= MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
        profile_image.setImageBitmap(bit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);
        log_out=(Button) findViewById(R.id.log_out_btn);
        upload=(Button) findViewById(R.id.upload_button);
        profile_image=(ImageView) findViewById(R.id.profile_image) ;
        name=(TextView)findViewById(R.id.username_show);
        email=(TextView)findViewById(R.id.email_show);
        name.setText(getIntent().getStringExtra("name"));
        email.setText(getIntent().getStringExtra("email"));

        Glide.with(Profile.this).load(getIntent().getStringExtra("my_photo")).error(R.drawable.account_icon).placeholder(R.drawable.account_icon).into(profile_image);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent=new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,1);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPhoto();
            }
        });


    }

    private void uploadPhoto() {
        ProgressDialog progress=new ProgressDialog(this);
        progress.setTitle("Uploading..");
        progress.show();
        FirebaseStorage.getInstance().getReference("Image/*"+ UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                updateImage(task.getResult().toString());
                            }

                        }
                    });

                    Toast.makeText(getApplicationContext(), "Upload Successfull..", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Profile.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double p=100.00*snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                progress.setMessage("Uploaded: "+(int)p+"%");
            }
        });
    }

    private void updateImage(String u) {
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+"/profile_picture").setValue(u);
    }
}