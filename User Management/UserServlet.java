import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONObject;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/userdb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Joelking@05";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            switch (action) {
                case "create":
                    createUser(request, conn);
                    break;
                case "update":
                    updateUser(request, conn);
                    break;
                case "delete":
                    deleteUser(request, conn);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":\"success\"}");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM users");
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        json.put("users", users);
        response.getWriter().write(json.toString());
    }

    private void createUser(HttpServletRequest request, Connection conn) throws SQLException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, email) VALUES (?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.executeUpdate();
    }

    private void updateUser(HttpServletRequest request, Connection conn) throws SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        PreparedStatement stmt = conn.prepareStatement("UPDATE users SET name=?, email=? WHERE id=?");
        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.setInt(3, id);
        stmt.executeUpdate();
    }

    private void deleteUser(HttpServletRequest request, Connection conn) throws SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id=?");
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    class User {
        int id;
        String name;
        String email;

        public User(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }
}
