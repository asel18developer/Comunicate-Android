package com.example.leonn.comunicate;

/**
 * Created by jesusgarcia on 2/5/17.
 */


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by jesusgarcia on 2/1/16.
 */
public abstract class AbstractAdapter<T> extends ArrayAdapter<T> {


    /* Generic item list */
    protected List<T> myItems;

    public AbstractAdapter(Context context, List<T> itemsPasados) {

        super(context, 0, itemsPasados);
        this.myItems = itemsPasados;

    }


    public abstract View getView(int posicion, View convertView, ViewGroup parent);
}