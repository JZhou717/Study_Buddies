package com.study.bindr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
    private Context context;
    //Keep track of full list for filtering
    private List<Student> studentsList = new ArrayList<>();
    private List<Student> studentsListFull=new ArrayList<>();

    private List<String> fullNamesListFull=new ArrayList<>();
    private List<String> fullNamesList=new ArrayList<>();
    private OnMatchIconListener onMatchIconListener;

    /**
     *
     * @param studentsList list of Student Objects
     * @param fullNamesList list of the student's full names
     * @param context context of this adapter
     * @param onMatchIconListener The interface (implemented in activity) that will be passed to each individual view so will know where to go to when a student is clicked
     */
    public MatchedStudentAdapter(List<Student> studentsList, List<String> fullNamesList , Context context, OnMatchIconListener onMatchIconListener) {
        this.studentsList = studentsList;
        this.studentsListFull=new ArrayList<>(this.studentsList);
        this.context = context;
        this.onMatchIconListener=onMatchIconListener;
        this.fullNamesList=fullNamesList;
        this.fullNamesListFull=new ArrayList<>(this.fullNamesList);
    }


    /**
     * Called whenever a viewholder is created.
     * Inflates viewholder with matched icon layout
     * @param parent
     * @param viewType
     * @return new viewholder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.matched_icon, parent, false);
        return new ViewHolder(view,onMatchIconListener);
    }

    /**
     * Called after viewholder is created. Binds data to the viewholder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(fullNamesList.get(position));
        studentsList.get(position).getPicture(new DatabaseCallBack<String>() {
            @Override
            public void onCallback(String items) {
                byte[] decodedString = Base64.decode(items, Base64.DEFAULT);
                Bitmap decodedBytes = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.image.setImageBitmap(decodedBytes);
            }
        });
        /*Student student=studentsList.get(position);
        student.getFullName(new DatabaseCallBack<String>() {
            @Override
            public void onCallback(String name) {
                holder.name.setText(name);

            }
        });
        */
    }

    /**
     * Gets size of students list
     * @return size
     */
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

        /**
         * Constructor for viewholder
         * @param itemView
         * @param onMatchIconListener
         */
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
    /**
     * Interface to detect click on matched student
     */
    public interface OnMatchIconListener{
        //Use this method in activity to send position of the clicked item
        void OnMatchIconClick(int position);
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }
    //Gets a filtered list of students by inputted name
    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Student> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                fullNamesList.clear();
                filteredList.addAll(studentsListFull);
                fullNamesList.addAll(fullNamesListFull);
            } else {
                fullNamesList.clear();
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (int i=0; i<studentsListFull.size(); i++){
                    Student student=studentsListFull.get(i);
                    String fullName=fullNamesListFull.get(i);
                    if (fullName.toLowerCase().contains(filterPattern)) {
                        filteredList.add(student);
                        fullNamesList.add(fullName);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }
        /**
         * Displays the filtered results
         * @param constraint
         * @param results
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            studentsList.clear();
            studentsList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };


}