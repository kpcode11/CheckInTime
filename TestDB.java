import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

public class TestDB {
    public static void main(String[] args) {
        String url = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/userdb");
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String pass = System.getenv().getOrDefault("DB_PASS", "");

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("DESCRIBE users")) {

            while (rs.next()) {
                System.out.println(rs.getString("Field") + " - " + rs.getString("Type"));
            }

            System.out.println("Users in DB:");
            try (ResultSet rs2 = stmt.executeQuery("SELECT username, password FROM users LIMIT 5")) {
                while (rs2.next()) {
                    System.out.println(rs2.getString("username") + " | length: " + rs2.getString("password").length() + " | " + rs2.getString("password"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
