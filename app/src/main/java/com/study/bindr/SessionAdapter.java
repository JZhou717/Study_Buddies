package com.study.bindr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteFindOptions;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

import model.Course;
import model.Session;
import model.Student;

import static com.mongodb.client.model.Filters.eq;

public class SessionAdapter extends ArrayAdapter<Session> {


    private Context sessionContext;
    private List<Session> sessionList;
    public String tempNameHolder;

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
        name.setText("How was your session on " + c.getDateTime() + "?");
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.rating_dialog);
                RatingBar focusRating = dialog.findViewById(R.id.focusRating);
                RatingBar productivityRating = dialog.findViewById(R.id.productivityRating);
                RatingBar engagementRating = dialog.findViewById(R.id.engagementRating);
                RatingBar environmentRating = dialog.findViewById(R.id.environmentRating);
                Button submitButton = dialog.findViewById(R.id.submitButton);
                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Student student = new Student(c.getPartnerID());
                        student.addNewRating(focusRating.getRating(), productivityRating.getRating(), engagementRating.getRating(), environmentRating.getRating());
                        name.setText(c.getDateTime().toString());
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        return listItem;
    }

}
