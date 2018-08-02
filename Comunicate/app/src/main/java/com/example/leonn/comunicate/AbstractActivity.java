package com.example.leonn.comunicate;

import android.support.v7.app.AppCompatActivity;

import io.socket.client.Socket;

/**
 * Created by jesusgarcia on 11/5/17.
 */

public abstract class AbstractActivity extends AppCompatActivity {

    protected Socket socket;

    /*
     * Funcion que realiza la configuracion inicial cuando se crea la ventana
     */
    public abstract void initialConfiguration();

    /*
     * Funcion para configurar el socket
     *   1) Obtenerlo
     *   2) Asignarle las señales que debe esuchar
     */
    public abstract void configureSocket();

}
