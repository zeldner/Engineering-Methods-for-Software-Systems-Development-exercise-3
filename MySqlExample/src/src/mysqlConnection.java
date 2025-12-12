// Ilya Zeldner
package src;
import java.sql.*;
public class mysqlConnection {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/braudedatabase?serverTimezone=Asia/Jerusalem&useSSL=false";    
    static final String USER = "root"; 
    static final String PASS = "root";
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {    
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);// Open a connection
            // Example 1: Simple SELECT query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql = "SELECT id, name, age FROM employees";
            rs = stmt.executeQuery(sql);
            // Extract data from result set
            System.out.println("--- Employees ---");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age);
            }
            // Example 2: INSERT query using PreparedStatement (for preventing SQL injection)
            System.out.println("--- Inserting new employee ---");
            String insertSql = "INSERT INTO employees (name, age) VALUES (?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, "Alice Smith");
            pstmt.setInt(2, 30);
            int rowsInserted = pstmt.executeUpdate();
            System.out.println(rowsInserted + " row(s) inserted.");
            //Example 3: Update query using PreparedStatement.
            System.out.println("--- Updating employee age ---");
            String updateSql = "Update employees set age = ? where name = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setInt(1, 31);
            pstmt.setString(2, "Alice Smith");
            int rowsUpdated = pstmt.executeUpdate();
            System.out.println(rowsUpdated + " row(s) updated.");
            //Example 4: Delete query using PreparedStatement.
            System.out.println("--- Deleting employee ---");
            String deleteSql = "Delete from employees where name = ?";
            pstmt = conn.prepareStatement(deleteSql);
            pstmt.setString(1, "Alice Smith");
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println(rowsDeleted + " row(s) deleted.");
            //Example 5: Select with WHERE clause.
            System.out.println("--- Selected employees where age > 25 ---");
            sql = "Select name, age from employees where age > ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, 25);
            rs = pstmt.executeQuery();
            while(rs.next()){
                System.out.println("Name: " + rs.getString("name") + ", Age: " + rs.getInt("age"));
            }
            //Example 6: Using a try with resources block
            System.out.println("--- Using try with resources for auto close ---");
            sql = "Select count(*) from employees";
            try(Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){
                if(resultSet.next()){
                    System.out.println("Number of employees: " + resultSet.getInt(1));
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            // Close resources in a finally block
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
  }
}