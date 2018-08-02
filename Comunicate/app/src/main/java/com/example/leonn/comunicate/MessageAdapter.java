package com.example.leonn.comunicate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by leonn on 24/05/2017.
 */

public class MessageAdapter extends AbstractAdapter<Message>{

    public MessageAdapter(Context context, List<Message> itemsPasados) {

        /* Constructor de la clase padre */
        super(context, itemsPasados);
        this.myItems = itemsPasados;

    }
    @Override
    public View getView(int posicion, View convertView, ViewGroup parent) {

        Message messageClicked = myItems.get(posicion);

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false);

        }

        // Lookup view for data population
        TextView username = (TextView) convertView.findViewById(R.id.message_username);
        TextView message = (TextView) convertView.findViewById(R.id.message_text);

        username.setText(messageClicked.getUser());
        message.setText(messageClicked.getMessage());


        return convertView;
    }
}
