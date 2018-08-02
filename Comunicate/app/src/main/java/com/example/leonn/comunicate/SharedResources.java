package com.example.leonn.comunicate;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jesusgarcia on 11/5/17.
 */

public class SharedResources {

    /*
     * Funcion para crear un JSON segun los parametros pasados.
     *      Esta funcion siempre recibe pares de parametros de la siguiente forma
     *      nombre del campo -> userID
     *      valor del campo -> 5
     *      ....
     * El JSON devuelto tiene por formato: {"userID":"5", .... }
     */
    public static JSONObject createJSON(Object... args) {

        JSONObject returnJSON = new JSONObject();

        try {

            for (int i = 0; i < args.length; i+=2) {

                returnJSON.put((String) args[i], (String) args[i+1]);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return returnJSON;

    }

    /*
     * Funcion usada para mostrar una notificación en pantalla con el mensaje pasado
     * por parametro.
     */
    public static void showToast(Activity activity, String message) {

       Toast.makeText(activity, message, Toast.LENGTH_LONG).show();


    }
}
