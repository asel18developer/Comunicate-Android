package com.example.leonn.comunicate;

import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class UserWindow extends AbstractActivity implements AdapterView.OnItemClickListener {

    private static final String USERNAME = "Nombre de usuario: ";
    private ListView listViewChats;
    private TextView emptyChats;
    private Socket socket;
    private ArrayList<Chat> chats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        initialConfiguration();

    }

    public void openUserWindows(View view) {



        Intent i = new Intent(UserWindow.this, UserListWindows.class);

        startActivity(i);
        finish();



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

        getMenuInflater().inflate(R.menu.menu_user, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editProfile:
                openEditProfile();
                return true;

            case R.id.editProfile_icon:
                openEditProfile();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void openEditProfile() {
        Intent i = new Intent(UserWindow.this, ModifyUserWindows.class);
        startActivity(i);
        finish();
    }


    private Emitter.Listener reloadMessages = new Emitter.Listener(){

        @Override
        public void call(final Object... args){

          /* Cargamos los chats abiertos, para ello se los pedimos a la base de datos */
            JSONObject credentials = SharedResources.createJSON(
                    "userID",
                    String.valueOf(SocketSingleton.getUser().getUserID()),
                    "username",
                    String.valueOf(SocketSingleton.getUser().getUsername()),
                    "accessToken",
                    String.valueOf(SocketSingleton.getUser().getAccessToken()),
                    "isExpert",
                    "0"
            );

            socket.emit("get_user_last_messages", credentials);

        }
    };
    private Emitter.Listener obtainMessages = new Emitter.Listener(){

        @Override
        public void call(final Object... args){
            JSONObject returnJSON = (JSONObject) args[0];
            //Log.e("RELOAD",returnJSON.toString());
            try {
                if (returnJSON.getJSONObject("status").getString("code").toString().equals("200")){

                    try {

                        JSONArray listMessages = (JSONArray) returnJSON.getJSONObject("data").getJSONArray("listMessages");

                        for (int i = 0; i < listMessages.length(); i++) {

                            JSONObject message = listMessages.getJSONObject(i);
                            chats.add(new Chat(
                                    message.getString("chatUsername"),
                                    SocketSingleton.getUser().getUsername(),
                                    message.getString("message"),
                                    message.getInt("chatID"),
                                    message.getInt("chatUserID"),
                                    message.getBoolean("inLine")
                            ));

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

                    Log.i("Respuesta", "Chats cargados correctamente");


                }else{


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void fillChats(){

        ChatsAdapter adapter = new ChatsAdapter(this, chats);
        listViewChats.setAdapter(adapter);


        if (chats.size() == 0){
            emptyChats.setVisibility(View.VISIBLE);
        }else{
            emptyChats.setVisibility(View.INVISIBLE);
        }
        chats = new ArrayList<Chat>();



    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(UserWindow.this, LoginWindow.class);
        startActivity(i);
        finish();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int chatID = ((Chat) parent.getItemAtPosition(position)).getChatID();
        int userExpert = ((Chat) parent.getItemAtPosition(position)).getExpertUserID();
        String usernameExpert = ((Chat) parent.getItemAtPosition(position)).getExpertUser();
        Intent i = new Intent(UserWindow.this, ChatWindow.class);
        i.putExtra("chatID", chatID);
        i.putExtra("userExpert", userExpert);
        i.putExtra("usernameExpert", usernameExpert);
        startActivity(i);
        finish();

    }

    @Override
    public void initialConfiguration() {

        listViewChats = (ListView) findViewById(R.id.chatsList);
        listViewChats.setOnItemClickListener(this);
        emptyChats = (TextView)findViewById(R.id.emptyChats);

        configureSocket();

    }



    @Override
    public void configureSocket() {

        socket = SocketSingleton.getSocket();
        socket.on("obtain_messages", obtainMessages);
        socket.on("receive_message", reloadMessages);

        /* Cargamos los chats abiertos, para ello se los pedimos a la base de datos */
        JSONObject credentials = SharedResources.createJSON(
                "userID",
                String.valueOf(SocketSingleton.getUser().getUserID()),
                "username",
                String.valueOf(SocketSingleton.getUser().getUsername()),
                "accessToken",
                String.valueOf(SocketSingleton.getUser().getAccessToken()),
                "isExpert",
                "0"
        );


        socket.emit("get_user_last_messages", credentials);

    }
}
