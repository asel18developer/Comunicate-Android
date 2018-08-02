package com.example.leonn.comunicate;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class PassTrapWindow extends AbstractActivity {

    private Socket socket;
    private EditText passTrap;
    private EditText repeatPassTrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_trap_window);

        initialConfiguration();
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(PassTrapWindow.this, LoginWindow.class);
        startActivity(i);
        finish();
    }


    private void openUserWindow() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(PassTrapWindow.this, UserWindow.class);
                startActivity(i);
                finish();
            }

        });

    }


    @Override
    public void initialConfiguration() {

        passTrap = (EditText) findViewById(R.id.passTrapText);
        repeatPassTrap = (EditText) findViewById(R.id.repeatPassTrap);

        configureSocket();
    }

    @Override
    public void configureSocket() {

        socket = SocketSingleton.getSocket();
        socket.on("added_pass_trap", addedPassTrap);

    }

    public void savePassTrap(View view) {

        String pass, repeatPass;

        pass =  passTrap.getText().toString();
        repeatPass  =  repeatPassTrap.getText().toString();

        if(!pass.equals(repeatPass)){

            SharedResources.showToast(PassTrapWindow.this, "No coinciden las contraseñas");

        }else{

            JSONObject credentials = SharedResources.createJSON(
                    "userID",
                    String.valueOf(SocketSingleton.getUser().getUserID()),
                    "passFalse",
                    pass
            );

            socket.emit("add_pass_trap", credentials);

        }



    }

    public void alertPassTrap(View view) {

        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setMessage("La segunda contraseña es la contraseña para introducir en caso de no querer acceder a la verdadera funcionalidad de la aplicación");
        dialogo.setPositiveButton(android.R.string.ok, null);
        dialogo.show();

    }

    /*
 * Funcion que se activa cuando el socket recibe la señal register_user
 * Se encarga de determinar si registro es correcto o no:
 *      Correcto: Se vuelve a la ventana del loggin.
 *      Incorrecto: Muestra un mensaje de error.
 */
    private Emitter.Listener addedPassTrap = new Emitter.Listener(){
        @Override
        public void call(final Object... args){

            JSONObject returnJSON = (JSONObject) args[0];
            try {
                Log.i("Respuesta registro", returnJSON.toString());

                if (returnJSON.getJSONObject("status").getString("message").toString().equals("Update passTrap")){

                    openUserWindow();

                }else{
                    //Aqui mostramos el mensaje al usuario de que esta mnal el usuario
                    Log.e("Respuesta2", "Usuario mal registrado");

                }

            } catch (JSONException e) {
                Log.e("Error", "error");
                return;
            }
        }
    };

}

