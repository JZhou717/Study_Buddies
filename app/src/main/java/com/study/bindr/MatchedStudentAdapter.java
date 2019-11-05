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
import model.Student;


//This adapter follows the view holder design pattern, which means that it allows you to define a custom class that extends RecyclerView.ViewHolder.
public class MatchedStudentAdapter extends RecyclerView.Adapter<MatchedStudentAdapter.ViewHolder> {

    private ArrayList<Student> studentsList = new ArrayList<>();

    //The interface (implemented in activity) will be passed to each individual view so will know where to go to when a match icon is clicked
    private OnMatchIconListener onMatchIconListener;

    public MatchedStudentAdapter(ArrayList<Student> studentsList, Context context,OnMatchIconListener onMatchIconListener) {
        this.studentsList = studentsList;
        this.context = context;
        this.onMatchIconListener=onMatchIconListener;
    }

    private Context context;

    @NonNull
    @Override
    //will be called whenever viewholder is created
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.matched_icon, parent, false);
        return new ViewHolder(view,onMatchIconListener);
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

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        //Get the objects from xml
        CircleImageView image;
        TextView name;
        //Each view holder will have a OnMatchIconListener interface
        OnMatchIconListener onMatchIconListener;
        public ViewHolder(View itemView,OnMatchIconListener onMatchIconListener) {
            super(itemView);
            image = itemView.findViewById(R.id.matchedProfile);
            name = itemView.findViewById(R.id.matchedName);

            this.onMatchIconListener=onMatchIconListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMatchIconListener.OnMatchIconClick(getAdapterPosition());
        }
    }

    public interface OnMatchIconListener{
        //Use this method in activity to send position of the clicked item
        void OnMatchIconClick(int position);
    }
}
