package com.study.bindr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import model.Course;
import model.Session;
import model.Student;

public class SessionAdapter extends ArrayAdapter<Session> {


    private Context sessionContext;
    private List<Session> sessionList;

    public SessionAdapter(@NonNull Context context, List<Session> list) {
        super(context , 0, list);
        sessionContext = context;
        sessionList = list;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItem = convertView;
        if(listItem == null){
            listItem = LayoutInflater.from(sessionContext).inflate(R.layout.course_row,parent,false);
        }
        Session c = sessionList.get(position);
        TextView name = (TextView) listItem.findViewById(R.id.name);
        //TODO: Set the text for each session row
        name.setText("Click to rate your session with ...");
        return listItem;
    }

}
