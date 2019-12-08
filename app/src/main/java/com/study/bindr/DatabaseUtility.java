package com.study.bindr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import model.Student;

public class DatabaseUtility {
    //Returns full names given a list of students ids, in the same order as the given students list
    public static void getFullNameList(DatabaseCallBack<List<String>> dbCallBack, List<String> studentIDs){
//Query by id
        ArrayList<ObjectId> ids=new ArrayList<>();
        for (int i=0; i<studentIDs.size();i++){
            ids.add(new ObjectId(studentIDs.get(i)));
        }
        Document filterDoc = new Document()
                .append("_id", new Document().append("$in", ids));

        RemoteFindIterable findResults = BindrController.studentsCollection
                .find(filterDoc)
                .projection(new Document().append("_id", 1).append("full_name",1));

        Task<List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    Log.d("getFullNameList", String.format("successfully found %d documents", items.size()));
                    ArrayList<Document> studentDocs=new ArrayList<>();
                    for (Document item: items) {
                        Log.d("getFullNameList", String.format("successfully found:  %s", item.toString()));
                        studentDocs.add(item);
                    }
                    ArrayList<String> fullNames=new ArrayList<>();
                    //Return full name in correct order
                    for (int i=0 ;i<studentIDs.size();i++){
                        for (int j=0; j<studentDocs.size(); j++){
                            String id=studentDocs.get(j).get("_id").toString();
                            if(studentIDs.get(i).equals(id)){
                                String fullName=studentDocs.get(j).getString("full_name");
                                fullNames.add(fullName);
                                break;
                            }
                        }
                    }
                    dbCallBack.onCallback(fullNames);

                } else {
                    Log.e("getFullNameList", "failed to find documents with: ", task.getException());
                }
            }
        });
    }
}
