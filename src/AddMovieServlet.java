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
import java.sql.*;
import java.util.HashMap;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movie-list"
@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/add-movie")
public class AddMovieServlet extends HttpServlet {

    // Create a dataSource which registered in web.
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        }
        catch (NamingException e) {
            e.printStackTrace();
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try(out;)
        {
            JsonArray jsonArray = new JsonArray();



            JsonObject urlObject = new JsonObject();
            HashMap<String, String> params = (HashMap<String, String>) request.getSession().getAttribute("allParams");
            String homeURL;
            if(params != null)
            {
                homeURL = String.format(
                        "movie-list.html?queryType=%s&sort=%s&ratingSort=%s&titleSort=%s&limitNum=%s&offset=%s&searchTitle=%s&searchYear=%s&searchDirector=%s&searchStar=%s&character=%s&genre=%s",
                        params.get("queryType"),params.get("sortType"),params.get("ratingSort"),params.get("titleSort"),
                        params.get("limitNum"),params.get("offset"), params.get("searchTitle"),params.get("searchYear"),
                        params.get("searchDirector"), params.get("searchStar"), params.get("character"), params.get("genre"));
            }
            else
            {
                homeURL = "movie-list.html?queryType=home&sort=rating&ratingSort=DESC&titleSort=ASC&limitNum=25&offset=0";

            }
            urlObject.addProperty("home_url", homeURL);
            jsonArray.add(urlObject);
            out.write(jsonArray.toString());
        }catch (Exception exception) {
            exception.printStackTrace();

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }




        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }



    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Output stream to STDOUT


        response.setContentType("application/json"); // Response mime type

        JsonObject responseJsonObject = new JsonObject();

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String genre = request.getParameter("genre");
        String star = request.getParameter("star");


        PrintWriter out = response.getWriter();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {

            if(title != "" && year != "" && director != "" && genre != "" && star != "")
            {
                CallableStatement statement = conn.prepareCall("{call add_movie(?, ?, ?, ?, ?)}");

                statement.setString(1, title);
                statement.setString(2, year);
                statement.setString(3, director);
                statement.setString(4, star);
                statement.setString(5, genre);


                ResultSet rs = statement.executeQuery();
                rs.next();
                responseJsonObject.addProperty("message", rs.getString("message"));
                rs.close();
            }
            else
            {
                responseJsonObject.addProperty("message", "Movie not added, need all parameters");
            }





            // set response status to 200 (OK)
            response.setStatus(200);
            responseJsonObject.addProperty("success", true);
            String jString = responseJsonObject.toString();
            out.write(jString);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            jsonObject.addProperty("success", false);
            String jString = jsonObject.toString();
            out.write(jString);

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }





        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
