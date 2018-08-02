package com.example.leonn.comunicate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginWindow extends AbstractActivity{

    private EditText editText_username, editText_pass;
    private Socket socket;
    private String usernameRecovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialConfiguration();

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

        getMenuInflater().inflate(R.menu.menu_loggin, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logginHelp_icon:
                // User chose the "Settings" item, show the app settings UI...
                developers();
                return true;

            case R.id.logginHelp:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                developers();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void developers() {

        // Creo una alerta y le asigno el titulo y el mensaje que se ha de mostrar
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Información comunicate");
        alert.setMessage("Aplicación desarrollada por Jesús García Potes y Cesar Guitierrez Pérez");

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


    /*
     * Funcion que se activa cuando el socket recibe la señal send_data_user (Cuando obtiene la respuesta)
     * Se encarga de determinar si el usuario introducido es correcto o no:
     *      Correcto: Se crea el usuario y se abre una nueva ventana.
     *      Incorrecto: Muestra un mensaje de error.
     */
    private Emitter.Listener getUser = new Emitter.Listener(){
        @Override
        public void call(final Object... args){

            JSONObject returnJSON = (JSONObject) args[0];
            try {

                Log.i("Respuesta", returnJSON.toString());

                if (returnJSON.getJSONObject("data").getString("isValidUser").toString().equals("1")){

                    /* Asignacion de valores al usuario del socket */
                    String accessToken = returnJSON.getJSONObject("data").getString("token").toString();
                    String username = returnJSON.getJSONObject("data").getString("userName").toString();
                    int userID = Integer.parseInt(returnJSON.getJSONObject("data").getString("userID").toString());
                    String email = returnJSON.getJSONObject("data").getString("email").toString();
                    SocketSingleton.setUser(username, userID, accessToken, email);

                    if (returnJSON.getJSONObject("data").getBoolean("passTrap")==true){

                        openUserWindow();

                    }else{

                        openPassTrapWindow();
                    }


                }else if(returnJSON.getJSONObject("data").getString("isPassTrap").toString().equals("1")){

                    openFalseWindow();

                }else{

                    wrongUser();

                }

            } catch (JSONException e) {
                Log.e("Error", "error");
                return;
            }
        }
    };

    /*
     * Funcion que se activa cuando el socket recibe la señal send_data_user (Cuando obtiene la respuesta)
     * Se encarga de determinar si el usuario introducido es correcto o no:
     *      Correcto: Se crea el usuario y se abre una nueva ventana.
     *      Incorrecto: Muestra un mensaje de error.
     */
    private Emitter.Listener passChanged = new Emitter.Listener(){
        @Override
        public void call(final Object... args){

            JSONObject returnJSON = (JSONObject) args[0];
            try {

                final String mensaje;

                if (returnJSON.getJSONObject("status").getString("code").toString().equals("200")){


                    mensaje = "Correo de recuperación enviado";

                }else if(returnJSON.getJSONObject("status").getString("message").toString().equals("This username dont exist")){

                    mensaje = "Nombre de usuario incorrecto";

                }else{

                    mensaje = "Debes introducir un nombre de usuario";

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedResources.showToast(LoginWindow.this, mensaje);
                    }

                });


            } catch (JSONException e) {
                Log.e("Error", "error");
                return;
            }
        }
    };

    /*
     * Funcion empleada para mostrar por pantalla un mensaje de error al introducir un usuario
     * incorrecto.
     */
    private void wrongUser() {

        /* Para dibujar en la interfaz principal desde un hilo secundario */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               SharedResources.showToast(LoginWindow.this, "Usuario/contraseña incorrecto");
            }

        });
    }


    /*
     * Funcion que abre la ventana principal del usuario que se ha logeado.
     */
    private void openUserWindow() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(LoginWindow.this, UserWindow.class);
                startActivity(i);
                finish();
            }

        });

    }

    /*
 * Funcion que abre la ventana principal del usuario que se ha logeado.
 */
    private void openFalseWindow() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(LoginWindow.this, AppFalse.class);
                startActivity(i);
                finish();
            }

        });

    }

    private void openPassTrapWindow() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(LoginWindow.this, PassTrapWindow.class);
                startActivity(i);
                finish();
            }

        });

    }


    /**
     * Funcion utilizada para abrir la ventana de registro ante el evento onclick
     */
    public void openRegistry(View view) {

        Intent i = new Intent(LoginWindow.this, RegisterWindow.class);
        startActivity(i);
        finish();

    }

    /**
     * Metodo utilizado recuperar la contraseña ante el evento onclick
     */
    public void recoverPass(View view) {

        // Creo una alerta y le asigno el titulo y el mensaje que se ha de mostrar
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Recuperación de contraseña");
        alert.setMessage("Introduce el nombre de usuario");

        // Creo un edit text que le asigno a la alerta
        final EditText entradaTexto = new EditText(this);
        alert.setView(entradaTexto);

        //Le asigno un boton para enviar y su correspondiente accion
        alert.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //Capturo el valor introducido por teclado y se lo asigno
                //a mi variable
                String cadenaIntroducida = entradaTexto.getText().toString();
                usernameRecovery = cadenaIntroducida;

                JSONObject credentials = SharedResources.createJSON(
                        "username",
                        usernameRecovery
                );
                socket.emit("recovery_pass", credentials);


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

    /*
     * Funcion que captura el click del boton inicio de sesion, esta funcion recoge los datos
     * de la interfaz y se los envia a node.
     */
    public void loggin(View view) {

        String username = editText_username.getText().toString();
        String pass = editText_pass.getText().toString();

        if (username.equals("") || pass.equals("")){

            SharedResources.showToast(LoginWindow.this, "Debe rellenar todos los campos");

        }else {
            JSONObject credentials = SharedResources.createJSON(
                    "username",
                    username,
                    "pass",
                    pass
            );

            socket.emit("check_user", credentials);

        }
        /*String message = String.valueOf(socket.connected());
        Log.i("SOCKET_CONNECTION",message);*/


    }

    /**
     * Metodo para capturar la pulsacion de salir de ventana y cambiar su comportmiento
     */
    @Override
    public void onBackPressed() {

        //SocketSingleton.getSocket().emit("close_session", SocketSingleton.getUser().getUserID());
        finish();
        System.exit(0);
    }

    /*
     *
     * Metodo usado para capturar los casos en los que esta pantalla entra en estado pausada
     * en concreto lo utilizado para los casos en los que salgo de la aplicacion (Al pulsar
     * el boton de home o el de multitarea)
     *
     * En estos casos cierro la ventana de juego para que no se pueda buscar en internet la
     * la pregunta que se esta jugando
     *
     */
    @Override
    protected void onPause() {

        //SocketSingleton.getSocket().emit("close_session", SocketSingleton.getAccessToken());
        super.onPause();
        finish();

    }



    @Override
    public void initialConfiguration() {

        editText_username = (EditText) findViewById(R.id.VLoggin_username);
        editText_pass = (EditText) findViewById(R.id.VLoggin_pass);
        configureSocket();

    }

    @Override
    public void configureSocket() {
        /* Obtengo el socket y le pongo a escuchar la señal send_data_user*/
        socket = SocketSingleton.getSocket();
        socket.on("send_data_user", getUser);
        socket.on("pass_changed", passChanged);
    }
}
