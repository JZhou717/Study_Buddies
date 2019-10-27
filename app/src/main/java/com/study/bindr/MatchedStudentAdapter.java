package com.study.bindr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


//This adapter follows the view holder design pattern, which means that it allows you to define a custom class that extends RecyclerView.ViewHolder.
public class MatchedStudentAdapter extends RecyclerView.Adapter<MatchedStudentAdapter.ViewHolder> {
    private ArrayList<Student> studentsList = new ArrayList<>();

    public MatchedStudentAdapter(ArrayList<Student> studentsList, Context context) {
        this.studentsList = studentsList;
        this.context = context;
    }

    private Context context;

    @NonNull
    @Override
    //will be called whenever viewholder is created
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.matched_icon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    //will be called after viewholder is created. It binds data to the viewholder
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student=studentsList.get(position);
        holder.name.setText(student.getFullName());
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Get the objects from xml
        CircleImageView image;
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.matchedProfile);
            name = itemView.findViewById(R.id.matchedName);
        }

    }
}
