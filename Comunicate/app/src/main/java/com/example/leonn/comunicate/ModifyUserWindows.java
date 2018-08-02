package com.example.leonn.comunicate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ModifyUserWindows extends AbstractActivity {

    private Socket socket;
    private EditText editText_username, editText_pass, editText_email, editText_passTrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_windows);
        initialConfiguration();
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(ModifyUserWindows.this, UserWindow.class);
        startActivity(i);
        finish();

    }

    public void modifyUser(View view) {

        JSONObject credentials = SharedResources.createJSON(
                "userID",
                String.valueOf(SocketSingleton.getUser().getUserID()),
                "username",
                editText_username.getText().toString(),
                "pass",
                editText_pass.getText().toString(),
                "email",
                editText_email.getText().toString(),
                "passTrap",
                editText_passTrap.getText().toString()
        );


        socket.emit("modify_user", credentials);

    }

    @Override
    public void initialConfiguration() {

        editText_username = (EditText) findViewById(R.id.modify_userName);
        editText_pass = (EditText) findViewById(R.id.modify_pass);
        editText_email = (EditText) findViewById(R.id.modify_email);
        editText_passTrap = (EditText) findViewById(R.id.modify_passTrap);

        editText_username.setText(SocketSingleton.getUser().getUsername());
        editText_email.setText(SocketSingleton.getUser().getEmail());

        configureSocket();
    }

    private void openUserWindow(){

        Intent i = new Intent(ModifyUserWindows.this, UserWindow.class);
        startActivity(i);
        finish();
    }
    private Emitter.Listener userModified = new Emitter.Listener(){
        @Override
        public void call(final Object... args){

            JSONObject returnJSON = (JSONObject) args[0];
            try {
                Log.i("Respuesta registro", returnJSON.toString());

                if (returnJSON.getJSONObject("status").getString("message").toString().equals("User created")){

                    /*Guardo la nueva información*/
                    SocketSingleton.getUser().setEmail(editText_email.getText().toString());
                    SocketSingleton.getUser().setUsername(editText_username.getText().toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                           openUserWindow();

                        }

                    });

                }else if(returnJSON.getJSONObject("status").getString("message").toString().equals("This username already exist")){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            SharedResources.showToast(ModifyUserWindows.this,"Nombre de usuario existente");

                        }

                    });

                }else{



                }

            } catch (JSONException e) {
                Log.e("Error", "error");
                return;
            }
        }
    };
    @Override
    public void configureSocket() {

        socket = SocketSingleton.getSocket();
        socket.on("user_modified", userModified);

    }




}
