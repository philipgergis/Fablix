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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Random;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movie-list"
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet {
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


        try(out;){

            HttpSession session = request.getSession(true);

            User currentUser = (User) session.getAttribute("user");

            String movieTitle = request.getParameter("title");
            String movieAddTemp = request.getParameter("add");
            String movieId = request.getParameter("id");



            if(movieTitle != null && movieAddTemp != null && movieId != null)
            {
                Integer movieAdd = Integer.parseInt(movieAddTemp);
                Double movieCost = (Double) session.getAttribute(movieId);
                if(movieCost == null)
                {
                    movieCost = ((int) (Math.random() * (1000 - 500) + 500))/100.0;
                    session.setAttribute(movieId, movieCost);
                }

                if(currentUser.getQuantity(movieId) == -1)
                {
                    currentUser.updateCart(movieId, movieAdd);
                }
                else
                {
                    currentUser.updateCart(movieId, currentUser.getQuantity(movieId) + movieAdd);
                }

                if(currentUser.getTitle(movieId) == null)
                {
                    currentUser.addTitle(movieId, movieTitle);
                }


                session.setAttribute("user", currentUser);
            }

            JsonArray jsonArray = new JsonArray();
            JsonObject urlObject = new JsonObject();


            HashMap<String, String> params = (HashMap<String, String>) request.getSession().getAttribute("allParams");
            String homeURL = String.format(
                    "movie-list.html?queryType=%s&sort=%s&ratingSort=%s&titleSort=%s&limitNum=%s&offset=%s&searchTitle=%s&searchYear=%s&searchDirector=%s&searchStar=%s&character=%s&genre=%s",
                    params.get("queryType"),params.get("sortType"),params.get("ratingSort"),params.get("titleSort"),
                    params.get("limitNum"),params.get("offset"), params.get("searchTitle"),params.get("searchYear"),
                    params.get("searchDirector"), params.get("searchStar"), params.get("character"), params.get("genre"));
            urlObject.addProperty("home_url", homeURL);
            jsonArray.add(urlObject);

            HashMap<String, Integer> cart = currentUser.getCart();

            for(String key: cart.keySet())
            {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", key);
                jsonObject.addProperty("movie_title", currentUser.getTitle(key));
                jsonObject.addProperty("movie_quantity", cart.get(key));
                jsonObject.addProperty("movie_cost", (Double) session.getAttribute(key));
                jsonArray.add(jsonObject);
            }



            out.write(jsonArray.toString());
        }catch (Exception exception) {
            exception.printStackTrace();

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
    }
}