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
@WebServlet(name = "MetaData", urlPatterns = "/_dashboard/api/meta-data")
public class MetaData extends HttpServlet {
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




        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {

            // Declare our statement
            DatabaseMetaData d = conn.getMetaData();
            ResultSet rs = d.getColumns("moviedb", null, "%", "%");

            while(rs.next()){
                JsonObject responseJsonObject = new JsonObject();

                String table = rs.getString("TABLE_NAME");
                String column = rs.getString("COLUMN_NAME");
                String type = rs.getString("TYPE_NAME");
                String size = rs.getString("COLUMN_SIZE");

                responseJsonObject.addProperty("table", table);
                responseJsonObject.addProperty("column", column);
                responseJsonObject.addProperty("type", type);
                responseJsonObject.addProperty("size", size);

                jsonArray.add(responseJsonObject);
            }

            rs.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

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



        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
