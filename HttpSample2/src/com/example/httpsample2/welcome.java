package com.example.httpsample2;

import android.app.Activity;
import android.os.Bundle;

public class Welcome extends Activity{
      @Override
      public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            System.gc();
            setContentView(R.layout.welcome);
      }
}