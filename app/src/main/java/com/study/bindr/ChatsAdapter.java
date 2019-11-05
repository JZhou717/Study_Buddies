package com.study.bindr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import model.Student;


//This adapter follows the view holder design pattern, which means that it allows you to define a custom class that extends RecyclerView.ViewHolder.
public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private ArrayList<Student> studentsList = new ArrayList<>();

    //The interface (implemented in activity) will be passed to each individual view so will know where to go to when a chat is clicked
    private OnChatListener onChatListener;

    public ChatsAdapter(ArrayList<Student> studentsList, Context context, OnChatListener onChatListener) {
        this.studentsList = studentsList;
        this.context = context;
        this.onChatListener=onChatListener;
    }

    private Context context;

    @NonNull
    @Override
    //will be called whenever viewholder is created
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);
        return new ViewHolder(view,onChatListener);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Get the objects from xml
        ImageView image;
        TextView name;
        TextView lastMessage;
        //Each view holder will have a onChatListener interface
        OnChatListener onChatListener;

        public ViewHolder(View itemView,OnChatListener onChatListener) {
            super(itemView);
            image = itemView.findViewById(R.id.chatPhoto);
            name = itemView.findViewById(R.id.chatName);
            lastMessage = itemView.findViewById(R.id.chatLastMessage);

            this.onChatListener=onChatListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onChatListener.onChatClick(getAdapterPosition());
        }
    }

    //Use this interface to detect click on chat
    public interface OnChatListener{
        //Use this method in activity to send position of the clicked item
        void onChatClick(int position);
    }
}
