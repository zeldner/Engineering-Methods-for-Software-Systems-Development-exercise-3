// Ilya Zeldner
package server;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class MyOCSFServer extends AbstractServer {
    private UsersRepository usersRepo;
    public MyOCSFServer(int port) {
        super(port);
        this.usersRepo = new UsersRepository();
    }
    protected void serverStarted(){System.out.println("Server listening for connections on port " + getPort());}
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            // Now this method exists because you added it!
            java.net.Socket socket = client.getClientSocket();
            
            String clientIP = socket.getInetAddress().getHostAddress();
            int clientPort = socket.getPort(); // <--- The Secret Port
            
            System.out.println("[Server Info] Client IP: " + clientIP);
            System.out.println("[Server Info] Client Port: " + clientPort);
            
        } catch (Exception e) {
            System.err.println("Error reading client info: " + e.getMessage());
        }
    	
    		String request = msg.toString();
        System.out.println("[Server] Request: " + request);
        String response;
        switch (request) {  // The "Routing" Logic
            case "GET_ALL_USERS":
                response = usersRepo.getAllUsers();
                break;                
            case "GET_LECTURERS":
                response = usersRepo.getLecturers();
                break;
            default:
                response = "Error: Unknown Command";
        }
        try {client.sendToClient(response);} 
        catch (Exception e) {e.printStackTrace();}
    }    
    public static void main(String[] args) {
        try {
            new MyOCSFServer(5555).listen();
        } catch (Exception e) { e.printStackTrace(); }
    }
}


