import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating\n" +
                    "FROM movies m, ratings r\n" +
                    "WHERE r.movieId=m.id AND m.id = ?;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movie_rating = rs.getString("r.rating");
                String movie_title = rs.getString("m.title");
                String movie_director = rs.getString("m.director");
                String movie_year = rs.getString("m.year");


                String movie_genre = "";
                String movie_star = "";
                String star_id = "";
                String movie_id = rs.getString("m.id");

//                String q ="SELECT orderedList.name, orderedList.id, COUNT(orderedList.id) as movieCount\n" +
//                        "FROM stars_in_movies sim,\n" +
//                        "\t(SELECT s.name, s.id\n" +
//                        "\tFROM stars s, stars_in_movies sim\n" +
//                        "\tWHERE s.id=sim.starID AND '" + movie_id + "'=sim.movieID \n" +
//                        "\tGROUP BY s.name, s.id ) as orderedList\n" +
//                        "WHERE orderedList.id=sim.starID\n" +
//                        "GROUP BY orderedList.name, orderedList.id\n" +
//                        "ORDER BY movieCount DESC, orderedList.name ASC;";

                String q ="SELECT orderedList.name, orderedList.id, COUNT(orderedList.id) as movieCount\n" +
                        "FROM stars_in_movies sim,\n" +
                        "\t(SELECT s.name, s.id\n" +
                        "\tFROM stars s, stars_in_movies sim\n" +
                        "\tWHERE s.id=sim.starID AND ?=sim.movieID \n" +
                        "\tGROUP BY s.name, s.id ) as orderedList\n" +
                        "WHERE orderedList.id=sim.starID\n" +
                        "GROUP BY orderedList.name, orderedList.id\n" +
                        "ORDER BY movieCount DESC, orderedList.name ASC;";

                PreparedStatement sStar = conn.prepareStatement(q);
                sStar.setString(1, movie_id);
                ResultSet rStar = sStar.executeQuery();


//                Statement sStar = conn.createStatement();
//                ResultSet rStar = sStar.executeQuery(q);
                while(rStar.next())
                {
                    movie_star += rStar.getString("orderedList.name") + ",";
                    star_id += rStar.getString("orderedList.id") + ",";
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
                while(rGenre.next())
                {
                    movie_genre += rGenre.getString("g.name") + ", ";
                }
                rGenre.close();
                sGenre.close();
                movie_genre = movie_genre.substring(0, movie_genre.length()-2);


                // Create a JsonObject based on the data we retrieve from rs

                HashMap<String, String> params = (HashMap<String, String>) request.getSession().getAttribute("allParams");
                String homeURL = String.format(
                        "movie-list.html?queryType=%s&sort=%s&ratingSort=%s&titleSort=%s&limitNum=%s&offset=%s&searchTitle=%s&searchYear=%s&searchDirector=%s&searchStar=%s&character=%s&genre=%s",
                        params.get("queryType"),params.get("sortType"),params.get("ratingSort"),params.get("titleSort"),
                        params.get("limitNum"),params.get("offset"), params.get("searchTitle"),params.get("searchYear"),
                        params.get("searchDirector"), params.get("searchStar"), params.get("character"), params.get("genre"));


                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genre", movie_genre);
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_star", movie_star);
                jsonObject.addProperty("star_id", star_id);
                jsonObject.addProperty("home_url",homeURL);


                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
