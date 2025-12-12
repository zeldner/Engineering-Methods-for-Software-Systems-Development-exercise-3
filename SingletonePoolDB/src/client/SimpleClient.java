// Ilya Zeldner
package client;

import ocsf.client.AbstractClient;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

public class SimpleClient extends AbstractClient {
    
    //To hold the future response
    private CompletableFuture<Object> currentResponse;
    private String clientName;

    public SimpleClient(String host, int port, String clientName) {
        super(host, port);
        this.clientName = clientName;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        System.out.println("Server said: " + msg);
        // When message arrives, complete!
        if (currentResponse != null) {
            currentResponse.complete(msg); 
        }
    }

    public Object sendRequestAndWait(String command) {
        try {
            openConnection();
            
            // 1. Create a new "Empty Box" (Future)
            currentResponse = new CompletableFuture<>();
            
            String myIP = InetAddress.getLocalHost().getHostAddress();
            
            // 2. Get My Secret Port 
            // Note: 'getClientSocket()' is the standard accessor in OCSF. 
            // If your version doesn't have it, you might need to add a getter to AbstractClient.
            int mySecretPort = getClientSocket().getLocalPort();
            
            System.out.println("--- CLIENT INFO ---");
            System.out.println("My Name: " + this.clientName);
            System.out.println("My IP: " + myIP);
            System.out.println("My Secret Port: " + mySecretPort); // e.g. 54921
            System.out.println("-------------------");
            
            sendToServer(command);
            
            // WAIT until 'handleMessageFromServer' fills the box
            Object response = currentResponse.get(); 
            
            closeConnection();
            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }   
    } 
}