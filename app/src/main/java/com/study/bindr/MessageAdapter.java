package com.study.bindr;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Message;


/**
 * Displays messages to chatbox
 */
public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;
    String chattingStudentID;


    /**
     * Constructor
     * @param context
     * @param chattingStudentID Id of the chatting student
     */
    public MessageAdapter(Context context, String chattingStudentID) {
        this.context = context;
        this.chattingStudentID=chattingStudentID;
    }

    /**
     * Add Message to list
     * @param message message to be added
     */
    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    /**
     * gets size of the list
     * @return size
     */
    @Override
    public int getCount() {
        return messages.size();
    }

    /**
     *  Get the Messafe at index i
     * @param i index of Message
     * @return Message at index i
     */
    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Inflate the view into a chat bubble.
     * If the message is sent by other student, will display their avatar.
     * @param i
     * @param convertView
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        //other User sent message
        if (message.getSenderID().equals(chattingStudentID)) {
            convertView = messageInflater.inflate(R.layout.their_message, null);
            holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);

            holder.messageBody.setText(message.getText());
            GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
        } else {//current user sent message

            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        }
        return convertView;
    }

}

class MessageViewHolder {
    public View avatar;
    public TextView messageBody;
}