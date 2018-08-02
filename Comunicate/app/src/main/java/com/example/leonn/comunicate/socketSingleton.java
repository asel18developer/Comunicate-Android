package com.example.leonn.comunicate;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by jesusgarcia and cesar gutierrez on 21/4/17.
 */

public class SocketSingleton {

    final static String URL_SOCKET = "http://comunicate.sytes.net";

    private static Socket socket = null;
    private static User logginUser;


    public static User getUser() {
        return logginUser;
    }


    public static void setUser(String username, int userID, String accessToken, String email) {

        logginUser = new User(username, userID, accessToken, email);

    }

    public static synchronized Socket getSocket() {

        if(socket == null) {
            try {
                socket = IO.socket(URL_SOCKET);
                Log.i("CONNECTED", "SUCCESS");
            } catch (URISyntaxException e) {
                Log.e("ERROR", "ERROR CONNECTING");
            }

            socket.connect();
            socket.on(Socket.EVENT_CONNECT, socketConnected);
            socket.on(Socket.EVENT_DISCONNECT, socketDisconnected);

        }
        return socket;
    }

    private static Emitter.Listener socketConnected = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Log.i("Server", "Connected");
        }
    };

    private static Emitter.Listener socketDisconnected = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            Log.w("Server", "Disconnected");
        }
    };


}
