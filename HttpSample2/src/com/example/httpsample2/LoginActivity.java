package com.example.httpsample2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
      /** Called when the activity is first created. */
      private static final String TAG = "Login";
      Button signin;
      String loginmessage = null;
      Thread t;
      private SharedPreferences mPreferences;
      ProgressDialog dialog;
      @Override
      public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
            if (!checkLoginInfo()) {
                  signin = (Button) findViewById(R.id.btn_sign_in);
                  signin.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                              showDialog(0);
                              t=new Thread() {
                                    public void run() {
                                          tryLogin();
                                    }
                              };
                        t.start();
                        }
                  });
            }
            else {
                  /*Directly opens the Welcome page, if the username and password is already available
                  in the SharedPreferences*/
                  Intent intent=new Intent(getApplicationContext(),Welcome.class);
                  startActivity(intent);
                  finish();
            }
      }
      @Override
      protected Dialog onCreateDialog(int id) {
            switch (id) {
                  case 0: {
                        dialog = new ProgressDialog(this);
                        dialog.setMessage("Please wait while connecting...");
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(true);
                        return dialog;
                  }
            }
            return null;
      }
      private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                  String loginmsg=(String)msg.obj;
                  if(loginmsg.equals("SUCCESS")) {
                        removeDialog(0);
                        Intent intent=new Intent(getApplicationContext(),Welcome.class);
                        startActivity(intent);
                        finish();
                  }
            }
      };
      public void tryLogin() {
            Log.v(TAG, "Trying to Login");
            EditText etxt_user = (EditText) findViewById(R.id.txt_username);
            EditText etxt_pass = (EditText) findViewById(R.id.txt_password);
            String username = etxt_user.getText().toString();
            String password = etxt_pass.getText().toString();
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://.......");
            List nvps = new ArrayList();
            nvps.add(new BasicNameValuePair("username", username));
            nvps.add(new BasicNameValuePair("password", password));
            try {
                  UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(nvps,
HTTP.UTF_8);
                  httppost.setEntity(p_entity);
                  HttpResponse response = client.execute(httppost);
                  Log.v(TAG, response.getStatusLine().toString());
                  HttpEntity responseEntity = response.getEntity();
                  Log.v(TAG, "Set response to responseEntity");

                  SAXParserFactory spf = SAXParserFactory.newInstance();
                  SAXParser sp = spf.newSAXParser();
                  XMLReader xr = sp.getXMLReader();
                  LoginHandler myLoginHandler = new LoginHandler();
                  xr.setContentHandler(myLoginHandler);
                  xr.parse(retrieveInputStream(responseEntity));
                  ParsedLoginDataSet parsedLoginDataSet = myLoginHandler.getParsedLoginData();
                  if (parsedLoginDataSet.getExtractedString().equals("SUCCESS")) {
                        // Store the username and password in SharedPreferences after the successful login
                        SharedPreferences.Editor editor=mPreferences.edit();
                        editor.putString("UserName", username);
                        editor.putString("PassWord", password);
                        editor.commit();
                        Message myMessage=new Message();
                        myMessage.obj="SUCCESS";
                        handler.sendMessage(myMessage);
                  } else if(parsedLoginDataSet.getExtractedString().equals("ERROR")) {
                        Intent intent = new Intent(getApplicationContext(), LoginError.class);
                        intent.putExtra("LoginMessage", parsedLoginDataSet.getMessage());
                        startActivity(intent);
                        removeDialog(0);
                  }
            } catch (Exception e)
            {
                  Intent intent = new Intent(getApplicationContext(), LoginError.class);
                  intent.putExtra("LoginMessage", "Unable to login");
                  startActivity(intent);
                  removeDialog(0);
            }
      }
      private InputSource retrieveInputStream(HttpEntity httpEntity) {
            InputSource insrc = null;
            try {
                  insrc = new InputSource(httpEntity.getContent());
            } catch (Exception e) {
            }
            return insrc;
      }
      //Checking whether the username and password has stored already or not
      private final boolean checkLoginInfo() {
            boolean username_set = mPreferences.contains("UserName");
            boolean password_set = mPreferences.contains("PassWord");
            if ( username_set || password_set ) {
                  return true;
            }
            return false;
      }
}
