// Ilya Zeldner
package client;

public class ClientSimulation {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5555;

        System.out.println("--- Starting Multi-Client Simulation ---");

        for (int i = 0; i < 5; i++) {
            final int index = i; // Needed for lambda
            
            new Thread(() -> {
                String clientName = "Client-" + (index + 1);
                
                // Create the  client
                SimpleClient client = new SimpleClient(host, port, clientName);
                
                System.out.println("[" + clientName + "] Sending request...");

                // Send and WAIT for the exact response 
                Object response = client.sendRequestAndWait("GET_ALL_USERS");
                
                // Print the result
                System.out.println("[" + clientName + "] Got Response: \n" + response);

            }).start();   
        }   
    }    
}