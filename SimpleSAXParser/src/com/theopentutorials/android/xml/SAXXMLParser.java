package com.theopentutorials.android.xml;
 
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import com.theopentutorials.android.beans.Employee;

import android.util.Log;
 
public class SAXXMLParser {
    public static List<Employee> parse(InputStream is) {
        List<Employee> employees = null;
        try {
            // create a XMLReader from SAXParser
        	// XMLReader allows an application to set and query features and properties in the parser,
        	// to register event handlers for document processing, and to initiate a document parse.
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            // create a SAXXMLHandler
            SAXXMLHandler saxHandler = new SAXXMLHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
            xmlReader.parse(new InputSource(is));
            // get the `Employee list`
            employees = saxHandler.getEmployees();
 
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: parse() failed");
        }
 
        // return Employee list
        return employees;
    }
}