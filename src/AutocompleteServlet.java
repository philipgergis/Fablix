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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movie-list"
@WebServlet(name = "AutocompleteServlet", urlPatterns = "/api/auto")
public class AutocompleteServlet extends HttpServlet {
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

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        JsonArray jsonArray = new JsonArray();


        try (out; Connection conn = dataSource.getConnection()){

            // get the query string from parameter
            String title = request.getParameter("searchTitle");

            // return the empty json array if query is null or empty
            if (title == null || title.trim().isEmpty()) {
                out.write(jsonArray.toString());
                return;
            }

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars

            String query = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating, r.numVotes\n" +
                    "FROM movies m, ratings r\n" +
                    "WHERE r.movieId=m.id AND match(m.title) against (? in boolean mode)\n" +
                    "LIMIT 10;";

            String newTitle = "";
            if(title.length() > 0)
            {
                String [] keywords = title.split(" ");
                for(String s : keywords)
                {
                    newTitle += "+" + s + "*";
                }
            }
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, newTitle);

            ResultSet rs = statement.executeQuery();

            while(rs.next())
            {
                String movie_title = rs.getString("m.title");
                String movie_year = rs.getString("m.year");
                String movie_director = rs.getString("m.director");
                String movie_rating = rs.getString("r.rating");
                String movie_id = rs.getString("m.id");

                jsonArray.add(generateJsonObject(movie_id, movie_title, movie_director, movie_rating, movie_year));
            }



            out.write(jsonArray.toString());
            out.close();
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }

    }

    private static JsonObject generateJsonObject(String id, String title, String director, String rating, String year) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject newObject = new JsonObject();
        newObject.addProperty("id", id);
        newObject.addProperty("director", director);
        newObject.addProperty("rating", rating);
        newObject.addProperty("year", year);

        jsonObject.add("data", newObject);
        return jsonObject;
    }
}


