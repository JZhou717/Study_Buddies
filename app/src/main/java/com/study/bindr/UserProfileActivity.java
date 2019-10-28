package com.study.bindr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

public class UserProfileActivity extends Activity{
    private TextView bioTextView;
    private TextView interestsTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_user_profile);
        bioTextView = (TextView)findViewById(R.id.textViewBio);
        interestsTextView = (TextView)findViewById(R.id.textViewInterests);
        bioTextView.setMovementMethod(new ScrollingMovementMethod());
        interestsTextView.setMovementMethod(new ScrollingMovementMethod());
    }
}
