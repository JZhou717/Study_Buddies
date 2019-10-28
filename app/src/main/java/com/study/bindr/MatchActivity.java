package com.study.bindr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.view.View;
import android.content.Intent;


public class MatchActivity extends Activity {
    private ImageView profilePictureImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_user_profile);
        profilePictureImageView = (ImageView)findViewById(R.id.imageViewProfilePic);
        profilePictureImageView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),UserProfileActivity.class);
                startActivity(i);
            }
        });
    }
}