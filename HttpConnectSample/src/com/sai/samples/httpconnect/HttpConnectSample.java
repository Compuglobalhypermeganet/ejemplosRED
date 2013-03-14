package com.sai.samples.httpconnect;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HttpConnectSample extends Activity {
    
	private Button getImageButton;
	private Button getTextButton;
	private ProgressDialog progressDialog;	
	private Bitmap bitmap = null;
	private String text = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getImageButton = (Button)findViewById(R.id.Button01);
        getTextButton = (Button)findViewById(R.id.Button02);
        
        getImageButton.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadImage("http://www.android.com/media/wallpaper/gif/android_logo.gif");
				
			}
        });
        
        getTextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadText("http://saigeethamn.blogspot.com/feeds/posts/default");
			}
        });
    }
    

	private void downloadImage(String urlStr) {
		// A dialog showing a progress indicator and an optional text message or view.
		progressDialog = ProgressDialog.show(this, "", "Fetching Image...");
		final String url = urlStr; // Variable de tipo "final" se puede acceder desde el nuevo hilo
		
		new Thread() { // Es mejor hacer un nuevo hilo dedicado a la conexion, en lugar de usar el principal
			public void run() {
				InputStream in = null;
				// Existe una cola de mensajes, por eso hay que crear uno nuevo mediante obtain
				Message msg = Message.obtain(); // message is an object for communication between threads
				msg.what = 1; // User-defined message code
				try {
				    in = openHttpConnection(url);
				    bitmap = BitmapFactory.decodeStream(in); // Decode an input stream into a bitmap
				    Bundle b = new Bundle();
				    b.putParcelable("bitmap", bitmap);
				    msg.setData(b);  // "Empaqueto" el objeto bitmap y lo paso al mensaje msg
				    in.close(); // Hay que cerrar el flujo de entrada o input-stream
				} catch (IOException e1) {
				    e1.printStackTrace();
				}
				// I notify the main / UI thread through this method and also pass on the Message object
				messageHandler.sendMessage(msg); // Enviamos el mensaje al hilo principal
				
			}
 		}.start(); // Starts the new Thread of execution

	}
	
	private void downloadText(String urlStr) { // Similar a downloadImage
		progressDialog = ProgressDialog.show(this, "", "Fetching Text...");
		final String url = urlStr;
		
		new Thread () {
			public void run() {
				int BUFFER_SIZE = 2000; // Cogemos caracteres de 2000 en 2000
		        InputStream in = null;
		        Message msg = Message.obtain();
		        msg.what=2;
		        try {
		        	in = openHttpConnection(url);
		            // inputstreamreader turns a byte stream into a character stream
		            InputStreamReader isr = new InputStreamReader(in); // Lee caracteres del input stream
		            // OJO: no lee un string sino un array de caracteres
		            int charRead;
		              text = ""; // String que contendra el texto final
		              char[] inputBuffer = new char[BUFFER_SIZE]; // Array de caracteres

		                // Reads characters from isr and stores them in the character
		                // array inputBuffer starting at offset 0.
		              	// Returns the number of characters actually read or -1
		                // if the end of the reader has been reached.
		                  while ((charRead = isr.read(inputBuffer))>0)
		                  {                    
		                      //---convert the chars to a String---  (data, start, length)
		                      String readString = String.copyValueOf(inputBuffer, 0, charRead);                    
		                      text += readString; // Vamos componiendo el texto
		                      inputBuffer = new char[BUFFER_SIZE]; // Limpiamos el buffer
		                  }
		                 Bundle b = new Bundle();
						    b.putString("text", text); // Empaquetamos el texto y lo enviamos al hilo principal
						    msg.setData(b);
		                  in.close();
	                  
				}catch (IOException e) {
	                e.printStackTrace();
	            }
				messageHandler.sendMessage(msg); // messageHandler es el nombre elegido para este handler
			}
		}.start();
          
	}
	
	// Hence is the code for opening and making an HTTP Connection:
	private InputStream openHttpConnection(String urlStr) {
		InputStream in = null;
		int resCode = -1;
		
		try {
			URL url = new URL(urlStr);// Pasamos el string a url
			URLConnection urlConn = url.openConnection();// Abrimos una conexion
			// Instances of URLConnection are not reusable
			
			if (!(urlConn instanceof HttpURLConnection)) {// Aseguramos que es una conexion HTTP
				throw new IOException ("URL is not an Http URL");
			}
			
			HttpURLConnection httpConn = (HttpURLConnection)urlConn;// Transformamos la conexion en conexion HTTP
			httpConn.setAllowUserInteraction(false);// Esto habra que cambiarlo ?? !!
            httpConn.setInstanceFollowRedirects(true);// true if this connection will follows redirects
            httpConn.setRequestMethod("GET");// Sets the request command which will be sent to the remote HTTP server
            httpConn.connect(); // Opens a connection to the resource

            resCode = httpConn.getResponseCode();// Returns the response code returned by the remote HTTP server.                
            if (resCode == HttpURLConnection.HTTP_OK) {
            	// Devuelve el objeto solicitado mediante GET
                in = httpConn.getInputStream(); // Returns an InputStream for reading data from the resource                            
            }         
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}

// You can create your own threads, and communicate back with the main application thread through a Handler.
	private Handler messageHandler = new Handler() { // Recibimos el mensaje en el hilo principal
		
		public void handleMessage(Message msg) { // Subclasses must implement this to receive messages
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				ImageView img = (ImageView) findViewById(R.id.imageview01);
				img.setImageBitmap((Bitmap)(msg.getData().getParcelable("bitmap")));
				break;
			case 2:
				TextView text = (TextView) findViewById(R.id.textview01);
				text.setText(msg.getData().getString("text"));
				break;
			}
			progressDialog.dismiss();
		}
	};
}