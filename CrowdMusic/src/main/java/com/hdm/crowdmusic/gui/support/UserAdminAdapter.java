package com.hdm.crowdmusic.gui.support;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.hdm.crowdmusic.R;
import com.hdm.crowdmusic.core.streaming.User;

import java.util.List;

public class UserAdminAdapter extends ArrayAdapter<User> {

    public UserAdminAdapter(Context context, int textViewResourceId, List<User> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if (v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_userlistentry, null);
        }

        User user = ((ArrayAdapter<User>) this).getItem(position);

        if (user != null){
            TextView userIp = (TextView) v.findViewById(R.id.userlist_item_IP);


            if(userIp != null){
                userIp.setText(user.getIp());

            }
        }
    return v;
    }
}


