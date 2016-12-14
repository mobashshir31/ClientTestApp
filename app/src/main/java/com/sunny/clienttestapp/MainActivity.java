package com.sunny.clienttestapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText servAddress, servPort, messageText;
    private TextView responseText;
    private Button connectButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        servAddress = (EditText) findViewById(R.id.servAddress);
        servPort = (EditText) findViewById(R.id.servPort);
        messageText = (EditText) findViewById(R.id.messageText);
        responseText = (TextView) findViewById(R.id.responseText);
        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String servAdd = servAddress.getText().toString();
                String servPortNumber = servPort.getText().toString();
                String message = messageText.getText().toString();
                if(servAdd.equals("")||servPortNumber.equals("")){
                    Toast.makeText(MainActivity.this, "Please Enter Valid server address & port", Toast.LENGTH_LONG).show();
                    return;
                }
                ClientTask clientTask = new ClientTask();
                clientTask.execute(servAdd, servPortNumber, message);
            }
        });
    }
    private class ClientTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String servAdd = params[0];
            int portNumber = Integer.parseInt(params[1]);
            String message = params[2];

            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            String replyMessage = "";
            try {
                socket = new Socket(servAdd, portNumber);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(message);

                replyMessage = dataInputStream.readUTF();

            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(dataInputStream!=null){
                    try{
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(dataOutputStream!=null){
                    try{
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(socket!=null){
                    try{
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return replyMessage;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            responseText.setText(s);
        }
    }
}
