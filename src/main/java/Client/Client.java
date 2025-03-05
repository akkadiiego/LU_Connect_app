package Client;

import Common.Models.User;
import Server.Server;
import static Common.Utils.Config.SERVER_PORT;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private Scanner reader;
    private Scanner in;
    private User user;

    public Client(){
        try{
            socket = new Socket("localhost", SERVER_PORT);
            reader = new Scanner(System.in);
            in = new Scanner(socket.getInputStream());
            user = null;

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            while (reader.hasNextLine()){
                writer.println(reader.nextLine());

                System.out.println(in.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser(){ return this.user; }
    public void setUser(User user) { this.user = user; }

    private void close() throws IOException {
        socket.close();
        reader.close();
        in.close();
    }

    public static void main(String[] args) {
        new Client();
    }
}
