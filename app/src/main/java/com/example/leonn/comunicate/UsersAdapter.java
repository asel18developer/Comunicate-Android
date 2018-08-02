package com.example.leonn.comunicate;

import android.content.Context;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jesusgarcia on 2/5/17.
 */

public class UsersAdapter extends AbstractAdapter<User> {

    public UsersAdapter(Context context, List<User> itemsPasados) {

        /* Constructor de la clase padre */
        super(context, itemsPasados);
        this.myItems = itemsPasados;

    }


    @Override
    public View getView(int posicion, View convertView, ViewGroup parent) {

        User userClicked = myItems.get(posicion);

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);

        }


        // Lookup view for data population
        TextView username = (TextView) convertView.findViewById(R.id.user_username);
        TextView state = (TextView) convertView.findViewById(R.id.user_state);
        RatingBar rating = (RatingBar) convertView.findViewById(R.id.ratingBar);
        rating.setRating(userClicked.getRating());

        username.setText("Usario: "+userClicked.getUsername());

        if(userClicked.getInLine()){
            state.setText("En linea");
        }else{
            state.setText("No conectado");
        }


        return convertView;
    }
}
