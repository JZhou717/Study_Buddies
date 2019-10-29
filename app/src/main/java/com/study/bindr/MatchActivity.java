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
        setContentView(R.layout.activity_match);
        profilePictureImageView = (ImageView)findViewById(R.id.imageViewProfilePic);
        profilePictureImageView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //TODO: MUST ACTUALLY RETRIEVE THE SPECIFIC USER'S PROFILE
                //(currently, we're just loading a mockup of a profile)
                Intent i = new Intent(MatchActivity.this,UserProfileActivity.class);
                startActivity(i);
            }
        });
    }
}