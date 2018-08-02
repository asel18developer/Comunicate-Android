package com.example.leonn.comunicate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jesusgarcia on 2/5/17.
 */

public class ChatsAdapter extends AbstractAdapter<Chat> {

    public ChatsAdapter(Context context, List<Chat> itemsPasados) {

        /* Constructor de la clase padre */
        super(context, itemsPasados);
        this.myItems = itemsPasados;

    }


    @Override
    public View getView(int posicion, View convertView, ViewGroup parent) {

        Chat chatClicked = myItems.get(posicion);

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_chat, parent, false);

        }


        // Lookup view for data population
        TextView username = (TextView) convertView.findViewById(R.id.item_chat_username);
        TextView last_message = (TextView) convertView.findViewById(R.id.item_chat_lastMessage);

        Boolean inLine = chatClicked.getInLine();
        if (inLine){
            //state.setText("En linea")
            username.setText(chatClicked.getExpertUser()+": En linea");
        }else{
            username.setText(chatClicked.getExpertUser()+": No conectado");
        }
        //username.setText(chatClicked.getExpertUser()+":");
        last_message.setText(chatClicked.getLastMessage());


        return convertView;
    }
}
