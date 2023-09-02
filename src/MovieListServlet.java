import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movie-list"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type
        // Start timing search
        long startTS = System.nanoTime();
        long totalTS;
        long startTJ;
        long totalTJ = -1;

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(true);

        HashMap<String, String> params = new HashMap<>();
        params.put("queryType", request.getParameter("queryType"));
        params.put("sortType" , request.getParameter("sort"));
        params.put("ratingSort" , request.getParameter("ratingSort"));
        params.put("titleSort" , request.getParameter("titleSort"));
        params.put("limitNum" , request.getParameter("limitNum"));
        params.put("offset" , request.getParameter("offset"));
        params.put( "character" , request.getParameter("character"));
        params.put("genre" , request.getParameter("genre"));
        params.put("searchTitle" , request.getParameter("searchTitle"));
//        params.put("searchYear" , request.getParameter("searchYear"));
//        params.put("searchDirector" , request.getParameter("searchDirector"));
//        params.put("searchStar" , request.getParameter("searchStar"));

        session.setAttribute("allParams", params);




        String query = "";
        ArrayList<String> inputParams = new ArrayList<>();

        if(params.get("queryType").equals("home"))
        {
            query = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating, r.numVotes\n" +
                    "FROM movies m, ratings r\n" +
                    "WHERE r.movieId=m.id\n";
        }
        else if(params.get("queryType").equals("search"))
        {
//            inputParams.add("%" + params.get("searchTitle") + "%");
//            if (params.get("searchYear").length() > 0)
//            {
//                query = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating, r.numVotes\n" +
//                        "FROM movies m, ratings r, stars_in_movies sim, stars s\n" +
//                        "WHERE r.movieId=m.id AND s.id=sim.starID AND m.id=sim.movieID AND m.title LIKE ? AND m.year= ? AND m.director LIKE ? AND s.name LIKE ?\n";
//
//                inputParams.add( params.get("searchYear") );
//
//            }
//            else
//            {
//                query = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating, r.numVotes\n" +
//                        "FROM movies m, ratings r, stars_in_movies sim, stars s\n" +
//                        "WHERE r.movieId=m.id AND s.id=sim.starID AND m.id=sim.movieID AND m.title LIKE ? AND m.director LIKE ? AND s.name LIKE ?\n";
//
//            }
//            inputParams.add("%" + params.get("searchDirector") + "%");
//            inputParams.add("%" + params.get("searchStar") + "%");
            query = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating, r.numVotes\n" +
                    "FROM movies m, ratings r\n" +
                    "WHERE r.movieId=m.id AND match(m.title) against (? in boolean mode)\n";

            String newTitle = "";
            if(params.get("searchTitle").length() > 0)
            {
                String [] keywords = params.get("searchTitle").split(" ");
                for(String s : keywords)
                {
                    newTitle += "+" + s + "*";
                }
            }
            inputParams.add(newTitle);
        }
        else if(params.get("queryType").equals("browse"))
        {
            if (params.get("character").equals("*"))
            {
                String newCharacter = "^[^a-z0-9A-Z].*";
                query = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating, r.numVotes\n" +
                        "FROM movies m, ratings r\n" +
                        "WHERE r.movieId=m.id AND m.title REGEXP(?)\n";
                inputParams.add(newCharacter);
            }
            else if (params.get("character").length() > 0)
            {
                query = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating, r.numVotes\n" +
                        "FROM movies m, ratings r\n" +
                        "WHERE r.movieId=m.id AND m.title LIKE ?\n";
                inputParams.add( params.get("character")  + "%");
            }
            else
            {
                query = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating, r.numVotes\n" +
                        "FROM movies m, ratings r, genres g, genres_in_movies gim\n" +
                        "WHERE r.movieId=m.id AND g.id=gim.genreID AND gim.movieID=m.id AND g.name=?\n";
                inputParams.add(params.get("genre"));
            }
        }
        String sortLine = "ORDER BY ";

        String rSort = params.get("ratingSort");
        String tSort = params.get("titleSort");

        if((rSort.equals("DESC") || rSort.equals("ASC")) && (tSort.equals("DESC") || tSort.equals("ASC")))
        {
            if(params.get("sortType").equals("rating"))
            {
                sortLine += "r.rating " + rSort + ", m.title " + tSort + "\n";
            }
            else
            {
                sortLine += "m.title " + tSort + ", r.rating " + rSort + "\n";
            }

            query += sortLine;
        }

        query += "LIMIT ? OFFSET ?;";
        inputParams.add(params.get("limitNum"));
        inputParams.add("" + (Integer.parseInt(params.get("limitNum")) * Integer.parseInt(params.get("offset")) ) );


        startTJ = System.nanoTime();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            for (int i = 0; i < inputParams.size()-2; i++)
            {
                statement.setString(i+1, inputParams.get(i));
            }

            for (int i = inputParams.size()-2; i < inputParams.size(); i++)
            {
                statement.setInt(i+1, (int) Integer.parseInt(inputParams.get(i))  );
            }

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_title = rs.getString("m.title");
                String movie_year = rs.getString("m.year");
                String movie_director = rs.getString("m.director");
                String movie_rating = rs.getString("r.rating");
                String movie_votes = rs.getString("r.numVotes");

                String movie_genre = "";
                String movie_star = "";
                String star_id = "";
                int star_count = 0;
                String movie_id = rs.getString("m.id");

//                String q = "SELECT orderedList.name, orderedList.id, COUNT(orderedList.id) as movieCount\n" +
//                        "FROM stars_in_movies sim,\n" +
//                        "\t(SELECT s.name, s.id\n" +
//                        "\tFROM stars s, stars_in_movies sim\n" +
//                        "\tWHERE s.id=sim.starID AND '" + movie_id + "'=sim.movieID \n" +
//                        "\tGROUP BY s.name, s.id ) as orderedList\n" +
//                        "WHERE orderedList.id=sim.starID\n" +
//                        "GROUP BY orderedList.name, orderedList.id\n" +
//                        "ORDER BY movieCount DESC, orderedList.name ASC; \n";

                String q = "SELECT orderedList.name, orderedList.id, COUNT(orderedList.id) as movieCount\n" +
                        "FROM stars_in_movies sim,\n" +
                        "\t(SELECT s.name, s.id\n" +
                        "\tFROM stars s, stars_in_movies sim\n" +
                        "\tWHERE s.id=sim.starID AND ?=sim.movieID \n" +
                        "\tGROUP BY s.name, s.id ) as orderedList\n" +
                        "WHERE orderedList.id=sim.starID\n" +
                        "GROUP BY orderedList.name, orderedList.id\n" +
                        "ORDER BY movieCount DESC, orderedList.name ASC; \n";

                PreparedStatement sStar = conn.prepareStatement(q);
                sStar.setString(1, movie_id);
                ResultSet rStar = sStar.executeQuery();


//                Statement sStar = conn.createStatement();
//                ResultSet rStar = sStar.executeQuery(q);
                while(rStar.next() && star_count < 3)
                {
                    movie_star += rStar.getString("orderedList.name") + ",";
                    star_id += rStar.getString("orderedList.id") + ",";
                    star_count++;
                }
                rStar.close();
                sStar.close();
                movie_star = movie_star.substring(0, movie_star.length()-1);
                star_id = star_id.substring(0, star_id.length()-1);



//                q = "SELECT g.name\n" +
//                        "FROM genres g, genres_in_movies gim\n" +
//                        "WHERE g.id=gim.genreId AND \"" + movie_id +  "\"=gim.movieId\n" +
//                        "ORDER BY g.name ASC;";

                q = "SELECT g.name\n" +
                        "FROM genres g, genres_in_movies gim\n" +
                        "WHERE g.id=gim.genreId AND ?=gim.movieId\n" +
                        "ORDER BY g.name ASC;";

                PreparedStatement sGenre = conn.prepareStatement(q);
                sGenre.setString(1, movie_id);
                ResultSet rGenre = sGenre.executeQuery();

//                Statement sGenre = conn.createStatement();
//                ResultSet rGenre = sGenre.executeQuery(q);
                int count = 0;
                while(rGenre.next() && count < 3)
                {
                    movie_genre += rGenre.getString("g.name") + ", ";
                    count++;
                }
                rGenre.close();
                sGenre.close();
                movie_genre = movie_genre.substring(0, movie_genre.length()-2);

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_genre", movie_genre);
                jsonObject.addProperty("movie_star", movie_star);
                jsonObject.addProperty("star_id", star_id);
                jsonObject.addProperty("star_count", star_count);
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_votes", movie_votes);


                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
            totalTJ = System.nanoTime() - startTJ;

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        totalTS = System.nanoTime() - startTS;
        if(params.get("queryType").equals("search")) {
            File file = new File(request.getServletContext().getRealPath("/") + "log.txt");
            if (file.createNewFile()) {
                FileWriter myWriter = new FileWriter(request.getServletContext().getRealPath("/") + file.getName());
                myWriter.write("TS-" + totalTS + ", TJ-" + totalTJ + "\n");
                myWriter.close();
            } else {
                FileWriter myWriter = new FileWriter(request.getServletContext().getRealPath("/") + file.getName(), true);
                myWriter.write("TS-" + totalTS + ", TJ-" + totalTJ + "\n");
                myWriter.close();
            }
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}