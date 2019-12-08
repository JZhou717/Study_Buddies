package com.study.bindr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import model.Student;


//This adapter follows the view holder design pattern, which means that it allows you to define a custom class that extends RecyclerView.ViewHolder.
public class MatchedStudentAdapter extends RecyclerView.Adapter<MatchedStudentAdapter.ViewHolder> implements Filterable {

    private List<Student> studentsList = new ArrayList<>();
    private List<Student> studentsListFull=new ArrayList<>();
    private List<String> fullNamesListFull=new ArrayList<>();
    private List<String> fullNamesList=new ArrayList<>();
    //The interface (implemented in activity) will be passed to each individual view so will know where to go to when a match icon is clicked
    private OnMatchIconListener onMatchIconListener;

    public MatchedStudentAdapter(List<Student> studentsList, List<String> fullNamesList , Context context, OnMatchIconListener onMatchIconListener) {
        this.studentsList = studentsList;
        this.studentsListFull=new ArrayList<>(this.studentsList);
        this.context = context;
        this.onMatchIconListener=onMatchIconListener;
        this.fullNamesList=fullNamesList;
        this.fullNamesListFull=new ArrayList<>(this.fullNamesList);
        System.out.println("Names List, "+fullNamesList);
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
        System.out.println("POSTION "+position );
        holder.name.setText(fullNamesList.get(position));
        /*Student student=studentsList.get(position);
        student.getFullName(new DatabaseCallBack<String>() {
            @Override
            public void onCallback(String name) {
                holder.name.setText(name);

            }
        });
*/

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

    @Override
    public Filter getFilter() {
        return nameFilter;
    }
    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Student> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(studentsListFull);
                fullNamesList.addAll(fullNamesListFull);
            } else {
                fullNamesList.clear();
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (int i=0; i<studentsListFull.size(); i++){
                    Student student=studentsListFull.get(i);
                    String fullName=fullNamesListFull.get(i);
                    if (fullName.toLowerCase().contains(filterPattern)) {
                        System.out.println("INDEX "+i+" ID "+student.getId()+" NAME "+fullName);
                        filteredList.add(student);
                        fullNamesList.add(fullName);
                    }
                }

            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            studentsList.clear();
            studentsList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
            System.out.println(fullNamesList);
        }
    };

}