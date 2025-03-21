package Server;

import static Common.Utils.Config.SERVER_PORT;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// In order to build the client Server connection I take inspiration from this video: https://www.youtube.com/watch?v=plh_cIEQ1Jo

public class Server {
    private static ServerSocket server; // server socket to accept connections
    private static final ExecutorService pool  = Executors.newFixedThreadPool(3); // thread pool to handle clients
    private static final ConcurrentHashMap<String, ClientManager> clients = new ConcurrentHashMap<>(); // store connected clients

    public Server(){
        try{
            server = new ServerSocket(SERVER_PORT); // start server
            System.out.println("Server is running on PORT: " + SERVER_PORT);

            while(true) {
                ClientManager clientManager = new ClientManager(server.accept()); // accept new connection
                pool.execute(clientManager); // handle it in a new thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registerClient(String username, ClientManager clientManager) {
        clients.put(username, clientManager); // add client to list
        System.out.println("Client Registered: " + username);
    }

    public static List<String> getClients() {
        return new ArrayList<>(clients.keySet()); // return all connected usernames
    }

    public static ClientManager getClient(String username) {
        return clients.get(username); // return client manager for username
    }

    public static void removeClient(String username) {
        clients.remove(username); // remove client from list
    }

    public static void main(String[] args) {
        new Server(); // start server
    }
}