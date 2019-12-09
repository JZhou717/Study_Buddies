package com.study.bindr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    /**
     * displays activity, runs code for startup.
     * @param savedInstanceState -bundle passed by previous activity
     */
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
        //System.out.println("Study Session for chat "+chat.getRoom());
    }

    /**
     * Sends back Chat object when back is pressed
     */
    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Chat", chat);
        bundle.putString("type", "chat");
        Intent intent = new Intent(SetStudySessionActivity.this, ChatboxActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }
    /**
     * Sends back Chat object when set up study session is cancelled
     */
    public void onCancelStudySession(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Chat", chat);
        bundle.putString("type", "chat");
        Intent intent = new Intent(SetStudySessionActivity.this, ChatboxActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    /**
     * Creates a Session object with the given date, time and reminder input from the user, and sends it back to chatbox activity
     * @param view
     */
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


    /**
     * shows a date picker when user clicks 'Select Date" button.
     * Listens to what the user selects.
     * @param view
     */
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
    /**
     * shows a time picker when user clicks 'Select Time" button.
     * Listens to what the user selects.
     * @param view
     */
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
