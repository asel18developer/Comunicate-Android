package com.example.leonn.comunicate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatWindow extends AbstractActivity{


    private Socket socket;
    private ArrayList<Message> messages = new ArrayList<Message>();
    private Message sendMessgage;
    private String chatID;
    private String userExpert;
    private String usernameExpert;
    private Boolean state;
    private RecyclerView mRecyclerView;
    private RatingBar ratingBar;
    private TextView textViewUsername;
    private TextView textViewState;
    private EditText mEditTextMessage;
    private ChatMessageAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initialConfiguration();

    }


    @Override
    public void initialConfiguration() {

        chatID = String.valueOf(getIntent().getIntExtra("chatID",0));
        userExpert = String.valueOf(getIntent().getIntExtra("userExpert",0));
        usernameExpert = getIntent().getStringExtra("usernameExpert");

        ratingBar = (RatingBar) findViewById(R.id.ratingBarChat);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        textViewUsername = (TextView) findViewById(R.id.chat_window_username);
        textViewState = (TextView) findViewById(R.id.chat_window_state);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChatMessageAdapter(this, new ArrayList<Message>());
        mRecyclerView.setAdapter(mAdapter);
        textViewUsername.setText(usernameExpert);
        configureSocket();
    }

    /**
     *
     * Metodo para crear un menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteChat:
                deleteChat();
                return true;

            case R.id.deleteChat_icon:
                deleteChat();
                return true;
            case R.id.addRating:
                addRating();
                return true;

            case R.id.addRating_icon:
                addRating();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void addRating(){

        // Creo una alerta y le asigno el titulo y el mensaje que se ha de mostrar
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.rating_dialog, null);
        alert.setView(view);


        //Le asigno un boton para enviar y su correspondiente accion
        alert.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                RatingBar ratingBar = (RatingBar) view.findViewById(R.id.add_rating);
                int valueRating = (int) ratingBar.getRating();

                JSONObject credentials = SharedResources.createJSON(
                        "userID",
                        userExpert,
                        "value",
                        String.valueOf(valueRating)
                );
                socket.emit("set_rating", credentials);

            }
        });


        //Añado el boton de cancelar con su correspondiente acción
        alert.setNegativeButton("Cerrar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        //Cierro el dialogo, mediante dismiss
                        dialog.dismiss();

                    }
                });


        // Una vez construido el dialogo con sus correspondientes acciones lo muestro
        alert.show();
    }
    private void deleteChat() {
        /* Cargamos los mensajes de dicho chat, para ello se los pedimos a la base de datos */
        JSONObject credentials = SharedResources.createJSON(
                "chatID",
                chatID
        );

        socket.emit("delete_chat", credentials);
    }

    private void paintMessage(String message,int userID, Boolean isMine) {

        Message chatMessage = new Message(userID, message, isMine);
        mAdapter.add(chatMessage);

        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

        mEditTextMessage.setText("");

    }
    private void paintSendMessage(){


        mAdapter.add(sendMessgage);

        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);


    }
    private void fillChats(){

        if (state){
            textViewState.setText("En linea");
        }else{
            textViewState.setText("No conectado");
        }

        for (int i = 0; i < messages.size() ; i++) {

            mAdapter.add(messages.get(i));

            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);


        }
    }

    @Override
    public void configureSocket() {

        socket = SocketSingleton.getSocket();
        socket.on("obtain_chat", fillMessages);
        socket.on("receive_message", receiveMessage);
        socket.on("obtain_rating", obtainedRating);
        socket.on("deleted_chat", closeChat);
        socket.on("rating_saved", updateRating);

        /* Cargamos los mensajes de dicho chat, para ello se los pedimos a la base de datos */
        JSONObject credentials = SharedResources.createJSON(
                "chatID",
               chatID,
                "userID",
                String.valueOf(SocketSingleton.getUser().getUserID())
        );

        socket.emit("get_chat", credentials);

        credentials = SharedResources.createJSON(
                "userID",
                userExpert
        );
        socket.emit("get_rating", credentials);
    }



    private Emitter.Listener updateRating = new Emitter.Listener(){

        @Override
        public void call(final Object... args){//meter en la interfaz el mensaje recivido

            JSONObject credentials = SharedResources.createJSON(
                    "userID",
                    userExpert
            );
            socket.emit("get_rating", credentials);




        }
    };
    private Emitter.Listener closeChat = new Emitter.Listener(){

        @Override
        public void call(final Object... args){//meter en la interfaz el mensaje recivido

            JSONObject returnJSON = (JSONObject) args[0];

            try {



                if (returnJSON.getJSONObject("status").getString("message").equals("Chat deleted")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Intent i = new Intent(ChatWindow.this, UserWindow.class);
                            startActivity(i);
                            finish();

                        }

                    });
                }





            } catch (JSONException e) {
                e.printStackTrace();
            }




        }
    };

    private Emitter.Listener obtainedRating = new Emitter.Listener(){

        @Override
        public void call(final Object... args){//meter en la interfaz el mensaje recivido

            JSONObject returnJSON = (JSONObject) args[0];

            try {

                int average = 0;

                if (returnJSON.getJSONObject("status").getString("message").equals("Rating average")){
                    average = returnJSON.getJSONObject("data").getInt("averageRating");
                }

                final int finalAverage = average;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ratingBar.setRating(finalAverage);

                    }

                });

            } catch (JSONException e) {
                e.printStackTrace();
            }




        }
    };

    private Emitter.Listener receiveMessage = new Emitter.Listener(){

        @Override
        public void call(final Object... args){//meter en la interfaz el mensaje recivido

            JSONObject returnJSON = (JSONObject) args[0];

            try {

                String message =  returnJSON.getString("message");
                int userID = returnJSON.getInt("userID");

                sendMessgage = new Message(userID, message,false);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    paintSendMessage();

                }

            });


        }
    };

    private Emitter.Listener fillMessages = new Emitter.Listener(){

        @Override
        public void call(final Object... args){//meter en la interfaz el mensaje recivido

            JSONObject returnJSON = (JSONObject) args[0];

            try {

                if (returnJSON.getJSONObject("status").getString("code").toString().equals("200")){

                    try {

                        state = returnJSON.getJSONObject("data").getBoolean("inLine");

                        if (!returnJSON.getJSONObject("status").getString("message").equals("Chat empty")){

                        JSONArray listMessages = (JSONArray) returnJSON.getJSONObject("data").getJSONArray("listMessages");

                        for (int i = 0; i < listMessages.length(); i++) {

                            JSONObject message = listMessages.getJSONObject(i);
                            if (message.getInt("userID") == SocketSingleton.getUser().getUserID()) {
                                messages.add(new Message(message.getInt("userID"),message.getString("message"),true, message.getString("sendTimestamp")));
                            }else{
                                messages.add(new Message(message.getInt("userID"),message.getString("message"),false, message.getString("sendTimestamp")));
                            }


                        }



                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                fillChats();

                            }

                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }else{


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    public void sendMessage(View view) { // enviar y meter en la interfaz el mensaje enviado

        JSONObject credentials = SharedResources.createJSON(
                "userID",
                String.valueOf(SocketSingleton.getUser().getUserID()),
                "accessToken",
                SocketSingleton.getUser().getAccessToken(),
                "message",
                mEditTextMessage.getText().toString(),
                "chatID",
                chatID,
                "chatUserID",
                userExpert
        );


        socket.emit("send_message", credentials);

        String message = mEditTextMessage.getText().toString();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        paintMessage(message,SocketSingleton.getUser().getUserID(), true);

    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(ChatWindow.this, UserWindow.class);
        startActivity(i);
        finish();
    }



    /*// from the link above
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks whether a hardware keyboard is available
        if (newConfig.keyboardHidden  == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Log.e("TECLADO","CERRADO");
        } else if (newConfig.keyboardHidden  == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Log.e("TECLADO","aBIERTO");
        }
    }*/
}
