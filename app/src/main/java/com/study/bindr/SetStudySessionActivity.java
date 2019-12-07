package com.study.bindr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import model.Chat;
import model.Session;

public class SetStudySessionActivity extends AppCompatActivity{
    private TextView dateTextView;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    private TextView timeTextView;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private EditText reminderEditText;
    private Chat chat=null;
    Calendar setCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_study_session);
        getSupportActionBar().setTitle("Setup Study Session");

        dateTextView= (TextView) findViewById(R.id.dateTextView);
        timeTextView=(TextView) findViewById(R.id.timeTextView);
        reminderEditText=(EditText) findViewById(R.id.reminderEditText);
        //get chats class
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        chat=(Chat)bundle.getSerializable("Chat");
        System.out.println("Study Session for chat "+chat.getRoom());


    }
    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Chat", chat);
        bundle.putString("type", "chat");
        Intent intent = new Intent(SetStudySessionActivity.this, ChatboxActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public void onCancelStudySession(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Chat", chat);
        bundle.putString("type", "chat");
        Intent intent = new Intent(SetStudySessionActivity.this, ChatboxActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public void onRequestStudySession(View view) {
        if (timeTextView.getText().equals("") || dateTextView.getText().equals("")){
            Toast.makeText(SetStudySessionActivity.this, "Please select time and date", Toast.LENGTH_LONG).show();
            return;
        }
        Date date=setCalendar.getTime();
        String reminderString=reminderEditText.getText().toString();
        int reminder=0;
        if(!reminderString.equals("")){
            reminder=Integer.parseInt(reminderEditText.getText().toString());
        }
        System.out.println("DATE "+date);
        Session session=new Session(chat.getChattingStudentID(), date,reminder);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Chat", chat);
        bundle.putString("type", "chat");
        bundle.putSerializable("Session", session);

        Intent intent = new Intent(SetStudySessionActivity.this, ChatboxActivity.class);
        Toast.makeText(SetStudySessionActivity.this, "Study Session Request Sent", Toast.LENGTH_LONG).show();
        intent.putExtras(bundle);

        startActivity(intent);
    }


    public void onSelectDate(View view) {

        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                setCalendar.set(Calendar.YEAR, year);
                setCalendar.set(Calendar.MONTH, month);
                setCalendar.set(Calendar.DATE, date);
                month++;
                String selectedDate = month + "/" + date + "/" + year;
                dateTextView.setText(selectedDate);
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();

    }

    public void onSelectStartTime(View view) {

        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                setCalendar.set(Calendar.HOUR, hour);
                setCalendar.set(Calendar.MINUTE, minute);
                String dateText = DateFormat.format("h:mm a", setCalendar).toString();
                timeTextView.setText(dateText);

            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();
    }

}
