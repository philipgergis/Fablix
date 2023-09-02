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
import java.util.*;


public class Parser extends DefaultHandler {

    //to maintain context
    private String tempVal;
    private String tempDirector;
    private Movie tempMovie;
    private Genre tempGenre;

    // store the current ids of the movies
    private Integer currentMovieID;
    private Integer currentGenreID;

    ArrayList<Movie> myMovies;
    private HashMap<String,Integer> genres;
    public static HashMap<Movie,String> movieDBMap;

    public Parser() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        String query = "SELECT max(substring(id, 3, 9)) as id from movies";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        // to increment future movie ids
        currentMovieID = 0;

        if (rs.next()){
            currentMovieID = Integer.parseInt(rs.getString("id"));
        }

        genres = new HashMap<String, Integer>();

        query = "SELECT name, id from genres";
        statement = connection.prepareStatement(query);
        rs = statement.executeQuery();

        while(rs.next()){
            genres.put(rs.getString("name"), Integer.parseInt(rs.getString("id")));
        }

        // to incremement future genre ids
        currentGenreID = Collections.max(genres.values());

        movieDBMap = new HashMap<>();

        // keep a running collection of movie
        myMovies = new ArrayList<Movie>();
    }

    public void run() throws IOException {
        parseDocument();
        writeMovieFile();
        writeGenreFile();
        updateMovies();
        writeGenreInMoviesFile();
    }

    private void updateMovies(){
        Iterator<Movie> it = myMovies.iterator();
        while(it.hasNext()){
            Iterator<Genre> gt = it.next().getGenreList().iterator();
            while (gt.hasNext()){
                Genre g = gt.next();
                if (genres.containsKey(g.getName())){
                    g.setId(genres.get(g.getName()));
                } else {
                    currentGenreID++;
                    genres.put(g.getName(), currentGenreID);
                    g.setId(currentGenreID);
                }
            }
        }
    }

    private void writeMovieFile() throws IOException {
        PrintWriter writer = new PrintWriter("movies.txt", "UTF-8");
        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            Movie m = it.next();
            // System.out.println(m.toString());
            String xmlId = m.getId();
            currentMovieID++;
            m.setId(String.format("tt%07d", currentMovieID));
            movieDBMap.put(m, m.getId());
            writer.printf("%s;;%s;;%d;;%s\n", m.getTitle(),m.getId(),m.getYear(),m.getDirector());
        }
        writer.close();
    }

    private void writeGenreInMoviesFile() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("genres_in_movies.txt", "UTF-8");

        Iterator<Movie> it = myMovies.iterator();
        while(it.hasNext()){
            Movie m = it.next();
            Iterator<Genre> gt = m.getGenreList().iterator();
            while (gt.hasNext()){
                Genre g = gt.next();
                if(g.getId() != null)
                    writer.printf("%d,%s\n",g.getId(),m.getId());
            }
        }
        writer.close();
    }

    private void writeGenreFile() throws IOException {
        PrintWriter writer = new PrintWriter("genres.txt", "UTF-8");
        Set<Genre> hash_Set = new HashSet<>();

        // .println(myMovies);

        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            Movie m =it.next();
            Iterator<Genre> gt = m.getGenreList().iterator();
            while(gt.hasNext()){
                Genre g = gt.next();
                if(!genres.containsKey(g.getId())) {
                    hash_Set.add(g);
                }
            }
        }

        Iterator<Genre> i = hash_Set.iterator();
        while (i.hasNext()) {
            Genre g = i.next();
            if(!genres.containsKey(g.getName())){
                currentGenreID++;
                g.setId(currentGenreID);
                genres.put(g.getName(), g.getId());
                writer.printf("%s,%s\n", g.getId(), g.getName().replaceAll("\\s",""));
            }
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
            sp.parse("mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private void printData() {

        // System.out.println("No of Movies '" + myMovies.size() + "'.");

        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of Movie
            tempMovie = new Movie();
        }
        if (qName.equalsIgnoreCase("cat")) {
            tempGenre = new Genre();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("directorfilms")) {
            tempDirector = null;
        }
        if (qName.equalsIgnoreCase("dirname")) {
            tempDirector = tempVal;
        }
        if (qName.equalsIgnoreCase("cat")) {
            if(!tempVal.equalsIgnoreCase("ctxx")){
                String [] splits = tempVal.split("[, ?.@-]+");
                for(String s : splits){
                    tempGenre.setName(s.toLowerCase());
                    tempMovie.addGenre(tempGenre);
                    tempGenre = new Genre();
                }
            }
        }
        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            tempMovie.setDirector(tempDirector);
            myMovies.add(tempMovie);
        }
        if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setId(tempVal);
        }
        if (qName.equalsIgnoreCase("t")) {
            //add it to the list
            tempMovie.setTitle(tempVal);
        }
        if (qName.equalsIgnoreCase("year")) {
            //add it to the list
            try{
                tempMovie.setYear(Integer.parseInt(tempVal));
            }
            catch (Exception e){
                tempMovie.setYear(0);
            }
        }
    }
}