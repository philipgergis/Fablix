import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, NamingException {
        // parse the movies
        Parser spe = new Parser();
        spe.run();

        // parse the stars
        parseStars starpe = new parseStars();
        starpe.run();

        // parse additional actorsow
        parseActors spActors = new parseActors();
        spActors.run();

        // Init variables to load into the database
        String line;
        HashMap<Integer, String> genres = new HashMap<Integer, String>();
        ArrayList<Movie> movies = new ArrayList<Movie>();
        HashMap<String, Star> stars = new HashMap<String, Star>();
        HashMap<String, Integer> gim = new HashMap<String, Integer>();
        HashMap<String, String> sim = new HashMap<String, String>();

        // parse through the txt files and then use the stored procedure to add those to the database

        // get the info from movies: title, year, director, id
        BufferedReader movieReader = new BufferedReader(new FileReader("movies.txt")); // used to load the movie info
        while ((line = movieReader.readLine()) != null) {

            String[] params = line.split(";;");

            Movie m = new Movie(params[0], params[1], Integer.parseInt(params[2]), params[3]);
            movies.add(m);
        }
        movieReader.close();

        // get info from genre: id, genre
        BufferedReader genreReader = new BufferedReader(new FileReader("genres.txt")); // used to load the movie info
        line = "";
        while ((line = genreReader.readLine()) != null) {
            String[] params = line.split(",");
            if(params.length > 1) {
                genres.put(Integer.parseInt(params[0]), params[1]);
            }
        }
        genreReader.close();

        // get the info from the stars
        BufferedReader starReader = new BufferedReader(new FileReader("stars.txt")); // used to load the movie info
        line = "";
        while ((line = starReader.readLine()) != null) {
            String[] params = line.split(",");
            Star s = new Star(params[0], params[1]);
            stars.put(s.getId(), s);
        }
        starReader.close();

        // get the info from additional Stars
        BufferedReader addStarReader = new BufferedReader(new FileReader("additionalStars.txt")); // used to load the movie info
        line = "";
        while ((line = addStarReader.readLine()) != null) {
            String[] params = line.split(",");
            if(params.length > 1){
                Star s = new Star(params[0], params[1]);
                stars.put(s.getId(), s);
            }
        }
        addStarReader.close();

        // get the info from additional Stars
        BufferedReader gimReader = new BufferedReader(new FileReader("genres_in_movies.txt")); // used to load the movie info
        line = "";
        while ((line = gimReader.readLine()) != null) {
            String[] params = line.split(",");
            if(params.length > 1){
                gim.put((params[1]), Integer.parseInt(params[0]));
            }
        }
        gimReader.close();

        // get the info from additional Stars
        BufferedReader simReader = new BufferedReader(new FileReader("stars_in_movies.txt")); // used to load the movie info
        line = "";
        while ((line = simReader.readLine()) != null) {
            String[] params = line.split(";;");
            if(params.length > 1){
                sim.put(params[1], params[0]);
            }
        }
        simReader.close();

//        Context initContext = new InitialContext();
//        Context envContext = (Context) initContext.lookup("java:/comp/env");
//        DataSource dataSource = (DataSource) envContext.lookup("master");

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);


        for(Movie m : movies){
            CallableStatement statement = connection.prepareCall("{call add_movie(?, ?, ?, ?, ?)}");

            System.out.println(m.toString());

            String movieID = m.getId();
            String genreName = "other";
            if(gim.containsKey(movieID)) {
                int genreID = gim.get(movieID);
                genreName = genres.get(genreID);
            }
            if (genreName==null){
                genreName = "other";
            }
            String starName = "unknown";
            String movieTitle = m.getTitle();
            if(sim.containsKey(movieTitle)){
                String starID = sim.get(movieTitle);
                starName = stars.get(starID).getName();
            }

            statement.setString(1, m.getTitle());
            statement.setString(2, Integer.toString(m.getYear()));
            statement.setString(3, m.getDirector());
            statement.setString(4, starName);
            statement.setString(5, genreName);

            ResultSet rs = statement.executeQuery();
        }
    }
}