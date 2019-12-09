package com.study.bindr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import model.Chat;


//This adapter follows the view holder design pattern, which means that it allows you to define a custom class that extends RecyclerView.ViewHolder.
public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> implements Filterable {
    private Context context;
    //Keep track of full list of filtered list

    private ArrayList<Chat> chatRoomsList = new ArrayList<>();
    private ArrayList<Chat> chatRoomsListFull = new ArrayList<>();

    private List<String> fullNamesListFull=new ArrayList<>();
    private List<String> fullNamesList=new ArrayList<>();

    private OnChatListener onChatListener;
    private String currentUserID;

    /**
     * Constructor
     * @param chatRoomsList list of Chat objects
     * @param fullNamesList list of the chatting student's full names
     * @param currentUserID current user's id
     * @param context context of this adapter
     * @param onChatListener The interface (implemented in activity) that will be passed to each individual view so will know where to go to when a chat is clicked
     */
    public ChatsAdapter(ArrayList<Chat> chatRoomsList, List<String> fullNamesList,String currentUserID, Context context, OnChatListener onChatListener) {
        this.chatRoomsList = chatRoomsList;
        this.chatRoomsListFull=new ArrayList<>(this.chatRoomsList);

        this.fullNamesList=fullNamesList;
        this.fullNamesListFull=new ArrayList<>(this.fullNamesList);

        this.context = context;

        this.onChatListener=onChatListener;
        this.currentUserID=currentUserID;
    }

    /**
     * Called whenever a viewholder is created.
     * Inflates viewholder with chat row layout
     * @param parent
     * @param viewType
     * @return new viewholder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);
        return new ViewHolder(view,onChatListener);
    }

    /**
     * Called after viewholder is created. Binds data to the viewholder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chatRoom=chatRoomsList.get(position);
        /*chatRoom.findChattingStudent(new DatabaseCallBack<Document>() {
            @Override
            public void onCallback(Document item) {
                String studentID=item.get("_id").toString();
                String full_name=item.getString("full_name");
                chatRoom.setChattingStudentID(studentID);
                chatRoom.setChattingStudentFullName(full_name);
                holder.name.setText(full_name);

            }
        }, currentUserID);*/
        chatRoom.getChattingStudent().getFullName(new DatabaseCallBack<String>() {
            @Override
            public void onCallback(String item) {
                chatRoom.getChattingStudent().getStatus(new DatabaseCallBack<Boolean>() {
                    @Override
                    public void onCallback(Boolean status) {
                        chatRoom.setChattingStudentFullName(item);
                        if(status){
                            holder.name.setText(item+" (Inactive)");
                        }else{
                            holder.name.setText(item);
                        }
                    }
                });
            }
        });
        chatRoom.findLastMessage(new DatabaseCallBack<Document>() {
            @Override
            public void onCallback(Document item) {
                String studentID=item.get("sender").toString();
                String text="";
                if (studentID.equals(currentUserID)){
                    text=text+"You: ";
                }
                text=text+item.getString("text");
                holder.lastMessage.setText(text);
            }
        });
    }

    /**
     * Gets size of chats list
     * @return size
     */
    @Override
    public int getItemCount() {
        return chatRoomsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Get the objects from xml
        ImageView image;
        TextView name;
        TextView lastMessage;
        //Each view holder will have a onChatListener interface
        OnChatListener onChatListener;

        /**
         * Constructor for viewholder
         * @param itemView
         * @param onChatListener
         */
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
    /**
     * Interface to detect click on chat
     */
    public interface OnChatListener{
        //Use this method in activity to send position of the clicked item
        void onChatClick(int position);
    }


    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    //Gets a filtered list of chats by inputted name
    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Chat> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                fullNamesList.clear();
                filteredList.addAll(chatRoomsListFull);
                fullNamesList.addAll(fullNamesListFull);
            } else {
                fullNamesList.clear();
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (int i=0; i<chatRoomsListFull.size(); i++){
                    Chat chat=chatRoomsListFull.get(i);
                    String fullName=fullNamesListFull.get(i);
                    if (fullName.toLowerCase().contains(filterPattern)) {
                        filteredList.add(chat);
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
            chatRoomsList.clear();
            chatRoomsList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

}