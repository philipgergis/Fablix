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

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {

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

        HttpSession session = request.getSession(true);

        User currentUser = (User) session.getAttribute("user");

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


        JsonObject jsonObject = new JsonObject();

        double total = 0;
        HashMap<String, Integer> cart = currentUser.getCart();

        for(String key: cart.keySet())
        {
            double cost =  (Double) session.getAttribute(key);
            total += cart.get(key) * cost;
        }

        jsonObject.addProperty("total", total);
        jsonArray.add(jsonObject);
        out.write(jsonArray.toString());
        out.close();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String creditCard = request.getParameter("creditCard");
        String expDate = request.getParameter("expDate");

        String inputFirstName = null;
        String inputLastName = null;
        String inputCreditCard = null;
        String inputExpDate = null;

        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            // Declare our statement
//            Statement statement = conn.createStatement();

//            String query = "SELECT id, expiration, firstName, lastName\n" +
//                    "FROM creditcards\n" +
//                    "WHERE id=\"" +creditCard +"\" AND firstName =\""+firstName +"\" AND lastName =\"" +lastName +"\" AND expiration=\""  +expDate + "\"\n";

            String query = "SELECT id, expiration, firstName, lastName\n" +
                    "FROM creditcards\n" +
                    "WHERE id= ? AND firstName = ? AND lastName = ? AND expiration= ?;";

            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1,creditCard);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expDate);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                inputFirstName = rs.getString("firstName");
                inputLastName = rs.getString("lastName");
                inputCreditCard = rs.getString("id");
                inputExpDate = rs.getString("expiration");
            }

            if(creditCard.equals(inputCreditCard))
            {

                query = "select id from customers where ccId=?;";

                PreparedStatement s2 = conn.prepareStatement(query);

                s2.setString(1,inputCreditCard);
                ResultSet rs2 = s2.executeQuery();

                if(rs2.next())
                {
                    session.setAttribute("customerId", rs2.getString("id"));
                }

                rs2.close();
                s2.close();
            }

            rs.close();
            statement.close();

            // Log to localhost log
            // request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Set response status to 200 (OK)
            // response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            // out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            // response.setStatus(500);
        }
        if (creditCard.equals(inputCreditCard) && expDate.equals(inputExpDate) && firstName.equals(inputFirstName) && lastName.equals(inputLastName)) {
            // Login success:
            // set this user into the session
            // request.getSession().setAttribute("user", new User(username));

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");
            responseJsonObject.addProperty("customerId", (String) session.getAttribute("customerId"));

        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Credit Card validation failed");
            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            responseJsonObject.addProperty("message", "incorrect payment information");
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}