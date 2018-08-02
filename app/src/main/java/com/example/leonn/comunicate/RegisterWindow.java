package com.example.leonn.comunicate;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RegisterWindow extends AbstractActivity {

    private Socket socket;
    private EditText editText_username, editText_pass, editText_pass_repeat, editText_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialConfiguration();
    }

    /*
     * Funcion que captura el click del boton registro, esta funcion recoge los datos
     * de la interfaz y se los envia a node.
     */
    public void register(View view) {

        String username, pass, passRepeat, email;

        username =  editText_username.getText().toString();
        pass = editText_pass.getText().toString();
        passRepeat = editText_pass_repeat.getText().toString();
        email = editText_email.getText().toString();
        if (username.equals("") || pass.equals("") || passRepeat.equals("") || email.equals("")){

            SharedResources.showToast(RegisterWindow.this, "Debe rellenar todos los campos");

        }else if( pass.length()< 6 || passRepeat.length() < 6){

            SharedResources.showToast(RegisterWindow.this, "La contraseña debe tener 6 caracteres mínimo");

        } else if(!pass.equals(passRepeat)){

            SharedResources.showToast(RegisterWindow.this, "No coinciden las contraseñas");

        }else{

            JSONObject credentials = SharedResources.createJSON(
                    "username",
                    username,
                    "pass",
                    pass,
                    "email",
                    email
            );

            socket.emit("add_user", credentials);
        }


    }

    /*public void trapPass(View view) {

        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setMessage("La contraseña trampa es la contraseña para introducir en caso de sentir golpes en a cabeza");
        dialogo.setPositiveButton(android.R.string.ok, null);
        dialogo.show();

    }*/

    /*
     * Funcion que se activa cuando el socket recibe la señal register_user
     * Se encarga de determinar si registro es correcto o no:
     *      Correcto: Se vuelve a la ventana del loggin.
     *      Incorrecto: Muestra un mensaje de error.
     */
    private Emitter.Listener registerUser = new Emitter.Listener(){
        @Override
        public void call(final Object... args){

            JSONObject returnJSON = (JSONObject) args[0];
            try {
                Log.i("Respuesta registro", returnJSON.toString());

                if (returnJSON.getJSONObject("status").getString("message").toString().equals("User created")){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Intent i = new Intent(RegisterWindow.this, LoginWindow.class);
                            startActivity(i);
                            finish();
                        }

                    });

                }else{
                    //Aqui mostramos el mensaje al usuario de que esta mnal el usuario
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            SharedResources.showToast(RegisterWindow.this,"Nombre de usuario existente");
                        }

                    });


                }

            } catch (JSONException e) {
                Log.e("Error", "error");
                return;
            }
        }
    };


    @Override
    public void onBackPressed() {

        Intent i = new Intent(RegisterWindow.this, LoginWindow.class);
        startActivity(i);
        finish();
    }



    @Override
    public void initialConfiguration() {

        editText_username = (EditText) findViewById(R.id.VRegister_username);
        editText_pass = (EditText) findViewById(R.id.VRegister_pass);
        editText_pass_repeat = (EditText) findViewById(R.id.VRegister_pass_repit);
        editText_email = (EditText) findViewById(R.id.VRegister_email);

        configureSocket();
    }

    @Override
    public void configureSocket() {

        socket = SocketSingleton.getSocket();
        socket.on("register_user", registerUser);

    }

}
