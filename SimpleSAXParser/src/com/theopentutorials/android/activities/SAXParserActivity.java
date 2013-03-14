package com.theopentutorials.android.activities;
 
import java.io.IOException;
import java.util.List;
import com.theopentutorials.android.beans.Employee;
import com.theopentutorials.android.xml.SAXXMLParser;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
 
public class SAXParserActivity extends Activity implements
        OnClickListener, OnItemSelectedListener {
 
    Button button;
    Spinner spinner;
    List<Employee> employees = null; // Every element in the List has an index
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        findViewsById();
        button.setOnClickListener(this);
    }
 
    private void findViewsById() {
        button = (Button) findViewById(R.id.button);
        spinner = (Spinner) findViewById(R.id.spinner);
    }
 
    public void onClick(View v) {
        try {
            employees = SAXXMLParser.parse(getAssets().open("employees.xml"));
            // getAssets().open: Open an asset using ACCESS_STREAMING mode
            ArrayAdapter<Employee> adapter = new ArrayAdapter<Employee>(this,R.layout.list_item, employees);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Employee employee = (Employee) parent.getItemAtPosition(pos);
        Toast.makeText(parent.getContext(), employee.getDetails(), Toast.LENGTH_LONG).show();
        // Toast is used for displaying superimpose data
    }
 
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
 
}