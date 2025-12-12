// Ilya Zeldner
package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersRepository {
    
    public String getAllUsers() {
        String sql = "SELECT * FROM users";
        return executeReadQuery(sql);
    }

    public String getLecturers() {
        String sql = "SELECT * FROM users WHERE role = 'Lecturer'";
        return executeReadQuery(sql);
    }
    
    // A helper method to avoid repeating code
    private String executeReadQuery(String sql) {
        MySQLConnectionPool pool = MySQLConnectionPool.getInstance();
        PooledConnection pConn = null;
        StringBuilder result = new StringBuilder();

        try {
            pConn = pool.getConnection();
            if (pConn == null) return "Error: Database Down";

            Connection conn = pConn.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.append(rs.getString("username"))
                      .append(" - ")
                      .append(rs.getString("role"))
                      .append("\n");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return "DB Error: " + e.getMessage();
        } finally {
            // Crucial: Return connection to the pool here!
            pool.releaseConnection(pConn);
        }
        
        return result.toString();
    }
}
