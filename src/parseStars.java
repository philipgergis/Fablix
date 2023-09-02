import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class parseStars extends DefaultHandler {

    HashMap<String, Movie> movies;
    HashMap<String, String> starDB;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    private String tempMovie;

    private String tempDirector;

    private String tempTitle;

    private Integer curStarId;

    private ArrayList<Star> newStars;

    private Integer starCounter;

    public parseStars() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        PreparedStatement statement = connection.prepareStatement("select max(substring(id, 3)) as id from stars");
        ResultSet rs = statement.executeQuery();
        rs.next();

        curStarId = Integer.parseInt(rs.getString("id"));
        starCounter = 0;
        movies = new HashMap<>();
        newStars = new ArrayList<>();
        starDB = new HashMap<>();
        rs = connection.prepareStatement("select id,name from stars").executeQuery();
        while(rs.next()){
            starDB.put(rs.getString("name"),rs.getString("id"));
        }
    }

    public void run() throws IOException {
        parseDocument();
        starsToMovie();
        writeToStars();
        printData();
    }

    private void parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private void starsToMovie() throws IOException {
        PrintWriter writer = new PrintWriter("stars_in_movies.txt", "UTF-8");

        Iterator hmIterator = movies.entrySet().iterator();

        while (hmIterator.hasNext()) {
            Map.Entry<String, Movie> it = (Map.Entry<String, Movie>) hmIterator.next();

            Movie m = it.getValue();

            if(m.getId() != null){
                Iterator<Star> st = m.getStarList().iterator();

                while(st.hasNext()){
                    Star s = st.next();

                    if(starDB.get(s.getName()) != null) {
                        writer.printf("%s;;%s\n", starDB.get(s.getName()), m.getId());
                    }
                    else{
                        starCounter++;
                        curStarId++;
                        s.setId(String.format("nm%07d",curStarId));
                        starDB.put(s.getName(), s.getId());
                        newStars.add(s);
                        writer.printf("%s;;%s\n", s.getId(), m.getTitle());
                    }
                }
            }
        }
        writer.close();
    }

    private void writeToStars() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("additionalStars.txt", "UTF-8");
        Iterator<Star> st = newStars.iterator();
        int starTracker = 0;
        System.out.println(newStars);
        while(st.hasNext()){
            Star s = st.next();
            starTracker++;
            writer.printf("%s,%s\n",s.getId(),s.getName());
        }
        System.out.println("NEW STARS " + starTracker);
        writer.close();

    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

//        System.out.println("No of Stars '" + movies.size() + "'.");
//
//        Iterator hmIterator = movies.entrySet().iterator();
//
//        System.out.println("No of new stars '" + starCounter + "'.");

    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            tempStar = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("dirfilms")) {
            tempDirector = null;
        }
        if (qName.equalsIgnoreCase("is")) {
            tempDirector = tempVal;
        }
        if (qName.equalsIgnoreCase("m")) {
            if(movies.get(tempTitle) != null){
                Movie m = movies.get(tempTitle);
                m.addStar(tempStar);
            }
            else{
                Movie m = new Movie();
                m.setDirector(tempDirector);
                m.addStar(tempStar);
                m.setTitle(tempTitle);
//                m.setId(Parser.movieDBMap.get(m)); // need to get the movie ID here but not sure how
                m.setId("");
                movies.put(m.getTitle(), m);
            }
        }

        if (qName.equalsIgnoreCase("f")) { //Movie Id
            tempMovie = tempVal;
        }

        if (qName.equalsIgnoreCase("t")) { //Movie Id
            tempTitle = tempVal;
        }

        if (qName.equalsIgnoreCase("a")) { //Star
            tempStar.setName(tempVal);
        }
    }
}