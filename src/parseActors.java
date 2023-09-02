
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class parseActors extends DefaultHandler {

    Set<Star> stars;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    private Integer curStarId;



    public parseActors() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        String query = "select max(substring(id, 3, 9)) as id from stars";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        rs.next();

        curStarId = Integer.parseInt(rs.getString("id"));

        stars = new HashSet<>();
    }

    public void run() throws IOException {
        parseDocument();
        writeStarFile();
        printData();
    }


    private void writeStarFile() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("stars.txt", "UTF-8");

        Iterator<Star> starIt = stars.iterator();
        while(starIt.hasNext()){
            Star s = starIt.next();
            curStarId++;
            s.setId("nm"+String.format("%07d",curStarId));
            writer.printf("%s,%s%s\n",s.getId(),s.getName(),(s.getYear() == 0 ? "" : ","+s.getYear()));
        }
        writer.close();
    }

    private void parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(new File("actors63.xml"), this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Stars '" + stars.size() + "'.");

        Iterator<Star> starIt = stars.iterator();
        while (starIt.hasNext()) {
            System.out.println(starIt.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) { // Its a movie
            //create a new instance of employee
            tempStar = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            stars.add(tempStar);
        }
        if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setName(tempVal);
        }
        if (qName.equalsIgnoreCase("dob")) {
            //add it to the list
            try{
                tempStar.setYear(Integer.parseInt(tempVal));
            }
            catch (Exception e){
                tempStar.setYear(0);
            }
        }
    }
}