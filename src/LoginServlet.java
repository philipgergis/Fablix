import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        }
        catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String input_email = null;
        String input_password = null;

        JsonObject responseJsonObject = new JsonObject();
        boolean success = false;

        PrintWriter out = response.getWriter();

        // Verify reCAPTCHA
        try(out;) {
            if(request.getParameter("mobile") == null){
                String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
                if(gRecaptchaResponse.equals("")){
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "reCaptcha missing");
                    out.write(responseJsonObject.toString());
                    return;
                }
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            }

            try (Connection conn = dataSource.getConnection()) {
                // Declare our statement


                String query = "SELECT email, password\n" +
                        "FROM customers\n" +
                        "WHERE email=?;";

                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1,username);
                // Perform the query
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    input_email = rs.getString("email");
                    input_password = rs.getString("password");
                }

                success = new StrongPasswordEncryptor().checkPassword(password, input_password);

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
            PrintWriter out2 = response.getWriter();
            if (success) { // fix this part so that it's not hardcoded & checks db instead
                // Login success:
                // set this user into the session
                request.getSession().setAttribute("user", new User(username));


                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (input_email == null) {
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            out2.write(responseJsonObject.toString());


        } catch (Exception e) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "reCaptcha missing");
            out.write(responseJsonObject.toString());

            out.close();
            return;
        }



    }
}