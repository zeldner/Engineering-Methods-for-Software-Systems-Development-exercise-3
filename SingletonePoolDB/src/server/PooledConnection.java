// Ilya Zeldner
package server;

import java.sql.Connection;
import java.sql.SQLException;

public class PooledConnection {
    private Connection connection;
    private long lastUsed;

    public PooledConnection(Connection connection) {
        this.connection = connection;
        this.lastUsed = System.currentTimeMillis();
    }

    public Connection getConnection() {
        return connection;
    }

    public void touch() {
        this.lastUsed = System.currentTimeMillis();
    }

    public long getLastUsed() {
        return lastUsed;
    }
    
    public void closePhysicalConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
