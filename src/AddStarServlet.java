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
@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/add-star")
public class AddStarServlet extends HttpServlet {

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

        try(out;){
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

        String name = request.getParameter("name");
        String year = request.getParameter("year");

        PrintWriter out = response.getWriter();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out;Connection conn = dataSource.getConnection()) {

            PreparedStatement pstate = conn.prepareStatement("With biggest as (select max(substring(id, 3)) as maxID from stars) select concat(\"nm\", maxID + 1) as starID from biggest;");
            ResultSet rs = pstate.executeQuery();
            rs.next();

            if(name != "")
            {
                String query = "INSERT INTO stars VALUES(?, ?, ?)";

                PreparedStatement statement = conn.prepareStatement(query);


                statement.setString(1, rs.getString("starID")); //set the id
                statement.setString(2, name);
                if(year != "")
                    statement.setString(3, year);
                else
                    statement.setString(3, null);

                statement.executeUpdate();

                responseJsonObject.addProperty("success", true);
                responseJsonObject.addProperty("id", rs.getString("starID"));
                statement.close();
            }
            else
            {
                responseJsonObject.addProperty("success", false);
            }


            // set response status to 200 (OK)
            response.setStatus(200);
            rs.close();
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
