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

public class CourseAdapter extends ArrayAdapter<Course> {


    private Context courseContext;
    private List<Course> courseList;

    public CourseAdapter(@NonNull Context context, List<Course> list) {
        super(context , 0, list);
        courseContext = context;
        courseList = list;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItem = convertView;
        if(listItem == null){
            listItem = LayoutInflater.from(courseContext).inflate(R.layout.course_row,parent,false);
        }
        Course c = courseList.get(position);
        TextView name = (TextView) listItem.findViewById(R.id.name);
        name.setText(c.getCourseName());
        return listItem;
    }

}
