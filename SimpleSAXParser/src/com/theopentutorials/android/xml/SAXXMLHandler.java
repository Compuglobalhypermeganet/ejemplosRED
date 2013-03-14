package com.theopentutorials.android.xml;
 
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.theopentutorials.android.beans.Employee;
 
public class SAXXMLHandler extends DefaultHandler {
 
    private List<Employee> employees;
    private String tempVal;
    private Employee tempEmp;
 
    public SAXXMLHandler() {
        employees = new ArrayList<Employee>();
    }
 
    public List<Employee> getEmployees() {
        return employees;
    }
 
    // Event Handlers
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        // reset
        tempVal = "";
        if (qName.equalsIgnoreCase("employee")) {
            // create a new instance of employee
            tempEmp = new Employee();
        }
    }
 
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tempVal = new String(ch, start, length);
    }
 
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("employee")) {
            // add it to the list
            employees.add(tempEmp);
        } else if (qName.equalsIgnoreCase("id")) {
            tempEmp.setId(Integer.parseInt(tempVal));
        } else if (qName.equalsIgnoreCase("name")) {
            tempEmp.setName(tempVal);
        } else if (qName.equalsIgnoreCase("department")) {
            tempEmp.setDepartment(tempVal);
        } else if (qName.equalsIgnoreCase("type")) {
            tempEmp.setType(tempVal);
        } else if (qName.equalsIgnoreCase("email")) {
            tempEmp.setEmail(tempVal);
        }
    }
}