package com.study.bindr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void goToEditCourses(View view) {
        Intent intent = new Intent(RegisterActivity.this, EditCoursesActivity.class);
        startActivity(intent);
    }
}
