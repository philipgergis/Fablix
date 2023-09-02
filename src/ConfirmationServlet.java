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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Date;


@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(true);

        User currentUser = (User) session.getAttribute("user");

        try (out;Connection conn = dataSource.getConnection())
        {



            JsonArray jsonArray = new JsonArray();

            JsonObject urlObject = new JsonObject();
            HashMap<String, String> params = (HashMap<String, String>) request.getSession().getAttribute("allParams");
            String homeURL = String.format(
                    "movie-list.html?queryType=%s&sort=%s&ratingSort=%s&titleSort=%s&limitNum=%s&offset=%s&searchTitle=%s&searchYear=%s&searchDirector=%s&searchStar=%s&character=%s&genre=%s",
                    params.get("queryType"), params.get("sortType"), params.get("ratingSort"), params.get("titleSort"),
                    params.get("limitNum"), params.get("offset"), params.get("searchTitle"), params.get("searchYear"),
                    params.get("searchDirector"), params.get("searchStar"), params.get("character"), params.get("genre"));
            urlObject.addProperty("home_url", homeURL);
            jsonArray.add(urlObject);


            JsonObject jsonObject = new JsonObject();

            double total = 0;
            HashMap<String, Integer> cart = currentUser.getCart();

            for (String key : cart.keySet()) {
                double cost = (Double) session.getAttribute(key);
                total += cart.get(key) * cost;
            }

            jsonObject.addProperty("total", total);
            jsonArray.add(jsonObject);


            String customerId = (String) session.getAttribute("customerId");
            Date date = new Date(session.getLastAccessedTime());
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String fullDate = String.format("%s-%s-%s", year, month, day);
            for(String key: cart.keySet())
            {
                jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", key);
                jsonObject.addProperty("movie_title", currentUser.getTitle(key));
                jsonObject.addProperty("movie_quantity", cart.get(key));
                jsonObject.addProperty("movie_cost", (Double) session.getAttribute(key));



                String query =  "INSERT INTO sales VALUES(DEFAULT, ?, ?, ?);";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1,customerId);
                statement.setString(2,key);
                statement.setString(3,fullDate);
                statement.executeUpdate();
                query = "select id from sales Order by id desc limit 1;";
                statement = conn.prepareStatement(query);
                ResultSet rs = statement.executeQuery();
                if(rs.next())
                {
                    jsonObject.addProperty("sale_id", rs.getString("id"));
                }
                rs.close();
                statement.close();
                jsonArray.add(jsonObject);
            }

            currentUser.newCart();


            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
        }
        catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally {
            out.close();
        }


    }
}