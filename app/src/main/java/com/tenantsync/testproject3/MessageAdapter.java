package com.tenantsync.testproject3;

import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dad on 9/24/2015.
 */
public class MessageAdapter extends BaseAdapter {
    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;
    //private List<Pair<String, Integer>> messages;
    private List<MessageContain> messages;
    private LayoutInflater layoutInflater;
    public MessageAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        //messages = new ArrayList<Pair<String, Integer>>();
        messages = new ArrayList<MessageContain>();
    }
    //public void addMessage(String message, int direction) {
    public void addMessage(MessageContain newMessage) {
        //messages.add(new Pair(message, direction));
        messages.add(newMessage);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return messages.size();
    }
    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public int getViewTypeCount() {
        return 2;
    }
    @Override
    public int getItemViewType(int i) {
        return messages.get(i).direction;
    }
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int direction = getItemViewType(i);
        //show message on left or right, depending on if
        //it's incoming or outgoing
        if (convertView == null) {
            int res = 0;
            if (direction == DIRECTION_INCOMING) {
                res = R.layout.message_right_two;
            } else if (direction == DIRECTION_OUTGOING) {
                res = R.layout.message_left_two;
            }
            convertView = layoutInflater.inflate(res, viewGroup, false);
        }
        //String message = messages.get(i).first;
        String message = messages.get(i).message;
        String newDate = messages.get(i).date;
        //This sets the text in the bubble
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        txtMessage.setText(message);
        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
        txtDate.setText(newDate);
        return convertView;
    }
}
