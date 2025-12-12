// Ilya Zeldner
package server;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class MySQLConnectionPool {

    private static MySQLConnectionPool instance;
    // DB Config
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test_db?serverTimezone=Asia/Jerusalem";    
    private static final String USER = "roots";
    private static final String PASS = "root";

    // Pool Config (Short times for Example)
    private static final int MAX_POOL_SIZE = 10;
    private static final long MAX_IDLE_TIME = 5000; // 5 Seconds Timeout
    private static final long CHECK_INTERVAL = 2;   // Check every 2 Seconds

    private BlockingQueue<PooledConnection> pool;
    private ScheduledExecutorService cleanerService;

    private MySQLConnectionPool() {
        pool = new LinkedBlockingQueue<>(MAX_POOL_SIZE);
        startCleanupTimer();
        System.out.println("[Pool] Initialized. Max Size: " + MAX_POOL_SIZE);
    }

    public static synchronized MySQLConnectionPool getInstance() {
        if (instance == null) {
            instance = new MySQLConnectionPool();
        }
        return instance;
    }

    public PooledConnection getConnection() {
        PooledConnection pConn = pool.poll(); // Try to get from queue
        
        if (pConn == null) {
            System.out.println("[Pool] Queue empty. Creating NEW physical connection!!!");
            return createNewConnection();
        }
        
        pConn.touch(); // Reset timer
        System.out.println("[Pool] Reusing existing connection.");
        return pConn;
    }

    public void releaseConnection(PooledConnection pConn) {
        if (pConn != null) {
            pConn.touch();
            boolean added = pool.offer(pConn); // Return to queue
            if (added) {
                System.out.println("[Pool] Connection returned. Current Pool Size: " + pool.size());
            } else {
                // Pool is full
                try { pConn.closePhysicalConnection(); } catch (Exception e) {}
            }
        }
    }

    private PooledConnection createNewConnection() {
        try {
            return new PooledConnection(DriverManager.getConnection(DB_URL, USER, PASS));
        } catch (SQLException e) {
            System.err.println("CONNECTION ERROR DETAILS");
            e.printStackTrace();
            return null;

        }
    }

    // THE TIMER LOGIC
    private void startCleanupTimer() {
        cleanerService = Executors.newSingleThreadScheduledExecutor();
        cleanerService.scheduleAtFixedRate(this::checkIdleConnections, CHECK_INTERVAL, CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    private void checkIdleConnections() {
        if (pool.isEmpty()) return;

        List<PooledConnection> activeConnections = new ArrayList<>();
        pool.drainTo(activeConnections); // Move all to temp list

        long now = System.currentTimeMillis();
        int closedCount = 0;

        for (PooledConnection pConn : activeConnections) {
            if (now - pConn.getLastUsed() > MAX_IDLE_TIME) {
                try {
                    pConn.closePhysicalConnection();
                    closedCount++;
                } catch (SQLException e) { e.printStackTrace(); }
            } else {
                pool.offer(pConn); // Put back
            }
        }
        
        if (closedCount > 0) { 
            System.out.println("[Timer] Evicted " + closedCount + " idle connections. Pool Size: " + pool.size());
        }
    }
}
