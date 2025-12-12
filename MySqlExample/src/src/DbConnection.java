// Ilya Zeldner
package src;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
public class DbConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root"; 
    private static final String PASS = "root"; // For example, week password 
    public static void main(String[] args) {
        System.out.println("Connecting to database...");
        // Use try-with-resources 
        // to automatically close connections Define the SQL query ( Outside try block !!!"doesn't need to be closed" ) 
        String  sql = "SELECT id, name, email FROM users";
        try (
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);  // Get a connection to the database
            Statement stmt = conn.createStatement(); // Create a statement object
            ResultSet rs = stmt.executeQuery(sql)  // Execute the query and get the result set
        ) 
        {
            System.out.println("Connection successful!");
            System.out.println("Querying for users...");
            // Process the result set
            while (rs.next()) {
                // Retrieve by column name
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                // Display values
                System.out.print("ID: " + id);
                System.out.print(", Name: " + name);
                System.out.println(", Email: " + email);
            }
        } catch (Exception e) {
            e.printStackTrace();  // Handle errors for JDBC
        }
        System.out.println("Database connection closed.");
    }
}



