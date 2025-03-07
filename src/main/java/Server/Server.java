package Server;

import static Common.Utils.Config.SERVER_PORT;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static ServerSocket server;
    private static final ExecutorService pool  = Executors.newFixedThreadPool(3);
    private static final ConcurrentHashMap<String, ClientManager> clients = new ConcurrentHashMap<>();
    public Server(){
        try{
            if (Objects.isNull(server)) {
                server = new ServerSocket(SERVER_PORT);
                System.out.println("Server is running on PORT: " + SERVER_PORT);
            }
            while(true) {
                ClientManager clientManager = new ClientManager(server.accept());
                pool.execute(clientManager);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registerClient(String username, ClientManager clientManager) {
        clients.put(username, clientManager);
        System.out.println("Client Registered: " + username);
    }

    public static ClientManager getClient(String username) {
        return clients.get(username);
    }
    public static void removeClient(String username) {clients.remove(username);}


    public static void main(String[] args) {
        new Server();
    }
}
