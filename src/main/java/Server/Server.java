package Server;

import static Common.Utils.Config.SERVER_PORT;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static ServerSocket server;
    private static final ExecutorService pool  = Executors.newFixedThreadPool(3);;

    public Server(){
        try{
            if (Objects.isNull(server)) {
                server = new ServerSocket(SERVER_PORT);
                System.out.println("Server is running on PORT: " + SERVER_PORT);
            }
            while(true) {
                pool.execute(new ClientManager(server.accept()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Server();
    }
}
