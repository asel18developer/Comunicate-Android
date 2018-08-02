package com.example.leonn.comunicate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class UserListWindows extends AbstractActivity implements AdapterView.OnItemClickListener  {

    private Socket socket;
    private ListView userListView;
    private ArrayList<User> users = new ArrayList<>();
    private int chatID;
    private int userExpert;
    private String usernameExpert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_windows);
        initialConfiguration();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        int expertID = ((User) parent.getItemAtPosition(position)).getUserID();
        String expertName = ((User) parent.getItemAtPosition(position)).getUsername();
        int userID = SocketSingleton.getUser().getUserID();
        String username = SocketSingleton.getUser().getUsername();

        /* Cargamos los chats abiertos, para ello se los pedimos a la base de datos */
        JSONObject credentials = SharedResources.createJSON(
                "userID",
                String.valueOf(userID),
                "expertID",
                String.valueOf(expertID),
                "expertName",
                expertName,
                "username",
                username
        );

        socket.emit("create_chat", credentials);

    }

    public void openChatWindow(){

        Intent i = new Intent(UserListWindows.this, ChatWindow.class);
        i.putExtra("chatID", chatID);
        i.putExtra("userExpert", userExpert);
        i.putExtra("usernameExpert", usernameExpert);
        startActivity(i);
        finish();

    }
    @Override
    public void initialConfiguration() {

        userListView = (ListView) findViewById(R.id.userListView);
        userListView.setOnItemClickListener(this);
        configureSocket();

    }

    public void fillUsers(){

        UsersAdapter adapter = new UsersAdapter(this, users);
        userListView.setAdapter(adapter);
        users = new ArrayList<User>();

    }


    private Emitter.Listener obtainExperts = new Emitter.Listener(){

        @Override
        public void call(final Object... args){
            JSONObject returnJSON = (JSONObject) args[0];

            try {
                if (returnJSON.getJSONObject("status").getString("code").toString().equals("200")){

                    try {

                        JSONArray listExpert = (JSONArray) returnJSON.getJSONObject("data").getJSONArray("experts");

                        for (int i = 0; i < listExpert.length(); i++) {

                            JSONObject expert = listExpert.getJSONObject(i);
                            users.add(new User(expert.getString("username"),expert.getInt("userID"),null,null, expert.getBoolean("inLine"),expert.getInt("averageRating") ));


                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            fillUsers();

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

    private Emitter.Listener openChat = new Emitter.Listener(){

        @Override
        public void call(final Object... args){
            JSONObject returnJSON = (JSONObject) args[0];



            try {
                if (returnJSON.getJSONObject("status").getString("code").toString().equals("200")){

                    try {

                        chatID = returnJSON.getJSONObject("data").getInt("chatID");
                        userExpert = returnJSON.getJSONObject("data").getInt("userExpert");
                        usernameExpert = returnJSON.getJSONObject("data").getString("usernameExpert");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                openChatWindow();

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

    @Override
    public void onBackPressed() {

        Intent i = new Intent(UserListWindows.this, UserWindow.class);
        startActivity(i);
        finish();
    }


    @Override
    public void configureSocket() {

        socket = SocketSingleton.getSocket();
        socket.on("send_experts", obtainExperts);
        socket.on("send_chat_created", openChat);

        /* Cargamos los chats abiertos, para ello se los pedimos a la base de datos */
        JSONObject credentials = SharedResources.createJSON(
                "userID",
                String.valueOf(SocketSingleton.getUser().getUserID())
        );


        socket.emit("get_experts", credentials);

    }
}
